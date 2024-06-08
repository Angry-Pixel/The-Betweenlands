package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

public class FollowTemptation extends Behavior<PathfinderMob> {
   public static final int TEMPTATION_COOLDOWN = 100;
   public static final double CLOSE_ENOUGH_DIST = 2.5D;
   private final Function<LivingEntity, Float> speedModifier;

   public FollowTemptation(Function<LivingEntity, Float> p_147486_) {
      super(Util.make(() -> {
         Builder<MemoryModuleType<?>, MemoryStatus> builder = ImmutableMap.builder();
         builder.put(MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED);
         builder.put(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED);
         builder.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT);
         builder.put(MemoryModuleType.IS_TEMPTED, MemoryStatus.REGISTERED);
         builder.put(MemoryModuleType.TEMPTING_PLAYER, MemoryStatus.VALUE_PRESENT);
         builder.put(MemoryModuleType.BREED_TARGET, MemoryStatus.VALUE_ABSENT);
         return builder.build();
      }));
      this.speedModifier = p_147486_;
   }

   protected float getSpeedModifier(PathfinderMob p_147498_) {
      return this.speedModifier.apply(p_147498_);
   }

   private Optional<Player> getTemptingPlayer(PathfinderMob p_147509_) {
      return p_147509_.getBrain().getMemory(MemoryModuleType.TEMPTING_PLAYER);
   }

   protected boolean timedOut(long p_147488_) {
      return false;
   }

   protected boolean canStillUse(ServerLevel p_147494_, PathfinderMob p_147495_, long p_147496_) {
      return this.getTemptingPlayer(p_147495_).isPresent() && !p_147495_.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET);
   }

   protected void start(ServerLevel p_147505_, PathfinderMob p_147506_, long p_147507_) {
      p_147506_.getBrain().setMemory(MemoryModuleType.IS_TEMPTED, true);
   }

   protected void stop(ServerLevel p_147515_, PathfinderMob p_147516_, long p_147517_) {
      Brain<?> brain = p_147516_.getBrain();
      brain.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 100);
      brain.setMemory(MemoryModuleType.IS_TEMPTED, false);
      brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel p_147523_, PathfinderMob p_147524_, long p_147525_) {
      Player player = this.getTemptingPlayer(p_147524_).get();
      Brain<?> brain = p_147524_.getBrain();
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(player, true));
      if (p_147524_.distanceToSqr(player) < 6.25D) {
         brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      } else {
         brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(player, false), this.getSpeedModifier(p_147524_), 2));
      }

   }
}