package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.stalactite.BlockStalactite;
import thebetweenlands.proxy.ClientProxy;

@SideOnly(Side.CLIENT)
public class BlockStalactiteRenderer implements ISimpleBlockRenderingHandler
{
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
    {

    }

    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        return BlockStalactite.renderBlock(block, x, y, z, world);
    }

    public boolean shouldRender3DInInventory(int modelId)
    {
        return false;
    }

    public int getRenderId()
    {
        return ClientProxy.BlockRenderIDs.STALACTITE.id();
    }
}
