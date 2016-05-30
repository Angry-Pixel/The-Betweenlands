package thebetweenlands.common.registries;

import com.google.common.base.CaseFormat;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.BLMaterial;
import thebetweenlands.common.item.armor.ItemBLArmor;
import thebetweenlands.common.item.food.*;
import thebetweenlands.common.item.herblore.ItemGenericCrushed;
import thebetweenlands.common.item.herblore.ItemGenericPlantDrop;
import thebetweenlands.common.item.misc.ItemGeneric;
import thebetweenlands.common.item.misc.ItemSwampTalisman;
import thebetweenlands.common.item.tools.ItemBLPickaxe;
import thebetweenlands.common.item.tools.ItemBLShovel;
import thebetweenlands.common.item.tools.ItemBLSword;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.TranslationHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    //generic
    public final Item itemsGeneric = new ItemGeneric().setCreativeTab(BLCreativeTabs.ITEMS);
    public final Item itemsGenericCrushed = new ItemGenericCrushed().setCreativeTab(BLCreativeTabs.HERBLORE);
    public final Item itemsGenericPlantDrop = new ItemGenericPlantDrop().setCreativeTab(BLCreativeTabs.HERBLORE);
    public final Item swampTalisman = new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.ITEMS);
    //food
    public final Item sapBall = new ItemSapBall();
    public final ItemRottenFood rottenFood = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
    //public static final Item middleFruitSeeds = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.middleFruitBush, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".middleFruitSeeds");
    //public static final Item spores = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.fungusCrop, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".spores");
    //public static final Item aspectrusCropSeed = new ItemAspectrusCropSeed(0, 0F).setUnlocalizedName(ModInfo.NAME_PREFIX + ".aspectrusSeeds");
    public final Item anglerMeatRaw = new ItemBLFood(4, 0.4F, false);
    public final Item anglerMeatCooked = new ItemBLFood(8, 0.8F, false);
    public final Item frogLegsRaw = new ItemBLFood(3, 0.4F, false);
    public final Item frogLegsCooked = new ItemBLFood(6, 0.8F, false);
    public final Item snailFleshRaw = new ItemBLFood(3, 0.4F, false);
    public final Item snailFleshCooked = new ItemBLFood(6, 0.9F, false);
    public final Item reedDonut = new ItemBLFood(6, 0.6F, false);
    public final Item jamDonut = new ItemBLFood(10, 0.6F, false);
    public final Item gertsDonut = new ItemGertsDonut();
    public final Item krakenTentacle = new ItemBLFood(8, 0.9F, false);
    public final Item krakenCalamari = new ItemBLFood(14, 1.2F, false);
    public final Item middleFruit = new ItemBLFood(6, 0.6F, false);
    public final Item mincePie = new ItemBLFood(4, 0.85F, false);
    public final Item weepingBluePetal = new ItemWeepingBluePetal();
    public final Item wightsHeart = new ItemWightHeart();
    public final Item yellowDottedFungus = new ItemBLFood(8, 0.6F, false);
    public final Item siltCrabClaw = new ItemBLFood(2, 0.6F, false);
    public final Item crabStick = new ItemBLFood(6, 0.9F, false);
    public final Item nettleSoup = new ItemNettleSoup();
    public final Item sludgeJello = new ItemBLFood(4, 0.9F, false);
    public final Item middleFruitJello = new ItemBLFood(10, 1.0F, false);
    public final Item sapJello = new ItemSapJello();
    public final Item marshmallow = new ItemMarshmallow();
    public final Item marshmallowPink = new ItemMarshmallowPink();
    public final Item flatheadMushroomItem = new ItemFlatheadMushroom();
    public final Item blackHatMushroomItem = new ItemBlackHatMushroom();
    public final Item bulbCappedMushroomItem = new ItemBulbCappedMushroom();
    public final Item friedSwampKelp = new ItemBLFood(5, 0.6F, false);
    public final Item forbiddenFig = new ItemForbiddenFig();
    public final Item candyBlue = new ItemBLFood(3, 1.0F, false);
    public final Item candyRed = new ItemBLFood(3, 1.0F, false);
    public final Item candyYellow = new ItemBLFood(3, 1.0F, false);
    public final Item chiromawWing = new ItemChiromawWing();
    //armor
    public final Item syrmoriteHelmet = new ItemBLArmor(BLMaterial.ARMOR_SYRMORITE, 0, EntityEquipmentSlot.HEAD, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item syrmoriteChestplate = new ItemBLArmor(BLMaterial.ARMOR_SYRMORITE, 1, EntityEquipmentSlot.CHEST, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item syrmoriteLeggings = new ItemBLArmor(BLMaterial.ARMOR_SYRMORITE, 2, EntityEquipmentSlot.LEGS, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item syrmoriteBoots = new ItemBLArmor(BLMaterial.ARMOR_SYRMORITE, 3, EntityEquipmentSlot.FEET, "syrmorite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item lurkerSkinHelmet = new ItemBLArmor(BLMaterial.ARMOR_LURKER_SKIN, 0, EntityEquipmentSlot.HEAD, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item lurkerSkinChestplate = new ItemBLArmor(BLMaterial.ARMOR_LURKER_SKIN, 1, EntityEquipmentSlot.CHEST, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item lurkerSkinLeggings = new ItemBLArmor(BLMaterial.ARMOR_LURKER_SKIN, 2, EntityEquipmentSlot.LEGS, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item lurkerSkinBoots = new ItemBLArmor(BLMaterial.ARMOR_LURKER_SKIN, 3, EntityEquipmentSlot.FEET, "lurkerSkin").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item boneHelmet = new ItemBLArmor(BLMaterial.ARMOR_BONE, 0, EntityEquipmentSlot.HEAD, "bone").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item boneChestplate = new ItemBLArmor(BLMaterial.ARMOR_BONE, 1, EntityEquipmentSlot.CHEST, "bone").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item boneLeggings = new ItemBLArmor(BLMaterial.ARMOR_BONE, 2, EntityEquipmentSlot.LEGS, "bone").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item boneBoots = new ItemBLArmor(BLMaterial.ARMOR_BONE, 3, EntityEquipmentSlot.FEET, "bone").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteHelmet = new ItemBLArmor(BLMaterial.ARMOR_VALONITE, 0, EntityEquipmentSlot.HEAD, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteChestplate = new ItemBLArmor(BLMaterial.ARMOR_VALONITE, 1, EntityEquipmentSlot.CHEST, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteLeggings = new ItemBLArmor(BLMaterial.ARMOR_VALONITE, 2, EntityEquipmentSlot.LEGS, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteBoots = new ItemBLArmor(BLMaterial.ARMOR_VALONITE, 3, EntityEquipmentSlot.FEET, "valonite").setCreativeTab(BLCreativeTabs.GEARS);
    //tools
    public final Item weedwoodSword = new ItemBLSword(BLMaterial.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item weedwoodShovel = new ItemBLShovel(BLMaterial.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    //public static final Item weedwoodAxe = new ItemBLAxe(BLMaterial.TOOL_WEEDWOOD).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_axe").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item weedwoodPickaxe = new ItemBLPickaxe(BLMaterial.TOOL_WEEDWOOD).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item betweenstoneSword = new ItemBLSword(BLMaterial.TOOL_BETWEENSTONE).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item betweenstoneShovel = new ItemBLShovel(BLMaterial.TOOL_BETWEENSTONE).setCreativeTab(BLCreativeTabs.GEARS);
    //public static final Item betweenstoneAxe = new ItemBLAxe(BLMaterial.TOOL_BETWEENSTONE).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_axe").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item betweenstonePickaxe = new ItemBLPickaxe(BLMaterial.TOOL_BETWEENSTONE).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item octineSword = new ItemBLSword(BLMaterial.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item octineShovel = new ItemBLShovel(BLMaterial.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
    //public static final Item octineAxe = new ItemBLAxe(BLMaterial.TOOL_OCTINE).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_axe").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item octinePickaxe = new ItemBLPickaxe(BLMaterial.TOOL_OCTINE).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteSword = new ItemBLSword(BLMaterial.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valoniteShovel = new ItemBLShovel(BLMaterial.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);
    //public static final Item valoniteAxe = new ItemBLAxe(BLMaterial.TOOL_VALONITE).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_axe").setCreativeTab(BLCreativeTabs.GEARS);
    public final Item valonitePickaxe = new ItemBLPickaxe(BLMaterial.TOOL_VALONITE).setCreativeTab(BLCreativeTabs.GEARS);

    public final List<Item> items = new ArrayList<Item>();

    public void preInit() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getType().isAssignableFrom(Item.class)) {
                    Item item = (Item) field.get(this);
                    registerItem(item, field.getName());
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


    private void registerItem(Item item, String fieldName) {
        String itemName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, fieldName);
        GameRegistry.register(item.setRegistryName(ModInfo.ID, itemName).setUnlocalizedName(ModInfo.NAME_PREFIX + itemName));
        items.add(item);
        String name = item.getUnlocalizedName();
        TranslationHelper.canTranslate(name + ".name");
    }

    public interface ISubItemsItem {
        List<String> getModels();
    }

    public interface ISingleJsonSubItems{
        List<String> getTypes();
    }
}
