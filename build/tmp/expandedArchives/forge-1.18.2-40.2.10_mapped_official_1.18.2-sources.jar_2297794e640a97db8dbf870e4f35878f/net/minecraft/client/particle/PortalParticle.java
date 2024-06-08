package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PortalParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;

   protected PortalParticle(ClientLevel p_107551_, double p_107552_, double p_107553_, double p_107554_, double p_107555_, double p_107556_, double p_107557_) {
      super(p_107551_, p_107552_, p_107553_, p_107554_);
      this.xd = p_107555_;
      this.yd = p_107556_;
      this.zd = p_107557_;
      this.x = p_107552_;
      this.y = p_107553_;
      this.z = p_107554_;
      this.xStart = this.x;
      this.yStart = this.y;
      this.zStart = this.z;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.2F + 0.5F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f * 0.9F;
      this.gCol = f * 0.3F;
      this.bCol = f;
      this.lifetime = (int)(Math.random() * 10.0D) + 40;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double p_107560_, double p_107561_, double p_107562_) {
      this.setBoundingBox(this.getBoundingBox().move(p_107560_, p_107561_, p_107562_));
      this.setLocationFromBoundingbox();
   }

   public float getQuadSize(float p_107567_) {
      float f = ((float)this.age + p_107567_) / (float)this.lifetime;
      f = 1.0F - f;
      f *= f;
      f = 1.0F - f;
      return this.quadSize * f;
   }

   public int getLightColor(float p_107564_) {
      int i = super.getLightColor(p_107564_);
      float f = (float)this.age / (float)this.lifetime;
      f *= f;
      f *= f;
      int j = i & 255;
      int k = i >> 16 & 255;
      k += (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / (float)this.lifetime;
         float f1 = -f + f * f * 2.0F;
         float f2 = 1.0F - f1;
         this.x = this.xStart + this.xd * (double)f2;
         this.y = this.yStart + this.yd * (double)f2 + (double)(1.0F - f);
         this.z = this.zStart + this.zd * (double)f2;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107570_) {
         this.sprite = p_107570_;
      }

      public Particle createParticle(SimpleParticleType p_107581_, ClientLevel p_107582_, double p_107583_, double p_107584_, double p_107585_, double p_107586_, double p_107587_, double p_107588_) {
         PortalParticle portalparticle = new PortalParticle(p_107582_, p_107583_, p_107584_, p_107585_, p_107586_, p_107587_, p_107588_);
         portalparticle.pickSprite(this.sprite);
         return portalparticle;
      }
   }
}