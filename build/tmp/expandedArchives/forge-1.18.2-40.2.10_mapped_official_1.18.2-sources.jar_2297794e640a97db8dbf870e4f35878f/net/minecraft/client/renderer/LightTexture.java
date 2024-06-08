package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LightTexture implements AutoCloseable {
   public static final int FULL_BRIGHT = 15728880;
   public static final int FULL_SKY = 15728640;
   public static final int FULL_BLOCK = 240;
   private final DynamicTexture lightTexture;
   private final NativeImage lightPixels;
   private final ResourceLocation lightTextureLocation;
   private boolean updateLightTexture;
   private float blockLightRedFlicker;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public LightTexture(GameRenderer p_109878_, Minecraft p_109879_) {
      this.renderer = p_109878_;
      this.minecraft = p_109879_;
      this.lightTexture = new DynamicTexture(16, 16, false);
      this.lightTextureLocation = this.minecraft.getTextureManager().register("light_map", this.lightTexture);
      this.lightPixels = this.lightTexture.getPixels();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            this.lightPixels.setPixelRGBA(j, i, -1);
         }
      }

      this.lightTexture.upload();
   }

   public void close() {
      this.lightTexture.close();
   }

   public void tick() {
      this.blockLightRedFlicker += (float)((Math.random() - Math.random()) * Math.random() * Math.random() * 0.1D);
      this.blockLightRedFlicker *= 0.9F;
      this.updateLightTexture = true;
   }

   public void turnOffLightLayer() {
      RenderSystem.setShaderTexture(2, 0);
   }

   public void turnOnLightLayer() {
      RenderSystem.setShaderTexture(2, this.lightTextureLocation);
      this.minecraft.getTextureManager().bindForSetup(this.lightTextureLocation);
      RenderSystem.texParameter(3553, 10241, 9729);
      RenderSystem.texParameter(3553, 10240, 9729);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void updateLightTexture(float p_109882_) {
      if (this.updateLightTexture) {
         this.updateLightTexture = false;
         this.minecraft.getProfiler().push("lightTex");
         ClientLevel clientlevel = this.minecraft.level;
         if (clientlevel != null) {
            float f = clientlevel.getSkyDarken(1.0F);
            float f1;
            if (clientlevel.getSkyFlashTime() > 0) {
               f1 = 1.0F;
            } else {
               f1 = f * 0.95F + 0.05F;
            }

            float f3 = this.minecraft.player.getWaterVision();
            float f2;
            if (this.minecraft.player.hasEffect(MobEffects.NIGHT_VISION)) {
               f2 = GameRenderer.getNightVisionScale(this.minecraft.player, p_109882_);
            } else if (f3 > 0.0F && this.minecraft.player.hasEffect(MobEffects.CONDUIT_POWER)) {
               f2 = f3;
            } else {
               f2 = 0.0F;
            }

            Vector3f vector3f = new Vector3f(f, f, 1.0F);
            vector3f.lerp(new Vector3f(1.0F, 1.0F, 1.0F), 0.35F);
            float f4 = this.blockLightRedFlicker + 1.5F;
            Vector3f vector3f1 = new Vector3f();

            for(int i = 0; i < 16; ++i) {
               for(int j = 0; j < 16; ++j) {
                  float f5 = this.getBrightness(clientlevel, i) * f1;
                  float f6 = this.getBrightness(clientlevel, j) * f4;
                  float f7 = f6 * ((f6 * 0.6F + 0.4F) * 0.6F + 0.4F);
                  float f8 = f6 * (f6 * f6 * 0.6F + 0.4F);
                  vector3f1.set(f6, f7, f8);
                  if (clientlevel.effects().forceBrightLightmap()) {
                     vector3f1.lerp(new Vector3f(0.99F, 1.12F, 1.0F), 0.25F);
                  } else {
                     Vector3f vector3f2 = vector3f.copy();
                     vector3f2.mul(f5);
                     vector3f1.add(vector3f2);
                     vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                     if (this.renderer.getDarkenWorldAmount(p_109882_) > 0.0F) {
                        float f9 = this.renderer.getDarkenWorldAmount(p_109882_);
                        Vector3f vector3f3 = vector3f1.copy();
                        vector3f3.mul(0.7F, 0.6F, 0.6F);
                        vector3f1.lerp(vector3f3, f9);
                     }
                  }

                  vector3f1.clamp(0.0F, 1.0F);
                  if (f2 > 0.0F) {
                     float f10 = Math.max(vector3f1.x(), Math.max(vector3f1.y(), vector3f1.z()));
                     if (f10 < 1.0F) {
                        float f12 = 1.0F / f10;
                        Vector3f vector3f5 = vector3f1.copy();
                        vector3f5.mul(f12);
                        vector3f1.lerp(vector3f5, f2);
                     }
                  }

                  float f11 = (float)this.minecraft.options.gamma;
                  Vector3f vector3f4 = vector3f1.copy();
                  vector3f4.map(this::notGamma);
                  vector3f1.lerp(vector3f4, f11);
                  vector3f1.lerp(new Vector3f(0.75F, 0.75F, 0.75F), 0.04F);
                  vector3f1.clamp(0.0F, 1.0F);
                  vector3f1.mul(255.0F);
                  int j1 = 255;
                  int k = (int)vector3f1.x();
                  int l = (int)vector3f1.y();
                  int i1 = (int)vector3f1.z();
                  this.lightPixels.setPixelRGBA(j, i, -16777216 | i1 << 16 | l << 8 | k);
               }
            }

            this.lightTexture.upload();
            this.minecraft.getProfiler().pop();
         }
      }
   }

   private float notGamma(float p_109893_) {
      float f = 1.0F - p_109893_;
      return 1.0F - f * f * f * f;
   }

   private float getBrightness(Level p_109889_, int p_109890_) {
      return p_109889_.dimensionType().brightness(p_109890_);
   }

   public static int pack(int p_109886_, int p_109887_) {
      return p_109886_ << 4 | p_109887_ << 20;
   }

   public static int block(int p_109884_) {
      return (p_109884_ & 0xFFFF) >> 4; // Forge: Fix fullbright quads showing dark artifacts. Reported as MC-169806
   }

   public static int sky(int p_109895_) {
      return p_109895_ >> 20 & '\uffff';
   }
}
