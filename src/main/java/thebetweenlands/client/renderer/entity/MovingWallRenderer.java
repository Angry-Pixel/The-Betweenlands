package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.MovingWallModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.MovingWall;

public class MovingWallRenderer extends EntityRenderer<MovingWall> {

	private static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/entity/moving_wall.png");

	private final MovingWallModel model;

	public MovingWallRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new MovingWallModel(context.bakeLayer(BLModelLayers.MOVING_WALL));
	}

	@Override
	public void render(MovingWall entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(entity.getYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		stack.translate(0.0D, -1.5D, 1.0D);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(MovingWall entity) {
		return TEXTURE;
	}
}
