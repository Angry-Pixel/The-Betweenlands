package thebetweenlands.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import thebetweenlands.client.BLModelLayers;
import thebetweenlands.client.renderer.entity.SmallSpiritTreeFaceRenderer;
import thebetweenlands.client.renderer.entity.SpiritTreeFaceMaskRenderer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.block.entity.GrubHubBlockEntity;
import thebetweenlands.util.RenderUtils;

import java.util.SplittableRandom;

public class GrubHubRenderer implements BlockEntityRenderer<GrubHubBlockEntity> {

	private final RenderType MASK_TYPE = RenderType.entityCutout(SmallSpiritTreeFaceRenderer.TEXTURE);
	private final RenderType EYE_TYPE = RenderType.EYES.apply(SmallSpiritTreeFaceRenderer.GLOW_TEXTURE, RenderType.TRANSLUCENT_TRANSPARENCY);

	private final ModelPart mask;
	private final ItemRenderer itemRenderer;

	public GrubHubRenderer(BlockEntityRendererProvider.Context context) {
		this.mask = context.bakeLayer(BLModelLayers.SMALL_SPIRIT_TREE_FACE_2);
		this.itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(GrubHubBlockEntity entity, float partialTick, PoseStack stack, MultiBufferSource buffer, int light, int overlay) {
		float fluidLevel = entity.tank.getFluidAmount();
		SplittableRandom rand = new SplittableRandom(entity.getBlockPos().asLong());

		for (Direction dir : Direction.Plane.HORIZONTAL) {
			stack.pushPose();
			stack.translate(0.5F + (0.9F * dir.getStepX()), 0.0F, 0.5F + (0.9F * dir.getStepZ()));
			stack.scale(1.0F, -1.0F, -1.0F);
			stack.mulPose(Axis.YP.rotationDegrees(dir.toYRot()));
			this.mask.render(stack, buffer.getBuffer(MASK_TYPE), light, overlay);
			if (entity.switchTextureCount > 0) {
				float opacity = Math.min(1.0F, entity.switchTextureCount / 10.0F);
				this.mask.render(stack, buffer.getBuffer(EYE_TYPE), light, overlay, FastColor.ARGB32.colorFromFloat(opacity, 1.0F, 1.0F, 1.0F));
			}
			stack.popPose();
		}

		if (fluidLevel > 0) {
			FluidStack fluid = entity.tank.getFluid();
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(fluid.getFluid()).getStillTexture());
			int color = IClientFluidTypeExtensions.of(fluid.getFluid()).getTintColor(fluid) | 0xFF000000;
			stack.pushPose();
			float textureYPos = (0.675F * ((float) entity.tank.getFluidAmount() / entity.tank.getCapacity()));
			RenderUtils.renderCuboid(stack.last(), buffer.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), light, color, sprite, 0.15F, 0.85F, 0.05F, 0.05F + textureYPos, 0.15F, 0.85F);
			stack.popPose();
		}

		ItemStack grubs = entity.getItem(0);

		if (!grubs.isEmpty()) {
			stack.pushPose();
			double yUp = 0.8125D;
			stack.translate(0.5D, yUp, 0.5D);
			for (int i = 0; i < grubs.getCount(); i++) {
				stack.pushPose();
				stack.translate(rand.nextDouble() / 2.0D - 1.0D / 4.0D, 0.0D, rand.nextDouble() / 2.0D - 1.0D / 4.0D);
				stack.mulPose(Axis.YP.rotationDegrees(rand.nextFloat() * 360.0F - 180.0F));
				stack.scale(0.25F, 0.25F, 0.25F);
				this.itemRenderer.renderStatic(grubs, ItemDisplayContext.FIXED, light, overlay, stack, buffer, null, 0);
				stack.popPose();
			}
			stack.popPose();
		}
	}
}
