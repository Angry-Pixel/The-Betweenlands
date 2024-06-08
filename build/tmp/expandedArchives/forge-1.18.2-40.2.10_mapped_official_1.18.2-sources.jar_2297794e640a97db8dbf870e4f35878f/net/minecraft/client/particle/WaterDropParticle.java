package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WaterDropParticle extends TextureSheetParticle {
   protected WaterDropParticle(ClientLevel p_108484_, double p_108485_, double p_108486_, double p_108487_) {
      super(p_108484_, p_108485_, p_108486_, p_108487_, 0.0D, 0.0D, 0.0D);
      this.xd *= (double)0.3F;
      this.yd = Math.random() * (double)0.2F + (double)0.1F;
      this.zd *= (double)0.3F;
      this.setSize(0.01F, 0.01F);
      this.gravity = 0.06F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.98F;
         this.yd *= (double)0.98F;
         this.zd *= (double)0.98F;
         if (this.onGround) {
            if (Math.random() < 0.5D) {
               this.remove();
            }

            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

         BlockPos blockpos = new BlockPos(this.x, this.y, this.z);
         double d0 = Math.max(this.level.getBlockState(blockpos).getCollisionShape(this.level, blockpos).max(Direction.Axis.Y, this.x - (double)blockpos.getX(), this.z - (double)blockpos.getZ()), (double)this.level.getFluidState(blockpos).getHeight(this.level, blockpos));
         if (d0 > 0.0D && this.y < (double)blockpos.getY() + d0) {
            this.remove();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_108492_) {
         this.sprite = p_108492_;
      }

      public Particle createParticle(SimpleParticleType p_108503_, ClientLevel p_108504_, double p_108505_, double p_108506_, double p_108507_, double p_108508_, double p_108509_, double p_108510_) {
         WaterDropParticle waterdropparticle = new WaterDropParticle(p_108504_, p_108505_, p_108506_, p_108507_);
         waterdropparticle.pickSprite(this.sprite);
         return waterdropparticle;
      }
   }
}