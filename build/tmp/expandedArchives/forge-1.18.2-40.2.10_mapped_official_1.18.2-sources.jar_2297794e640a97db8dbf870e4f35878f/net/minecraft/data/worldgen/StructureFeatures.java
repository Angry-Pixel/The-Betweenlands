package net.minecraft.data.worldgen;

import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.NetherFortressFeature;
import net.minecraft.world.level.levelgen.feature.RuinedPortalFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RangeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RuinedPortalConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.OceanRuinFeature;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;

public class StructureFeatures {
   public static final Holder<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = register(BuiltinStructures.PILLAGER_OUTPOST, StructureFeature.PILLAGER_OUTPOST.configured(new JigsawConfiguration(PillagerOutpostPools.START, 7), BiomeTags.HAS_PILLAGER_OUTPOST, true, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1))))));
   public static final Holder<ConfiguredStructureFeature<?, ?>> MINESHAFT = register(BuiltinStructures.MINESHAFT, StructureFeature.MINESHAFT.configured(new MineshaftConfiguration(0.004F, MineshaftFeature.Type.NORMAL), BiomeTags.HAS_MINESHAFT));
   public static final Holder<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = register(BuiltinStructures.MINESHAFT_MESA, StructureFeature.MINESHAFT.configured(new MineshaftConfiguration(0.004F, MineshaftFeature.Type.MESA), BiomeTags.HAS_MINESHAFT_MESA));
   public static final Holder<ConfiguredStructureFeature<?, ?>> WOODLAND_MANSION = register(BuiltinStructures.WOODLAND_MANSION, StructureFeature.WOODLAND_MANSION.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_WOODLAND_MANSION));
   public static final Holder<ConfiguredStructureFeature<?, ?>> JUNGLE_TEMPLE = register(BuiltinStructures.JUNGLE_TEMPLE, StructureFeature.JUNGLE_TEMPLE.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_JUNGLE_TEMPLE));
   public static final Holder<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = register(BuiltinStructures.DESERT_PYRAMID, StructureFeature.DESERT_PYRAMID.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_DESERT_PYRAMID));
   public static final Holder<ConfiguredStructureFeature<?, ?>> IGLOO = register(BuiltinStructures.IGLOO, StructureFeature.IGLOO.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_IGLOO));
   public static final Holder<ConfiguredStructureFeature<?, ?>> SHIPWRECK = register(BuiltinStructures.SHIPWRECK, StructureFeature.SHIPWRECK.configured(new ShipwreckConfiguration(false), BiomeTags.HAS_SHIPWRECK));
   public static final Holder<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = register(BuiltinStructures.SHIPWRECK_BEACHED, StructureFeature.SHIPWRECK.configured(new ShipwreckConfiguration(true), BiomeTags.HAS_SHIPWRECK_BEACHED));
   public static final Holder<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = register(BuiltinStructures.SWAMP_HUT, StructureFeature.SWAMP_HUT.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_SWAMP_HUT, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1))), MobCategory.CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1))))));
   public static final Holder<ConfiguredStructureFeature<?, ?>> STRONGHOLD = register(BuiltinStructures.STRONGHOLD, StructureFeature.STRONGHOLD.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_STRONGHOLD, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> OCEAN_MONUMENT = register(BuiltinStructures.OCEAN_MONUMENT, StructureFeature.OCEAN_MONUMENT.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_OCEAN_MONUMENT, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create(new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4))), MobCategory.UNDERGROUND_WATER_CREATURE, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST), MobCategory.AXOLOTLS, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST))));
   public static final Holder<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = register(BuiltinStructures.OCEAN_RUIN_COLD, StructureFeature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.COLD, 0.3F, 0.9F), BiomeTags.HAS_OCEAN_RUIN_COLD));
   public static final Holder<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = register(BuiltinStructures.OCEAN_RUIN_WARM, StructureFeature.OCEAN_RUIN.configured(new OceanRuinConfiguration(OceanRuinFeature.Type.WARM, 0.3F, 0.9F), BiomeTags.HAS_OCEAN_RUIN_WARM));
   public static final Holder<ConfiguredStructureFeature<?, ?>> FORTRESS = register(BuiltinStructures.FORTRESS, StructureFeature.FORTRESS.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_NETHER_FORTRESS, Map.of(MobCategory.MONSTER, new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, NetherFortressFeature.FORTRESS_ENEMIES))));
   public static final Holder<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = register(BuiltinStructures.NETHER_FOSSIL, StructureFeature.NETHER_FOSSIL.configured(new RangeConfiguration(UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))), BiomeTags.HAS_NETHER_FOSSIL, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> END_CITY = register(BuiltinStructures.END_CITY, StructureFeature.END_CITY.configured(NoneFeatureConfiguration.INSTANCE, BiomeTags.HAS_END_CITY));
   public static final Holder<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = register(BuiltinStructures.BURIED_TREASURE, StructureFeature.BURIED_TREASURE.configured(new ProbabilityFeatureConfiguration(0.01F), BiomeTags.HAS_BURIED_TREASURE));
   public static final Holder<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = register(BuiltinStructures.BASTION_REMNANT, StructureFeature.BASTION_REMNANT.configured(new JigsawConfiguration(BastionPieces.START, 6), BiomeTags.HAS_BASTION_REMNANT));
   public static final Holder<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = register(BuiltinStructures.VILLAGE_PLAINS, StructureFeature.VILLAGE.configured(new JigsawConfiguration(PlainVillagePools.START, 6), BiomeTags.HAS_VILLAGE_PLAINS, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = register(BuiltinStructures.VILLAGE_DESERT, StructureFeature.VILLAGE.configured(new JigsawConfiguration(DesertVillagePools.START, 6), BiomeTags.HAS_VILLAGE_DESERT, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = register(BuiltinStructures.VILLAGE_SAVANNA, StructureFeature.VILLAGE.configured(new JigsawConfiguration(SavannaVillagePools.START, 6), BiomeTags.HAS_VILLAGE_SAVANNA, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = register(BuiltinStructures.VILLAGE_SNOWY, StructureFeature.VILLAGE.configured(new JigsawConfiguration(SnowyVillagePools.START, 6), BiomeTags.HAS_VILLAGE_SNOWY, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = register(BuiltinStructures.VILLAGE_TAIGA, StructureFeature.VILLAGE.configured(new JigsawConfiguration(TaigaVillagePools.START, 6), BiomeTags.HAS_VILLAGE_TAIGA, true));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_STANDARD = register(BuiltinStructures.RUINED_PORTAL_STANDARD, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.STANDARD), BiomeTags.HAS_RUINED_PORTAL_STANDARD));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = register(BuiltinStructures.RUINED_PORTAL_DESERT, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.DESERT), BiomeTags.HAS_RUINED_PORTAL_DESERT));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = register(BuiltinStructures.RUINED_PORTAL_JUNGLE, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.JUNGLE), BiomeTags.HAS_RUINED_PORTAL_JUNGLE));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = register(BuiltinStructures.RUINED_PORTAL_SWAMP, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.SWAMP), BiomeTags.HAS_RUINED_PORTAL_SWAMP));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.MOUNTAIN), BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = register(BuiltinStructures.RUINED_PORTAL_OCEAN, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.OCEAN), BiomeTags.HAS_RUINED_PORTAL_OCEAN));
   public static final Holder<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = register(BuiltinStructures.RUINED_PORTAL_NETHER, StructureFeature.RUINED_PORTAL.configured(new RuinedPortalConfiguration(RuinedPortalFeature.Type.NETHER), BiomeTags.HAS_RUINED_PORTAL_NETHER));

   public static Holder<? extends ConfiguredStructureFeature<?, ?>> bootstrap() {
      return MINESHAFT;
   }

   private static <FC extends FeatureConfiguration, F extends StructureFeature<FC>> Holder<ConfiguredStructureFeature<?, ?>> register(ResourceKey<ConfiguredStructureFeature<?, ?>> p_211107_, ConfiguredStructureFeature<FC, F> p_211108_) {
      return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, p_211107_, p_211108_);
   }
}