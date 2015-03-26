package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockRubberLog;
import thebetweenlands.blocks.BlockWeedWoodBush;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockBushRenderer implements ISimpleBlockRenderingHandler {
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.setColorOpaque_F(1, 1, 1);
		IIcon topIcon = ((BlockRubberLog)block).getTopIcon(0);
		IIcon sideIcon = ((BlockRubberLog)block).getSideIcon(0);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		float mini = 0F, minj = 0F, mink = 0F, maxi = 0F, maxj = 0F, maxk = 0F;

		if (world.getBlock(x - 1, y, z) instanceof BlockWeedWoodBush)
			mini = -0.25F;
		if (world.getBlock(x + 1, y, z) instanceof BlockWeedWoodBush)
			maxi = 0.25F;
		if (world.getBlock(x, y - 1, z) instanceof BlockWeedWoodBush)
			minj = -0.25F;
		if (world.getBlock(x, y + 1, z) instanceof BlockWeedWoodBush)
			maxj = 0.25F;
		if (world.getBlock(x, y, z - 1) instanceof BlockWeedWoodBush)
			mink = -0.25F;
		if (world.getBlock(x, y, z + 1) instanceof BlockWeedWoodBush)
			maxk = 0.25F;
		
		IIcon icon = ((BlockWeedWoodBush)block).iconFast;
		if (Minecraft.isFancyGraphicsEnabled()) icon = ((BlockWeedWoodBush)block).iconFancy;
		double umin = icon.getMinU();
		double vmin = icon.getMinV();
		double umax = icon.getMaxU();
		double vmax = icon.getMaxV();
		
		Tessellator tessellator = Tessellator.instance;
		
		tessellator.setColorOpaque_F(1, 1, 1);
		tessellator.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		
		tessellator.addTranslation(x, y, z);
		
		// Right Side
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.25 + mink, umax, vmin);

		// Right-Top Side
		tessellator.addVertexWithUV(0, 0.75, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.25, 1, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.25, 1, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0, 0.75, 0.25 + mink, umax, vmin);

		// Right-Bottom Side
		tessellator.addVertexWithUV(0.25, 0.0, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.0, 0.25, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25 + mink, umax, vmin);

		// Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25 + mink, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25 + mink, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75 + maxk, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75 + maxk, umax, vmin);

		// Left-Top Side
		tessellator.addVertexWithUV(0.75, 1, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.75, 1, 0.25 + mink, umax, vmin);

		// Left-Bottom Side
		tessellator.addVertexWithUV(1, 0.25, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.25 + mink, umax, vmin);

		// Front Side
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 0, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 0, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 0, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 0, umax, vmin);

		// Front-Right Side
		tessellator.addVertexWithUV(0.0, 0.25 + minj, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.75 + maxj, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 0, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 0, umax, vmin);

		// Front-Left Side
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 0.0, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 0.0, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25, umax, vmin);

		// Front-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 0, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 0, umax, vmin);

		// Front-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 0.0, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25, umax, vmin);

		// Back Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 1, umin, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 1, umax, vmin);

		// Back-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 1, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75, umax, vmin);

		// Back-Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75, umin, vmin);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 1, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 1, umax, vmin);

		// Back-Right Side
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 1, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 1, umin, vmax);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75, umax, vmin);

		// Back-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 0, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 1, umax, vmin);

		// Top Side
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25 + mink, umax, vmin);

		// Bottom Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.75 + maxk, umin, vmin);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.75 + maxk, umin, vmax);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25 + mink, umax, vmax);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25 + mink, umax, vmin);

		// Corners
		tessellator.addVertexWithUV(1, 0.25, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, umin, vmax);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, umax, vmin);

		tessellator.addVertexWithUV(0.75, 0.25, 1, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75, umin, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.75, umax, vmax);
		tessellator.addVertexWithUV(1, 0.25, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.75, 1, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.75, 0.75, 1, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.75, umax, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.75, 0.75, 0, umin, vmin);
		tessellator.addVertexWithUV(0.75, 1, 0.25, umin, vmax);
		tessellator.addVertexWithUV(1, 0.75, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.75, 0.75, 0, umax, vmin);

		tessellator.addVertexWithUV(0.0, 0.25, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.25, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, umax, vmin);

		tessellator.addVertexWithUV(0.25, 1, 0.75, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.75, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.75, 1, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.75, 1, umax, vmin);

		tessellator.addVertexWithUV(0.25, 0.25, 1, umin, vmin);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75, umin, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, umax, vmax);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, umax, vmin);

		tessellator.addVertexWithUV(0.25, 1, 0.25, umin, vmin);
		tessellator.addVertexWithUV(0.25, 0.75, 0.0, umin, vmax);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, umax, vmax);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, umax, vmin);

		if (Minecraft.isFancyGraphicsEnabled()) {
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, 1.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, 1.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.1, 0.5, 0.7, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, 0.3, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, 0.3, umin, vmin);
			tessellator.addVertexWithUV(0.3, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.1, 0.5, 0.7, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.3, 0.5, 1.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.9, 0.5, -0.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);

			tessellator.addVertexWithUV(0.9, 0.5, -0.1, umin, vmin);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, umax, vmin);
			tessellator.addVertexWithUV(0.3, 0.5, 1.1, umax, vmax);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, umin, vmax);
		}
		
		tessellator.addTranslation(-x, -y, -z);
		
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.BUSH.id();
	}
}