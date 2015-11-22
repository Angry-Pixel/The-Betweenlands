package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelMossBed;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;

/**
 * Created by Bart on 22/11/2015.
 */
public class BlockMossBedRenderer implements ISimpleBlockRenderingHandler {
    public static ModelMossBed modelMossBed = new ModelMossBed();

    public static ModelConverter modelConverterMossBed = null;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {

    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if (world == null)
            return false;
        if (modelConverterMossBed == null) {
            modelConverterMossBed = new ModelConverter(
                    modelMossBed,
                    0.065D,
                    true
            );
        }

        Tessellator.instance.setColorRGBA_F(1, 1, 1, 1);

        Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));

        GL11.glRotatef(90f * world.getBlockMetadata(x, y, z), 0f, 1f, 0f);
        Tessellator.instance.addTranslation(x + 0.5F, y + 1.5F, z + 0.5F);
        modelConverterMossBed.renderWithTessellator(Tessellator.instance, 64, 64, BLBlockRegistry.mossBed.bedIcon);
        Tessellator.instance.addTranslation(-x - 0.5F, -y - 1.5F, -z - 0.5F);
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.MOSS_BED.id();
    }
}
