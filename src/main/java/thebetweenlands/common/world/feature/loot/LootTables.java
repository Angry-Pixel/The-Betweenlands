package thebetweenlands.common.world.feature.loot;

import net.minecraft.item.ItemStack;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Random;

public class LootTables {

    public static final WeightedLootList COMMON_POT_LOOT = new WeightedLootList(new LootItemStack[] {
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SWAMP_REED_ROPE.getID()).setAmount(2, 6).setWeight(45),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SULFUR.getID()).setAmount(2, 10).setWeight(40),
            new LootItemStack(ItemRegistry.SAP_BALL).setAmount(2, 6).setWeight(26),

            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.LURKER_SKIN.getID()).setAmount(1, 4).setWeight(12),
            //new LootItemStack(ItemRegistry.anglerToothArrow).setAmount(6, 16).setWeight(12),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SYRMORITE_INGOT.getID()).setAmount(2, 8).setWeight(11),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.OCTINE_INGOT.getID()).setAmount(2, 8).setWeight(11),
            //new LootItemStack(ItemRegistry.scroll).setAmount(1).setWeight(8),
            //new LootItemStack(ItemRegistry.poisonedAnglerToothArrow).setAmount(6, 14).setWeight(7),
            //new LootItemStack(ItemRegistry.octineArrow).setAmount(6, 16).setWeight(7),
            new LootItemStack(ItemRegistry.WIGHT_HEART).setAmount(1, 3).setWeight(6),
            //new LootItemStack(ItemRegistry.basiliskArrow).setAmount(8).setWeight(6),
            new LootItemStack(ItemRegistry.WEEPING_BLUE_PETAL).setAmount(1, 3).setWeight(6),

            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.VALONITE_SHARD.getID()).setAmount(1, 4).setWeight(3),

            //new LootItemStack(ItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(1),
            //new LootItemStack(ItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(1),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.AMULET_SOCKET.getID()).setWeight(1),
            //new LootItemStack(ItemRegistry.LORE).setAmount(1).setWeight(1)
    }).setPostProcessor(new IPostProcess() {
        @Override
        public ItemStack postProcessItem(ItemStack is, Random rand) {
            /*if (is != null && is.getItem() == ItemRegistry.lore) {
                is = ItemLore.createPageStack(rand);
            }*/
            return is;
        }
    });


    public static final WeightedLootList DUNGEON_POT_LOOT = new WeightedLootList(new LootItemStack[] {
            new LootItemStack(ItemRegistry.MARSHMALLOW).setAmount(1).setWeight(28),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SYRMORITE_INGOT.getID()).setAmount(4, 16).setWeight(28),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.OCTINE_INGOT.getID()).setAmount(4, 16).setWeight(28),
            new LootItemStack(ItemRegistry.MARSHMALLOW_PINK).setAmount(1).setWeight(28),
            new LootItemStack(ItemRegistry.REED_DONUT).setAmount(2, 4).setWeight(28),
            new LootItemStack(ItemRegistry.JAM_DONUT).setAmount(2, 4).setWeight(28),
            //new LootItemStack(ItemRegistry.scroll).setAmount(1).setWeight(26),

            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SULFUR.getID()).setAmount(8, 16).setWeight(15),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.SWAMP_REED_ROPE.getID()).setAmount(4, 8).setWeight(15),
            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.VALONITE_SHARD.getID()).setAmount(1, 4).setWeight(15),
            //new LootItemStack(ItemRegistry.angryPebble).setAmount(8, 16).setWeight(15),
            //new LootItemStack(ItemRegistry.scroll).setAmount(1, 3).setWeight(15),
            //new LootItemStack(ItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(15),
            new LootItemStack(ItemRegistry.SAP_BALL).setAmount(8, 32).setWeight(15),

            new LootItemStack(ItemRegistry.ITEMS_MISC).setDamage(ItemMisc.EnumItemMisc.AMULET_SOCKET.getID()).setWeight(6),
            //new LootItemStack(ItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(4),
            //new LootItemStack(ItemRegistry.lore).setAmount(1).setWeight(1)
    }).setPostProcessor(new IPostProcess() {
        @Override
        public ItemStack postProcessItem(ItemStack is, Random rand) {
            /*if (is != null && is.getItem() == ItemRegistry.lore) {
                is = ItemLore.createPageStack(rand);
            }*/
            return is;
        }
    });
}
