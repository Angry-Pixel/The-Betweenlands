package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.pathfinder.Path;

public class YieldJobSite extends Behavior<Villager> {
   private final float speedModifier;

   public YieldJobSite(float p_24835_) {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.speedModifier = p_24835_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24844_, Villager p_24845_) {
      if (p_24845_.isBaby()) {
         return false;
      } else {
         return p_24845_.getVillagerData().getProfession() == VillagerProfession.NONE;
      }
   }

   protected void start(ServerLevel p_24847_, Villager p_24848_, long p_24849_) {
      BlockPos blockpos = p_24848_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos();
      Optional<PoiType> optional = p_24847_.getPoiManager().getType(blockpos);
      if (optional.isPresent()) {
         BehaviorUtils.getNearbyVillagersWithCondition(p_24848_, (p_24874_) -> {
            return this.nearbyWantsJobsite(optional.get(), p_24874_, blockpos);
         }).findFirst().ifPresent((p_24860_) -> {
            this.yieldJobSite(p_24847_, p_24848_, p_24860_, blockpos, p_24860_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent());
         });
      }
   }

   private boolean nearbyWantsJobsite(PoiType p_24862_, Villager p_24863_, BlockPos p_24864_) {
      boolean flag = p_24863_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent();
      if (flag) {
         return false;
      } else {
         Optional<GlobalPos> optional = p_24863_.getBrain().getMemory(MemoryModuleType.JOB_SITE);
         VillagerProfession villagerprofession = p_24863_.getVillagerData().getProfession();
         if (p_24863_.getVillagerData().getProfession() != VillagerProfession.NONE && villagerprofession.getJobPoiType().getPredicate().test(p_24862_)) {
            return !optional.isPresent() ? this.canReachPos(p_24863_, p_24864_, p_24862_) : optional.get().pos().equals(p_24864_);
         } else {
            return false;
         }
      }
   }

   private void yieldJobSite(ServerLevel p_24851_, Villager p_24852_, Villager p_24853_, BlockPos p_24854_, boolean p_24855_) {
      this.eraseMemories(p_24852_);
      if (!p_24855_) {
         BehaviorUtils.setWalkAndLookTargetMemories(p_24853_, p_24854_, this.speedModifier, 1);
         p_24853_.getBrain().setMemory(MemoryModuleType.POTENTIAL_JOB_SITE, GlobalPos.of(p_24851_.dimension(), p_24854_));
         DebugPackets.sendPoiTicketCountPacket(p_24851_, p_24854_);
      }

   }

   private boolean canReachPos(Villager p_24868_, BlockPos p_24869_, PoiType p_24870_) {
      Path path = p_24868_.getNavigation().createPath(p_24869_, p_24870_.getValidRange());
      return path != null && path.canReach();
   }

   private void eraseMemories(Villager p_24866_) {
      p_24866_.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
      p_24866_.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
      p_24866_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
   }
}