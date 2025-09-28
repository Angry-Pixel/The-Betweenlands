package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.AnadiaModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.fishing.anadia.Anadia;
import thebetweenlands.common.entity.fishing.anadia.AnadiaParts;

import javax.annotation.Nullable;

public class AnadiaRenderer extends MultiPieceMobModelRenderer<Anadia, AnadiaModel> {

	public AnadiaRenderer(EntityRendererProvider.Context context) {
		super(context, new AnadiaModel(context.bakeLayer(BLModelLayers.ANADIA)), 0.5F);
	}

	@Override
	public ResourceLocation getTextureLocation(Anadia entity) {
		return TheBetweenlands.prefix("textures/entity/anadia_1_base.png");
	}

	@Override
	protected void setupRotations(Anadia entity, PoseStack stack, float bob, float yBodyRot, float partialTicks, float scale) {
		float smoothedPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
		float smoothedYaw = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());

		stack.mulPose(Axis.YP.rotationDegrees(180 - smoothedYaw));

		float fishSize = entity.getFishSize();
		stack.translate(0, 0.4f * fishSize, 0);
		//stack.mulPose(Axis.XP.rotationDegrees(-smoothedPitch));
		stack.translate(0, -0.4f * fishSize, 0);

		super.setupRotations(entity, stack, bob, yBodyRot, partialTicks, scale);
	}

	@Override
	protected void scale(Anadia entity, PoseStack stack, float partialTick) {
		stack.scale(entity.getFishSize(), entity.getFishSize(), entity.getFishSize());
	}

	@Override
	protected void renderModel(Anadia entity, PoseStack stack, MultiBufferSource buffer, float partialTicks, float yaw, int light, boolean visible, boolean translucent, boolean glowing) {
		int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
		int color = translucent ? 654311423 : -1;

		RenderType rendertype = this.getRenderType(entity.getHeadType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getHeadType(), "head", stack, vertexconsumer, light, overlay, color);
		}

		rendertype = this.getRenderType(entity.getBodyType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getBodyType(), "body", stack, vertexconsumer, light, overlay, color);
		}

		rendertype = this.getRenderType(entity.getTailType(), entity, visible, translucent, glowing);
		if (rendertype != null) {
			VertexConsumer vertexconsumer = buffer.getBuffer(rendertype);
			this.model.renderPart(entity.getTailType(), "tail", stack, vertexconsumer, light, overlay, color);
		}
	}

	@Nullable
	protected RenderType getRenderType(Enum<?> type, Anadia entity, boolean bodyVisible, boolean translucent, boolean glowing) {
		ResourceLocation texture = this.assembleTexturePath(type, entity.getFishColor());
		if (translucent) {
			return RenderType.itemEntityTranslucentCull(texture);
		} else if (bodyVisible) {
			return this.model.renderType(texture);
		} else {
			return glowing ? RenderType.outline(texture) : null;
		}
	}

	private ResourceLocation assembleTexturePath(Enum<?> type, AnadiaParts.AnadiaColor color) {
		return TheBetweenlands.prefix("textures/entity/anadia/anadia_" + (type.ordinal() + 1) + "_" + color.getSerializedName() + ".png");
	}
}
