package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TheEndPortalRenderer<T extends TheEndPortalBlockEntity> implements BlockEntityRenderer<T> {
   public static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
   public static final ResourceLocation END_PORTAL_LOCATION = new ResourceLocation("textures/entity/end_portal.png");

   public TheEndPortalRenderer(BlockEntityRendererProvider.Context p_173689_) {
   }

   public void render(T p_112650_, float p_112651_, PoseStack p_112652_, MultiBufferSource p_112653_, int p_112654_, int p_112655_) {
      Matrix4f matrix4f = p_112652_.last().pose();
      this.renderCube(p_112650_, matrix4f, p_112653_.getBuffer(this.renderType()));
   }

   private void renderCube(T p_173691_, Matrix4f p_173692_, VertexConsumer p_173693_) {
      float f = this.getOffsetDown();
      float f1 = this.getOffsetUp();
      this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
      this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
      this.renderFace(p_173691_, p_173692_, p_173693_, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.EAST);
      this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
      this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
      this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, f1, f1, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
   }

   private void renderFace(T p_173695_, Matrix4f p_173696_, VertexConsumer p_173697_, float p_173698_, float p_173699_, float p_173700_, float p_173701_, float p_173702_, float p_173703_, float p_173704_, float p_173705_, Direction p_173706_) {
      if (p_173695_.shouldRenderFace(p_173706_)) {
         p_173697_.vertex(p_173696_, p_173698_, p_173700_, p_173702_).endVertex();
         p_173697_.vertex(p_173696_, p_173699_, p_173700_, p_173703_).endVertex();
         p_173697_.vertex(p_173696_, p_173699_, p_173701_, p_173704_).endVertex();
         p_173697_.vertex(p_173696_, p_173698_, p_173701_, p_173705_).endVertex();
      }

   }

   protected float getOffsetUp() {
      return 0.75F;
   }

   protected float getOffsetDown() {
      return 0.375F;
   }

   protected RenderType renderType() {
      return RenderType.endPortal();
   }
}