package net.minecraft.client.renderer;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private static final int WATER_FOG_DISTANCE = 96;
   public static final float BIOME_FOG_TRANSITION_TIME = 5000.0F;
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public static void setupColor(Camera p_109019_, float p_109020_, ClientLevel p_109021_, int p_109022_, float p_109023_) {
      FogType fogtype = p_109019_.getFluidInCamera();
      Entity entity = p_109019_.getEntity();
      if (fogtype == FogType.WATER) {
         long i = Util.getMillis();
         int j = p_109021_.getBiome(new BlockPos(p_109019_.getPosition())).value().getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = j;
            previousBiomeFog = j;
            biomeChangedTime = i;
         }

         int k = targetBiomeFog >> 16 & 255;
         int l = targetBiomeFog >> 8 & 255;
         int i1 = targetBiomeFog & 255;
         int j1 = previousBiomeFog >> 16 & 255;
         int k1 = previousBiomeFog >> 8 & 255;
         int l1 = previousBiomeFog & 255;
         float f = Mth.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float f1 = Mth.lerp(f, (float)j1, (float)k);
         float f2 = Mth.lerp(f, (float)k1, (float)l);
         float f3 = Mth.lerp(f, (float)l1, (float)i1);
         fogRed = f1 / 255.0F;
         fogGreen = f2 / 255.0F;
         fogBlue = f3 / 255.0F;
         if (targetBiomeFog != j) {
            targetBiomeFog = j;
            previousBiomeFog = Mth.floor(f1) << 16 | Mth.floor(f2) << 8 | Mth.floor(f3);
            biomeChangedTime = i;
         }
      } else if (fogtype == FogType.LAVA) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else if (fogtype == FogType.POWDER_SNOW) {
         fogRed = 0.623F;
         fogGreen = 0.734F;
         fogBlue = 0.785F;
         biomeChangedTime = -1L;
         RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
      } else {
         float f4 = 0.25F + 0.75F * (float)p_109022_ / 32.0F;
         f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
         Vec3 vec3 = p_109021_.getSkyColor(p_109019_.getPosition(), p_109020_);
         float f7 = (float)vec3.x;
         float f9 = (float)vec3.y;
         float f10 = (float)vec3.z;
         float f11 = Mth.clamp(Mth.cos(p_109021_.getTimeOfDay(p_109020_) * ((float)Math.PI * 2F)) * 2.0F + 0.5F, 0.0F, 1.0F);
         BiomeManager biomemanager = p_109021_.getBiomeManager();
         Vec3 vec31 = p_109019_.getPosition().subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
         Vec3 vec32 = CubicSampler.gaussianSampleVec3(vec31, (p_109033_, p_109034_, p_109035_) -> {
            return p_109021_.effects().getBrightnessDependentFogColor(Vec3.fromRGB24(biomemanager.getNoiseBiomeAtQuart(p_109033_, p_109034_, p_109035_).value().getFogColor()), f11);
         });
         fogRed = (float)vec32.x();
         fogGreen = (float)vec32.y();
         fogBlue = (float)vec32.z();
         if (p_109022_ >= 4) {
            float f12 = Mth.sin(p_109021_.getSunAngle(p_109020_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f12, 0.0F, 0.0F);
            float f16 = p_109019_.getLookVector().dot(vector3f);
            if (f16 < 0.0F) {
               f16 = 0.0F;
            }

            if (f16 > 0.0F) {
               float[] afloat = p_109021_.effects().getSunriseColor(p_109021_.getTimeOfDay(p_109020_), p_109020_);
               if (afloat != null) {
                  f16 *= afloat[3];
                  fogRed = fogRed * (1.0F - f16) + afloat[0] * f16;
                  fogGreen = fogGreen * (1.0F - f16) + afloat[1] * f16;
                  fogBlue = fogBlue * (1.0F - f16) + afloat[2] * f16;
               }
            }
         }

         fogRed += (f7 - fogRed) * f4;
         fogGreen += (f9 - fogGreen) * f4;
         fogBlue += (f10 - fogBlue) * f4;
         float f13 = p_109021_.getRainLevel(p_109020_);
         if (f13 > 0.0F) {
            float f14 = 1.0F - f13 * 0.5F;
            float f17 = 1.0F - f13 * 0.4F;
            fogRed *= f14;
            fogGreen *= f14;
            fogBlue *= f17;
         }

         float f15 = p_109021_.getThunderLevel(p_109020_);
         if (f15 > 0.0F) {
            float f18 = 1.0F - f15 * 0.5F;
            fogRed *= f18;
            fogGreen *= f18;
            fogBlue *= f18;
         }

         biomeChangedTime = -1L;
      }

      float f5 = ((float)p_109019_.getPosition().y - (float)p_109021_.getMinBuildHeight()) * p_109021_.getLevelData().getClearColorScale();
      if (p_109019_.getEntity() instanceof LivingEntity && ((LivingEntity)p_109019_.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         int i2 = ((LivingEntity)p_109019_.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if (i2 < 20) {
            f5 = 1.0F - (float)i2 / 20.0F;
         } else {
            f5 = 0.0F;
         }
      }

      if (f5 < 1.0F && fogtype != FogType.LAVA && fogtype != FogType.POWDER_SNOW) {
         if (f5 < 0.0F) {
            f5 = 0.0F;
         }

         f5 *= f5;
         fogRed *= f5;
         fogGreen *= f5;
         fogBlue *= f5;
      }

      if (p_109023_ > 0.0F) {
         fogRed = fogRed * (1.0F - p_109023_) + fogRed * 0.7F * p_109023_;
         fogGreen = fogGreen * (1.0F - p_109023_) + fogGreen * 0.6F * p_109023_;
         fogBlue = fogBlue * (1.0F - p_109023_) + fogBlue * 0.6F * p_109023_;
      }

      float f6;
      if (fogtype == FogType.WATER) {
         if (entity instanceof LocalPlayer) {
            f6 = ((LocalPlayer)entity).getWaterVision();
         } else {
            f6 = 1.0F;
         }
      } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.NIGHT_VISION)) {
         f6 = GameRenderer.getNightVisionScale((LivingEntity)entity, p_109020_);
      } else {
         f6 = 0.0F;
      }

      if (fogRed != 0.0F && fogGreen != 0.0F && fogBlue != 0.0F) {
         float f8 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         fogRed = fogRed * (1.0F - f6) + fogRed * f8 * f6;
         fogGreen = fogGreen * (1.0F - f6) + fogGreen * f8 * f6;
         fogBlue = fogBlue * (1.0F - f6) + fogBlue * f8 * f6;
      }

      net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event = new net.minecraftforge.client.event.EntityViewRenderEvent.FogColors(p_109019_, p_109020_, fogRed, fogGreen, fogBlue);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

      fogRed = event.getRed();
      fogGreen = event.getGreen();
      fogBlue = event.getBlue();

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.setShaderFogStart(Float.MAX_VALUE);
   }
   @Deprecated // FORGE: Pass in partialTicks
   public static void setupFog(Camera p_109025_, FogRenderer.FogMode p_109026_, float p_109027_, boolean p_109028_) {
      setupFog(p_109025_, p_109026_, p_109027_, p_109028_, 0);
   }

   public static void setupFog(Camera p_109025_, FogRenderer.FogMode p_109026_, float p_109027_, boolean p_109028_, float partialTicks) {
      FogType fogtype = p_109025_.getFluidInCamera();
      Entity entity = p_109025_.getEntity();
      FogShape fogshape = FogShape.SPHERE;
      float f;
      float f1;
      // TODO: remove this hook in 1.19
      float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(p_109026_, p_109025_, partialTicks, 0.1F);
      if (hook >= 0) {
         f = -8.0f;
         f1 = hook * 0.5F;
      } else
      if (fogtype == FogType.LAVA) {
         if (entity.isSpectator()) {
            f = -8.0F;
            f1 = p_109027_ * 0.5F;
         } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.FIRE_RESISTANCE)) {
            f = 0.0F;
            f1 = 3.0F;
         } else {
            f = 0.25F;
            f1 = 1.0F;
         }
      } else if (fogtype == FogType.POWDER_SNOW) {
         if (entity.isSpectator()) {
            f = -8.0F;
            f1 = p_109027_ * 0.5F;
         } else {
            f = 0.0F;
            f1 = 2.0F;
         }
      } else if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(MobEffects.BLINDNESS)) {
         int i = ((LivingEntity)entity).getEffect(MobEffects.BLINDNESS).getDuration();
         float f3 = Mth.lerp(Math.min(1.0F, (float)i / 20.0F), p_109027_, 5.0F);
         if (p_109026_ == FogRenderer.FogMode.FOG_SKY) {
            f = 0.0F;
            f1 = f3 * 0.8F;
         } else {
            f = fogtype == FogType.WATER ? -4.0F : f3 * 0.25F;
            f1 = f3;
         }
      } else if (fogtype == FogType.WATER) {
         f = -8.0F;
         f1 = 96.0F;
         if (entity instanceof LocalPlayer) {
            LocalPlayer localplayer = (LocalPlayer)entity;
            f1 *= Math.max(0.25F, localplayer.getWaterVision());
            Holder<Biome> holder = localplayer.level.getBiome(localplayer.blockPosition());
            if (Biome.getBiomeCategory(holder) == Biome.BiomeCategory.SWAMP) {
               f1 *= 0.85F;
            }
         }

         if (f1 > p_109027_) {
            f1 = p_109027_;
            fogshape = FogShape.CYLINDER;
         }
      } else if (p_109028_) {
         f = p_109027_ * 0.05F;
         f1 = Math.min(p_109027_, 192.0F) * 0.5F;
      } else if (p_109026_ == FogRenderer.FogMode.FOG_SKY) {
         f = 0.0F;
         f1 = p_109027_;
         fogshape = FogShape.CYLINDER;
      } else {
         float f2 = Mth.clamp(p_109027_ / 10.0F, 4.0F, 64.0F);
         f = p_109027_ - f2;
         f1 = p_109027_;
         fogshape = FogShape.CYLINDER;
      }

      RenderSystem.setShaderFogStart(f);
      RenderSystem.setShaderFogEnd(f1);
      RenderSystem.setShaderFogShape(fogshape);
      net.minecraftforge.client.ForgeHooksClient.onFogRender(p_109026_, p_109025_, partialTicks, f, f1, fogshape);
   }

   public static void levelFogColor() {
      RenderSystem.setShaderFogColor(fogRed, fogGreen, fogBlue);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogMode {
      FOG_SKY,
      FOG_TERRAIN;
   }
}
