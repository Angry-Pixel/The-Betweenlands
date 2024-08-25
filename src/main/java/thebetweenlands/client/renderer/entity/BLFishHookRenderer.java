package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import org.joml.Matrix4f;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.BLFishHookModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entities.fishing.BLFishHook;

public class BLFishHookRenderer extends EntityRenderer<BLFishHook> {

	private static final ResourceLocation HOOK_TEXTURE = TheBetweenlands.prefix("textures/entity/fish_hook.png");
	private final BLFishHookModel hook;

	public BLFishHookRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.hook = new BLFishHookModel(context.bakeLayer(BLModelLayers.FISH_HOOK));
	}

	@Override
	public void render(BLFishHook entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource source, int light) {
		Player player = entity.getPlayerOwner();
		if (player != null) {
			stack.pushPose();
			stack.pushPose();
			stack.translate(0.0F, 1.1F, 0.0F);
			stack.scale(-0.8F, -0.8F, 0.8F);
			this.hook.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
			stack.popPose();
			float attackAnim = player.getAttackAnim(partialTick);
			float f1 = Mth.sin(Mth.sqrt(attackAnim) * Mth.PI);
			Vec3 handPos = this.getPlayerHandPos(player, f1, partialTick);
			Vec3 hookStringPos = entity.getPosition(partialTick).add(0.0D, 0.25D, 0.0D);
			float startX = (float) (handPos.x() - hookStringPos.x());
			float startY = (float) (handPos.y() - hookStringPos.y());
			float startZ = (float) (handPos.z() - hookStringPos.z());
			BlockPos hookPos = BlockPos.containing(entity.getEyePosition(partialTick));
			BlockPos playerPos = BlockPos.containing(player.getEyePosition(partialTick));
			int hookBlockLight = this.getBlockLightLevel(entity, hookPos);
			int playerBlockLight = player.level().getBrightness(LightLayer.BLOCK, playerPos);
			int hookSkyLight = entity.level().getBrightness(LightLayer.SKY, hookPos);
			int playerSkyLight = entity.level().getBrightness(LightLayer.SKY, playerPos);

			VertexConsumer consumer = source.getBuffer(RenderType.leash());
			Matrix4f pose = stack.last().pose();

			for (int count = 0; count <= 48; count++) {
				renderString(consumer, pose, startX, startY, startZ, 0.2375F, 0.2625F, hookBlockLight, playerBlockLight, hookSkyLight, playerSkyLight, count, false);
			}

			for (int count = 48; count >= 0; count--) {
				renderString(consumer, pose, startX, startY, startZ, 0.2625F, 0.2375F, hookBlockLight, playerBlockLight, hookSkyLight, playerSkyLight, count, true);
			}

			stack.popPose();
			super.render(entity, entityYaw, partialTick, stack, source, light);
		}
	}

	private static void renderString(VertexConsumer consumer, Matrix4f pose, float startX, float startY, float startZ, float minOffsetY, float maxOffsetY, int entityBlockLight, int holderBlockLight, int entitySkyLight, int holderSkyLight, int index, boolean reverse) {
		float f = index / 48.0F;
		int i = (int)Mth.lerp(f, entityBlockLight, holderBlockLight);
		int j = (int)Mth.lerp(f, entitySkyLight, holderSkyLight);
		int k = LightTexture.pack(i, j);

		float f1 = index % 2 == (reverse ? 1 : 0) ? 0.8F : 1.0F;
		float f2 = 0.9764F * f1;
		float f3 = 0.9215F * f1;
		float f4 = 0.8F * f1;
		consumer.addVertex(pose, startX * f - 0.0125F, startY * (f * f + f) * 0.5F + maxOffsetY, startZ * f).setColor(f2, f3, f4, 1.0F).setLight(k);
		consumer.addVertex(pose, startX * f + 0.0125F, startY * (f * f + f) * 0.5F + minOffsetY, startZ * f).setColor(f2, f3, f4, 1.0F).setLight(k);
	}

	private Vec3 getPlayerHandPos(Player player, float p_340872_, float partialTick) {
		int i = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
		ItemStack itemstack = player.getMainHandItem();
		if (!itemstack.canPerformAction(ItemAbilities.FISHING_ROD_CAST)) {
			i = -i;
		}

		if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson() && player == Minecraft.getInstance().player) {
			double d4 = 960.0 / (double) this.entityRenderDispatcher.options.fov().get();
			Vec3 vec3 = this.entityRenderDispatcher
				.camera
				.getNearPlane()
				.getPointOnPlane((float)i * 0.525F, -0.1F)
				.scale(d4)
				.yRot(p_340872_ * 0.5F)
				.xRot(-p_340872_ * 0.7F);
			return player.getEyePosition(partialTick).add(vec3);
		} else {
			float f = Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot) * (float) (Math.PI / 180.0);
			double d0 = Mth.sin(f);
			double d1 = Mth.cos(f);
			float f1 = player.getScale();
			double d2 = (double)i * 0.35 * (double)f1;
			double d3 = 0.8 * (double)f1;
			float f2 = player.isCrouching() ? -0.1875F : 0.0F;
			return player.getEyePosition(partialTick).add(-d1 * d2 - d0 * d3, (double)f2 - 0.45 * (double)f1, -d0 * d2 + d1 * d3);
		}
	}

	@Override
	public ResourceLocation getTextureLocation(BLFishHook entity) {
		return HOOK_TEXTURE;
	}
}
