package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.AnimatorBlock;
import thebetweenlands.common.block.container.RubberTapBlock;
import thebetweenlands.common.block.entity.RubberTapBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.util.RenderUtils;

public class RubberTapRenderer implements BlockEntityRenderer<RubberTapBlockEntity> {

	private static final RenderType WEEDWOOD_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/weedwood_rubber_tap.png"));
	private static final RenderType SYRMORITE_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/syrmorite_rubber_tap.png"));
	private final ModelPart tap;
	private final ModelPart flow;

	public RubberTapRenderer(BlockEntityRendererProvider.Context context) {
		this.tap = context.bakeLayer(BLModelLayers.RUBBER_TAP);
		this.flow = context.bakeLayer(BLModelLayers.RUBBER_TAP_FLOW);
	}

	@Override
	public void render(RubberTapBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(AnimatorBlock.FACING).toYRot()));
		stack.translate(0.0F, -0.5F, 0.0F);
		stack.scale(1.0F, -1.0F, -1.0F);
		stack.pushPose();
		this.tap.render(stack, source.getBuffer(entity.getBlockState().is(BlockRegistry.SYRMORITE_RUBBER_TAP) ? SYRMORITE_TEXTURE : WEEDWOOD_TEXTURE), light, overlay);
		stack.popPose();

		if (entity.tank.getFluidAmount() < entity.tank.getCapacity()) {
			TextureAtlasSprite flow = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(FluidRegistry.RUBBER_FLOW.get()).getFlowingTexture());
			this.flow.render(stack, flow.wrap(source.getBuffer(RenderType.entityTranslucent(flow.atlasLocation()))), light, overlay);
		}
		stack.popPose();

		if (!entity.tank.isEmpty()) {
			FluidStack fluid = entity.tank.getFluid();
			TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
			Direction dir = entity.getBlockState().getValue(RubberTapBlock.FACING);

			stack.pushPose();
			stack.translate(-0.3F * dir.getStepX(), 0.0F, -0.3F * dir.getStepZ());
			int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid) | 0xFF000000;

			float height = fluid.getAmount() / (float)entity.tank.getCapacity() * 0.65F + 0.1F;
			RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(still.atlasLocation())), light, color, 0.2F, 0.8F, height, 0.2F, 0.8F, still.getU0(), still.getU1(), still.getV0(), still.getV1());
			stack.popPose();
		}
	}
}
