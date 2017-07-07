package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesPlantTonic implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		int sap = 0;
		ItemStack bucket = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.SYRMORITE_BUCKET_FILLED || stack.getItem() == ItemRegistry.WEEDWOOD_BUCKET_FILLED) {
					if(bucket != null)
						return false;
					bucket = stack;
				} else if(stack.getItem() == ItemRegistry.SAP_BALL) {
					sap++;
				} else {
					return false;
				}
			}
		}
		if(bucket == null || sap != 1) {
			return false;
		}
		if(bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
			IFluidHandler handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			FluidStack extracted = handler.drain(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME), false);
			if(extracted != null && extracted.amount == Fluid.BUCKET_VOLUME) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack bucket = null;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(stack != null) {
				if(stack.getItem() == ItemRegistry.SYRMORITE_BUCKET_FILLED || stack.getItem() == ItemRegistry.WEEDWOOD_BUCKET_FILLED) {
					bucket = stack;
					break;
				}
			}
		}
		if(bucket.getItem() == ItemRegistry.SYRMORITE_BUCKET_FILLED) {
			return new ItemStack(ItemRegistry.SYRMORITE_BUCKET_PLANT_TONIC);
		} else {
			return new ItemStack(ItemRegistry.WEEDWOOD_BUCKET_PLANT_TONIC);
		}
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

		for (int i = 0; i < remaining.size(); ++i) {
			ItemStack stack = inv.getStackInSlot(i);
			if(stack != null && (stack.getItem() == ItemRegistry.SYRMORITE_BUCKET_FILLED || stack.getItem() == ItemRegistry.WEEDWOOD_BUCKET_FILLED)) {
				remaining.set(i, null);
			} else {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			}
		}

		return remaining;
	}
}