package thebetweenlands.client.render.block;

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.IWaterRenderer;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockSwampWaterRenderer implements ISimpleBlockRenderingHandler {
	private Field f_renderItem1 = ReflectionHelper.findField(GuiIngame.class, "itemRenderer", "field_73841_b", "i");
	private Field f_renderBlocks1 = ReflectionHelper.findField(RenderItem.class, "renderBlocksRi", "field_147913_i", "i");
	private Field f_renderBlocks2 = ReflectionHelper.findField(Render.class, "field_147909_c", "c");

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		IWaterRenderer specialRenderer = ((BlockSwampWater)block).getSpecialRenderer();
		if(specialRenderer != null) {
			if(specialRenderer.getIcon() != null) {
				boolean isInInventory = false;
				try {
					//I'm sorry
					RenderItem renderItem1 = (RenderItem) f_renderItem1.get(Minecraft.getMinecraft().ingameGUI);
					RenderBlocks renderBlocks1 = (RenderBlocks) f_renderBlocks1.get(renderItem1);
					RenderBlocks renderBlocks2 = (RenderBlocks) f_renderBlocks2.get(renderItem1);
					isInInventory = renderBlocks1 == renderer || renderBlocks2 == renderer;
				} catch(Exception ex) {
					ex.printStackTrace();
				}
				if(isInInventory) {
					GL11.glPushMatrix();
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glRotatef(-45, 1, 0, -1);
					GL11.glRotatef(-45, 0, 1, 0);
					IIcon icon = specialRenderer.getIcon();
					double size = 0.82D;
					Tessellator tessellator = Tessellator.instance;
					tessellator.setColorOpaque(255, 255, 255);
					tessellator.startDrawingQuads();
					tessellator.addVertexWithUV(0, size, size, icon.getMinU(), icon.getMinV());
					tessellator.addVertexWithUV(0, -size, size, icon.getMinU(), icon.getMaxV());
					tessellator.addVertexWithUV(0, -size, -size, icon.getMaxU(), icon.getMaxV());
					tessellator.addVertexWithUV(0, size, -size, icon.getMaxU(), icon.getMinV());
					tessellator.draw();
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glPopMatrix();
				} else {
					specialRenderer.renderInventoryBlock(block, metadata, modelID, renderer);
				}
			} else {
				specialRenderer.renderInventoryBlock(block, metadata, modelID, renderer);
			}
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		BlockSwampWater swampWaterBlock = (BlockSwampWater) block;

		Tessellator tessellator = Tessellator.instance;
		int colorMultiplier = block.colorMultiplier(blockAccess, x, y, z);
		float colorR = (float)(colorMultiplier >> 16 & 255) / 255.0F;
		float colorG = (float)(colorMultiplier >> 8 & 255) / 255.0F;
		float colorB = (float)(colorMultiplier & 255) / 255.0F;
		boolean isBreakingBlock = renderer.overrideBlockTexture != null;
		boolean renderTop = block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1);
		boolean renderBottom = block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0);
		boolean[] renderSides = new boolean[] {block.shouldSideBeRendered(blockAccess, x, y, z - 1, 2), block.shouldSideBeRendered(blockAccess, x, y, z + 1, 3), block.shouldSideBeRendered(blockAccess, x - 1, y, z, 4), block.shouldSideBeRendered(blockAccess, x + 1, y, z, 5)};

		IWaterRenderer specialRenderer = ((BlockSwampWater)block).getSpecialRenderer();
		if(specialRenderer != null) {
			tessellator.setBrightness(blockAccess.getLightBrightnessForSkyBlocks(x, y, z, 0));
			specialRenderer.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderer);
		}

		if (isBreakingBlock || !renderTop && !renderBottom && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3])
		{
			return false;
		}
		else
		{
			boolean flag2 = false;
			float f3 = 0.5F;
			float colorMult = 1.0F;
			tessellator.setColorOpaque_F(colorMult * colorR, colorMult * colorG, colorMult * colorB);
			float f5 = 0.8F;
			float f6 = 0.6F;
			double d0 = 0.0D;
			double d1 = 1.0D;
			int i1 = blockAccess.getBlockMetadata(x, y, z);
			Material material = block.getMaterial();
			double wch = (double)getLiquidHeight(blockAccess, x, y, z, Material.water);
			double wzh = (double)getLiquidHeight(blockAccess, x, y, z + 1, Material.water);
			double wxzh = (double)getLiquidHeight(blockAccess, x + 1, y, z + 1, Material.water);
			double wxh = (double)getLiquidHeight(blockAccess, x + 1, y, z, Material.water);
			double minHeightSub = 0.0010000000474974513D;
			float f9;
			float f10;
			float f11;

			if (renderer.renderAllFaces || renderTop)
			{
				flag2 = true;
				IIcon iicon = /*renderer.getBlockIconFromSideAndMetadata(block, 1, i1)*/swampWaterBlock.getWaterIcon(1);
				float f7 = 0.0f;
				if((material == Material.water || material == Material.lava) && block instanceof BlockSwampWater == false) {
					f7 = (float)BlockLiquid.getFlowDirection(blockAccess, x, y, z, material);
				} else {
					f7 = (float)((BlockSwampWater)block).getFlowDirection(blockAccess, x, y, z);
				}

				if (f7 > -999.0F)
				{
					iicon = /*renderer.getBlockIconFromSideAndMetadata(block, 2, i1)*/swampWaterBlock.getWaterIcon(2);
				}

				wch -= minHeightSub;
				wzh -= minHeightSub;
				wxzh -= minHeightSub;
				wxh -= minHeightSub;
				double d7;
				double d8;
				double d10;
				double d12;
				double d14;
				double d16;
				double d18;
				double d20;

				if (f7 < -999.0F)
				{
					d7 = (double)iicon.getInterpolatedU(0.0D);
					d14 = (double)iicon.getInterpolatedV(0.0D);
					d8 = d7;
					d16 = (double)iicon.getInterpolatedV(16.0D);
					d10 = (double)iicon.getInterpolatedU(16.0D);
					d18 = d16;
					d12 = d10;
					d20 = d14;
				}
				else
				{
					f9 = MathHelper.sin(f7) * 0.25F;
					f10 = MathHelper.cos(f7) * 0.25F;
					f11 = 8.0F;
					d7 = (double)iicon.getInterpolatedU((double)(8.0F + (-f10 - f9) * 16.0F));
					d14 = (double)iicon.getInterpolatedV((double)(8.0F + (-f10 + f9) * 16.0F));
					d8 = (double)iicon.getInterpolatedU((double)(8.0F + (-f10 + f9) * 16.0F));
					d16 = (double)iicon.getInterpolatedV((double)(8.0F + (f10 + f9) * 16.0F));
					d10 = (double)iicon.getInterpolatedU((double)(8.0F + (f10 + f9) * 16.0F));
					d18 = (double)iicon.getInterpolatedV((double)(8.0F + (f10 - f9) * 16.0F));
					d12 = (double)iicon.getInterpolatedU((double)(8.0F + (f10 - f9) * 16.0F));
					d20 = (double)iicon.getInterpolatedV((double)(8.0F + (-f10 - f9) * 16.0F));
				}

				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y, z));
				tessellator.setColorOpaque_F(colorMult * colorR, colorMult * colorG, colorMult * colorB);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + wch, (double)(z + 0), d7, d14);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + wzh, (double)(z + 1), d8, d16);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + wxzh, (double)(z + 1), d10, d18);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + wxh, (double)(z + 0), d12, d20);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + wch, (double)(z + 0), d7, d14);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + wxh, (double)(z + 0), d12, d20);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + wxzh, (double)(z + 1), d10, d18);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + wzh, (double)(z + 1), d8, d16);
			}

			if (renderer.renderAllFaces || renderBottom)
			{
				if(blockAccess.getBlock(x, y-1, z) instanceof BlockSwampWater == false) {
					tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z));
					tessellator.setColorOpaque_F(colorMult * colorR, colorMult * colorG, colorMult * colorB);
					renderer.renderFaceYNeg(block, (double)x, (double)y + minHeightSub, (double)z, /*renderer.getBlockIconFromSide(block, 0)*/swampWaterBlock.getWaterIcon(0));
					flag2 = true;
				}
			}

			for (int k1 = 0; k1 < 4; ++k1)
			{
				int l1 = x;
				int j1 = z;

				if (k1 == 0)
				{
					j1 = z - 1;
				}

				if (k1 == 1)
				{
					++j1;
				}

				if (k1 == 2)
				{
					l1 = x - 1;
				}

				if (k1 == 3)
				{
					++l1;
				}

				IIcon iicon1 = /*renderer.getBlockIconFromSideAndMetadata(block, k1 + 2, i1)*/swampWaterBlock.getWaterIcon(k1 + 2);

				if (renderer.renderAllFaces || renderSides[k1])
				{
					double d9;
					double d11;
					double d13;
					double d15;
					double d17;
					double d19;

					if (k1 == 0)
					{
						d9 = wch;
						d11 = wxh;
						d13 = (double)x;
						d17 = (double)(x + 1);
						d15 = (double)z + minHeightSub;
						d19 = (double)z + minHeightSub;
					}
					else if (k1 == 1)
					{
						d9 = wxzh;
						d11 = wzh;
						d13 = (double)(x + 1);
						d17 = (double)x;
						d15 = (double)(z + 1) - minHeightSub;
						d19 = (double)(z + 1) - minHeightSub;
					}
					else if (k1 == 2)
					{
						d9 = wzh;
						d11 = wch;
						d13 = (double)x + minHeightSub;
						d17 = (double)x + minHeightSub;
						d15 = (double)(z + 1);
						d19 = (double)z;
					}
					else
					{
						d9 = wxh;
						d11 = wxzh;
						d13 = (double)(x + 1) - minHeightSub;
						d17 = (double)(x + 1) - minHeightSub;
						d15 = (double)z;
						d19 = (double)(z + 1);
					}

					flag2 = true;
					float f8 = iicon1.getInterpolatedU(0.0D);
					f9 = iicon1.getInterpolatedU(8.0D);
					f10 = iicon1.getInterpolatedV((1.0D - d9) * 16.0D * 0.5D);
					f11 = iicon1.getInterpolatedV((1.0D - d11) * 16.0D * 0.5D);
					float f12 = iicon1.getInterpolatedV(8.0D);
					tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, l1, y, j1));
					float f13 = 1.0F;
					f13 *= k1 < 2 ? f5 : f6;
					tessellator.setColorOpaque_F(colorMult * f13 * colorR, colorMult * f13 * colorG, colorMult * f13 * colorB);
					tessellator.addVertexWithUV(d13, (double)y + d9, d15, (double)f8, (double)f10);
					tessellator.addVertexWithUV(d17, (double)y + d11, d19, (double)f9, (double)f11);
					tessellator.addVertexWithUV(d17, (double)(y + 0), d19, (double)f9, (double)f12);
					tessellator.addVertexWithUV(d13, (double)(y + 0), d15, (double)f8, (double)f12);
					tessellator.addVertexWithUV(d13, (double)(y + 0), d15, (double)f8, (double)f12);
					tessellator.addVertexWithUV(d17, (double)(y + 0), d19, (double)f9, (double)f12);
					tessellator.addVertexWithUV(d17, (double)y + d11, d19, (double)f9, (double)f11);
					tessellator.addVertexWithUV(d13, (double)y + d9, d15, (double)f8, (double)f10);
				}
			}

			renderer.renderMinY = d0;
			renderer.renderMaxY = d1;
			return flag2;
		}
	}

	public float getLiquidHeight(IBlockAccess blockAccess, int x, int y, int z, Material liquidMaterial) {
		int l = 0;
		float f = 0.0F;

		for (int sb = 0; sb < 4; ++sb)
		{
			int xp = x - (sb & 1);
			int zp = z - (sb >> 1 & 1);

			if (blockAccess.getBlock(xp, y + 1, zp).getMaterial() == liquidMaterial)
			{
				return 1.0F;
			}

			if(blockAccess.getBlock(xp, y, zp) instanceof BlockSwampWater) {
				f += 1.1 - ((BlockSwampWater)blockAccess.getBlock(xp, y, zp)).getQuantaPercentage(blockAccess, xp, y, zp);
				++l;
			} else {
				Material material = blockAccess.getBlock(xp, y, zp).getMaterial();

				if (material == liquidMaterial)
				{

					int l1 = blockAccess.getBlockMetadata(xp, y, zp);

					if (l1 >= 8 || l1 == 0)
					{
						f += BlockLiquid.getLiquidHeightPercent(l1) * 10.0F;
						l += 10;
					}

					f += BlockLiquid.getLiquidHeightPercent(l1);
					++l;
				}
				else if (!material.isSolid())
				{
					++f;
					++l;
				}
			}
		}

		return 1.0F - f / (float)l;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.SWAMP_WATER.id();
	}
}
