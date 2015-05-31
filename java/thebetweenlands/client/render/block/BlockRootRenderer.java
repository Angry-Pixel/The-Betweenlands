package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.render.block.water.WaterRootRenderer;
import thebetweenlands.proxy.ClientProxy;

public class BlockRootRenderer implements ISimpleBlockRenderingHandler {
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    	Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		Tessellator.instance.setColorOpaque(255, 255, 255);
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.theWorld != null && mc.thePlayer != null) {
			tessellator.setBrightness(mc.theWorld.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
		tessellator.startDrawingQuads();
		WaterRootRenderer.renderBlock(block, 0, 0, 0, true, 1, true, 0, mc.theWorld.getLightBrightnessForSkyBlocks(
				(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    	Tessellator.instance.setColorOpaque(255, 255, 255);
		WaterRootRenderer.renderBlock(block, x, y, z, world);
		return true;
    }

    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.ROOT.id();
    }
}
