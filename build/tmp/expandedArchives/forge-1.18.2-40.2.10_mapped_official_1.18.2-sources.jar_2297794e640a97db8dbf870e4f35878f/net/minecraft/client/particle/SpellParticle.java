package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellParticle extends TextureSheetParticle {
   private static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   SpellParticle(ClientLevel p_107762_, double p_107763_, double p_107764_, double p_107765_, double p_107766_, double p_107767_, double p_107768_, SpriteSet p_107769_) {
      super(p_107762_, p_107763_, p_107764_, p_107765_, 0.5D - RANDOM.nextDouble(), p_107767_, 0.5D - RANDOM.nextDouble());
      this.friction = 0.96F;
      this.gravity = -0.1F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = p_107769_;
      this.yd *= (double)0.2F;
      if (p_107766_ == 0.0D && p_107768_ == 0.0D) {
         this.xd *= (double)0.1F;
         this.zd *= (double)0.1F;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(p_107769_);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }

   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      } else {
         this.setAlpha(Mth.lerp(0.05F, this.alpha, 1.0F));
      }

   }

   private boolean isCloseToScopingPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localplayer = minecraft.player;
      return localplayer != null && localplayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 9.0D && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping();
   }

   @OnlyIn(Dist.CLIENT)
   public static class AmbientMobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public AmbientMobProvider(SpriteSet p_107784_) {
         this.sprite = p_107784_;
      }

      public Particle createParticle(SimpleParticleType p_107795_, ClientLevel p_107796_, double p_107797_, double p_107798_, double p_107799_, double p_107800_, double p_107801_, double p_107802_) {
         Particle particle = new SpellParticle(p_107796_, p_107797_, p_107798_, p_107799_, p_107800_, p_107801_, p_107802_, this.sprite);
         particle.setAlpha(0.15F);
         particle.setColor((float)p_107800_, (float)p_107801_, (float)p_107802_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class InstantProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public InstantProvider(SpriteSet p_107805_) {
         this.sprite = p_107805_;
      }

      public Particle createParticle(SimpleParticleType p_107816_, ClientLevel p_107817_, double p_107818_, double p_107819_, double p_107820_, double p_107821_, double p_107822_, double p_107823_) {
         return new SpellParticle(p_107817_, p_107818_, p_107819_, p_107820_, p_107821_, p_107822_, p_107823_, this.sprite);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MobProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public MobProvider(SpriteSet p_107826_) {
         this.sprite = p_107826_;
      }

      public Particle createParticle(SimpleParticleType p_107837_, ClientLevel p_107838_, double p_107839_, double p_107840_, double p_107841_, double p_107842_, double p_107843_, double p_107844_) {
         Particle particle = new SpellParticle(p_107838_, p_107839_, p_107840_, p_107841_, p_107842_, p_107843_, p_107844_, this.sprite);
         particle.setColor((float)p_107842_, (float)p_107843_, (float)p_107844_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet p_107847_) {
         this.sprite = p_107847_;
      }

      public Particle createParticle(SimpleParticleType p_107858_, ClientLevel p_107859_, double p_107860_, double p_107861_, double p_107862_, double p_107863_, double p_107864_, double p_107865_) {
         return new SpellParticle(p_107859_, p_107860_, p_107861_, p_107862_, p_107863_, p_107864_, p_107865_, this.sprite);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WitchProvider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public WitchProvider(SpriteSet p_107868_) {
         this.sprite = p_107868_;
      }

      public Particle createParticle(SimpleParticleType p_107879_, ClientLevel p_107880_, double p_107881_, double p_107882_, double p_107883_, double p_107884_, double p_107885_, double p_107886_) {
         SpellParticle spellparticle = new SpellParticle(p_107880_, p_107881_, p_107882_, p_107883_, p_107884_, p_107885_, p_107886_, this.sprite);
         float f = p_107880_.random.nextFloat() * 0.5F + 0.35F;
         spellparticle.setColor(1.0F * f, 0.0F * f, 1.0F * f);
         return spellparticle;
      }
   }
}