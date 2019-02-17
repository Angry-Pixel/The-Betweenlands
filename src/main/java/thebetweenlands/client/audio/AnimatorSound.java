package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.tile.TileEntityAnimator;

@OnlyIn(Dist.CLIENT)
public class AnimatorSound extends TileEntitySound<TileEntityAnimator> {
	public AnimatorSound(SoundEvent sound, SoundCategory category, TileEntityAnimator animator) {
		super(sound, category, animator, (tile) -> tile.isRunning());
		this.volume = 0.01F;
	}
	
	@Override
	public void update() {
		if(this.volume < 0.5F && !this.isStopping()) {
			this.volume += 0.05F;
		}
		super.update();
	}
}
