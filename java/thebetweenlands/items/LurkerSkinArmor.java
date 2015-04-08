package thebetweenlands.items;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import thebetweenlands.recipes.BLMaterials;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LurkerSkinArmor extends ItemArmor {

	public LurkerSkinArmor(int armorType) {
		super(BLMaterials.armorLurkerSkin, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.lurkerSkinLeggings)
			return "thebetweenlands:textures/armour/lurker2.png";
		else
			return "thebetweenlands:textures/armour/lurker1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.materialsBL && material.getItemDamage() == EnumMaterialsBL.LURKER_SKIN.ordinal();
	}
}