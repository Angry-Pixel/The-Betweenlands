package net.minecraft.world.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EvokerFangs extends Entity {
   public static final int ATTACK_DURATION = 20;
   public static final int LIFE_OFFSET = 2;
   public static final int ATTACK_TRIGGER_TICKS = 14;
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks = 22;
   private boolean clientSideAttackStarted;
   @Nullable
   private LivingEntity owner;
   @Nullable
   private UUID ownerUUID;

   public EvokerFangs(EntityType<? extends EvokerFangs> p_36923_, Level p_36924_) {
      super(p_36923_, p_36924_);
   }

   public EvokerFangs(Level p_36926_, double p_36927_, double p_36928_, double p_36929_, float p_36930_, int p_36931_, LivingEntity p_36932_) {
      this(EntityType.EVOKER_FANGS, p_36926_);
      this.warmupDelayTicks = p_36931_;
      this.setOwner(p_36932_);
      this.setYRot(p_36930_ * (180F / (float)Math.PI));
      this.setPos(p_36927_, p_36928_, p_36929_);
   }

   protected void defineSynchedData() {
   }

   public void setOwner(@Nullable LivingEntity p_36939_) {
      this.owner = p_36939_;
      this.ownerUUID = p_36939_ == null ? null : p_36939_.getUUID();
   }

   @Nullable
   public LivingEntity getOwner() {
      if (this.owner == null && this.ownerUUID != null && this.level instanceof ServerLevel) {
         Entity entity = ((ServerLevel)this.level).getEntity(this.ownerUUID);
         if (entity instanceof LivingEntity) {
            this.owner = (LivingEntity)entity;
         }
      }

      return this.owner;
   }

   protected void readAdditionalSaveData(CompoundTag p_36941_) {
      this.warmupDelayTicks = p_36941_.getInt("Warmup");
      if (p_36941_.hasUUID("Owner")) {
         this.ownerUUID = p_36941_.getUUID("Owner");
      }

   }

   protected void addAdditionalSaveData(CompoundTag p_36943_) {
      p_36943_.putInt("Warmup", this.warmupDelayTicks);
      if (this.ownerUUID != null) {
         p_36943_.putUUID("Owner", this.ownerUUID);
      }

   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int i = 0; i < 12; ++i) {
                  double d0 = this.getX() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double d1 = this.getY() + 0.05D + this.random.nextDouble();
                  double d2 = this.getZ() + (this.random.nextDouble() * 2.0D - 1.0D) * (double)this.getBbWidth() * 0.5D;
                  double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                  double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.level.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            for(LivingEntity livingentity : this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.2D, 0.0D, 0.2D))) {
               this.dealDamageTo(livingentity);
            }
         }

         if (!this.sentSpikeEvent) {
            this.level.broadcastEntityEvent(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.discard();
         }
      }

   }

   private void dealDamageTo(LivingEntity p_36945_) {
      LivingEntity livingentity = this.getOwner();
      if (p_36945_.isAlive() && !p_36945_.isInvulnerable() && p_36945_ != livingentity) {
         if (livingentity == null) {
            p_36945_.hurt(DamageSource.MAGIC, 6.0F);
         } else {
            if (livingentity.isAlliedTo(p_36945_)) {
               return;
            }

            p_36945_.hurt(DamageSource.indirectMagic(this, livingentity), 6.0F);
         }

      }
   }

   public void handleEntityEvent(byte p_36935_) {
      super.handleEntityEvent(p_36935_);
      if (p_36935_ == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.EVOKER_FANGS_ATTACK, this.getSoundSource(), 1.0F, this.random.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   public float getAnimationProgress(float p_36937_) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int i = this.lifeTicks - 2;
         return i <= 0 ? 1.0F : 1.0F - ((float)i - p_36937_) / 20.0F;
      }
   }

   public Packet<?> getAddEntityPacket() {
      return new ClientboundAddEntityPacket(this);
   }
}