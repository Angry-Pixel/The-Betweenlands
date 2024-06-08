package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.core.BlockPos;
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

public class FireChargeItem extends Item {
   public FireChargeItem(Item.Properties p_41202_) {
      super(p_41202_);
   }

   public InteractionResult useOn(UseOnContext p_41204_) {
      Level level = p_41204_.getLevel();
      BlockPos blockpos = p_41204_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      boolean flag = false;
      if (!CampfireBlock.canLight(blockstate) && !CandleBlock.canLight(blockstate) && !CandleCakeBlock.canLight(blockstate)) {
         blockpos = blockpos.relative(p_41204_.getClickedFace());
         if (BaseFireBlock.canBePlacedAt(level, blockpos, p_41204_.getHorizontalDirection())) {
            this.playSound(level, blockpos);
            level.setBlockAndUpdate(blockpos, BaseFireBlock.getState(level, blockpos));
            level.gameEvent(p_41204_.getPlayer(), GameEvent.BLOCK_PLACE, blockpos);
            flag = true;
         }
      } else {
         this.playSound(level, blockpos);
         level.setBlockAndUpdate(blockpos, blockstate.setValue(BlockStateProperties.LIT, Boolean.valueOf(true)));
         level.gameEvent(p_41204_.getPlayer(), GameEvent.BLOCK_PLACE, blockpos);
         flag = true;
      }

      if (flag) {
         p_41204_.getItemInHand().shrink(1);
         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.FAIL;
      }
   }

   private void playSound(Level p_41206_, BlockPos p_41207_) {
      Random random = p_41206_.getRandom();
      p_41206_.playSound((Player)null, p_41207_, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
   }
}