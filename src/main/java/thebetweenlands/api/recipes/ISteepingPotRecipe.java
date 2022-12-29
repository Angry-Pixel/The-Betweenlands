package thebetweenlands.api.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

public interface ISteepingPotRecipe {

	public ItemStack getOutputItem();

	public boolean matches(IFluidTank tankIn, ItemStack... stacks);

	public FluidStack getOutputFluidStack();

	public int getOutputFluidMeta();

	public FluidStack getInputFluidStack();

	public Object[] getInputs();
	
	public List<ItemStack> getInputAsStacks();
	
	public boolean containsInput(ItemStack stack);
}
