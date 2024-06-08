package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class JumpOnBed extends Behavior<Mob> {
   private static final int MAX_TIME_TO_REACH_BED = 100;
   private static final int MIN_JUMPS = 3;
   private static final int MAX_JUMPS = 6;
   private static final int COOLDOWN_BETWEEN_JUMPS = 5;
   private final float speedModifier;
   @Nullable
   private BlockPos targetBed;
   private int remainingTimeToReachBed;
   private int remainingJumps;
   private int remainingCooldownUntilNextJump;

   public JumpOnBed(float p_23335_) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_BED, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speedModifier = p_23335_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23346_, Mob p_23347_) {
      return p_23347_.isBaby() && this.nearBed(p_23346_, p_23347_);
   }

   protected void start(ServerLevel p_23349_, Mob p_23350_, long p_23351_) {
      super.start(p_23349_, p_23350_, p_23351_);
      this.getNearestBed(p_23350_).ifPresent((p_23355_) -> {
         this.targetBed = p_23355_;
         this.remainingTimeToReachBed = 100;
         this.remainingJumps = 3 + p_23349_.random.nextInt(4);
         this.remainingCooldownUntilNextJump = 0;
         this.startWalkingTowardsBed(p_23350_, p_23355_);
      });
   }

   protected void stop(ServerLevel p_23372_, Mob p_23373_, long p_23374_) {
      super.stop(p_23372_, p_23373_, p_23374_);
      this.targetBed = null;
      this.remainingTimeToReachBed = 0;
      this.remainingJumps = 0;
      this.remainingCooldownUntilNextJump = 0;
   }

   protected boolean canStillUse(ServerLevel p_23383_, Mob p_23384_, long p_23385_) {
      return p_23384_.isBaby() && this.targetBed != null && this.isBed(p_23383_, this.targetBed) && !this.tiredOfWalking(p_23383_, p_23384_) && !this.tiredOfJumping(p_23383_, p_23384_);
   }

   protected boolean timedOut(long p_23337_) {
      return false;
   }

   protected void tick(ServerLevel p_23394_, Mob p_23395_, long p_23396_) {
      if (!this.onOrOverBed(p_23394_, p_23395_)) {
         --this.remainingTimeToReachBed;
      } else if (this.remainingCooldownUntilNextJump > 0) {
         --this.remainingCooldownUntilNextJump;
      } else {
         if (this.onBedSurface(p_23394_, p_23395_)) {
            p_23395_.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
         }

      }
   }

   private void startWalkingTowardsBed(Mob p_23362_, BlockPos p_23363_) {
      p_23362_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(p_23363_, this.speedModifier, 0));
   }

   private boolean nearBed(ServerLevel p_23369_, Mob p_23370_) {
      return this.onOrOverBed(p_23369_, p_23370_) || this.getNearestBed(p_23370_).isPresent();
   }

   private boolean onOrOverBed(ServerLevel p_23380_, Mob p_23381_) {
      BlockPos blockpos = p_23381_.blockPosition();
      BlockPos blockpos1 = blockpos.below();
      return this.isBed(p_23380_, blockpos) || this.isBed(p_23380_, blockpos1);
   }

   private boolean onBedSurface(ServerLevel p_23391_, Mob p_23392_) {
      return this.isBed(p_23391_, p_23392_.blockPosition());
   }

   private boolean isBed(ServerLevel p_23357_, BlockPos p_23358_) {
      return p_23357_.getBlockState(p_23358_).is(BlockTags.BEDS);
   }

   private Optional<BlockPos> getNearestBed(Mob p_23360_) {
      return p_23360_.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
   }

   private boolean tiredOfWalking(ServerLevel p_23398_, Mob p_23399_) {
      return !this.onOrOverBed(p_23398_, p_23399_) && this.remainingTimeToReachBed <= 0;
   }

   private boolean tiredOfJumping(ServerLevel p_23401_, Mob p_23402_) {
      return this.onOrOverBed(p_23401_, p_23402_) && this.remainingJumps <= 0;
   }
}