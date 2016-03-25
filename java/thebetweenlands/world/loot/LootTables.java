package thebetweenlands.world.loot;

import java.util.Random;

import net.minecraft.item.ItemStack;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.loot.ItemLore;
import thebetweenlands.items.misc.ItemGeneric;

public class LootTables {
	public static final WeightedLootList COMMON_POT_LOOT = new WeightedLootList(new LootItemStack[] {
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(34),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(34),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(34),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(34),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(34),

			new LootItemStack(BLItemRegistry.anglerToothArrow).setAmount(12, 26).setWeight(22),
			new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 16).setWeight(18),
			new LootItemStack(BLItemRegistry.poisonedAnglerToothArrow).setAmount(8, 16).setWeight(16),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(12),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(12),
			new LootItemStack(BLItemRegistry.weepingBluePetal).setAmount(2, 8).setWeight(11),
			new LootItemStack(BLItemRegistry.octineArrow).setAmount(8, 16).setWeight(11),
			new LootItemStack(BLItemRegistry.wightsHeart).setAmount(1, 3).setWeight(9),
			new LootItemStack(BLItemRegistry.basiliskArrow).setAmount(8).setWeight(9),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(8),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.VALONITE_SHARD.id).setAmount(1, 4).setWeight(3),

			//very rare
			new LootItemStack(BLItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(1),
			new LootItemStack(BLItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(1),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(1),
			new LootItemStack(BLItemRegistry.lore).setAmount(1).setWeight(1),
	}).setPostProcessor(new IPostProcess() {
		@Override
		public ItemStack postProcessItem(ItemStack is, Random rand) {
			if (is != null && is.getItem() == BLItemRegistry.lore) {
				is = ItemLore.createPageStack(rand);
			}
			return is;
		}
	});

	public static final WeightedLootList COMMON_CHEST_LOOT = new WeightedLootList(new LootItemStack[] {
			new LootItemStack(BLItemRegistry.marshmallowPink).setAmount(1).setWeight(22),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(18),
			new LootItemStack(BLItemRegistry.anglerToothArrow).setAmount(12, 26).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(18),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(18),

			new LootItemStack(BLItemRegistry.marshmallow).setAmount(1).setWeight(17),
			new LootItemStack(BLItemRegistry.reedDonut).setAmount(2, 4).setWeight(17),
			new LootItemStack(BLItemRegistry.jamDonut).setAmount(2, 4).setWeight(17),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(17),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(15),
			new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 16).setWeight(13),
			new LootItemStack(BLItemRegistry.poisonedAnglerToothArrow).setAmount(8, 16).setWeight(13),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(12),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(12),
			new LootItemStack(BLItemRegistry.wightsHeart).setAmount(1, 3).setWeight(12),
			new LootItemStack(BLItemRegistry.weepingBluePetal).setAmount(2, 8).setWeight(11),
			new LootItemStack(BLItemRegistry.octineArrow).setAmount(8, 16).setWeight(11),
			new LootItemStack(BLItemRegistry.basiliskArrow).setAmount(8).setWeight(11),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.VALONITE_SHARD.id).setAmount(1, 4).setWeight(8),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(1),

			//very rare
			new LootItemStack(BLItemRegistry.lore).setAmount(1).setWeight(1),
	}).setPostProcessor(new IPostProcess() {
		@Override
		public ItemStack postProcessItem(ItemStack is, Random rand) {
			if (is != null && is.getItem() == BLItemRegistry.lore) {
				is = ItemLore.createPageStack(rand);
			}
			return is;
		}
	});

	public static final WeightedLootList DUNGEON_POT_LOOT = new WeightedLootList(new LootItemStack[] {
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(21),
			new LootItemStack(BLItemRegistry.marshmallow).setAmount(1).setWeight(20),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(20),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(20),
			new LootItemStack(BLItemRegistry.marshmallowPink).setAmount(1).setWeight(20),
			new LootItemStack(BLItemRegistry.reedDonut).setAmount(2, 4).setWeight(20),
			new LootItemStack(BLItemRegistry.jamDonut).setAmount(2, 4).setWeight(20),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(20),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.VALONITE_SHARD.id).setAmount(1, 4).setWeight(15),
			new LootItemStack(BLItemRegistry.angryPebble).setAmount(8, 16).setWeight(15),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1, 3).setWeight(15),
			new LootItemStack(BLItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(15),
			new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(15),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(8),
			new LootItemStack(BLItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(8),
			new LootItemStack(BLItemRegistry.lore).setAmount(1).setWeight(3),
	}).setPostProcessor(new IPostProcess() {
		@Override
		public ItemStack postProcessItem(ItemStack is, Random rand) {
			if (is != null && is.getItem() == BLItemRegistry.lore) {
				is = ItemLore.createPageStack(rand);
			}
			return is;
		}
	});

	public static final WeightedLootList DUNGEON_CHEST_LOOT = new WeightedLootList(new LootItemStack[] {
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(22),
			new LootItemStack(BLItemRegistry.marshmallowPink).setAmount(1).setWeight(20),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(17),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(17),
			new LootItemStack(BLItemRegistry.marshmallow).setAmount(1).setWeight(17),
			new LootItemStack(BLItemRegistry.reedDonut).setAmount(2, 4).setWeight(17),
			new LootItemStack(BLItemRegistry.jamDonut).setAmount(2, 4).setWeight(17),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(17),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1).setWeight(17),

			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(12),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(12),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(12),
			new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(22),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(12),

			new LootItemStack(BLItemRegistry.forbiddenFig).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.explorerHat).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.voodooDoll).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.swiftPick).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.wightsBane).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.critterCruncher).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.hagHacker).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.sludgeSlicer).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.skullMask).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.lore).setAmount(1).setWeight(2),

			new LootItemStack(BLItemRegistry.ringOfPower).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.ringOfFlight).setAmount(1).setWeight(1),

			new LootItemStack(BLItemRegistry.astatos).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.betweenYouAndMe).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.christmasOnTheMarsh).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.theExplorer).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.hagDance).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.lonelyFire).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.mysteriousRecord).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.ancient).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.beneathAGreenSky).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.dJWightsMixtape).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.onwards).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.stuckInTheMud).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.wanderingWisps).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.waterlogged).setAmount(1).setWeight(1),
	}).setPostProcessor(new IPostProcess() {
		@Override
		public ItemStack postProcessItem(ItemStack is, Random rand) {
			if (is != null && is.getItem() == BLItemRegistry.lore) {
				is = ItemLore.createPageStack(rand);
			}
			return is;
		}
	});

	/*public static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
			//common
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.ANGLER_TOOTH.id).setAmount(4, 8).setWeight(21),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.LURKER_SKIN.id).setAmount(2, 4).setWeight(21),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SULFUR.id).setAmount(8, 16).setWeight(21),
			new LootItemStack(BLItemRegistry.sapBall).setAmount(8, 32).setWeight(21),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SWAMP_REED_ROPE.id).setAmount(4, 8).setWeight(21),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.PARCHMENT.id).setAmount(8, 16).setWeight(21),
			//in between
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.SYRMORITE_INGOT.id).setAmount(4, 16).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.OCTINE_INGOT.id).setAmount(4, 16).setWeight(15),
			new LootItemStack(BLItemRegistry.marshmallowPink).setAmount(1).setWeight(18),
			new LootItemStack(BLItemRegistry.marshmallow).setAmount(1).setWeight(15),
			new LootItemStack(BLItemRegistry.reedDonut).setAmount(2, 4).setWeight(15),
			new LootItemStack(BLItemRegistry.jamDonut).setAmount(2, 4).setWeight(15),
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.AMULET_SOCKET.id).setWeight(15),
			//rare
			new LootItemStack(BLItemRegistry.itemsGeneric).setDamage(ItemGeneric.EnumItemGeneric.VALONITE_SHARD.id).setAmount(1, 4).setWeight(9),
			new LootItemStack(BLItemRegistry.angryPebble).setAmount(8, 16).setWeight(9),
			new LootItemStack(BLItemRegistry.scroll).setAmount(1, 3).setWeight(9),
			new LootItemStack(BLItemRegistry.middleFruitSeeds).setAmount(1, 8).setWeight(9),
			new LootItemStack(BLItemRegistry.aspectrusCropSeed).setAmount(1, 8).setWeight(9),
			//very rare
			new LootItemStack(BLItemRegistry.forbiddenFig).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.explorerHat).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.voodooDoll).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.ringOfPower).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.ringOfFlight).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.swiftPick).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.wightsBane).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.critterCruncher).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.hagHacker).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.sludgeSlicer).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.skullMask).setAmount(1).setWeight(2),
			new LootItemStack(BLItemRegistry.lore).setAmount(1).setWeight(2),

			new LootItemStack(BLItemRegistry.astatos).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.betweenYouAndMe).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.christmasOnTheMarsh).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.theExplorer).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.hagDance).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.lonelyFire).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.mysteriousRecord).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.ancient).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.beneathAGreenSky).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.dJWightsMixtape).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.onwards).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.stuckInTheMud).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.wanderingWisps).setAmount(1).setWeight(1),
			new LootItemStack(BLItemRegistry.waterlogged).setAmount(1).setWeight(1),

	}).setPostProcessor(new IPostProcess() {
		@Override
		public ItemStack postProcessItem(ItemStack is, Random rand) {
			if (is != null && is.getItem() == BLItemRegistry.lore) {
				is = ItemLore.createPageStack(rand);
			}
			return is;
		}
	});*/
}