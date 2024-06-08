package thebetweenlands.common.colors;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BetweenlandsGrassBlockColors implements BlockColor {

	@Override
	public int getColor(BlockState p_92567_, BlockAndTintGetter p_92568_, BlockPos p_92569_, int p_92570_) {
		if (p_92570_ == 0) {
			return BiomeColors.getAverageGrassColor(p_92568_,p_92569_);
		}
		else {
			return -1;
		}
	}
}
