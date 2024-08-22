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
import thebetweenlands.common.block.LootUrnBlock;
import thebetweenlands.common.block.entity.LootUrnBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class LootUrnRenderer implements BlockEntityRenderer<LootUrnBlockEntity> {

	private static final RenderType URN_1_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_1.png"));
	private static final RenderType URN_2_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_2.png"));
	private static final RenderType URN_3_TEXTURE = RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_urn_3.png"));

	private final ModelPart urn1;
	private final ModelPart urn2;
	private final ModelPart urn3;

	public LootUrnRenderer(BlockEntityRendererProvider.Context context) {
		this.urn1 = context.bakeLayer(BLModelLayers.LOOT_URN_1);
		this.urn2 = context.bakeLayer(BLModelLayers.LOOT_URN_2);
		this.urn3 = context.bakeLayer(BLModelLayers.LOOT_URN_3);
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
			this.urn1.render(stack, source.getBuffer(URN_1_TEXTURE), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_URN_2)) {
			this.urn2.render(stack, source.getBuffer(URN_2_TEXTURE), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_URN_3)) {
			this.urn3.render(stack, source.getBuffer(URN_3_TEXTURE), light, overlay);
		}
		stack.popPose();
	}
}
