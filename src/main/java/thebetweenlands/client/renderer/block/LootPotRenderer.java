package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.model.block.pot.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.LootPotBlock;
import thebetweenlands.common.block.entity.LootPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class LootPotRenderer implements BlockEntityRenderer<LootPotBlockEntity> {

	private final LootPotModel1 pot1;
	private final LootPotModel2 pot2;
	private final LootPotModel3 pot3;

	public LootPotRenderer(BlockEntityRendererProvider.Context context) {
		this.pot1 = new LootPotModel1(context.bakeLayer(BLModelLayers.LOOT_POT_1));
		this.pot2 = new LootPotModel2(context.bakeLayer(BLModelLayers.LOOT_POT_2));
		this.pot3 = new LootPotModel3(context.bakeLayer(BLModelLayers.LOOT_POT_3));
	}

	@Override
	public void render(LootPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(Axis.XP.rotationDegrees(180.0F));
		stack.translate(0.0F, 0.5F, 0.0F);
		stack.mulPose(Axis.YP.rotationDegrees(entity.getBlockState().getValue(LootPotBlock.FACING).toYRot()));
		stack.scale(-1.0F, 1.0F, 1.0F);
		if (entity.getBlockState().is(BlockRegistry.LOOT_POT_1)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_1.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_POT_2)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_2.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_POT_3)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_3.png"))), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_1)) {
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.XP.rotationDegrees(10));
			this.pot1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_1.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.SOLID_TAR.get().defaultBlockState(), stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_2)) {
			stack.translate(0.075D, -0.325D, 0.1D);
			stack.scale(0.825F, 0.825F, 0.825F);
			stack.mulPose(Axis.YP.rotationDegrees(-30));
			stack.mulPose(Axis.ZP.rotationDegrees(-10));
			this.pot2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_2.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.SOLID_TAR.get().defaultBlockState(), stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_3)) {
			stack.translate(0.05D, -0.175D, 0.04D);
			stack.mulPose(Axis.XP.rotationDegrees(5));
			stack.mulPose(Axis.YP.rotationDegrees(-4));
			stack.mulPose(Axis.ZP.rotationDegrees(-4));
			this.pot3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_3.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.SOLID_TAR.get().defaultBlockState(), stack, source, light, overlay);
		}  else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_1)) {
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.XP.rotationDegrees(10));
			this.pot1.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_1.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.COMPACTED_MUD.get().defaultBlockState(), stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_2)) {
			stack.translate(0.075D, -0.325D, 0.1D);
			stack.scale(0.825F, 0.825F, 0.825F);
			stack.mulPose(Axis.YP.rotationDegrees(-30));
			stack.mulPose(Axis.ZP.rotationDegrees(-10));
			this.pot2.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_2.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.COMPACTED_MUD.get().defaultBlockState(), stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_3)) {
			stack.translate(0.05D, -0.175D, 0.04D);
			stack.mulPose(Axis.XP.rotationDegrees(5));
			stack.mulPose(Axis.YP.rotationDegrees(-4));
			stack.mulPose(Axis.ZP.rotationDegrees(-4));
			this.pot3.renderToBuffer(stack, source.getBuffer(RenderType.entityCutoutNoCull(TheBetweenlands.prefix("textures/entity/block/loot_pot_3.png"))), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(BlockRegistry.COMPACTED_MUD.get().defaultBlockState(), stack, source, light, overlay);
		}
		stack.popPose();
	}
}
