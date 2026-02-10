package thebetweenlands.client.renderer.entity.layers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.WatcherEyesModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.WatcherEyes;

public class WatcherEyesLayer extends RenderLayer<WatcherEyes, WatcherEyesModel> {

    private final WatcherEyesModel eyesModel;
    public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/watcher_eyes.png");

    public WatcherEyesLayer(RenderLayerParent<WatcherEyes, WatcherEyesModel> entity, EntityModelSet modelSet) {
    	super(entity);
    	this.eyesModel = new WatcherEyesModel(modelSet.bakeLayer(BLModelLayers.WATCHER_EYES));
    }

    @Override
   	public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, WatcherEyes entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
	    BlockPos offsetPos = entity.blockPosition().relative(entity.getDirection(), 1);
	    int customLight = LevelRenderer.getLightColor(entity.level(), offsetPos);
    	eyesModel.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
		eyesModel.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		stack.pushPose();
		eyesModel.renderEyesToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), customLight, OverlayTexture.NO_OVERLAY, -1);
	    stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(WatcherEyes entity) {
		return TEXTURE;
	}
}