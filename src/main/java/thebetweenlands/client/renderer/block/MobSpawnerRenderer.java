package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.spawner.MobSpawnerBlockEntity;

public class MobSpawnerRenderer implements BlockEntityRenderer<MobSpawnerBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityTranslucent(TheBetweenlands.prefix("textures/entity/block/spawner_crystal.png"));
	private final ModelPart crystal;

	public MobSpawnerRenderer(BlockEntityRendererProvider.Context context) {
		this.crystal = context.bakeLayer(BLModelLayers.MOB_SPAWNER_CRYSTAL);
	}

	@Override
	public void render(MobSpawnerBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		float interpolatedCounter = -(entity.lastCounter + (entity.counter - entity.lastCounter) * partialTicks);

		float counter1 = interpolatedCounter;
		stack.pushPose();
		stack.translate(0.475F, 0.38F + (float) Math.sin(counter1) / 5.0F,  0.475F);
		stack.scale(0.3F, 0.3F, 0.3F);
		if (entity.getLevel() != null) {
			renderSpawnerMob(entity, partialTicks, stack, source);
		}
		stack.popPose();

		stack.pushPose();
		stack.translate(0.45F, 1.8F + (float) Math.sin(counter1) / 5.0F, 0.45F);
		stack.translate(0.025F, -0.5F, 0.025F);
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(counter1 * 180));
		stack.scale(2.5F, 2.5F, 2.5F);
		this.renderCrystal(stack, source, overlay, this.calculateColor(counter1, true));
		stack.popPose();

		float counter2 = interpolatedCounter / 1.5F;
		stack.pushPose();
		stack.translate(0.475F, 1.5F + (float) Math.sin(counter2 * 3) / 2.0F - 0.5F, 0.475F);
		stack.translate(0.025F, -0.5F, 0.025F);
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(counter2 * 720));
		stack.translate(0.5F, 0, 0);
		stack.scale(2.25F, 2.25F, 2.25F);
		stack.scale(0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2, 0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2, 0.2F + (float) Math.sin(counter2) * (float) Math.sin(counter2) / 2);
		this.renderCrystal(stack, source, overlay, this.calculateColor(counter2, false));
		stack.popPose();

		float counter3 = interpolatedCounter / 2.0F;
		stack.pushPose();
		stack.translate(0.475F, 1.5F - (float) Math.cos(counter3 * 3) / 2.0F, 0.475F);
		stack.translate(0.025F, -0.5F, 0.025F);
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(counter1 * 720));
		stack.translate(0, 0, 0.5F);
		stack.scale(2.25F, 2.25F, 2.25F);
		stack.scale(0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2, 0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2, 0.2F + (float) Math.sin(counter3) * (float) Math.sin(counter3) / 2);
		this.renderCrystal(stack, source, overlay, this.calculateColor(counter3, false));
		stack.popPose();

		float counter4 = interpolatedCounter / 2.5F;
		stack.pushPose();
		stack.translate(0.475F, 1.5F + (float) Math.cos(counter4 * 3) / 2.0F - 0.5F, 0.475F);
		stack.translate(0.025F, -0.5F, 0.025F);
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(counter1 * 720));
		stack.translate(0.5F, 0, 0.5F);
		stack.scale(2.25F, 2.25F, 2.25F);
		stack.scale(0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2, 0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2, 0.2F + (float) Math.sin(counter4) * (float) Math.sin(counter4) / 2);
		this.renderCrystal(stack, source, overlay, this.calculateColor(counter4, false));
		stack.popPose();

		float counter5 = interpolatedCounter / 3.0F;
		stack.pushPose();
		stack.translate(0.475F, 1.5F + (float) Math.cos(counter5 * 3) / 2.0F - 0.5F, 0.475F);
		stack.translate(0.025F, -0.5F, 0.025F);
		stack.mulPose(Axis.ZP.rotationDegrees(180.0F));
		stack.mulPose(Axis.YP.rotationDegrees(counter1 * 720));
		stack.translate(0F, 0, -0.5F);
		stack.scale(2.25F, 2.25F, 2.25F);
		stack.scale(0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2, 0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2, 0.2F + (float) Math.sin(counter5) * (float) Math.sin(counter5) / 2);
		this.renderCrystal(stack, source, overlay, this.calculateColor(counter5, false));
		stack.popPose();
	}

	public static void renderSpawnerMob(MobSpawnerBlockEntity spawner, float partialTicks, PoseStack stack, MultiBufferSource source) {
		Entity entity = spawner.getSpawner().getOrCreateDisplayEntity(spawner.getLevel(), spawner.getBlockPos());
		if (entity != null) {
			float scale = 0.4375F;
			stack.translate(0.0F, 0.4F, 0.0F);
			stack.mulPose(Axis.YP.rotationDegrees((float) ((spawner.getSpawner().oSpin + (spawner.getSpawner().spin - spawner.getSpawner().oSpin) * partialTicks) * 10.0F)));
			stack.mulPose(Axis.XN.rotationDegrees(30.0F));
			stack.translate(0.0F, -0.4F, 0.0F);
			stack.scale(scale, scale, scale);
			EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
			boolean hitboxes = dispatcher.shouldRenderHitBoxes();
			dispatcher.setRenderShadow(false);
			dispatcher.setRenderHitBoxes(false);
			dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, stack, source, LightTexture.FULL_BRIGHT);
			dispatcher.setRenderShadow(true);
			dispatcher.setRenderHitBoxes(hitboxes);
		}
	}

	private int calculateColor(float counter, boolean constantAlpha) {
		return FastColor.ARGB32.colorFromFloat(
			constantAlpha ? 0.5F : Math.clamp((float) Math.cos(counter) * (float) Math.cos(counter) * 2.0F, 0.0F, 1.0F),
			1.0F,
			Math.clamp(4.0F + (float) Math.sin(counter) * 3.0F, 0.0F, 1.0F),
			Math.clamp((float) Math.sin(counter) * 2.0F, 0.0F, 1.0F));
	}

	private void renderCrystal(PoseStack stack, MultiBufferSource source, int overlay, int color) {
		stack.pushPose();
		stack.scale(0.1F, 0.3F, 0.1F);
		this.crystal.render(stack, source.getBuffer(TEXTURE), LightTexture.FULL_BRIGHT, overlay, color);
		stack.popPose();
	}
}
