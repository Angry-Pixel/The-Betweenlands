package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession extends Behavior<Villager> {
   public ResetProfession() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23773_, Villager p_23774_) {
      VillagerData villagerdata = p_23774_.getVillagerData();
      return villagerdata.getProfession() != VillagerProfession.NONE && villagerdata.getProfession() != VillagerProfession.NITWIT && p_23774_.getVillagerXp() == 0 && villagerdata.getLevel() <= 1;
   }

   protected void start(ServerLevel p_23776_, Villager p_23777_, long p_23778_) {
      p_23777_.setVillagerData(p_23777_.getVillagerData().setProfession(VillagerProfession.NONE));
      p_23777_.refreshBrain(p_23776_);
   }
}