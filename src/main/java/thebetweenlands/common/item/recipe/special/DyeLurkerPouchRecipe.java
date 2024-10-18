package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.item.misc.BLDyeItem;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;

public class DyeLurkerPouchRecipe extends CustomRecipe {

	private static final List<Item> POUCHES = List.of(ItemRegistry.SMALL_LURKER_SKIN_POUCH.get(), ItemRegistry.MEDIUM_LURKER_SKIN_POUCH.get(), ItemRegistry.LARGE_LURKER_SKIN_POUCH.get(), ItemRegistry.XL_LURKER_SKIN_POUCH.get());

	public DyeLurkerPouchRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		boolean hasDye = false;
		boolean hasPouch = false;

		for (int i = 0; i < input.size(); i++) {
			ItemStack checkStack = input.getItem(i);

			if (POUCHES.contains(checkStack.getItem()) && !checkStack.has(DataComponents.DYED_COLOR)) {
				hasPouch = true;
			} else if (checkStack.getItem() instanceof BLDyeItem) {
				hasDye = true;
			}
		}

		return hasDye && hasPouch;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		BLDyeItem itemStackDye = null;
		ItemStack itemStackPouch = ItemStack.EMPTY;

		for (int i = 0; i < input.size(); i++) {
			ItemStack checkStack = input.getItem(i);

			if (POUCHES.contains(checkStack.getItem()) && !checkStack.has(DataComponents.DYED_COLOR)) {
				itemStackPouch = checkStack;
			} else if (checkStack.getItem() instanceof BLDyeItem dye) {
				itemStackDye = dye;
			}
		}

		if (itemStackDye == null || itemStackPouch.isEmpty()) {
			return ItemStack.EMPTY;
		}

		ItemStack newPouch = itemStackPouch.copy();
		newPouch.set(DataComponents.DYED_COLOR, new DyedItemColor(itemStackDye.getColor().getColorValue(), true));

		return newPouch;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.DYE_LURKER_POUCH.get();
	}
}
