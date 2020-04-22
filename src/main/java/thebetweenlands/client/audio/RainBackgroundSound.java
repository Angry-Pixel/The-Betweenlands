package thebetweenlands.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.AmbienceSoundPlayHandler;

@SideOnly(Side.CLIENT)
public class RainBackgroundSound extends MovingSound {
	public RainBackgroundSound(SoundEvent sound, SoundCategory category) {
		super(sound, category);
		this.attenuationType = AttenuationType.NONE;
	}

	@Override
	public void update() {
		this.updateSound();
	}

	private void updateSound() {
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		if(view != null) {
			this.xPosF = AmbienceSoundPlayHandler.getRelativeRainX() + (float)view.posX;
			this.yPosF = AmbienceSoundPlayHandler.getRelativeRainY() + (float)view.posY;
			this.zPosF = AmbienceSoundPlayHandler.getRelativeRainZ() + (float)view.posZ;
			this.pitch = 1.0f - AmbienceSoundPlayHandler.getRainAbove() * 0.5f;
			this.volume = (0.5f - AmbienceSoundPlayHandler.getRainAbove() * 0.4f) * AmbienceSoundPlayHandler.getRainVolume();
		}
	}
}
