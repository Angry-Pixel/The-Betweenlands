package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class LocateHidingPlace extends Behavior<LivingEntity> {
   private final float speedModifier;
   private final int radius;
   private final int closeEnoughDist;
   private Optional<BlockPos> currentPos = Optional.empty();

   public LocateHidingPlace(int p_23408_, float p_23409_, int p_23410_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.HOME, MemoryStatus.REGISTERED, MemoryModuleType.HIDING_PLACE, MemoryStatus.REGISTERED));
      this.radius = p_23408_;
      this.speedModifier = p_23409_;
      this.closeEnoughDist = p_23410_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_23412_, LivingEntity p_23413_) {
      Optional<BlockPos> optional = p_23412_.getPoiManager().find((p_23423_) -> {
         return p_23423_ == PoiType.HOME;
      }, (p_23425_) -> {
         return true;
      }, p_23413_.blockPosition(), this.closeEnoughDist + 1, PoiManager.Occupancy.ANY);
      if (optional.isPresent() && optional.get().closerToCenterThan(p_23413_.position(), (double)this.closeEnoughDist)) {
         this.currentPos = optional;
      } else {
         this.currentPos = Optional.empty();
      }

      return true;
   }

   protected void start(ServerLevel p_23415_, LivingEntity p_23416_, long p_23417_) {
      Brain<?> brain = p_23416_.getBrain();
      Optional<BlockPos> optional = this.currentPos;
      if (!optional.isPresent()) {
         optional = p_23415_.getPoiManager().getRandom((p_23419_) -> {
            return p_23419_ == PoiType.HOME;
         }, (p_23421_) -> {
            return true;
         }, PoiManager.Occupancy.ANY, p_23416_.blockPosition(), this.radius, p_23416_.getRandom());
         if (!optional.isPresent()) {
            Optional<GlobalPos> optional1 = brain.getMemory(MemoryModuleType.HOME);
            if (optional1.isPresent()) {
               optional = Optional.of(optional1.get().pos());
            }
         }
      }

      if (optional.isPresent()) {
         brain.eraseMemory(MemoryModuleType.PATH);
         brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
         brain.eraseMemory(MemoryModuleType.BREED_TARGET);
         brain.eraseMemory(MemoryModuleType.INTERACTION_TARGET);
         brain.setMemory(MemoryModuleType.HIDING_PLACE, GlobalPos.of(p_23415_.dimension(), optional.get()));
         if (!optional.get().closerToCenterThan(p_23416_.position(), (double)this.closeEnoughDist)) {
            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(optional.get(), this.speedModifier, this.closeEnoughDist));
         }
      }

   }
}