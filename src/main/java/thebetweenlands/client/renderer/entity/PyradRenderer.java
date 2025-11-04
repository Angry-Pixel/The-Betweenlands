package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.PyradModel;
import thebetweenlands.client.renderer.entity.layers.AlphaGlowLayer;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.monster.Pyrad;

public class PyradRenderer extends MobRenderer<Pyrad, PyradModel> {

	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/pyrad.png");
	private final AlphaGlowLayer<Pyrad, PyradModel> layer;

	public PyradRenderer(EntityRendererProvider.Context context) {
		super(context, new PyradModel(context.bakeLayer(BLModelLayers.PYRAD)), 0.5F);
		this.addLayer(this.layer = new AlphaGlowLayer<>(this, TheBetweenlands.prefix("textures/entity/pyrad_glow.png")));
	}

	@Override
	public void render(Pyrad entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
		this.layer.setAlpha(entity.getActiveTicks(partialTick) / 60.0F + (!entity.isActive() ? entity.getHitTicks(partialTick) / 20.0F * 0.45F : 0.0F));

		stack.pushPose();
		stack.translate(0, (Math.sin((entity.tickCount + partialTick) / 10.0F) / 4.0F - 0.25D) * entity.getActiveTicks(partialTick) / 60.0D - 0.9D * (1.0D - entity.getActiveTicks(partialTick) / 60.0D), 0);
		super.render(entity, entityYaw, partialTick, stack, buffer, packedLight);
		stack.popPose();

		double rx = Mth.lerp(partialTick, entity.xOld, entity.getX());
		double ry = Mth.lerp(partialTick, entity.yOld, entity.getY());
		double rz = Mth.lerp(partialTick, entity.zOld, entity.getZ());

		if (!entity.isActive() && entity.getHitTicks(partialTick) > 8 && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			float hitTicks = entity.getHitTicks(partialTick);
			float brightness = 1.0F - (float) (Math.cos((hitTicks - 8) / 12.0F * 2.0F * Math.PI) + 1.0F) / 2.0F;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry + 0.5D, rz,
				1.8f,
				134.0f / 255.0f * 3.0F * brightness,
				214.0f / 255.0f * 3.0F * brightness,
				55.0f / 255.0f * 3.0F * brightness));
		}

		if (entity.getGlowTicks(partialTick) > 0 && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			float glow = entity.getGlowTicks(partialTick) / 10.0F;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry + entity.getEyeHeight(), rz,
				4.75F * glow + 1.0F,
				134.0f / 255.0f * 10.0F * glow,
				214.0f / 255.0f * 10.0F * glow,
				55.0f / 255.0f * 10.0F * glow));
		}
	}

	@Override
	public ResourceLocation getTextureLocation(Pyrad entity) {
		return TEXTURE;
	}
}
