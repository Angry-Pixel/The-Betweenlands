package thebetweenlands.items.armor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.manual.IManualEntryItem;
import thebetweenlands.recipes.BLMaterials;

public class ItemSyrmoriteArmor extends ItemArmor implements IManualEntryItem {

	public ItemSyrmoriteArmor(int armorType) {
		super(BLMaterials.armorOctine, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.syrmoriteLeggings)
			return "thebetweenlands:textures/armour/syrmorite2.png";
		else
			return "thebetweenlands:textures/armour/syrmorite1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.SYRMORITE_INGOT.ordinal();
	}

	@Override
	public String manualName(int meta) {
		return "syrmoriteArmor";
	}

	@Override
	public Item getItem() {
		return this;
	}

	@Override
	public int[] recipeType(int meta) {
		return new int[]{2};
	}

	@Override
	public int metas() {
		return 0;
	}
}