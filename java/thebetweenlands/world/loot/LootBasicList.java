package thebetweenlands.world.loot;
import java.util.Random;

import net.minecraft.item.Item;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;

public class LootBasicList {

	public static final WeightedLootList loot = new WeightedLootList(new LootItemStack[]{
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
			new LootItemStack(BLItemRegistry.forbiddenFig).setWeight(2),
			new LootItemStack(BLItemRegistry.explorerHat).setWeight(2),
			new LootItemStack(BLItemRegistry.voodooDoll).setWeight(2),
			new LootItemStack(BLItemRegistry.ringOfPower).setWeight(2),
			new LootItemStack(BLItemRegistry.ringOfFlight).setWeight(2),
			new LootItemStack(BLItemRegistry.swiftPick).setWeight(2),
			new LootItemStack(BLItemRegistry.wightsBane).setWeight(2),
			new LootItemStack(BLItemRegistry.critterCruncher).setWeight(2),
			new LootItemStack(BLItemRegistry.hagHacker).setWeight(2),
			new LootItemStack(BLItemRegistry.sludgeSlicer).setWeight(2),
			new LootItemStack(BLItemRegistry.skullMask).setWeight(2),

			new LootItemStack(getARandomMusicDisk()).setWeight(1),

	});
	
	public static Item getARandomMusicDisk() {
		Random rand = new Random();
		int disc = rand.nextInt(14);
		switch (disc) {
		case 0:
			return BLItemRegistry.astatos;
		case 1:
			return BLItemRegistry.betweenYouAndMe;
		case 2:
			return BLItemRegistry.christmasOnTheMarsh;
		case 3:
			return BLItemRegistry.theExplorer;
		case 4:
			return BLItemRegistry.hagDance;
		case 5:
			return BLItemRegistry.lonelyFire;
		case 6:
			return BLItemRegistry.mysteriousRecord;
		case 7:
			return BLItemRegistry.ancient;
		case 8:
			return BLItemRegistry.beneathAGreenSky;
		case 9:
			return BLItemRegistry.dJWightsMixtape;
		case 10:
			return BLItemRegistry.onwards;
		case 11:
			return BLItemRegistry.stuckInTheMud;
		case 12:
			return BLItemRegistry.wanderingWisps;
		case 13:
			return BLItemRegistry.waterlogged;
		}
		return null;
	}
}