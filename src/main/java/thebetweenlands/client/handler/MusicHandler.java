package thebetweenlands.client.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import thebetweenlands.api.audio.IEntitySound;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.api.entity.IEntityMusicProvider;
import thebetweenlands.client.audio.SoundSystemOpenALAccess;
import thebetweenlands.client.audio.ambience.AmbienceManager;
import thebetweenlands.client.gui.menu.GuiBLMainMenu;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.SoundRegistry;

public class MusicHandler {
	public static final MusicHandler INSTANCE = new MusicHandler();

	private MusicHandler() { }

	private static final int MIN_WAIT = 3000;
	private static final int MAX_WAIT = 6000;
	private static final int MIN_WAIT_MENU = 20;
	private static final int MAX_WAIT_MENU = 600;

	private final SoundSystemOpenALAccess openAlAccess = new SoundSystemOpenALAccess();

	private final Map<Class<? extends Entity>, IEntityMusicProvider> entityMusicProviders = new HashMap<>();

	private List<Sound> musicDimTrackAccessors;
	private List<Sound> musicMenuTrackAccessors;
	private Minecraft mc = Minecraft.getMinecraft();
	private final Random RNG = new Random();
	private int timeUntilMusic = 100;
	private ISound currentSound;
	private Sound previousSound;
	private IntSet playingEntityMusicLayers = new IntOpenHashSet();
	private Int2ObjectMap<IEntitySound> entityMusicMap = new Int2ObjectOpenHashMap<>();

	private boolean hasBlMainMenu = false;
	private boolean isInBlMainMenu = false;

	public boolean registerEntityMusicProvider(Class<? extends Entity> entityCls, IEntityMusicProvider musicProvider) {
		if(!this.entityMusicProviders.containsKey(entityCls)) {
			this.entityMusicProviders.put(entityCls, musicProvider);
			return true;
		}
		return false;
	}
	
	public boolean unregisterEntityMusicProvider(Class<? extends Entity> entityCls, IEntityMusicProvider musicProvider) {
		if(this.entityMusicProviders.get(entityCls) == musicProvider) {
			this.entityMusicProviders.remove(entityCls);
			return true;
		}
		return false;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onSoundSystemLoad(SoundLoadEvent event) {
		if(event.getManager() == this.mc.getSoundHandler().sndManager) {
			this.openAlAccess.cleanup();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onGuiOpen(GuiOpenEvent event) {
		if(event.getGui() != null && event.getGui().getClass() == GuiMainMenu.class) {
			hasBlMainMenu = false;
		}
	}

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			if(this.mc.getSoundHandler() != null && !this.openAlAccess.isErrored() && !this.openAlAccess.isInitialized()) {
				//Needs to be done here because during SoundLoadEvent and SoundSetupEvent the system isn't fully set up yet
				this.openAlAccess.init(this.mc.getSoundHandler().sndManager);
			}

			EntityPlayer player = getPlayer();

			boolean isInMainMenu = (!(mc.currentScreen instanceof GuiWinGame) && mc.player == null) && BetweenlandsConfig.GENERAL.blMainMenu;

			if(mc.currentScreen instanceof GuiBLMainMenu) {
				hasBlMainMenu = true;
			}

			isInBlMainMenu = isInMainMenu && hasBlMainMenu;

			if ((isInBlMainMenu || (player != null && player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId))) {

				Int2ObjectMap<Pair<IEntityMusic, Entity>> closestMusicEntityMap = new Int2ObjectOpenHashMap<>();

				if(mc.world != null) {
					for(Entity entity : this.mc.world.loadedEntityList) {
						IEntityMusic entityMusic = null;

						IEntityMusicProvider entityMusicProvider = this.entityMusicProviders.get(entity.getClass());
						if(entityMusicProvider != null) {
							entityMusic = entityMusicProvider.getEntityMusic(entity);
						} else if(entity instanceof IEntityMusic) {
							entityMusic = (IEntityMusic) entity;
						}

						if(entityMusic != null) {
							int layer = entityMusic.getMusicLayer(player);

							Pair<IEntityMusic, Entity> closestPair = closestMusicEntityMap.get(layer);
							Entity closest = closestPair != null ? closestPair.getRight() : null;

							if((closest == null || entity.getDistance(player) < closest.getDistance(player))
									&& entity.getDistance(player) <= entityMusic.getMusicRange(player)
									&& entityMusic.isMusicActive(player)) {
								closestMusicEntityMap.put(layer, Pair.of(entityMusic, entity));
							}
						}
					}
				}

				IntIterator it = this.playingEntityMusicLayers.iterator();
				while(it.hasNext()) {
					int layer = it.nextInt();
					IEntitySound sound = this.entityMusicMap.get(layer);
					if(!this.mc.getSoundHandler().isSoundPlaying(sound)) {
						it.remove();
						this.entityMusicMap.remove(layer);
					} else if(!((IEntityMusic) sound.getMusicEntity()).isMusicActive(player)) {
						sound.stopEntityMusic();
					}
				}

				if(!closestMusicEntityMap.isEmpty()) {
					for(Int2ObjectMap.Entry<Pair<IEntityMusic, Entity>> entry : closestMusicEntityMap.int2ObjectEntrySet()) {
						IEntitySound currentlyPlaying = this.entityMusicMap.get(entry.getIntKey());

						IEntityMusic closestEntityMusic = entry.getValue().getLeft();
						Entity closestEntity = entry.getValue().getRight();

						if(currentlyPlaying == null) {
							IEntitySound newSound = closestEntityMusic.getMusicSound(player);

							if(newSound != null) {
								this.entityMusicMap.put(entry.getIntKey(), newSound);
								this.playingEntityMusicLayers.add(entry.getIntKey());

								this.mc.getSoundHandler().playSound(newSound);
							}
						} else if(currentlyPlaying.getMusicEntity() != closestEntity && closestEntityMusic.canInterruptOtherEntityMusic(player)) {
							currentlyPlaying.stopEntityMusic();
						}
					}
				}

				if(!this.entityMusicMap.isEmpty() || AmbienceManager.INSTANCE.shouldStopMusic()) {
					if(this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
						this.mc.getSoundHandler().stopSound(this.currentSound);
						this.currentSound = null;
						this.timeUntilMusic = Math.min(MathHelper.getInt(this.RNG, MIN_WAIT, MAX_WAIT), this.timeUntilMusic);
					}
				} else if(this.mc.gameSettings.getSoundLevel(SoundCategory.MUSIC) > 0.0F) {
					if (this.currentSound != null) {

						if ((!isInBlMainMenu && SoundRegistry.BL_MUSIC_MENU.getSoundName().equals(this.currentSound.getSoundLocation())) || (isInBlMainMenu && SoundRegistry.BL_MUSIC_DIMENSION.getSoundName().equals(this.currentSound.getSoundLocation()))) {
							this.mc.getSoundHandler().stopSound(this.currentSound);
							this.timeUntilMusic = MathHelper.getInt(this.RNG, 0, (isInBlMainMenu ? MIN_WAIT_MENU : MIN_WAIT) / 2);
						}
						//Wait for sound track to finish
						if (!this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
							this.currentSound = null;
							this.timeUntilMusic = Math.min(MathHelper.getInt(this.RNG, (isInBlMainMenu ? MIN_WAIT_MENU : MIN_WAIT), (isInBlMainMenu ? MAX_WAIT_MENU : MAX_WAIT)), this.timeUntilMusic);
						}
					}

					this.timeUntilMusic = Math.min(this.timeUntilMusic, (isInBlMainMenu ? MAX_WAIT_MENU : MAX_WAIT));

					if (this.currentSound == null && this.timeUntilMusic-- <= 0) {
						//Start new sound track
						this.timeUntilMusic = Integer.MAX_VALUE;
						this.playRandomSoundTrack();
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event) {
		EntityPlayer player = getPlayer();

		if((isInBlMainMenu || (player != null && player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId)) && event.getSound().getCategory() == SoundCategory.MUSIC && this.isVanillaMusic(event.getSound())) {
			//Cancel non Betweenlands music
			event.setResultSound(null);
		}
	}

	public boolean isVanillaMusic(ISound sound) {
		return this.isSoundContainedIn(SoundEvents.MUSIC_CREATIVE, sound) || this.isSoundContainedIn(SoundEvents.MUSIC_CREDITS, sound) || this.isSoundContainedIn(SoundEvents.MUSIC_DRAGON, sound) || this.isSoundContainedIn(SoundEvents.MUSIC_END, sound) ||
				this.isSoundContainedIn(SoundEvents.MUSIC_GAME, sound) || this.isSoundContainedIn(SoundEvents.MUSIC_MENU, sound) || this.isSoundContainedIn(SoundEvents.MUSIC_NETHER, sound);
	}

	private boolean isSoundContainedIn(SoundEvent track, ISound sound) {
		if(Objects.equals(sound.getSoundLocation(), track.getSoundName())) {
			return true;
		}
		Sound soundInstance = sound.getSound();
		if(soundInstance != null) {
			SoundEventAccessor soundEventAccessor = this.mc.getSoundHandler().getAccessor(track.getSoundName());
			if (soundEventAccessor != null) {
				List<ISoundEventAccessor<Sound>> soundAccessors = soundEventAccessor.accessorList;
				for (ISoundEventAccessor<Sound> accessor : soundAccessors) {
					if (accessor instanceof Sound && Objects.equals(((Sound) accessor).getSoundLocation(), soundInstance.getSoundLocation())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 * @return A list with menu music
	 */
	public List<Sound> getBetweenlandsMenuMusicTracks() {
		if(this.musicMenuTrackAccessors == null) {
			try {
				this.musicMenuTrackAccessors = new ArrayList<>();
				SoundEventAccessor soundEventAccessor = this.mc.getSoundHandler().getAccessor(SoundRegistry.BL_MUSIC_MENU.getSoundName());
				if (soundEventAccessor != null) {
					List<ISoundEventAccessor<Sound>> soundAccessors = soundEventAccessor.accessorList;
					for (ISoundEventAccessor<Sound> accessor : soundAccessors) {
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
	 * @return A list of dimension music
	 */
	public List<Sound> getBetweenlandsMusicTracks() {
		if(this.musicDimTrackAccessors == null) {
			try {
				this.musicDimTrackAccessors = new ArrayList<>();
				SoundEventAccessor soundEventAccessor = this.mc.getSoundHandler().getAccessor(SoundRegistry.BL_MUSIC_DIMENSION.getSoundName());
				if (soundEventAccessor != null) {
					List<ISoundEventAccessor<Sound>> soundAccessors = soundEventAccessor.accessorList;
					for (ISoundEventAccessor<Sound> accessor : soundAccessors) {
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
	 * Plays a random Betweenlands sound track.
	 * The previously played sound track will be excluded.
	 */
	public void playRandomSoundTrack() {
		List<Sound> availableSounds = new ArrayList<>(isInBlMainMenu ? this.getBetweenlandsMenuMusicTracks(): this.getBetweenlandsMusicTracks());
		if(!availableSounds.isEmpty()) {
			if(availableSounds.size() > 1 && this.previousSound != null) {
				availableSounds.remove(this.previousSound);
			}
			int weight = 0;
			for(Sound sound : availableSounds) {
				weight += sound.getWeight();
			}
			if(weight != 0) {
				int choice = RNG.nextInt(weight);
				Iterator<Sound> entryIter = availableSounds.iterator();
				Sound sound;
				do {
					sound = entryIter.next();
					choice -= sound.getWeight();
				} while (choice >= 0);
				this.previousSound = sound;
				ISound parentSound = PositionedSoundRecord.getMusicRecord(isInBlMainMenu ? SoundRegistry.BL_MUSIC_MENU: SoundRegistry.BL_MUSIC_DIMENSION);
				ISound playingSound = SoundWrapper.wrap(parentSound, sound);
				this.currentSound = playingSound;
				this.mc.getSoundHandler().playSound(playingSound);
			}
		}
	}

	@Nullable
	public EntityPlayer getPlayer() {
		return this.mc.player;
	}

	public SoundSystemOpenALAccess getOpenALAccess() {
		return this.openAlAccess;
	}

	@Nullable
	public IEntitySound getEntityMusic(int layer) {
		return this.entityMusicMap.get(layer);
	}

	public static class SoundWrapper implements ISound {
		private final ISound parent;
		private final Sound sound;

		private SoundWrapper(ISound parent, Sound sound) {
			this.parent = parent;
			this.sound = sound;
		}

		/**
		 * Creates a new sound with the properties of the specified parent sound (-pool) but only
		 * the specified sound is played
		 * @param parent
		 * @param sound
		 * @return
		 */
		public static ISound wrap(ISound parent, Sound sound) {
			if(parent instanceof ITickableSound) {
				return new SoundWrapperTickable((ITickableSound) parent, sound);
			}
			return new SoundWrapper(parent, sound);
		}

		@Override
		public ResourceLocation getSoundLocation() {
			return this.sound.getSoundLocation();
		}

		@Override
		public SoundEventAccessor createAccessor(SoundHandler handler) {
			SoundEventAccessor parentAccessor = this.parent.createAccessor(handler);
			ITextComponent subtitle = parentAccessor != null ? parentAccessor.getSubtitle() : null;
			SoundEventAccessor accessor = new SoundEventAccessor(this.sound.getSoundLocation(), subtitle != null ? subtitle.getFormattedText() : null);
			return accessor;
		}

		@Override
		public Sound getSound() {
			return this.sound;
		}

		@Override
		public SoundCategory getCategory() {
			return this.parent.getCategory();
		}

		@Override
		public boolean canRepeat() {
			return this.parent.canRepeat();
		}

		@Override
		public int getRepeatDelay() {
			return this.parent.getRepeatDelay();
		}

		@Override
		public float getVolume() {
			return this.sound.getVolume();
		}

		@Override
		public float getPitch() {
			return this.sound.getPitch();
		}

		@Override
		public float getXPosF() {
			return this.parent.getXPosF();
		}

		@Override
		public float getYPosF() {
			return this.parent.getYPosF();
		}

		@Override
		public float getZPosF() {
			return this.parent.getZPosF();
		}

		@Override
		public AttenuationType getAttenuationType() {
			return this.parent.getAttenuationType();
		}
	}

	protected static class SoundWrapperTickable extends SoundWrapper implements ITickableSound {
		private final ITickableSound parent;

		private SoundWrapperTickable(ITickableSound parent, Sound sound) {
			super(parent, sound);
			this.parent = parent;
		}

		@Override
		public void update() {
			this.parent.update();
		}

		@Override
		public boolean isDonePlaying() {
			return this.parent.isDonePlaying();
		}
	}
}
