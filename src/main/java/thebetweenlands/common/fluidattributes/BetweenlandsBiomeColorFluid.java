package thebetweenlands.common.fluidattributes;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;

public class BetweenlandsBiomeColorFluid extends FluidAttributes {

	protected BetweenlandsBiomeColorFluid(Builder builder, Fluid fluid) {
		super(builder, fluid);
	}
	
	@Override
	public int getColor(BlockAndTintGetter world, BlockPos pos) {
		return BiomeColors.getAverageFoliageColor(world,pos);
	}
}
