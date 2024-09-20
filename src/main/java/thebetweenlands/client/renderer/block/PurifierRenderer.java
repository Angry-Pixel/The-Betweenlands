package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.container.PurifierBlock;
import thebetweenlands.common.block.entity.PurifierBlockEntity;
import thebetweenlands.util.RenderUtils;

public class PurifierRenderer implements BlockEntityRenderer<PurifierBlockEntity> {

	private static final RenderType TEXTURE = RenderType.entityCutout(TheBetweenlands.prefix("textures/entity/block/purifier.png"));
	private final ModelPart base;
	private final ModelPart fire;
	private final ItemRenderer itemRenderer;

	public PurifierRenderer(BlockEntityRendererProvider.Context context) {
		ModelPart root = context.bakeLayer(BLModelLayers.PURIFIER);
		this.base = root.getChild("base");
		this.fire = root.getChild("fire_plate");
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(PurifierBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource source, int light, int overlay) {
		stack.pushPose();
		stack.translate(0.5F, 0.0F, 0.5F);
		stack.mulPose(Axis.YP.rotationDegrees(-entity.getBlockState().getValue(PurifierBlock.FACING).toYRot()));
		stack.scale(1.0F, -1.0F, -1.0F);
		this.base.render(stack, source.getBuffer(TEXTURE), light, overlay);
		if (entity.getBlockState().getValue(PurifierBlock.LIT)) {
			this.fire.render(stack, source.getBuffer(TEXTURE), light, overlay);
		}
		stack.popPose();

		int amount = entity.tank.getFluidAmount();
		int capacity = entity.tank.getCapacity();
		float size = 1.0F / capacity * amount;
		if (!entity.getItem(2).isEmpty()) {
			stack.pushPose();
			stack.translate(0.5D, 0.27D, 0.5D);
			stack.mulPose(Axis.XP.rotationDegrees(180.0F));
			int items = entity.getItem(2).getCount() / 4 + 1;
			RandomSource rand = RandomSource.create(entity.getBlockPos().asLong());
			for (int i = 0; i < items; i++) {
				stack.pushPose();
				stack.translate(rand.nextFloat() / 3.0D - 1.0D / 6.0D, -0.25D, rand.nextFloat() / 3.0D - 1.0D / 6.0D);
				stack.mulPose(Axis.XP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
				stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 30.0F - 15.0F));
				stack.scale(0.15F, 0.15F, 0.15F);
				stack.mulPose(Axis.XN.rotationDegrees(90));
				stack.mulPose(Axis.ZP.rotationDegrees(rand.nextFloat() * 360.0F));
				this.itemRenderer.renderStatic(entity.getItem(2), ItemDisplayContext.FIXED, light, overlay, stack, source, null, 0);
				stack.popPose();
			}
			stack.popPose();
		}

		if (amount >= 100) {
			FluidStack fluidStack = entity.tank.getFluid();
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluidStack.getFluid()).getStillTexture());

			stack.pushPose();
			stack.translate(0.0F, 0.35F + size * 0.5F, 0.0F);
			RenderUtils.renderTopQuad(stack.last(), source.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, FastColor.ARGB32.colorFromFloat(1.0F, 0.2F, 0.6F, 0.4F), 0.1F, 0.9F, 0.0F, 0.1F, 0.9F, sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());
			stack.popPose();
		}
	}
}
