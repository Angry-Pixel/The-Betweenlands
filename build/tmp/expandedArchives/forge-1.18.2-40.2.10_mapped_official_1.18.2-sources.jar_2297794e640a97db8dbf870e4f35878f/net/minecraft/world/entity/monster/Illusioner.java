package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Illusioner extends SpellcasterIllager implements RangedAttackMob {
   private static final int NUM_ILLUSIONS = 4;
   private static final int ILLUSION_TRANSITION_TICKS = 3;
   private static final int ILLUSION_SPREAD = 3;
   private int clientSideIllusionTicks;
   private final Vec3[][] clientSideIllusionOffsets;

   public Illusioner(EntityType<? extends Illusioner> p_32911_, Level p_32912_) {
      super(p_32911_, p_32912_);
      this.xpReward = 5;
      this.clientSideIllusionOffsets = new Vec3[2][4];

      for(int i = 0; i < 4; ++i) {
         this.clientSideIllusionOffsets[0][i] = Vec3.ZERO;
         this.clientSideIllusionOffsets[1][i] = Vec3.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SpellcasterIllager.SpellcasterCastingSpellGoal());
      this.goalSelector.addGoal(4, new Illusioner.IllusionerMirrorSpellGoal());
      this.goalSelector.addGoal(5, new Illusioner.IllusionerBlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5D, 20, 15.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolem.class, false)).setUnseenMemoryTicks(300));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.5D).add(Attributes.FOLLOW_RANGE, 18.0D).add(Attributes.MAX_HEALTH, 32.0D);
   }

   public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_32921_, DifficultyInstance p_32922_, MobSpawnType p_32923_, @Nullable SpawnGroupData p_32924_, @Nullable CompoundTag p_32925_) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
      return super.finalizeSpawn(p_32921_, p_32922_, p_32923_, p_32924_, p_32925_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.isInvisible()) {
         --this.clientSideIllusionTicks;
         if (this.clientSideIllusionTicks < 0) {
            this.clientSideIllusionTicks = 0;
         }

         if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
            if (this.hurtTime == this.hurtDuration - 1) {
               this.clientSideIllusionTicks = 3;

               for(int k = 0; k < 4; ++k) {
                  this.clientSideIllusionOffsets[0][k] = this.clientSideIllusionOffsets[1][k];
                  this.clientSideIllusionOffsets[1][k] = new Vec3(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.clientSideIllusionTicks = 3;
            float f = -6.0F;
            int i = 13;

            for(int j = 0; j < 4; ++j) {
               this.clientSideIllusionOffsets[0][j] = this.clientSideIllusionOffsets[1][j];
               this.clientSideIllusionOffsets[1][j] = new Vec3((double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D, (double)Math.max(0, this.random.nextInt(6) - 4), (double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D);
            }

            for(int l = 0; l < 16; ++l) {
               this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getRandomY(), this.getZ(0.5D), 0.0D, 0.0D, 0.0D);
            }

            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   public Vec3[] getIllusionOffsets(float p_32940_) {
      if (this.clientSideIllusionTicks <= 0) {
         return this.clientSideIllusionOffsets[1];
      } else {
         double d0 = (double)(((float)this.clientSideIllusionTicks - p_32940_) / 3.0F);
         d0 = Math.pow(d0, 0.25D);
         Vec3[] avec3 = new Vec3[4];

         for(int i = 0; i < 4; ++i) {
            avec3[i] = this.clientSideIllusionOffsets[1][i].scale(1.0D - d0).add(this.clientSideIllusionOffsets[0][i].scale(d0));
         }

         return avec3;
      }
   }

   public boolean isAlliedTo(Entity p_32938_) {
      if (super.isAlliedTo(p_32938_)) {
         return true;
      } else if (p_32938_ instanceof LivingEntity && ((LivingEntity)p_32938_).getMobType() == MobType.ILLAGER) {
         return this.getTeam() == null && p_32938_.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource p_32930_) {
      return SoundEvents.ILLUSIONER_HURT;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.ILLUSIONER_CAST_SPELL;
   }

   public void applyRaidBuffs(int p_32915_, boolean p_32916_) {
   }

   public void performRangedAttack(LivingEntity p_32918_, float p_32919_) {
      ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem)));
      AbstractArrow abstractarrow = ProjectileUtil.getMobArrow(this, itemstack, p_32919_);
      if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem)
         abstractarrow = ((net.minecraft.world.item.BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow);
      double d0 = p_32918_.getX() - this.getX();
      double d1 = p_32918_.getY(0.3333333333333333D) - abstractarrow.getY();
      double d2 = p_32918_.getZ() - this.getZ();
      double d3 = Math.sqrt(d0 * d0 + d2 * d2);
      abstractarrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(abstractarrow);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   class IllusionerBlindnessSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private int lastTargetId;

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else if (Illusioner.this.getTarget() == null) {
            return false;
         } else if (Illusioner.this.getTarget().getId() == this.lastTargetId) {
            return false;
         } else {
            return Illusioner.this.level.getCurrentDifficultyAt(Illusioner.this.blockPosition()).isHarderThan((float)Difficulty.NORMAL.ordinal());
         }
      }

      public void start() {
         super.start();
         LivingEntity livingentity = Illusioner.this.getTarget();
         if (livingentity != null) {
            this.lastTargetId = livingentity.getId();
         }

      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void performSpellCasting() {
         Illusioner.this.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400), Illusioner.this);
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.BLINDNESS;
      }
   }

   class IllusionerMirrorSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            return !Illusioner.this.hasEffect(MobEffects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         Illusioner.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.DISAPPEAR;
      }
   }
}
