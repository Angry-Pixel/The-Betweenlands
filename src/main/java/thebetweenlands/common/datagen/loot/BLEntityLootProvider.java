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
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemDamageFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.registries.DeferredHolder;
import thebetweenlands.common.loot.AnadiaBodyPredicate;
import thebetweenlands.common.loot.AnadiaHeadPredicate;
import thebetweenlands.common.loot.AnadiaTailPredicate;
import thebetweenlands.common.loot.SetCountFromAnadiaFunction;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.function.Supplier;
import java.util.stream.Stream;

public class BLEntityLootProvider extends EntityLootSubProvider {

	protected BLEntityLootProvider(HolderLookup.Provider provider) {
		super(FeatureFlags.REGISTRY.allFlags(), provider);
	}

	//TODO actually generate loot tables
	@Override
	public void generate() {
		this.noLoot(EntityRegistry.WIGHT);
		this.noLoot(EntityRegistry.SWAMP_HAG);
		this.noLoot(EntityRegistry.GECKO);
		this.noLoot(EntityRegistry.BUBBLER_CRAB);
		this.noLoot(EntityRegistry.SILT_CRAB);
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
	}

	public <T extends Entity> void noLoot(DeferredHolder<EntityType<?>, EntityType<T>> type) {
		this.add(type.get(), LootTable.lootTable());
	}

	@Override
	protected Stream<EntityType<?>> getKnownEntityTypes() {
		return EntityRegistry.ENTITY_TYPES.getEntries().stream().map(Supplier::get);
	}
}
