package net.minecraft.world.entity.boss.enderdragon.phases;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonSittingScanningPhase extends AbstractDragonSittingPhase {
   private static final int SITTING_SCANNING_IDLE_TICKS = 100;
   private static final int SITTING_ATTACK_Y_VIEW_RANGE = 10;
   private static final int SITTING_ATTACK_VIEW_RANGE = 20;
   private static final int SITTING_CHARGE_VIEW_RANGE = 150;
   private static final TargetingConditions CHARGE_TARGETING = TargetingConditions.forCombat().range(150.0D);
   private final TargetingConditions scanTargeting;
   private int scanningTime;

   public DragonSittingScanningPhase(EnderDragon p_31342_) {
      super(p_31342_);
      this.scanTargeting = TargetingConditions.forCombat().range(20.0D).selector((p_31345_) -> {
         return Math.abs(p_31345_.getY() - p_31342_.getY()) <= 10.0D;
      });
   }

   public void doServerTick() {
      ++this.scanningTime;
      LivingEntity livingentity = this.dragon.level.getNearestPlayer(this.scanTargeting, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (livingentity != null) {
         if (this.scanningTime > 25) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_ATTACKING);
         } else {
            Vec3 vec3 = (new Vec3(livingentity.getX() - this.dragon.getX(), 0.0D, livingentity.getZ() - this.dragon.getZ())).normalize();
            Vec3 vec31 = (new Vec3((double)Mth.sin(this.dragon.getYRot() * ((float)Math.PI / 180F)), 0.0D, (double)(-Mth.cos(this.dragon.getYRot() * ((float)Math.PI / 180F))))).normalize();
            float f = (float)vec31.dot(vec3);
            float f1 = (float)(Math.acos((double)f) * (double)(180F / (float)Math.PI)) + 0.5F;
            if (f1 < 0.0F || f1 > 10.0F) {
               double d0 = livingentity.getX() - this.dragon.head.getX();
               double d1 = livingentity.getZ() - this.dragon.head.getZ();
               double d2 = Mth.clamp(Mth.wrapDegrees(180.0D - Mth.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.dragon.getYRot()), -100.0D, 100.0D);
               this.dragon.yRotA *= 0.8F;
               float f2 = (float)Math.sqrt(d0 * d0 + d1 * d1) + 1.0F;
               float f3 = f2;
               if (f2 > 40.0F) {
                  f2 = 40.0F;
               }

               this.dragon.yRotA += (float)d2 * (0.7F / f2 / f3);
               this.dragon.setYRot(this.dragon.getYRot() + this.dragon.yRotA);
            }
         }
      } else if (this.scanningTime >= 100) {
         livingentity = this.dragon.level.getNearestPlayer(CHARGE_TARGETING, this.dragon, this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
         this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         if (livingentity != null) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.CHARGING_PLAYER);
            this.dragon.getPhaseManager().getPhase(EnderDragonPhase.CHARGING_PLAYER).setTarget(new Vec3(livingentity.getX(), livingentity.getY(), livingentity.getZ()));
         }
      }

   }

   public void begin() {
      this.scanningTime = 0;
   }

   public EnderDragonPhase<DragonSittingScanningPhase> getPhase() {
      return EnderDragonPhase.SITTING_SCANNING;
   }
}