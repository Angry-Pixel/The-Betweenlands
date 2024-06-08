package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class TargetBlock extends Block {
   private static final IntegerProperty OUTPUT_POWER = BlockStateProperties.POWER;
   private static final int ACTIVATION_TICKS_ARROWS = 20;
   private static final int ACTIVATION_TICKS_OTHER = 8;

   public TargetBlock(BlockBehaviour.Properties p_57379_) {
      super(p_57379_);
      this.registerDefaultState(this.stateDefinition.any().setValue(OUTPUT_POWER, Integer.valueOf(0)));
   }

   public void onProjectileHit(Level p_57381_, BlockState p_57382_, BlockHitResult p_57383_, Projectile p_57384_) {
      int i = updateRedstoneOutput(p_57381_, p_57382_, p_57383_, p_57384_);
      Entity entity = p_57384_.getOwner();
      if (entity instanceof ServerPlayer) {
         ServerPlayer serverplayer = (ServerPlayer)entity;
         serverplayer.awardStat(Stats.TARGET_HIT);
         CriteriaTriggers.TARGET_BLOCK_HIT.trigger(serverplayer, p_57384_, p_57383_.getLocation(), i);
      }

   }

   private static int updateRedstoneOutput(LevelAccessor p_57392_, BlockState p_57393_, BlockHitResult p_57394_, Entity p_57395_) {
      int i = getRedstoneStrength(p_57394_, p_57394_.getLocation());
      int j = p_57395_ instanceof AbstractArrow ? 20 : 8;
      if (!p_57392_.getBlockTicks().hasScheduledTick(p_57394_.getBlockPos(), p_57393_.getBlock())) {
         setOutputPower(p_57392_, p_57393_, i, p_57394_.getBlockPos(), j);
      }

      return i;
   }

   private static int getRedstoneStrength(BlockHitResult p_57409_, Vec3 p_57410_) {
      Direction direction = p_57409_.getDirection();
      double d0 = Math.abs(Mth.frac(p_57410_.x) - 0.5D);
      double d1 = Math.abs(Mth.frac(p_57410_.y) - 0.5D);
      double d2 = Math.abs(Mth.frac(p_57410_.z) - 0.5D);
      Direction.Axis direction$axis = direction.getAxis();
      double d3;
      if (direction$axis == Direction.Axis.Y) {
         d3 = Math.max(d0, d2);
      } else if (direction$axis == Direction.Axis.Z) {
         d3 = Math.max(d0, d1);
      } else {
         d3 = Math.max(d1, d2);
      }

      return Math.max(1, Mth.ceil(15.0D * Mth.clamp((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
   }

   private static void setOutputPower(LevelAccessor p_57386_, BlockState p_57387_, int p_57388_, BlockPos p_57389_, int p_57390_) {
      p_57386_.setBlock(p_57389_, p_57387_.setValue(OUTPUT_POWER, Integer.valueOf(p_57388_)), 3);
      p_57386_.scheduleTick(p_57389_, p_57387_.getBlock(), p_57390_);
   }

   public void tick(BlockState p_57397_, ServerLevel p_57398_, BlockPos p_57399_, Random p_57400_) {
      if (p_57397_.getValue(OUTPUT_POWER) != 0) {
         p_57398_.setBlock(p_57399_, p_57397_.setValue(OUTPUT_POWER, Integer.valueOf(0)), 3);
      }

   }

   public int getSignal(BlockState p_57402_, BlockGetter p_57403_, BlockPos p_57404_, Direction p_57405_) {
      return p_57402_.getValue(OUTPUT_POWER);
   }

   public boolean isSignalSource(BlockState p_57418_) {
      return true;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_57407_) {
      p_57407_.add(OUTPUT_POWER);
   }

   public void onPlace(BlockState p_57412_, Level p_57413_, BlockPos p_57414_, BlockState p_57415_, boolean p_57416_) {
      if (!p_57413_.isClientSide() && !p_57412_.is(p_57415_.getBlock())) {
         if (p_57412_.getValue(OUTPUT_POWER) > 0 && !p_57413_.getBlockTicks().hasScheduledTick(p_57414_, this)) {
            p_57413_.setBlock(p_57414_, p_57412_.setValue(OUTPUT_POWER, Integer.valueOf(0)), 18);
         }

      }
   }
}