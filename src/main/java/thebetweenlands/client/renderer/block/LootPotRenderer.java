package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.LootPotBlock;
import thebetweenlands.common.block.entity.LootPotBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;

public class LootPotRenderer implements BlockEntityRenderer<LootPotBlockEntity> {

	private static final RenderType POT_1_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_pot_1.png"));
	private static final RenderType POT_2_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_pot_2.png"));
	private static final RenderType POT_3_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/loot_pot_3.png"));

	private static final BlockState TAR = BlockRegistry.SOLID_TAR.get().defaultBlockState();
	private static final BlockState MUD = BlockRegistry.COMPACTED_MUD.get().defaultBlockState();

	private final ModelPart pot1;
	private final ModelPart pot2;
	private final ModelPart pot3;

	public LootPotRenderer(BlockEntityRendererProvider.Context context) {
		this.pot1 = context.bakeLayer(BLModelLayers.LOOT_POT_1);
		this.pot2 = context.bakeLayer(BLModelLayers.LOOT_POT_2);
		this.pot3 = context.bakeLayer(BLModelLayers.LOOT_POT_3);
	}

	@Override
	public void render(LootPotBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(LootPotBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		if (entity.getBlockState().is(BlockRegistry.LOOT_POT_1)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot1.render(stack, source.getBuffer(POT_1_TEXTURE), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_POT_2)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot2.render(stack, source.getBuffer(POT_2_TEXTURE), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.LOOT_POT_3)) {
			stack.mulPose(Axis.YP.rotationDegrees(entity.getModelRotationOffset()));
			this.pot3.render(stack, source.getBuffer(POT_3_TEXTURE), light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_1)) {
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.XP.rotationDegrees(10));
			this.pot1.render(stack, source.getBuffer(POT_1_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(TAR, stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_2)) {
			stack.translate(0.075D, -0.325D, 0.1D);
			stack.scale(0.825F, 0.825F, 0.825F);
			stack.mulPose(Axis.YP.rotationDegrees(-30));
			stack.mulPose(Axis.ZP.rotationDegrees(-10));
			this.pot2.render(stack, source.getBuffer(POT_2_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(TAR, stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.TAR_LOOT_POT_3)) {
			stack.translate(0.05D, -0.175D, 0.04D);
			stack.mulPose(Axis.XP.rotationDegrees(5));
			stack.mulPose(Axis.YP.rotationDegrees(-4));
			stack.mulPose(Axis.ZP.rotationDegrees(-4));
			this.pot3.render(stack, source.getBuffer(POT_3_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(TAR, stack, source, light, overlay);
		}  else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_1)) {
			stack.translate(0.0D, -0.25D, 0.0D);
			stack.mulPose(Axis.XP.rotationDegrees(10));
			this.pot1.render(stack, source.getBuffer(POT_1_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(MUD, stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_2)) {
			stack.translate(0.075D, -0.325D, 0.1D);
			stack.scale(0.825F, 0.825F, 0.825F);
			stack.mulPose(Axis.YP.rotationDegrees(-30));
			stack.mulPose(Axis.ZP.rotationDegrees(-10));
			this.pot2.render(stack, source.getBuffer(POT_2_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(MUD, stack, source, light, overlay);
		} else if (entity.getBlockState().is(BlockRegistry.MUD_LOOT_POT_3)) {
			stack.translate(0.05D, -0.175D, 0.04D);
			stack.mulPose(Axis.XP.rotationDegrees(5));
			stack.mulPose(Axis.YP.rotationDegrees(-4));
			stack.mulPose(Axis.ZP.rotationDegrees(-4));
			this.pot3.render(stack, source.getBuffer(POT_3_TEXTURE), light, overlay);
			stack.popPose();
			stack.pushPose();
			Minecraft.getInstance().getBlockRenderer().renderSingleBlock(MUD, stack, source, light, overlay);
		}
		stack.popPose();
	}
}
