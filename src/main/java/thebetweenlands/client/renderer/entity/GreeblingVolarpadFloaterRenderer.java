package thebetweenlands.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.data.ModelData;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.entity.GreeblingVolarpadFloaterModel;
import thebetweenlands.common.entity.creature.GreeblingVolarpadFloater;
import thebetweenlands.common.registries.BlockRegistry;

public class GreeblingVolarpadFloaterRenderer extends EntityRenderer<GreeblingVolarpadFloater> {

	private final BlockRenderDispatcher dispatcher;
	private final GreeblingVolarpadFloaterModel model;

	public GreeblingVolarpadFloaterRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.model = new GreeblingVolarpadFloaterModel(context.bakeLayer(BLModelLayers.GREEBLING_VOLARPAD_FLOATER));
		this.dispatcher = context.getBlockRenderDispatcher();
	}

	@Override
	public void render(GreeblingVolarpadFloater entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int light) {
		stack.pushPose();
		stack.mulPose(Axis.YP.rotationDegrees(entity.smoothedAngle(partialTick)));

		stack.pushPose();
		stack.translate(0.0625D, 1.5D, -0.125D);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.model.renderToBuffer(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity))), light, OverlayTexture.NO_OVERLAY);
		stack.popPose();

		stack.pushPose();
		stack.translate(0.125D, 1.125D, -0.35D);
		stack.scale(0.5F, 0.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(180.0F));
		this.dispatcher.renderSingleBlock(BlockRegistry.VOLARPAD.get().defaultBlockState(), stack, buffer, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.cutout());
		stack.popPose();

		stack.popPose();
		super.render(entity, entityYaw, partialTick, stack, buffer, light);
	}

	@Override
	public ResourceLocation getTextureLocation(GreeblingVolarpadFloater entity) {
		return ChiromawRenderer.TEXTURE;
	}
}
