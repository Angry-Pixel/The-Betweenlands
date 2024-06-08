package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   protected int spellCastingTickCount;
   private SpellcasterIllager.IllagerSpell currentSpell = SpellcasterIllager.IllagerSpell.NONE;

   protected SpellcasterIllager(EntityType<? extends SpellcasterIllager> p_33724_, Level p_33725_) {
      super(p_33724_, p_33725_);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundTag p_33732_) {
      super.readAdditionalSaveData(p_33732_);
      this.spellCastingTickCount = p_33732_.getInt("SpellTicks");
   }

   public void addAdditionalSaveData(CompoundTag p_33734_) {
      super.addAdditionalSaveData(p_33734_);
      p_33734_.putInt("SpellTicks", this.spellCastingTickCount);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level.isClientSide) {
         return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(SpellcasterIllager.IllagerSpell p_33728_) {
      this.currentSpell = p_33728_;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)p_33728_.id);
   }

   protected SpellcasterIllager.IllagerSpell getCurrentSpell() {
      return !this.level.isClientSide ? this.currentSpell : SpellcasterIllager.IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && this.isCastingSpell()) {
         SpellcasterIllager.IllagerSpell spellcasterillager$illagerspell = this.getCurrentSpell();
         double d0 = spellcasterillager$illagerspell.spellColor[0];
         double d1 = spellcasterillager$illagerspell.spellColor[1];
         double d2 = spellcasterillager$illagerspell.spellColor[2];
         float f = this.yBodyRot * ((float)Math.PI / 180F) + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
         float f1 = Mth.cos(f);
         float f2 = Mth.sin(f);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)f2 * 0.6D, d0, d1, d2);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)f1 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)f2 * 0.6D, d0, d1, d2);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   protected static enum IllagerSpell {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      final int id;
      final double[] spellColor;

      private IllagerSpell(int p_33754_, double p_33755_, double p_33756_, double p_33757_) {
         this.id = p_33754_;
         this.spellColor = new double[]{p_33755_, p_33756_, p_33757_};
      }

      public static SpellcasterIllager.IllagerSpell byId(int p_33759_) {
         for(SpellcasterIllager.IllagerSpell spellcasterillager$illagerspell : values()) {
            if (p_33759_ == spellcasterillager$illagerspell.id) {
               return spellcasterillager$illagerspell;
            }
         }

         return NONE;
      }
   }

   protected class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return SpellcasterIllager.this.getSpellCastingTime() > 0;
      }

      public void start() {
         super.start();
         SpellcasterIllager.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcasterIllager.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
      }

      public void tick() {
         if (SpellcasterIllager.this.getTarget() != null) {
            SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
         }

      }
   }

   protected abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      public boolean canUse() {
         LivingEntity livingentity = SpellcasterIllager.this.getTarget();
         if (livingentity != null && livingentity.isAlive()) {
            if (SpellcasterIllager.this.isCastingSpell()) {
               return false;
            } else {
               return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         LivingEntity livingentity = SpellcasterIllager.this.getTarget();
         return livingentity != null && livingentity.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent soundevent = this.getSpellPrepareSound();
         if (soundevent != null) {
            SpellcasterIllager.this.playSound(soundevent, 1.0F, 1.0F);
         }

         SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
      }

      public void tick() {
         --this.attackWarmupDelay;
         if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
         }

      }

      protected abstract void performSpellCasting();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcasterIllager.IllagerSpell getSpell();
   }
}