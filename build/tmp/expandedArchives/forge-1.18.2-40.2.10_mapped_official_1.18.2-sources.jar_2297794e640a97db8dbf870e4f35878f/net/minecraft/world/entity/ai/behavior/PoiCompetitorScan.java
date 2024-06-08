package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class PoiCompetitorScan extends Behavior<Villager> {
   final VillagerProfession profession;

   public PoiCompetitorScan(VillagerProfession p_23710_) {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.profession = p_23710_;
   }

   protected void start(ServerLevel p_23716_, Villager p_23717_, long p_23718_) {
      GlobalPos globalpos = p_23717_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      p_23716_.getPoiManager().getType(globalpos.pos()).ifPresent((p_23730_) -> {
         BehaviorUtils.getNearbyVillagersWithCondition(p_23717_, (p_147712_) -> {
            return this.competesForSameJobsite(globalpos, p_23730_, p_147712_);
         }).reduce(p_23717_, PoiCompetitorScan::selectWinner);
      });
   }

   private static Villager selectWinner(Villager p_23725_, Villager p_23726_) {
      Villager villager;
      Villager villager1;
      if (p_23725_.getVillagerXp() > p_23726_.getVillagerXp()) {
         villager = p_23725_;
         villager1 = p_23726_;
      } else {
         villager = p_23726_;
         villager1 = p_23725_;
      }

      villager1.getBrain().eraseMemory(MemoryModuleType.JOB_SITE);
      return villager;
   }

   private boolean competesForSameJobsite(GlobalPos p_23732_, PoiType p_23733_, Villager p_23734_) {
      return this.hasJobSite(p_23734_) && p_23732_.equals(p_23734_.getBrain().getMemory(MemoryModuleType.JOB_SITE).get()) && this.hasMatchingProfession(p_23733_, p_23734_.getVillagerData().getProfession());
   }

   private boolean hasMatchingProfession(PoiType p_23720_, VillagerProfession p_23721_) {
      return p_23721_.getJobPoiType().getPredicate().test(p_23720_);
   }

   private boolean hasJobSite(Villager p_23723_) {
      return p_23723_.getBrain().getMemory(MemoryModuleType.JOB_SITE).isPresent();
   }
}