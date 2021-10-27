package thebetweenlands.compat.jei.recipes.misc;

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
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.util.NBTHelper;

public class FishingRodWormAddRecipeJEI implements ICraftingRecipeWrapper {

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.FISHING_ROD_WORM_ADD;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
    	ItemStack rodNoWorm = new ItemStack(ItemRegistry.WEEDWOOD_FISHING_ROD);
    	NBTTagCompound nbt1 = NBTHelper.getStackNBTSafe(rodNoWorm);
    	nbt1.setBoolean("baited", false);
    	rodNoWorm.setTagCompound(nbt1);

    	ItemStack rodHasWorm = new ItemStack(ItemRegistry.WEEDWOOD_FISHING_ROD);
    	NBTTagCompound nbt2 = NBTHelper.getStackNBTSafe(rodHasWorm);
    	nbt2.setBoolean("baited", true);
    	rodHasWorm.setTagCompound(nbt2);

        List<List<ItemStack>> inputLists = new ArrayList<>(2);
        List<ItemStack> worms = NonNullList.create();
        inputLists.add(NonNullList.from(ItemStack.EMPTY, rodNoWorm));
        worms.add(new ItemStack(ItemRegistry.TINY_SLUDGE_WORM));
        worms.add(new ItemStack(ItemRegistry.TINY_SLUDGE_WORM_HELPER));
        inputLists.add(worms);

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutput(VanillaTypes.ITEM, rodHasWorm);
    }
}
