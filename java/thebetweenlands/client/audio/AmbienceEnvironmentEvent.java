package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;

public class AmbienceEnvironmentEvent extends MovingSound
{
    private EnvironmentEvent event;

    public AmbienceEnvironmentEvent(ResourceLocation sound, EnvironmentEvent event) {
        super(sound);
        this.repeat = true;
        this.field_147666_i = AttenuationType.NONE;
        this.volume = 0.1F;
        this.event = event;
    }

    @Override
    public void update() {
    	WorldProviderBetweenlands provider = WorldProviderBetweenlands.getProvider(Minecraft.getMinecraft().theWorld);
        if(provider == null || !event.isActive()) {
            if( this.volume > 0.05F ) {
                this.volume -= 0.02F;
                if( this.volume < 0.05F ) {
                    this.volume = 0.05F;
                }
            } else {
                this.donePlaying = true;
            }
        } else {
            if( this.volume < 1.0F ) {
                this.volume += 0.02F;
                if( this.volume > 1.0F ) {
                    this.volume = 1.0F;
                }
            }
        }
    }

    public void stop() {
        this.donePlaying = true;
    }
}
