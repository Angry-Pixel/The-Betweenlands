package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite extends Behavior<Villager> {
   public AssignProfessionFromJobSite() {
      super(ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22450_, Villager p_22451_) {
      BlockPos blockpos = p_22451_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().pos();
      return blockpos.closerToCenterThan(p_22451_.position(), 2.0D) || p_22451_.assignProfessionWhenSpawned();
   }

   protected void start(ServerLevel p_22453_, Villager p_22454_, long p_22455_) {
      GlobalPos globalpos = p_22454_.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get();
      p_22454_.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
      p_22454_.getBrain().setMemory(MemoryModuleType.JOB_SITE, globalpos);
      p_22453_.broadcastEntityEvent(p_22454_, (byte)14);
      if (p_22454_.getVillagerData().getProfession() == VillagerProfession.NONE) {
         MinecraftServer minecraftserver = p_22453_.getServer();
         Optional.ofNullable(minecraftserver.getLevel(globalpos.dimension())).flatMap((p_22467_) -> {
            return p_22467_.getPoiManager().getType(globalpos.pos());
         }).flatMap((p_22457_) -> {
            return Registry.VILLAGER_PROFESSION.stream().filter((p_147412_) -> {
               return p_147412_.getJobPoiType() == p_22457_;
            }).findFirst();
         }).ifPresent((p_22464_) -> {
            p_22454_.setVillagerData(p_22454_.getVillagerData().setProfession(p_22464_));
            p_22454_.refreshBrain(p_22453_);
         });
      }
   }
}