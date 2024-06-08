package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase extends AbstractDragonPhaseInstance {
   @Nullable
   private Vec3 targetLocation;
   private int time;

   public DragonDeathPhase(EnderDragon p_31217_) {
      super(p_31217_);
   }

   public void doClientTick() {
      if (this.time++ % 10 == 0) {
         float f = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.dragon.getRandom().nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.dragon.getRandom().nextFloat() - 0.5F) * 8.0F;
         this.dragon.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getX() + (double)f, this.dragon.getY() + 2.0D + (double)f1, this.dragon.getZ() + (double)f2, 0.0D, 0.0D, 0.0D);
      }

   }

   public void doServerTick() {
      ++this.time;
      if (this.targetLocation == null) {
         BlockPos blockpos = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
         this.targetLocation = Vec3.atBottomCenterOf(blockpos);
      }

      double d0 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ());
      if (!(d0 < 100.0D) && !(d0 > 22500.0D) && !this.dragon.horizontalCollision && !this.dragon.verticalCollision) {
         this.dragon.setHealth(1.0F);
      } else {
         this.dragon.setHealth(0.0F);
      }

   }

   public void begin() {
      this.targetLocation = null;
      this.time = 0;
   }

   public float getFlySpeed() {
      return 3.0F;
   }

   @Nullable
   public Vec3 getFlyTargetLocation() {
      return this.targetLocation;
   }

   public EnderDragonPhase<DragonDeathPhase> getPhase() {
      return EnderDragonPhase.DYING;
   }
}