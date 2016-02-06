package thebetweenlands.utils;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public class DyeOreDictHelper {
	private static final String[] DYE_NAMES = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};

	private static List<ItemStack>[] dyeItemStacks;

	private static List<ItemStack> allDyeItemStacks;

	private DyeOreDictHelper() {
	}

	public static boolean isDye(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getItem() == Items.dye) {
				return true;
			}
			initDyeItemStacks();
			for (ItemStack dye : allDyeItemStacks) {
				if (OreDictionary.itemMatches(dye, itemStack, false)) {
					return true;
				}
			}
		}
		return false;
	}

	public static int getDyeMetadata(ItemStack itemStack) {
		if (itemStack != null) {
			if (itemStack.getItem() == Items.dye) {
				return itemStack.getItemDamage();
			}
			initDyeItemStacks();
			for (int i = 0; i < DYE_NAMES.length; i++) {
				List<ItemStack> dyes = dyeItemStacks[i];
				for (ItemStack dye : dyes) {
					if (OreDictionary.itemMatches(dye, itemStack, false)) {
						return i;
					}
				}
			}
		}
		return -1;
	}

	private static void initDyeItemStacks() {
		if (dyeItemStacks == null) {
			dyeItemStacks = new List[DYE_NAMES.length];
			allDyeItemStacks = new ArrayList<ItemStack>();
			for (int i = 0; i < DYE_NAMES.length; i++) {
				dyeItemStacks[i] = OreDictionary.getOres(DYE_NAMES[i]);
				allDyeItemStacks.addAll(dyeItemStacks[i]);
			}
		}
	}
}
