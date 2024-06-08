package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class GoToPotentialJobSite extends Behavior<Villager> {
   private static final int TICKS_UNTIL_TIMEOUT = 1200;
   final float speedModifier;

   public GoToPotentialJobSite(float p_23098_) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT), 1200);
      this.speedModifier = p_23098_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23103_, Villager p_23104_) {
      return p_23104_.getBrain().getActiveNonCoreActivity().map((p_23115_) -> {
         return p_23115_ == Activity.IDLE || p_23115_ == Activity.WORK || p_23115_ == Activity.PLAY;
      }).orElse(true);
   }

   protected boolean canStillUse(ServerLevel p_23106_, Villager p_23107_, long p_23108_) {
      return p_23107_.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
   }

   protected void tick(ServerLevel p_23121_, Villager p_23122_, long p_23123_) {
      BehaviorUtils.setWalkAndLookTargetMemories(p_23122_, p_23122_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos(), this.speedModifier, 1);
   }

   protected void stop(ServerLevel p_23129_, Villager p_23130_, long p_23131_) {
      Optional<GlobalPos> optional = p_23130_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      optional.ifPresent((p_23111_) -> {
         BlockPos blockpos = p_23111_.pos();
         ServerLevel serverlevel = p_23129_.getServer().getLevel(p_23111_.dimension());
         if (serverlevel != null) {
            PoiManager poimanager = serverlevel.getPoiManager();
            if (poimanager.exists(blockpos, (p_147557_) -> {
               return true;
            })) {
               poimanager.release(blockpos);
            }

            DebugPackets.sendPoiTicketCountPacket(p_23129_, blockpos);
         }
      });
      p_23130_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}