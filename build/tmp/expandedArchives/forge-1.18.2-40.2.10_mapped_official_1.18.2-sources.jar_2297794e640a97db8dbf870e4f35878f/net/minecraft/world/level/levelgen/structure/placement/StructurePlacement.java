package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.chunk.ChunkGenerator;

public interface StructurePlacement {
   Codec<StructurePlacement> CODEC = Registry.STRUCTURE_PLACEMENT_TYPE.byNameCodec().dispatch(StructurePlacement::type, StructurePlacementType::codec);

   boolean isFeatureChunk(ChunkGenerator p_212319_, long p_212320_, int p_212321_, int p_212322_);

   StructurePlacementType<?> type();
}