package thebetweenlands.client.render.block.water;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.plants.roots.RootData;
import thebetweenlands.blocks.stalactite.StalactiteHelper;

public class WaterRootRenderer implements IWaterRenderer {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		tessellator.startDrawingQuads();
		renderBlock(block, 0, 0, 0, true, 1, true, 0, mc.theWorld.getLightBrightnessForSkyBlocks(
				(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator.instance.setColorOpaque(255, 255, 255);
		renderBlock(block, x, y, z, world);
		return true;
	}

	public static boolean renderBlock(Block block, int x, int y, int z, IBlockAccess blockAccess)
	{
		RootData info = RootData.getData(blockAccess, x, y, z);
		return renderBlock(block, info.posX, info.posY, info.posZ, info.noBottom, info.distDown, info.noTop, info.distUp, block.getMixedBrightnessForBlock(blockAccess, x, y, z));
	}

	public static boolean renderBlock(Block block, int _x, int _y, int _z, boolean noBottom, int distDown, boolean noTop, int distUp, int brightness)
	{
		int totalHeight = 1 + distDown + distUp;
		float distToMidBottom, distToMidTop;

		double squareAmount = 1.2D;
		double halfTotalHeightSQ;

		if(noTop)
		{
			halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
			distToMidBottom = Math.abs(distUp + 1);
			distToMidTop = Math.abs(distUp);
		}
		else if(noBottom)
		{
			halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
			distToMidBottom = Math.abs(distDown);
			distToMidTop = Math.abs(distDown + 1);
		}
		else
		{
			float halfTotalHeight = totalHeight * 0.5F;
			halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
			distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
			distToMidTop = Math.abs(halfTotalHeight - distUp);
		}

		int minValBottom = (noBottom && distDown == 0) ? 0 : 1;
		int minValTop = (noTop && distUp == 0) ? 0 : 1;
		int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
		int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

		IIcon icon = BLBlockRegistry.root.getIcon(0, 0);
		float u0 = icon.getMinU();
		float u1 = icon.getMaxU();
		float v0 = icon.getMinV();
		float v1 = icon.getMaxV();

		double halfSize, halfSizeTexW;
		double halfSize1, halfSizeTex1;
		halfSize = (double) scaledValBottom / 16;
		halfSizeTexW = halfSize * (u1 - u0);
		halfSize1 = (double) (scaledValTop) / 16;
		halfSizeTex1 = halfSize1 * (u1 - u0);

		StalactiteHelper core = StalactiteHelper.getValsFor(_x, _y, _z);

		Tessellator t = Tessellator.instance;
		t.setBrightness(brightness);
		float f = 0.9F;
		t.setColorOpaque_F(f, f, f);

		// front
		t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0, v1);
		t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0, v0);
		t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
		// back
		t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0, v1);
		t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
		t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
		// left
		t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0, v1);
		t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
		t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
		// right
		t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0, v1);
		t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0, v0);
		t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);

		icon = block.getIcon(2, 0);
		u0 = icon.getMinU();
		v0 = icon.getMinV();

		// top
		if(distUp == 0)
		{
			t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0);
			t.addVertexWithUV(_x + core.tX - halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
			t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0 + halfSizeTex1 * 2);
			t.addVertexWithUV(_x + core.tX + halfSize1, _y + 1, _z + core.tZ - halfSize1, u0, v0 + halfSizeTex1 * 2);
		}

		// bottom
		if(distDown == 0)
		{
			t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0);
			t.addVertexWithUV(_x + core.bX - halfSize, _y, _z + core.bZ - halfSize, u0, v0);
			t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ - halfSize, u0, v0 + halfSizeTexW * 2);
			t.addVertexWithUV(_x + core.bX + halfSize, _y, _z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0 + halfSizeTexW * 2);
		}

		return true;
	}

	@Override
	public IIcon getIcon() {
		return BLBlockRegistry.root.getIcon(0, 0);
	}
}
