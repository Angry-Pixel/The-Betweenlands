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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.AnimatorBlock;
import thebetweenlands.common.block.entity.BarrelBlockEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.util.RenderUtils;

public class BarrelRenderer implements BlockEntityRenderer<BarrelBlockEntity> {

	private static final RenderType WEEDWOOD_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/weedwood_barrel.png"));
	private static final RenderType SYRMORITE_TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/syrmorite_barrel.png"));
	private final ModelPart barrel;

	public BarrelRenderer(BlockEntityRendererProvider.Context context) {
		this.barrel = context.bakeLayer(BLModelLayers.BARREL);
	}

	@Override
	public void render(BarrelBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.5F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(AnimatorBlock.FACING).toYRot() + 180));
		stack.translate(0.0F, -0.5F, 0.0F);
		stack.scale(1.0F, -1.0F, -1.0F);
		this.barrel.render(stack, source.getBuffer(entity.getBlockState().is(BlockRegistry.SYRMORITE_BARREL) ? SYRMORITE_TEXTURE : WEEDWOOD_TEXTURE), light, overlay);
		stack.popPose();

		if (!entity.fluidTank.getFluid().isEmpty()) {
			FluidStack fluid = entity.fluidTank.getFluid();
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
			int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid);

			float height = fluid.getAmount() / (float)entity.fluidTank.getCapacity() * 0.8F + 0.13F;
			RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, color, 0.25F, 0.75F, height, 0.25F, 0.75F, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
		}
	}
}
