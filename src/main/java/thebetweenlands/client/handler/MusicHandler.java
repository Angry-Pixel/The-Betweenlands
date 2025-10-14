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

	public static final MusicHandler INSTANCE = new MusicHandler();
	public static final Music BL_DIMENSION = new Music(SoundRegistry.BL_MUSIC_DIMENSION, 3000, 6000, false);
	public static final Music BL_MAIN_MENU = new Music(SoundRegistry.BL_MUSIC_MENU, 20, 600, false);

	private final Map<Class<? extends Entity>, EntityMusicProvider> entityMusicProviders = new HashMap<>();

	@Nullable
	private List<Sound> musicDimTrackAccessors;
	@Nullable
	private List<Sound> musicMenuTrackAccessors;
	private final RandomSource RANDOM = RandomSource.create();
	private int timeUntilMusic = 100;
	@Nullable
	private SoundInstance currentSound;
	@Nullable
	private Sound previousSound;
	private final IntSet playingEntityMusicLayers = new IntOpenHashSet();
	private final Int2ObjectMap<Pair<EntitySoundInstance, MusicPlayer>> entityMusicMap = new Int2ObjectOpenHashMap<>();

	private boolean hasBlMainMenu = false;
	private boolean isInBlMainMenu = false;

	public void init() {
		NeoForge.EVENT_BUS.addListener(MusicHandler.INSTANCE::tickMusic);
		NeoForge.EVENT_BUS.addListener(MusicHandler.INSTANCE::cancelVanillaMusic);
	}

	public boolean registerEntityMusicProvider(Class<? extends Entity> entityCls, EntityMusicProvider musicProvider) {
		if(!this.entityMusicProviders.containsKey(entityCls)) {
			this.entityMusicProviders.put(entityCls, musicProvider);
			return true;
		}
		return false;
	}

	public boolean unregisterEntityMusicProvider(Class<? extends Entity> entityCls, EntityMusicProvider musicProvider) {
		if(this.entityMusicProviders.get(entityCls) == musicProvider) {
			this.entityMusicProviders.remove(entityCls);
			return true;
		}
		return false;
	}

	private void tickMusic(ClientTickEvent.Pre event) {
		Player player = Minecraft.getInstance().player;
		SoundManager manager = Minecraft.getInstance().getSoundManager();
		if (Minecraft.getInstance().isPaused()) return;

		boolean isInMainMenu = (!(Minecraft.getInstance().screen instanceof WinScreen) && player == null) && BetweenlandsConfig.blMainMenu;

		if (Minecraft.getInstance().screen instanceof BLTitleScreen) {
			this.hasBlMainMenu = true;
		}

		this.isInBlMainMenu = isInMainMenu && this.hasBlMainMenu;

		if ((this.isInBlMainMenu || (player != null && player.level().dimension() == DimensionRegistries.DIMENSION_KEY))) {

			Int2ObjectMap<Pair<MusicPlayer, Entity>> closestMusicEntityMap = new Int2ObjectOpenHashMap<>();

			if (Minecraft.getInstance().level != null) {
				for (Entity entity : Minecraft.getInstance().level.entitiesForRendering()) {
					MusicPlayer entityMusic = null;

					EntityMusicProvider entityMusicProvider = this.entityMusicProviders.get(entity.getClass());
					if (entityMusicProvider != null) {
						entityMusic = entityMusicProvider.getEntityMusic(entity);
					} else if (entity instanceof MusicPlayer musicPlayer) {
						entityMusic = musicPlayer;
					}

					if (entityMusic != null && player != null) {
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

			IntIterator it = this.playingEntityMusicLayers.iterator();
			while (it.hasNext()) {
				int layer = it.nextInt();
				Pair<EntitySoundInstance, MusicPlayer> pair = this.entityMusicMap.get(layer);
				EntitySoundInstance sound = pair.getLeft();
				MusicPlayer music = pair.getRight();
				if (!manager.isActive(sound)) {
					it.remove();
					this.entityMusicMap.remove(layer);
				} else if (!music.isMusicActive(player)) {
					sound.stopEntityMusic();
				}
			}

			if (!closestMusicEntityMap.isEmpty()) {
				for (Int2ObjectMap.Entry<Pair<MusicPlayer, Entity>> entry : closestMusicEntityMap.int2ObjectEntrySet()) {
					Pair<EntitySoundInstance, MusicPlayer> currentlyPlayingPair = this.entityMusicMap.get(entry.getIntKey());
					EntitySoundInstance currentlyPlaying = currentlyPlayingPair != null ? currentlyPlayingPair.getKey() : null;

					MusicPlayer closestEntityMusic = entry.getValue().getLeft();
					Entity closestEntity = entry.getValue().getRight();

					if (currentlyPlaying == null) {
						EntitySoundInstance newSound = closestEntityMusic.getMusicSound(player);

						if (newSound != null) {
							this.entityMusicMap.put(entry.getIntKey(), Pair.of(newSound, closestEntityMusic));
							this.playingEntityMusicLayers.add(entry.getIntKey());

							manager.play(newSound);
						}
					} else if (currentlyPlaying.getEntity() != closestEntity && closestEntityMusic.canInterruptOtherEntityMusic(player)) {
						currentlyPlaying.stopEntityMusic();
					}
				}
			}

			//TODO
			if (!this.entityMusicMap.isEmpty() /*|| AmbienceManager.INSTANCE.shouldStopMusic()*/) {
				if (this.currentSound != null && manager.isActive(this.currentSound)) {
					manager.stop(this.currentSound);
					this.currentSound = null;
					this.timeUntilMusic = Math.min(Mth.nextInt(RANDOM, BL_DIMENSION.getMinDelay(), BL_DIMENSION.getMaxDelay()), this.timeUntilMusic);
				}
			} else if (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MUSIC) > 0.0F) {
				if (this.currentSound != null) {
					if ((!this.isInBlMainMenu && SoundRegistry.BL_MUSIC_MENU.getId().equals(this.currentSound.getLocation())) || (this.isInBlMainMenu && SoundRegistry.BL_MUSIC_DIMENSION.getId().equals(this.currentSound.getLocation()))) {
						manager.stop(currentSound);
						this.timeUntilMusic = Mth.nextInt(RANDOM, 0, (isInBlMainMenu ? BL_MAIN_MENU.getMinDelay() : BL_DIMENSION.getMinDelay()) / 2);
					}
					//Wait for sound track to finish
					if (!manager.isActive(this.currentSound)) {
						this.currentSound = null;
						this.timeUntilMusic = Math.min(Mth.nextInt(RANDOM, (this.isInBlMainMenu ? BL_MAIN_MENU.getMinDelay() : BL_DIMENSION.getMinDelay()), (this.isInBlMainMenu ? BL_MAIN_MENU.getMaxDelay() : BL_DIMENSION.getMaxDelay())), this.timeUntilMusic);
					}
				}

				this.timeUntilMusic = Math.min(this.timeUntilMusic, (this.isInBlMainMenu ? BL_MAIN_MENU.getMaxDelay() : BL_DIMENSION.getMaxDelay()));

				if (this.currentSound == null && this.timeUntilMusic-- <= 0) {
					//Start new sound track
					this.timeUntilMusic = Integer.MAX_VALUE;
					this.playRandomSoundTrack();
				}
			}
		}
	}

	private void cancelVanillaMusic(PlaySoundEvent event) {
		Player player = Minecraft.getInstance().player;

		if (event.getSound() != null) {
			if ((this.isInBlMainMenu || (player != null && player.level().dimension() == DimensionRegistries.DIMENSION_KEY)) && event.getSound().getSource() == SoundSource.MUSIC && isVanillaMusic(event.getSound())) {
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
		WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(track.getKey().location());
		if (soundEventAccessor != null) {
			List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
			for (Weighted<Sound> accessor : soundAccessors) {
				if (accessor instanceof Sound accessedSound && Objects.equals(accessedSound.getLocation(), soundInstance.getLocation())) {
					return true;
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
	private List<Sound> getBetweenlandsMenuMusicTracks() {
		if (this.musicMenuTrackAccessors == null) {
			try {
				this.musicMenuTrackAccessors = new ArrayList<>();
				WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(SoundRegistry.BL_MUSIC_MENU.getId());
				if (soundEventAccessor != null) {
					List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
					for (Weighted<Sound> accessor : soundAccessors) {
						if (accessor instanceof Sound) {
							this.musicMenuTrackAccessors.add((Sound) accessor);
						}
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return this.musicMenuTrackAccessors;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 *
	 * @return A list of dimension music
	 */
	private List<Sound> getBetweenlandsMusicTracks() {
		if (this.musicDimTrackAccessors == null) {
			try {
				this.musicDimTrackAccessors = new ArrayList<>();
				WeighedSoundEvents soundEventAccessor = Minecraft.getInstance().getSoundManager().getSoundEvent(SoundRegistry.BL_MUSIC_DIMENSION.getId());
				if (soundEventAccessor != null) {
					List<Weighted<Sound>> soundAccessors = soundEventAccessor.list;
					for (Weighted<Sound> accessor : soundAccessors) {
						if (accessor instanceof Sound) {
							this.musicDimTrackAccessors.add((Sound) accessor);
						}
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return this.musicDimTrackAccessors;
	}

	/**
	 * Plays a random Betweenlands soundtrack.
	 * The previously played soundtrack will be excluded.
	 */
	private void playRandomSoundTrack() {
		List<Sound> availableSounds = new ArrayList<>(this.isInBlMainMenu ? getBetweenlandsMenuMusicTracks() : getBetweenlandsMusicTracks());
		if (!availableSounds.isEmpty()) {
			if (availableSounds.size() > 1 && this.previousSound != null) {
				availableSounds.remove(this.previousSound);
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
				this.previousSound = sound;
				SoundInstance parentSound = SimpleSoundInstance.forMusic(this.isInBlMainMenu ? SoundRegistry.BL_MUSIC_MENU.get() : SoundRegistry.BL_MUSIC_DIMENSION.get());
				SoundInstance playingSound = SoundWrapper.wrap(parentSound, sound);
				this.currentSound = playingSound;
				Minecraft.getInstance().getSoundManager().play(playingSound);
			}
		}
	}

	@Nullable
	public EntitySoundInstance getEntityMusic(int layer) {
		Pair<EntitySoundInstance, MusicPlayer> pair = this.entityMusicMap.get(layer);
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
