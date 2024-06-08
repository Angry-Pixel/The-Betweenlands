package net.minecraft.core.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

public class ShearsDispenseItemBehavior extends OptionalDispenseItemBehavior {
   protected ItemStack execute(BlockSource p_123580_, ItemStack p_123581_) {
      Level level = p_123580_.getLevel();
      if (!level.isClientSide()) {
         BlockPos blockpos = p_123580_.getPos().relative(p_123580_.getBlockState().getValue(DispenserBlock.FACING));
         this.setSuccess(tryShearBeehive((ServerLevel)level, blockpos) || tryShearLivingEntity((ServerLevel)level, blockpos));
         if (this.isSuccess() && p_123581_.hurt(1, level.getRandom(), (ServerPlayer)null)) {
            p_123581_.setCount(0);
         }
      }

      return p_123581_;
   }

   private static boolean tryShearBeehive(ServerLevel p_123577_, BlockPos p_123578_) {
      BlockState blockstate = p_123577_.getBlockState(p_123578_);
      if (blockstate.is(BlockTags.BEEHIVES, (p_202454_) -> {
         return p_202454_.hasProperty(BeehiveBlock.HONEY_LEVEL) && p_202454_.getBlock() instanceof BeehiveBlock;
      })) {
         int i = blockstate.getValue(BeehiveBlock.HONEY_LEVEL);
         if (i >= 5) {
            p_123577_.playSound((Player)null, p_123578_, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            BeehiveBlock.dropHoneycomb(p_123577_, p_123578_);
            ((BeehiveBlock)blockstate.getBlock()).releaseBeesAndResetHoneyLevel(p_123577_, blockstate, p_123578_, (Player)null, BeehiveBlockEntity.BeeReleaseStatus.BEE_RELEASED);
            p_123577_.gameEvent((Entity)null, GameEvent.SHEAR, p_123578_);
            return true;
         }
      }

      return false;
   }

   private static boolean tryShearLivingEntity(ServerLevel p_123583_, BlockPos p_123584_) {
      for(LivingEntity livingentity : p_123583_.getEntitiesOfClass(LivingEntity.class, new AABB(p_123584_), EntitySelector.NO_SPECTATORS)) {
         if (livingentity instanceof Shearable) {
            Shearable shearable = (Shearable)livingentity;
            if (shearable.readyForShearing()) {
               shearable.shear(SoundSource.BLOCKS);
               p_123583_.gameEvent((Entity)null, GameEvent.SHEAR, p_123584_);
               return true;
            }
         }
      }

      return false;
   }
}