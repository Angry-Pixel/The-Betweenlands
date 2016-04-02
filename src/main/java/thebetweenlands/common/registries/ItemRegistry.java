package thebetweenlands.common.registries;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.tabs.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.BLMaterial;
import thebetweenlands.common.items.BasicItem;
import thebetweenlands.common.items.armor.ItemArmorBL;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.utils.TranslationHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final Item sapBall = new BasicItem().setUnlocalizedName(ModInfo.NAME_PREFIX + "sap_ball").setCreativeTab(BLCreativeTabs.items);
    //armor
    public static final Item syrmoriteHelmet = new ItemArmorBL(BLMaterial.armorSyrmorite, 0, EntityEquipmentSlot.HEAD, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteChestplate = new ItemArmorBL(BLMaterial.armorSyrmorite, 1, EntityEquipmentSlot.CHEST, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteLeggings = new ItemArmorBL(BLMaterial.armorSyrmorite, 2, EntityEquipmentSlot.LEGS, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteBoots = new ItemArmorBL(BLMaterial.armorSyrmorite, 3, EntityEquipmentSlot.FEET, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinHelmet = new ItemArmorBL(BLMaterial.armorLurkerSkin, 0, EntityEquipmentSlot.HEAD, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinChestplate = new ItemArmorBL(BLMaterial.armorLurkerSkin, 1, EntityEquipmentSlot.CHEST, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinLeggings = new ItemArmorBL(BLMaterial.armorLurkerSkin, 2, EntityEquipmentSlot.LEGS, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinBoots = new ItemArmorBL(BLMaterial.armorLurkerSkin, 3, EntityEquipmentSlot.FEET, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneHelmet = new ItemArmorBL(BLMaterial.armorBone, 0, EntityEquipmentSlot.HEAD, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneChestplate = new ItemArmorBL(BLMaterial.armorBone, 1, EntityEquipmentSlot.CHEST, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneLeggings = new ItemArmorBL(BLMaterial.armorBone, 2, EntityEquipmentSlot.LEGS, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneBoots = new ItemArmorBL(BLMaterial.armorBone, 3, EntityEquipmentSlot.FEET, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteHelmet = new ItemArmorBL(BLMaterial.armorValonite, 0, EntityEquipmentSlot.HEAD, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteChestplate = new ItemArmorBL(BLMaterial.armorValonite, 1, EntityEquipmentSlot.CHEST, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteLeggings = new ItemArmorBL(BLMaterial.armorValonite, 2, EntityEquipmentSlot.LEGS, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteBoots = new ItemArmorBL(BLMaterial.armorValonite, 3, EntityEquipmentSlot.FEET, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_boots").setCreativeTab(BLCreativeTabs.gears);
    public final List<Item> items = new ArrayList<Item>();

    public void preInit() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(Item.class)) {
                    Item item = (Item) field.get(this);
                    registerItem(item);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void init() {
        for (Item item : this.items) {
            TheBetweenlands.proxy.registerDefaultItemRenderer(item);
        }
    }


    private void registerItem(Item item) {
        String name = item.getUnlocalizedName();
        String itemName = name.substring(name.lastIndexOf(".") + 1, name.length());
        GameRegistry.registerItem(item, itemName);
        items.add(item);
        TranslationHelper.canTranslate(name + ".name");
    }

}
