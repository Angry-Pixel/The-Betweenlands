package thebetweenlands.client.render.block.water;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;

public class SwampReedUWRenderer implements IWaterRenderer {
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
		renderer.drawCrossedSquares(BLBlockRegistry.swampReed.getIcons(1), -0.5, -0.5, -0.5, 1.0f);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		renderer.drawCrossedSquares(BLBlockRegistry.swampReed.getIcons(1), x, y, z, 1.0f);
		Block blockAbove = world.getBlock(x, y+1, z);
		if(blockAbove == BLBlockRegistry.swampWater || blockAbove == Blocks.air) {
			renderer.drawCrossedSquares(BLBlockRegistry.swampReed.getIcons(0), x, y+1, z, 1.0f);
		}
		return true;
	}

	@Override
	public IIcon getIcon() {
		return BLBlockRegistry.swampReed.getIcons(0) != null ? BLBlockRegistry.swampReed.getIcons(0) : BLBlockRegistry.swampReed.getIcons(1);
	}
}
