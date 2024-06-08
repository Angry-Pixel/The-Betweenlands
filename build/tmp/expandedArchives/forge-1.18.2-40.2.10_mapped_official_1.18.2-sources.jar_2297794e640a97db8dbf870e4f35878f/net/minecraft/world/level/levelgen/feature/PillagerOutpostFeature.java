package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class PillagerOutpostFeature extends JigsawFeature {
   public PillagerOutpostFeature(Codec<JigsawConfiguration> p_66562_) {
      super(p_66562_, 0, true, true, PillagerOutpostFeature::checkLocation);
   }

   private static boolean checkLocation(PieceGeneratorSupplier.Context<JigsawConfiguration> p_197134_) {
      ChunkPos chunkpos = p_197134_.chunkPos();
      int i = chunkpos.x >> 4;
      int j = chunkpos.z >> 4;
      WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
      worldgenrandom.setSeed((long)(i ^ j << 4) ^ p_197134_.seed());
      worldgenrandom.nextInt();
      if (worldgenrandom.nextInt(5) != 0) {
         return false;
      } else {
         return !p_197134_.chunkGenerator().hasFeatureChunkInRange(BuiltinStructureSets.VILLAGES, p_197134_.seed(), chunkpos.x, chunkpos.z, 10);
      }
   }
}