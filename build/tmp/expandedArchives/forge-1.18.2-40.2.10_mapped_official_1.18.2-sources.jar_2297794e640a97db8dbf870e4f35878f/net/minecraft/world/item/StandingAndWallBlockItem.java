package net.minecraft.world.item;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;

public class StandingAndWallBlockItem extends BlockItem {
   protected final Block wallBlock;

   public StandingAndWallBlockItem(Block p_43248_, Block p_43249_, Item.Properties p_43250_) {
      super(p_43248_, p_43250_);
      this.wallBlock = p_43249_;
   }

   @Nullable
   protected BlockState getPlacementState(BlockPlaceContext p_43255_) {
      BlockState blockstate = this.wallBlock.getStateForPlacement(p_43255_);
      BlockState blockstate1 = null;
      LevelReader levelreader = p_43255_.getLevel();
      BlockPos blockpos = p_43255_.getClickedPos();

      for(Direction direction : p_43255_.getNearestLookingDirections()) {
         if (direction != Direction.UP) {
            BlockState blockstate2 = direction == Direction.DOWN ? this.getBlock().getStateForPlacement(p_43255_) : blockstate;
            if (blockstate2 != null && blockstate2.canSurvive(levelreader, blockpos)) {
               blockstate1 = blockstate2;
               break;
            }
         }
      }

      return blockstate1 != null && levelreader.isUnobstructed(blockstate1, blockpos, CollisionContext.empty()) ? blockstate1 : null;
   }

   public void registerBlocks(Map<Block, Item> p_43252_, Item p_43253_) {
      super.registerBlocks(p_43252_, p_43253_);
      p_43252_.put(this.wallBlock, p_43253_);
   }

   public void removeFromBlockToItemMap(Map<Block, Item> blockToItemMap, Item itemIn) {
      super.removeFromBlockToItemMap(blockToItemMap, itemIn);
      blockToItemMap.remove(this.wallBlock);
   }
}
