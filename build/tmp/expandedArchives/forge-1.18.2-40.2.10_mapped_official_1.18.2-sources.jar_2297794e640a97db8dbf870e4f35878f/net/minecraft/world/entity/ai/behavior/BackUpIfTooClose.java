package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BackUpIfTooClose<E extends Mob> extends Behavior<E> {
   private final int tooCloseDistance;
   private final float strafeSpeed;

   public BackUpIfTooClose(int p_22492_, float p_22493_) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryStatus.VALUE_PRESENT));
      this.tooCloseDistance = p_22492_;
      this.strafeSpeed = p_22493_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_22502_, E p_22503_) {
      return this.isTargetVisible(p_22503_) && this.isTargetTooClose(p_22503_);
   }

   protected void start(ServerLevel p_22505_, E p_22506_, long p_22507_) {
      p_22506_.getBrain().setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker(this.getTarget(p_22506_), true));
      p_22506_.getMoveControl().strafe(-this.strafeSpeed, 0.0F);
      p_22506_.setYRot(Mth.rotateIfNecessary(p_22506_.getYRot(), p_22506_.yHeadRot, 0.0F));
   }

   private boolean isTargetVisible(E p_22509_) {
      return p_22509_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get().contains(this.getTarget(p_22509_));
   }

   private boolean isTargetTooClose(E p_22511_) {
      return this.getTarget(p_22511_).closerThan(p_22511_, (double)this.tooCloseDistance);
   }

   private LivingEntity getTarget(E p_22513_) {
      return p_22513_.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get();
   }
}