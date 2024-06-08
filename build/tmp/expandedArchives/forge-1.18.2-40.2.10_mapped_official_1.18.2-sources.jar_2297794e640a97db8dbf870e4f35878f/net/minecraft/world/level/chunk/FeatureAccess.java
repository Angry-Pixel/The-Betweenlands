package net.minecraft.world.level.chunk;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public interface FeatureAccess {
   @Nullable
   StructureStart getStartForFeature(ConfiguredStructureFeature<?, ?> p_208099_);

   void setStartForFeature(ConfiguredStructureFeature<?, ?> p_208102_, StructureStart p_208103_);

   LongSet getReferencesForFeature(ConfiguredStructureFeature<?, ?> p_208104_);

   void addReferenceForFeature(ConfiguredStructureFeature<?, ?> p_208100_, long p_208101_);

   Map<ConfiguredStructureFeature<?, ?>, LongSet> getAllReferences();

   void setAllReferences(Map<ConfiguredStructureFeature<?, ?>, LongSet> p_62638_);
}