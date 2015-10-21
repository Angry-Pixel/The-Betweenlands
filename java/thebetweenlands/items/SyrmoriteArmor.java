package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.BLMaterials;

public class SyrmoriteArmor extends ItemArmor {

	public SyrmoriteArmor(int armorType) {
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
		return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == EnumMaterialsBL.SYRMORITE_INGOT.ordinal();
	}
	
}