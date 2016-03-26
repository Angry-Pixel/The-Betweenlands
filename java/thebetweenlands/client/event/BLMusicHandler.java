package thebetweenlands.client.event;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.audio.EntityMusicSound;
import thebetweenlands.entities.IEntityMusic;
import thebetweenlands.lib.ModInfo;

public class BLMusicHandler {
	private MusicTicker prevMusicTicker;
	private Field mcMusicTickerField;

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if(this.mcMusicTickerField == null) {
			this.mcMusicTickerField = ReflectionHelper.findField(Minecraft.class, "mcMusicTicker", "field_147126_aw", "ax");
		}

		if(event.phase == Phase.START) {
			Minecraft mc = Minecraft.getMinecraft();
			if( mc.theWorld != null ) {
				if( mc.theWorld.provider.dimensionId == ModInfo.DIMENSION_ID && this.prevMusicTicker == null ) {
					this.prevMusicTicker = getMcMusicTicker();
					setMcMusicTicker(new BLMusicTicker(mc));
				} else if( mc.theWorld.provider.dimensionId != ModInfo.DIMENSION_ID && this.prevMusicTicker != null ) {
					setMcMusicTicker(this.prevMusicTicker);
					this.prevMusicTicker = null;
				}
			} else if(this.prevMusicTicker != null) {
				setMcMusicTicker(this.prevMusicTicker);
				this.prevMusicTicker = null;
			}
		}
	}

	private void setMcMusicTicker(MusicTicker musicTicker) {
		try {
			mcMusicTickerField.set(Minecraft.getMinecraft(), musicTicker);
		} catch( IllegalAccessException e ) {
			e.printStackTrace();
		}
	}

	private MusicTicker getMcMusicTicker() {
		try {
			return (MusicTicker) mcMusicTickerField.get(Minecraft.getMinecraft());
		} catch( IllegalAccessException e ) {
			e.printStackTrace();
		}

		return null;
	}

	public static class BLMusicTicker extends MusicTicker {
		private final Random RNG = new Random();
		private final Minecraft mc;
		private ISound currentSound;
		private int timeUntilMusic = 100;
		private static final ResourceLocation BL_MUSIC_RES = new ResourceLocation("thebetweenlands:music.blDimension");
		private static final int MIN_WAIT = 6000;
		private static final int MAX_WAIT = 12000;
		private EntityMusicSound currentlyPlayingEntityMusic = null;

		public BLMusicTicker(Minecraft minecraft) {
			super(minecraft);
			this.mc = minecraft;
		}

		public void update() {
			Entity closestMusicEntity = null;
			if(mc.theWorld != null) {
				for(Entity entity : (List<Entity>) this.mc.theWorld.loadedEntityList) {
					if(entity instanceof IEntityMusic) {
						if((closestMusicEntity == null || entity.getDistanceToEntity(this.mc.thePlayer) < closestMusicEntity.getDistanceToEntity(this.mc.thePlayer))
								&& entity.getDistanceToEntity(this.mc.thePlayer) <= ((IEntityMusic)entity).getMusicRange(this.mc.thePlayer) 
								&& ((IEntityMusic)entity).isMusicActive(this.mc.thePlayer))
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
					this.timeUntilMusic = Math.min(MathHelper.getRandomIntegerInRange(this.RNG, MIN_WAIT, MAX_WAIT), this.timeUntilMusic);
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
					this.currentlyPlayingEntityMusic = new EntityMusicSound(closestMusicEntity, new ResourceLocation(((IEntityMusic)closestMusicEntity).getMusicFile(this.mc.thePlayer)), 1, 1);
					this.mc.getSoundHandler().playSound(this.currentlyPlayingEntityMusic);
				}
			}

			if(this.currentlyPlayingEntityMusic != null && !this.currentlyPlayingEntityMusic.isDonePlaying()) {
				if(!((IEntityMusic)this.currentlyPlayingEntityMusic.entity).isMusicActive(this.mc.thePlayer)) {
					if(this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
						this.currentlyPlayingEntityMusic.stop();
						this.currentlyPlayingEntityMusic = null;
					}
				}
			} else {
				if (this.currentSound != null) {
					if(!BL_MUSIC_RES.equals(this.currentSound.getPositionedSoundLocation())) {
						this.mc.getSoundHandler().stopSound(this.currentSound);
						this.timeUntilMusic = MathHelper.getRandomIntegerInRange(this.RNG, 0, MIN_WAIT / 2);
					}

					if(!this.mc.getSoundHandler().isSoundPlaying(this.currentSound)) {
						this.currentSound = null;
						this.timeUntilMusic = Math.min(MathHelper.getRandomIntegerInRange(this.RNG, MIN_WAIT, MAX_WAIT), this.timeUntilMusic);
					}
				}

				if (this.currentSound == null && this.timeUntilMusic-- <= 0) {
					this.currentSound = PositionedSoundRecord.func_147673_a(BL_MUSIC_RES);
					this.mc.getSoundHandler().playSound(this.currentSound);
					this.timeUntilMusic = Integer.MAX_VALUE;
				}
			}
		}
	}
}
