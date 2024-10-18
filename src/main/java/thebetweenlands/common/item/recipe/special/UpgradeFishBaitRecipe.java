package thebetweenlands.common.item.recipe.special;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.item.FishBaitStats;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.RecipeRegistry;

import java.util.ArrayList;
import java.util.List;

public class UpgradeFishBaitRecipe extends CustomRecipe {

	private static final List<Item> INGREDIENTS = List.of(
		ItemRegistry.GROUND_BETWEENSTONE_PEBBLE.get(), //sink speed++
		ItemRegistry.TAR_DRIP.get(), //dissolve time++
		ItemRegistry.CRAB_STICK.get(), //range++
		ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM.get(), //glowing++
		ItemRegistry.GROUND_BLADDERWORT_STALK.get(), //sink speed--
		ItemRegistry.GROUND_MILKWEED.get(), //dissolve time--
		ItemRegistry.SAP_SPIT.get(), //range--
		ItemRegistry.SLUDGE_BALL.get() //glowing--

	);

	public UpgradeFishBaitRecipe(CraftingBookCategory category) {
		super(category);
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		boolean hasBait = false;
		int ingredients = 0;

		for (int inputSlot = 0; inputSlot < input.size(); ++inputSlot) {
			ItemStack itemstack = input.getItem(inputSlot);

			if (!itemstack.isEmpty()) {
				if (itemstack.is(ItemRegistry.FISH_BAIT)) {
					hasBait = true;
				} else if (INGREDIENTS.contains(itemstack.getItem())) {
					ingredients++;
				}
			}
		}
		return hasBait && ingredients > 0;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		List<Item> ingredients = new ArrayList<>();
		ItemStack bait = ItemStack.EMPTY;
		for (int i = 0; i < input.size(); ++i) {
			ItemStack itemstack = input.getItem(i);
			if (!itemstack.isEmpty()) {
				if (itemstack.is(ItemRegistry.FISH_BAIT)) {
					if (bait.isEmpty()) {
						bait = itemstack;
					} else {
						//Only accept 1 bait
						return ItemStack.EMPTY;
					}
				} else if (INGREDIENTS.contains(itemstack.getItem())) {
					ingredients.add(itemstack.getItem());
				}
			}
		}

		if (!ingredients.isEmpty() && !bait.isEmpty()) {
			FishBaitStats stats = bait.getOrDefault(DataComponentRegistry.FISH_BAIT, FishBaitStats.DEFAULT);

			if (ingredients.contains(ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM.get()) && ingredients.contains(ItemRegistry.SLUDGE_BALL.get())) return ItemStack.EMPTY;

			for (Item upgrade : ingredients) {
				if (upgrade == ItemRegistry.GROUND_BETWEENSTONE_PEBBLE.get()) {
					stats = stats.withSinkSpeed(stats.sinkSpeed() + 1);
				} else if (upgrade == ItemRegistry.TAR_DRIP.get()) {
					stats = stats.withDissolveTime(stats.dissolveTime() + 20);
				} else if (upgrade == ItemRegistry.CRAB_STICK.get()) {
					stats = stats.withRange(stats.range() + 1);
				} else if (upgrade == ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM.get()) {
					if (stats.glowing()) return ItemStack.EMPTY;
					stats = stats.withGlow(true);
				} else if (upgrade == ItemRegistry.GROUND_BLADDERWORT_STALK.get()) {
					stats = stats.withSinkSpeed(stats.sinkSpeed() - 1);
					if (stats.sinkSpeed() < 0) return ItemStack.EMPTY;
				} else if (upgrade == ItemRegistry.GROUND_MILKWEED.get()) {
					stats = stats.withDissolveTime(stats.dissolveTime() - 20);
					if (stats.dissolveTime() < 0) return ItemStack.EMPTY;
				} else if (upgrade == ItemRegistry.SAP_SPIT.get()) {
					stats = stats.withRange(stats.range() - 1);
					if (stats.range() < 0) return ItemStack.EMPTY;
				} else if (upgrade == ItemRegistry.SLUDGE_BALL.get()) {
					if (!stats.glowing()) return ItemStack.EMPTY;
					stats = stats.withGlow(false);
				} else {
					return ItemStack.EMPTY; //Invalid item, fail craft
				}
			}
			var finalBait = bait.copy();
			finalBait.set(DataComponentRegistry.FISH_BAIT, stats);
			return finalBait;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RecipeRegistry.UPGRADE_FISH_BAIT.get();
	}
}
