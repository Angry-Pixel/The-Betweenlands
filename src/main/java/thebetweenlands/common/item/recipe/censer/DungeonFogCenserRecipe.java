package thebetweenlands.common.item.recipe.censer;

import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.api.block.Censer;
import thebetweenlands.common.registries.FluidRegistry;

public class DungeonFogCenserRecipe extends AbstractCenserRecipe<Void> {

	@Override
	public boolean matchesInput(FluidStack stack) {
		return stack.is(FluidRegistry.FOG_STILL);
	}

	@Override
	public boolean isCreatingDungeonFog(Void context, Censer censer) {
		return true;
	}

	@Override
	public int getConsumptionDuration(Void context, Censer censer) {
		return 18;
	}
}
