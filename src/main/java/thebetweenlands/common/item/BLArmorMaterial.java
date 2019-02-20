package thebetweenlands.common.item;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.crafting.Ingredient;

public class BLArmorMaterial {
	public static final ArmorMaterial BL_CLOTH = ArmorMaterial.create("bl_cloth", "bl_cloth", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial LURKER_SKIN = ArmorMaterial.create("lurker_skin", "lurker_skin", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial BONE = ArmorMaterial.create("slimy_bone", "slimy_bone", 6, new int[]{1, 3, 5, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial SYRMORITE = ArmorMaterial.create("syrmorite", "syrmorite", 16, new int[]{2, 5, 6, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial VALONITE = ArmorMaterial.create("valonite", "valonite", 35, new int[]{3, 6, 8, 3}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial RUBBER = ArmorMaterial.create("rubber", "rubber", 10, new int[]{1, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0f, () -> Ingredient.EMPTY);
	public static final ArmorMaterial LEGEND = ArmorMaterial.create("legend", "legend", 66, new int[]{6, 12, 16, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f, () -> Ingredient.EMPTY);

	public static int getMinRepairFuelCost(ArmorMaterial material) {
		if(material == BL_CLOTH) {
			return 2;
		} else if(material == BONE) {
			return 3;
		} else if(material == RUBBER) {
			return 3;
		} else if(material == LURKER_SKIN) {
			return 4;
		} else if(material == SYRMORITE) {
			return 5;
		} else if(material == VALONITE) {
			return 6;
		} else if(material == LEGEND) {
			return 24;
		}
		return 4;
	}

	public static int getFullRepairFuelCost(ArmorMaterial material) {
		if(material == BL_CLOTH) {
			return 6;
		} else if(material == BONE) {
			return 8;
		} else if(material == RUBBER) {
			return 8;
		} else if(material == LURKER_SKIN) {
			return 10;
		} else if(material == SYRMORITE) {
			return 12;
		} else if(material == VALONITE) {
			return 16;
		} else if(material == LEGEND) {
			return 48;
		}
		return 8;
	}

	public static int getMinRepairLifeCost(ArmorMaterial material) {
		if(material == BL_CLOTH) {
			return 4;
		} else if(material == BONE) {
			return 4;
		} else if(material == RUBBER) {
			return 4;
		} else if(material == LURKER_SKIN) {
			return 4;
		} else if(material == SYRMORITE) {
			return 5;
		} else if(material == VALONITE) {
			return 12;
		} else if(material == LEGEND) {
			return 48;
		}
		return 4;
	}

	public static int getFullRepairLifeCost(ArmorMaterial material) {
		if(material == BL_CLOTH) {
			return 16;
		} else if(material == BONE) {
			return 16;
		} else if(material == RUBBER) {
			return 16;
		} else if(material == LURKER_SKIN) {
			return 16;
		} else if(material == SYRMORITE) {
			return 32;
		} else if(material == VALONITE) {
			return 48;
		} else if(material == LEGEND) {
			return 110;
		}
		return 8;
	}
}
