package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractChestBlock<E extends BlockEntity> extends BaseEntityBlock {
   protected final Supplier<BlockEntityType<? extends E>> blockEntityType;

   protected AbstractChestBlock(BlockBehaviour.Properties p_48677_, Supplier<BlockEntityType<? extends E>> p_48678_) {
      super(p_48677_);
      this.blockEntityType = p_48678_;
   }

   public abstract DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState p_48679_, Level p_48680_, BlockPos p_48681_, boolean p_48682_);
}