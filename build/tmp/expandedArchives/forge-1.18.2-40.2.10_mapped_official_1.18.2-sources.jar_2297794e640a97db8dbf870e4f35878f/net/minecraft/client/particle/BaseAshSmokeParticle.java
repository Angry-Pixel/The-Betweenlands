package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BaseAshSmokeParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected BaseAshSmokeParticle(ClientLevel p_171904_, double p_171905_, double p_171906_, double p_171907_, float p_171908_, float p_171909_, float p_171910_, double p_171911_, double p_171912_, double p_171913_, float p_171914_, SpriteSet p_171915_, float p_171916_, int p_171917_, float p_171918_, boolean p_171919_) {
      super(p_171904_, p_171905_, p_171906_, p_171907_, 0.0D, 0.0D, 0.0D);
      this.friction = 0.96F;
      this.gravity = p_171918_;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_171915_;
      this.xd *= (double)p_171908_;
      this.yd *= (double)p_171909_;
      this.zd *= (double)p_171910_;
      this.xd += p_171911_;
      this.yd += p_171912_;
      this.zd += p_171913_;
      float f = p_171904_.random.nextFloat() * p_171916_;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize *= 0.75F * p_171914_;
      this.lifetime = (int)((double)p_171917_ / ((double)p_171904_.random.nextFloat() * 0.8D + 0.2D) * (double)p_171914_);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(p_171915_);
      this.hasPhysics = p_171919_;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_105642_) {
      return this.quadSize * Mth.clamp(((float)this.age + p_105642_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
   }
}