package net.minecraft.client.renderer;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemInHandRenderer {
   private static final RenderType MAP_BACKGROUND = RenderType.text(new ResourceLocation("textures/map/map_background.png"));
   private static final RenderType MAP_BACKGROUND_CHECKERBOARD = RenderType.text(new ResourceLocation("textures/map/map_background_checkerboard.png"));
   private static final float ITEM_SWING_X_POS_SCALE = -0.4F;
   private static final float ITEM_SWING_Y_POS_SCALE = 0.2F;
   private static final float ITEM_SWING_Z_POS_SCALE = -0.2F;
   private static final float ITEM_HEIGHT_SCALE = -0.6F;
   private static final float ITEM_POS_X = 0.56F;
   private static final float ITEM_POS_Y = -0.52F;
   private static final float ITEM_POS_Z = -0.72F;
   private static final float ITEM_PRESWING_ROT_Y = 45.0F;
   private static final float ITEM_SWING_X_ROT_AMOUNT = -80.0F;
   private static final float ITEM_SWING_Y_ROT_AMOUNT = -20.0F;
   private static final float ITEM_SWING_Z_ROT_AMOUNT = -20.0F;
   private static final float EAT_JIGGLE_X_ROT_AMOUNT = 10.0F;
   private static final float EAT_JIGGLE_Y_ROT_AMOUNT = 90.0F;
   private static final float EAT_JIGGLE_Z_ROT_AMOUNT = 30.0F;
   private static final float EAT_JIGGLE_X_POS_SCALE = 0.6F;
   private static final float EAT_JIGGLE_Y_POS_SCALE = -0.5F;
   private static final float EAT_JIGGLE_Z_POS_SCALE = 0.0F;
   private static final double EAT_JIGGLE_EXPONENT = 27.0D;
   private static final float EAT_EXTRA_JIGGLE_CUTOFF = 0.8F;
   private static final float EAT_EXTRA_JIGGLE_SCALE = 0.1F;
   private static final float ARM_SWING_X_POS_SCALE = -0.3F;
   private static final float ARM_SWING_Y_POS_SCALE = 0.4F;
   private static final float ARM_SWING_Z_POS_SCALE = -0.4F;
   private static final float ARM_SWING_Y_ROT_AMOUNT = 70.0F;
   private static final float ARM_SWING_Z_ROT_AMOUNT = -20.0F;
   private static final float ARM_HEIGHT_SCALE = -0.6F;
   private static final float ARM_POS_SCALE = 0.8F;
   private static final float ARM_POS_X = 0.8F;
   private static final float ARM_POS_Y = -0.75F;
   private static final float ARM_POS_Z = -0.9F;
   private static final float ARM_PRESWING_ROT_Y = 45.0F;
   private static final float ARM_PREROTATION_X_OFFSET = -1.0F;
   private static final float ARM_PREROTATION_Y_OFFSET = 3.6F;
   private static final float ARM_PREROTATION_Z_OFFSET = 3.5F;
   private static final float ARM_POSTROTATION_X_OFFSET = 5.6F;
   private static final int ARM_ROT_X = 200;
   private static final int ARM_ROT_Y = -135;
   private static final int ARM_ROT_Z = 120;
   private static final float MAP_SWING_X_POS_SCALE = -0.4F;
   private static final float MAP_SWING_Z_POS_SCALE = -0.2F;
   private static final float MAP_HANDS_POS_X = 0.0F;
   private static final float MAP_HANDS_POS_Y = 0.04F;
   private static final float MAP_HANDS_POS_Z = -0.72F;
   private static final float MAP_HANDS_HEIGHT_SCALE = -1.2F;
   private static final float MAP_HANDS_TILT_SCALE = -0.5F;
   private static final float MAP_PLAYER_PITCH_SCALE = 45.0F;
   private static final float MAP_HANDS_Z_ROT_AMOUNT = -85.0F;
   private static final float MAPHAND_X_ROT_AMOUNT = 45.0F;
   private static final float MAPHAND_Y_ROT_AMOUNT = 92.0F;
   private static final float MAPHAND_Z_ROT_AMOUNT = -41.0F;
   private static final float MAP_HAND_X_POS = 0.3F;
   private static final float MAP_HAND_Y_POS = -1.1F;
   private static final float MAP_HAND_Z_POS = 0.45F;
   private static final float MAP_SWING_X_ROT_AMOUNT = 20.0F;
   private static final float MAP_PRE_ROT_SCALE = 0.38F;
   private static final float MAP_GLOBAL_X_POS = -0.5F;
   private static final float MAP_GLOBAL_Y_POS = -0.5F;
   private static final float MAP_GLOBAL_Z_POS = 0.0F;
   private static final float MAP_FINAL_SCALE = 0.0078125F;
   private static final int MAP_BORDER = 7;
   private static final int MAP_HEIGHT = 128;
   private static final int MAP_WIDTH = 128;
   private static final float BOW_CHARGE_X_POS_SCALE = 0.0F;
   private static final float BOW_CHARGE_Y_POS_SCALE = 0.0F;
   private static final float BOW_CHARGE_Z_POS_SCALE = 0.04F;
   private static final float BOW_CHARGE_SHAKE_X_SCALE = 0.0F;
   private static final float BOW_CHARGE_SHAKE_Y_SCALE = 0.004F;
   private static final float BOW_CHARGE_SHAKE_Z_SCALE = 0.0F;
   private static final float BOW_CHARGE_Z_SCALE = 0.2F;
   private static final float BOW_MIN_SHAKE_CHARGE = 0.1F;
   private final Minecraft minecraft;
   private ItemStack mainHandItem = ItemStack.EMPTY;
   private ItemStack offHandItem = ItemStack.EMPTY;
   private float mainHandHeight;
   private float oMainHandHeight;
   private float offHandHeight;
   private float oOffHandHeight;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;

   public ItemInHandRenderer(Minecraft p_109310_) {
      this.minecraft = p_109310_;
      this.entityRenderDispatcher = p_109310_.getEntityRenderDispatcher();
      this.itemRenderer = p_109310_.getItemRenderer();
   }

   public void renderItem(LivingEntity p_109323_, ItemStack p_109324_, ItemTransforms.TransformType p_109325_, boolean p_109326_, PoseStack p_109327_, MultiBufferSource p_109328_, int p_109329_) {
      if (!p_109324_.isEmpty()) {
         this.itemRenderer.renderStatic(p_109323_, p_109324_, p_109325_, p_109326_, p_109327_, p_109328_, p_109323_.level, p_109329_, OverlayTexture.NO_OVERLAY, p_109323_.getId() + p_109325_.ordinal());
      }
   }

   private float calculateMapTilt(float p_109313_) {
      float f = 1.0F - p_109313_ / 45.0F + 0.1F;
      f = Mth.clamp(f, 0.0F, 1.0F);
      return -Mth.cos(f * (float)Math.PI) * 0.5F + 0.5F;
   }

   private void renderMapHand(PoseStack p_109362_, MultiBufferSource p_109363_, int p_109364_, HumanoidArm p_109365_) {
      RenderSystem.setShaderTexture(0, this.minecraft.player.getSkinTextureLocation());
      PlayerRenderer playerrenderer = (PlayerRenderer)this.entityRenderDispatcher.<AbstractClientPlayer>getRenderer(this.minecraft.player);
      p_109362_.pushPose();
      float f = p_109365_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      p_109362_.mulPose(Vector3f.YP.rotationDegrees(92.0F));
      p_109362_.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      p_109362_.mulPose(Vector3f.ZP.rotationDegrees(f * -41.0F));
      p_109362_.translate((double)(f * 0.3F), (double)-1.1F, (double)0.45F);
      if (p_109365_ == HumanoidArm.RIGHT) {
         playerrenderer.renderRightHand(p_109362_, p_109363_, p_109364_, this.minecraft.player);
      } else {
         playerrenderer.renderLeftHand(p_109362_, p_109363_, p_109364_, this.minecraft.player);
      }

      p_109362_.popPose();
   }

   private void renderOneHandedMap(PoseStack p_109354_, MultiBufferSource p_109355_, int p_109356_, float p_109357_, HumanoidArm p_109358_, float p_109359_, ItemStack p_109360_) {
      float f = p_109358_ == HumanoidArm.RIGHT ? 1.0F : -1.0F;
      p_109354_.translate((double)(f * 0.125F), -0.125D, 0.0D);
      if (!this.minecraft.player.isInvisible()) {
         p_109354_.pushPose();
         p_109354_.mulPose(Vector3f.ZP.rotationDegrees(f * 10.0F));
         this.renderPlayerArm(p_109354_, p_109355_, p_109356_, p_109357_, p_109359_, p_109358_);
         p_109354_.popPose();
      }

      p_109354_.pushPose();
      p_109354_.translate((double)(f * 0.51F), (double)(-0.08F + p_109357_ * -1.2F), -0.75D);
      float f1 = Mth.sqrt(p_109359_);
      float f2 = Mth.sin(f1 * (float)Math.PI);
      float f3 = -0.5F * f2;
      float f4 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
      float f5 = -0.3F * Mth.sin(p_109359_ * (float)Math.PI);
      p_109354_.translate((double)(f * f3), (double)(f4 - 0.3F * f2), (double)f5);
      p_109354_.mulPose(Vector3f.XP.rotationDegrees(f2 * -45.0F));
      p_109354_.mulPose(Vector3f.YP.rotationDegrees(f * f2 * -30.0F));
      this.renderMap(p_109354_, p_109355_, p_109356_, p_109360_);
      p_109354_.popPose();
   }

   private void renderTwoHandedMap(PoseStack p_109340_, MultiBufferSource p_109341_, int p_109342_, float p_109343_, float p_109344_, float p_109345_) {
      float f = Mth.sqrt(p_109345_);
      float f1 = -0.2F * Mth.sin(p_109345_ * (float)Math.PI);
      float f2 = -0.4F * Mth.sin(f * (float)Math.PI);
      p_109340_.translate(0.0D, (double)(-f1 / 2.0F), (double)f2);
      float f3 = this.calculateMapTilt(p_109343_);
      p_109340_.translate(0.0D, (double)(0.04F + p_109344_ * -1.2F + f3 * -0.5F), (double)-0.72F);
      p_109340_.mulPose(Vector3f.XP.rotationDegrees(f3 * -85.0F));
      if (!this.minecraft.player.isInvisible()) {
         p_109340_.pushPose();
         p_109340_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         this.renderMapHand(p_109340_, p_109341_, p_109342_, HumanoidArm.RIGHT);
         this.renderMapHand(p_109340_, p_109341_, p_109342_, HumanoidArm.LEFT);
         p_109340_.popPose();
      }

      float f4 = Mth.sin(f * (float)Math.PI);
      p_109340_.mulPose(Vector3f.XP.rotationDegrees(f4 * 20.0F));
      p_109340_.scale(2.0F, 2.0F, 2.0F);
      this.renderMap(p_109340_, p_109341_, p_109342_, this.mainHandItem);
   }

   private void renderMap(PoseStack p_109367_, MultiBufferSource p_109368_, int p_109369_, ItemStack p_109370_) {
      p_109367_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
      p_109367_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      p_109367_.scale(0.38F, 0.38F, 0.38F);
      p_109367_.translate(-0.5D, -0.5D, 0.0D);
      p_109367_.scale(0.0078125F, 0.0078125F, 0.0078125F);
      Integer integer = MapItem.getMapId(p_109370_);
      MapItemSavedData mapitemsaveddata = MapItem.getSavedData(integer, this.minecraft.level);
      VertexConsumer vertexconsumer = p_109368_.getBuffer(mapitemsaveddata == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
      Matrix4f matrix4f = p_109367_.last().pose();
      vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_109369_).endVertex();
      vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_109369_).endVertex();
      vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_109369_).endVertex();
      vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_109369_).endVertex();
      if (mapitemsaveddata != null) {
         this.minecraft.gameRenderer.getMapRenderer().render(p_109367_, p_109368_, integer, mapitemsaveddata, false, p_109369_);
      }

   }

   private void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_) {
      boolean flag = p_109352_ != HumanoidArm.LEFT;
      float f = flag ? 1.0F : -1.0F;
      float f1 = Mth.sqrt(p_109351_);
      float f2 = -0.3F * Mth.sin(f1 * (float)Math.PI);
      float f3 = 0.4F * Mth.sin(f1 * ((float)Math.PI * 2F));
      float f4 = -0.4F * Mth.sin(p_109351_ * (float)Math.PI);
      p_109347_.translate((double)(f * (f2 + 0.64000005F)), (double)(f3 + -0.6F + p_109350_ * -0.6F), (double)(f4 + -0.71999997F));
      p_109347_.mulPose(Vector3f.YP.rotationDegrees(f * 45.0F));
      float f5 = Mth.sin(p_109351_ * p_109351_ * (float)Math.PI);
      float f6 = Mth.sin(f1 * (float)Math.PI);
      p_109347_.mulPose(Vector3f.YP.rotationDegrees(f * f6 * 70.0F));
      p_109347_.mulPose(Vector3f.ZP.rotationDegrees(f * f5 * -20.0F));
      AbstractClientPlayer abstractclientplayer = this.minecraft.player;
      RenderSystem.setShaderTexture(0, abstractclientplayer.getSkinTextureLocation());
      p_109347_.translate((double)(f * -1.0F), (double)3.6F, 3.5D);
      p_109347_.mulPose(Vector3f.ZP.rotationDegrees(f * 120.0F));
      p_109347_.mulPose(Vector3f.XP.rotationDegrees(200.0F));
      p_109347_.mulPose(Vector3f.YP.rotationDegrees(f * -135.0F));
      p_109347_.translate((double)(f * 5.6F), 0.0D, 0.0D);
      PlayerRenderer playerrenderer = (PlayerRenderer)this.entityRenderDispatcher.<AbstractClientPlayer>getRenderer(abstractclientplayer);
      if (flag) {
         playerrenderer.renderRightHand(p_109347_, p_109348_, p_109349_, abstractclientplayer);
      } else {
         playerrenderer.renderLeftHand(p_109347_, p_109348_, p_109349_, abstractclientplayer);
      }

   }

   private void applyEatTransform(PoseStack p_109331_, float p_109332_, HumanoidArm p_109333_, ItemStack p_109334_) {
      float f = (float)this.minecraft.player.getUseItemRemainingTicks() - p_109332_ + 1.0F;
      float f1 = f / (float)p_109334_.getUseDuration();
      if (f1 < 0.8F) {
         float f2 = Mth.abs(Mth.cos(f / 4.0F * (float)Math.PI) * 0.1F);
         p_109331_.translate(0.0D, (double)f2, 0.0D);
      }

      float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
      int i = p_109333_ == HumanoidArm.RIGHT ? 1 : -1;
      p_109331_.translate((double)(f3 * 0.6F * (float)i), (double)(f3 * -0.5F), (double)(f3 * 0.0F));
      p_109331_.mulPose(Vector3f.YP.rotationDegrees((float)i * f3 * 90.0F));
      p_109331_.mulPose(Vector3f.XP.rotationDegrees(f3 * 10.0F));
      p_109331_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f3 * 30.0F));
   }

   private void applyItemArmAttackTransform(PoseStack p_109336_, HumanoidArm p_109337_, float p_109338_) {
      int i = p_109337_ == HumanoidArm.RIGHT ? 1 : -1;
      float f = Mth.sin(p_109338_ * p_109338_ * (float)Math.PI);
      p_109336_.mulPose(Vector3f.YP.rotationDegrees((float)i * (45.0F + f * -20.0F)));
      float f1 = Mth.sin(Mth.sqrt(p_109338_) * (float)Math.PI);
      p_109336_.mulPose(Vector3f.ZP.rotationDegrees((float)i * f1 * -20.0F));
      p_109336_.mulPose(Vector3f.XP.rotationDegrees(f1 * -80.0F));
      p_109336_.mulPose(Vector3f.YP.rotationDegrees((float)i * -45.0F));
   }

   private void applyItemArmTransform(PoseStack p_109383_, HumanoidArm p_109384_, float p_109385_) {
      int i = p_109384_ == HumanoidArm.RIGHT ? 1 : -1;
      p_109383_.translate((double)((float)i * 0.56F), (double)(-0.52F + p_109385_ * -0.6F), (double)-0.72F);
   }

   public void renderHandsWithItems(float p_109315_, PoseStack p_109316_, MultiBufferSource.BufferSource p_109317_, LocalPlayer p_109318_, int p_109319_) {
      float f = p_109318_.getAttackAnim(p_109315_);
      InteractionHand interactionhand = MoreObjects.firstNonNull(p_109318_.swingingArm, InteractionHand.MAIN_HAND);
      float f1 = Mth.lerp(p_109315_, p_109318_.xRotO, p_109318_.getXRot());
      ItemInHandRenderer.HandRenderSelection iteminhandrenderer$handrenderselection = evaluateWhichHandsToRender(p_109318_);
      float f2 = Mth.lerp(p_109315_, p_109318_.xBobO, p_109318_.xBob);
      float f3 = Mth.lerp(p_109315_, p_109318_.yBobO, p_109318_.yBob);
      p_109316_.mulPose(Vector3f.XP.rotationDegrees((p_109318_.getViewXRot(p_109315_) - f2) * 0.1F));
      p_109316_.mulPose(Vector3f.YP.rotationDegrees((p_109318_.getViewYRot(p_109315_) - f3) * 0.1F));
      if (iteminhandrenderer$handrenderselection.renderMainHand) {
         float f4 = interactionhand == InteractionHand.MAIN_HAND ? f : 0.0F;
         float f5 = 1.0F - Mth.lerp(p_109315_, this.oMainHandHeight, this.mainHandHeight);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(InteractionHand.MAIN_HAND, p_109316_, p_109317_, p_109319_, p_109315_, f1, f4, f5, this.mainHandItem))
         this.renderArmWithItem(p_109318_, p_109315_, f1, InteractionHand.MAIN_HAND, f4, this.mainHandItem, f5, p_109316_, p_109317_, p_109319_);
      }

      if (iteminhandrenderer$handrenderselection.renderOffHand) {
         float f6 = interactionhand == InteractionHand.OFF_HAND ? f : 0.0F;
         float f7 = 1.0F - Mth.lerp(p_109315_, this.oOffHandHeight, this.offHandHeight);
         if(!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonHand(InteractionHand.OFF_HAND, p_109316_, p_109317_, p_109319_, p_109315_, f1, f6, f7, this.offHandItem))
         this.renderArmWithItem(p_109318_, p_109315_, f1, InteractionHand.OFF_HAND, f6, this.offHandItem, f7, p_109316_, p_109317_, p_109319_);
      }

      p_109317_.endBatch();
   }

   @VisibleForTesting
   static ItemInHandRenderer.HandRenderSelection evaluateWhichHandsToRender(LocalPlayer p_172915_) {
      ItemStack itemstack = p_172915_.getMainHandItem();
      ItemStack itemstack1 = p_172915_.getOffhandItem();
      boolean flag = itemstack.is(Items.BOW) || itemstack1.is(Items.BOW);
      boolean flag1 = itemstack.is(Items.CROSSBOW) || itemstack1.is(Items.CROSSBOW);
      if (!flag && !flag1) {
         return ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS;
      } else if (p_172915_.isUsingItem()) {
         return selectionUsingItemWhileHoldingBowLike(p_172915_);
      } else {
         return isChargedCrossbow(itemstack) ? ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY : ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS;
      }
   }

   private static ItemInHandRenderer.HandRenderSelection selectionUsingItemWhileHoldingBowLike(LocalPlayer p_172917_) {
      ItemStack itemstack = p_172917_.getUseItem();
      InteractionHand interactionhand = p_172917_.getUsedItemHand();
      if (!itemstack.is(Items.BOW) && !itemstack.is(Items.CROSSBOW)) {
         return interactionhand == InteractionHand.MAIN_HAND && isChargedCrossbow(p_172917_.getOffhandItem()) ? ItemInHandRenderer.HandRenderSelection.RENDER_MAIN_HAND_ONLY : ItemInHandRenderer.HandRenderSelection.RENDER_BOTH_HANDS;
      } else {
         return ItemInHandRenderer.HandRenderSelection.onlyForHand(interactionhand);
      }
   }

   private static boolean isChargedCrossbow(ItemStack p_172913_) {
      return p_172913_.is(Items.CROSSBOW) && CrossbowItem.isCharged(p_172913_);
   }

   private void renderArmWithItem(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_, InteractionHand p_109375_, float p_109376_, ItemStack p_109377_, float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_) {
      if (!p_109372_.isScoping()) {
         boolean flag = p_109375_ == InteractionHand.MAIN_HAND;
         HumanoidArm humanoidarm = flag ? p_109372_.getMainArm() : p_109372_.getMainArm().getOpposite();
         p_109379_.pushPose();
         if (p_109377_.isEmpty()) {
            if (flag && !p_109372_.isInvisible()) {
               this.renderPlayerArm(p_109379_, p_109380_, p_109381_, p_109378_, p_109376_, humanoidarm);
            }
         } else if (p_109377_.is(Items.FILLED_MAP)) {
            if (flag && this.offHandItem.isEmpty()) {
               this.renderTwoHandedMap(p_109379_, p_109380_, p_109381_, p_109374_, p_109378_, p_109376_);
            } else {
               this.renderOneHandedMap(p_109379_, p_109380_, p_109381_, p_109378_, humanoidarm, p_109376_, p_109377_);
            }
         } else if (p_109377_.getItem() instanceof CrossbowItem) {
            boolean flag1 = CrossbowItem.isCharged(p_109377_);
            boolean flag2 = humanoidarm == HumanoidArm.RIGHT;
            int i = flag2 ? 1 : -1;
            if (p_109372_.isUsingItem() && p_109372_.getUseItemRemainingTicks() > 0 && p_109372_.getUsedItemHand() == p_109375_) {
               this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
               p_109379_.translate((double)((float)i * -0.4785682F), (double)-0.094387F, (double)0.05731531F);
               p_109379_.mulPose(Vector3f.XP.rotationDegrees(-11.935F));
               p_109379_.mulPose(Vector3f.YP.rotationDegrees((float)i * 65.3F));
               p_109379_.mulPose(Vector3f.ZP.rotationDegrees((float)i * -9.785F));
               float f9 = (float)p_109377_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
               float f13 = f9 / (float)CrossbowItem.getChargeDuration(p_109377_);
               if (f13 > 1.0F) {
                  f13 = 1.0F;
               }

               if (f13 > 0.1F) {
                  float f16 = Mth.sin((f9 - 0.1F) * 1.3F);
                  float f3 = f13 - 0.1F;
                  float f4 = f16 * f3;
                  p_109379_.translate((double)(f4 * 0.0F), (double)(f4 * 0.004F), (double)(f4 * 0.0F));
               }

               p_109379_.translate((double)(f13 * 0.0F), (double)(f13 * 0.0F), (double)(f13 * 0.04F));
               p_109379_.scale(1.0F, 1.0F, 1.0F + f13 * 0.2F);
               p_109379_.mulPose(Vector3f.YN.rotationDegrees((float)i * 45.0F));
            } else {
               float f = -0.4F * Mth.sin(Mth.sqrt(p_109376_) * (float)Math.PI);
               float f1 = 0.2F * Mth.sin(Mth.sqrt(p_109376_) * ((float)Math.PI * 2F));
               float f2 = -0.2F * Mth.sin(p_109376_ * (float)Math.PI);
               p_109379_.translate((double)((float)i * f), (double)f1, (double)f2);
               this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
               this.applyItemArmAttackTransform(p_109379_, humanoidarm, p_109376_);
               if (flag1 && p_109376_ < 0.001F && flag) {
                  p_109379_.translate((double)((float)i * -0.641864F), 0.0D, 0.0D);
                  p_109379_.mulPose(Vector3f.YP.rotationDegrees((float)i * 10.0F));
               }
            }

            this.renderItem(p_109372_, p_109377_, flag2 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag2, p_109379_, p_109380_, p_109381_);
         } else {
            boolean flag3 = humanoidarm == HumanoidArm.RIGHT;
            if (p_109372_.isUsingItem() && p_109372_.getUseItemRemainingTicks() > 0 && p_109372_.getUsedItemHand() == p_109375_) {
               int k = flag3 ? 1 : -1;
               switch(p_109377_.getUseAnimation()) {
               case NONE:
                  this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                  break;
               case EAT:
               case DRINK:
                  this.applyEatTransform(p_109379_, p_109373_, humanoidarm, p_109377_);
                  this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                  break;
               case BLOCK:
                  this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                  break;
               case BOW:
                  this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                  p_109379_.translate((double)((float)k * -0.2785682F), (double)0.18344387F, (double)0.15731531F);
                  p_109379_.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
                  p_109379_.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
                  p_109379_.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
                  float f8 = (float)p_109377_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
                  float f12 = f8 / 20.0F;
                  f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
                  if (f12 > 1.0F) {
                     f12 = 1.0F;
                  }

                  if (f12 > 0.1F) {
                     float f15 = Mth.sin((f8 - 0.1F) * 1.3F);
                     float f18 = f12 - 0.1F;
                     float f20 = f15 * f18;
                     p_109379_.translate((double)(f20 * 0.0F), (double)(f20 * 0.004F), (double)(f20 * 0.0F));
                  }

                  p_109379_.translate((double)(f12 * 0.0F), (double)(f12 * 0.0F), (double)(f12 * 0.04F));
                  p_109379_.scale(1.0F, 1.0F, 1.0F + f12 * 0.2F);
                  p_109379_.mulPose(Vector3f.YN.rotationDegrees((float)k * 45.0F));
                  break;
               case SPEAR:
                  this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
                  p_109379_.translate((double)((float)k * -0.5F), (double)0.7F, (double)0.1F);
                  p_109379_.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
                  p_109379_.mulPose(Vector3f.YP.rotationDegrees((float)k * 35.3F));
                  p_109379_.mulPose(Vector3f.ZP.rotationDegrees((float)k * -9.785F));
                  float f7 = (float)p_109377_.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - p_109373_ + 1.0F);
                  float f11 = f7 / 10.0F;
                  if (f11 > 1.0F) {
                     f11 = 1.0F;
                  }

                  if (f11 > 0.1F) {
                     float f14 = Mth.sin((f7 - 0.1F) * 1.3F);
                     float f17 = f11 - 0.1F;
                     float f19 = f14 * f17;
                     p_109379_.translate((double)(f19 * 0.0F), (double)(f19 * 0.004F), (double)(f19 * 0.0F));
                  }

                  p_109379_.translate(0.0D, 0.0D, (double)(f11 * 0.2F));
                  p_109379_.scale(1.0F, 1.0F, 1.0F + f11 * 0.2F);
                  p_109379_.mulPose(Vector3f.YN.rotationDegrees((float)k * 45.0F));
               }
            } else if (p_109372_.isAutoSpinAttack()) {
               this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
               int j = flag3 ? 1 : -1;
               p_109379_.translate((double)((float)j * -0.4F), (double)0.8F, (double)0.3F);
               p_109379_.mulPose(Vector3f.YP.rotationDegrees((float)j * 65.0F));
               p_109379_.mulPose(Vector3f.ZP.rotationDegrees((float)j * -85.0F));
            } else {
               float f5 = -0.4F * Mth.sin(Mth.sqrt(p_109376_) * (float)Math.PI);
               float f6 = 0.2F * Mth.sin(Mth.sqrt(p_109376_) * ((float)Math.PI * 2F));
               float f10 = -0.2F * Mth.sin(p_109376_ * (float)Math.PI);
               int l = flag3 ? 1 : -1;
               p_109379_.translate((double)((float)l * f5), (double)f6, (double)f10);
               this.applyItemArmTransform(p_109379_, humanoidarm, p_109378_);
               this.applyItemArmAttackTransform(p_109379_, humanoidarm, p_109376_);
            }

            this.renderItem(p_109372_, p_109377_, flag3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3, p_109379_, p_109380_, p_109381_);
         }

         p_109379_.popPose();
      }
   }

   public void tick() {
      this.oMainHandHeight = this.mainHandHeight;
      this.oOffHandHeight = this.offHandHeight;
      LocalPlayer localplayer = this.minecraft.player;
      ItemStack itemstack = localplayer.getMainHandItem();
      ItemStack itemstack1 = localplayer.getOffhandItem();
      if (ItemStack.matches(this.mainHandItem, itemstack)) {
         this.mainHandItem = itemstack;
      }

      if (ItemStack.matches(this.offHandItem, itemstack1)) {
         this.offHandItem = itemstack1;
      }

      if (localplayer.isHandsBusy()) {
         this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4F, 0.0F, 1.0F);
         this.offHandHeight = Mth.clamp(this.offHandHeight - 0.4F, 0.0F, 1.0F);
      } else {
         float f = localplayer.getAttackStrengthScale(1.0F);
         boolean requipM = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.mainHandItem, itemstack, localplayer.getInventory().selected);
         boolean requipO = net.minecraftforge.client.ForgeHooksClient.shouldCauseReequipAnimation(this.offHandItem, itemstack1, -1);

         if (!requipM && this.mainHandItem != itemstack)
            this.mainHandItem = itemstack;
         if (!requipO && this.offHandItem != itemstack1)
            this.offHandItem = itemstack1;

         this.mainHandHeight += Mth.clamp((!requipM ? f * f * f : 0.0F) - this.mainHandHeight, -0.4F, 0.4F);
         this.offHandHeight += Mth.clamp((float)(!requipO ? 1 : 0) - this.offHandHeight, -0.4F, 0.4F);
      }

      if (this.mainHandHeight < 0.1F) {
         this.mainHandItem = itemstack;
      }

      if (this.offHandHeight < 0.1F) {
         this.offHandItem = itemstack1;
      }

   }

   public void itemUsed(InteractionHand p_109321_) {
      if (p_109321_ == InteractionHand.MAIN_HAND) {
         this.mainHandHeight = 0.0F;
      } else {
         this.offHandHeight = 0.0F;
      }

   }

   @OnlyIn(Dist.CLIENT)
   @VisibleForTesting
   static enum HandRenderSelection {
      RENDER_BOTH_HANDS(true, true),
      RENDER_MAIN_HAND_ONLY(true, false),
      RENDER_OFF_HAND_ONLY(false, true);

      final boolean renderMainHand;
      final boolean renderOffHand;

      private HandRenderSelection(boolean p_172928_, boolean p_172929_) {
         this.renderMainHand = p_172928_;
         this.renderOffHand = p_172929_;
      }

      public static ItemInHandRenderer.HandRenderSelection onlyForHand(InteractionHand p_172932_) {
         return p_172932_ == InteractionHand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
      }
   }
}
