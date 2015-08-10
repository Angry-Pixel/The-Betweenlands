package thebetweenlands.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlayStreamingSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.client.audio.AmbienceBloodSky;
import thebetweenlands.client.audio.AmbienceCaveSound;
import thebetweenlands.client.audio.AmbienceSwampSound;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.WorldProviderBetweenlands;

public class AmbienceSoundPlayHandler
{
	private AmbienceSwampSound ambienceSoundSwamp;
	private AmbienceCaveSound ambienceSoundCave;
	private AmbienceBloodSky ambienceBloodSky;

	public static final int CAVE_START = WorldProviderBetweenlands.LAYER_HEIGHT - 10;

	@SubscribeEvent
	public void onPlayerCltTick(PlayerTickEvent event) {
		if( event.phase == Phase.START && event.side == Side.CLIENT ) {
			if( event.player.dimension == ModInfo.DIMENSION_ID ) {
				Minecraft mc = Minecraft.getMinecraft();
				boolean isBloodSky = false;
				World world = mc.theWorld;
				if(world != null && world.provider instanceof WorldProviderBetweenlands) {
					isBloodSky = ((WorldProviderBetweenlands)world.provider).getWorldData().getEnvironmentEventRegistry().BLOODSKY.isActive();
				}
				if(!isBloodSky) {
					if( event.player.posY >= CAVE_START && (this.ambienceSoundSwamp == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceSoundSwamp)) ) {
						if( this.ambienceSoundSwamp != null ) {
							this.ambienceSoundSwamp.stop();
						}
						this.ambienceSoundSwamp = new AmbienceSwampSound(event.player);
						Minecraft.getMinecraft().getSoundHandler().playSound(ambienceSoundSwamp);
					}
					if( event.player.posY < CAVE_START && (ambienceSoundCave == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceSoundCave)) ) {
						if( this.ambienceSoundCave != null ) {
							this.ambienceSoundCave.stop();
						}
						this.ambienceSoundCave = new AmbienceCaveSound(event.player);
						Minecraft.getMinecraft().getSoundHandler().playSound(this.ambienceSoundCave);
					}
				} else {
					//TODO: Improve this if you know a better way of stopping all music sounds playing currently
					if(mc.getSoundHandler().isSoundPlaying(this.ambienceBloodSky)) {
						float prevSoundLvl = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MUSIC);
						Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0.0F);
						Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, prevSoundLvl);
					}
					if(this.ambienceBloodSky == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceBloodSky)) {
						if( this.ambienceBloodSky != null ) {
							this.ambienceBloodSky.stop();
						}
						this.ambienceBloodSky = new AmbienceBloodSky(event.player);
						Minecraft.getMinecraft().getSoundHandler().playSound(this.ambienceBloodSky);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onStreamRandomSound(PlayStreamingSourceEvent event) {
		//TODO: I don't even know if this works.
		SoundEventAccessorComposite soundeventaccessorcomposite = Minecraft.getMinecraft().getSoundHandler().getSound(event.sound.getPositionedSoundLocation());
		if(soundeventaccessorcomposite.getSoundCategory() == SoundCategory.MUSIC) {
			boolean isBloodSky = false;
			World world = Minecraft.getMinecraft().theWorld;
			if(world != null && world.provider instanceof WorldProviderBetweenlands) {
				isBloodSky = ((WorldProviderBetweenlands)world.provider).getWorldData().getEnvironmentEventRegistry().BLOODSKY.isActive();
			}
			if(isBloodSky) {
				Minecraft.getMinecraft().getSoundHandler().stopSound(event.sound);
			}
		}
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event) {
		if( event.world.isRemote ) {
			if( this.ambienceSoundSwamp != null ) {
				this.ambienceSoundSwamp.stop();
				this.ambienceSoundSwamp = null;
			}
			if( this.ambienceSoundCave != null ) {
				this.ambienceSoundCave.stop();
				this.ambienceSoundCave = null;
			}
			if( this.ambienceBloodSky != null ) {
				this.ambienceBloodSky.stop();
				this.ambienceBloodSky = null;
			}
		}
	}
}
