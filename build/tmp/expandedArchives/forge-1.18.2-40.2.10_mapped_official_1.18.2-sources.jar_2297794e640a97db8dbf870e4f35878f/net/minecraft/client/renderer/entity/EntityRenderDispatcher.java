package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private static final RenderType SHADOW_RENDER_TYPE = RenderType.entityShadow(new ResourceLocation("textures/misc/shadow.png"));
   public Map<EntityType<?>, EntityRenderer<?>> renderers = ImmutableMap.of();
   private Map<String, EntityRenderer<? extends Player>> playerRenderers = ImmutableMap.of();
   public final TextureManager textureManager;
   private Level level;
   public Camera camera;
   private Quaternion cameraOrientation;
   public Entity crosshairPickEntity;
   private final ItemRenderer itemRenderer;
   private final Font font;
   public final Options options;
   private final EntityModelSet entityModels;
   private boolean shouldRenderShadow = true;
   private boolean renderHitBoxes;

   public <E extends Entity> int getPackedLightCoords(E p_114395_, float p_114396_) {
      return this.getRenderer(p_114395_).getPackedLightCoords(p_114395_, p_114396_);
   }

   public EntityRenderDispatcher(TextureManager p_173998_, ItemRenderer p_173999_, Font p_174000_, Options p_174001_, EntityModelSet p_174002_) {
      this.textureManager = p_173998_;
      this.itemRenderer = p_173999_;
      this.font = p_174000_;
      this.options = p_174001_;
      this.entityModels = p_174002_;
   }

   public <T extends Entity> EntityRenderer<? super T> getRenderer(T p_114383_) {
      if (p_114383_ instanceof AbstractClientPlayer) {
         String s = ((AbstractClientPlayer)p_114383_).getModelName();
         EntityRenderer<? extends Player> entityrenderer = this.playerRenderers.get(s);
         return (EntityRenderer) (entityrenderer != null ? entityrenderer : this.playerRenderers.get("default"));
      } else {
         return (EntityRenderer) this.renderers.get(p_114383_.getType());
      }
   }

   public void prepare(Level p_114409_, Camera p_114410_, Entity p_114411_) {
      this.level = p_114409_;
      this.camera = p_114410_;
      this.cameraOrientation = p_114410_.rotation();
      this.crosshairPickEntity = p_114411_;
   }

   public void overrideCameraOrientation(Quaternion p_114413_) {
      this.cameraOrientation = p_114413_;
   }

   public void setRenderShadow(boolean p_114469_) {
      this.shouldRenderShadow = p_114469_;
   }

   public void setRenderHitBoxes(boolean p_114474_) {
      this.renderHitBoxes = p_114474_;
   }

   public boolean shouldRenderHitBoxes() {
      return this.renderHitBoxes;
   }

   public <E extends Entity> boolean shouldRender(E p_114398_, Frustum p_114399_, double p_114400_, double p_114401_, double p_114402_) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(p_114398_);
      return entityrenderer.shouldRender(p_114398_, p_114399_, p_114400_, p_114401_, p_114402_);
   }

   public <E extends Entity> void render(E p_114385_, double p_114386_, double p_114387_, double p_114388_, float p_114389_, float p_114390_, PoseStack p_114391_, MultiBufferSource p_114392_, int p_114393_) {
      EntityRenderer<? super E> entityrenderer = this.getRenderer(p_114385_);

      try {
         Vec3 vec3 = entityrenderer.getRenderOffset(p_114385_, p_114390_);
         double d2 = p_114386_ + vec3.x();
         double d3 = p_114387_ + vec3.y();
         double d0 = p_114388_ + vec3.z();
         p_114391_.pushPose();
         p_114391_.translate(d2, d3, d0);
         entityrenderer.render(p_114385_, p_114389_, p_114390_, p_114391_, p_114392_, p_114393_);
         if (p_114385_.displayFireAnimation()) {
            this.renderFlame(p_114391_, p_114392_, p_114385_);
         }

         p_114391_.translate(-vec3.x(), -vec3.y(), -vec3.z());
         if (this.options.entityShadows && this.shouldRenderShadow && entityrenderer.shadowRadius > 0.0F && !p_114385_.isInvisible()) {
            double d1 = this.distanceToSqr(p_114385_.getX(), p_114385_.getY(), p_114385_.getZ());
            float f = (float)((1.0D - d1 / 256.0D) * (double)entityrenderer.shadowStrength);
            if (f > 0.0F) {
               renderShadow(p_114391_, p_114392_, p_114385_, f, p_114390_, this.level, entityrenderer.shadowRadius);
            }
         }

         if (this.renderHitBoxes && !p_114385_.isInvisible() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            renderHitbox(p_114391_, p_114392_.getBuffer(RenderType.lines()), p_114385_, p_114390_);
         }

         p_114391_.popPose();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Rendering entity in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being rendered");
         p_114385_.fillCrashReportCategory(crashreportcategory);
         CrashReportCategory crashreportcategory1 = crashreport.addCategory("Renderer details");
         crashreportcategory1.setDetail("Assigned renderer", entityrenderer);
         crashreportcategory1.setDetail("Location", CrashReportCategory.formatLocation(this.level, p_114386_, p_114387_, p_114388_));
         crashreportcategory1.setDetail("Rotation", p_114389_);
         crashreportcategory1.setDetail("Delta", p_114390_);
         throw new ReportedException(crashreport);
      }
   }

   private static void renderHitbox(PoseStack p_114442_, VertexConsumer p_114443_, Entity p_114444_, float p_114445_) {
      AABB aabb = p_114444_.getBoundingBox().move(-p_114444_.getX(), -p_114444_.getY(), -p_114444_.getZ());
      LevelRenderer.renderLineBox(p_114442_, p_114443_, aabb, 1.0F, 1.0F, 1.0F, 1.0F);
      if (p_114444_.isMultipartEntity()) {
         double d0 = -Mth.lerp((double)p_114445_, p_114444_.xOld, p_114444_.getX());
         double d1 = -Mth.lerp((double)p_114445_, p_114444_.yOld, p_114444_.getY());
         double d2 = -Mth.lerp((double)p_114445_, p_114444_.zOld, p_114444_.getZ());

         for(net.minecraftforge.entity.PartEntity<?> enderdragonpart : p_114444_.getParts()) {
            p_114442_.pushPose();
            double d3 = d0 + Mth.lerp((double)p_114445_, enderdragonpart.xOld, enderdragonpart.getX());
            double d4 = d1 + Mth.lerp((double)p_114445_, enderdragonpart.yOld, enderdragonpart.getY());
            double d5 = d2 + Mth.lerp((double)p_114445_, enderdragonpart.zOld, enderdragonpart.getZ());
            p_114442_.translate(d3, d4, d5);
            LevelRenderer.renderLineBox(p_114442_, p_114443_, enderdragonpart.getBoundingBox().move(-enderdragonpart.getX(), -enderdragonpart.getY(), -enderdragonpart.getZ()), 0.25F, 1.0F, 0.0F, 1.0F);
            p_114442_.popPose();
         }
      }

      if (p_114444_ instanceof LivingEntity) {
         float f = 0.01F;
         LevelRenderer.renderLineBox(p_114442_, p_114443_, aabb.minX, (double)(p_114444_.getEyeHeight() - 0.01F), aabb.minZ, aabb.maxX, (double)(p_114444_.getEyeHeight() + 0.01F), aabb.maxZ, 1.0F, 0.0F, 0.0F, 1.0F);
      }

      Vec3 vec3 = p_114444_.getViewVector(p_114445_);
      Matrix4f matrix4f = p_114442_.last().pose();
      Matrix3f matrix3f = p_114442_.last().normal();
      p_114443_.vertex(matrix4f, 0.0F, p_114444_.getEyeHeight(), 0.0F).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();
      p_114443_.vertex(matrix4f, (float)(vec3.x * 2.0D), (float)((double)p_114444_.getEyeHeight() + vec3.y * 2.0D), (float)(vec3.z * 2.0D)).color(0, 0, 255, 255).normal(matrix3f, (float)vec3.x, (float)vec3.y, (float)vec3.z).endVertex();
   }

   private void renderFlame(PoseStack p_114454_, MultiBufferSource p_114455_, Entity p_114456_) {
      TextureAtlasSprite textureatlassprite = ModelBakery.FIRE_0.sprite();
      TextureAtlasSprite textureatlassprite1 = ModelBakery.FIRE_1.sprite();
      p_114454_.pushPose();
      float f = p_114456_.getBbWidth() * 1.4F;
      p_114454_.scale(f, f, f);
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = p_114456_.getBbHeight() / f;
      float f4 = 0.0F;
      p_114454_.mulPose(Vector3f.YP.rotationDegrees(-this.camera.getYRot()));
      p_114454_.translate(0.0D, 0.0D, (double)(-0.3F + (float)((int)f3) * 0.02F));
      float f5 = 0.0F;
      int i = 0;
      VertexConsumer vertexconsumer = p_114455_.getBuffer(Sheets.cutoutBlockSheet());

      for(PoseStack.Pose posestack$pose = p_114454_.last(); f3 > 0.0F; ++i) {
         TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite : textureatlassprite1;
         float f6 = textureatlassprite2.getU0();
         float f7 = textureatlassprite2.getV0();
         float f8 = textureatlassprite2.getU1();
         float f9 = textureatlassprite2.getV1();
         if (i / 2 % 2 == 0) {
            float f10 = f8;
            f8 = f6;
            f6 = f10;
         }

         fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
         fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
         fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
         fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
         f3 -= 0.45F;
         f4 -= 0.45F;
         f1 *= 0.9F;
         f5 += 0.03F;
      }

      p_114454_.popPose();
   }

   private static void fireVertex(PoseStack.Pose p_114415_, VertexConsumer p_114416_, float p_114417_, float p_114418_, float p_114419_, float p_114420_, float p_114421_) {
      p_114416_.vertex(p_114415_.pose(), p_114417_, p_114418_, p_114419_).color(255, 255, 255, 255).uv(p_114420_, p_114421_).overlayCoords(0, 10).uv2(240).normal(p_114415_.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   private static void renderShadow(PoseStack p_114458_, MultiBufferSource p_114459_, Entity p_114460_, float p_114461_, float p_114462_, LevelReader p_114463_, float p_114464_) {
      float f = p_114464_;
      if (p_114460_ instanceof Mob) {
         Mob mob = (Mob)p_114460_;
         if (mob.isBaby()) {
            f = p_114464_ * 0.5F;
         }
      }

      double d2 = Mth.lerp((double)p_114462_, p_114460_.xOld, p_114460_.getX());
      double d0 = Mth.lerp((double)p_114462_, p_114460_.yOld, p_114460_.getY());
      double d1 = Mth.lerp((double)p_114462_, p_114460_.zOld, p_114460_.getZ());
      int i = Mth.floor(d2 - (double)f);
      int j = Mth.floor(d2 + (double)f);
      int k = Mth.floor(d0 - (double)f);
      int l = Mth.floor(d0);
      int i1 = Mth.floor(d1 - (double)f);
      int j1 = Mth.floor(d1 + (double)f);
      PoseStack.Pose posestack$pose = p_114458_.last();
      VertexConsumer vertexconsumer = p_114459_.getBuffer(SHADOW_RENDER_TYPE);

      for(BlockPos blockpos : BlockPos.betweenClosed(new BlockPos(i, k, i1), new BlockPos(j, l, j1))) {
         renderBlockShadow(posestack$pose, vertexconsumer, p_114463_, blockpos, d2, d0, d1, f, p_114461_);
      }

   }

   private static void renderBlockShadow(PoseStack.Pose p_114432_, VertexConsumer p_114433_, LevelReader p_114434_, BlockPos p_114435_, double p_114436_, double p_114437_, double p_114438_, float p_114439_, float p_114440_) {
      BlockPos blockpos = p_114435_.below();
      BlockState blockstate = p_114434_.getBlockState(blockpos);
      if (blockstate.getRenderShape() != RenderShape.INVISIBLE && p_114434_.getMaxLocalRawBrightness(p_114435_) > 3) {
         if (blockstate.isCollisionShapeFullBlock(p_114434_, blockpos)) {
            VoxelShape voxelshape = blockstate.getShape(p_114434_, p_114435_.below());
            if (!voxelshape.isEmpty()) {
               float f = (float)(((double)p_114440_ - (p_114437_ - (double)p_114435_.getY()) / 2.0D) * 0.5D * (double)p_114434_.getBrightness(p_114435_));
               if (f >= 0.0F) {
                  if (f > 1.0F) {
                     f = 1.0F;
                  }

                  AABB aabb = voxelshape.bounds();
                  double d0 = (double)p_114435_.getX() + aabb.minX;
                  double d1 = (double)p_114435_.getX() + aabb.maxX;
                  double d2 = (double)p_114435_.getY() + aabb.minY;
                  double d3 = (double)p_114435_.getZ() + aabb.minZ;
                  double d4 = (double)p_114435_.getZ() + aabb.maxZ;
                  float f1 = (float)(d0 - p_114436_);
                  float f2 = (float)(d1 - p_114436_);
                  float f3 = (float)(d2 - p_114437_);
                  float f4 = (float)(d3 - p_114438_);
                  float f5 = (float)(d4 - p_114438_);
                  float f6 = -f1 / 2.0F / p_114439_ + 0.5F;
                  float f7 = -f2 / 2.0F / p_114439_ + 0.5F;
                  float f8 = -f4 / 2.0F / p_114439_ + 0.5F;
                  float f9 = -f5 / 2.0F / p_114439_ + 0.5F;
                  shadowVertex(p_114432_, p_114433_, f, f1, f3, f4, f6, f8);
                  shadowVertex(p_114432_, p_114433_, f, f1, f3, f5, f6, f9);
                  shadowVertex(p_114432_, p_114433_, f, f2, f3, f5, f7, f9);
                  shadowVertex(p_114432_, p_114433_, f, f2, f3, f4, f7, f8);
               }

            }
         }
      }
   }

   private static void shadowVertex(PoseStack.Pose p_114423_, VertexConsumer p_114424_, float p_114425_, float p_114426_, float p_114427_, float p_114428_, float p_114429_, float p_114430_) {
      p_114424_.vertex(p_114423_.pose(), p_114426_, p_114427_, p_114428_).color(1.0F, 1.0F, 1.0F, p_114425_).uv(p_114429_, p_114430_).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(p_114423_.normal(), 0.0F, 1.0F, 0.0F).endVertex();
   }

   public void setLevel(@Nullable Level p_114407_) {
      this.level = p_114407_;
      if (p_114407_ == null) {
         this.camera = null;
      }

   }

   public double distanceToSqr(Entity p_114472_) {
      return this.camera.getPosition().distanceToSqr(p_114472_.position());
   }

   public double distanceToSqr(double p_114379_, double p_114380_, double p_114381_) {
      return this.camera.getPosition().distanceToSqr(p_114379_, p_114380_, p_114381_);
   }

   public Quaternion cameraOrientation() {
      return this.cameraOrientation;
   }

   public Map<String, EntityRenderer<? extends Player>> getSkinMap() {
      return java.util.Collections.unmodifiableMap(playerRenderers);
   }

   public void onResourceManagerReload(ResourceManager p_174004_) {
      EntityRendererProvider.Context entityrendererprovider$context = new EntityRendererProvider.Context(this, this.itemRenderer, p_174004_, this.entityModels, this.font);
      this.renderers = EntityRenderers.createEntityRenderers(entityrendererprovider$context);
      this.playerRenderers = EntityRenderers.createPlayerRenderers(entityrendererprovider$context);
      net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.EntityRenderersEvent.AddLayers(renderers, playerRenderers));
   }
}
