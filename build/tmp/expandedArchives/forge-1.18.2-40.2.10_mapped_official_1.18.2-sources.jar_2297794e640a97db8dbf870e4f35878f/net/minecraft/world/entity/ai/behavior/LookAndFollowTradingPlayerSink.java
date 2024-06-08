package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class LookAndFollowTradingPlayerSink extends Behavior<Villager> {
   private final float speedModifier;

   public LookAndFollowTradingPlayerSink(float p_23434_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
      this.speedModifier = p_23434_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23445_, Villager p_23446_) {
      Player player = p_23446_.getTradingPlayer();
      return p_23446_.isAlive() && player != null && !p_23446_.isInWater() && !p_23446_.hurtMarked && p_23446_.distanceToSqr(player) <= 16.0D && player.containerMenu != null;
   }

   protected boolean canStillUse(ServerLevel p_23448_, Villager p_23449_, long p_23450_) {
      return this.checkExtraStartConditions(p_23448_, p_23449_);
   }

   protected void start(ServerLevel p_23458_, Villager p_23459_, long p_23460_) {
      this.followPlayer(p_23459_);
   }

   protected void stop(ServerLevel p_23466_, Villager p_23467_, long p_23468_) {
      Brain<?> brain = p_23467_.getBrain();
      brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel p_23474_, Villager p_23475_, long p_23476_) {
      this.followPlayer(p_23475_);
   }

   protected boolean timedOut(long p_23436_) {
      return false;
   }

   private void followPlayer(Villager p_23452_) {
      Brain<?> brain = p_23452_.getBrain();
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker(p_23452_.getTradingPlayer(), false), this.speedModifier, 2));
      brain.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(p_23452_.getTradingPlayer(), true));
   }
}