package thebetweenlands.recipes;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thebetweenlands.utils.DyeOreDictHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class RecipeDyeColorNBT implements IRecipe {
	private final int recipeWidth;

	private final int recipeHeight;

	private final Object[] recipeItems;

	private final ItemStack recipeOutput;

	private ItemStack result;

	public RecipeDyeColorNBT(ItemStack recipeOutput, Object[] recipeObjects) {
		this.recipeOutput = recipeOutput;

		String craftingRows = "";
		int i = 0;
		int recipeWidth = 0;
		int recipeHeight = 0;

		if (recipeObjects[i] instanceof String[]) {
			String[] craftingLines = (String[]) recipeObjects[i++];

			for (String row : craftingLines) {
				recipeHeight++;
				recipeWidth = row.length();
				craftingRows = craftingRows + row;
			}
		} else {
			while (recipeObjects[i] instanceof String) {
				String row = (String) recipeObjects[i++];
				recipeHeight++;
				recipeWidth = row.length();
				craftingRows = craftingRows + row;
			}
		}
		this.recipeWidth = recipeWidth;
		this.recipeHeight = recipeHeight;

		HashMap<Character, Object> charMap = Maps.<Character, Object>newHashMap();

		for (; i < recipeObjects.length; i += 2) {
			Character recipeSymbol = (Character) recipeObjects[i];
			Object recipeItem = null;

			Object ingredient = recipeObjects[i + 1];
			if (ingredient instanceof Item) {
				recipeItem = new ItemStack((Item) ingredient);
			} else if (ingredient instanceof Block) {
				recipeItem = new ItemStack((Block) ingredient, 1, OreDictionary.WILDCARD_VALUE);
			} else if (ingredient instanceof ItemStack) {
				recipeItem = ((ItemStack) ingredient).copy();
			} else if (ingredient instanceof String) {
				recipeItem = OreDictionary.getOres((String) ingredient);
			}

			charMap.put(recipeSymbol, recipeItem);
		}

		recipeItems = new Object[recipeWidth * recipeHeight];

		for (i = 0; i < recipeWidth * recipeHeight; ++i) {
			char recipeSymbol = craftingRows.charAt(i);

			if (charMap.containsKey(recipeSymbol)) {
				recipeItems[i] = charMap.get(recipeSymbol);
			} else {
				recipeItems[i] = null;
			}
		}
	}

	@Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Override
	public boolean matches(InventoryCrafting inventoryCrafting, World world) {
		result = recipeOutput.copy();
		result.setTagInfo("color", new NBTTagInt(11));
		for (int i = 0; i <= 3 - recipeWidth; i++) {
			for (int j = 0; j <= 3 - recipeHeight; j++) {
				if (checkMatch(inventoryCrafting, i, j, true)) {
					return true;
				}

				if (checkMatch(inventoryCrafting, i, j, false)) {
					return true;
				}
			}
		}
		result = null;
		return false;
	}

	private boolean checkMatch(InventoryCrafting inventoryCrafting, int offsetX, int offsetY, boolean mirrored) {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int ax = x - offsetX;
				int ay = y - offsetY;
				Object target = null;

				if (ax >= 0 && ay >= 0 && ax < recipeWidth && ay < recipeHeight) {
					if (mirrored) {
						target = recipeItems[recipeWidth - ax - 1 + ay * recipeWidth];
					} else {
						target = recipeItems[ax + ay * recipeWidth];
					}
				}

				ItemStack stackInCraftingTable = inventoryCrafting.getStackInRowAndColumn(x, y);

				if (stackInCraftingTable != null || target != null) {
					if (stackInCraftingTable == null || target == null) {
						return false;
					}

					if (target instanceof ItemStack) {
						if (!OreDictionary.itemMatches((ItemStack) target, stackInCraftingTable, false)) {
							return false;
						}
					} else if (target instanceof List) {
						Iterator<ItemStack> iter = ((List<ItemStack>) target).iterator();
						boolean matched = false;
						while (iter.hasNext() && !matched) {
							ItemStack ingredient = iter.next();
							matched = OreDictionary.itemMatches(ingredient, stackInCraftingTable, false);
							if (matched && DyeOreDictHelper.isDye(stackInCraftingTable)) {
								result.setTagInfo("color", new NBTTagInt(DyeOreDictHelper.getDyeMetadata(stackInCraftingTable)));
							}
						}
						if (!matched) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting) {
		return getRecipeOutput().copy();
	}

	@Override
	public int getRecipeSize() {
		return recipeWidth * recipeHeight;
	}
}
