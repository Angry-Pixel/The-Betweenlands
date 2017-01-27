package thebetweenlands.client.audio;

import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityAnimator;

@SideOnly(Side.CLIENT)
public class AnimatorSound extends TileEntitySound<TileEntityAnimator> {
	public AnimatorSound(SoundEvent sound, SoundCategory category, TileEntityAnimator animator) {
		super(sound, category, animator, (tile) -> tile.isRunning());
	}
}
