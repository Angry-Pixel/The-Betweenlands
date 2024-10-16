package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.JellyfishModel;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Jellyfish;

public class JellyfishRenderer extends MobRenderer<Jellyfish, JellyfishModel> {

	private static final ResourceLocation[] TEXTURE = new ResourceLocation[] {
		TheBetweenlands.prefix("textures/entity/jellyfish_1.png"),
		TheBetweenlands.prefix("textures/entity/jellyfish_2.png"),
		TheBetweenlands.prefix("textures/entity/jellyfish_3.png"),
		TheBetweenlands.prefix("textures/entity/jellyfish_4.png"),
		TheBetweenlands.prefix("textures/entity/jellyfish_5.png")
	};

	private static final float[][] GLOW_COLORS = {
		{238 / 255.0f, 173 / 255.0f, 114 / 255.0f},
		{180 / 255.0f, 42 / 255.0f, 42 / 255.0f},
		{224 / 255.0f, 83 / 255.0f, 108 / 255.0f},
		{90 / 255.0f, 170 / 255.0f, 145 / 255.0f},
		{215 / 255.0f, 176 / 255.0f, 195 / 255.0f},
	};

	public JellyfishRenderer(EntityRendererProvider.Context context) {
		super(context, new JellyfishModel(context.bakeLayer(BLModelLayers.JELLYFISH)), 0.5F);
	}

	@Override
	public void render(Jellyfish entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
		super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
		this.addLighting(entity, partialTicks);
	}

	protected void addLighting(Jellyfish entity, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double interpX = Mth.lerp(partialTicks, entity.xOld, entity.getX());
			double interpY = Mth.lerp(partialTicks, entity.yOld, entity.getY());
			double interpZ = Mth.lerp(partialTicks, entity.zOld, entity.getZ());

			float str = 5.0f * entity.getScale();

			float[] colors = GLOW_COLORS[entity.getJellyfishColor()];

			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(interpX, interpY, interpZ, 3.0f, str * colors[0], str * colors[1], str * colors[2]));
		}
	}

	@Override
	protected void scale(Jellyfish entity, PoseStack stack, float partialTick) {
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
	protected void setupRotations(Jellyfish entity, PoseStack stack, float bob, float yBodyRot, float partialTick, float scale) {
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

	@Override
	public ResourceLocation getTextureLocation(Jellyfish entity) {
		return TEXTURE[entity.getJellyfishColor()];
	}
}
