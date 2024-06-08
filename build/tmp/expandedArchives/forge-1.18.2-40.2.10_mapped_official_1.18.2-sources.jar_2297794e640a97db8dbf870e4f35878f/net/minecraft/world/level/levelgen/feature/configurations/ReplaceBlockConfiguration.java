package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;

public class ReplaceBlockConfiguration implements FeatureConfiguration {
   public static final Codec<ReplaceBlockConfiguration> CODEC = RecordCodecBuilder.create((p_161087_) -> {
      return p_161087_.group(Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter((p_161089_) -> {
         return p_161089_.targetStates;
      })).apply(p_161087_, ReplaceBlockConfiguration::new);
   });
   public final List<OreConfiguration.TargetBlockState> targetStates;

   public ReplaceBlockConfiguration(BlockState p_68028_, BlockState p_68029_) {
      this(ImmutableList.of(OreConfiguration.target(new BlockStateMatchTest(p_68028_), p_68029_)));
   }

   public ReplaceBlockConfiguration(List<OreConfiguration.TargetBlockState> p_161085_) {
      this.targetStates = p_161085_;
   }
}