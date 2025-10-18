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
import thebetweenlands.client.model.entity.LargeSpiritTreeFaceModel;
import thebetweenlands.client.model.entity.SmallSpiritTreeFaceModel;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.SpiritTreeFaceMask;

public class SpiritTreeFaceMaskRenderer extends EntityRenderer<SpiritTreeFaceMask> {

	//TODO move these to proper entity renderers when theyre added
	public static final ResourceLocation TEXTURE_LARGE = TheBetweenlands.prefix("textures/entity/large_spirit_tree_face.png");
	public static final ResourceLocation TEXTURE_SMALL = TheBetweenlands.prefix("textures/entity/small_spirit_tree_face.png");

	private final LargeSpiritTreeFaceModel largeModel;
	private final SmallSpiritTreeFaceModel smallModel;

	public SpiritTreeFaceMaskRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.smallModel = new SmallSpiritTreeFaceModel(context.bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_FACE_2));
		this.largeModel = new LargeSpiritTreeFaceModel(context.bakeLayer(BLModelLayers.LARGE_SPIRIT_TREE_FACE));
	}

	@Override
	public void render(SpiritTreeFaceMask entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {

		stack.pushPose();

		stack.mulPose(Axis.YP.rotationDegrees(-entityYaw));
		stack.scale(1.0F, -1.0F, -1.0F);

		if(entity.getMaskType() == SpiritTreeFaceMask.MaskType.LARGE) {
			stack.translate(0, -0.4D, 0);
			this.largeModel.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		} else {
			stack.translate(0, 0.4D, -0.5D);
			this.smallModel.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		}

		stack.popPose();

		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(SpiritTreeFaceMask entity) {
		return entity.getMaskType() == SpiritTreeFaceMask.MaskType.LARGE ? TEXTURE_LARGE : TEXTURE_SMALL;
	}
}
