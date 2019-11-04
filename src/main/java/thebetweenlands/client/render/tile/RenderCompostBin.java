package thebetweenlands.client.render.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.model.tile.ModelCompostBin;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.tile.TileEntityCompostBin;

import java.util.Random;

public class RenderCompostBin extends TileEntitySpecialRenderer<TileEntityCompostBin> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/compost_bin.png");
	public static final ModelCompostBin MODEL = new ModelCompostBin();

	@Override
	public void render(TileEntityCompostBin te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		int meta = te != null ? te.getBlockMetadata() : 0;
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

		float compostHeight = te != null ? Math.min(te.getCompostedAmount() / (float) TileEntityCompostBin.MAX_COMPOST_AMOUNT, 0.82f) : 0;

		if (compostHeight > 0.01f) {
			IBlockState compost = BlockRegistry.COMPOST_BLOCK.getDefaultState();
			BlockPos blockPos = te.getPos();

			this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

			GlStateManager.pushMatrix();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			GlStateManager.translate(x + 0.5D, y + 0.005D, z + 0.5D);
			GlStateManager.scale(0.8f, compostHeight, 0.8f);
			GlStateManager.translate(-(float)blockPos.getX() - 0.5D, -(float)blockPos.getY(), -(float)blockPos.getZ() - 0.5D);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder vertexbuffer = tessellator.getBuffer();
			vertexbuffer.begin(7, DefaultVertexFormats.BLOCK);

			BlockRendererDispatcher blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
			blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), blockRenderer.getModelForState(compost), compost, blockPos, vertexbuffer, false, MathHelper.getPositionRandom(blockPos));
			tessellator.draw();

			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) x + 0.5f, (float) y, (float) z + 0.5f);
		GlStateManager.rotate(getRotation(meta), 0.0F, 1F, 0F);

		GlStateManager.pushMatrix();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.translate(0, 1.5f, 0);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.disableCull();

		this.bindTexture(TEXTURE);
		MODEL.render(te != null ? te.getLidAngle(partialTicks) : 0);

		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if(te != null) {
			for (int i = 0; i < te.getSizeInventory(); i++) {
				ItemStack stack = te.getStackInSlot(i);

				if (!stack.isEmpty()) {
					GlStateManager.pushMatrix();

					// 0.4 for items, 0.5 for compost
					GlStateManager.translate(0, 0.005f + compostHeight + i * 0.4f / te.getSizeInventory(), 0.08f);
					GlStateManager.scale(0.6f, 0.6f, 0.6f);
					GlStateManager.rotate(new Random(i * 12315).nextFloat() * 360f, 0, 1, 0);
					GlStateManager.rotate(90.0f, 1, 0, 0);

					renderItem.renderItem(stack, TransformType.FIXED);

					GlStateManager.popMatrix();
				}
			}
		}

		GlStateManager.popMatrix();
	}

	public static float getRotation(int meta) {
		switch (meta) {
		case 5:
			return 180F;
		case 4:
		default:
			return 0F;
		case 3:
			return 90F;
		case 2:
			return -90F;
		}
	}
}