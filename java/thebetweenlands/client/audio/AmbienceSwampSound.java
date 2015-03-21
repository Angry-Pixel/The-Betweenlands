package thebetweenlands.client.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.event.AmbienceSoundPlayHandler;
import thebetweenlands.lib.ModInfo;

public class AmbienceSwampSound
        extends MovingSound
{
    private EntityPlayer player;

    public AmbienceSwampSound(EntityPlayer entityPlayer) {
        super(new ResourceLocation("thebetweenlands:ambientSwamp"));
        this.player = entityPlayer;
        this.repeat = true;
        this.field_147666_i = ISound.AttenuationType.NONE;
        this.volume = 0.1F;
    }

    @Override
    public void update() {
        if( this.player.dimension != ModInfo.DIMENSION_ID || this.player.posY < AmbienceSoundPlayHandler.CAVE_START ) {
            if( this.volume > 0.05F ) {
                this.volume -= 0.075F;
                if( this.volume < 0.05F ) {
                    this.volume = 0.05F;
                }
            } else {
                this.donePlaying = true;
            }
        } else {
            if( this.volume < 1.0F ) {
                this.volume += 0.2F;
                if( this.volume > 1.0F ) {
                    this.volume = 1.0F;
                }
            }
        }
    }
}
