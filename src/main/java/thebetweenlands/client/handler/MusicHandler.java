package thebetweenlands.client.handler;

import java.util.*;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.SelectMusicEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import thebetweenlands.api.audio.EntityMusicProvider;
import thebetweenlands.api.audio.EntitySoundInstance;
import thebetweenlands.api.entity.MusicPlayer;
import thebetweenlands.client.gui.menu.BLTitleScreen;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.SoundRegistry;

public class MusicHandler {

	public static final Music BL_DIMENSION = new Music(SoundRegistry.BL_MUSIC_DIMENSION, 3000, 6000, false);
	public static final Music BL_MAIN_MENU = new Music(SoundRegistry.BL_MUSIC_MENU, 20, 600, false);

	private static final Map<Class<? extends Entity>, EntityMusicProvider> entityMusicProviders = new HashMap<>();

	@Nullable
	private static List<Sound> musicDimTrackAccessors;
	@Nullable
	private static List<Sound> musicMenuTrackAccessors;
	private static final RandomSource RANDOM = RandomSource.create();
	private static int timeUntilMusic = 100;
	@Nullable
	private static  SoundInstance currentSound;
	@Nullable
	private static Sound previousSound;
	private static final IntSet playingEntityMusicLayers = new IntOpenHashSet();
	private static final Int2ObjectMap<Pair<EntitySoundInstance, MusicPlayer>> entityMusicMap = new Int2ObjectOpenHashMap<>();

	private static boolean hasBlMainMenu = false;
	private static boolean isInBlMainMenu = false;

	public static void init() {
		NeoForge.EVENT_BUS.addListener(MusicHandler::tickMusic);
		NeoForge.EVENT_BUS.addListener(MusicHandler::setMusicInDimension);
		NeoForge.EVENT_BUS.addListener(MusicHandler::cancelVanillaMusic);
	}

	private static void tickMusic(ClientTickEvent.Pre event) {
		Player player = Minecraft.getInstance().player;
		SoundManager manager = Minecraft.getInstance().getSoundManager();
		if (Minecraft.getInstance().isPaused()) return;

		boolean isInMainMenu = (!(Minecraft.getInstance().screen instanceof WinScreen) && player == null) && BetweenlandsConfig.blMainMenu;

		if (Minecraft.getInstance().screen instanceof BLTitleScreen) {
			hasBlMainMenu = true;
		}

		isInBlMainMenu = isInMainMenu && hasBlMainMenu;

		if ((isInBlMainMenu || (player != null && player.level().dimension() == DimensionRegistries.DIMENSION_KEY))) {

			Int2ObjectMap<Pair<MusicPlayer, Entity>> closestMusicEntityMap = new Int2ObjectOpenHashMap<>();

			if (Minecraft.getInstance().level != null) {
				for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
					MusicPlayer entityMusic = null;

					EntityMusicProvider entityMusicProvider = entityMusicProviders.get(entity.getClass());
					if (entityMusicProvider != null) {
						entityMusic = entityMusicProvider.getEntityMusic(entity);
					} else if (entity instanceof MusicPlayer musicPlayer) {
						entityMusic = musicPlayer;
					}

					if (entityMusic != null) {
						int layer = entityMusic.getMusicLayer(player);

						Pair<MusicPlayer, Entity> closestPair = closestMusicEntityMap.get(layer);
						Entity closest = closestPair != null ? closestPair.getRight() : null;

						if ((closest == null || entity.distanceTo(player) < closest.distanceTo(player))
							&& entity.distanceTo(player) <= entityMusic.getMusicRange(player)
							&& entityMusic.isMusicActive(player)) {
							closestMusicEntityMap.put(layer, Pair.of(entityMusic, entity));
						}
					}
				}
			}

			IntIterator it = playingEntityMusicLayers.iterator();
			while (it.hasNext()) {
				int layer = it.nextInt();
				Pair<EntitySoundInstance, MusicPlayer> pair = entityMusicMap.get(layer);
				EntitySoundInstance sound = pair.getLeft();
				MusicPlayer music = pair.getRight();
				if (!manager.isActive(sound)) {
					it.remove();
					entityMusicMap.remove(layer);
				} else if (!music.isMusicActive(player)) {
					sound.stopEntityMusic();
				}
			}

			if (!closestMusicEntityMap.isEmpty()) {
				for (Int2ObjectMap.Entry<Pair<MusicPlayer, Entity>> entry : closestMusicEntityMap.int2ObjectEntrySet()) {
					Pair<EntitySoundInstance, MusicPlayer> currentlyPlayingPair = entityMusicMap.get(entry.getIntKey());
					EntitySoundInstance currentlyPlaying = currentlyPlayingPair != null ? currentlyPlayingPair.getKey() : null;

					MusicPlayer closestEntityMusic = entry.getValue().getLeft();
					Entity closestEntity = entry.getValue().getRight();

					if (currentlyPlaying == null) {
						EntitySoundInstance newSound = closestEntityMusic.getMusicSound(player);

						if (newSound != null) {
							entityMusicMap.put(entry.getIntKey(), Pair.of(newSound, closestEntityMusic));
							playingEntityMusicLayers.add(entry.getIntKey());

							manager.play(newSound);
						}
					} else if (currentlyPlaying.getEntity() != closestEntity && closestEntityMusic.canInterruptOtherEntityMusic(player)) {
						currentlyPlaying.stopEntityMusic();
					}
				}
			}

			//TODO
			if (!entityMusicMap.isEmpty() /*|| AmbienceManager.INSTANCE.shouldStopMusic()*/) {
				if (manager.isActive(currentSound)) {
					manager.stop(currentSound);
					currentSound = null;
					timeUntilMusic = Math.min(Mth.nextInt(RANDOM, BL_DIMENSION.getMinDelay(), BL_DIMENSION.getMaxDelay()), timeUntilMusic);
				}
			} else if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) > 0.0F) {
				if (currentSound != null) {
					if ((!isInBlMainMenu && SoundRegistry.BL_MUSIC_MENU.getId().equals(currentSound.getLocation())) || (isInBlMainMenu && SoundRegistry.BL_MUSIC_DIMENSION.getId().equals(currentSound.getLocation()))) {
						manager.stop(currentSound);
						timeUntilMusic = Mth.nextInt(RANDOM, 0, (isInBlMainMenu ? BL_MAIN_MENU.getMinDelay() : BL_DIMENSION.getMinDelay()) / 2);
					}
					//Wait for sound track to finish
					if (!manager.isActive(currentSound)) {
						currentSound = null;
						timeUntilMusic = Math.min(Mth.nextInt(RANDOM, (isInBlMainMenu ? BL_MAIN_MENU.getMinDelay() : BL_DIMENSION.getMinDelay()), (isInBlMainMenu ? BL_MAIN_MENU.getMaxDelay() : BL_DIMENSION.getMaxDelay())), timeUntilMusic);
					}
				}

				timeUntilMusic = Math.min(timeUntilMusic, (isInBlMainMenu ? BL_MAIN_MENU.getMaxDelay() : BL_MAIN_MENU.getMaxDelay()));

				if (currentSound == null && timeUntilMusic-- <= 0) {
					//Start new sound track
					timeUntilMusic = Integer.MAX_VALUE;
					playRandomSoundTrack();
				}
			}
		}
	}

	private static void setMusicInDimension(SelectMusicEvent event) {
		Music music = event.getOriginalMusic();
		if (Minecraft.getInstance().level != null && Minecraft.getInstance().player != null && (music == Musics.CREATIVE || music == Musics.UNDER_WATER) && Minecraft.getInstance().level.dimension() == DimensionRegistries.DIMENSION_KEY) {
			event.setMusic(Minecraft.getInstance().level.getBiomeManager().getNoiseBiomeAtPosition(Minecraft.getInstance().player.blockPosition()).value().getBackgroundMusic().orElse(BL_DIMENSION));
		}
	}

	private static void cancelVanillaMusic(PlaySoundEvent event) {
		Player player = Minecraft.getInstance().player;

		if (event.getSound() != null) {
			if ((isInBlMainMenu || (player != null && player.level().dimension() == DimensionRegistries.DIMENSION_KEY)) && event.getSound().getSource() == SoundSource.MUSIC && isVanillaMusic(event.getSound())) {
				//Cancel non Betweenlands music
				event.setSound(null);
			}
		}
	}

	private static boolean isVanillaMusic(SoundInstance sound) {
		return isSoundContainedIn(SoundEvents.MUSIC_CREATIVE, sound) || isSoundContainedIn(SoundEvents.MUSIC_CREDITS, sound) || isSoundContainedIn(SoundEvents.MUSIC_DRAGON, sound) || isSoundContainedIn(SoundEvents.MUSIC_END, sound) ||
				isSoundContainedIn(SoundEvents.MUSIC_GAME, sound) || isSoundContainedIn(SoundEvents.MUSIC_MENU, sound) || isSoundContainedIn(SoundEvents.MUSIC_UNDER_WATER, sound);
	}

	private static boolean isSoundContainedIn(Holder<SoundEvent> track, SoundInstance sound) {
		if(Objects.equals(sound.getLocation(), track.getKey().location())) {
			return true;
		}
		Sound soundInstance = sound.getSound();
		if(soundInstance != null) {
			WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(track.getKey().location());
			if (soundEventAccessor != null) {
				List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
				for (Weighted<Sound> accessor : soundAccessors) {
					if (accessor instanceof Sound accessedSound && Objects.equals(accessedSound.getLocation(), soundInstance.getLocation())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 *
	 * @return A list with menu music
	 */
	private static List<Sound> getBetweenlandsMenuMusicTracks() {
		if (musicMenuTrackAccessors == null) {
			try {
				musicMenuTrackAccessors = new ArrayList<>();
				WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(SoundRegistry.BL_MUSIC_MENU.getId());
				if (soundEventAccessor != null) {
					List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
					for (Weighted<Sound> accessor : soundAccessors) {
						if (accessor instanceof Sound) {
							musicMenuTrackAccessors.add((Sound) accessor);
						}
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return musicMenuTrackAccessors;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 *
	 * @return A list of dimension music
	 */
	private static List<Sound> getBetweenlandsMusicTracks() {
		if (musicDimTrackAccessors == null) {
			try {
				musicDimTrackAccessors = new ArrayList<>();
				WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(SoundRegistry.BL_MUSIC_DIMENSION.getId());
				if (soundEventAccessor != null) {
					List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
					for (Weighted<Sound> accessor : soundAccessors) {
						if (accessor instanceof Sound) {
							musicDimTrackAccessors.add((Sound) accessor);
						}
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return musicDimTrackAccessors;
	}

	/**
	 * Plays a random Betweenlands sound track.
	 * The previously played sound track will be excluded.
	 */
	private static void playRandomSoundTrack() {
		List<Sound> availableSounds = new ArrayList<>(isInBlMainMenu ? getBetweenlandsMenuMusicTracks() : getBetweenlandsMusicTracks());
		if (!availableSounds.isEmpty()) {
			if (availableSounds.size() > 1 && previousSound != null) {
				availableSounds.remove(previousSound);
			}
			int weight = 0;
			for (Sound sound : availableSounds) {
				weight += sound.getWeight();
			}
			if (weight != 0) {
				int choice = RANDOM.nextInt(weight);
				Iterator<Sound> entryIter = availableSounds.iterator();
				Sound sound;
				do {
					sound = entryIter.next();
					choice -= sound.getWeight();
				} while (choice >= 0);
				previousSound = sound;
				SoundInstance parentSound = SimpleSoundInstance.forMusic(isInBlMainMenu ? SoundRegistry.BL_MUSIC_MENU.get() : SoundRegistry.BL_MUSIC_DIMENSION.get());
				SoundInstance playingSound = SoundWrapper.wrap(parentSound, sound);
				currentSound = playingSound;
				Minecraft.getInstance().getSoundManager().play(playingSound);
			}
		}
	}

	@Nullable
	public static EntitySoundInstance getEntityMusic(int layer) {
		Pair<EntitySoundInstance, MusicPlayer> pair = entityMusicMap.get(layer);
		return pair != null ? pair.getKey() : null;
	}

	public static class SoundWrapper implements SoundInstance {
		private final SoundInstance parent;
		private final Sound sound;

		private SoundWrapper(SoundInstance parent, Sound sound) {
			this.parent = parent;
			this.sound = sound;
		}

		/**
		 * Creates a new sound with the properties of the specified parent sound (-pool) but only
		 * the specified sound is played
		 */
		public static SoundInstance wrap(SoundInstance parent, Sound sound) {
			if (parent instanceof TickableSoundInstance tickable) {
				return new SoundWrapperTickable(tickable, sound);
			}
			return new SoundWrapper(parent, sound);
		}

		@Override
		public ResourceLocation getLocation() {
			return this.sound.getLocation();
		}

		@Override
		public @Nullable WeighedSoundEvents resolve(SoundManager manager) {
			WeighedSoundEvents parentAccessor = this.parent.resolve(manager);
			Component subtitle = parentAccessor != null ? parentAccessor.getSubtitle() : null;
			return new WeighedSoundEvents(this.sound.getLocation(), subtitle != null ? subtitle.getString() : null);
		}

		@Override
		public Sound getSound() {
			return this.sound;
		}

		@Override
		public SoundSource getSource() {
			return this.parent.getSource();
		}

		@Override
		public boolean isLooping() {
			return this.parent.isLooping();
		}

		@Override
		public int getDelay() {
			return this.parent.getDelay();
		}

		@Override
		public float getVolume() {
			return this.parent.getVolume();
		}

		@Override
		public float getPitch() {
			return this.parent.getPitch();
		}

		@Override
		public double getX() {
			return this.parent.getX();
		}

		@Override
		public double getY() {
			return this.parent.getY();
		}

		@Override
		public double getZ() {
			return this.parent.getZ();
		}

		@Override
		public Attenuation getAttenuation() {
			return this.parent.getAttenuation();
		}

		@Override
		public boolean isRelative() {
			return this.parent.isRelative();
		}
	}

	protected static class SoundWrapperTickable extends SoundWrapper implements TickableSoundInstance {
		private final TickableSoundInstance parent;

		private SoundWrapperTickable(TickableSoundInstance parent, Sound sound) {
			super(parent, sound);
			this.parent = parent;
		}

		@Override
		public void tick() {
			this.parent.tick();
		}

		@Override
		public boolean isStopped() {
			return this.parent.isStopped();
		}
	}
}
