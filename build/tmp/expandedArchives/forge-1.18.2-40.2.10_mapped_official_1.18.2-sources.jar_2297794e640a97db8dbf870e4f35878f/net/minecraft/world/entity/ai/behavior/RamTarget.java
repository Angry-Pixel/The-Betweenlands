package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RamTarget<E extends PathfinderMob> extends Behavior<E> {
   public static final int TIME_OUT_DURATION = 200;
   public static final float RAM_SPEED_FORCE_FACTOR = 1.65F;
   private final Function<E, UniformInt> getTimeBetweenRams;
   private final TargetingConditions ramTargeting;
   private final float speed;
   private final ToDoubleFunction<E> getKnockbackForce;
   private Vec3 ramDirection;
   private final Function<E, SoundEvent> getImpactSound;

   public RamTarget(Function<E, UniformInt> p_182335_, TargetingConditions p_182336_, float p_182337_, ToDoubleFunction<E> p_182338_, Function<E, SoundEvent> p_182339_) {
      super(ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, MemoryStatus.VALUE_ABSENT, MemoryModuleType.RAM_TARGET, MemoryStatus.VALUE_PRESENT), 200);
      this.getTimeBetweenRams = p_182335_;
      this.ramTargeting = p_182336_;
      this.speed = p_182337_;
      this.getKnockbackForce = p_182338_;
      this.getImpactSound = p_182339_;
      this.ramDirection = Vec3.ZERO;
   }

   protected boolean checkExtraStartConditions(ServerLevel p_147824_, PathfinderMob p_147825_) {
      return p_147825_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected boolean canStillUse(ServerLevel p_147827_, PathfinderMob p_147828_, long p_147829_) {
      return p_147828_.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
   }

   protected void start(ServerLevel p_147838_, PathfinderMob p_147839_, long p_147840_) {
      BlockPos blockpos = p_147839_.blockPosition();
      Brain<?> brain = p_147839_.getBrain();
      Vec3 vec3 = brain.getMemory(MemoryModuleType.RAM_TARGET).get();
      this.ramDirection = (new Vec3((double)blockpos.getX() - vec3.x(), 0.0D, (double)blockpos.getZ() - vec3.z())).normalize();
      brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(vec3, this.speed, 0));
   }

   protected void tick(ServerLevel p_147842_, E p_147843_, long p_147844_) {
      List<LivingEntity> list = p_147842_.getNearbyEntities(LivingEntity.class, this.ramTargeting, p_147843_, p_147843_.getBoundingBox());
      Brain<?> brain = p_147843_.getBrain();
      if (!list.isEmpty()) {
         LivingEntity livingentity = list.get(0);
         livingentity.hurt(DamageSource.mobAttack(p_147843_).setNoAggro(), (float)p_147843_.getAttributeValue(Attributes.ATTACK_DAMAGE));
         int i = p_147843_.hasEffect(MobEffects.MOVEMENT_SPEED) ? p_147843_.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
         int j = p_147843_.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? p_147843_.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
         float f = 0.25F * (float)(i - j);
         float f1 = Mth.clamp(p_147843_.getSpeed() * 1.65F, 0.2F, 3.0F) + f;
         float f2 = livingentity.isDamageSourceBlocked(DamageSource.mobAttack(p_147843_)) ? 0.5F : 1.0F;
         livingentity.knockback((double)(f2 * f1) * this.getKnockbackForce.applyAsDouble(p_147843_), this.ramDirection.x(), this.ramDirection.z());
         this.finishRam(p_147842_, p_147843_);
         p_147842_.playSound((Player)null, p_147843_, this.getImpactSound.apply(p_147843_), SoundSource.HOSTILE, 1.0F, 1.0F);
      } else {
         Optional<WalkTarget> optional = brain.getMemory(MemoryModuleType.WALK_TARGET);
         Optional<Vec3> optional1 = brain.getMemory(MemoryModuleType.RAM_TARGET);
         boolean flag = !optional.isPresent() || !optional1.isPresent() || optional.get().getTarget().currentPosition().distanceTo(optional1.get()) < 0.25D;
         if (flag) {
            this.finishRam(p_147842_, p_147843_);
         }
      }

   }

   protected void finishRam(ServerLevel p_147835_, E p_147836_) {
      p_147835_.broadcastEntityEvent(p_147836_, (byte)59);
      p_147836_.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, this.getTimeBetweenRams.apply(p_147836_).sample(p_147835_.random));
      p_147836_.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
   }
}