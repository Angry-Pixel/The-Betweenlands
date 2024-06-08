package net.minecraft.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ThrowableProjectile extends Projectile {
   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
      super(p_37466_, p_37467_);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> p_37456_, double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
      this(p_37456_, p_37460_);
      this.setPos(p_37457_, p_37458_, p_37459_);
   }

   protected ThrowableProjectile(EntityType<? extends ThrowableProjectile> p_37462_, LivingEntity p_37463_, Level p_37464_) {
      this(p_37462_, p_37463_.getX(), p_37463_.getEyeY() - (double)0.1F, p_37463_.getZ(), p_37464_);
      this.setOwner(p_37463_);
   }

   public boolean shouldRenderAtSqrDistance(double p_37470_) {
      double d0 = this.getBoundingBox().getSize() * 4.0D;
      if (Double.isNaN(d0)) {
         d0 = 4.0D;
      }

      d0 *= 64.0D;
      return p_37470_ < d0 * d0;
   }

   public void tick() {
      super.tick();
      HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
      boolean flag = false;
      if (hitresult.getType() == HitResult.Type.BLOCK) {
         BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
         BlockState blockstate = this.level.getBlockState(blockpos);
         if (blockstate.is(Blocks.NETHER_PORTAL)) {
            this.handleInsidePortal(blockpos);
            flag = true;
         } else if (blockstate.is(Blocks.END_GATEWAY)) {
            BlockEntity blockentity = this.level.getBlockEntity(blockpos);
            if (blockentity instanceof TheEndGatewayBlockEntity && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
               TheEndGatewayBlockEntity.teleportEntity(this.level, blockpos, blockstate, this, (TheEndGatewayBlockEntity)blockentity);
            }

            flag = true;
         }
      }

      if (hitresult.getType() != HitResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
         this.onHit(hitresult);
      }

      this.checkInsideBlocks();
      Vec3 vec3 = this.getDeltaMovement();
      double d2 = this.getX() + vec3.x;
      double d0 = this.getY() + vec3.y;
      double d1 = this.getZ() + vec3.z;
      this.updateRotation();
      float f;
      if (this.isInWater()) {
         for(int i = 0; i < 4; ++i) {
            float f1 = 0.25F;
            this.level.addParticle(ParticleTypes.BUBBLE, d2 - vec3.x * 0.25D, d0 - vec3.y * 0.25D, d1 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
         }

         f = 0.8F;
      } else {
         f = 0.99F;
      }

      this.setDeltaMovement(vec3.scale((double)f));
      if (!this.isNoGravity()) {
         Vec3 vec31 = this.getDeltaMovement();
         this.setDeltaMovement(vec31.x, vec31.y - (double)this.getGravity(), vec31.z);
      }

      this.setPos(d2, d0, d1);
   }

   protected float getGravity() {
      return 0.03F;
   }
}
