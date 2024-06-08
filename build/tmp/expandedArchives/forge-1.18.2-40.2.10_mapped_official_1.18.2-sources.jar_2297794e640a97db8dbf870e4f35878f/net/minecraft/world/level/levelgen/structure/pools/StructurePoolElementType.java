package net.minecraft.world.level.levelgen.structure.pools;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;

public interface StructurePoolElementType<P extends StructurePoolElement> {
   StructurePoolElementType<SinglePoolElement> SINGLE = register("single_pool_element", SinglePoolElement.CODEC);
   StructurePoolElementType<ListPoolElement> LIST = register("list_pool_element", ListPoolElement.CODEC);
   StructurePoolElementType<FeaturePoolElement> FEATURE = register("feature_pool_element", FeaturePoolElement.CODEC);
   StructurePoolElementType<EmptyPoolElement> EMPTY = register("empty_pool_element", EmptyPoolElement.CODEC);
   StructurePoolElementType<LegacySinglePoolElement> LEGACY = register("legacy_single_pool_element", LegacySinglePoolElement.CODEC);

   Codec<P> codec();

   static <P extends StructurePoolElement> StructurePoolElementType<P> register(String p_210551_, Codec<P> p_210552_) {
      return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, p_210551_, () -> {
         return p_210552_;
      });
   }
}