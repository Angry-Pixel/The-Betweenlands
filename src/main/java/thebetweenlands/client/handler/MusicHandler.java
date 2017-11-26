package thebetweenlands.client.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.api.entity.IEntityMusic;
import thebetweenlands.client.audio.EntityMusicSound;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.sound.BLSoundEvent;
import thebetweenlands.util.config.ConfigHandler;

public class MusicHandler {
	public static final MusicHandler INSTANCE = new MusicHandler();

	private MusicHandler() { }

	private static final Field f_accessorList = ReflectionHelper.findField(SoundEventAccessor.class, "accessorList", "a", "field_188716_a");

	private static final int MIN_WAIT = 3000;
	private static final int MAX_WAIT = 6000;
	private static final int MIN_WAIT_MENU = 20;
	private static final int MAX_WAIT_MENU = 600;

	private List<Sound> musicDimTrackAccessors;
	private List<Sound> musicMenuTrackAccessors;
	private Minecraft mc = Minecraft.getMinecraft();
	private final Random RNG = new Random();
	private int timeUntilMusic = 100;
	private ISound currentSound;
	private Sound previousSound;
	private boolean menu;
	private EntityMusicSound currentlyPlayingEntityMusic = null;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.START) {
			EntityPlayer player = getPlayer();

			menu = (!(mc.currentScreen instanceof GuiWinGame) && mc.player == null) && ConfigHandler.blMainMenu;

			if ((menu || (player != null && player.dimension == ConfigHandler.dimensionId)) && this.mc.gameSettings.getSoundLevel(SoundCategory.MUSIC) > 0.0F) {

				Entity closestMusicEntity = null;
				if(mc.world != null) {
					for(Entity entity : this.mc.world.loadedEntityList) {
						if(entity instanceof IEntityMusic) {
							if((closestMusicEntity == null || entity.getDistance(player) < closestMusicEntity.getDistance(player))
									&& entity.getDistance(player) <= ((IEntityMusic)entity).getMusicRange(player)
									&& ((IEntityMusic)entity).isMusicActive(player))
								closestMusicEntity = entity;
						}
					}
				}

				if(this.currentlyPlayingEntityMusic != null && !this.mc.getSoundHandler().isSoundPlaying(this.currentlyPlayingEntityMusic)) {
					this.currentlyPlayingEntityMusic = null;
				}

				if(closestMusicEntity != null) {
					if(this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
						this.mc.getSoundHandler().stopSound(this.currentSound);
						this.currentSound = null;
						this.timeUntilMusic = Math.min(MathHelper.getInt(this.RNG, MIN_WAIT, MAX_WAIT), this.timeUntilMusic);
					}

					if(this.currentlyPlayingEntityMusic != null) {
						if(this.currentlyPlayingEntityMusic.entity != closestMusicEntity) {
							if(!this.currentlyPlayingEntityMusic.isDonePlaying() && this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
								this.currentlyPlayingEntityMusic.stop();
								this.currentlyPlayingEntityMusic = null;
							}
						}
					}
					if(this.currentlyPlayingEntityMusic == null) {
						IEntityMusic entityMusic = (IEntityMusic)closestMusicEntity;
						BLSoundEvent soundEvent = entityMusic.getMusicFile(player);
						this.currentlyPlayingEntityMusic = new EntityMusicSound(soundEvent, soundEvent.category, closestMusicEntity, 1);
						this.mc.getSoundHandler().playSound(this.currentlyPlayingEntityMusic);
					}
				}

				if(this.currentlyPlayingEntityMusic != null && !this.currentlyPlayingEntityMusic.isDonePlaying()) {
					if(!((IEntityMusic)this.currentlyPlayingEntityMusic.entity).isMusicActive(player)) {
						if(this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
							this.currentlyPlayingEntityMusic.stop();
							this.currentlyPlayingEntityMusic = null;
						}
					}
				} else {
					if (this.currentSound != null) {

						if ((!menu && SoundRegistry.BL_MUSIC_MENU.getSoundName().equals(this.currentSound.getSoundLocation())) || (menu && SoundRegistry.BL_MUSIC_DIMENSION.getSoundName().equals(this.currentSound.getSoundLocation()))) {
							this.mc.getSoundHandler().stopSound(this.currentSound);
							this.timeUntilMusic = MathHelper.getInt(this.RNG, 0, (menu ? MIN_WAIT_MENU : MIN_WAIT) / 2);
						}
						//Wait for sound track to finish
						if (!this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
							this.currentSound = null;
							this.timeUntilMusic = Math.min(MathHelper.getInt(this.RNG, (menu ? MIN_WAIT_MENU : MIN_WAIT), (menu ? MAX_WAIT_MENU : MAX_WAIT)), this.timeUntilMusic);
						}
					}

					this.timeUntilMusic = Math.min(this.timeUntilMusic, (menu ? MAX_WAIT_MENU : MAX_WAIT));

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

		if((menu || (player != null && player.dimension == ConfigHandler.dimensionId)) && event.getSound().getCategory() == SoundCategory.MUSIC && !this.isBetweenlandsMusic(event.getSound())) {
			//Cancel non Betweenlands music
			event.setResultSound(null);
		}
	}

	/**
	 * Returns whether the specified sound is a Betweenlands music track
	 * @param sound
	 * @return
	 */
	public boolean isBetweenlandsMusic(ISound sound) {
		if(SoundRegistry.BL_MUSIC_DIMENSION.getSoundName().equals(sound.getSoundLocation()))
			return true;
		if (SoundRegistry.BL_MUSIC_MENU.getSoundName().equals(sound.getSoundLocation()))
			return true;
		if (SoundRegistry.FORTRESS_BOSS_LOOP.getSoundName().equals(sound.getSoundLocation()))
			return true;
		if (SoundRegistry.DREADFUL_PEAT_MUMMY_LOOP.getSoundName().equals(sound.getSoundLocation()))
			return true;
		List<Sound> betweenlandsSoundTracks = new ArrayList<>(this.getBetweenlandsMusicTracks());
		betweenlandsSoundTracks.addAll(getBetweenlandsMenuMusicTracks());
		for(Sound blSound : betweenlandsSoundTracks) {
			if(blSound.getSoundLocation().equals(sound.getSoundLocation())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 * @return A list with menu music
	 */
	@SuppressWarnings("unchecked")
	public List<Sound> getBetweenlandsMenuMusicTracks() {
		if(this.musicMenuTrackAccessors == null) {
			try {
				this.musicMenuTrackAccessors = new ArrayList<>();
				List<ISoundEventAccessor<Sound>> soundAccessors = (List<ISoundEventAccessor<Sound>>) f_accessorList.get(this.mc.getSoundHandler().getAccessor(SoundRegistry.BL_MUSIC_MENU.getSoundName()));
				for(ISoundEventAccessor<Sound> accessor : soundAccessors) {
					if(accessor instanceof Sound) {
						this.musicMenuTrackAccessors.add((Sound) accessor);
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
	@SuppressWarnings("unchecked")
	public List<Sound> getBetweenlandsMusicTracks() {
		if(this.musicDimTrackAccessors == null) {
			try {
				this.musicDimTrackAccessors = new ArrayList<>();
				List<ISoundEventAccessor<Sound>> soundAccessors = (List<ISoundEventAccessor<Sound>>) f_accessorList.get(this.mc.getSoundHandler().getAccessor(SoundRegistry.BL_MUSIC_DIMENSION.getSoundName()));
				for(ISoundEventAccessor<Sound> accessor : soundAccessors) {
					if(accessor instanceof Sound) {
						this.musicDimTrackAccessors.add((Sound) accessor);
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
		List<Sound> availableSounds = new ArrayList<>(menu ? this.getBetweenlandsMenuMusicTracks(): this.getBetweenlandsMusicTracks());
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
				ISound parentSound = PositionedSoundRecord.getMusicRecord(menu ? SoundRegistry.BL_MUSIC_MENU: SoundRegistry.BL_MUSIC_DIMENSION);
				ISound playingSound = SoundWrapper.wrap(parentSound, sound);
				this.currentSound = playingSound;
				this.mc.getSoundHandler().playSound(playingSound);
			}
		}
	}

	private EntityPlayer getPlayer() {
		return this.mc.player;
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
