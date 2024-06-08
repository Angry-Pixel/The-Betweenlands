package net.minecraft.world.entity.monster;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableWitchTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestHealableRaiderTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Witch extends Raider implements RangedAttackMob {
   private static final UUID SPEED_MODIFIER_DRINKING_UUID = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
   private static final AttributeModifier SPEED_MODIFIER_DRINKING = new AttributeModifier(SPEED_MODIFIER_DRINKING_UUID, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION);
   private static final EntityDataAccessor<Boolean> DATA_USING_ITEM = SynchedEntityData.defineId(Witch.class, EntityDataSerializers.BOOLEAN);
   private int usingTime;
   private NearestHealableRaiderTargetGoal<Raider> healRaidersGoal;
   private NearestAttackableWitchTargetGoal<Player> attackPlayersGoal;

   public Witch(EntityType<? extends Witch> p_34134_, Level p_34135_) {
      super(p_34134_, p_34135_);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.healRaidersGoal = new NearestHealableRaiderTargetGoal<>(this, Raider.class, true, (p_34159_) -> {
         return p_34159_ != null && this.hasActiveRaid() && p_34159_.getType() != EntityType.WITCH;
      });
      this.attackPlayersGoal = new NearestAttackableWitchTargetGoal<>(this, Player.class, 10, true, false, (Predicate<LivingEntity>)null);
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 60, 10.0F));
      this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Raider.class));
      this.targetSelector.addGoal(2, this.healRaidersGoal);
      this.targetSelector.addGoal(3, this.attackPlayersGoal);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_USING_ITEM, false);
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.WITCH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_34154_) {
      return SoundEvents.WITCH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.WITCH_DEATH;
   }

   public void setUsingItem(boolean p_34164_) {
      this.getEntityData().set(DATA_USING_ITEM, p_34164_);
   }

   public boolean isDrinkingPotion() {
      return this.getEntityData().get(DATA_USING_ITEM);
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 26.0D).add(Attributes.MOVEMENT_SPEED, 0.25D);
   }

   public void aiStep() {
      if (!this.level.isClientSide && this.isAlive()) {
         this.healRaidersGoal.decrementCooldown();
         if (this.healRaidersGoal.getCooldown() <= 0) {
            this.attackPlayersGoal.setCanAttack(true);
         } else {
            this.attackPlayersGoal.setCanAttack(false);
         }

         if (this.isDrinkingPotion()) {
            if (this.usingTime-- <= 0) {
               this.setUsingItem(false);
               ItemStack itemstack = this.getMainHandItem();
               this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               if (itemstack.is(Items.POTION)) {
                  List<MobEffectInstance> list = PotionUtils.getMobEffects(itemstack);
                  if (list != null) {
                     for(MobEffectInstance mobeffectinstance : list) {
                        this.addEffect(new MobEffectInstance(mobeffectinstance));
                     }
                  }
               }

               this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_DRINKING);
            }
         } else {
            Potion potion = null;
            if (this.random.nextFloat() < 0.15F && this.isEyeInFluid(FluidTags.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
               potion = Potions.WATER_BREATHING;
            } else if (this.random.nextFloat() < 0.15F && (this.isOnFire() || this.getLastDamageSource() != null && this.getLastDamageSource().isFire()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
               potion = Potions.FIRE_RESISTANCE;
            } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
               potion = Potions.HEALING;
            } else if (this.random.nextFloat() < 0.5F && this.getTarget() != null && !this.hasEffect(MobEffects.MOVEMENT_SPEED) && this.getTarget().distanceToSqr(this) > 121.0D) {
               potion = Potions.SWIFTNESS;
            }

            if (potion != null) {
               this.setItemSlot(EquipmentSlot.MAINHAND, PotionUtils.setPotion(new ItemStack(Items.POTION), potion));
               this.usingTime = this.getMainHandItem().getUseDuration();
               this.setUsingItem(true);
               if (!this.isSilent()) {
                  this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_DRINK, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
               }

               AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
               attributeinstance.removeModifier(SPEED_MODIFIER_DRINKING);
               attributeinstance.addTransientModifier(SPEED_MODIFIER_DRINKING);
            }
         }

         if (this.random.nextFloat() < 7.5E-4F) {
            this.level.broadcastEntityEvent(this, (byte)15);
         }
      }

      super.aiStep();
   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.WITCH_CELEBRATE;
   }

   public void handleEntityEvent(byte p_34138_) {
      if (p_34138_ == 15) {
         for(int i = 0; i < this.random.nextInt(35) + 10; ++i) {
            this.level.addParticle(ParticleTypes.WITCH, this.getX() + this.random.nextGaussian() * (double)0.13F, this.getBoundingBox().maxY + 0.5D + this.random.nextGaussian() * (double)0.13F, this.getZ() + this.random.nextGaussian() * (double)0.13F, 0.0D, 0.0D, 0.0D);
         }
      } else {
         super.handleEntityEvent(p_34138_);
      }

   }

   protected float getDamageAfterMagicAbsorb(DamageSource p_34149_, float p_34150_) {
      p_34150_ = super.getDamageAfterMagicAbsorb(p_34149_, p_34150_);
      if (p_34149_.getEntity() == this) {
         p_34150_ = 0.0F;
      }

      if (p_34149_.isMagic()) {
         p_34150_ *= 0.15F;
      }

      return p_34150_;
   }

   public void performRangedAttack(LivingEntity p_34143_, float p_34144_) {
      if (!this.isDrinkingPotion()) {
         Vec3 vec3 = p_34143_.getDeltaMovement();
         double d0 = p_34143_.getX() + vec3.x - this.getX();
         double d1 = p_34143_.getEyeY() - (double)1.1F - this.getY();
         double d2 = p_34143_.getZ() + vec3.z - this.getZ();
         double d3 = Math.sqrt(d0 * d0 + d2 * d2);
         Potion potion = Potions.HARMING;
         if (p_34143_ instanceof Raider) {
            if (p_34143_.getHealth() <= 4.0F) {
               potion = Potions.HEALING;
            } else {
               potion = Potions.REGENERATION;
            }

            this.setTarget((LivingEntity)null);
         } else if (d3 >= 8.0D && !p_34143_.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
            potion = Potions.SLOWNESS;
         } else if (p_34143_.getHealth() >= 8.0F && !p_34143_.hasEffect(MobEffects.POISON)) {
            potion = Potions.POISON;
         } else if (d3 <= 3.0D && !p_34143_.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
            potion = Potions.WEAKNESS;
         }

         ThrownPotion thrownpotion = new ThrownPotion(this.level, this);
         thrownpotion.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
         thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
         thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
         if (!this.isSilent()) {
            this.level.playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
         }

         this.level.addFreshEntity(thrownpotion);
      }
   }

   protected float getStandingEyeHeight(Pose p_34146_, EntityDimensions p_34147_) {
      return 1.62F;
   }

   public void applyRaidBuffs(int p_34140_, boolean p_34141_) {
   }

   public boolean canBeLeader() {
      return false;
   }
}