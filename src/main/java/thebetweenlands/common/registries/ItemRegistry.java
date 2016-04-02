package thebetweenlands.common.registries;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.BasicItem;
import thebetweenlands.common.item.armor.ItemBLArmor;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.TranslationHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    public static final Item sapBall = new BasicItem().setUnlocalizedName(ModInfo.NAME_PREFIX + "sap_ball").setCreativeTab(BLCreativeTabs.items);
    //armor
    public static final Item syrmoriteHelmet = new ItemBLArmor(BLMaterial.armorSyrmorite, 0, EntityEquipmentSlot.HEAD, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteChestplate = new ItemBLArmor(BLMaterial.armorSyrmorite, 1, EntityEquipmentSlot.CHEST, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteLeggings = new ItemBLArmor(BLMaterial.armorSyrmorite, 2, EntityEquipmentSlot.LEGS, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item syrmoriteBoots = new ItemBLArmor(BLMaterial.armorSyrmorite, 3, EntityEquipmentSlot.FEET, "syrmorite").setUnlocalizedName(ModInfo.NAME_PREFIX + "syrmorite_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinHelmet = new ItemBLArmor(BLMaterial.armorLurkerSkin, 0, EntityEquipmentSlot.HEAD, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinChestplate = new ItemBLArmor(BLMaterial.armorLurkerSkin, 1, EntityEquipmentSlot.CHEST, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinLeggings = new ItemBLArmor(BLMaterial.armorLurkerSkin, 2, EntityEquipmentSlot.LEGS, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item lurkerSkinBoots = new ItemBLArmor(BLMaterial.armorLurkerSkin, 3, EntityEquipmentSlot.FEET, "lurker_skin").setUnlocalizedName(ModInfo.NAME_PREFIX + "lurker_skin_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneHelmet = new ItemBLArmor(BLMaterial.armorBone, 0, EntityEquipmentSlot.HEAD, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneChestplate = new ItemBLArmor(BLMaterial.armorBone, 1, EntityEquipmentSlot.CHEST, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneLeggings = new ItemBLArmor(BLMaterial.armorBone, 2, EntityEquipmentSlot.LEGS, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item boneBoots = new ItemBLArmor(BLMaterial.armorBone, 3, EntityEquipmentSlot.FEET, "bone").setUnlocalizedName(ModInfo.NAME_PREFIX + "bone_boots").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteHelmet = new ItemBLArmor(BLMaterial.armorValonite, 0, EntityEquipmentSlot.HEAD, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_helmet").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteChestplate = new ItemBLArmor(BLMaterial.armorValonite, 1, EntityEquipmentSlot.CHEST, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_chestplate").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteLeggings = new ItemBLArmor(BLMaterial.armorValonite, 2, EntityEquipmentSlot.LEGS, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_leggings").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteBoots = new ItemBLArmor(BLMaterial.armorValonite, 3, EntityEquipmentSlot.FEET, "valonite").setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_boots").setCreativeTab(BLCreativeTabs.gears);
    //tools
    public static final Item weedwoodSword = new ItemBLSword(BLMaterial.toolWeedWood).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_sword").setCreativeTab(BLCreativeTabs.gears);
    public static final Item weedwoodShovel = new ItemBLShovel(BLMaterial.toolWeedWood).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_shovel").setCreativeTab(BLCreativeTabs.gears);
    //public static final Item weedwoodAxe = new ItemBLAxe(BLMaterial.toolWeedWood).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_axe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item weedwoodPickaxe = new ItemBLPickaxe(BLMaterial.toolWeedWood).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_pickaxe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item betweenstoneSword = new ItemBLSword(BLMaterial.toolBetweenstone).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_sword").setCreativeTab(BLCreativeTabs.gears);
    public static final Item betweenstoneShovel = new ItemBLShovel(BLMaterial.toolBetweenstone).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_shovel").setCreativeTab(BLCreativeTabs.gears);
    //public static final Item betweenstoneAxe = new ItemBLAxe(BLMaterial.toolBetweenstone).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_axe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item betweenstonePickaxe = new ItemBLPickaxe(BLMaterial.toolBetweenstone).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_pickaxe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item octineSword = new ItemBLSword(BLMaterial.toolOctine).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_sword").setCreativeTab(BLCreativeTabs.gears);
    public static final Item octineShovel = new ItemBLShovel(BLMaterial.toolOctine).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_shovel").setCreativeTab(BLCreativeTabs.gears);
    //public static final Item octineAxe = new ItemBLAxe(BLMaterial.toolOctine).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_axe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item octinePickaxe = new ItemBLPickaxe(BLMaterial.toolOctine).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_pickaxe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteSword = new ItemBLSword(BLMaterial.toolValonite).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_sword").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valoniteShovel = new ItemBLShovel(BLMaterial.toolValonite).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_shovel").setCreativeTab(BLCreativeTabs.gears);
    //public static final Item valoniteAxe = new ItemBLAxe(BLMaterial.toolValonite).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_axe").setCreativeTab(BLCreativeTabs.gears);
    public static final Item valonitePickaxe = new ItemBLPickaxe(BLMaterial.toolValonite).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_pickaxe").setCreativeTab(BLCreativeTabs.gears);
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
