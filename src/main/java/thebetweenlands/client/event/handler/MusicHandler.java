package thebetweenlands.client.event.handler;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventAccessor;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.config.ConfigHandler;

public class MusicHandler {
	public static final MusicHandler INSTANCE = new MusicHandler();

	private MusicHandler() { }

	private static final Field f_accessorList = ReflectionHelper.findField(SoundEventAccessor.class, "accessorList", "a", "field_188716_a");

	private static final int MIN_WAIT = 6000;
	private static final int MAX_WAIT = 12000;

	private List<Sound> musicTrackAccessors;
	private Minecraft mc = Minecraft.getMinecraft();
	private final Random RNG = new Random();
	private int timeUntilMusic = 100;
	private ISound currentSound;
	private Sound previousSound;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		EntityPlayer player = getPlayer();

		if(player != null && this.mc.gameSettings.getSoundLevel(SoundCategory.MUSIC) > 0.0F) {
			if(this.currentSound != null) {
				//Wait for sound track to finish
				if(!this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
					this.currentSound = null;
					this.timeUntilMusic = Math.min(MathHelper.getRandomIntegerInRange(this.RNG, MIN_WAIT, MAX_WAIT), this.timeUntilMusic);
				}
			} else if(this.timeUntilMusic-- <= 0) {
				//Start new sound track
				this.timeUntilMusic = MathHelper.getRandomIntegerInRange(this.RNG, MIN_WAIT, MAX_WAIT);
				this.playRandomSoundTrack();
			}
		}
	}

	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event) {
		EntityPlayer player = getPlayer();

		if(player != null && player.dimension == ConfigHandler.dimensionId && event.getSound().getCategory() == SoundCategory.MUSIC && !this.isBetweenlandsMusic(event.getSound())) {
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
		if(SoundRegistry.BL_MUSIC_DIMENSION.getSoundName().equals(sound.getSoundLocation())) {
			return true;
		}
		List<Sound> betweenlandsSoundTracks = this.getBetweenlandsMusicTracks();
		for(Sound blSound : betweenlandsSoundTracks) {
			if(blSound.getSoundLocation().equals(sound.getSoundLocation())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of all Betweenlands music tracks
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Sound> getBetweenlandsMusicTracks() {
		if(this.musicTrackAccessors == null) {
			try {
				this.musicTrackAccessors = new ArrayList<Sound>();
				List<ISoundEventAccessor<Sound>> soundAccessors = (List<ISoundEventAccessor<Sound>>) f_accessorList.get(this.mc.getSoundHandler().getAccessor(SoundRegistry.BL_MUSIC_DIMENSION.getSoundName()));
				for(ISoundEventAccessor<Sound> accessor : soundAccessors) {
					if(accessor instanceof Sound) {
						this.musicTrackAccessors.add((Sound) accessor);
					}
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return this.musicTrackAccessors;
	}

	/**
	 * Plays a random Betweenlands sound track.
	 * The previously played sound track will be excluded.
	 */
	public void playRandomSoundTrack() {
		List<Sound> availableSounds = new ArrayList<Sound>(this.getBetweenlandsMusicTracks());
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
				ISound parentSound = PositionedSoundRecord.getMusicRecord(SoundRegistry.BL_MUSIC_DIMENSION);
				ISound playingSound = new SoundWrapper(parentSound, sound);
				this.currentSound = playingSound;
				this.mc.getSoundHandler().playSound(playingSound);
			}
		}
	}

	private EntityPlayer getPlayer() {
		return this.mc.thePlayer;
	}

	protected static class SoundWrapper implements ISound {
		private final ISound parent;
		private final Sound sound;

		public SoundWrapper(ISound parent, Sound sound) {
			this.parent = parent;
			this.sound = sound;
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
}
