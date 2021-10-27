package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesFishBait extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        this.resultItem = ItemStack.EMPTY;
        int hasBaitItem = 0;
//        int addSaturation = 0;
        int addSinkSpeed = 0;
        int addDissolveTime = 0;
        int addRange = 0;
        boolean addGlowing = false;
        boolean minusGlowing = false;

		for (int inputSlot = 0; inputSlot < inv.getSizeInventory(); ++inputSlot) {
			ItemStack itemstack = inv.getStackInSlot(inputSlot);

			if (!itemstack.isEmpty()) {
				if (itemstack.getItem() == ItemRegistry.FISH_BAIT)
					hasBaitItem++;
//				else if (itemstack.getItem() == ItemRegistry.SNAIL_FLESH_RAW)
//					addSaturation++;
				else if (EnumItemCrushed.GROUND_BETWEENSTONE_PEBBLE.isItemOf(itemstack))
					addSinkSpeed++;
				else if (EnumItemMisc.TAR_DRIP.isItemOf(itemstack))
					addDissolveTime++;
				else if (itemstack.getItem() == ItemRegistry.CRAB_STICK)
					addRange++;
				else if (EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.isItemOf(itemstack))
					addGlowing = true;
//				else if (itemstack.getItem() == ItemRegistry.CANDY_BLUE)
//					addSaturation--;
				else if (EnumItemCrushed.GROUND_BLADDERWORT_STALK.isItemOf(itemstack))
					addSinkSpeed--;
				else if (EnumItemCrushed.GROUND_MILKWEED.isItemOf(itemstack))
					addDissolveTime--;
				else if (itemstack.getItem() == ItemRegistry.SAP_SPIT)
					addRange--;
				else if (itemstack.getItem() == ItemRegistry.SLUDGE_BALL)
					minusGlowing = true;
				else
					return false;
			}
		}

		if (hasBaitItem == 1) {
			if(addGlowing && minusGlowing)
				return false;

			for (int inputSlot2 = 0; inputSlot2 < inv.getSizeInventory(); ++inputSlot2) {
				ItemStack stack = inv.getStackInSlot(inputSlot2);

				if (stack.getItem() == ItemRegistry.FISH_BAIT && stack.hasTagCompound()) {

					this.resultItem = stack.copy().splitStack(1);

					if(stack.getTagCompound().getBoolean("glowing") && addGlowing || !stack.getTagCompound().getBoolean("glowing") && minusGlowing)
						return false;

					//add stuffs
/*					if (stack.getTagCompound().getInteger("saturation") + addSaturation * 20 < 0)
						return false;
					else
						resultItem.getTagCompound().setInteger("saturation", stack.getTagCompound().getInteger("saturation") + addSaturation * 20);
*/
					if (stack.getTagCompound().getInteger("sink_speed") + addSinkSpeed < 0)
						return false;
					else
						resultItem.getTagCompound().setInteger("sink_speed", stack.getTagCompound().getInteger("sink_speed") + addSinkSpeed);

					if (stack.getTagCompound().getInteger("dissolve_time") + addDissolveTime * 20 < 0)
						return false;
					else
						resultItem.getTagCompound().setInteger("dissolve_time", stack.getTagCompound().getInteger("dissolve_time") + addDissolveTime * 20);

					if (stack.getTagCompound().getInteger("range") + addRange < 0)
						return false;
					else
						resultItem.getTagCompound().setInteger("range", stack.getTagCompound().getInteger("range") + addRange);

					if (addGlowing)
						resultItem.getTagCompound().setBoolean("glowing", true);

					if (minusGlowing)
						resultItem.getTagCompound().setBoolean("glowing", false);
				}
			}
			return true;
		}
		return false;
	}

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv){
        return this.resultItem.copy();
    }

    @Override
    public ItemStack getRecipeOutput(){
        return this.resultItem;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv){
        NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for (int i = 0; i < nonnulllist.size(); ++i){
            ItemStack itemstack = inv.getStackInSlot(i);
            nonnulllist.set(i, net.minecraftforge.common.ForgeHooks.getContainerItem(itemstack));
        }
        return nonnulllist;
    }

    @Override
    public boolean isDynamic(){
        return true;
    }

    @Override
    public boolean canFit(int width, int height){
        return width * height >= 1;
    }
}
