package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class EndCrystalItem extends Item {
   public EndCrystalItem(Item.Properties p_41174_) {
      super(p_41174_);
   }

   public InteractionResult useOn(UseOnContext p_41176_) {
      Level level = p_41176_.getLevel();
      BlockPos blockpos = p_41176_.getClickedPos();
      BlockState blockstate = level.getBlockState(blockpos);
      if (!blockstate.is(Blocks.OBSIDIAN) && !blockstate.is(Blocks.BEDROCK)) {
         return InteractionResult.FAIL;
      } else {
         BlockPos blockpos1 = blockpos.above();
         if (!level.isEmptyBlock(blockpos1)) {
            return InteractionResult.FAIL;
         } else {
            double d0 = (double)blockpos1.getX();
            double d1 = (double)blockpos1.getY();
            double d2 = (double)blockpos1.getZ();
            List<Entity> list = level.getEntities((Entity)null, new AABB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));
            if (!list.isEmpty()) {
               return InteractionResult.FAIL;
            } else {
               if (level instanceof ServerLevel) {
                  EndCrystal endcrystal = new EndCrystal(level, d0 + 0.5D, d1, d2 + 0.5D);
                  endcrystal.setShowBottom(false);
                  level.addFreshEntity(endcrystal);
                  level.gameEvent(p_41176_.getPlayer(), GameEvent.ENTITY_PLACE, blockpos1);
                  EndDragonFight enddragonfight = ((ServerLevel)level).dragonFight();
                  if (enddragonfight != null) {
                     enddragonfight.tryRespawn();
                  }
               }

               p_41176_.getItemInHand().shrink(1);
               return InteractionResult.sidedSuccess(level.isClientSide);
            }
         }
      }
   }

   public boolean isFoil(ItemStack p_41178_) {
      return true;
   }
}