package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateConfiguration implements FeatureConfiguration {
   public static final Codec<BlockStateConfiguration> CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockStateConfiguration::new, (p_67552_) -> {
      return p_67552_.state;
   }).codec();
   public final BlockState state;

   public BlockStateConfiguration(BlockState p_67550_) {
      this.state = p_67550_;
   }
}