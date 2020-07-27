package thebetweenlands.common.recipe.misc;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import thebetweenlands.common.registries.ItemRegistry;

public class RecipesFishBait extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        this.resultItem = ItemStack.EMPTY;
        boolean hasBaitItem = false;
        boolean addSaturation = false;
        boolean addSinkSpeed = false;
        boolean addDissolveTime = false;
        boolean addRange = false;
        boolean addGlowing = false;

        boolean minusSaturation = false;
        boolean minusSinkSpeed = false;
        boolean minusDissolveTime = false;
        boolean minusRange = false;
        boolean minusGlowing = false;

		for (int inputSlot = 0; inputSlot < inv.getSizeInventory(); ++inputSlot) {
			ItemStack itemstack = inv.getStackInSlot(inputSlot);

			if (!itemstack.isEmpty()) {
				if (itemstack.getItem() == ItemRegistry.FISH_BAIT)
					hasBaitItem = true;
				else if (itemstack.getItem() == ItemRegistry.SNAIL_FLESH_RAW)
					addSaturation = true;
				else if (itemstack.getItem() == ItemRegistry.CHRISTMAS_PUDDING)
					addSinkSpeed = true;
				else if (itemstack.getItem() == ItemRegistry.GLUE)
					addDissolveTime = true;
				else if (itemstack.getItem() == ItemRegistry.CRAB_STICK)
					addRange = true;
				else if (itemstack.getItem() == ItemRegistry.BULB_CAPPED_MUSHROOM_ITEM)
					addGlowing = true;
				else if (itemstack.getItem() == ItemRegistry.CANDY_BLUE)
					minusSaturation = true;
				else if (itemstack.getItem() == ItemRegistry.MARSHMALLOW_PINK)
					minusSinkSpeed = true;
				else if (itemstack.getItem() == ItemRegistry.SAP_SPIT)
					minusDissolveTime = true;
				else if (itemstack.getItem() == ItemRegistry.SLUDGE_BALL)
					minusRange = true;
				else if (itemstack.getItem() == ItemRegistry.FLAT_HEAD_MUSHROOM_ITEM)
					minusGlowing = true;
			}
		}

		if (hasBaitItem) {
			if(addSaturation && minusSaturation)
				return false;
			if(addSinkSpeed && minusSinkSpeed)
				return false;
			if(addDissolveTime && minusDissolveTime)
				return false;
			if(addRange && minusRange)
				return false;
			if(addGlowing && minusGlowing)
				return false;

			for (int inputSlot2 = 0; inputSlot2 < inv.getSizeInventory(); ++inputSlot2) {
				ItemStack stack = inv.getStackInSlot(inputSlot2);

				if (stack.getItem() == ItemRegistry.FISH_BAIT && stack.hasTagCompound()) {

					this.resultItem = stack.copy().splitStack(1);
					if(stack.getTagCompound().getBoolean("glowing") && addGlowing || !stack.getTagCompound().getBoolean("glowing") && minusGlowing)
						return false;

					//add
					if (addSaturation)
						resultItem.getTagCompound().setInteger("saturation", stack.getTagCompound().getInteger("saturation") + 20);
					if (addSinkSpeed)
						resultItem.getTagCompound().setInteger("sink_speed", stack.getTagCompound().getInteger("sink_speed") + 1);
					if (addDissolveTime)
						resultItem.getTagCompound().setInteger("dissolve_time", stack.getTagCompound().getInteger("dissolve_time") + 20);
					if (addRange)
						resultItem.getTagCompound().setInteger("range", stack.getTagCompound().getInteger("range") + 1);
					if (addGlowing)
						resultItem.getTagCompound().setBoolean("glowing", true);

					//minus
					if (minusSaturation)
						if (stack.getTagCompound().getInteger("saturation") <= 0)
							return false;
						else
							resultItem.getTagCompound().setInteger("saturation", stack.getTagCompound().getInteger("saturation") - 20);
	
					if (minusSinkSpeed)
						if (stack.getTagCompound().getInteger("sink_speed") <= 0)
							return false;
						else
							resultItem.getTagCompound().setInteger("sink_speed", stack.getTagCompound().getInteger("sink_speed") - 1);

					if (minusDissolveTime)
						if (stack.getTagCompound().getInteger("dissolve_time") <= 0)
							return false;
						else
							resultItem.getTagCompound().setInteger("dissolve_time", stack.getTagCompound().getInteger("dissolve_time") - 20);

					if (minusRange)
						if (stack.getTagCompound().getInteger("range") <= 0)
							return false;
						else
							resultItem.getTagCompound().setInteger("range", stack.getTagCompound().getInteger("range") - 1);

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
