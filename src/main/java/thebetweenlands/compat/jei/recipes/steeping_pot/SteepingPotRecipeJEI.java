package thebetweenlands.compat.jei.recipes.steeping_pot;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import thebetweenlands.api.recipes.ISteepingPotRecipe;

public class SteepingPotRecipeJEI implements IRecipeWrapper {

	private final List<ItemStack> input;
	private final FluidStack fluidIn;
	private final FluidStack fluidOut;

	private final int fluidOutMeta;

    public SteepingPotRecipeJEI(ISteepingPotRecipe recipe) {
		this.fluidOut = recipe.getOutputFluidStack();
		this.fluidOutMeta = recipe.getOutputFluidMeta();
		this.fluidIn = recipe.getInputFluidStack();
		ArrayList<ItemStack> inputs = new ArrayList<>();
		this.input = recipe.getInputAsStacks();
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    	NBTTagCompound typeOut = new NBTTagCompound();
    	typeOut.setInteger("type", this.fluidOutMeta);
    	ingredients.setInput(VanillaTypes.FLUID, new FluidStack(getFluidIn(), Fluid.BUCKET_VOLUME));
		ingredients.setInputs(VanillaTypes.ITEM, getInput());
		ingredients.setOutput(VanillaTypes.FLUID, typeOut.hasKey("type") ? new FluidStack(getFluidOut().getFluid(), Fluid.BUCKET_VOLUME, typeOut): new FluidStack(getFluidOut().getFluid(), Fluid.BUCKET_VOLUME));
    }
    
    public List<ItemStack> getInput() {
		return this.input;
	}

    public FluidStack getFluidOut() {
		return this.fluidOut;
	}
	
	public FluidStack getFluidIn() {
		return this.fluidIn;
	}
}
