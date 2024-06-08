package net.minecraft.world.entity.animal.axolotl;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class PlayDead extends Behavior<Axolotl> {
   public PlayDead() {
      super(ImmutableMap.of(MemoryModuleType.PLAY_DEAD_TICKS, MemoryStatus.VALUE_PRESENT, MemoryModuleType.HURT_BY_ENTITY, MemoryStatus.VALUE_PRESENT), 200);
   }

   protected boolean checkExtraStartConditions(ServerLevel p_149319_, Axolotl p_149320_) {
      return p_149320_.isInWaterOrBubble();
   }

   protected boolean canStillUse(ServerLevel p_149322_, Axolotl p_149323_, long p_149324_) {
      return p_149323_.isInWaterOrBubble() && p_149323_.getBrain().hasMemoryValue(MemoryModuleType.PLAY_DEAD_TICKS);
   }

   protected void start(ServerLevel p_149330_, Axolotl p_149331_, long p_149332_) {
      Brain<Axolotl> brain = p_149331_.getBrain();
      brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
      p_149331_.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
   }
}