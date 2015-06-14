package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

@SideOnly(Side.CLIENT)
public class BlockSwampReedRenderer implements ISimpleBlockRenderingHandler {
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
		renderer.drawCrossedSquares(BLBlockRegistry.swampReedUW.iconSwampReed, -0.5, -0.5, -0.5, 1.0f);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
		renderer.drawCrossedSquares(BLBlockRegistry.swampReedUW.iconSwampReed, x, y, z, 1.0f);
		Block blockAbove = world.getBlock(x, y+1, z);
		if(blockAbove == BLBlockRegistry.swampWater || blockAbove == Blocks.air) {
			renderer.drawCrossedSquares(BLBlockRegistry.swampReedUW.iconSwampReedTop, x, y+1, z, 1.0f);
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BlockRenderIDs.SWAMP_REED.id();
	}
}