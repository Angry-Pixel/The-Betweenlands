package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Projectile {
   public double xPower;
   public double yPower;
   public double zPower;

   protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36833_, Level p_36834_) {
      super(p_36833_, p_36834_);
   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36817_, double p_36818_, double p_36819_, double p_36820_, double p_36821_, double p_36822_, double p_36823_, Level p_36824_) {
      this(p_36817_, p_36824_);
      this.moveTo(p_36818_, p_36819_, p_36820_, this.getYRot(), this.getXRot());
      this.reapplyPosition();
      double d0 = Math.sqrt(p_36821_ * p_36821_ + p_36822_ * p_36822_ + p_36823_ * p_36823_);
      if (d0 != 0.0D) {
         this.xPower = p_36821_ / d0 * 0.1D;
         this.yPower = p_36822_ / d0 * 0.1D;
         this.zPower = p_36823_ / d0 * 0.1D;
      }

   }

   public AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> p_36826_, LivingEntity p_36827_, double p_36828_, double p_36829_, double p_36830_, Level p_36831_) {
      this(p_36826_, p_36827_.getX(), p_36827_.getY(), p_36827_.getZ(), p_36828_, p_36829_, p_36830_, p_36831_);
      this.setOwner(p_36827_);
      this.setRot(p_36827_.getYRot(), p_36827_.getXRot());
   }

   protected void defineSynchedData() {
   }

   public boolean shouldRenderAtSqrDistance(double p_36837_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_36837_ < d0 * d0;
   }

   public void tick() {
      Entity entity = this.getOwner();
      if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
         super.tick();
         if (this.shouldBurn()) {
            this.setSecondsOnFire(1);
         }

         HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
         if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
            this.onHit(hitresult);
         }

         this.checkInsideBlocks();
         Vec3 vec3 = this.getDeltaMovement();
         double d0 = this.getX() + vec3.x;
         double d1 = this.getY() + vec3.y;
         double d2 = this.getZ() + vec3.z;
         ProjectileUtil.rotateTowardsMovement(this, 0.2F);
         float f = this.getInertia();
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               float f1 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
            }

            f = 0.8F;
         }

         this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)f));
         this.level.addParticle(this.getTrailParticle(), d0, d1 + 0.5D, d2, 0.0D, 0.0D, 0.0D);
         this.setPos(d0, d1, d2);
      } else {
         this.discard();
      }
   }

   protected boolean canHitEntity(Entity p_36842_) {
      return super.canHitEntity(p_36842_) && !p_36842_.noPhysics;
   }

   protected boolean shouldBurn() {
      return true;
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.SMOKE;
   }

   protected float getInertia() {
      return 0.95F;
   }

   public void addAdditionalSaveData(CompoundTag p_36848_) {
      super.addAdditionalSaveData(p_36848_);
      p_36848_.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
   }

   public void readAdditionalSaveData(CompoundTag p_36844_) {
      super.readAdditionalSaveData(p_36844_);
      if (p_36844_.contains("power", 9)) {
         ListTag listtag = p_36844_.getList("power", 6);
         if (listtag.size() == 3) {
            this.xPower = listtag.getDouble(0);
            this.yPower = listtag.getDouble(1);
            this.zPower = listtag.getDouble(2);
         }
      }

   }

   public boolean isPickable() {
      return true;
   }

   public float getPickRadius() {
      return 1.0F;
   }

   public boolean hurt(DamageSource p_36839_, float p_36840_) {
      if (this.isInvulnerableTo(p_36839_)) {
         return false;
      } else {
         this.markHurt();
         Entity entity = p_36839_.getEntity();
         if (entity != null) {
            if (!this.level.isClientSide) {
               Vec3 vec3 = entity.getLookAngle();
               this.setDeltaMovement(vec3);
               this.xPower = vec3.x * 0.1D;
               this.yPower = vec3.y * 0.1D;
               this.zPower = vec3.z * 0.1D;
               this.setOwner(entity);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float getBrightness() {
      return 1.0F;
   }

   public Packet<?> getAddEntityPacket() {
      Entity entity = this.getOwner();
      int i = entity == null ? 0 : entity.getId();
      return new ClientboundAddEntityPacket(this.getId(), this.getUUID(), this.getX(), this.getY(), this.getZ(), this.getXRot(), this.getYRot(), this.getType(), i, new Vec3(this.xPower, this.yPower, this.zPower));
   }

   public void recreateFromPacket(ClientboundAddEntityPacket p_150128_) {
      super.recreateFromPacket(p_150128_);
      double d0 = p_150128_.getXa();
      double d1 = p_150128_.getYa();
      double d2 = p_150128_.getZa();
      double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
      if (d3 != 0.0D) {
         this.xPower = d0 / d3 * 0.1D;
         this.yPower = d1 / d3 * 0.1D;
         this.zPower = d2 / d3 * 0.1D;
      }

   }
}
