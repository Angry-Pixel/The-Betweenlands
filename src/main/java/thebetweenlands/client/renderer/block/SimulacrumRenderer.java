package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.simulacrum.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.SimulacrumBlock;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class SimulacrumRenderer implements BlockEntityRenderer<SimulacrumBlockEntity> {

	private final DeepmanSimulacrumModel1 deepman1;
	private final DeepmanSimulacrumModel2 deepman2;
	private final DeepmanSimulacrumModel3 deepman3;
	private final LakeCavernSimulacrumModel1 lakeman1;
	private final LakeCavernSimulacrumModel2 lakeman2;
	private final LakeCavernSimulacrumModel3 lakeman3;
	private final RootmanSimulacrumModel1 rootman1;
	private final RootmanSimulacrumModel2 rootman2;
	private final RootmanSimulacrumModel3 rootman3;

	public SimulacrumRenderer(BlockEntityRendererProvider.Context context) {
		this.deepman1 = new DeepmanSimulacrumModel1(context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_1));
		this.deepman2 = new DeepmanSimulacrumModel2(context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_2));
		this.deepman3 = new DeepmanSimulacrumModel3(context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_3));
		this.lakeman1 = new LakeCavernSimulacrumModel1(context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_1));
		this.lakeman2 = new LakeCavernSimulacrumModel2(context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_2));
		this.lakeman3 = new LakeCavernSimulacrumModel3(context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_3));
		this.rootman1 = new RootmanSimulacrumModel1(context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_1));
		this.rootman2 = new RootmanSimulacrumModel2(context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_2));
		this.rootman3 = new RootmanSimulacrumModel3(context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_3));
	}

	@Override
	public void render(SimulacrumBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 1.0F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 1.0F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(SimulacrumBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		if (entity.getBlockState().is(BlockRegistry.DEEPMAN_SIMULACRUM_1)) {
			this.deepman1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_1.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.DEEPMAN_SIMULACRUM_2)) {
			this.deepman2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_2.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.DEEPMAN_SIMULACRUM_3)) {
			this.deepman3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_3.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_1)) {
			this.lakeman1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_1.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_2)) {
			this.lakeman2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_2.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_3)) {
			this.lakeman3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_3.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_1)) {
			this.rootman1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_1.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_2)) {
			this.rootman2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_2.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_3)) {
			this.rootman3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_3.png"))), light, overlay);
		}
		stack.popPose();
	}
}
