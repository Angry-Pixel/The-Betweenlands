package thebetweenlands.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.renderer.BLRenderTypes;

public class AnimatedLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {

	private final ResourceLocation texture;

	public AnimatedLayer(RenderLayerParent<T, M> renderer, ResourceLocation texture) {
		super(renderer);
		this.texture = texture;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource source, int packedLight, T livingEntity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
		float f = (float)livingEntity.tickCount + partialTick;
		EntityModel<T> entitymodel = this.getParentModel();
		entitymodel.prepareMobModel(livingEntity, limbSwing, limbSwingAmount, partialTick);
		this.getParentModel().copyPropertiesTo(entitymodel);
		VertexConsumer vertexconsumer = source.getBuffer(BLRenderTypes.animatedLayer(this.texture, 0, -f * 0.002F % 1.0F));
		entitymodel.setupAnim(livingEntity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
		entitymodel.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
	}
}
