package thebetweenlands.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.ItemAmateMap;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.savedata.AmateMapItemSavedData;

// Inject map code
// TODO: separate and bundle into "Martian essentials library"
@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRenderer {

    @Shadow protected abstract float calculateMapTilt(float float1);
    @Shadow protected abstract void renderMapHand(PoseStack poseStack, MultiBufferSource multiBufferSource, int int1, HumanoidArm arm);
    @Shadow protected abstract void renderPlayerArm(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_);
    @Shadow private ItemStack offHandItem;
    @Shadow private ItemStack mainHandItem;
    @Shadow private Minecraft minecraft;

    private static final RenderType AMATE_MAP_BACKGROUND = RenderType.text(new ResourceLocation(TheBetweenlands.ID,"textures/items/amate_map_background.png"));   // empty
    private static final RenderType AMATE_MAP_BACKGROUND_CHECKERBOARD = RenderType.text(new ResourceLocation(TheBetweenlands.ID,"textures/items/amate_map_background.png"));  // used

    // Check for our map then render
    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    protected void renderArmWithItem(AbstractClientPlayer p_109372_, float p_109373_, float p_109374_, InteractionHand p_109375_, float p_109376_, ItemStack p_109377_, float p_109378_, PoseStack p_109379_, MultiBufferSource p_109380_, int p_109381_, CallbackInfo ci) {
        if (!p_109377_.is(ItemRegistry.USED_AMATE_MAP.get())) {
            return;
        }

        boolean offhand = p_109375_ == InteractionHand.MAIN_HAND;
        HumanoidArm humanoidarm = offhand ? p_109372_.getMainArm() : p_109372_.getMainArm().getOpposite();
        if (offhand && this.offHandItem.isEmpty()) {
            this.renderTwoHandedAmateMap(p_109379_, p_109380_, p_109381_, p_109374_, p_109378_, p_109376_);
        }
        else {
            this.renderOneHandedAmateMap(p_109379_, p_109380_, p_109381_, p_109378_, humanoidarm, p_109376_, p_109377_);
        }
        ci.cancel();
    }

    private void renderOneHandedAmateMap(PoseStack p_109354_, MultiBufferSource p_109355_, int p_109356_, float p_109357_, HumanoidArm p_109358_, float p_109359_, ItemStack p_109360_) {
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
        this.renderAmateMap(p_109354_, p_109355_, p_109356_, p_109360_);
        p_109354_.popPose();
    }

    private void renderTwoHandedAmateMap(PoseStack p_109340_, MultiBufferSource p_109341_, int p_109342_, float p_109343_, float p_109344_, float p_109345_) {
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
        this.renderAmateMap(p_109340_, p_109341_, p_109342_, this.mainHandItem);
    }

    private void renderAmateMap(PoseStack p_109367_, MultiBufferSource p_109368_, int p_109369_, ItemStack p_109370_) {
        // render map texture
        p_109367_.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        p_109367_.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        p_109367_.scale(0.38F, 0.38F, 0.38F);
        p_109367_.translate(-0.5D, -0.5D, 0.0D);
        p_109367_.scale(0.0078125F, 0.0078125F, 0.0078125F);
        Integer integer = ItemAmateMap.getAmateMapId(p_109370_);
        AmateMapItemSavedData mapitemsaveddata = ItemAmateMap.getSavedData(integer, Minecraft.getInstance().level);
        VertexConsumer vertexconsumer = p_109368_.getBuffer(mapitemsaveddata == null ? AMATE_MAP_BACKGROUND : AMATE_MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = p_109367_.last().pose();
        vertexconsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(p_109369_).endVertex();
        vertexconsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(p_109369_).endVertex();

        if (mapitemsaveddata != null) {
            // Render map
            TheBetweenlands.amateMapRenderer.render(p_109367_, p_109368_, integer, mapitemsaveddata, false, p_109369_);
        }
    }
}
