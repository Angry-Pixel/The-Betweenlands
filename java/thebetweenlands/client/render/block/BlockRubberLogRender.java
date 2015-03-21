package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockRubberLog;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockRubberLogRender implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorOpaque_F(1, 1, 1);
		IIcon topIcon = ((BlockRubberLog)block).getTopIcon(0);
		IIcon sideIcon = ((BlockRubberLog)block).getSideIcon(0);
		this.renderBox(topIcon, sideIcon, 0x1 | 0x2, tessellator, 
				0.32D, 0.0D, 0.32D, 
				0.68D, 1.0D, 0.68D);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		tessellator.setColorOpaque_F(1, 1, 1);
		IIcon topIcon = ((BlockRubberLog)block).getTopIcon(0);
		IIcon sideIcon = ((BlockRubberLog)block).getSideIcon(0);

		this.renderBox(topIcon, sideIcon, 0, tessellator, 
				x+0.32D, y+0.32D, z+0.32D,
				x+0.68D, y+0.68D, z+0.68D);
		
		//y+1
		if(this.isValidBlock(world.getBlock(x, y+1, z))) {
			this.renderBox(topIcon, sideIcon, 0x1, tessellator, 
					x+0.32D, y+0.68D, z+0.32D,
					x+0.68D, y+1.0D, z+0.68D);
		}
		//y-1
		if(this.isValidBlock(world.getBlock(x, y-1, z)) || 
				world.getBlock(x, y-1, z).isSideSolid(world, x, y-1, z, ForgeDirection.UP)) {
			this.renderBox(topIcon, sideIcon, 0x2, tessellator, 
					x+0.32D, y, z+0.32D,
					x+0.68D, y+0.32D, z+0.68D);
		}
		//x+1
		if(this.isValidBlock(world.getBlock(x+1, y, z))) {
			this.renderBoxSide(topIcon, sideIcon, 0x4, tessellator, 
					x+0.68D, y+0.32D, z+0.32D,
					x+1.0D, y+0.68D, z+0.68D);
		}
		//x-1
		if(this.isValidBlock(world.getBlock(x-1, y, z))) {
			this.renderBoxSide(topIcon, sideIcon, 0x8, tessellator, 
					x, y+0.32D, z+0.32D,
					x+0.32D, y+0.68D, z+0.68D);
		}
		//z+1
		if(this.isValidBlock(world.getBlock(x, y, z+1))) {
			this.renderBoxSide(topIcon, sideIcon, 0x10, tessellator, 
					x+0.32D, y+0.32D, z+0.68D,
					x+0.68D, y+0.68D, z+1.0D);
		}
		//z-1
		if(this.isValidBlock(world.getBlock(x, y, z-1))) {
			this.renderBoxSide(topIcon, sideIcon, 0x20, tessellator, 
					x+0.32D, y+0.32D, z,
					x+0.68D, y+0.68D, z+0.32D);
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

	public void renderBox(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2);

		//+x
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2,
				x2, y1, z1);

		//-x
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z1,
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1);

		//+z
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2);

		//-z
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x1, y1, z1,
				x1, y2, z1,
				x2, y2, z1);
	}

	public void renderBoxSide(IIcon topIcon, IIcon sideIcon, int topIconIndex, Tessellator tessellator,
			double x1, double y1, double z1,
			double x2, double y2, double z2) {
		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		//+y
		this.renderQuad((topIconIndex & 0x1) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y2, z1,
				x1, y2, z2,
				x2, y2, z2,
				x2, y2, z1);

		//-y
		this.renderQuad((topIconIndex & 0x2) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y1, z2,
				x1, y1, z2,
				x1, y1, z1);

		//+x
		this.renderQuad((topIconIndex & 0x4) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z1,
				x2, y2, z1,
				x2, y2, z2,
				x2, y1, z2);

		//-x
		this.renderQuad((topIconIndex & 0x8) == 0 ? sideIcon : topIcon, tessellator, 
				x1, y1, z2,
				x1, y2, z2,
				x1, y2, z1,
				x1, y1, z1);

		//+z
		this.renderQuad((topIconIndex & 0x10) == 0 ? sideIcon : topIcon, tessellator, 
				x2, y1, z2,
				x2, y2, z2,
				x1, y2, z2,
				x1, y1, z2);

		//-z
		this.renderQuad((topIconIndex & 0x20) == 0 ? sideIcon : topIcon, tessellator, 
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
		return false;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.RUBBER_LOG.id();
	}
}