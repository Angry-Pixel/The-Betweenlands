package net.minecraft.world.entity.projectile;

import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class DragonFireball extends AbstractHurtingProjectile {
   public static final float SPLASH_RANGE = 4.0F;

   public DragonFireball(EntityType<? extends DragonFireball> p_36892_, Level p_36893_) {
      super(p_36892_, p_36893_);
   }

   public DragonFireball(Level p_36903_, LivingEntity p_36904_, double p_36905_, double p_36906_, double p_36907_) {
      super(EntityType.DRAGON_FIREBALL, p_36904_, p_36905_, p_36906_, p_36907_, p_36903_);
   }

   protected void onHit(HitResult p_36913_) {
      super.onHit(p_36913_);
      if (p_36913_.getType() != HitResult.Type.ENTITY || !this.ownedBy(((EntityHitResult)p_36913_).getEntity())) {
         if (!this.level.isClientSide) {
            List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D));
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
            Entity entity = this.getOwner();
            if (entity instanceof LivingEntity) {
               areaeffectcloud.setOwner((LivingEntity)entity);
            }

            areaeffectcloud.setParticle(ParticleTypes.DRAGON_BREATH);
            areaeffectcloud.setRadius(3.0F);
            areaeffectcloud.setDuration(600);
            areaeffectcloud.setRadiusPerTick((7.0F - areaeffectcloud.getRadius()) / (float)areaeffectcloud.getDuration());
            areaeffectcloud.addEffect(new MobEffectInstance(MobEffects.HARM, 1, 1));
            if (!list.isEmpty()) {
               for(LivingEntity livingentity : list) {
                  double d0 = this.distanceToSqr(livingentity);
                  if (d0 < 16.0D) {
                     areaeffectcloud.setPos(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                     break;
                  }
               }
            }

            this.level.levelEvent(2006, this.blockPosition(), this.isSilent() ? -1 : 1);
            this.level.addFreshEntity(areaeffectcloud);
            this.discard();
         }

      }
   }

   public boolean isPickable() {
      return false;
   }

   public boolean hurt(DamageSource p_36910_, float p_36911_) {
      return false;
   }

   protected ParticleOptions getTrailParticle() {
      return ParticleTypes.DRAGON_BREATH;
   }

   protected boolean shouldBurn() {
      return false;
   }
}