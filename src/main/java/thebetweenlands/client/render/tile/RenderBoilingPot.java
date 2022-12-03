package thebetweenlands.client.render.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.tile.TileEntityBoilingPot;

@SideOnly(Side.CLIENT)
public class RenderBoilingPot extends TileEntitySpecialRenderer<TileEntityBoilingPot> {

	@Override
	public void render(TileEntityBoilingPot tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		if(tile == null || !tile.hasWorld())
			return;
		
		float fluidLevel = tile.tank.getFluidAmount();
		float height = 0.0625F;

		if (fluidLevel > 0) {
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			FluidStack fluidStack = tile.tank.getFluid();
			height = (0.375F / tile.tank.getCapacity()) * tile.tank.getFluidAmount();
			TextureAtlasSprite fluidStillSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill().toString());
			if(tile.getHeatProgress() > 0  && tile.tank.getFluid().getFluid() == FluidRegistry.DYE_FLUID || tile.tank.getFluid().getFluid() == FluidRegistry.DRINKABLE_BREW)
				fluidStillSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getFlowing().toString());	
			int fluidColor = fluidStack.getFluid().getColor(fluidStack);
			int fluidColorTemp = tile.tempFluidColour;
			float fade = tile.getHeatProgress() > 50 && tile.hasBundle() ? (-50 + tile.getHeatProgress()) * 0.02F: 0F;
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.translate(x, y + 0.25F, z);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			float xMax, zMax, xMin, zMin, yMin = 0;

			xMax = 1.625F;
			zMax = 1.625F;
			xMin = 0.375F;
			zMin = 0.375F;
			yMin = 0F;

			if (fluidLevel >= Fluid.BUCKET_VOLUME) {
				xMax = 1.75F;
				zMax = 1.75F;
				xMin = 0.25F;
				zMin = 0.25F;
				yMin = 0F;
			}
			setGLColorFromInt(fluidColor, 1F - fade);
			renderCuboid(buffer, xMax, xMin, yMin, height, zMin, zMax, fluidStillSprite);
			tessellator.draw();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			
			GlStateManager.disableLighting();
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			GlStateManager.translate(x, y + 0.25F, z);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			setGLColorFromInt(fluidColorTemp, 0F + fade);
			renderCuboid(buffer, xMax, xMin, yMin, height + 0.01F, zMin, zMax, fluidStillSprite);
			tessellator.draw();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
		}

		if (!tile.getStackInSlot(0).isEmpty()) {
			int itemBob = tile.itemBob;
			int prevItemBob = tile.prevItemBob;
			int stirProgress = tile.itemRotate;
			int prevStirProgress = tile.prevItemRotate;
			float stirTicks = stirProgress + (stirProgress - prevStirProgress) * partialTick;
			float bobTicks = itemBob + (itemBob - prevItemBob) * partialTick;

			double itemY = y + 0.25D + height;
			Random rand = new Random();
			rand.setSeed((long) (tile.getPos().getX() + tile.getPos().getY() + tile.getPos().getZ()));
			float randRot = rand.nextFloat() * 360.0F;
			double xo = -0.2D + rand.nextFloat() * 0.4D;
			double zo = -0.2D + rand.nextFloat() * 0.4D;
			double rot = (stirTicks < 180 && fluidLevel > 0 ? stirTicks * 2D + 90D + randRot : 90D + randRot);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5D, 0, z + 0.5D);
			GlStateManager.rotate((float) -rot, 0, 1, 0);
			GlStateManager.translate(xo, 0, zo);
			renderItemInSlot(tile, 0, 0, itemY, 0, fluidLevel > 0 ? bobTicks : 0D, -rot);
			GlStateManager.popMatrix();
		}

	}

	private void renderItemInSlot(TileEntityBoilingPot tile, int slotIndex, double x, double y, double z, double itemBob, double rotation) {
		if (!tile.getStackInSlot(slotIndex).isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.translate(x, y, z);
			GlStateManager.scale(0.5D, 0.5D, 0.5D);
			GlStateManager.translate(0D, itemBob * 0.0025F, 0D);
			GlStateManager.rotate((float) rotation, 0, 1, 0);
			GlStateManager.rotate(tile.getHeatProgress() < 20 ? 0F : (float) itemBob * 0.2F, 0, 0, 1);
			GlStateManager.rotate(tile.getHeatProgress() < 40 ? 0F : (float) itemBob * 0.2F, 1, 0, 0);
			GlStateManager.rotate(tile.getHeatProgress() < 60 ? 0F : (float) itemBob * 0.4F, 0, -1, 0);
			Minecraft.getMinecraft().getRenderItem().renderItem(tile.getStackInSlot(slotIndex), TransformType.FIXED);
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

		// top only needed ;)
		addVertexWithUV(buffer, xMax, height, zMax, uMax, vMin);
		addVertexWithUV(buffer, xMax, height, zMin, uMin, vMin);
		addVertexWithUV(buffer, xMin, height, zMin, uMin, vMax);
		addVertexWithUV(buffer, xMin, height, zMax, uMax, vMax);

	}

	private void addVertexWithUV(BufferBuilder buffer, float x, float y, float z, double u, double v) {
		buffer.pos(x / 2f, y, z / 2f).tex(u, v).endVertex();
	}

	private void addVertexWithColor(BufferBuilder buffer, float x, float y, float z, float red, float green, float blue, float alpha) {
		buffer.pos(x / 2f, y, z / 2f).color(red, green, blue, alpha).endVertex();
	}
}