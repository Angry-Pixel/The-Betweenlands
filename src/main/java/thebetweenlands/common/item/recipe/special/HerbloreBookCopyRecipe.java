package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.item.DiscoveryContainerData;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;

public class HerbloreBookCopyRecipe extends CustomRecipe {

	public HerbloreBookCopyRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int bookCount = 0;
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.isEmpty() || !stack.is(ItemRegistry.HERBLORE_BOOK)) {
				return false;
			} else {
				++bookCount;
			}
		}
		return bookCount >= 2;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		List<DiscoveryContainerData> discoveryContainers = new ArrayList<>();
		for (int i = 0; i < input.size(); i++) {
			ItemStack stack = input.getItem(i);
			if (stack.is(ItemRegistry.HERBLORE_BOOK)) {
				discoveryContainers.add(stack.getOrDefault(DataComponentRegistry.DISCOVERY_DATA, DiscoveryContainerData.EMPTY));
			}
		}
		ItemStack result = new ItemStack(ItemRegistry.HERBLORE_BOOK.get(), discoveryContainers.size());
		DiscoveryContainerData newData = DiscoveryContainerData.EMPTY;
		for (DiscoveryContainerData container : discoveryContainers)
			newData.mergeDiscoveries(container);
		result.set(DataComponentRegistry.DISCOVERY_DATA, newData);
		return result;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.HERBLORE_BOOK_COPY.get();
	}
}
