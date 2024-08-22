package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.SimulacrumBlock;
import thebetweenlands.common.block.entity.simulacrum.SimulacrumBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class SimulacrumRenderer implements BlockEntityRenderer<SimulacrumBlockEntity> {

	private static final RenderType DEEPMAN_1 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_1.png"));
	private static final RenderType DEEPMAN_2 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_2.png"));
	private static final RenderType DEEPMAN_3 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/deepman_simulacrum_3.png"));
	private static final RenderType LAKEMAN_1 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_1.png"));
	private static final RenderType LAKEMAN_2 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_2.png"));
	private static final RenderType LAKEMAN_3 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/lake_cavern_simulacrum_3.png"));
	private static final RenderType ROOTMAN_1 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_1.png"));
	private static final RenderType ROOTMAN_2 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_2.png"));
	private static final RenderType ROOTMAN_3 = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/rootman_simulacrum_3.png"));

	private final ModelPart deepman1, deepman2, deepman3;
	private final ModelPart lakeman1, lakeman2, lakeman3;
	private final ModelPart rootman1, rootman2, rootman3;

	public SimulacrumRenderer(BlockEntityRendererProvider.Context context) {
		this.deepman1 = context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_1);
		this.deepman2 = context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_2);
		this.deepman3 = context.bakeLayer(BLModelLayers.DEEPMAN_SIMULACRUM_3);
		this.lakeman1 = context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_1);
		this.lakeman2 = context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_2);
		this.lakeman3 = context.bakeLayer(BLModelLayers.LAKE_CAVERN_SIMULACRUM_3);
		this.rootman1 = context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_1);
		this.rootman2 = context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_2);
		this.rootman3 = context.bakeLayer(BLModelLayers.ROOTMAN_SIMULACRUM_3);
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
			this.deepman1.render(stack, source.getBuffer(DEEPMAN_1), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.DEEPMAN_SIMULACRUM_2)) {
			this.deepman2.render(stack, source.getBuffer(DEEPMAN_2), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.DEEPMAN_SIMULACRUM_3)) {
			this.deepman3.render(stack, source.getBuffer(DEEPMAN_3), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_1)) {
			this.lakeman1.render(stack, source.getBuffer(LAKEMAN_1), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_2)) {
			this.lakeman2.render(stack, source.getBuffer(LAKEMAN_2), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LAKE_CAVERN_SIMULACRUM_3)) {
			this.lakeman3.render(stack, source.getBuffer(LAKEMAN_3), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_1)) {
			this.rootman1.render(stack, source.getBuffer(ROOTMAN_1), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_2)) {
			this.rootman2.render(stack, source.getBuffer(ROOTMAN_2), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.ROOTMAN_SIMULACRUM_3)) {
			this.rootman3.render(stack, source.getBuffer(ROOTMAN_3), light, overlay);
		}
		stack.popPose();
	}
}
