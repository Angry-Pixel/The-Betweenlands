package thebetweenlands.compat.jei.recipes.fishbait;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.item.herblore.ItemCrushed.EnumItemCrushed;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.NBTHelper;

public class FishBaitRecipeJEI implements ICraftingRecipeWrapper, ICustomCraftingRecipeWrapper {
	private final ICraftingGridHelper craftingGridHelper;


	public FishBaitRecipeJEI(IGuiHelper guiHelper) {
		craftingGridHelper = guiHelper.createCraftingGridHelper(1, 0);
	}


	@Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return RecipeRegistry.FISH_BAIT;
    }


	public ItemStack baitModifierWrapper(ItemStack modifier) {
		ItemStack resultItem = new ItemStack(ItemRegistry.FISH_BAIT);
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(resultItem);
		nbt.setInteger("sink_speed", 3);
		nbt.setInteger("dissolve_time", 200);
		nbt.setInteger("range", 1);
		nbt.setBoolean("glowing", false);
		resultItem.setTagCompound(nbt);

		int addSinkSpeed = 0;
		int addDissolveTime = 0;
		int addRange = 0;
		boolean addGlowing = false;
		boolean minusGlowing = false;

		if (EnumItemCrushed.GROUND_BETWEENSTONE_PEBBLE.isItemOf(modifier))
			addSinkSpeed++;
		else if (EnumItemMisc.TAR_DRIP.isItemOf(modifier))
			addDissolveTime++;
		else if (modifier.getItem() == ItemRegistry.CRAB_STICK)
			addRange++;
		else if (EnumItemCrushed.GROUND_BULB_CAPPED_MUSHROOM.isItemOf(modifier))
			addGlowing = true;
		else if (EnumItemCrushed.GROUND_BLADDERWORT_STALK.isItemOf(modifier))
			addSinkSpeed--;
		else if (EnumItemCrushed.GROUND_MILKWEED.isItemOf(modifier))
			addDissolveTime--;
		else if (modifier.getItem() == ItemRegistry.SAP_SPIT)
			addRange--;
		else if (modifier.getItem() == ItemRegistry.SLUDGE_BALL)
			minusGlowing = true;

		if (resultItem.getTagCompound().getInteger("sink_speed") + addSinkSpeed > 0)
			resultItem.getTagCompound().setInteger("sink_speed", resultItem.getTagCompound().getInteger("sink_speed") + addSinkSpeed);

		if (resultItem.getTagCompound().getInteger("dissolve_time") + addDissolveTime * 20 >= 0)
			resultItem.getTagCompound().setInteger("dissolve_time", resultItem.getTagCompound().getInteger("dissolve_time") + addDissolveTime * 20);

		if (resultItem.getTagCompound().getInteger("range") + addRange > 0)
			resultItem.getTagCompound().setInteger("range", resultItem.getTagCompound().getInteger("range") + addRange);

		if (addGlowing)
			resultItem.getTagCompound().setBoolean("glowing", true);

		if (minusGlowing)
			resultItem.getTagCompound().setBoolean("glowing", false);

		return resultItem;
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

    	List<ItemStack> baitItems = NonNullList.create();

		ItemStack baitItem = new ItemStack(ItemRegistry.FISH_BAIT);
		NBTTagCompound nbt = NBTHelper.getStackNBTSafe(baitItem);
		nbt.setInteger("sink_speed", 3);
		nbt.setInteger("dissolve_time", 200);
		nbt.setInteger("range", 1);
		nbt.setBoolean("glowing", false);
		baitItem.setTagCompound(nbt);

		for(ItemStack modifier : modifierItems) {
			baitItems.add(baitModifierWrapper(modifier));
		}

		System.out.println(baitItems.size());

        List<List<ItemStack>> inputLists = new ArrayList<>(2);
        inputLists.add(NonNullList.from(ItemStack.EMPTY, baitItem));
        inputLists.add(modifierItems);

		List<List<ItemStack>> outputLists = new ArrayList<>(1);
		outputLists.add(baitItems);

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);
        ingredients.setOutputLists(VanillaTypes.ITEM, outputLists);
    }

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
		recipeLayout.setShapeless();

		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		List<List<ItemStack>> inputs = ingredients.getInputs(VanillaTypes.ITEM);
		List<List<ItemStack>> outputs = ingredients.getOutputs(VanillaTypes.ITEM);

		guiItemStacks.setOverrideDisplayFocus(null);
		craftingGridHelper.setInputs(guiItemStacks, inputs);
		guiItemStacks.set(0, outputs.get(0));
		BetweenlandsJEIPlugin.addRecipeName(getRegistryName(), guiItemStacks, 0);
	}
}
