package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleStateProvider extends BlockStateProvider {
   public static final Codec<SimpleStateProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(SimpleStateProvider::new, (p_68804_) -> {
      return p_68804_.state;
   }).codec();
   private final BlockState state;

   protected SimpleStateProvider(BlockState p_68801_) {
      this.state = p_68801_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
   }

   public BlockState getState(Random p_68806_, BlockPos p_68807_) {
      return this.state;
   }
}