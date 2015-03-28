package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.IWaterRenderer;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockSwampWaterRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,
			RenderBlocks renderer) {
		IWaterRenderer specialRenderer = ((BlockSwampWater)block).getSpecialRenderer();
		if(specialRenderer != null) {
			specialRenderer.renderInventoryBlock(block, metadata, modelID, renderer);
		}
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess blockAccess, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		int l = block.colorMultiplier(blockAccess, x, y, z);
		float f = (float)(l >> 16 & 255) / 255.0F;
		float f1 = (float)(l >> 8 & 255) / 255.0F;
		float f2 = (float)(l & 255) / 255.0F;
		boolean flag = block.shouldSideBeRendered(blockAccess, x, y + 1, z, 1);
		boolean flag1 = block.shouldSideBeRendered(blockAccess, x, y - 1, z, 0);
		boolean[] aboolean = new boolean[] {block.shouldSideBeRendered(blockAccess, x, y, z - 1, 2), block.shouldSideBeRendered(blockAccess, x, y, z + 1, 3), block.shouldSideBeRendered(blockAccess, x - 1, y, z, 4), block.shouldSideBeRendered(blockAccess, x + 1, y, z, 5)};

		IWaterRenderer specialRenderer = ((BlockSwampWater)block).getSpecialRenderer();
		if(specialRenderer != null) {
			specialRenderer.renderWorldBlock(blockAccess, x, y, z, block, modelId, renderer);
		}

		if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
		{
			return false;
		}
		else
		{
			boolean flag2 = false;
			float f3 = 0.5F;
			float f4 = 1.0F;
			float f5 = 0.8F;
			float f6 = 0.6F;
			double d0 = 0.0D;
			double d1 = 1.0D;
			Material material = block.getMaterial();
			int i1 = blockAccess.getBlockMetadata(x, y, z);
			double d2 = (double)renderer.getLiquidHeight(x, y, z, material);
			double d3 = (double)renderer.getLiquidHeight(x, y, z + 1, material);
			double d4 = (double)renderer.getLiquidHeight(x + 1, y, z + 1, material);
			double d5 = (double)renderer.getLiquidHeight(x + 1, y, z, material);
			double d6 = 0.0010000000474974513D;
			float f9;
			float f10;
			float f11;

			if (renderer.renderAllFaces || flag)
			{
				flag2 = true;
				IIcon iicon = renderer.getBlockIconFromSideAndMetadata(block, 1, i1);
				float f7 = (float)BlockLiquid.getFlowDirection(blockAccess, x, y, z, material);

				if (f7 > -999.0F)
				{
					iicon = renderer.getBlockIconFromSideAndMetadata(block, 2, i1);
				}

				d2 -= d6;
				d3 -= d6;
				d4 -= d6;
				d5 -= d6;
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
				tessellator.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + d2, (double)(z + 0), d7, d14);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + d3, (double)(z + 1), d8, d16);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)(z + 1), d10, d18);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + d5, (double)(z + 0), d12, d20);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + d2, (double)(z + 0), d7, d14);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + d5, (double)(z + 0), d12, d20);
				tessellator.addVertexWithUV((double)(x + 1), (double)y + d4, (double)(z + 1), d10, d18);
				tessellator.addVertexWithUV((double)(x + 0), (double)y + d3, (double)(z + 1), d8, d16);
			}

			if (renderer.renderAllFaces || flag1)
			{
				tessellator.setBrightness(block.getMixedBrightnessForBlock(blockAccess, x, y - 1, z));
				tessellator.setColorOpaque_F(f3, f3, f3);
				if(blockAccess.getBlock(x, y-1, z) != block) {
					tessellator.setColorOpaque_F(f4 * f, f4 * f1, f4 * f2);
					renderer.renderFaceYNeg(block, (double)x, (double)y + d6, (double)z, renderer.getBlockIconFromSide(block, 1));
				}
				flag2 = true;
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

				IIcon iicon1 = renderer.getBlockIconFromSideAndMetadata(block, k1 + 2, i1);

				if (renderer.renderAllFaces || aboolean[k1])
				{
					double d9;
					double d11;
					double d13;
					double d15;
					double d17;
					double d19;

					if (k1 == 0)
					{
						d9 = d2;
						d11 = d5;
						d13 = (double)x;
						d17 = (double)(x + 1);
						d15 = (double)z + d6;
						d19 = (double)z + d6;
					}
					else if (k1 == 1)
					{
						d9 = d4;
						d11 = d3;
						d13 = (double)(x + 1);
						d17 = (double)x;
						d15 = (double)(z + 1) - d6;
						d19 = (double)(z + 1) - d6;
					}
					else if (k1 == 2)
					{
						d9 = d3;
						d11 = d2;
						d13 = (double)x + d6;
						d17 = (double)x + d6;
						d15 = (double)(z + 1);
						d19 = (double)z;
					}
					else
					{
						d9 = d5;
						d11 = d4;
						d13 = (double)(x + 1) - d6;
						d17 = (double)(x + 1) - d6;
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
					tessellator.setColorOpaque_F(f4 * f13 * f, f4 * f13 * f1, f4 * f13 * f2);
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

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.SWAMP_WATER.id();
	}
}
