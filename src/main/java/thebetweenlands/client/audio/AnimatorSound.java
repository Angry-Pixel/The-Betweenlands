package thebetweenlands.client.audio;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityAnimator;

@SideOnly(Side.CLIENT)
public class AnimatorSound extends MovingSound {
	public final TileEntityAnimator animator;

	public AnimatorSound(SoundEvent sound, SoundCategory category, TileEntityAnimator animator) {
		super(sound, category);
		this.repeat = true;
		this.attenuationType = AttenuationType.LINEAR;
		this.animator = animator;
		BlockPos pos = animator.getPos();
		this.xPosF = pos.getX() + 0.5F;
		this.yPosF = pos.getY() + 0.5F;
		this.zPosF = pos.getZ() + 0.5F;
		this.volume = 0.4F;
	}

	@Override
	public void update() {
		if(this.animator == null || !this.animator.hasWorldObj() || !this.animator.getWorld().isBlockLoaded(this.animator.getPos()) || !this.animator.isRunning()
				|| this.animator.getWorld().getTileEntity(this.animator.getPos()) != this.animator) {
			this.repeat = false;
			
			this.volume -= 0.05F;
			if(this.volume <= 0.0F) {
				this.donePlaying = true;
			}
		}
	}
}
