package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class Endermite extends Monster {
   private static final int MAX_LIFE = 2400;
   private int life;

   public Endermite(EntityType<? extends Endermite> p_32591_, Level p_32592_) {
      super(p_32591_, p_32592_);
      this.xpReward = 3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
   }

   protected float getStandingEyeHeight(Pose p_32604_, EntityDimensions p_32605_) {
      return 0.13F;
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.ATTACK_DAMAGE, 2.0D);
   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_32615_) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos p_32607_, BlockState p_32608_) {
      this.playSound(SoundEvents.ENDERMITE_STEP, 0.15F, 1.0F);
   }

   public void readAdditionalSaveData(CompoundTag p_32595_) {
      super.readAdditionalSaveData(p_32595_);
      this.life = p_32595_.getInt("Lifetime");
   }

   public void addAdditionalSaveData(CompoundTag p_32610_) {
      super.addAdditionalSaveData(p_32610_);
      p_32610_.putInt("Lifetime", this.life);
   }

   public void tick() {
      this.yBodyRot = this.getYRot();
      super.tick();
   }

   public void setYBodyRot(float p_32621_) {
      this.setYRot(p_32621_);
      super.setYBodyRot(p_32621_);
   }

   public double getMyRidingOffset() {
      return 0.1D;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         for(int i = 0; i < 2; ++i) {
            this.level.addParticle(ParticleTypes.PORTAL, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if (!this.isPersistenceRequired()) {
            ++this.life;
         }

         if (this.life >= 2400) {
            this.discard();
         }
      }

   }

   public static boolean checkEndermiteSpawnRules(EntityType<Endermite> p_32598_, LevelAccessor p_32599_, MobSpawnType p_32600_, BlockPos p_32601_, Random p_32602_) {
      if (checkAnyLightMonsterSpawnRules(p_32598_, p_32599_, p_32600_, p_32601_, p_32602_)) {
         Player player = p_32599_.getNearestPlayer((double)p_32601_.getX() + 0.5D, (double)p_32601_.getY() + 0.5D, (double)p_32601_.getZ() + 0.5D, 5.0D, true);
         return player == null;
      } else {
         return false;
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }
}