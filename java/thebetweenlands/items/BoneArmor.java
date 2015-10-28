package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;
import thebetweenlands.recipes.BLMaterials;

public class BoneArmor extends ItemArmor {

	public BoneArmor(int armorType) {
		super(BLMaterials.armorBone, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.boneLeggings)
			return "thebetweenlands:textures/armour/bone2.png";
		else
			return "thebetweenlands:textures/armour/bone1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.SLIMY_BONE.ordinal();
	}
}