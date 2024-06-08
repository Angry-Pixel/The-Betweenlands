package net.minecraft.data.worldgen;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public interface StructureSets {
   Holder<StructureSet> VILLAGES = register(BuiltinStructureSets.VILLAGES, new StructureSet(List.of(StructureSet.entry(StructureFeatures.VILLAGE_PLAINS), StructureSet.entry(StructureFeatures.VILLAGE_DESERT), StructureSet.entry(StructureFeatures.VILLAGE_SAVANNA), StructureSet.entry(StructureFeatures.VILLAGE_SNOWY), StructureSet.entry(StructureFeatures.VILLAGE_TAIGA)), new RandomSpreadStructurePlacement(34, 8, RandomSpreadType.LINEAR, 10387312)));
   Holder<StructureSet> DESERT_PYRAMIDS = register(BuiltinStructureSets.DESERT_PYRAMIDS, StructureFeatures.DESERT_PYRAMID, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357617));
   Holder<StructureSet> IGLOOS = register(BuiltinStructureSets.IGLOOS, StructureFeatures.IGLOO, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357618));
   Holder<StructureSet> JUNGLE_TEMPLES = register(BuiltinStructureSets.JUNGLE_TEMPLES, StructureFeatures.JUNGLE_TEMPLE, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357619));
   Holder<StructureSet> SWAMP_HUTS = register(BuiltinStructureSets.SWAMP_HUTS, StructureFeatures.SWAMP_HUT, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357620));
   Holder<StructureSet> PILLAGER_OUTPOSTS = register(BuiltinStructureSets.PILLAGER_OUTPOSTS, StructureFeatures.PILLAGER_OUTPOST, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 165745296));
   Holder<StructureSet> OCEAN_MONUMENTS = register(BuiltinStructureSets.OCEAN_MONUMENTS, StructureFeatures.OCEAN_MONUMENT, new RandomSpreadStructurePlacement(32, 5, RandomSpreadType.TRIANGULAR, 10387313));
   Holder<StructureSet> WOODLAND_MANSIONS = register(BuiltinStructureSets.WOODLAND_MANSIONS, StructureFeatures.WOODLAND_MANSION, new RandomSpreadStructurePlacement(80, 20, RandomSpreadType.TRIANGULAR, 10387319));
   Holder<StructureSet> BURIED_TREASURES = register(BuiltinStructureSets.BURIED_TREASURES, StructureFeatures.BURIED_TREASURE, new RandomSpreadStructurePlacement(1, 0, RandomSpreadType.LINEAR, 0, new Vec3i(9, 0, 9)));
   Holder<StructureSet> MINESHAFTS = register(BuiltinStructureSets.MINESHAFTS, new StructureSet(List.of(StructureSet.entry(StructureFeatures.MINESHAFT), StructureSet.entry(StructureFeatures.MINESHAFT_MESA)), new RandomSpreadStructurePlacement(1, 0, RandomSpreadType.LINEAR, 0)));
   Holder<StructureSet> RUINED_PORTALS = register(BuiltinStructureSets.RUINED_PORTALS, new StructureSet(List.of(StructureSet.entry(StructureFeatures.RUINED_PORTAL_STANDARD), StructureSet.entry(StructureFeatures.RUINED_PORTAL_DESERT), StructureSet.entry(StructureFeatures.RUINED_PORTAL_JUNGLE), StructureSet.entry(StructureFeatures.RUINED_PORTAL_SWAMP), StructureSet.entry(StructureFeatures.RUINED_PORTAL_MOUNTAIN), StructureSet.entry(StructureFeatures.RUINED_PORTAL_OCEAN), StructureSet.entry(StructureFeatures.RUINED_PORTAL_NETHER)), new RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 34222645)));
   Holder<StructureSet> SHIPWRECKS = register(BuiltinStructureSets.SHIPWRECKS, new StructureSet(List.of(StructureSet.entry(StructureFeatures.SHIPWRECK), StructureSet.entry(StructureFeatures.SHIPWRECK_BEACHED)), new RandomSpreadStructurePlacement(24, 4, RandomSpreadType.LINEAR, 165745295)));
   Holder<StructureSet> OCEAN_RUINS = register(BuiltinStructureSets.OCEAN_RUINS, new StructureSet(List.of(StructureSet.entry(StructureFeatures.OCEAN_RUIN_COLD), StructureSet.entry(StructureFeatures.OCEAN_RUIN_WARM)), new RandomSpreadStructurePlacement(20, 8, RandomSpreadType.LINEAR, 14357621)));
   Holder<StructureSet> NETHER_COMPLEXES = register(BuiltinStructureSets.NETHER_COMPLEXES, new StructureSet(List.of(StructureSet.entry(StructureFeatures.FORTRESS, 2), StructureSet.entry(StructureFeatures.BASTION_REMNANT, 3)), new RandomSpreadStructurePlacement(27, 4, RandomSpreadType.LINEAR, 30084232)));
   Holder<StructureSet> NETHER_FOSSILS = register(BuiltinStructureSets.NETHER_FOSSILS, StructureFeatures.NETHER_FOSSIL, new RandomSpreadStructurePlacement(2, 1, RandomSpreadType.LINEAR, 14357921));
   Holder<StructureSet> END_CITIES = register(BuiltinStructureSets.END_CITIES, StructureFeatures.END_CITY, new RandomSpreadStructurePlacement(20, 11, RandomSpreadType.TRIANGULAR, 10387313));
   Holder<StructureSet> STRONGHOLDS = register(BuiltinStructureSets.STRONGHOLDS, StructureFeatures.STRONGHOLD, new ConcentricRingsStructurePlacement(32, 3, 128));

   static Holder<StructureSet> bootstrap() {
      return BuiltinRegistries.STRUCTURE_SETS.holders().iterator().next();
   }

   static Holder<StructureSet> register(ResourceKey<StructureSet> p_211129_, StructureSet p_211130_) {
      return BuiltinRegistries.register(BuiltinRegistries.STRUCTURE_SETS, p_211129_, p_211130_);
   }

   static Holder<StructureSet> register(ResourceKey<StructureSet> p_211132_, Holder<ConfiguredStructureFeature<?, ?>> p_211133_, StructurePlacement p_211134_) {
      return register(p_211132_, new StructureSet(p_211133_, p_211134_));
   }
}