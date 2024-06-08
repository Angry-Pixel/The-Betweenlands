package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
   protected static final float NAMETAG_SCALE = 0.025F;
   protected final EntityRenderDispatcher entityRenderDispatcher;
   private final Font font;
   protected float shadowRadius;
   protected float shadowStrength = 1.0F;

   protected EntityRenderer(EntityRendererProvider.Context p_174008_) {
      this.entityRenderDispatcher = p_174008_.getEntityRenderDispatcher();
      this.font = p_174008_.getFont();
   }

   public final int getPackedLightCoords(T p_114506_, float p_114507_) {
      BlockPos blockpos = new BlockPos(p_114506_.getLightProbePosition(p_114507_));
      return LightTexture.pack(this.getBlockLightLevel(p_114506_, blockpos), this.getSkyLightLevel(p_114506_, blockpos));
   }

   protected int getSkyLightLevel(T p_114509_, BlockPos p_114510_) {
      return p_114509_.level.getBrightness(LightLayer.SKY, p_114510_);
   }

   protected int getBlockLightLevel(T p_114496_, BlockPos p_114497_) {
      return p_114496_.isOnFire() ? 15 : p_114496_.level.getBrightness(LightLayer.BLOCK, p_114497_);
   }

   public boolean shouldRender(T p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
      if (!p_114491_.shouldRender(p_114493_, p_114494_, p_114495_)) {
         return false;
      } else if (p_114491_.noCulling) {
         return true;
      } else {
         AABB aabb = p_114491_.getBoundingBoxForCulling().inflate(0.5D);
         if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
            aabb = new AABB(p_114491_.getX() - 2.0D, p_114491_.getY() - 2.0D, p_114491_.getZ() - 2.0D, p_114491_.getX() + 2.0D, p_114491_.getY() + 2.0D, p_114491_.getZ() + 2.0D);
         }

         return p_114492_.isVisible(aabb);
      }
   }

   public Vec3 getRenderOffset(T p_114483_, float p_114484_) {
      return Vec3.ZERO;
   }

   public void render(T p_114485_, float p_114486_, float p_114487_, PoseStack p_114488_, MultiBufferSource p_114489_, int p_114490_) {
      net.minecraftforge.client.event.RenderNameplateEvent renderNameplateEvent = new net.minecraftforge.client.event.RenderNameplateEvent(p_114485_, p_114485_.getDisplayName(), this, p_114488_, p_114489_, p_114490_, p_114487_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(renderNameplateEvent);
      if (renderNameplateEvent.getResult() != net.minecraftforge.eventbus.api.Event.Result.DENY && (renderNameplateEvent.getResult() == net.minecraftforge.eventbus.api.Event.Result.ALLOW || this.shouldShowName(p_114485_))) {
         this.renderNameTag(p_114485_, renderNameplateEvent.getContent(), p_114488_, p_114489_, p_114490_);
      }
   }

   protected boolean shouldShowName(T p_114504_) {
      return p_114504_.shouldShowName() && p_114504_.hasCustomName();
   }

   public abstract ResourceLocation getTextureLocation(T p_114482_);

   public Font getFont() {
      return this.font;
   }

   protected void renderNameTag(T p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_114498_);
      if (net.minecraftforge.client.ForgeHooksClient.isNameplateInRenderDistance(p_114498_, d0)) {
         boolean flag = !p_114498_.isDiscrete();
         float f = p_114498_.getBbHeight() + 0.5F;
         int i = "deadmau5".equals(p_114499_.getString()) ? -10 : 0;
         p_114500_.pushPose();
         p_114500_.translate(0.0D, (double)f, 0.0D);
         p_114500_.mulPose(this.entityRenderDispatcher.cameraOrientation());
         p_114500_.scale(-0.025F, -0.025F, 0.025F);
         Matrix4f matrix4f = p_114500_.last().pose();
         float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
         int j = (int)(f1 * 255.0F) << 24;
         Font font = this.getFont();
         float f2 = (float)(-font.width(p_114499_) / 2);
         font.drawInBatch(p_114499_, f2, (float)i, 553648127, false, matrix4f, p_114501_, flag, j, p_114502_);
         if (flag) {
            font.drawInBatch(p_114499_, f2, (float)i, -1, false, matrix4f, p_114501_, false, 0, p_114502_);
         }

         p_114500_.popPose();
      }
   }
}
