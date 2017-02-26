package thebetweenlands.client.audio;

import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
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

    @Override
    public final boolean isDonePlaying() {
        return isDone;
    }

    @Override
    public void update() {
        if (donePlaying && !isDone) {
            if (pauseTicks == 0) {
                SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
                SoundManager manager = ReflectionHelper.getPrivateValue(SoundHandler.class, handler, "field_147694_f", "sndSystem");
                SoundSystem sys = ReflectionHelper.getPrivateValue(SoundManager.class, manager, "field_148620_e", "sndSystem");
                Map<ISound, String> sounds = ReflectionHelper.getPrivateValue(SoundManager.class, manager, "field_148630_i", "invPlayingSounds");
                sys.pause(sounds.get(this));
            }
            pauseTicks++;
            if (pauseTicks >= 200) {
                isDone = true;
            }
        }
    }
}
