package thebetweenlands.common.items.recipe;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class FluidRecipeInput extends MultiStackInput {

	private final FluidStack fluid;

	public FluidRecipeInput(FluidStack fluid, List<ItemStack> item) {
		super(item);
		this.fluid = fluid;
	}

	public FluidStack getFluid() {
		return this.fluid;
	}
}
