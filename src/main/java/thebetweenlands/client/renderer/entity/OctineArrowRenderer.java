package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.projectile.arrow.OctineArrow;

public class OctineArrowRenderer extends ArrowRenderer<OctineArrow> {
	public OctineArrowRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public void render(OctineArrow entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			double rx = Mth.lerp(partialTicks, entity.xo, entity.getX());
			double ry = Mth.lerp(partialTicks, entity.yo, entity.getY());
			double rz = Mth.lerp(partialTicks, entity.zo, entity.getZ());
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz, 3.0F, 2.3F, 0.5F, 0));
		}
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
	}

	@Override
	public ResourceLocation getTextureLocation(OctineArrow entity) {
		return TheBetweenlands.prefix("textures/entity/arrow/octine_arrow.png");
	}
}
