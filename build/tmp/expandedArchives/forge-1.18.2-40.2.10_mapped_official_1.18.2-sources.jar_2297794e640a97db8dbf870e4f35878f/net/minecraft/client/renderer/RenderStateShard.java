package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Triple;

@OnlyIn(Dist.CLIENT)
public abstract class RenderStateShard {
   private static final float VIEW_SCALE_Z_EPSILON = 0.99975586F;
   protected final String name;
   protected Runnable setupState;
   private final Runnable clearState;
   protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
      RenderSystem.disableBlend();
   }, () -> {
   });
   protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("additive_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard LIGHTNING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("lightning_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard GLINT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("glint_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard CRUMBLING_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("crumbling_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
      RenderSystem.enableBlend();
      RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
   });
   protected static final RenderStateShard.ShaderStateShard NO_SHADER = new RenderStateShard.ShaderStateShard();
   protected static final RenderStateShard.ShaderStateShard BLOCK_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getBlockShader);
   protected static final RenderStateShard.ShaderStateShard NEW_ENTITY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getNewEntityShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_LIGHTMAP_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorLightmapShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_TEX_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_TEX_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_TEX_LIGHTMAP_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorTexLightmapShader);
   protected static final RenderStateShard.ShaderStateShard POSITION_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeSolidShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_MIPPED_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutMippedShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCutoutShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTranslucentShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TRANSLUCENT_MOVING_BLOCK_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTranslucentMovingBlockShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TRANSLUCENT_NO_CRUMBLING_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTranslucentNoCrumblingShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ARMOR_CUTOUT_NO_CULL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeArmorCutoutNoCullShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_SOLID_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySolidShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityCutoutShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_CUTOUT_NO_CULL_Z_OFFSET_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityCutoutNoCullZOffsetShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ITEM_ENTITY_TRANSLUCENT_CULL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeItemEntityTranslucentCullShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_CULL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentCullShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_SMOOTH_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntitySmoothCutoutShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_BEACON_BEAM_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeBeaconBeamShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_DECAL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityDecalShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_NO_OUTLINE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityNoOutlineShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_SHADOW_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityShadowShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_ALPHA_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityAlphaShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_EYES_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEyesShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENERGY_SWIRL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEnergySwirlShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LEASH_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLeashShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_WATER_MASK_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeWaterMaskShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_OUTLINE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeOutlineShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ARMOR_GLINT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeArmorGlintShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeArmorEntityGlintShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_GLINT_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeGlintTranslucentShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_GLINT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeGlintShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_GLINT_DIRECT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeGlintDirectShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_GLINT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityGlintShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityGlintDirectShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_CRUMBLING_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeCrumblingShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TEXT_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTextShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTextIntensityShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TEXT_SEE_THROUGH_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTextSeeThroughShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TEXT_INTENSITY_SEE_THROUGH_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTextIntensitySeeThroughShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LIGHTNING_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLightningShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_TRIPWIRE_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTripwireShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_END_PORTAL_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEndPortalShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_END_GATEWAY_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEndGatewayShader);
   protected static final RenderStateShard.ShaderStateShard RENDERTYPE_LINES_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeLinesShader);
   protected static final RenderStateShard.TextureStateShard BLOCK_SHEET_MIPPED = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true);
   protected static final RenderStateShard.TextureStateShard BLOCK_SHEET = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false);
   protected static final RenderStateShard.EmptyTextureStateShard NO_TEXTURE = new RenderStateShard.EmptyTextureStateShard();
   protected static final RenderStateShard.TexturingStateShard DEFAULT_TEXTURING = new RenderStateShard.TexturingStateShard("default_texturing", () -> {
   }, () -> {
   });
   protected static final RenderStateShard.TexturingStateShard GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("glint_texturing", () -> {
      setupGlintTexturing(8.0F);
   }, () -> {
      RenderSystem.resetTextureMatrix();
   });
   protected static final RenderStateShard.TexturingStateShard ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> {
      setupGlintTexturing(0.16F);
   }, () -> {
      RenderSystem.resetTextureMatrix();
   });
   protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
   protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
   protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
   protected static final RenderStateShard.OverlayStateShard NO_OVERLAY = new RenderStateShard.OverlayStateShard(false);
   protected static final RenderStateShard.CullStateShard CULL = new RenderStateShard.CullStateShard(true);
   protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
   protected static final RenderStateShard.DepthTestStateShard NO_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("always", 519);
   protected static final RenderStateShard.DepthTestStateShard EQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("==", 514);
   protected static final RenderStateShard.DepthTestStateShard LEQUAL_DEPTH_TEST = new RenderStateShard.DepthTestStateShard("<=", 515);
   protected static final RenderStateShard.WriteMaskStateShard COLOR_DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(true, true);
   protected static final RenderStateShard.WriteMaskStateShard COLOR_WRITE = new RenderStateShard.WriteMaskStateShard(true, false);
   protected static final RenderStateShard.WriteMaskStateShard DEPTH_WRITE = new RenderStateShard.WriteMaskStateShard(false, true);
   protected static final RenderStateShard.LayeringStateShard NO_LAYERING = new RenderStateShard.LayeringStateShard("no_layering", () -> {
   }, () -> {
   });
   protected static final RenderStateShard.LayeringStateShard POLYGON_OFFSET_LAYERING = new RenderStateShard.LayeringStateShard("polygon_offset_layering", () -> {
      RenderSystem.polygonOffset(-1.0F, -10.0F);
      RenderSystem.enablePolygonOffset();
   }, () -> {
      RenderSystem.polygonOffset(0.0F, 0.0F);
      RenderSystem.disablePolygonOffset();
   });
   protected static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING = new RenderStateShard.LayeringStateShard("view_offset_z_layering", () -> {
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.scale(0.99975586F, 0.99975586F, 0.99975586F);
      RenderSystem.applyModelViewMatrix();
   }, () -> {
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   });
   protected static final RenderStateShard.OutputStateShard MAIN_TARGET = new RenderStateShard.OutputStateShard("main_target", () -> {
   }, () -> {
   });
   protected static final RenderStateShard.OutputStateShard OUTLINE_TARGET = new RenderStateShard.OutputStateShard("outline_target", () -> {
      Minecraft.getInstance().levelRenderer.entityTarget().bindWrite(false);
   }, () -> {
      Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
   });
   protected static final RenderStateShard.OutputStateShard TRANSLUCENT_TARGET = new RenderStateShard.OutputStateShard("translucent_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderStateShard.OutputStateShard PARTICLES_TARGET = new RenderStateShard.OutputStateShard("particles_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getParticlesTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderStateShard.OutputStateShard WEATHER_TARGET = new RenderStateShard.OutputStateShard("weather_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getWeatherTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderStateShard.OutputStateShard CLOUDS_TARGET = new RenderStateShard.OutputStateShard("clouds_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getCloudsTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderStateShard.OutputStateShard ITEM_ENTITY_TARGET = new RenderStateShard.OutputStateShard("item_entity_target", () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().levelRenderer.getItemEntityTarget().bindWrite(false);
      }

   }, () -> {
      if (Minecraft.useShaderTransparency()) {
         Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
      }

   });
   protected static final RenderStateShard.LineStateShard DEFAULT_LINE = new RenderStateShard.LineStateShard(OptionalDouble.of(1.0D));

   public RenderStateShard(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
      this.name = p_110161_;
      this.setupState = p_110162_;
      this.clearState = p_110163_;
   }

   public void setupRenderState() {
      this.setupState.run();
   }

   public void clearRenderState() {
      this.clearState.run();
   }

   public String toString() {
      return this.name;
   }

   private static void setupGlintTexturing(float p_110187_) {
      long i = Util.getMillis() * 8L;
      float f = (float)(i % 110000L) / 110000.0F;
      float f1 = (float)(i % 30000L) / 30000.0F;
      Matrix4f matrix4f = Matrix4f.createTranslateMatrix(-f, f1, 0.0F);
      matrix4f.multiply(Vector3f.ZP.rotationDegrees(10.0F));
      matrix4f.multiply(Matrix4f.createScaleMatrix(p_110187_, p_110187_, p_110187_));
      RenderSystem.setTextureMatrix(matrix4f);
   }

   @OnlyIn(Dist.CLIENT)
   public static class BooleanStateShard extends RenderStateShard {
      private final boolean enabled;

      public BooleanStateShard(String p_110229_, Runnable p_110230_, Runnable p_110231_, boolean p_110232_) {
         super(p_110229_, p_110230_, p_110231_);
         this.enabled = p_110232_;
      }

      public String toString() {
         return this.name + "[" + this.enabled + "]";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class CullStateShard extends RenderStateShard.BooleanStateShard {
      public CullStateShard(boolean p_110238_) {
         super("cull", () -> {
            if (!p_110238_) {
               RenderSystem.disableCull();
            }

         }, () -> {
            if (!p_110238_) {
               RenderSystem.enableCull();
            }

         }, p_110238_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class DepthTestStateShard extends RenderStateShard {
      private final String functionName;

      public DepthTestStateShard(String p_110246_, int p_110247_) {
         super("depth_test", () -> {
            if (p_110247_ != 519) {
               RenderSystem.enableDepthTest();
               RenderSystem.depthFunc(p_110247_);
            }

         }, () -> {
            if (p_110247_ != 519) {
               RenderSystem.disableDepthTest();
               RenderSystem.depthFunc(515);
            }

         });
         this.functionName = p_110246_;
      }

      public String toString() {
         return this.name + "[" + this.functionName + "]";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class EmptyTextureStateShard extends RenderStateShard {
      public EmptyTextureStateShard(Runnable p_173117_, Runnable p_173118_) {
         super("texture", p_173117_, p_173118_);
      }

      EmptyTextureStateShard() {
         super("texture", () -> {
         }, () -> {
         });
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return Optional.empty();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LayeringStateShard extends RenderStateShard {
      public LayeringStateShard(String p_110267_, Runnable p_110268_, Runnable p_110269_) {
         super(p_110267_, p_110268_, p_110269_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LightmapStateShard extends RenderStateShard.BooleanStateShard {
      public LightmapStateShard(boolean p_110271_) {
         super("lightmap", () -> {
            if (p_110271_) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
            }

         }, () -> {
            if (p_110271_) {
               Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
            }

         }, p_110271_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected static class LineStateShard extends RenderStateShard {
      private final OptionalDouble width;

      public LineStateShard(OptionalDouble p_110278_) {
         super("line_width", () -> {
            if (!Objects.equals(p_110278_, OptionalDouble.of(1.0D))) {
               if (p_110278_.isPresent()) {
                  RenderSystem.lineWidth((float)p_110278_.getAsDouble());
               } else {
                  RenderSystem.lineWidth(Math.max(2.5F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
               }
            }

         }, () -> {
            if (!Objects.equals(p_110278_, OptionalDouble.of(1.0D))) {
               RenderSystem.lineWidth(1.0F);
            }

         });
         this.width = p_110278_;
      }

      public String toString() {
         return this.name + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MultiTextureStateShard extends RenderStateShard.EmptyTextureStateShard {
      private final Optional<ResourceLocation> cutoutTexture;

      MultiTextureStateShard(ImmutableList<Triple<ResourceLocation, Boolean, Boolean>> p_173123_) {
         super(() -> {
            int i = 0;

            for(Triple<ResourceLocation, Boolean, Boolean> triple : p_173123_) {
               TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
               texturemanager.getTexture(triple.getLeft()).setFilter(triple.getMiddle(), triple.getRight());
               RenderSystem.setShaderTexture(i++, triple.getLeft());
            }

         }, () -> {
         });
         this.cutoutTexture = p_173123_.stream().findFirst().map(Triple::getLeft);
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.cutoutTexture;
      }

      public static RenderStateShard.MultiTextureStateShard.Builder builder() {
         return new RenderStateShard.MultiTextureStateShard.Builder();
      }

      @OnlyIn(Dist.CLIENT)
      public static final class Builder {
         private final ImmutableList.Builder<Triple<ResourceLocation, Boolean, Boolean>> builder = new ImmutableList.Builder<>();

         public RenderStateShard.MultiTextureStateShard.Builder add(ResourceLocation p_173133_, boolean p_173134_, boolean p_173135_) {
            this.builder.add(Triple.of(p_173133_, p_173134_, p_173135_));
            return this;
         }

         public RenderStateShard.MultiTextureStateShard build() {
            return new RenderStateShard.MultiTextureStateShard(this.builder.build());
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static final class OffsetTexturingStateShard extends RenderStateShard.TexturingStateShard {
      public OffsetTexturingStateShard(float p_110290_, float p_110291_) {
         super("offset_texturing", () -> {
            RenderSystem.setTextureMatrix(Matrix4f.createTranslateMatrix(p_110290_, p_110291_, 0.0F));
         }, () -> {
            RenderSystem.resetTextureMatrix();
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OutputStateShard extends RenderStateShard {
      public OutputStateShard(String p_110300_, Runnable p_110301_, Runnable p_110302_) {
         super(p_110300_, p_110301_, p_110302_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class OverlayStateShard extends RenderStateShard.BooleanStateShard {
      public OverlayStateShard(boolean p_110304_) {
         super("overlay", () -> {
            if (p_110304_) {
               Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
            }

         }, () -> {
            if (p_110304_) {
               Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
            }

         }, p_110304_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class ShaderStateShard extends RenderStateShard {
      private final Optional<Supplier<ShaderInstance>> shader;

      public ShaderStateShard(Supplier<ShaderInstance> p_173139_) {
         super("shader", () -> {
            RenderSystem.setShader(p_173139_);
         }, () -> {
         });
         this.shader = Optional.of(p_173139_);
      }

      public ShaderStateShard() {
         super("shader", () -> {
            RenderSystem.setShader(() -> {
               return null;
            });
         }, () -> {
         });
         this.shader = Optional.empty();
      }

      public String toString() {
         return this.name + "[" + this.shader + "]";
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TextureStateShard extends RenderStateShard.EmptyTextureStateShard {
      private final Optional<ResourceLocation> texture;
      protected boolean blur;
      protected boolean mipmap;

      public TextureStateShard(ResourceLocation p_110333_, boolean p_110334_, boolean p_110335_) {
         super(() -> {
            RenderSystem.enableTexture();
            TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
            texturemanager.getTexture(p_110333_).setFilter(p_110334_, p_110335_);
            RenderSystem.setShaderTexture(0, p_110333_);
         }, () -> {
         });
         this.texture = Optional.of(p_110333_);
         this.blur = p_110334_;
         this.mipmap = p_110335_;
      }

      public String toString() {
         return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
      }

      protected Optional<ResourceLocation> cutoutTexture() {
         return this.texture;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TexturingStateShard extends RenderStateShard {
      public TexturingStateShard(String p_110349_, Runnable p_110350_, Runnable p_110351_) {
         super(p_110349_, p_110350_, p_110351_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class TransparencyStateShard extends RenderStateShard {
      public TransparencyStateShard(String p_110353_, Runnable p_110354_, Runnable p_110355_) {
         super(p_110353_, p_110354_, p_110355_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WriteMaskStateShard extends RenderStateShard {
      private final boolean writeColor;
      private final boolean writeDepth;

      public WriteMaskStateShard(boolean p_110359_, boolean p_110360_) {
         super("write_mask_state", () -> {
            if (!p_110360_) {
               RenderSystem.depthMask(p_110360_);
            }

            if (!p_110359_) {
               RenderSystem.colorMask(p_110359_, p_110359_, p_110359_, p_110359_);
            }

         }, () -> {
            if (!p_110360_) {
               RenderSystem.depthMask(true);
            }

            if (!p_110359_) {
               RenderSystem.colorMask(true, true, true, true);
            }

         });
         this.writeColor = p_110359_;
         this.writeDepth = p_110360_;
      }

      public String toString() {
         return this.name + "[writeColor=" + this.writeColor + ", writeDepth=" + this.writeDepth + "]";
      }
   }
}