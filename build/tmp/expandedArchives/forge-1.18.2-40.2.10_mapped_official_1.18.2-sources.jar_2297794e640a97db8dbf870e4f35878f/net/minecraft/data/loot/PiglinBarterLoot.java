package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetPotionFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class PiglinBarterLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_124468_) {
      p_124468_.accept(BuiltInLootTables.PIGLIN_BARTERING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.BOOK).setWeight(5).apply((new EnchantRandomlyFunction.Builder()).withEnchantment(Enchantments.SOUL_SPEED))).add(LootItem.lootTableItem(Items.IRON_BOOTS).setWeight(8).apply((new EnchantRandomlyFunction.Builder()).withEnchantment(Enchantments.SOUL_SPEED))).add(LootItem.lootTableItem(Items.POTION).setWeight(8).apply(SetPotionFunction.setPotion(Potions.FIRE_RESISTANCE))).add(LootItem.lootTableItem(Items.SPLASH_POTION).setWeight(8).apply(SetPotionFunction.setPotion(Potions.FIRE_RESISTANCE))).add(LootItem.lootTableItem(Items.POTION).setWeight(10).apply(SetPotionFunction.setPotion(Potions.WATER))).add(LootItem.lootTableItem(Items.IRON_NUGGET).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(10.0F, 36.0F)))).add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(10).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))).add(LootItem.lootTableItem(Items.STRING).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 9.0F)))).add(LootItem.lootTableItem(Items.QUARTZ).setWeight(20).apply(SetItemCountFunction.setCount(UniformGenerator.between(5.0F, 12.0F)))).add(LootItem.lootTableItem(Items.OBSIDIAN).setWeight(40)).add(LootItem.lootTableItem(Items.CRYING_OBSIDIAN).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))).add(LootItem.lootTableItem(Items.FIRE_CHARGE).setWeight(40)).add(LootItem.lootTableItem(Items.LEATHER).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 4.0F)))).add(LootItem.lootTableItem(Items.SOUL_SAND).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))).add(LootItem.lootTableItem(Items.NETHER_BRICK).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 8.0F)))).add(LootItem.lootTableItem(Items.SPECTRAL_ARROW).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(6.0F, 12.0F)))).add(LootItem.lootTableItem(Items.GRAVEL).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 16.0F)))).add(LootItem.lootTableItem(Items.BLACKSTONE).setWeight(40).apply(SetItemCountFunction.setCount(UniformGenerator.between(8.0F, 16.0F))))));
   }
}