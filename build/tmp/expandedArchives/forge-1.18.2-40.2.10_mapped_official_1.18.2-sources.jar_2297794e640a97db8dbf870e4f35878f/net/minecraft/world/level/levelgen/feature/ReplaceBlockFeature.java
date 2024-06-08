package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.ReplaceBlockConfiguration;

public class ReplaceBlockFeature extends Feature<ReplaceBlockConfiguration> {
   public ReplaceBlockFeature(Codec<ReplaceBlockConfiguration> p_66651_) {
      super(p_66651_);
   }

   public boolean place(FeaturePlaceContext<ReplaceBlockConfiguration> p_160216_) {
      WorldGenLevel worldgenlevel = p_160216_.level();
      BlockPos blockpos = p_160216_.origin();
      ReplaceBlockConfiguration replaceblockconfiguration = p_160216_.config();

      for(OreConfiguration.TargetBlockState oreconfiguration$targetblockstate : replaceblockconfiguration.targetStates) {
         if (oreconfiguration$targetblockstate.target.test(worldgenlevel.getBlockState(blockpos), p_160216_.random())) {
            worldgenlevel.setBlock(blockpos, oreconfiguration$targetblockstate.state, 2);
            break;
         }
      }

      return true;
   }
}