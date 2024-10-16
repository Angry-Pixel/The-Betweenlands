package thebetweenlands.client.renderer.entity.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;

public class FireflyGlowLayer<T extends Entity, M extends EntityModel<T>> extends GenericEyesLayer<T, M> {

	private float alpha;

	public FireflyGlowLayer(RenderLayerParent<T, M> parent, ResourceLocation eyeType) {
		super(parent, eyeType);
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource source, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		VertexConsumer vertexconsumer = source.getBuffer(this.renderType());
		this.getParentModel().renderToBuffer(stack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(this.alpha, this.alpha, this.alpha, this.alpha));
	}
}
