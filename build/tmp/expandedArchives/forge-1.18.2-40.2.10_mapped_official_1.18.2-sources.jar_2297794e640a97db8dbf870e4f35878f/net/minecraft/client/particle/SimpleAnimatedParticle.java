package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimpleAnimatedParticle extends TextureSheetParticle {
   protected final SpriteSet sprites;
   private float fadeR;
   private float fadeG;
   private float fadeB;
   private boolean hasFade;

   protected SimpleAnimatedParticle(ClientLevel p_107647_, double p_107648_, double p_107649_, double p_107650_, SpriteSet p_107651_, float p_107652_) {
      super(p_107647_, p_107648_, p_107649_, p_107650_);
      this.friction = 0.91F;
      this.gravity = p_107652_;
      this.sprites = p_107651_;
   }

   public void setColor(int p_107658_) {
      float f = (float)((p_107658_ & 16711680) >> 16) / 255.0F;
      float f1 = (float)((p_107658_ & '\uff00') >> 8) / 255.0F;
      float f2 = (float)((p_107658_ & 255) >> 0) / 255.0F;
      float f3 = 1.0F;
      this.setColor(f * 1.0F, f1 * 1.0F, f2 * 1.0F);
   }

   public void setFadeColor(int p_107660_) {
      this.fadeR = (float)((p_107660_ & 16711680) >> 16) / 255.0F;
      this.fadeG = (float)((p_107660_ & '\uff00') >> 8) / 255.0F;
      this.fadeB = (float)((p_107660_ & 255) >> 0) / 255.0F;
      this.hasFade = true;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.age > this.lifetime / 2) {
         this.setAlpha(1.0F - ((float)this.age - (float)(this.lifetime / 2)) / (float)this.lifetime);
         if (this.hasFade) {
            this.rCol += (this.fadeR - this.rCol) * 0.2F;
            this.gCol += (this.fadeG - this.gCol) * 0.2F;
            this.bCol += (this.fadeB - this.bCol) * 0.2F;
         }
      }

   }

   public int getLightColor(float p_107655_) {
      return 15728880;
   }
}