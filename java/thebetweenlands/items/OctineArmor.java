package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.BLMaterials;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class OctineArmor extends ItemArmor {

	public OctineArmor(int armorType) {
		super(BLMaterials.armorOctine, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity event, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.octineLeggings)
			return "thebetweenlands:textures/armour/octine2.png";
		else
			return "thebetweenlands:textures/armour/octine1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == EnumMaterialsBL.OCTINE_INGOT.ordinal();
	}
	
}