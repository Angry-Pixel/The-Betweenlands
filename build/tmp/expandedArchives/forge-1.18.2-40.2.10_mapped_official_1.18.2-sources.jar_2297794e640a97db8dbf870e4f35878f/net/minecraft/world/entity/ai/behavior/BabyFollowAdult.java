package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class BabyFollowAdult<E extends AgeableMob> extends Behavior<E> {
   private final UniformInt followRange;
   private final Function<LivingEntity, Float> speedModifier;

   public BabyFollowAdult(UniformInt p_147414_, float p_147415_) {
      this(p_147414_, (p_147421_) -> {
         return p_147415_;
      });
   }

   public BabyFollowAdult(UniformInt p_147417_, Function<LivingEntity, Float> p_147418_) {
      super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.followRange = p_147417_;
      this.speedModifier = p_147418_;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_147423_, E p_147424_) {
      if (!p_147424_.isBaby()) {
         return false;
      } else {
         AgeableMob ageablemob = this.getNearestAdult(p_147424_);
         return p_147424_.closerThan(ageablemob, (double)(this.followRange.getMaxValue() + 1)) && !p_147424_.closerThan(ageablemob, (double)this.followRange.getMinValue());
      }
   }

   protected void start(ServerLevel p_147426_, E p_147427_, long p_147428_) {
      BehaviorUtils.setWalkAndLookTargetMemories(p_147427_, this.getNearestAdult(p_147427_), this.speedModifier.apply(p_147427_), this.followRange.getMinValue() - 1);
   }

   private AgeableMob getNearestAdult(E p_147430_) {
      return p_147430_.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
   }
}