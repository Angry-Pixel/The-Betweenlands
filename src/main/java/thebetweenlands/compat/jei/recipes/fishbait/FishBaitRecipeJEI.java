package thebetweenlands.compat.jei.recipes.fishbait;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.util.NBTHelper;

public class FishBaitRecipeJEI implements ICraftingRecipeWrapper {

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.FISH_BAIT;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    	//TODO - This needs some stupid Wrapper shit to show all things - I don't want to make it :P
    	
    	List<ItemStack> modifierItems = NonNullList.create();
    	modifierItems.add(EnumItemCrushed.GROUND_BETWEENSTONE_PEBBLE.create(1));
    	modifierItems.add(EnumItemMisc.TAR_DRIP.create(1));
    	modifierItems.add(new ItemStack(ItemRegistry.CRAB_STICK));
    	modifierItems.add(EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.create(1));
    	modifierItems.add(EnumItemCrushed.GROUND_BLADDERWORT_STALK.create(1));
    	modifierItems.add(EnumItemCrushed.GROUND_MILKWEED.create(1));
    	modifierItems.add(new ItemStack(ItemRegistry.SAP_SPIT));
    	modifierItems.add(new ItemStack(ItemRegistry.SLUDGE_BALL));
    	
		ItemStack baitItem = new ItemStack(ItemRegistry.FISH_BAIT);
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(baitItem);
		nbt.setInteger("sink_speed", 3);
		nbt.setInteger("dissolve_time", 200);
		nbt.setInteger("range", 1);
		nbt.setBoolean("glowing", false);
		baitItem.setTagCompound(nbt);

		ItemStack baitItemWithMods1 = new ItemStack(ItemRegistry.FISH_BAIT);
		NBTTagCompound nbt1 = NBTHelper.getStackNBTSafe(baitItemWithMods1);
		nbt1.setInteger("jei_tooltip", 1);
		baitItemWithMods1.setTagCompound(nbt1);

        List<List<ItemStack>> inputLists = new ArrayList<>(2);
        inputLists.add(NonNullList.from(ItemStack.EMPTY, baitItem));
        inputLists.add(modifierItems);

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, baitItemWithMods1);
    }
}
