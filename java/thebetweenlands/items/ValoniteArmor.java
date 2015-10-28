package thebetweenlands.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;
import thebetweenlands.recipes.BLMaterials;

public class ValoniteArmor extends ItemArmor {

	public ValoniteArmor(int armorType) {
		super(BLMaterials.armorValonite, 2, armorType);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		if (stack.getItem() == BLItemRegistry.valoniteLeggings)
			return "thebetweenlands:textures/armour/valonite2.png";
		else
			return "thebetweenlands:textures/armour/valonite1.png";
	}

	@Override
	public boolean getIsRepairable(ItemStack armour, ItemStack material) {
		return material.getItem() == BLItemRegistry.itemsGeneric && material.getItemDamage() == EnumItemGeneric.VALONITE_SHARD.ordinal();
	}
}