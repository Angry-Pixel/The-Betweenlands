package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.CaveJellyfishModel;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.CaveJellyfish;

public class CaveJellyfishRenderer extends MobRenderer<CaveJellyfish, CaveJellyfishModel> {
	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/jellyfish_cave.png");

	public CaveJellyfishRenderer(EntityRendererProvider.Context context) {
		super(context, new CaveJellyfishModel(context.bakeLayer(BLModelLayers.JELLYFISH_CAVE)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(CaveJellyfish entity) {
		return TEXTURE;
	}

	protected void addLighting(CaveJellyfish entity, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double interpX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
			double interpY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
			double interpZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

			float str = 2.0f * entity.getScale();

			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(interpX, interpY, interpZ, 3.0f, str * 0.49f, str, str));
		}
	}

	@Override
	protected void scale(CaveJellyfish entity, PoseStack stack, float partialTick) {
		stack.scale(entity.getJellyfishSize(), entity.getJellyfishSize() * entity.getJellyfishLength(), entity.getJellyfishSize());

		float limbSwingAmount = entity.walkAnimation.speed(partialTick);
		float limbSwing = entity.walkAnimation.position(1.0F - partialTick);

		stack.scale(
			1.0F + Mth.sin(limbSwing * 0.75F) * Math.min(limbSwingAmount, 0.2F) * 2.0F,
			1.0F - Mth.sin(limbSwing * 0.75F) * Math.min(limbSwingAmount, 0.2F),
			1.0F + Mth.sin(limbSwing * 0.75F) * Math.min(limbSwingAmount, 0.2F) * 2.0F
		);
	}

	@Override
	protected void setupRotations(CaveJellyfish entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale) {
		Vec3 weightPos = entity.getOrientationPos(partialTick);

		double dx = Mth.lerp(partialTick, entity.xOld, entity.getX()) - weightPos.x;
		double dy = Mth.lerp(partialTick, entity.yOld, entity.getY()) - weightPos.y;
		double dz = Mth.lerp(partialTick, entity.zOld, entity.getZ()) - weightPos.z;

		float yaw = -(float)Math.toDegrees(Mth.atan2(dz, dx));
		float pitch = (float)Math.toDegrees(Mth.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) - 180;

		stack.translate(0.0D, 0.5D, 0.0D);
		stack.mulPose(Axis.YP.rotationDegrees(yaw));
		stack.mulPose(Axis.ZP.rotationDegrees(pitch));
		stack.mulPose(Axis.YP.rotationDegrees(-yaw));
		stack.translate(0.0D, -0.5D, 0.0D);

	}
}
