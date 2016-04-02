package thebetweenlands.common.item;

import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterial {
    public static final ToolMaterial toolWeedWood = EnumHelper.addToolMaterial("weedwood", 0, 80, 2.0F, 0.0F, 15);
    public static final ToolMaterial toolBetweenstone = EnumHelper.addToolMaterial("betweenstone", 1, 320, 4.0F, 1.0F, 5);
    public static final ToolMaterial toolOctine = EnumHelper.addToolMaterial("octane", 2, 900, 6.0F, 2.0F, 14);
    public static final ToolMaterial toolValonite = EnumHelper.addToolMaterial("valonite", 3, 2500, 8.0F, 3.0F, 10);
    public static final ToolMaterial toolLoot = EnumHelper.addToolMaterial("loot", 2, 7500, 2.0F, 0.5F, 5);
    public static final ToolMaterial toolOfLegends = EnumHelper.addToolMaterial("legend", 6, 10000, 16.0F, 6.0F, 20);

    //TODO add armor equip sounds maybe?
    public static final ArmorMaterial armorBLCloth = EnumHelper.addArmorMaterial("bl_cloth", "bl_cloth", 12, new int[]{1, 3, 2, 1}, 0, SoundEvents.item_armor_equip_leather);
    public static final ArmorMaterial armorLurkerSkin = EnumHelper.addArmorMaterial("lurker_skin", "lurker_skin", 12, new int[]{1, 3, 2, 1}, 0, SoundEvents.item_armor_equip_leather);
    public static final ArmorMaterial armorBone = EnumHelper.addArmorMaterial("slimy_bone", "slimy_bone", 6, new int[]{2, 5, 3, 1}, 0, SoundEvents.item_armor_equip_chain);
    public static final ArmorMaterial armorSyrmorite = EnumHelper.addArmorMaterial("syrmorite", "syrmorite", 16, new int[]{2, 6, 5, 2}, 0, SoundEvents.item_armor_equip_iron);
    public static final ArmorMaterial armorValonite = EnumHelper.addArmorMaterial("valonite", "valonite", 35, new int[]{3, 8, 6, 3}, 0, SoundEvents.item_armor_equip_diamond);
    public static final ArmorMaterial armorRubber = EnumHelper.addArmorMaterial("rubber", "rubber", 10, new int[]{0, 0, 0, 1}, 0, SoundEvents.item_armor_equip_generic);
    public static final ArmorMaterial armorOfLegends = EnumHelper.addArmorMaterial("legend", "legend", 66, new int[]{6, 16, 12, 6}, 0, SoundEvents.item_armor_equip_diamond);

}
