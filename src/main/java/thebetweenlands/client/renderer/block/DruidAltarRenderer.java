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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.DruidAltarBlock;
import thebetweenlands.common.block.entity.DruidAltarBlockEntity;

public class DruidAltarRenderer implements BlockEntityRenderer<DruidAltarBlockEntity> {

	private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

	private static final RenderType ACTIVE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/druid_altar_active.png"));
	private static final RenderType ACTIVE_GLOW = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/druid_altar_active_glow.png"));
	private static final RenderType NORMAL = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/druid_altar.png"));
	private static final RenderType NORMAL_GLOW = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/druid_altar_glow.png"));

	private final ModelPart altar;
	private final ModelPart stones;
	private final ItemRenderer itemRenderer;

	public DruidAltarRenderer(BlockEntityRendererProvider.Context context) {
		this.altar = context.bakeLayer(BLModelLayers.DRUID_ALTAR);
		this.stones = context.bakeLayer(BLModelLayers.DRUID_STONES);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(DruidAltarBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		float renderRotation = entity.rotation + (entity.rotation - entity.prevRotation) * partialTicks;
		//altar
		int lighting = Math.min(150 + light, LightTexture.FULL_BLOCK);
		if (entity.getLevel() == null) {
			//render glowing in item form
			lighting = LightTexture.FULL_BRIGHT;
		} else if (entity.getBlockState().getValue(DruidAltarBlock.ACTIVE)) {
			lighting = (int) Math.min(light + Math.sin(Math.toRadians(renderRotation) * 4.0f) * 105.0F + 150.0F, LightTexture.FULL_BLOCK);
		}
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.altar.render(stack, source.getBuffer(entity.getBlockState().getValue(DruidAltarBlock.ACTIVE) ? ACTIVE : NORMAL), light, overlay);
		this.altar.render(stack, source.getBuffer(entity.getBlockState().getValue(DruidAltarBlock.ACTIVE) ? ACTIVE_GLOW : NORMAL_GLOW), lighting, overlay);

		//stones
		stack.mulPose(Axis.YP.rotationDegrees(renderRotation));
		stack.translate(0.0F, -1.5F, 0.0F);
		this.stones.render(stack, source.getBuffer(entity.getBlockState().getValue(DruidAltarBlock.ACTIVE) ? ACTIVE : NORMAL), light, overlay);
		this.stones.render(stack, source.getBuffer(entity.getBlockState().getValue(DruidAltarBlock.ACTIVE) ? ACTIVE_GLOW : NORMAL_GLOW), lighting, overlay);
		stack.popPose();

		//Animate the 4 talisman pieces
		double yOff = 1.2D;
		if (entity.getBlockState().getValue(DruidAltarBlock.ACTIVE) && entity.craftingProgress != 0) {
			yOff = Math.min(entity.renderYOffset + (entity.renderYOffset - entity.prevRenderYOffset) * partialTicks, DruidAltarBlockEntity.FINAL_HEIGHT + 1.0D);

			stack.pushPose();
			stack.translate(0.5D, 3.1D, 0.5D);
			stack.mulPose(Axis.YP.rotationDegrees(renderRotation * 2.0F));
			float shineScale = (float) (0.04F * Math.pow(1.0D - (DruidAltarBlockEntity.FINAL_HEIGHT + 1.0D - yOff) / DruidAltarBlockEntity.FINAL_HEIGHT, 12));
			stack.scale(shineScale, shineScale, shineScale);
			this.renderShine(stack, source.getBuffer(RenderType.dragonRays()), (float) Math.sin(Math.toRadians(renderRotation)) / 2.0F - 0.2F, (int) (80 * Math.pow(1.0D - (DruidAltarBlockEntity.FINAL_HEIGHT + 1.0D - yOff) / DruidAltarBlockEntity.FINAL_HEIGHT, 12)));
			stack.popPose();
		}
		for (int xi = 0; xi < 2; xi++) {
			for (int zi = 0; zi < 2; zi++) {
				ItemStack item = entity.getItem(zi * 2 + xi + 1);
				if (item.isEmpty()) {
					continue;
				}
				float xOff = xi == 0 ? -0.18F : 1.18F;
				float zOff = zi == 0 ? -0.18F : 1.18F;
				stack.pushPose();
				stack.translate(xOff, 1, zOff);
				this.renderCone(stack, source.getBuffer(BLRenderTypes.druidCone()), 5);
				stack.popPose();
				Vector3d midVec = new Vector3d();
				midVec.x = 0.5F;
				midVec.z = 0.5F;
				Vector3d diffVec = new Vector3d();
				diffVec.x = xOff - midVec.x;
				diffVec.z = zOff - midVec.z;
				double rProgress = 1.0D - Math.pow(1.0D - (DruidAltarBlockEntity.FINAL_HEIGHT + 1.0D - yOff) / DruidAltarBlockEntity.FINAL_HEIGHT, 6);
				diffVec.x *= rProgress;
				diffVec.z *= rProgress;
				midVec.x += diffVec.x;
				midVec.z += diffVec.z;
				stack.pushPose();
				stack.translate(midVec.x, yOff, midVec.z);
				stack.scale(0.3F, 0.3F, 0.3F);
				stack.mulPose(Axis.YP.rotationDegrees(-renderRotation * 2.0F));
				this.itemRenderer.renderStatic(item, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, overlay, stack, source, null, 0);
				stack.popPose();
			}
		}

		//Render swamp talisman
		ItemStack itemTalisman = entity.getItem(0);
		if (!itemTalisman.isEmpty()) {
			stack.pushPose();
			stack.translate(0.5D, 3.1D, 0.5D);
			stack.mulPose(Axis.YP.rotationDegrees(renderRotation * 2.0F));
			stack.scale(0.04F, 0.04F, 0.04F);
			this.renderShine(stack, source.getBuffer(RenderType.dragonRays()), (float) Math.sin(Math.toRadians(renderRotation)) / 2.0F - 0.2F, 80);
			stack.popPose();
			stack.pushPose();
			stack.translate(0.5D, 3.1D, 0.5D);
			stack.mulPose(Axis.YP.rotationDegrees(-renderRotation * 2.0F));
			stack.scale(0.3F, 0.3F, 0.3F);

			this.itemRenderer.renderStatic(itemTalisman, ItemDisplayContext.FIXED, LightTexture.FULL_BRIGHT, overlay, stack, source, null, 0);
			stack.popPose();
		}
	}

	private void renderShine(PoseStack poseStack, VertexConsumer buffer, float rotation, int iterations) {
		poseStack.pushPose();
		float f2 = 0.0F;
		if (rotation > 0.8F) {
			f2 = (rotation - 0.8F) / 0.2F;
		}
		int i = FastColor.ARGB32.colorFromFloat(1.0F - f2, 1.0F, 1.0F, 1.0F);
		RandomSource randomsource = RandomSource.create(432L);
		Vector3f vector3f = new Vector3f();
		Vector3f vector3f1 = new Vector3f();
		Vector3f vector3f2 = new Vector3f();
		Vector3f vector3f3 = new Vector3f();
		Quaternionf quaternionf = new Quaternionf();

		for (int l = 0; l < iterations; l++) {
			quaternionf
				.rotationXYZ(randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI)
				.rotateXYZ(randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI, randomsource.nextFloat() * Mth.TWO_PI + rotation * Mth.HALF_PI);
			poseStack.mulPose(quaternionf);
			float pos1 = randomsource.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = randomsource.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			vector3f1.set(-HALF_SQRT_3 * pos2, pos1, -0.5F * pos2);
			vector3f2.set(HALF_SQRT_3 * pos2, pos1, -0.5F * pos2);
			vector3f3.set(0.0F, pos1, pos2);
			PoseStack.Pose pose = poseStack.last();
			buffer.addVertex(pose, vector3f).setColor(i);
			buffer.addVertex(pose, vector3f1).setColor(0x0000FF);
			buffer.addVertex(pose, vector3f2).setColor(0x0000FF);
			buffer.addVertex(pose, vector3f).setColor(i);
			buffer.addVertex(pose, vector3f2).setColor(0x0000FF);
			buffer.addVertex(pose, vector3f3).setColor(0x0000FF);
			buffer.addVertex(pose, vector3f).setColor(i);
			buffer.addVertex(pose, vector3f3).setColor(0x0000FF);
			buffer.addVertex(pose, vector3f1).setColor(0x0000FF);
		}

		poseStack.popPose();
	}

	private void renderCone(PoseStack stack, VertexConsumer consumer, int faces) {
		Matrix4f matrix = stack.last().pose();
		stack.pushPose();
		float step = 360.0F / faces;

		for (float i = 0; i < 360.0F; i += step) {
			float lr = 0.1F;
			float ur = 0.3F;
			float height = 0.2F;
			float sin = Mth.sin((float) Math.toRadians(i));
			float cos = Mth.cos((float) Math.toRadians(i));
			float sin2 = Mth.sin((float) Math.toRadians(i + step));
			float cos2 = Mth.cos((float) Math.toRadians(i + step));

			consumer.addVertex(matrix, sin * lr, 0, cos * lr).setColor(255, 255, 255, 0);
			consumer.addVertex(matrix, sin2 * lr, 0, cos2 * lr).setColor(255, 255, 255, 0);

			consumer.addVertex(matrix, sin2 * ur, height, cos2 * ur).setColor(0, 0, 255, 60);
			consumer.addVertex(matrix, sin * ur, height, cos * ur).setColor(0, 0, 255, 60);

			consumer.addVertex(matrix, sin * ur, height, cos * ur).setColor(0, 0, 255, 60);
			consumer.addVertex(matrix, sin2 * ur, height, cos2 * ur).setColor(0, 0, 255, 60);

			consumer.addVertex(matrix, sin2 * lr, 0, cos2 * lr).setColor(255, 255, 255, 0);
			consumer.addVertex(matrix, sin * lr, 0, cos * lr).setColor(255, 255, 255, 0);
		}

		stack.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(DruidAltarBlockEntity entity) {
		return BlockEntityRenderer.super.getRenderBoundingBox(entity).inflate(0.75D).expandTowards(0.0D, 1.0D, 0.0D);
	}
}
