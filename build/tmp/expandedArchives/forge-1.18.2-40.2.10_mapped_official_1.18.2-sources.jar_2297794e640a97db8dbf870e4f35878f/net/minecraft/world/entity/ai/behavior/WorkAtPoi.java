package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi extends Behavior<Villager> {
   private static final int CHECK_COOLDOWN = 300;
   private static final double DISTANCE = 1.73D;
   private long lastCheck;

   public WorkAtPoi() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24827_, Villager p_24828_) {
      if (p_24827_.getGameTime() - this.lastCheck < 300L) {
         return false;
      } else if (p_24827_.random.nextInt(2) != 0) {
         return false;
      } else {
         this.lastCheck = p_24827_.getGameTime();
         GlobalPos globalpos = p_24828_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
         return globalpos.dimension() == p_24827_.dimension() && globalpos.pos().closerToCenterThan(p_24828_.position(), 1.73D);
      }
   }

   protected void start(ServerLevel p_24816_, Villager p_24817_, long p_24818_) {
      Brain<Villager> brain = p_24817_.getBrain();
      brain.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, p_24818_);
      brain.getMemory(MemoryModuleType.JOB_SITE).ifPresent((p_24821_) -> {
         brain.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker(p_24821_.pos()));
      });
      p_24817_.playWorkSound();
      this.useWorkstation(p_24816_, p_24817_);
      if (p_24817_.shouldRestock()) {
         p_24817_.restock();
      }

   }

   protected void useWorkstation(ServerLevel p_24813_, Villager p_24814_) {
   }

   protected boolean canStillUse(ServerLevel p_24830_, Villager p_24831_, long p_24832_) {
      Optional<GlobalPos> optional = p_24831_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
      if (!optional.isPresent()) {
         return false;
      } else {
         GlobalPos globalpos = optional.get();
         return globalpos.dimension() == p_24830_.dimension() && globalpos.pos().closerToCenterThan(p_24831_.position(), 1.73D);
      }
   }
}