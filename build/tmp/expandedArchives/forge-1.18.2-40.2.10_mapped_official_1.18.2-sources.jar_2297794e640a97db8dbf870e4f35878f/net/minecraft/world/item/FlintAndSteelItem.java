package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FlintAndSteelItem extends Item {
   public FlintAndSteelItem(Item.Properties p_41295_) {
      super(p_41295_);
   }

   public InteractionResult useOn(UseOnContext p_41297_) {
      Player player = p_41297_.getPlayer();
      Level level = p_41297_.getLevel();
      BlockPos blockpos = p_41297_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
         BlockPos blockpos1 = blockpos.relative(p_41297_.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(level, blockpos1, p_41297_.getHorizontalDirection())) {
            level.playSound(player, blockpos1, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            BlockState blockstate1 = BaseFireBlock.getState(level, blockpos1);
            level.setBlock(blockpos1, blockstate1, 11);
            level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
            ItemStack itemstack = p_41297_.getItemInHand();
            if (player instanceof ServerPlayer) {
               CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, blockpos1, itemstack);
               itemstack.hurtAndBreak(1, player, (p_41300_) -> {
                  p_41300_.broadcastBreakEvent(p_41297_.getHand());
               });
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
         } else {
            return InteractionResult.FAIL;
         }
      } else {
         level.playSound(player, blockpos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
         level.setBlock(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)), 11);
         level.gameEvent(player, GameEvent.BLOCK_PLACE, blockpos);
         if (player != null) {
            p_41297_.getItemInHand().hurtAndBreak(1, player, (p_41303_) -> {
               p_41303_.broadcastBreakEvent(p_41297_.getHand());
            });
         }

         return InteractionResult.sidedSuccess(level.isClientSide());
      }
   }
}