package net.minecraft.world.level.levelgen.structure;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public interface BuiltinStructures {
   ResourceKey<ConfiguredStructureFeature<?, ?>> PILLAGER_OUTPOST = createKey("pillager_outpost");
   ResourceKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = createKey("mineshaft");
   ResourceKey<ConfiguredStructureFeature<?, ?>> MINESHAFT_MESA = createKey("mineshaft_mesa");
   ResourceKey<ConfiguredStructureFeature<?, ?>> WOODLAND_MANSION = createKey("mansion");
   ResourceKey<ConfiguredStructureFeature<?, ?>> JUNGLE_TEMPLE = createKey("jungle_pyramid");
   ResourceKey<ConfiguredStructureFeature<?, ?>> DESERT_PYRAMID = createKey("desert_pyramid");
   ResourceKey<ConfiguredStructureFeature<?, ?>> IGLOO = createKey("igloo");
   ResourceKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = createKey("shipwreck");
   ResourceKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK_BEACHED = createKey("shipwreck_beached");
   ResourceKey<ConfiguredStructureFeature<?, ?>> SWAMP_HUT = createKey("swamp_hut");
   ResourceKey<ConfiguredStructureFeature<?, ?>> STRONGHOLD = createKey("stronghold");
   ResourceKey<ConfiguredStructureFeature<?, ?>> OCEAN_MONUMENT = createKey("monument");
   ResourceKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_COLD = createKey("ocean_ruin_cold");
   ResourceKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN_WARM = createKey("ocean_ruin_warm");
   ResourceKey<ConfiguredStructureFeature<?, ?>> FORTRESS = createKey("fortress");
   ResourceKey<ConfiguredStructureFeature<?, ?>> NETHER_FOSSIL = createKey("nether_fossil");
   ResourceKey<ConfiguredStructureFeature<?, ?>> END_CITY = createKey("end_city");
   ResourceKey<ConfiguredStructureFeature<?, ?>> BURIED_TREASURE = createKey("buried_treasure");
   ResourceKey<ConfiguredStructureFeature<?, ?>> BASTION_REMNANT = createKey("bastion_remnant");
   ResourceKey<ConfiguredStructureFeature<?, ?>> VILLAGE_PLAINS = createKey("village_plains");
   ResourceKey<ConfiguredStructureFeature<?, ?>> VILLAGE_DESERT = createKey("village_desert");
   ResourceKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SAVANNA = createKey("village_savanna");
   ResourceKey<ConfiguredStructureFeature<?, ?>> VILLAGE_SNOWY = createKey("village_snowy");
   ResourceKey<ConfiguredStructureFeature<?, ?>> VILLAGE_TAIGA = createKey("village_taiga");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_STANDARD = createKey("ruined_portal");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_DESERT = createKey("ruined_portal_desert");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_JUNGLE = createKey("ruined_portal_jungle");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_SWAMP = createKey("ruined_portal_swamp");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_MOUNTAIN = createKey("ruined_portal_mountain");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_OCEAN = createKey("ruined_portal_ocean");
   ResourceKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL_NETHER = createKey("ruined_portal_nether");

   private static ResourceKey<ConfiguredStructureFeature<?, ?>> createKey(String p_209873_) {
      return ResourceKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(p_209873_));
   }
}