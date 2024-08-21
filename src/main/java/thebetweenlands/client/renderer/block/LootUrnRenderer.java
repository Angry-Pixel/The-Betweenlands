package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.pot.LootUrnModel1;
import thebetweenlands.client.model.block.pot.LootUrnModel2;
import thebetweenlands.client.model.block.pot.LootUrnModel3;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.LootUrnBlock;
import thebetweenlands.common.block.entity.LootUrnBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class LootUrnRenderer implements BlockEntityRenderer<LootUrnBlockEntity> {

	private final LootUrnModel1 urn1;
	private final LootUrnModel2 urn2;
	private final LootUrnModel3 urn3;

	public LootUrnRenderer(BlockEntityRendererProvider.Context context) {
		this.urn1 = new LootUrnModel1(context.bakeLayer(BLModelLayers.LOOT_URN_1));
		this.urn2 = new LootUrnModel2(context.bakeLayer(BLModelLayers.LOOT_URN_2));
		this.urn3 = new LootUrnModel3(context.bakeLayer(BLModelLayers.LOOT_URN_3));
	}

	@Override
	public void render(LootUrnBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 0.5F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(LootUrnBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		if (entity.getBlockState().is(BlockRegistry.LOOT_URN_1)) {
			this.urn1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_1.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_URN_2)) {
			this.urn2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_2.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_URN_3)) {
			this.urn3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_3.png"))), light, overlay);
		}
		stack.popPose();
	}
}
