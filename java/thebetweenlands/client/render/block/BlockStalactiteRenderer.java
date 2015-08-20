package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.stalactite.BlockStalactite;
import thebetweenlands.proxy.ClientProxy;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BlockStalactiteRenderer implements ISimpleBlockRenderingHandler {
	public void renderInventoryBlock(Block block, int metadata, int renderId, RenderBlocks renderer) {
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		BlockStalactite.renderBlock(block, 0, 0, 0, false, 0, true, 0, 0xF00000);
		tessellator.draw();
		GL11.glPopMatrix();
	}

	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int renderId, RenderBlocks renderer) {
		return BlockStalactite.renderBlock(block, x, y, z, world);
	}

	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	public int getRenderId() {
		return ClientProxy.BlockRenderIDs.STALACTITE.id();
	}
}
