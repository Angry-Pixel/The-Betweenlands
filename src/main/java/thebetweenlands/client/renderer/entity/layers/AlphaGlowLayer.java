package thebetweenlands.client.renderer.entity.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import thebetweenlands.common.TheBetweenlands;

public class AlphaGlowLayer<T extends Entity, M extends EntityModel<T>> extends GenericEyesLayer<T, M> {

	private float red = 1.0F;
	private float green = 1.0F;
	private float blue = 1.0F;
	private float alpha = 1.0F;

	public AlphaGlowLayer(RenderLayerParent<T, M> parent, ResourceLocation eyeType) {
		super(parent, eyeType);
	}

	public void setColor(float red, float green, float blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setAlpha(float alpha) {
		this.alpha = Mth.clamp(alpha, 0.0F, 1.0F);
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource source, int light, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		VertexConsumer vertexconsumer = source.getBuffer(this.renderType());
		this.getParentModel().renderToBuffer(stack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(this.alpha, this.red * this.alpha, this.green * this.alpha, this.blue * this.alpha));
	}
}
