package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelWalkway;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;

/**
 * Created by Bart on 12-6-2015.
 */
public class RendererWalkWay implements ISimpleBlockRenderingHandler {

    public static ModelWalkway modelWalkway = new ModelWalkway();

    public static ModelConverter modelConverterWalkway = new ModelConverter(
            modelWalkway,
            0.065D,
            new ModelConverter.TextureMap(128, 64, BLBlockRegistry.blockWalkWay.icon),
            true
    );


    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        modelConverterWalkway.renderWithTessellator(Tessellator.instance);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.WALKWAY.id();
    }
}
