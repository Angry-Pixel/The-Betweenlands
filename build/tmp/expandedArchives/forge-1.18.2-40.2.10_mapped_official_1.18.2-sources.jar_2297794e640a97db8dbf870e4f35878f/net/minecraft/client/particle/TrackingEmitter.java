package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrackingEmitter extends NoRenderParticle {
   private final Entity entity;
   private int life;
   private final int lifeTime;
   private final ParticleOptions particleType;

   public TrackingEmitter(ClientLevel p_108390_, Entity p_108391_, ParticleOptions p_108392_) {
      this(p_108390_, p_108391_, p_108392_, 3);
   }

   public TrackingEmitter(ClientLevel p_108394_, Entity p_108395_, ParticleOptions p_108396_, int p_108397_) {
      this(p_108394_, p_108395_, p_108396_, p_108397_, p_108395_.getDeltaMovement());
   }

   private TrackingEmitter(ClientLevel p_108399_, Entity p_108400_, ParticleOptions p_108401_, int p_108402_, Vec3 p_108403_) {
      super(p_108399_, p_108400_.getX(), p_108400_.getY(0.5D), p_108400_.getZ(), p_108403_.x, p_108403_.y, p_108403_.z);
      this.entity = p_108400_;
      this.lifeTime = p_108402_;
      this.particleType = p_108401_;
      this.tick();
   }

   public void tick() {
      for(int i = 0; i < 16; ++i) {
         double d0 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double d1 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         double d2 = (double)(this.random.nextFloat() * 2.0F - 1.0F);
         if (!(d0 * d0 + d1 * d1 + d2 * d2 > 1.0D)) {
            double d3 = this.entity.getX(d0 / 4.0D);
            double d4 = this.entity.getY(0.5D + d1 / 4.0D);
            double d5 = this.entity.getZ(d2 / 4.0D);
            this.level.addParticle(this.particleType, false, d3, d4, d5, d0, d1 + 0.2D, d2);
         }
      }

      ++this.life;
      if (this.life >= this.lifeTime) {
         this.remove();
      }

   }
}