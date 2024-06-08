package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.phys.Vec3;

public class DragonSittingFlamingPhase extends AbstractDragonSittingPhase {
   private static final int FLAME_DURATION = 200;
   private static final int SITTING_FLAME_ATTACKS_COUNT = 4;
   private static final int WARMUP_TIME = 10;
   private int flameTicks;
   private int flameCount;
   @Nullable
   private AreaEffectCloud flame;

   public DragonSittingFlamingPhase(EnderDragon p_31330_) {
      super(p_31330_);
   }

   public void doClientTick() {
      ++this.flameTicks;
      if (this.flameTicks % 2 == 0 && this.flameTicks < 10) {
         Vec3 vec3 = this.dragon.getHeadLookVector(1.0F).normalize();
         vec3.yRot((-(float)Math.PI / 4F));
         double d0 = this.dragon.head.getX();
         double d1 = this.dragon.head.getY(0.5D);
         double d2 = this.dragon.head.getZ();

         for(int i = 0; i < 8; ++i) {
            double d3 = d0 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double d4 = d1 + this.dragon.getRandom().nextGaussian() / 2.0D;
            double d5 = d2 + this.dragon.getRandom().nextGaussian() / 2.0D;

            for(int j = 0; j < 6; ++j) {
               this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vec3.x * (double)0.08F * (double)j, -vec3.y * (double)0.6F, -vec3.z * (double)0.08F * (double)j);
            }

            vec3.yRot(0.19634955F);
         }
      }

   }

   public void doServerTick() {
      ++this.flameTicks;
      if (this.flameTicks >= 200) {
         if (this.flameCount >= 4) {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.TAKEOFF);
         } else {
            this.dragon.getPhaseManager().setPhase(EnderDragonPhase.SITTING_SCANNING);
         }
      } else if (this.flameTicks == 10) {
         Vec3 vec3 = (new Vec3(this.dragon.head.getX() - this.dragon.getX(), 0.0D, this.dragon.head.getZ() - this.dragon.getZ())).normalize();
         float f = 5.0F;
         double d0 = this.dragon.head.getX() + vec3.x * 5.0D / 2.0D;
         double d1 = this.dragon.head.getZ() + vec3.z * 5.0D / 2.0D;
         double d2 = this.dragon.head.getY(0.5D);
         double d3 = d2;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(d0, d2, d1);

         while(this.dragon.level.isEmptyBlock(blockpos$mutableblockpos)) {
            --d3;
            if (d3 < 0.0D) {
               d3 = d2;
               break;
            }

            blockpos$mutableblockpos.set(d0, d3, d1);
         }

         d3 = (double)(Mth.floor(d3) + 1);
         this.flame = new AreaEffectCloud(this.dragon.level, d0, d3, d1);
         this.flame.setOwner(this.dragon);
         this.flame.setRadius(5.0F);
         this.flame.setDuration(200);
         this.flame.setParticle(ParticleTypes.DRAGON_BREATH);
         this.flame.addEffect(new MobEffectInstance(MobEffects.HARM));
         this.dragon.level.addFreshEntity(this.flame);
      }

   }

   public void begin() {
      this.flameTicks = 0;
      ++this.flameCount;
   }

   public void end() {
      if (this.flame != null) {
         this.flame.discard();
         this.flame = null;
      }

   }

   public EnderDragonPhase<DragonSittingFlamingPhase> getPhase() {
      return EnderDragonPhase.SITTING_FLAMING;
   }

   public void resetFlameCount() {
      this.flameCount = 0;
   }
}