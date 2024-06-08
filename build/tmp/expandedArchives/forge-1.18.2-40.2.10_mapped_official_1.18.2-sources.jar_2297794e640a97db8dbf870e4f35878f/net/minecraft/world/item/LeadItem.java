package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class LeadItem extends Item {
   public LeadItem(Item.Properties p_42828_) {
      super(p_42828_);
   }

   public InteractionResult useOn(UseOnContext p_42834_) {
      Level level = p_42834_.getLevel();
      BlockPos blockpos = p_42834_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      if (blockstate.is(BlockTags.FENCES)) {
         Player player = p_42834_.getPlayer();
         if (!level.isClientSide && player != null) {
            bindPlayerMobs(player, level, blockpos);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   public static InteractionResult bindPlayerMobs(Player p_42830_, Level p_42831_, BlockPos p_42832_) {
      LeashFenceKnotEntity leashfenceknotentity = null;
      boolean flag = false;
      double d0 = 7.0D;
      int i = p_42832_.getX();
      int j = p_42832_.getY();
      int k = p_42832_.getZ();

      for(Mob mob : p_42831_.getEntitiesOfClass(Mob.class, new AABB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (mob.getLeashHolder() == p_42830_) {
            if (leashfenceknotentity == null) {
               leashfenceknotentity = LeashFenceKnotEntity.getOrCreateKnot(p_42831_, p_42832_);
               leashfenceknotentity.playPlacementSound();
            }

            mob.setLeashedTo(leashfenceknotentity, true);
            flag = true;
         }
      }

      return flag ? InteractionResult.SUCCESS : InteractionResult.PASS;
   }
}