package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;

public class StrollToPoiList extends Behavior<Villager> {
   private final MemoryModuleType<List<GlobalPos>> strollToMemoryType;
   private final MemoryModuleType<GlobalPos> mustBeCloseToMemoryType;
   private final float speedModifier;
   private final int closeEnoughDist;
   private final int maxDistanceFromPoi;
   private long nextOkStartTime;
   @Nullable
   private GlobalPos targetPos;

   public StrollToPoiList(MemoryModuleType<List<GlobalPos>> p_24362_, float p_24363_, int p_24364_, int p_24365_, MemoryModuleType<GlobalPos> p_24366_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, p_24362_, MemoryStatus.VALUE_PRESENT, p_24366_, MemoryStatus.VALUE_PRESENT));
      this.strollToMemoryType = p_24362_;
      this.speedModifier = p_24363_;
      this.closeEnoughDist = p_24364_;
      this.maxDistanceFromPoi = p_24365_;
      this.mustBeCloseToMemoryType = p_24366_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_24375_, Villager p_24376_) {
      Optional<List<GlobalPos>> optional = p_24376_.getBrain().getMemory(this.strollToMemoryType);
      Optional<GlobalPos> optional1 = p_24376_.getBrain().getMemory(this.mustBeCloseToMemoryType);
      if (optional.isPresent() && optional1.isPresent()) {
         List<GlobalPos> list = optional.get();
         if (!list.isEmpty()) {
            this.targetPos = list.get(p_24375_.getRandom().nextInt(list.size()));
            return this.targetPos != null && p_24375_.dimension() == this.targetPos.dimension() && optional1.get().pos().closerToCenterThan(p_24376_.position(), (double)this.maxDistanceFromPoi);
         }
      }

      return false;
   }

   protected void start(ServerLevel p_24378_, Villager p_24379_, long p_24380_) {
      if (p_24380_ > this.nextOkStartTime && this.targetPos != null) {
         p_24379_.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.targetPos.pos(), this.speedModifier, this.closeEnoughDist));
         this.nextOkStartTime = p_24380_ + 100L;
      }

   }
}