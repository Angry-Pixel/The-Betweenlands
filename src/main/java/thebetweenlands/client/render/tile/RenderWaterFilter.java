package thebetweenlands.client.render.tile;

import java.util.SplittableRandom;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.tile.TileEntityWaterFilter;

@SideOnly(Side.CLIENT)
public class RenderWaterFilter extends TileEntitySpecialRenderer<TileEntityWaterFilter> {

	@Override
	public void render(TileEntityWaterFilter tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {

		if(tile == null || !tile.hasWorld())
			return;

		float fluidLevel = tile.tank.getFluidAmount();
		float height = 0.375F;
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		SplittableRandom rand = new SplittableRandom((long) (tile.getPos().getX() + tile.getPos().getY() + tile.getPos().getZ()));
		float xMax, zMax, xMin, zMin, yMin = 0;

		if (fluidLevel > 0) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			FluidStack fluidStack = tile.tank.getFluid();
			height = (0.4375F / tile.tank.getCapacity()) * tile.tank.getFluidAmount();
			TextureAtlasSprite fluidStillSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill().toString());
			int fluidColor = fluidStack.getFluid().getColor(fluidStack);
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.translate(x, y + 0.5F, z);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			xMax = 1.75F;
			zMax = 1.75F;
			xMin = 0.25F;
			zMin = 0.25F;
			yMin = 0F;

			setGLColorFromInt(fluidColor, 1F);
			renderCuboid(buffer, xMax, xMin, yMin, height, zMin, zMax, fluidStillSprite);
			tessellator.draw();

			if(tile.getFluidAnimation()) {
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				GlStateManager.translate(0F, -0.1875F, 0F);
				renderCuboid(buffer, 1.0625F, 0.9375F, -0.125F, 0.25F, 0.9375F, 1.0625F, fluidStillSprite);
				if (tile.hasMossFilter() || tile.hasSilkFilter())
					renderCuboid(buffer, 1.375F, 0.625F, 0F, 0.00625F, 0.625F, 1.375F, fluidStillSprite);
				tessellator.draw();
			}

			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

		if (!tile.getStackInSlot(0).isEmpty()) {
			GlStateManager.pushMatrix();
			double yUp = 0.28125D;
			GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5D, 1D, 0.5D);
			GlStateManager.rotate(90, 1, 0, 0);
			ItemStack stack = tile.getStackInSlot(0);
			renderItem.renderItem(stack, TransformType.FIXED);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
		}

		if (!tile.getStackInSlot(1).isEmpty()) {
			ItemStack stack = tile.getStackInSlot(1);
			double yUp = 0.3125;
			int items = tile.getStackInSlot(1).getCount() / 4 + 1;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			for (int i = 0; i < items; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rand.nextDouble() * 0.25D - 0.125D, 0.0D, rand.nextDouble() * 0.25D - 0.125D);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
				GlStateManager.scale(0.125D, 0.125D, 0.125D);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 360.0F, 0, 0, 1);
				renderItem.renderItem(stack, TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}
		
		if (!tile.getStackInSlot(2).isEmpty()) {
			ItemStack stack = tile.getStackInSlot(2);
			double yUp = 0.3125;
			int items = tile.getStackInSlot(2).getCount() / 4 + 1;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			for (int i = 0; i < items; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rand.nextDouble() * 0.25D - 0.125D, 0.0D, rand.nextDouble() * 0.25D - 0.125D);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
				GlStateManager.scale(0.125D, 0.125D, 0.125D);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 360.0F, 0, 0, 1);
				renderItem.renderItem(stack, TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}
		
		if (!tile.getStackInSlot(3).isEmpty()) {
			ItemStack stack = tile.getStackInSlot(3);
			double yUp = 0.3125;
			int items = tile.getStackInSlot(3).getCount() / 4 + 1;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			for (int i = 0; i < items; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rand.nextDouble() * 0.25D - 0.125D, 0.0D, rand.nextDouble() * 0.25D - 0.125D);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
				GlStateManager.scale(0.125D, 0.125D, 0.125D);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 360.0F, 0, 0, 1);
				renderItem.renderItem(stack, TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}
		
		if (!tile.getStackInSlot(4).isEmpty()) {
			ItemStack stack = tile.getStackInSlot(4);
			double yUp = 0.3125;
			int items = tile.getStackInSlot(4).getCount() / 4 + 1;
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, y + yUp, z + 0.5D);
			GlStateManager.rotate(180, 1, 0, 0);
			for (int i = 0; i < items; i++) {
				GlStateManager.pushMatrix();
				GlStateManager.translate(rand.nextDouble() * 0.25D - 0.125D, 0.0D, rand.nextDouble() * 0.25D - 0.125D);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 30.0f - 15.0f, 0, 0, 1);
				GlStateManager.scale(0.125D, 0.125D, 0.125D);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.rotate((float) rand.nextDouble() * 360.0F, 0, 0, 1);
				renderItem.renderItem(stack, TransformType.FIXED);
				GlStateManager.popMatrix();
			}
			GlStateManager.popMatrix();
		}
	}

	private void setGLColorFromInt(int color, float alpha) {
		float red = (color >> 16 & 0xFF) / 255.0F;
		float green = (color >> 8 & 0xFF) / 255.0F;
		float blue = (color & 0xFF) / 255.0F;
		GlStateManager.color(red, green, blue, alpha);
	}

	private void renderCuboid(BufferBuilder buffer, float xMax, float xMin, float yMin, float height, float zMin, float zMax, TextureAtlasSprite textureAtlasSprite) {

		double uMin = (double) textureAtlasSprite.getMinU();
		double uMax = (double) textureAtlasSprite.getMaxU();
		double vMin = (double) textureAtlasSprite.getMinV();
		double vMax = (double) textureAtlasSprite.getMaxV();

		final double vHeight = vMax - vMin;

		// top
		addVertexWithUV(buffer, xMax, height, zMax, uMax, vMin);
		addVertexWithUV(buffer, xMax, height, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMin, uMin, vMax);
		addVertexWithUV(buffer, xMin, height, zMax, uMax, vMax);

		// north
		addVertexWithUV(buffer, xMax, yMin, zMin, uMax, vMin);
		addVertexWithUV(buffer, xMin, yMin, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMin, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, height, zMin, uMax, vMin + (vHeight * height));

		// south
		addVertexWithUV(buffer, xMax, yMin, zMax, uMin, vMin);
		addVertexWithUV(buffer, xMax, height, zMax, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, height, zMax, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, yMin, zMax, uMax, vMin);

		// east
		addVertexWithUV(buffer, xMax, yMin, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMax, height, zMin, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, height, zMax, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMax, yMin, zMax, uMax, vMin);

		// west
		addVertexWithUV(buffer, xMin, yMin, zMax, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMax, uMin, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, height, zMin, uMax, vMin + (vHeight * height));
		addVertexWithUV(buffer, xMin, yMin, zMin, uMax, vMin);

	}

	private void addVertexWithUV(BufferBuilder buffer, float x, float y, float z, double u, double v) {
		buffer.pos(x / 2f, y, z / 2f).tex(u, v).endVertex();
	}
}