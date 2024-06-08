package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SimpleRandomSelectorFeature extends Feature<SimpleRandomFeatureConfiguration> {
   public SimpleRandomSelectorFeature(Codec<SimpleRandomFeatureConfiguration> p_66822_) {
      super(p_66822_);
   }

   public boolean place(FeaturePlaceContext<SimpleRandomFeatureConfiguration> p_160343_) {
      Random random = p_160343_.random();
      SimpleRandomFeatureConfiguration simplerandomfeatureconfiguration = p_160343_.config();
      WorldGenLevel worldgenlevel = p_160343_.level();
      BlockPos blockpos = p_160343_.origin();
      ChunkGenerator chunkgenerator = p_160343_.chunkGenerator();
      int i = random.nextInt(simplerandomfeatureconfiguration.features.size());
      PlacedFeature placedfeature = simplerandomfeatureconfiguration.features.get(i).value();
      return placedfeature.place(worldgenlevel, chunkgenerator, random, blockpos);
   }
}