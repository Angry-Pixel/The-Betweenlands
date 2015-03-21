package thebetweenlands.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.PlayerTickEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.world.WorldEvent;
import thebetweenlands.client.audio.AmbienceCaveSound;
import thebetweenlands.client.audio.AmbienceSwampSound;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.WorldProviderBetweenlands;

public class AmbienceSoundPlayHandler
{
    private AmbienceSwampSound ambienceSoundSwamp;
    private AmbienceCaveSound ambienceSoundCave;

    public static final int CAVE_START = WorldProviderBetweenlands.LAYER_HEIGHT - 10;

    @SubscribeEvent
    public void onPlayerCltTick(PlayerTickEvent event) {
        if( event.phase == Phase.START && event.side == Side.CLIENT ) {
            if( event.player.dimension == ModInfo.DIMENSION_ID ) {
                if( event.player.posY >= CAVE_START && (this.ambienceSoundSwamp == null || this.ambienceSoundSwamp.isDonePlaying())) {
                    ambienceSoundSwamp = new AmbienceSwampSound(event.player);
                    Minecraft.getMinecraft().getSoundHandler().playSound(ambienceSoundSwamp);
                }
                if( event.player.posY < CAVE_START && (this.ambienceSoundCave == null || this.ambienceSoundCave.isDonePlaying())) {
                    ambienceSoundCave = new AmbienceCaveSound(event.player);
                    Minecraft.getMinecraft().getSoundHandler().playSound(ambienceSoundCave);
                }
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
        }
    }
}
