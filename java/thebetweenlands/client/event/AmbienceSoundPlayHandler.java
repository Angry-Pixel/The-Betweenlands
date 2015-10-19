package thebetweenlands.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent.SoundSourceEvent;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.client.audio.AmbienceCaveSound;
import thebetweenlands.client.audio.AmbienceEnvironmentEvent;
import thebetweenlands.client.audio.AmbienceSwampSound;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.WorldProviderBetweenlands;

public class AmbienceSoundPlayHandler
{
	private AmbienceSwampSound ambienceSoundSwamp;
	private AmbienceCaveSound ambienceSoundCave;
	private AmbienceEnvironmentEvent ambienceBloodSky;
	private AmbienceEnvironmentEvent ambienceSpoopy;
	private int bloodSkyAmbienceTimer = 0;

	@SubscribeEvent
	public void onPlayerCltTick(PlayerTickEvent event) {
		if( event.phase == Phase.START && event.side == Side.CLIENT ) {
			if( event.player.dimension == ModInfo.DIMENSION_ID ) {
				Minecraft mc = Minecraft.getMinecraft();
				boolean isBloodSky = false;
				WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(mc.theWorld);
				if(provider == null) return;
				isBloodSky = provider.getWorldData().getEnvironmentEventRegistry().BLOODSKY.isActive();
				if(!isBloodSky) {
					if( event.player.posY >= WorldProviderBetweenlands.CAVE_START && (this.ambienceSoundSwamp == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceSoundSwamp)) ) {
						if( this.ambienceSoundSwamp != null ) {
							this.ambienceSoundSwamp.stop();
						}
						this.ambienceSoundSwamp = new AmbienceSwampSound(event.player);
						Minecraft.getMinecraft().getSoundHandler().playSound(ambienceSoundSwamp);
					}
					if( event.player.posY < WorldProviderBetweenlands.CAVE_START && (ambienceSoundCave == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceSoundCave)) ) {
						if( this.ambienceSoundCave != null ) {
							this.ambienceSoundCave.stop();
						}
						this.ambienceSoundCave = new AmbienceCaveSound(event.player);
						Minecraft.getMinecraft().getSoundHandler().playSound(this.ambienceSoundCave);
					}

					if(provider.getEnvironmentEventRegistry().SPOOPY.isActive()) {
						if(this.ambienceSpoopy == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceSpoopy)) {
							if( this.ambienceSpoopy != null ) {
								this.ambienceSpoopy.stop();
							}
							this.ambienceSpoopy = new AmbienceEnvironmentEvent(new ResourceLocation("thebetweenlands:ambientSpoopy"), provider.getEnvironmentEventRegistry().SPOOPY);
							Minecraft.getMinecraft().getSoundHandler().playSound(this.ambienceSpoopy);
						}
					}

					this.bloodSkyAmbienceTimer = 0;
				} else {
					float prevSoundLvl = Minecraft.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MUSIC);
					Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, 0.0F);
					Minecraft.getMinecraft().gameSettings.setSoundLevel(SoundCategory.MUSIC, prevSoundLvl);
					if(this.bloodSkyAmbienceTimer < 140) {
						this.bloodSkyAmbienceTimer++;
					} else {
						if(this.ambienceBloodSky == null || !mc.getSoundHandler().isSoundPlaying(this.ambienceBloodSky)) {
							if( this.ambienceBloodSky != null ) {
								this.ambienceBloodSky.stop();
							}
							this.ambienceBloodSky = new AmbienceEnvironmentEvent(new ResourceLocation("thebetweenlands:ambientBloodSky"), provider.getEnvironmentEventRegistry().BLOODSKY);
							Minecraft.getMinecraft().getSoundHandler().playSound(this.ambienceBloodSky);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlaySound(SoundSourceEvent event) {
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
