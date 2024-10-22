package thebetweenlands.common.datagen.loot;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.loot.*;
import thebetweenlands.common.registries.*;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class BLEntityLootProvider extends EntityLootSubProvider {

	protected BLEntityLootProvider(HolderLookup.Provider provider) {
		super(FeatureFlags.REGISTRY.allFlags(), provider);
	}

	//TODO add seasonal drops
	@Override
	public void generate() {
		this.add(EntityRegistry.WIGHT.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.WIGHT_HEART)
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))
				.add(EmptyLootItem.emptyItem().setWeight(2))
				.when(LootItemKilledByPlayerCondition.killedByPlayer())));
		this.add(EntityRegistry.SWAMP_HAG.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SLIMY_BONE)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.noLoot(EntityRegistry.GECKO);
		this.noLoot(EntityRegistry.BUBBLER_CRAB);
		this.add(EntityRegistry.SILT_CRAB.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SILT_CRAB_CLAW)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.add(EntityRegistry.ANADIA.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_ANADIA_MEAT)
					.apply(SmeltItemFunction.smelted()
						.when(this.shouldSmeltLoot()))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F))))));

		this.add(EntityRegistry.ANADIA.get(), LootTableRegistry.ANADIA_HEAD, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_BONES))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaHeadPredicate.head(MinMaxBounds.Ints.exactly(0))).build())))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_EYE))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaHeadPredicate.head(MinMaxBounds.Ints.exactly(1))).build())))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_GILLS))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaHeadPredicate.head(MinMaxBounds.Ints.exactly(2))).build()))));

		this.add(EntityRegistry.ANADIA.get(), LootTableRegistry.ANADIA_BODY, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_ANADIA_MEAT))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
				.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 5))));

		this.add(EntityRegistry.ANADIA.get(), LootTableRegistry.ANADIA_TAIL, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_SCALES))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaTailPredicate.tail(MinMaxBounds.Ints.exactly(0))).build())))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_FINS))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaTailPredicate.tail(MinMaxBounds.Ints.exactly(1))).build())))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_SWIM_BLADDER))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(AnadiaTailPredicate.tail(MinMaxBounds.Ints.exactly(2))).build()))));

		this.add(EntityRegistry.ANADIA.get(), LootTableRegistry.ANADIA_TREASURE, LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SHIMMER_STONE).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1)))
				.add(LootItem.lootTableItem(ItemRegistry.RUBBER_BOOTS).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1))
					.apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.45F))))
				.add(LootItem.lootTableItem(ItemRegistry.ROCK_SNOT_PEARL).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.LOOT_SCRAPS).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_NUGGET).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 2, 4)))
				.add(LootItem.lootTableItem(ItemRegistry.FISH_VORTEX_UPGRADE).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build())))
				.add(LootItem.lootTableItem(ItemRegistry.URCHIN_SPIKE_UPGRADE).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(0))).build())))
				.add(LootItem.lootTableItem(ItemRegistry.AQUA_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1)))
				.add(LootItem.lootTableItem(ItemRegistry.GREEN_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1)))
				.add(LootItem.lootTableItem(ItemRegistry.CRIMSON_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1)))
				.add(LootItem.lootTableItem(ItemRegistry.ROCK_SNOT_PEARL).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 2, 3)))
				.add(LootItem.lootTableItem(ItemRegistry.LOOT_SCRAPS).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_INGOT).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 1)))
				.add(LootItem.lootTableItem(ItemRegistry.GLIDE_UPGRADE).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build())))
				.add(LootItem.lootTableItem(ItemRegistry.ELECTRIC_UPGRADE).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(1))).build())))
				.add(LootItem.lootTableItem(ItemRegistry.VALONITE_SPLINTER).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 2, 4)))
				.add(LootItem.lootTableItem(ItemRegistry.AQUA_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.GREEN_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.CRIMSON_MIDDLE_GEM).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.ROCK_SNOT_PEARL).setWeight(1)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 2, 4)))
				.add(LootItem.lootTableItem(ItemRegistry.LOOT_SCRAPS).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 3)))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_INGOT).setWeight(2)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build()))
					.apply(SetCountFromAnadiaFunction.setAnadiaCount(0.125F, 0.95F, 1, 2)))
				.add(LootItem.lootTableItem(ItemRegistry.ASCENT_UPGRADE).setWeight(3)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(AnadiaBodyPredicate.body(MinMaxBounds.Ints.exactly(2))).build())))));

		this.add(EntityRegistry.MIRE_SNAIL.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.RAW_SNAIL_FLESH)
				.apply(SmeltItemFunction.smelted()
					.when(this.shouldSmeltLoot()))
				.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))))
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.OCHRE_SNAIL_SHELL)
				.when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(this.registries, 0.5F, 0.0625F)))));

		this.add(EntityRegistry.SHAMBLER.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SHAMBLER_TONGUE))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))));

		this.add(EntityRegistry.SLUDGE_WORM.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))));

		//TODO change this to use the squashed function for valonite shards and remove the sludgeballs
		this.add(EntityRegistry.TINY_SLUDGE_WORM.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))));

		this.noLoot(EntityRegistry.TINY_SLUDGE_WORM_HELPER);

		this.noLoot(EntityRegistry.STALKER);

		this.add(EntityRegistry.DREADFUL_PEAT_MUMMY.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.RING_OF_SUMMONING)))
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.AMULET_SLOT)))
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SHIMMER_STONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 6))))));

		this.add(EntityRegistry.PEAT_MUMMY.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(new BossPeatMummyPredicate(false)).build()))
				.add(LootItem.lootTableItem(ItemRegistry.SHIMMER_STONE).setWeight(6))
				.add(LootItem.lootTableItem(ItemRegistry.SHIMMER_STONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 5))).setWeight(1))
				.add(EmptyLootItem.emptyItem().setWeight(8)))
			.withPool(LootPool.lootPool()
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(new MummyHoldingShimmerstonePredicate(true)).build()))
				.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
					EntityPredicate.Builder.entity().subPredicate(new BossPeatMummyPredicate(false)).build()))
				.add(LootItem.lootTableItem(ItemRegistry.SHIMMER_STONE).setWeight(9))
				.add(EmptyLootItem.emptyItem().setWeight(1))));

		this.add(EntityRegistry.ASH_SPRITE.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.CREMAINS)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F))))));

		this.add(EntityRegistry.BARRISHEE.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RUNE_DOOR_KEY)))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.LIFE_CRYSTAL).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.68F))))
				.add(LootItem.lootTableItem(ItemRegistry.LIFE_CRYSTAL).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.50F))))
				.add(LootItem.lootTableItem(ItemRegistry.LIFE_CRYSTAL).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.25F))))
				.add(LootItem.lootTableItem(ItemRegistry.LIFE_CRYSTAL).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.20F))))));
		this.noLoot(EntityRegistry.GREEBLING);
		this.add(EntityRegistry.GREEBLING_CORACLE.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_ANADIA_MEAT).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.TINY_SLUDGE_WORM).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_BONES).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA_REMAINS).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA).apply(SetAnadiaPropertiesFunction.builder().randomize()).setWeight(2))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA).apply(SetAnadiaPropertiesFunction.builder().randomize().setRotten()))
				.add(LootItem.lootTableItem(ItemRegistry.ANADIA).apply(SetAnadiaPropertiesFunction.builder().randomize().setTreasureFish())))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANGLER_TOOTH))
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN))
				.add(LootItem.lootTableItem(BlockRegistry.SWAMP_KELP).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(LootItem.lootTableItem(ItemRegistry.REED_ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(LootItem.lootTableItem(ItemRegistry.ALGAE_CLUMP).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(LootItem.lootTableItem(ItemRegistry.WATER_WEEDS_DROP).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))))
			.withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(0.0F, 1.0F))
				.add(LootItem.lootTableItem(ItemRegistry.NET).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.FISHING_FLOAT_AND_HOOK).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.WEEDWOOD_BOWL).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.AMATE_MAP).setWeight(2))
				//TODO
//				.add(LootItem.lootTableItem(ItemRegistry.FISHING_SPEAR).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.72F))))
//				.add(LootItem.lootTableItem(ItemRegistry.FISHING_SPEAR).apply(SetItemDamageFunction.setDamage(ConstantValue.exactly(0.2F))))
				.add(LootItem.lootTableItem(ItemRegistry.FABRICATED_SCROLL))));

		this.add(EntityRegistry.CRYPT_CRAWLER.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.TAR_DRIP).setWeight(3))
				.add(LootItem.lootTableItem(ItemRegistry.VALONITE_SPLINTER).setWeight(1))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY_WING).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.OCTINE_NUGGET).setWeight(13))
				.add(LootItem.lootTableItem(ItemRegistry.SYRMORITE_NUGGET).setWeight(15))
				.add(LootItem.lootTableItem(ItemRegistry.AMATE_PAPER).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN).setWeight(10))
				.add(LootItem.lootTableItem(ItemRegistry.REED_DONUT).setWeight(5))
				.add(LootItem.lootTableItem(ItemRegistry.REED_ROPE).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))).setWeight(35)))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.ANCIENT_REMNANT).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
				.add(EmptyLootItem.emptyItem())));

		this.add(EntityRegistry.BIPED_CRYPT_CRAWLER.get(), this.fromEntityLootTable(EntityRegistry.CRYPT_CRAWLER.get()));
		this.add(EntityRegistry.CHIEF_CRYPT_CRAWLER.get(), this.fromEntityLootTable(EntityRegistry.CRYPT_CRAWLER.get()));
		this.noLoot(EntityRegistry.OLM);
		this.add(EntityRegistry.EMBERLING.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.OCTINE_NUGGET)
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(1, 3)))))
			.withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(ItemRegistry.UNDYING_EMBERS)
					.when(LootItemKilledByPlayerCondition.killedByPlayer())
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.add(EntityRegistry.EMBERLING_SHAMAN.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(ItemRegistry.UNDYING_EMBERS)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.add(EntityRegistry.DRAGONFLY.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(ItemRegistry.DRAGONFLY_WING)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.noLoot(EntityRegistry.FIREFLY);
		this.noLoot(EntityRegistry.JELLYFISH);
		this.add(EntityRegistry.LURKER.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.LURKER_SKIN)
					.when(LootItemKilledByPlayerCondition.killedByPlayer())
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.add(EntityRegistry.FRESHWATER_URCHIN.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(UniformGenerator.between(1, 2))
				.add(LootItem.lootTableItem(ItemRegistry.URCHIN_SPIKE)
					.when(LootItemKilledByPlayerCondition.killedByPlayer())
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.noLoot(EntityRegistry.CAVE_FISH);
		this.add(EntityRegistry.DARK_DRUID.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 1))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_1)
					.when(AnyOfCondition.anyOf(
						LootItemKilledByPlayerCondition.killedByPlayer().invert(),
						PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_1).invert())))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_2)
					.when(AnyOfCondition.anyOf(
						LootItemKilledByPlayerCondition.killedByPlayer().invert(),
						PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_2).invert())))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_3)
					.when(AnyOfCondition.anyOf(
						LootItemKilledByPlayerCondition.killedByPlayer().invert(),
						PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_3).invert())))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_4)
					.when(AnyOfCondition.anyOf(
						LootItemKilledByPlayerCondition.killedByPlayer().invert(),
						PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_4).invert()))))
			.withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 1))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_1))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_2))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_3))
				.add(LootItem.lootTableItem(ItemRegistry.SWAMP_TALISMAN_PIECE_4))
				.when(AllOfCondition.allOf(
					PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_1),
					PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_2),
					PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_3),
					PlayerHasItemCondition.hasItem(ItemRegistry.SWAMP_TALISMAN_PIECE_4)))));
		this.add(EntityRegistry.FROG.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.RAW_FROG_LEGS)
					.apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2)))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1)))
					.apply(SmeltItemFunction.smelted().when(this.shouldSmeltLoot()))))
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.POISON_GLAND)
					.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS,
						EntityPredicate.Builder.entity().subPredicate(FrogVariantPredicate.variant(this.registries.holderOrThrow(FrogVariantRegistry.POISON)))))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 2))))));
		this.add(EntityRegistry.ROOT_SPRITE.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(BlockRegistry.ROOT_POD)))
			.withPool(LootPool.lootPool()
				.add(EmptyLootItem.emptyItem().setWeight(2))
				.add(LootItem.lootTableItem(BlockRegistry.ROOT_POD)))
			.withPool(LootPool.lootPool()
				.add(EmptyLootItem.emptyItem().setWeight(14))
				.add(NestedLootTable.inlineLootTable(LootTable.lootTable().withPool(LootPool.lootPool()
					.add(LootItem.lootTableItem(ItemRegistry.LEAF)).add(LootItem.lootTableItem(ItemRegistry.ALGAE_CLUMP))
					.add(LootItem.lootTableItem(ItemRegistry.ARROW_ARUM_LEAF)).add(LootItem.lootTableItem(ItemRegistry.BLUE_EYED_GRASS_FLOWERS))
					.add(LootItem.lootTableItem(ItemRegistry.BLUE_IRIS_PETAL)).add(LootItem.lootTableItem(ItemRegistry.MIRE_CORAL_STALK))
					.add(LootItem.lootTableItem(ItemRegistry.DEEP_WATER_CORAL_STALK)).add(LootItem.lootTableItem(ItemRegistry.BOG_BEAN_FLOWER_DROP))
					.add(LootItem.lootTableItem(ItemRegistry.BONESET_FLOWERS)).add(LootItem.lootTableItem(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES))
					.add(LootItem.lootTableItem(ItemRegistry.BROOMSEDGE_LEAVES)).add(LootItem.lootTableItem(ItemRegistry.BUTTON_BUSH_FLOWERS))
					.add(LootItem.lootTableItem(ItemRegistry.CARDINAL_FLOWER_PETALS)).add(LootItem.lootTableItem(ItemRegistry.CATTAIL_HEAD))
					.add(LootItem.lootTableItem(ItemRegistry.CAVE_GRASS_BLADES)).add(LootItem.lootTableItem(ItemRegistry.COPPER_IRIS_PETALS))
					.add(LootItem.lootTableItem(ItemRegistry.GOLDEN_CLUB_FLOWERS)).add(LootItem.lootTableItem(ItemRegistry.LICHEN_CLUMP))
					.add(LootItem.lootTableItem(ItemRegistry.MARSH_HIBISCUS_FLOWER)).add(LootItem.lootTableItem(ItemRegistry.MARSH_MALLOW_FLOWER))
					.add(LootItem.lootTableItem(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP)).add(LootItem.lootTableItem(ItemRegistry.NETTLE_LEAF))
					.add(LootItem.lootTableItem(ItemRegistry.PHRAGMITE_STEMS)).add(LootItem.lootTableItem(ItemRegistry.PICKERELWEED_FLOWER))
					.add(LootItem.lootTableItem(ItemRegistry.SHOOT_LEAVES)).add(LootItem.lootTableItem(ItemRegistry.SLUDGECREEP_LEAVES))
					.add(LootItem.lootTableItem(ItemRegistry.SOFT_RUSH_LEAVES)).add(LootItem.lootTableItem(ItemRegistry.SUNDEW_HEAD))
					.add(LootItem.lootTableItem(ItemRegistry.SWAMP_GRASS_BLADES)).add(LootItem.lootTableItem(ItemRegistry.CAVE_MOSS_CLUMP))
					.add(LootItem.lootTableItem(ItemRegistry.MOSS_CLUMP)).add(LootItem.lootTableItem(ItemRegistry.MILKWEED_FLOWER))
					.add(LootItem.lootTableItem(ItemRegistry.HANGER_DROP)).add(LootItem.lootTableItem(ItemRegistry.PITCHER_PLANT_TRAP))
					.add(LootItem.lootTableItem(ItemRegistry.WATER_WEEDS_DROP)).add(LootItem.lootTableItem(ItemRegistry.VENUS_FLY_TRAP_HEAD))
					.add(LootItem.lootTableItem(ItemRegistry.VOLARPAD_LEAF)).add(LootItem.lootTableItem(ItemRegistry.THORN_BRANCH))
					.add(LootItem.lootTableItem(ItemRegistry.POISON_IVY_LEAF)).add(LootItem.lootTableItem(ItemRegistry.BLADDERWORT_STALK_DROP))
					.add(LootItem.lootTableItem(ItemRegistry.BLADDERWORT_FLOWER_DROP)).add(LootItem.lootTableItem(ItemRegistry.EDGE_SHROOM_GILLS))
					.add(LootItem.lootTableItem(ItemRegistry.EDGE_MOSS_CLUMP)).add(LootItem.lootTableItem(ItemRegistry.EDGE_LEAF_DROP))
					.add(LootItem.lootTableItem(ItemRegistry.ROTBULB_STALK)).add(LootItem.lootTableItem(ItemRegistry.PALE_GRASS_BLADES))
					.add(LootItem.lootTableItem(ItemRegistry.STRING_ROOT_FIBERS)).add(LootItem.lootTableItem(ItemRegistry.CRYPTWEED_BLADES))).build()))
				.add(LootItem.lootTableItem(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS).setWeight(4))
				.add(LootItem.lootTableItem(ItemRegistry.ASPECTRUS_SEEDS))));
		this.add(EntityRegistry.SPORELING.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.SPORES))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1)))));
		this.add(EntityRegistry.TARMINION.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(ItemRegistry.INANIMATE_TARMINION))));
		this.noLoot(EntityRegistry.SPLODESHROOM);
		this.add(EntityRegistry.SLUDGE.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().setRolls(UniformGenerator.between(0, 2))
				.add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL)
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
		this.add(EntityRegistry.SMOL_SLUDGE.get(), this.fromEntityLootTable(EntityRegistry.SLUDGE.get()));
	}

	public <T extends Entity> void noLoot(DeferredHolder<EntityType<?>, EntityType<T>> type) {
		this.add(type.get(), LootTable.lootTable());
	}

	public LootTable.Builder fromEntityLootTable(EntityType<?> parent) {
		return LootTable.lootTable()
			.withPool(LootPool.lootPool()
				.setRolls(ConstantValue.exactly(1))
				.add(NestedLootTable.lootTableReference(parent.getDefaultLootTable())));
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return EntityRegistry.ENTITY_TYPES.getEntries().stream().map(Supplier::get);
	}
}
