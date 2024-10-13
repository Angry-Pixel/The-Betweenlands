package thebetweenlands.common.datagen.loot;

import net.minecraft.advancements.critereon.EntityPredicate;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.loot.*;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

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

		this.add(EntityRegistry.SLUDGE_WORM.get(), LootTable.lootTable()
			.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL))
				.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))));

		//TODO change this to use the squashed function for valonite shards and remove the sludgeballs
		this.add(EntityRegistry.SLUDGE_WORM_TINY.get(), LootTable.lootTable()
				.withPool(LootPool.lootPool().add(LootItem.lootTableItem(ItemRegistry.SLUDGE_BALL))
					.apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0.0F, 1.0F)))));

		this.noLoot(EntityRegistry.SLUDGE_WORM_TINY_HELPER);

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
