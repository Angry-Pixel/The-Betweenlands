package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.AnglerModel;
import thebetweenlands.client.renderer.entity.layers.GenericEyesLayer;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Angler;

public class AnglerRenderer extends MobRenderer<Angler, AnglerModel> {
	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/angler.png");

	public AnglerRenderer(EntityRendererProvider.Context context) {
		super(context, new AnglerModel(context.bakeLayer(BLModelLayers.ANGLER)), 0.5F);
		this.addLayer(new GenericEyesLayer<>(this, TheBetweenlands.prefix("textures/entity/angler_glow.png")));
	}

	@Override
	protected void scale(Angler entity, PoseStack stack, float partialTick) {
		stack.translate(0, 0.5F, 0);
		if (entity.isGrounded() && !entity.isLeaping()) {
			stack.mulPose(Axis.ZP.rotationDegrees(90F));
			stack.translate(-0.35F, 0.7F, 0F);
		}

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			double rx = entity.xOld + (entity.getX() - entity.xOld) * partialTick;
			double ry = entity.yOld + (entity.getY() - entity.yOld) * partialTick;
			double rz = entity.zOld + (entity.getZ() - entity.zOld) * partialTick;
            double xOff = Math.sin(Math.toRadians(-entity.getYRot())) * 0.3f;
            double zOff = Math.cos(Math.toRadians(-entity.getYRot())) * 0.3f;
            ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx + xOff, ry + 0.95f, rz + zOff,
                    2.6f,
                    30.0f / 255.0f * 13.0F,
                    90.0f / 255.0f * 13.0F,
                    60.0f / 255.0f * 13.0F));
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Angler entity) {
		return TEXTURE;
	}
}
