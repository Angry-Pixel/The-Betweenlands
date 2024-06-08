package net.minecraft.tags;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;

public interface ConfiguredStructureTags {
   TagKey<ConfiguredStructureFeature<?, ?>> EYE_OF_ENDER_LOCATED = create("eye_of_ender_located");
   TagKey<ConfiguredStructureFeature<?, ?>> DOLPHIN_LOCATED = create("dolphin_located");
   TagKey<ConfiguredStructureFeature<?, ?>> ON_WOODLAND_EXPLORER_MAPS = create("on_woodland_explorer_maps");
   TagKey<ConfiguredStructureFeature<?, ?>> ON_OCEAN_EXPLORER_MAPS = create("on_ocean_explorer_maps");
   TagKey<ConfiguredStructureFeature<?, ?>> ON_TREASURE_MAPS = create("on_treasure_maps");
   TagKey<ConfiguredStructureFeature<?, ?>> VILLAGE = create("village");
   TagKey<ConfiguredStructureFeature<?, ?>> MINESHAFT = create("mineshaft");
   TagKey<ConfiguredStructureFeature<?, ?>> SHIPWRECK = create("shipwreck");
   TagKey<ConfiguredStructureFeature<?, ?>> RUINED_PORTAL = create("ruined_portal");
   TagKey<ConfiguredStructureFeature<?, ?>> OCEAN_RUIN = create("ocean_ruin");

   private static TagKey<ConfiguredStructureFeature<?, ?>> create(String p_207644_) {
      return TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation(p_207644_));
   }
}