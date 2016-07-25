package thebetweenlands.common.item;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterialRegistry {
    public static final ToolMaterial TOOL_WEEDWOOD = EnumHelper.addToolMaterial("weedwood", 0, 80, 2.0F, 0.0F, 15);
    public static final ToolMaterial TOOL_BONE = EnumHelper.addToolMaterial("bone", 1, 320, 4.0F, 1.0F, 5);
    public static final ToolMaterial TOOL_OCTINE = EnumHelper.addToolMaterial("octane", 2, 900, 6.0F, 2.0F, 14);
    public static final ToolMaterial TOOL_VALONITE = EnumHelper.addToolMaterial("valonite", 3, 2500, 8.0F, 3.0F, 10);
    //TWEAK values
    public static final ToolMaterial TOOL_SYRMORITE = EnumHelper.addToolMaterial("syrmorite", 3, 2500, 8.0F, 3.0F, 10);
    public static final ToolMaterial TOOL_LOOT = EnumHelper.addToolMaterial("loot", 2, 7500, 2.0F, 0.5F, 5);
    public static final ToolMaterial TOOL_LEGEND = EnumHelper.addToolMaterial("legend", 6, 10000, 16.0F, 6.0F, 20);

    //TODO add armor equip sounds maybe and fix the thoughness values
    public static final ArmorMaterial ARMOR_BL_CLOTH = EnumHelper.addArmorMaterial("bl_cloth", "bl_cloth", 12, new int[]{1, 3, 2, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0f);
    public static final ArmorMaterial ARMOR_LURKER_SKIN = EnumHelper.addArmorMaterial("lurker_skin", "lurker_skin", 12, new int[]{1, 3, 2, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0f);
    public static final ArmorMaterial ARMOR_BONE = EnumHelper.addArmorMaterial("slimy_bone", "slimy_bone", 6, new int[]{2, 5, 3, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1.0f);
    public static final ArmorMaterial ARMOR_SYRMORITE = EnumHelper.addArmorMaterial("syrmorite", "syrmorite", 16, new int[]{2, 6, 5, 2}, 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 1.0f);
    public static final ArmorMaterial ARMOR_VALONITE = EnumHelper.addArmorMaterial("valonite", "valonite", 35, new int[]{3, 8, 6, 3}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f);
    public static final ArmorMaterial ARMOR_RUBBER = EnumHelper.addArmorMaterial("rubber", "rubber", 10, new int[]{0, 0, 0, 1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 1.0f);
    public static final ArmorMaterial ARMOR_LEGEND = EnumHelper.addArmorMaterial("legend", "legend", 66, new int[]{6, 16, 12, 6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, 1.0f);

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



}
