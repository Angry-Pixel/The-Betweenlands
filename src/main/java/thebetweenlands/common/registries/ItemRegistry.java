package thebetweenlands.common.registries;

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
import thebetweenlands.util.TranslationHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ItemRegistry {
    //generic
    public final Item itemsGeneric = new ItemGeneric().setCreativeTab(BLCreativeTabs.items);
    public final Item itemsGenericCrushed = new ItemGenericCrushed().setCreativeTab(BLCreativeTabs.herbLore);
    public final Item itemsGenericPlantDrop = new ItemGenericPlantDrop().setCreativeTab(BLCreativeTabs.herbLore);
    public final Item swampTalisman = new ItemSwampTalisman().setCreativeTab(BLCreativeTabs.items);
    //food
    public final Item sapBall = new ItemSapBall();
    public final ItemRottenFood rottenFood = (ItemRottenFood) new ItemRottenFood().setAlwaysEdible();
    //public static final Item middleFruitSeeds = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.middleFruitBush, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".middleFruitSeeds");
    //public static final Item spores = new ItemBLGenericSeed(0, 0F, BLBlockRegistry.fungusCrop, BLBlockRegistry.farmedDirt).setUnlocalizedName(ModInfo.NAME_PREFIX + ".spores");
    //public static final Item aspectrusCropSeed = new ItemAspectrusCropSeed(0, 0F).setUnlocalizedName(ModInfo.NAME_PREFIX + ".aspectrusSeeds");
    public final Item anglerMeatRaw = new ItemBLFood(4, 0.4F, false, "anglerMeatRaw");
    public final Item anglerMeatCooked = new ItemBLFood(8, 0.8F, false, "anglerMeatCooked");
    public final Item frogLegsRaw = new ItemBLFood(3, 0.4F, false, "frogLegsRaw");
    public final Item frogLegsCooked = new ItemBLFood(6, 0.8F, false, "frogLegsCooked");
    public final Item snailFleshRaw = new ItemBLFood(3, 0.4F, false, "snailFleshRaw");
    public final Item snailFleshCooked = new ItemBLFood(6, 0.9F, false, "snailFleshCooked");
    public final Item reedDonut = new ItemBLFood(6, 0.6F, false, "reedDonut");
    public final Item jamDonut = new ItemBLFood(10, 0.6F, false, "jamDonut");
    public final Item gertsDonut = new ItemGertsDonut();
    public final Item krakenTentacle = new ItemBLFood(8, 0.9F, false, "krakenTentacle");
    public final Item krakenCalamari = new ItemBLFood(14, 1.2F, false, "krakenCalamari");
    public final Item middleFruit = new ItemBLFood(6, 0.6F, false, "middleFruit");
    public final Item mincePie = new ItemBLFood(4, 0.85F, false, "mincePie");
    public final Item weepingBluePetal = new ItemWeepingBluePetal();
    public final Item wightsHeart = new ItemWightHeart();
    public final Item yellowDottedFungus = new ItemBLFood(8, 0.6F, false, "yellowDottedFungus");
    public final Item siltCrabClaw = new ItemBLFood(2, 0.6F, false, "siltCrabClaw");
    public final Item crabStick = new ItemBLFood(6, 0.9F, false, "crabStick");
    public final Item nettleSoup = new ItemNettleSoup();
    public final Item sludgeJello = new ItemBLFood(4, 0.9F, false, "sludgeJello");
    public final Item middleFruitJello = new ItemBLFood(10, 1.0F, false, "middleFruitJello");
    public final Item sapJello = new ItemSapJello();
    public final Item marshmallow = new ItemMarshmallow();
    public final Item marshmallowPink = new ItemMarshmallowPink();
    public final Item flatheadMushroomItem = new ItemFlatheadMushroom();
    public final Item blackHatMushroomItem = new ItemBlackHatMushroom();
    public final Item bulbCappedMushroomItem = new ItemBulbCappedMushroom();
    public final Item friedSwampKelp = new ItemBLFood(5, 0.6F, false, "friedSwampKelp");
    public final Item forbiddenFig = new ItemForbiddenFig();
    public final Item candyBlue = new ItemBLFood(3, 1.0F, false, "candyBlue");
    public final Item candyRed = new ItemBLFood(3, 1.0F, false, "candyRed");
    public final Item candyYellow = new ItemBLFood(3, 1.0F, false, "candyYellow");
    public final Item chiromawWing = new ItemChiromawWing();
    //armor
    public final Item syrmoriteHelmet = new ItemBLArmor(BLMaterial.armorSyrmorite, 0, EntityEquipmentSlot.HEAD, "syrmorite").setCreativeTab(BLCreativeTabs.gears);
    public final Item syrmoriteChestplate = new ItemBLArmor(BLMaterial.armorSyrmorite, 1, EntityEquipmentSlot.CHEST, "syrmorite").setCreativeTab(BLCreativeTabs.gears);
    public final Item syrmoriteLeggings = new ItemBLArmor(BLMaterial.armorSyrmorite, 2, EntityEquipmentSlot.LEGS, "syrmorite").setCreativeTab(BLCreativeTabs.gears);
    public final Item syrmoriteBoots = new ItemBLArmor(BLMaterial.armorSyrmorite, 3, EntityEquipmentSlot.FEET, "syrmorite").setCreativeTab(BLCreativeTabs.gears);
    public final Item lurkerSkinHelmet = new ItemBLArmor(BLMaterial.armorLurkerSkin, 0, EntityEquipmentSlot.HEAD, "lurkerSkin").setCreativeTab(BLCreativeTabs.gears);
    public final Item lurkerSkinChestplate = new ItemBLArmor(BLMaterial.armorLurkerSkin, 1, EntityEquipmentSlot.CHEST, "lurkerSkin").setCreativeTab(BLCreativeTabs.gears);
    public final Item lurkerSkinLeggings = new ItemBLArmor(BLMaterial.armorLurkerSkin, 2, EntityEquipmentSlot.LEGS, "lurkerSkin").setCreativeTab(BLCreativeTabs.gears);
    public final Item lurkerSkinBoots = new ItemBLArmor(BLMaterial.armorLurkerSkin, 3, EntityEquipmentSlot.FEET, "lurkerSkin").setCreativeTab(BLCreativeTabs.gears);
    public final Item boneHelmet = new ItemBLArmor(BLMaterial.armorBone, 0, EntityEquipmentSlot.HEAD, "bone").setCreativeTab(BLCreativeTabs.gears);
    public final Item boneChestplate = new ItemBLArmor(BLMaterial.armorBone, 1, EntityEquipmentSlot.CHEST, "bone").setCreativeTab(BLCreativeTabs.gears);
    public final Item boneLeggings = new ItemBLArmor(BLMaterial.armorBone, 2, EntityEquipmentSlot.LEGS, "bone").setCreativeTab(BLCreativeTabs.gears);
    public final Item boneBoots = new ItemBLArmor(BLMaterial.armorBone, 3, EntityEquipmentSlot.FEET, "bone").setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteHelmet = new ItemBLArmor(BLMaterial.armorValonite, 0, EntityEquipmentSlot.HEAD, "valonite").setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteChestplate = new ItemBLArmor(BLMaterial.armorValonite, 1, EntityEquipmentSlot.CHEST, "valonite").setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteLeggings = new ItemBLArmor(BLMaterial.armorValonite, 2, EntityEquipmentSlot.LEGS, "valonite").setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteBoots = new ItemBLArmor(BLMaterial.armorValonite, 3, EntityEquipmentSlot.FEET, "valonite").setCreativeTab(BLCreativeTabs.gears);
    //tools
    public final Item weedwoodSword = new ItemBLSword(BLMaterial.toolWeedWood).setCreativeTab(BLCreativeTabs.gears);
    public final Item weedwoodShovel = new ItemBLShovel(BLMaterial.toolWeedWood).setCreativeTab(BLCreativeTabs.gears);
    //public static final Item weedwoodAxe = new ItemBLAxe(BLMaterial.toolWeedWood).setUnlocalizedName(ModInfo.NAME_PREFIX + "weedwood_axe").setCreativeTab(BLCreativeTabs.gears);
    public final Item weedwoodPickaxe = new ItemBLPickaxe(BLMaterial.toolWeedWood).setCreativeTab(BLCreativeTabs.gears);
    public final Item betweenstoneSword = new ItemBLSword(BLMaterial.toolBetweenstone).setCreativeTab(BLCreativeTabs.gears);
    public final Item betweenstoneShovel = new ItemBLShovel(BLMaterial.toolBetweenstone).setCreativeTab(BLCreativeTabs.gears);
    //public static final Item betweenstoneAxe = new ItemBLAxe(BLMaterial.toolBetweenstone).setUnlocalizedName(ModInfo.NAME_PREFIX + "betweenstone_axe").setCreativeTab(BLCreativeTabs.gears);
    public final Item betweenstonePickaxe = new ItemBLPickaxe(BLMaterial.toolBetweenstone).setCreativeTab(BLCreativeTabs.gears);
    public final Item octineSword = new ItemBLSword(BLMaterial.toolOctine).setCreativeTab(BLCreativeTabs.gears);
    public final Item octineShovel = new ItemBLShovel(BLMaterial.toolOctine).setCreativeTab(BLCreativeTabs.gears);
    //public static final Item octineAxe = new ItemBLAxe(BLMaterial.toolOctine).setUnlocalizedName(ModInfo.NAME_PREFIX + "octine_axe").setCreativeTab(BLCreativeTabs.gears);
    public final Item octinePickaxe = new ItemBLPickaxe(BLMaterial.toolOctine).setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteSword = new ItemBLSword(BLMaterial.toolValonite).setCreativeTab(BLCreativeTabs.gears);
    public final Item valoniteShovel = new ItemBLShovel(BLMaterial.toolValonite).setCreativeTab(BLCreativeTabs.gears);
    //public static final Item valoniteAxe = new ItemBLAxe(BLMaterial.toolValonite).setUnlocalizedName(ModInfo.NAME_PREFIX + "valonite_axe").setCreativeTab(BLCreativeTabs.gears);
    public final Item valonitePickaxe = new ItemBLPickaxe(BLMaterial.toolValonite).setCreativeTab(BLCreativeTabs.gears);

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
        GameRegistry.register(item);
        items.add(item);
        String name = item.getUnlocalizedName();
        TranslationHelper.canTranslate(name + ".name");
    }

}
