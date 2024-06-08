package net.minecraft.world.level.levelgen.material;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public record MaterialRuleList(List<NoiseChunk.BlockStateFiller> materialRuleList) implements NoiseChunk.BlockStateFiller {
   @Nullable
   public BlockState calculate(DensityFunction.FunctionContext p_209815_) {
      for(NoiseChunk.BlockStateFiller noisechunk$blockstatefiller : this.materialRuleList) {
         BlockState blockstate = noisechunk$blockstatefiller.calculate(p_209815_);
         if (blockstate != null) {
            return blockstate;
         }
      }

      return null;
   }
}