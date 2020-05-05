package thebetweenlands.common.item;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterialRegistry {
	public static final ToolMaterial TOOL_WEEDWOOD = EnumHelper.addToolMaterial("weedwood", 0, 80, 2.0F, 0.0F, 0);
	public static final ToolMaterial TOOL_BONE = EnumHelper.addToolMaterial("bone", 1, 320, 4.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_LURKER_SKIN = EnumHelper.addToolMaterial("bone", 1, 600, 5.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_DENTROTHYST = EnumHelper.addToolMaterial("dentrothyst", 1, 600, 7.0F, 1.0F, 0);
	public static final ToolMaterial TOOL_OCTINE = EnumHelper.addToolMaterial("octine", 2, 900, 6.0F, 2.0F, 0);
	public static final ToolMaterial TOOL_SYRMORITE = EnumHelper.addToolMaterial("syrmorite", 2, 900, 6.0F, 2.0F, 0);
	public static final ToolMaterial TOOL_VALONITE = EnumHelper.addToolMaterial("valonite", 3, 2500, 8.0F, 3.0F, 0);
	public static final ToolMaterial TOOL_LOOT = EnumHelper.addToolMaterial("loot", 3, 7500, 2.0F, 0.5F, 0);
	public static final ToolMaterial TOOL_LEGEND = EnumHelper.addToolMaterial("legend", 6, 10000, 16.0F, 6.0F, 0);

	//TODO add armor equip sounds maybe
	public static final ArmorMaterial ARMOR_BL_CLOTH = EnumHelper.addArmorMaterial("bl_cloth", "bl_cloth", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);
	public static final ArmorMaterial ARMOR_LURKER_SKIN = EnumHelper.addArmorMaterial("lurker_skin", "lurker_skin", 12, new int[]{1, 2, 3, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0f);
	public static final ArmorMaterial ARMOR_BONE = EnumHelper.addArmorMaterial("slimy_bone", "slimy_bone", 6, new int[]{1, 3, 5, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 0.0f);
	public static final ArmorMaterial ARMOR_SYRMORITE = EnumHelper.addArmorMaterial("syrmorite", "syrmorite", 16, new int[]{2, 5, 6, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0f);
	public static final ArmorMaterial ARMOR_VALONITE = EnumHelper.addArmorMaterial("valonite", "valonite", 35, new int[]{3, 6, 8, 3}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f);
	public static final ArmorMaterial ARMOR_RUBBER = EnumHelper.addArmorMaterial("rubber", "rubber", 10, new int[]{1, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0f);
	public static final ArmorMaterial ARMOR_LEGEND = EnumHelper.addArmorMaterial("legend", "legend", 66, new int[]{6, 12, 16, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f);
	public static final ArmorMaterial ARMOR_ANCIENT = EnumHelper.addArmorMaterial("ancient", "ancient", 66, new int[]{6, 12, 16, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 2.0f);

	public static final Material MUD = new Material(MapColor.DIRT) {
		@Override
		public boolean isOpaque() {
			return false;
		}
	};
	public static final Material WISP = new BLMaterial(MapColor.AIR){

		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public boolean  blocksLight() {
			return false;
		}

		@Override
		public boolean blocksMovement() {
			return false;
		}
	}.setReplaceable().setTranslucent().setNoPushMobility();
	public static final Material TAR = new MaterialLiquid(MapColor.BLACK);
	public static final Material RUBBER = new MaterialLiquid(MapColor.WATER);
	public static final Material SLUDGE = new BLMaterial(MapColor.DIRT).setRequiresTool();

	public static int getMinRepairFuelCost(ToolMaterial material) {
		if(material == TOOL_WEEDWOOD) {
			return 2;
		} else if(material == TOOL_BONE) {
			return 3;
		} else if(material == TOOL_LURKER_SKIN) {
			return 4;
		} else if(material == TOOL_DENTROTHYST) {
			return 5;
		} else if(material == TOOL_OCTINE) {
			return 5;
		} else if(material == TOOL_SYRMORITE) {
			return 5;
		} else if(material == TOOL_VALONITE) {
			return 6;
		} else if(material == TOOL_LOOT) {
			return 16;
		} else if(material == TOOL_LEGEND) {
			return 24;
		}
		return 4;
	}

	public static int getFullRepairFuelCost(ToolMaterial material) {
		if(material == TOOL_WEEDWOOD) {
			return 6;
		} else if(material == TOOL_BONE) {
			return 8;
		} else if(material == TOOL_LURKER_SKIN) {
			return 10;
		} else if(material == TOOL_DENTROTHYST) {
			return 10;
		} else if(material == TOOL_OCTINE) {
			return 12;
		} else if(material == TOOL_SYRMORITE) {
			return 12;
		} else if(material == TOOL_VALONITE) {
			return 16;
		} else if(material == TOOL_LOOT) {
			return 32;
		} else if(material == TOOL_LEGEND) {
			return 48;
		}
		return 8;
	}

	public static int getMinRepairLifeCost(ToolMaterial material) {
		if(material == TOOL_WEEDWOOD) {
			return 4;
		} else if(material == TOOL_BONE) {
			return 4;
		} else if(material == TOOL_LURKER_SKIN) {
			return 4;
		} else if(material == TOOL_DENTROTHYST) {
			return 4;
		} else if(material == TOOL_OCTINE) {
			return 5;
		} else if(material == TOOL_SYRMORITE) {
			return 5;
		} else if(material == TOOL_VALONITE) {
			return 12;
		} else if(material == TOOL_LOOT) {
			return 32;
		} else if(material == TOOL_LEGEND) {
			return 48;
		}
		return 4;
	}

	public static int getFullRepairLifeCost(ToolMaterial material) {
		if(material == TOOL_WEEDWOOD) {
			return 16;
		} else if(material == TOOL_BONE) {
			return 16;
		} else if(material == TOOL_LURKER_SKIN) {
			return 16;
		} else if(material == TOOL_DENTROTHYST) {
			return 16;
		} else if(material == TOOL_OCTINE) {
			return 32;
		} else if(material == TOOL_SYRMORITE) {
			return 32;
		} else if(material == TOOL_VALONITE) {
			return 48;
		} else if(material == TOOL_LOOT) {
			return 64;
		} else if(material == TOOL_LEGEND) {
			return 110;
		}
		return 8;
	}

	public static int getMinRepairFuelCost(ArmorMaterial material) {
		if(material == ARMOR_BL_CLOTH) {
			return 2;
		} else if(material == ARMOR_BONE) {
			return 3;
		} else if(material == ARMOR_RUBBER) {
			return 3;
		} else if(material == ARMOR_LURKER_SKIN) {
			return 4;
		} else if(material == ARMOR_SYRMORITE) {
			return 5;
		} else if(material == ARMOR_VALONITE) {
			return 6;
		} else if(material == ARMOR_LEGEND) {
			return 24;
		}
		return 4;
	}

	public static int getFullRepairFuelCost(ArmorMaterial material) {
		if(material == ARMOR_BL_CLOTH) {
			return 6;
		} else if(material == ARMOR_BONE) {
			return 8;
		} else if(material == ARMOR_RUBBER) {
			return 8;
		} else if(material == ARMOR_LURKER_SKIN) {
			return 10;
		} else if(material == ARMOR_SYRMORITE) {
			return 12;
		} else if(material == ARMOR_VALONITE) {
			return 16;
		} else if(material == ARMOR_LEGEND) {
			return 48;
		}
		return 8;
	}

	public static int getMinRepairLifeCost(ArmorMaterial material) {
		if(material == ARMOR_BL_CLOTH) {
			return 4;
		} else if(material == ARMOR_BONE) {
			return 4;
		} else if(material == ARMOR_RUBBER) {
			return 4;
		} else if(material == ARMOR_LURKER_SKIN) {
			return 4;
		} else if(material == ARMOR_SYRMORITE) {
			return 5;
		} else if(material == ARMOR_VALONITE) {
			return 12;
		} else if(material == ARMOR_LEGEND) {
			return 48;
		}
		return 4;
	}

	public static int getFullRepairLifeCost(ArmorMaterial material) {
		if(material == ARMOR_BL_CLOTH) {
			return 16;
		} else if(material == ARMOR_BONE) {
			return 16;
		} else if(material == ARMOR_RUBBER) {
			return 16;
		} else if(material == ARMOR_LURKER_SKIN) {
			return 16;
		} else if(material == ARMOR_SYRMORITE) {
			return 32;
		} else if(material == ARMOR_VALONITE) {
			return 48;
		} else if(material == ARMOR_LEGEND) {
			return 110;
		}
		return 8;
	}
}
