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
import net.minecraftforge.registries.IForgeRegistryEntry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesPlantTonic extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	@Override
	public boolean matches(InventoryCrafting crafter, World world) {
		int sap = 0;
		ItemStack bucket = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.BL_BUCKET) {
					if(!bucket.isEmpty())
						return false;
					bucket = stack;
				} else if(stack.getItem() == ItemRegistry.SAP_BALL) {
					sap++;
				} else {
					return false;
				}
			}
		}
		if(bucket.isEmpty() || sap != 1) {
			return false;
		}
		if(bucket.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
			IFluidHandler handler = bucket.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
			FluidStack extracted = handler.drain(new FluidStack(FluidRegistry.SWAMP_WATER, Fluid.BUCKET_VOLUME), false);
			if(extracted != null && extracted.amount == Fluid.BUCKET_VOLUME) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafter) {
		ItemStack bucket = ItemStack.EMPTY;
		for (int i = 0; i < crafter.getSizeInventory(); ++i) {
			ItemStack stack = crafter.getStackInSlot(i);
			if(!stack.isEmpty()) {
				if(stack.getItem() == ItemRegistry.BL_BUCKET) {
					bucket = stack;
					break;
				}
			}
		}
		return new ItemStack(ItemRegistry.BL_BUCKET_PLANT_TONIC, 1, bucket.getMetadata());
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public boolean isDynamic() {
		return true;
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
			if(!stack.isEmpty() && (stack.getItem() == ItemRegistry.BL_BUCKET)) {
				remaining.set(i, ItemStack.EMPTY);
			} else {
				remaining.set(i, ForgeHooks.getContainerItem(stack));
			}
		}

		return remaining;
	}
}