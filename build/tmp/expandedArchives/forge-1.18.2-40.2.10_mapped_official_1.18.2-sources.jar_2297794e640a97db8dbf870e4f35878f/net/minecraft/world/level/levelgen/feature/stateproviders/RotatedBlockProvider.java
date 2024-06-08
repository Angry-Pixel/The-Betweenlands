package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RotatedBlockProvider extends BlockStateProvider {
   public static final Codec<RotatedBlockProvider> CODEC = BlockState.CODEC.fieldOf("state").xmap(BlockBehaviour.BlockStateBase::getBlock, Block::defaultBlockState).xmap(RotatedBlockProvider::new, (p_68793_) -> {
      return p_68793_.block;
   }).codec();
   private final Block block;

   public RotatedBlockProvider(Block p_68790_) {
      this.block = p_68790_;
   }

   protected BlockStateProviderType<?> type() {
      return BlockStateProviderType.ROTATED_BLOCK_PROVIDER;
   }

   public BlockState getState(Random p_68795_, BlockPos p_68796_) {
      Direction.Axis direction$axis = Direction.Axis.getRandom(p_68795_);
      return this.block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, direction$axis);
   }
}