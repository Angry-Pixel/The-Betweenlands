package thebetweenlands.client.render.block.water;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public interface IWaterRenderer {
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer);
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer);
}