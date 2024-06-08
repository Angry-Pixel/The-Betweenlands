package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ScaffoldingBlockItem extends BlockItem {
   public ScaffoldingBlockItem(Block p_43060_, Item.Properties p_43061_) {
      super(p_43060_, p_43061_);
   }

   @Nullable
   public BlockPlaceContext updatePlacementContext(BlockPlaceContext p_43063_) {
      BlockPos blockpos = p_43063_.getClickedPos();
      Level level = p_43063_.getLevel();
      BlockState blockstate = level.getBlockState(blockpos);
      Block block = this.getBlock();
      if (!blockstate.is(block)) {
         return ScaffoldingBlock.getDistance(level, blockpos) == 7 ? null : p_43063_;
      } else {
         Direction direction;
         if (p_43063_.isSecondaryUseActive()) {
            direction = p_43063_.isInside() ? p_43063_.getClickedFace().getOpposite() : p_43063_.getClickedFace();
         } else {
            direction = p_43063_.getClickedFace() == Direction.UP ? p_43063_.getHorizontalDirection() : Direction.UP;
         }

         int i = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable().move(direction);

         while(i < 7) {
            if (!level.isClientSide && !level.isInWorldBounds(blockpos$mutableblockpos)) {
               Player player = p_43063_.getPlayer();
               int j = level.getMaxBuildHeight();
               if (player instanceof ServerPlayer && blockpos$mutableblockpos.getY() >= j) {
                  ((ServerPlayer)player).sendMessage((new TranslatableComponent("build.tooHigh", j - 1)).withStyle(ChatFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
               }
               break;
            }

            blockstate = level.getBlockState(blockpos$mutableblockpos);
            if (!blockstate.is(this.getBlock())) {
               if (blockstate.canBeReplaced(p_43063_)) {
                  return BlockPlaceContext.at(p_43063_, blockpos$mutableblockpos, direction);
               }
               break;
            }

            blockpos$mutableblockpos.move(direction);
            if (direction.getAxis().isHorizontal()) {
               ++i;
            }
         }

         return null;
      }
   }

   protected boolean mustSurvive() {
      return false;
   }
}