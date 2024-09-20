package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.RepellerBlock;
import thebetweenlands.common.block.entity.RepellerBlockEntity;

public class RepellerRenderer implements BlockEntityRenderer<RepellerBlockEntity> {

	private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);
	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/repeller.png"));
	private static final RenderType LIQUID = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/fluid.png"));
	private final ModelPart repeller;
	private final ModelPart fluid;

	public RepellerRenderer(BlockEntityRendererProvider.Context context) {
		var root = context.bakeLayer(BLModelLayers.REPELLER);
		this.repeller = root.getChild("repeller");
		this.fluid = root.getChild("jar_liquid");
	}

	@Override
	public void render(RepellerBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		Direction dir = entity.getBlockState().getValue(RepellerBlock.FACING);
		double xOff = dir.getStepX() * 0.12F;
		double zOff = dir.getStepZ() * 0.12F;
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-dir.toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		if (entity.getFuel() > 0) {
			stack.pushPose();
			stack.scale(1.0F, (float) entity.getFuel() / entity.getMaxFuel(), 1.0F);
			this.fluid.render(stack, source.getBuffer(LIQUID), LightTexture.FULL_BRIGHT, overlay, 0xFF293828);
			stack.popPose();
		}
		this.repeller.render(stack, source.getBuffer(TEXTURE), light, overlay);
		stack.popPose();

		if (entity.getLevel() != null && entity.hasShimmerstone()) {
			stack.pushPose();
			stack.translate(0.5F, 0.0F, 0.5F);
			stack.translate(1.5F * xOff, 1.15F, 1.5F * zOff);
			stack.scale(0.008F, 0.008F, 0.008F);
			this.renderShine(stack, source.getBuffer(RenderType.dragonRays()), entity.renderTicks + partialTick, 20);
			stack.popPose();
		}

		if (entity.getLevel() != null && entity.isRunning()) {
			if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
			}
//			WorldRenderHandler.REPELLER_SHIELDS.add(Pair.of(new Vec3(0.5F + xOff, y + 1.15F, z + 0.5F - zOff), entity.getRadius(partialTick)));
		}
	}

	private void renderShine(PoseStack poseStack, VertexConsumer buffer, float ticks, int iterations) {
		poseStack.pushPose();
		float rotation = ticks / 360.0F;
		float size = ((float)Math.sin(ticks / 40.0F) + 1.0F) * 1.8F + 1.1F;
		float brightness = Mth.clamp(size / 2.0F + 0.45F, 0.0f, 1.0f);
		int startColor = FastColor.ARGB32.colorFromFloat(0.0F * brightness, 1.0F, 0.8F, 0.0F);
		int endColor = FastColor.ARGB32.colorFromFloat(brightness, 1.0F, 0.8F, 0.0F);
		RandomSource randomsource = RandomSource.create(432L);
		Vector3f vector3f = new Vector3f();
		Vector3f vector3f1 = new Vector3f();
		Vector3f vector3f2 = new Vector3f();
		Vector3f vector3f3 = new Vector3f();
		Quaternionf quaternionf = new Quaternionf();

		for (int l = 0; l < iterations; l++) {
			quaternionf
				.rotationXYZ(randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI)
				.rotateXYZ(randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI + rotation * Mth.HALF_PI)
				.rotateX(randomsource.nextFloat() * Mth.TWO_PI + rotation * Mth.PI);
			poseStack.mulPose(quaternionf);
			float pos1 = randomsource.nextFloat() * 20.0F + 5.0F + size * 10.0F;
			float pos2 = randomsource.nextFloat() * 2.0F + 1.0F + size * 2.0F;
			vector3f1.set(-HALF_SQRT_3 * pos2, pos1, -0.5F * pos2);
			vector3f2.set(HALF_SQRT_3 * pos2, pos1, -0.5F * pos2);
			vector3f3.set(0.0F, pos1, pos2);
			PoseStack.Pose pose = poseStack.last();
			buffer.addVertex(pose, vector3f).setColor(endColor);
			buffer.addVertex(pose, vector3f1).setColor(startColor);
			buffer.addVertex(pose, vector3f2).setColor(startColor);
			buffer.addVertex(pose, vector3f).setColor(endColor);
			buffer.addVertex(pose, vector3f2).setColor(startColor);
			buffer.addVertex(pose, vector3f3).setColor(startColor);
			buffer.addVertex(pose, vector3f).setColor(endColor);
			buffer.addVertex(pose, vector3f3).setColor(startColor);
			buffer.addVertex(pose, vector3f1).setColor(startColor);
		}

		poseStack.popPose();
	}

	@Override
	public int getViewDistance() {
		return 256;
	}

	@Override
	public AABB getRenderBoundingBox(RepellerBlockEntity entity) {
		return AABB.INFINITE;
	}
}
