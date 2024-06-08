package net.minecraft.world.entity.ai.sensing;

import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public abstract class Sensor<E extends LivingEntity> {
   private static final Random RANDOM = new Random();
   private static final int DEFAULT_SCAN_RATE = 20;
   protected static final int TARGETING_RANGE = 16;
   private static final TargetingConditions TARGET_CONDITIONS = TargetingConditions.forNonCombat().range(16.0D);
   private static final TargetingConditions TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forNonCombat().range(16.0D).ignoreInvisibilityTesting();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS = TargetingConditions.forCombat().range(16.0D);
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forCombat().range(16.0D).ignoreInvisibilityTesting();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0D).ignoreLineOfSight();
   private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();
   private final int scanRate;
   private long timeToTick;

   public Sensor(int p_26800_) {
      this.scanRate = p_26800_;
      this.timeToTick = (long)RANDOM.nextInt(p_26800_);
   }

   public Sensor() {
      this(20);
   }

   public final void tick(ServerLevel p_26807_, E p_26808_) {
      if (--this.timeToTick <= 0L) {
         this.timeToTick = (long)this.scanRate;
         this.doTick(p_26807_, p_26808_);
      }

   }

   protected abstract void doTick(ServerLevel p_26801_, E p_26802_);

   public abstract Set<MemoryModuleType<?>> requires();

   public static boolean isEntityTargetable(LivingEntity p_26804_, LivingEntity p_26805_) {
      return p_26804_.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, p_26805_) ? TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(p_26804_, p_26805_) : TARGET_CONDITIONS.test(p_26804_, p_26805_);
   }

   public static boolean isEntityAttackable(LivingEntity p_148313_, LivingEntity p_148314_) {
      return p_148313_.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, p_148314_) ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test(p_148313_, p_148314_) : ATTACK_TARGET_CONDITIONS.test(p_148313_, p_148314_);
   }

   public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity p_182378_, LivingEntity p_182379_) {
      return p_182378_.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, p_182379_) ? ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test(p_182378_, p_182379_) : ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test(p_182378_, p_182379_);
   }
}