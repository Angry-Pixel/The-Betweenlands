package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
   public SpawnerBlock(BlockBehaviour.Properties p_56781_) {
      super(p_56781_);
   }

   public BlockEntity newBlockEntity(BlockPos p_154687_, BlockState p_154688_) {
      return new SpawnerBlockEntity(p_154687_, p_154688_);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_154683_, BlockState p_154684_, BlockEntityType<T> p_154685_) {
      return createTickerHelper(p_154685_, BlockEntityType.MOB_SPAWNER, p_154683_.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
   }

   public void spawnAfterBreak(BlockState p_56789_, ServerLevel p_56790_, BlockPos p_56791_, ItemStack p_56792_) {
      super.spawnAfterBreak(p_56789_, p_56790_, p_56791_, p_56792_);
   }

   @Override
   public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, int fortune, int silktouch) {
      return 15 + RANDOM.nextInt(15) + RANDOM.nextInt(15);
   }

   public RenderShape getRenderShape(BlockState p_56794_) {
      return RenderShape.MODEL;
   }

   public ItemStack getCloneItemStack(BlockGetter p_56785_, BlockPos p_56786_, BlockState p_56787_) {
      return ItemStack.EMPTY;
   }
}
