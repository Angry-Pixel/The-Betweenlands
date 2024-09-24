package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.shader.LightSource;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.terrain.WaystoneBlock;
import thebetweenlands.common.block.entity.WaystoneBlockEntity;

public class WaystoneRenderer implements BlockEntityRenderer<WaystoneBlockEntity> {

	private static final RenderType ACTIVE_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/waystone_active.png"));
	private static final RenderType INACTIVE_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/waystone_inactive.png"));
	private static final RenderType GRASS_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/waystone_grass.png"));
	private final ModelPart waystone;

	public WaystoneRenderer(BlockEntityRendererProvider.Context context) {
		this.waystone = context.bakeLayer(BLModelLayers.WAYSTONE);
	}

	@Override
	public void render(WaystoneBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getRotation()));
		stack.scale(1.0F, -1.0F, -1.0F);

		if (entity.getLevel() != null && entity.getBlockState().getValue(WaystoneBlock.ACTIVE) && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			double px = entity.getBlockPos().getX() + 0.5D;
			double py = entity.getBlockPos().getY();
			double pz = entity.getBlockPos().getZ() + 0.5D;

			Player closestPlayer = entity.getLevel().getNearestPlayer(px, py, pz, 4.0D, false);

			if (closestPlayer != null) {
				ShaderHelper.INSTANCE.require();

				float brightness = 3.0F * (float) (1.0D - closestPlayer.distanceToSqr(px, py, pz) / 4.0D);

				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(px, py, pz, 8F, 0.44f * brightness, 0.8f * brightness, brightness));
			}
		}

		this.waystone.render(stack, source.getBuffer(entity.getBlockState().getValue(WaystoneBlock.ACTIVE) ? ACTIVE_TEXTURE : INACTIVE_TEXTURE), light, overlay);
		int color = entity.getLevel() != null ? BiomeColors.getAverageGrassColor(entity.getLevel(), entity.getBlockPos()) : GrassColor.getDefaultColor();
		this.waystone.render(stack, source.getBuffer(GRASS_TEXTURE), light, overlay, color);
		stack.popPose();
	}

	@Override
	public AABB getRenderBoundingBox(WaystoneBlockEntity entity) {
		return new AABB(entity.getBlockPos()).expandTowards(0.0D, 2.0D, 0.0D);
	}
}
