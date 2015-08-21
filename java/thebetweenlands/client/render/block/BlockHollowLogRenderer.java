package thebetweenlands.client.render.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.tree.BlockHollowLog;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 8-8-2015.
 */
public class BlockHollowLogRenderer implements ISimpleBlockRenderingHandler {

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
        float pixel = 0.0625F;
        renderer.renderAllFaces = true;
        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, pixel);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.setRenderBounds(0D, 0D, 0D, pixel, 1D, 1D);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.setRenderBounds(1D - pixel, 0D, 0D, 1D, 1D, 1D);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.setRenderBounds(0D, 0D, 1D - pixel, 1D, 1D, 1D);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.setRenderBounds(0D, 0D, 0D, 1D, pixel, 1D);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.setRenderBounds(0D, 1D - pixel, 0D, 1D, 1D, 1D);
        BlockRenderHelper.renderSimpleBlock(BLBlockRegistry.hollowLog, metadata, renderer);
        renderer.renderAllFaces = false;
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);

        float pixel = 0.0005F; // 0.0625F; <-- causes Z-fighting
        renderer.renderAllFaces = true;



        if (!(world.getBlock(x, y, z - 1) == block && world.getBlockMetadata(x, y, z - 1) <= 3 && meta <= 3)) {
            renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, meta > 3 ? pixel : 0D);
            renderer.renderStandardBlock(BLBlockRegistry.hollowLog, x, y, z);
        }

        if (!(world.getBlock(x - 1, y, z) == block && world.getBlockMetadata(x - 1, y, z) > 3 && meta > 3)) {
            renderer.setRenderBounds(0D, 0D, 0D, meta <= 3 ? pixel : 0D, 1D, 1D);
            renderer.renderStandardBlock(BLBlockRegistry.hollowLog, x, y, z);
        }

        if (!(world.getBlock(x + 1, y, z) == block && world.getBlockMetadata(x + 1, y, z) > 3 && meta > 3)) {
            renderer.setRenderBounds(1D - (meta <= 3 ? pixel : 0D), 0D, 0D, 1D, 1D, 1D);
            renderer.renderStandardBlock(BLBlockRegistry.hollowLog, x, y, z);
        }

        if (!(world.getBlock(x, y, z + 1) == block && world.getBlockMetadata(x, y, z + 1) <= 3 && meta <= 3)) {
            renderer.setRenderBounds(0D, 0D, 1D - (meta > 3 ? pixel : 0D), 1D, 1D, 1D);
            renderer.renderStandardBlock(BLBlockRegistry.hollowLog, x, y, z);
        }

        renderer.setRenderBounds(0D, 0D, 0D, 1D, 1D, 1D);
        if (meta <= 3) { 
        	renderer.uvRotateEast = renderer.uvRotateWest = 1;
        	renderer.uvRotateTop = renderer.uvRotateBottom = 2;
        }
        
        renderer.renderFaceYNeg(BLBlockRegistry.hollowLog, x, y+1, z, BLBlockRegistry.hollowLog.getIcon(1, world.getBlockMetadata(x, y, z)));
        renderer.renderFaceYPos(BLBlockRegistry.hollowLog, x, y-1, z, BLBlockRegistry.hollowLog.getIcon(0, world.getBlockMetadata(x, y, z)));
        
        if (meta <= 3) { 
            renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateTop = renderer.uvRotateBottom = 1;
        }
        
        renderer.renderFaceYNeg(BLBlockRegistry.hollowLog, x, y, z, BLBlockRegistry.hollowLog.getIcon(0, world.getBlockMetadata(x, y, z)));
        renderer.renderFaceYPos(BLBlockRegistry.hollowLog, x, y, z, BLBlockRegistry.hollowLog.getIcon(1, world.getBlockMetadata(x, y, z)));
        
        renderer.uvRotateEast = renderer.uvRotateWest = renderer.uvRotateTop = renderer.uvRotateBottom = 0;
        renderer.renderAllFaces = false;
        
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.HOLLOW_LOG.id();
    }


}
