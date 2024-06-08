package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkGenerator;

public record ConcentricRingsStructurePlacement(int distance, int spread, int count) implements StructurePlacement {
   public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create((p_204960_) -> {
      return p_204960_.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count)).apply(p_204960_, ConcentricRingsStructurePlacement::new);
   });

   public boolean isFeatureChunk(ChunkGenerator p_212310_, long p_212311_, int p_212312_, int p_212313_) {
      List<ChunkPos> list = p_212310_.getRingPositionsFor(this);
      return list == null ? false : list.contains(new ChunkPos(p_212312_, p_212313_));
   }

   public StructurePlacementType<?> type() {
      return StructurePlacementType.CONCENTRIC_RINGS;
   }
}