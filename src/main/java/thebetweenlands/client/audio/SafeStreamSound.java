package thebetweenlands.client.audio;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import paulscode.sound.SoundSystem;

@SideOnly(Side.CLIENT)
public class SafeStreamSound extends MovingSound {
    private boolean isDone;

    private int pauseTicks = 0;
    
    protected SafeStreamSound(SoundEvent sound, SoundCategory category) {
        super(sound, category);
    }

    protected boolean isSoundStreamed(boolean defaultVal) {
    	Sound sound = this.getSound();
    	if(sound != null) {
    		return sound.isStreaming();
    	}
    	return defaultVal;
    }
    
    @Override
    public boolean isDonePlaying() {
        return this.isSoundStreamed(false) ? isDone : donePlaying;
    }

    @Override
    public void update() {
        this.updateSafeStreamSound();
    }
    
    protected void updateSafeStreamSound() {
    	if (this.isSoundStreamed(false) && donePlaying && !isDone) {
            if (pauseTicks == 0) {
                SoundManager manager = Minecraft.getMinecraft().getSoundHandler().sndManager;
                SoundSystem sys = manager.sndSystem;
                Map<ISound, String> sounds = manager.invPlayingSounds;
                sys.pause(sounds.get(this));
            }
            pauseTicks++;
            if (pauseTicks >= 200) {
                isDone = true;
            }
        }
    }
}
