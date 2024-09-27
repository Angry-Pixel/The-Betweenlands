package thebetweenlands.common.fluid;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.TheBetweenlands;

public class SwampWaterFluidType extends BasicFluidType {

	private static final int DEEP_COLOR_R = 19;
	private static final int DEEP_COLOR_G = 24;
	private static final int DEEP_COLOR_B = 68;

	public SwampWaterFluidType() {
		super("swamp_water");
	}

	@Override
	public int getTintColor() {
		return 0xFF2981FF;
	}

	@Override
	public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
		int r = 0;
		int g = 0;
		int b = 0;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				int colorMultiplier = BiomeColors.getAverageWaterColor(getter, pos);
				r += FastColor.ARGB32.red(colorMultiplier);
				g += FastColor.ARGB32.green(colorMultiplier);
				b += FastColor.ARGB32.blue(colorMultiplier);
			}
		}
		r /= 9;
		g /= 9;
		b /= 9;
		float depth;
		if (pos.getY() > TheBetweenlands.CAVE_START) {
			depth = 1;
		} else {
			if (pos.getY() < TheBetweenlands.CAVE_WATER_HEIGHT) {
				depth = 0;
			} else {
				depth = (pos.getY() - TheBetweenlands.CAVE_WATER_HEIGHT) / (float) (TheBetweenlands.CAVE_START - TheBetweenlands.CAVE_WATER_HEIGHT);
			}
		}
		r = (int) (r * depth + DEEP_COLOR_R * (1 - depth) + 0.5F);
		g = (int) (g * depth + DEEP_COLOR_G * (1 - depth) + 0.5F);
		b = (int) (b * depth + DEEP_COLOR_B * (1 - depth) + 0.5F);
		return FastColor.ARGB32.color(FastColor.as8BitChannel(1.0F), r, g, b);
	}
}
