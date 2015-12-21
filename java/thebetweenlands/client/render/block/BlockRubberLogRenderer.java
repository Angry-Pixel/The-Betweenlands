package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockRubberLog;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

@SideOnly(Side.CLIENT)
public class BlockRubberLogRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorOpaque_F(1, 1, 1);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		IIcon topIcon = ((BlockRubberLog)block).getTopIcon(0);
		IIcon sideIcon = ((BlockRubberLog)block).getSideIcon(0);
		tessellator.startDrawingQuads();
		this.renderBoxX(topIcon, sideIcon, 0x1 | 0x2, tessellator, 
				0.32D, 0.0D, 0.32D, 
				0.68D, 1.0D, 0.68D);
		tessellator.draw();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		tessellator.setColorOpaque_F(1, 1, 1);
		IIcon topIcon = ((BlockRubberLog)block).getTopIcon(0);
		IIcon sideIcon = ((BlockRubberLog)block).getSideIcon(0);

		boolean yp = this.isValidBlock(world.getBlock(x, y+1, z));
		boolean ym = this.isValidBlock(world.getBlock(x, y-1, z)) || 
				world.getBlock(x, y-1, z).isSideSolid(world, x, y-1, z, ForgeDirection.UP);
		boolean xp = this.isValidBlock(world.getBlock(x+1, y, z));
		boolean xm = this.isValidBlock(world.getBlock(x-1, y, z));
		boolean zp = this.isValidBlock(world.getBlock(x, y, z+1));
		boolean zm = this.isValidBlock(world.getBlock(x, y, z-1));

		int sideIndex = 0;
		int sideCount = 0;
		if(yp) ++sideCount;
		if(ym) ++sideCount;
		if(xp) ++sideCount;
		if(xm) ++sideCount;
		if(zp) ++sideCount;
		if(zm) ++sideCount;
		if(sideCount == 1) {
			if(yp) sideIndex |= 0x2;
			if(ym) sideIndex |= 0x1;
			if(xp) sideIndex |= 0x8;
			if(xm) sideIndex |= 0x4;
			if(zp) sideIndex |= 0x20;
			if(zm) sideIndex |= 0x10;
		}

		//center piece
		if(!yp && !ym) {
			if(xp || xm) {
				this.renderBoxSideX(topIcon, sideIcon, sideIndex, tessellator, 
						x+0.25D, y+0.25D, z+0.25D,
						x+0.75D, y+0.75D, z+0.75D);
			} else {
				this.renderBoxSideZ(topIcon, sideIcon, sideIndex, tessellator, 
						x+0.25D, y+0.25D, z+0.25D,
						x+0.75D, y+0.75D, z+0.75D);
			}
		} else {
			this.renderBoxX(topIcon, sideIcon, sideIndex, tessellator, 
					x+0.25D, y+0.25D, z+0.25D,
					x+0.75D, y+0.75D, z+0.75D);
		}

		//y+1
		if(yp) {
			this.renderBoxXS(topIcon, sideIcon, 0x1, tessellator, 
					x+0.25D, y+0.75D, z+0.25D,
					x+0.75D, y+1.0D, z+0.75D);
		}
		//y-1
		if(ym) {
			this.renderBoxXS(topIcon, sideIcon, 0x2, tessellator, 
					x+0.25D, y, z+0.25D,
					x+0.75D, y+0.25D, z+0.75D);
		}
		//x+1
		if(xp) {
			this.renderBoxSideXS(topIcon, sideIcon, 0x4, tessellator, 
					x+0.75D, y+0.25D, z+0.25D,
					x+1.0D, y+0.75D, z+0.75D);
		}
		//x-1
		if(xm) {
			this.renderBoxSideXS(topIcon, sideIcon, 0x8, tessellator, 
					x, y+0.25D, z+0.25D,
					x+0.25D, y+0.75D, z+0.75D);
		}
		//z+1
		if(zp) {
			this.renderBoxSideZS(topIcon, sideIcon, 0x10, tessellator, 
					x+0.25D, y+0.25D, z+0.75D,
					x+0.75D, y+0.75D, z+1.0D);
		}
		//z-1
		if(zm) {
			this.renderBoxSideZS(topIcon, sideIcon, 0x20, tessellator, 
					x+0.25D, y+0.25D, z,
					x+0.75D, y+0.75D, z+0.25D);
		}
		return true;
	}

	public void renderQuad(IIcon icon, Tessellator tessellator, 
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			double x3, double y3, double z3,
			double x4, double y4, double z4) {
		double umin = (double)icon.getMinU();
		double vmin = (double)icon.getMinV();
		double umax = (double)icon.getMaxU();
		double vmax = (double)icon.getMaxV();

		//meh... I'm probably too stupid to figure out the width of the actual texture, but this works too
		double du = umax - umin;
		double dv = vmax - vmin;
		umin += du / 16.0D * 4.0D;
		umax -= du / 16.0D * 4.0D;
		vmin += dv / 16.0D * 4.0D;
		vmax -= dv / 16.0D * 4.0D;

		tessellator.addVertexWithUV(x1, y1, z1, umin, vmin);
		tessellator.addVertexWithUV(x2, y2, z2, umax, vmin);
		tessellator.addVertexWithUV(x3, y3, z3, umax, vmax);
		tessellator.addVertexWithUV(x4, y4, z4, umin, vmax);
	}

	public void renderQuadS(IIcon icon, Tessellator tessellator, 
			double x1, double y1, double z1,
			double x2, double y2, double z2,
			double x3, double y3, double z3,
			double x4, double y4, double z4) {
		double umin = (double)icon.getMinU();
		double vmin = (double)icon.getMinV();
		double umax = (double)icon.getMaxU();
		double vmax = (double)icon.getMaxV();

		//meh... I'm probably too stupid to figure out the width of the actual texture, but this works too
		double du = umax - umin;
		double dv = vmax - vmin;
		umin += du / 16.0D * 4.0D;
		umax -= du / 16.0D * 4.0D;
		vmin += dv / 16.0D * 4.0D;
		vmax -= dv / 16.0D * 8.0D;

		tessellator.addVertexWithUV(x1, y1, z1, umin, vmin);
		tessellator.addVertexWithUV(x2, y2, z2, umax, vmin);
		tessellator.addVertexWithUV(x3, y3, z3, umax, vmax);
		tessellator.addVertexWithUV(x4, y4, z4, umin, vmax);
	}

	public void renderBoxX(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2,
				x2, y1, z1);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1);
	}

	public void renderBoxZ(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2,
				x2, y1, z1);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1);
	}

	public void renderBoxSideX(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2,
				x1, y1, z1);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				x1, y1, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				x1, y1, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1,
				x2, y1, z1);
	}

	public void renderBoxSideZ(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				x1, y1, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				x1, y1, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1,
				x2, y1, z1);
	}

	public void renderBoxXS(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuadS((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuadS((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuadS((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2,
				x2, y1, z1);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuadS((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuadS((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuadS((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1);
	}

	public void renderBoxZS(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuadS((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuadS((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuadS((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2,
				x2, y1, z1);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuadS((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuadS((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuadS((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1);
	}

	public void renderBoxSideXS(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(1, 0, 0);
		this.renderQuadS((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		tessellator.setNormal(-1, 0, 0);
		this.renderQuadS((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2,
				x1, y1, z1);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuadS((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuadS((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				x1, y1, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuadS((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				x1, y1, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuadS((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1,
				x2, y1, z1);
	}

	public void renderBoxSideZS(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		tessellator.setNormal(0, 1, 0);
		this.renderQuadS((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2);

		//-y
		tessellator.setNormal(0, -1, 0);
		this.renderQuadS((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		tessellator.setNormal(1, 0, 0);
		this.renderQuadS((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2);

		//-x
		tessellator.setNormal(-1, 0, 0);
		this.renderQuadS((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				x1, y1, z1);

		//+z
		tessellator.setNormal(0, 0, 1);
		this.renderQuadS((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				x1, y1, z2);

		//-z
		tessellator.setNormal(0, 0, -1);
		this.renderQuadS((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1,
				x2, y1, z1);
	}

	public boolean isValidBlock(Block block) {
		return block == BLBlockRegistry.rubberTreeLog || block == BLBlockRegistry.rubberTreeLeaves;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.RUBBER_LOG.id();
	}
}