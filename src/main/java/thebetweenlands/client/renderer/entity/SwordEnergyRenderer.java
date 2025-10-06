package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.SwordEnergyModel;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.entity.SwordEnergy;
import thebetweenlands.common.registries.ItemRegistry;

public class SwordEnergyRenderer extends EntityRenderer<SwordEnergy> {

	private static final ResourceLocation FORCE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper_armor.png");
	private final SwordEnergyModel model;
	private final ItemRenderer renderer;

	private final ItemStack swordPiece1 = ItemRegistry.SHOCKWAVE_SWORD_PIECE_1.toStack();
	private final ItemStack swordPiece2 = ItemRegistry.SHOCKWAVE_SWORD_PIECE_2.toStack();
	private final ItemStack swordPiece3 = ItemRegistry.SHOCKWAVE_SWORD_PIECE_3.toStack();
	private final ItemStack swordPiece4 = ItemRegistry.SHOCKWAVE_SWORD_PIECE_4.toStack();

	public SwordEnergyRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new SwordEnergyModel(context.bakeLayer(BLModelLayers.SWORD_ENERGY));
		this.renderer = context.getItemRenderer();
	}

	@Override
	public void render(SwordEnergy entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.getX(), entity.getY() + 0.5D, entity.getZ(),
				2f,
				5.0f / 255.0f * 13.0F,
				40.0f / 255.0f * 13.0F,
				60.0f / 255.0f * 13.0F));
		}

		float ticks = entity.tickCount + partialTick;
		stack.pushPose();
		stack.translate(0.0D, -0.0625D - entity.pulseFloat, 0.0D);
		stack.scale(1F + entity.pulseFloat, 1F + entity.pulseFloat, 1F + entity.pulseFloat);
		this.renderEnergyModel(stack, buffer, ticks);
		stack.popPose();

		float interpPos1 = Mth.lerp(partialTick, entity.lastPos1, entity.pos1);
		this.renderItemInBlock(stack, buffer, -interpPos1, 0.725F, -interpPos1, this.swordPiece1, ticks);
		float interpPos2 = Mth.lerp(partialTick, entity.lastPos2, entity.pos2);
		this.renderItemInBlock(stack, buffer, interpPos2, 0.725F, -interpPos2, this.swordPiece2, ticks);
		float interpPos3 = Mth.lerp(partialTick, entity.lastPos3, entity.pos3);
		this.renderItemInBlock(stack, buffer, interpPos3, 0.725F, interpPos3, this.swordPiece3, ticks);
		float interpPos4 = Mth.lerp(partialTick, entity.lastPos4, entity.pos4);
		this.renderItemInBlock(stack, buffer, -interpPos4, 0.725F, interpPos4, this.swordPiece4, ticks);

		Vec3 energyPos = new Vec3(0.0F, 0.85F, 0.0F);

		renderBeam(stack.last(), buffer, ticks, energyPos, new Vec3(-interpPos1 - 0.1F, 0.9F, -interpPos1 - 0.1F), 0.05F, 0.25F, true, true);
		stack.pushPose();
		stack.translate(-interpPos1, -0.14F, -interpPos1);
		if (entity.pos1 < SwordEnergy.DISTANCE)
			this.renderEnergyModel(stack, buffer, ticks);
		stack.popPose();
		renderBeam(stack.last(), buffer, ticks, energyPos, new Vec3(interpPos2 + 0.1F, 0.9F, -interpPos2 - 0.1F), 0.05F, 0.25F, true, true);
		stack.pushPose();
		stack.translate(interpPos2, -0.14F, 0.0D - interpPos2);
		if (entity.pos2 < SwordEnergy.DISTANCE)
			this.renderEnergyModel(stack, buffer, ticks);
		stack.popPose();
		renderBeam(stack.last(), buffer, ticks, energyPos, new Vec3(interpPos3 + 0.1F, 0.9F, interpPos3 + 0.1F), 0.05F, 0.25F, true, true);
		stack.pushPose();
		stack.translate(interpPos3, -0.14F, interpPos3);
		if (entity.pos3 < SwordEnergy.DISTANCE)
			this.renderEnergyModel(stack, buffer, ticks);
		stack.popPose();
		renderBeam(stack.last(), buffer, ticks, energyPos, new Vec3(-interpPos4 - 0.1F, 0.9F, interpPos4 + 0.1F), 0.05F, 0.25F, true, true);
		stack.pushPose();
		stack.translate(-interpPos4, -0.14F, interpPos4);
		if (entity.pos4 < SwordEnergy.DISTANCE)
			this.renderEnergyModel(stack, buffer, ticks);
		stack.popPose();
	}

	private void renderEnergyModel(PoseStack stack, MultiBufferSource buffer, float ticks) {
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.energySwirl(FORCE_TEXTURE, ticks * 0.01F, ticks * 0.01F)), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, FastColor.ARGB32.colorFromFloat(1.0F, 0.5F, 0.5F, 0.5F));
	}

	public void renderItemInBlock(PoseStack stack, MultiBufferSource buffer, double x, double y, double z, ItemStack item, float ticks) {
		stack.pushPose();
		stack.translate(x, y, z);
		stack.scale(1.25F, 1.25F, 1.25F);
		stack.mulPose(Axis.YP.rotationDegrees(ticks * 4F));
		this.renderer.renderStatic(item, ItemDisplayContext.GROUND, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, stack, buffer, null, 0);
		stack.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(SwordEnergy entity) {
		return null;
	}

	public static void renderBeam(PoseStack.Pose pose, MultiBufferSource buffer, float ticks, Vec3 start, Vec3 end, float startWidth, float endWidth, boolean renderStartCap, boolean renderEndCap) {
		VertexConsumer consumer = buffer.getBuffer(BLRenderTypes.energySwirl(FORCE_TEXTURE, ticks * 0.01F, 0.0F));

		Vec3 diff = start.subtract(end);
		Vec3 dir = diff.normalize();
		Vec3 upVec = new Vec3(0, 1, 0);
		Vec3 localSide = dir.cross(upVec).normalize();
		Vec3 localUp = localSide.cross(dir).normalize();

		float maxVStart = (float) (diff.length() / 8.0F);
		float maxVEnd = (float) (diff.length() / 8.0F);
		float minVStart = 0.0F;
		float minVEnd = 0.0F;
		float maxU = (float) (diff.length() / 2.0F);

		consumer.addVertex(pose, (float) (start.x + (localSide.x + localUp.x) * startWidth), (float) (start.y + (localSide.y + localUp.y) * startWidth), (float) (start.z + (localSide.z + localUp.z) * startWidth)).setUv(0, minVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (localSide.x - localUp.x) * startWidth), (float) (start.y + (localSide.y - localUp.y) * startWidth), (float) (start.z + (localSide.z - localUp.z) * startWidth)).setUv(0, maxVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (localSide.x - localUp.x) * endWidth), (float) (end.y + (localSide.y - localUp.y) * endWidth), (float) (end.z + (localSide.z - localUp.z) * endWidth)).setUv(maxU, maxVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (localSide.x + localUp.x) * endWidth), (float) (end.y + (localSide.y + localUp.y) * endWidth), (float) (end.z + (localSide.z + localUp.z) * endWidth)).setUv(maxU, minVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		consumer.addVertex(pose, (float) (end.x + (-localSide.x + localUp.x) * endWidth), (float) (end.y + (-localSide.y + localUp.y) * endWidth), (float) (end.z + (-localSide.z + localUp.z) * endWidth)).setUv(maxU, minVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (-localSide.x - localUp.x) * endWidth), (float) (end.y + (-localSide.y - localUp.y) * endWidth), (float) (end.z + (-localSide.z - localUp.z) * endWidth)).setUv(maxU, maxVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (-localSide.x - localUp.x) * startWidth), (float) (start.y + (-localSide.y - localUp.y) * startWidth), (float) (start.z + (-localSide.z - localUp.z) * startWidth)).setUv(0, maxVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (-localSide.x + localUp.x) * startWidth), (float) (start.y + (-localSide.y + localUp.y) * startWidth), (float) (start.z + (-localSide.z + localUp.z) * startWidth)).setUv(0, minVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		consumer.addVertex(pose, (float) (end.x + (localUp.x + localSide.x) * endWidth), (float) (end.y + (localUp.y + localSide.y) * endWidth), (float) (end.z + (localUp.z + localSide.z) * endWidth)).setUv(maxU, minVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (localUp.x - localSide.x) * endWidth), (float) (end.y + (localUp.y - localSide.y) * endWidth), (float) (end.z + (localUp.z - localSide.z) * endWidth)).setUv(maxU, maxVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (localUp.x - localSide.x) * startWidth), (float) (start.y + (localUp.y - localSide.y) * startWidth), (float) (start.z + (localUp.z - localSide.z) * startWidth)).setUv(0, maxVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (localUp.x + localSide.x) * startWidth), (float) (start.y + (localUp.y + localSide.y) * startWidth), (float) (start.z + (localUp.z + localSide.z) * startWidth)).setUv(0, minVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		consumer.addVertex(pose, (float) (start.x + (-localUp.x + localSide.x) * startWidth), (float) (start.y + (-localUp.y + localSide.y) * startWidth), (float) (start.z + (-localUp.z + localSide.z) * startWidth)).setUv(0, minVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (start.x + (-localUp.x - localSide.x) * startWidth), (float) (start.y + (-localUp.y - localSide.y) * startWidth), (float) (start.z + (-localUp.z - localSide.z) * startWidth)).setUv(0, maxVStart).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (-localUp.x - localSide.x) * endWidth), (float) (end.y + (-localUp.y - localSide.y) * endWidth), (float) (end.z + (-localUp.z - localSide.z) * endWidth)).setUv(maxU, maxVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		consumer.addVertex(pose, (float) (end.x + (-localUp.x + localSide.x) * endWidth), (float) (end.y + (-localUp.y + localSide.y) * endWidth), (float) (end.z + (-localUp.z + localSide.z) * endWidth)).setUv(maxU, minVEnd).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);

		if (renderStartCap) {
			consumer.addVertex(pose, (float) (start.x + (localUp.x - localSide.x) * startWidth), (float) (start.y + (localUp.y - localSide.y) * startWidth), (float) (start.z + (localUp.z - localSide.z) * startWidth)).setUv(0, 1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (start.x + (-localUp.x - localSide.x) * startWidth), (float) (start.y + (-localUp.y - localSide.y) * startWidth), (float) (start.z + (-localUp.z - localSide.z) * startWidth)).setUv(1, 1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (start.x + (-localUp.x + localSide.x) * startWidth), (float) (start.y + (-localUp.y + localSide.y) * startWidth), (float) (start.z + (-localUp.z + localSide.z) * startWidth)).setUv(1, 0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (start.x + (localUp.x + localSide.x) * startWidth), (float) (start.y + (localUp.y + localSide.y) * startWidth), (float) (start.z + (localUp.z + localSide.z) * startWidth)).setUv(0, 0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		}

		if (renderEndCap) {
			consumer.addVertex(pose, (float) (end.x + (localUp.x + localSide.x) * endWidth), (float) (end.y + (localUp.y + localSide.y) * endWidth), (float) (end.z + (localUp.z + localSide.z) * endWidth)).setUv(0, 0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (end.x + (-localUp.x + localSide.x) * endWidth), (float) (end.y + (-localUp.y + localSide.y) * endWidth), (float) (end.z + (-localUp.z + localSide.z) * endWidth)).setUv(1, 0).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (end.x + (-localUp.x - localSide.x) * endWidth), (float) (end.y + (-localUp.y - localSide.y) * endWidth), (float) (end.z + (-localUp.z - localSide.z) * endWidth)).setUv(1, 1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
			consumer.addVertex(pose, (float) (end.x + (localUp.x - localSide.x) * endWidth), (float) (end.y + (localUp.y - localSide.y) * endWidth), (float) (end.z + (localUp.z - localSide.z) * endWidth)).setUv(0, 1).setColor(0.5F, 0.5F, 0.5F, 1.0F).setLight(LightTexture.FULL_BRIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 1.0F, 0.0F);
		}
	}
}
