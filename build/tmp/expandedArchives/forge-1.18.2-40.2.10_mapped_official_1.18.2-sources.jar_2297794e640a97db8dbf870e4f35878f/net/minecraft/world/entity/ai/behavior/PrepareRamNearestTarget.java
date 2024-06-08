package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.Vec3;

public class PrepareRamNearestTarget<E extends PathfinderMob> extends Behavior<E> {
   public static final int TIME_OUT_DURATION = 160;
   private final ToIntFunction<E> getCooldownOnFail;
   private final int minRamDistance;
   private final int maxRamDistance;
   private final float walkSpeed;
   private final TargetingConditions ramTargeting;
   private final int ramPrepareTime;
   private final Function<E, SoundEvent> getPrepareRamSound;
   private Optional<Long> reachedRamPositionTimestamp = Optional.empty();
   private Optional<PrepareRamNearestTarget.RamCandidate> ramCandidate = Optional.empty();

   public PrepareRamNearestTarget(ToIntFunction<E> p_147724_, int p_147725_, int p_147726_, float p_147727_, TargetingConditions p_147728_, int p_147729_, Function<E, SoundEvent> p_147730_) {
      super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_ABSENT), 160);
      this.getCooldownOnFail = p_147724_;
      this.minRamDistance = p_147725_;
      this.maxRamDistance = p_147726_;
      this.walkSpeed = p_147727_;
      this.ramTargeting = p_147728_;
      this.ramPrepareTime = p_147729_;
      this.getPrepareRamSound = p_147730_;
   }

   protected void start(ServerLevel p_147736_, PathfinderMob p_147737_, long p_147738_) {
      Brain<?> brain = p_147737_.getBrain();
      brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).flatMap((p_186049_) -> {
         return p_186049_.findClosest((p_147789_) -> {
            return this.ramTargeting.test(p_147737_, p_147789_);
         });
      }).ifPresent((p_147778_) -> {
         this.chooseRamPosition(p_147737_, p_147778_);
      });
   }

   protected void stop(ServerLevel p_147762_, E p_147763_, long p_147764_) {
      Brain<?> brain = p_147763_.getBrain();
      if (!brain.hasMemoryValue(MemoryModuleType.RAM_TARGET)) {
         p_147762_.broadcastEntityEvent(p_147763_, (byte)59);
         brain.setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getCooldownOnFail.applyAsInt(p_147763_));
      }

   }

   protected boolean canStillUse(ServerLevel p_147773_, PathfinderMob p_147774_, long p_147775_) {
      return this.ramCandidate.isPresent() && this.ramCandidate.get().getTarget().isAlive();
   }

   protected void tick(ServerLevel p_147784_, E p_147785_, long p_147786_) {
      if (this.ramCandidate.isPresent()) {
         p_147785_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.ramCandidate.get().getStartPosition(), this.walkSpeed, 0));
         p_147785_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.ramCandidate.get().getTarget(), true));
         boolean flag = !this.ramCandidate.get().getTarget().blockPosition().equals(this.ramCandidate.get().getTargetPosition());
         if (flag) {
            p_147784_.broadcastEntityEvent(p_147785_, (byte)59);
            p_147785_.getNavigation().stop();
            this.chooseRamPosition(p_147785_, (this.ramCandidate.get()).target);
         } else {
            BlockPos blockpos = p_147785_.blockPosition();
            if (blockpos.equals(this.ramCandidate.get().getStartPosition())) {
               p_147784_.broadcastEntityEvent(p_147785_, (byte)58);
               if (!this.reachedRamPositionTimestamp.isPresent()) {
                  this.reachedRamPositionTimestamp = Optional.of(p_147786_);
               }

               if (p_147786_ - this.reachedRamPositionTimestamp.get() >= (long)this.ramPrepareTime) {
                  p_147785_.getBrain().setMemory(MemoryModuleType.RAM_TARGET, this.getEdgeOfBlock(blockpos, this.ramCandidate.get().getTargetPosition()));
                  p_147784_.playSound((Player)null, p_147785_, this.getPrepareRamSound.apply(p_147785_), SoundSource.HOSTILE, 1.0F, p_147785_.getVoicePitch());
                  this.ramCandidate = Optional.empty();
               }
            }
         }

      }
   }

   private Vec3 getEdgeOfBlock(BlockPos p_147755_, BlockPos p_147756_) {
      double d0 = 0.5D;
      double d1 = 0.5D * (double)Mth.sign((double)(p_147756_.getX() - p_147755_.getX()));
      double d2 = 0.5D * (double)Mth.sign((double)(p_147756_.getZ() - p_147755_.getZ()));
      return Vec3.atBottomCenterOf(p_147756_).add(d1, 0.0D, d2);
   }

   private Optional<BlockPos> calculateRammingStartPosition(PathfinderMob p_147743_, LivingEntity p_147744_) {
      BlockPos blockpos = p_147744_.blockPosition();
      if (!this.isWalkableBlock(p_147743_, blockpos)) {
         return Optional.empty();
      } else {
         List<BlockPos> list = Lists.newArrayList();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = blockpos.mutable();

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            blockpos$mutableblockpos.set(blockpos);

            for(int i = 0; i < this.maxRamDistance; ++i) {
               if (!this.isWalkableBlock(p_147743_, blockpos$mutableblockpos.move(direction))) {
                  blockpos$mutableblockpos.move(direction.getOpposite());
                  break;
               }
            }

            if (blockpos$mutableblockpos.distManhattan(blockpos) >= this.minRamDistance) {
               list.add(blockpos$mutableblockpos.immutable());
            }
         }

         PathNavigation pathnavigation = p_147743_.getNavigation();
         return list.stream().sorted(Comparator.comparingDouble(p_147743_.blockPosition()::distSqr)).filter((p_147753_) -> {
            Path path = pathnavigation.createPath(p_147753_, 0);
            return path != null && path.canReach();
         }).findFirst();
      }
   }

   private boolean isWalkableBlock(PathfinderMob p_147746_, BlockPos p_147747_) {
      return p_147746_.getNavigation().isStableDestination(p_147747_) && p_147746_.getPathfindingMalus(WalkNodeEvaluator.getBlockPathTypeStatic(p_147746_.level, p_147747_.mutable())) == 0.0F;
   }

   private void chooseRamPosition(PathfinderMob p_147766_, LivingEntity p_147767_) {
      this.reachedRamPositionTimestamp = Optional.empty();
      this.ramCandidate = this.calculateRammingStartPosition(p_147766_, p_147767_).map((p_147741_) -> {
         return new PrepareRamNearestTarget.RamCandidate(p_147741_, p_147767_.blockPosition(), p_147767_);
      });
   }

   public static class RamCandidate {
      private final BlockPos startPosition;
      private final BlockPos targetPosition;
      final LivingEntity target;

      public RamCandidate(BlockPos p_147794_, BlockPos p_147795_, LivingEntity p_147796_) {
         this.startPosition = p_147794_;
         this.targetPosition = p_147795_;
         this.target = p_147796_;
      }

      public BlockPos getStartPosition() {
         return this.startPosition;
      }

      public BlockPos getTargetPosition() {
         return this.targetPosition;
      }

      public LivingEntity getTarget() {
         return this.target;
      }
   }
}