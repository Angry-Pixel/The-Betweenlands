package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BlockBLFlowerPot;
import thebetweenlands.client.model.block.ModelMudFlowerPot;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;

/**
 * Created by Bart on 21-6-2015.
 */
public class BlockMudFlowerPotRenderer implements ISimpleBlockRenderingHandler {
    public static ModelMudFlowerPot modelFlowerPot = new ModelMudFlowerPot();

    public static ModelConverter modelConverterFlowerPot = null;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        if(world == null)
            return false;
        if(modelConverterFlowerPot == null) {
            modelConverterFlowerPot = new ModelConverter(
                    modelFlowerPot,
                    0.065D,
                    new ModelConverter.TextureMap(64, 64, BLBlockRegistry.mudFlowerPot.icon),
                    true
            );
        }

        Tessellator.instance.setColorRGBA_F(1, 1, 1, 1);

        Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));

        Tessellator.instance.addTranslation(x + 0.5F, y + 1.5F, z + 0.5F);
        modelConverterFlowerPot.renderWithTessellator(Tessellator.instance);
        Tessellator.instance.addTranslation(-x - 0.5F, -y - 1.5F, -z - 0.5F);

        Tessellator tessellator = Tessellator.instance;
        TileEntity tileentity = world.getTileEntity(x, y, z);

        if (tileentity != null && tileentity instanceof TileEntityFlowerPot) {
            Item item = ((TileEntityFlowerPot) tileentity).getFlowerPotItem();
            int i1 = ((TileEntityFlowerPot) tileentity).getFlowerPotData();

            if (item instanceof ItemBlock) {
                int l;

                Block flower = Block.getBlockFromItem(item);
                int j1 = flower.getRenderType();
                float f6 = 0.0F;
                float f7 = 4.0F;
                float f8 = 0.0F;
                tessellator.addTranslation(f6 / 16.0F, f7 / 16.0F, f8 / 16.0F);
                l = flower.colorMultiplier(world, x, y, z);

                if (l != 16777215) {
                    float f = (float) (l >> 16 & 255) / 255.0F;
                    float f1 = (float) (l >> 8 & 255) / 255.0F;
                    float f2 = (float) (l & 255) / 255.0F;
                    tessellator.setColorOpaque_F(f, f1, f2);
                }

                if (j1 == 1) {
                    renderer.drawCrossedSquares(renderer.getBlockIconFromSideAndMetadata(flower, 0, i1), (double) x, (double) y, (double) z, 0.75F);
                } else if (j1 == 13) {
                    renderer.renderAllFaces = true;
                    float f9 = 0.125F;
                    renderer.setRenderBounds((double) (0.5F - f9), 0.0D, (double) (0.5F - f9), (double) (0.5F + f9), 0.25D, (double) (0.5F + f9));
                    renderer.renderStandardBlock(flower, x, y, z);
                    renderer.setRenderBounds((double) (0.5F - f9), 0.25D, (double) (0.5F - f9), (double) (0.5F + f9), 0.5D, (double) (0.5F + f9));
                    renderer.renderStandardBlock(flower, x, y, z);
                    renderer.setRenderBounds((double) (0.5F - f9), 0.5D, (double) (0.5F - f9), (double) (0.5F + f9), 0.75D, (double) (0.5F + f9));
                    renderer.renderStandardBlock(flower, x, y, z);
                    renderer.renderAllFaces = false;
                    renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
                }

                tessellator.addTranslation(-f6 / 16.0F, -f7 / 16.0F, -f8 / 16.0F);
            }
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.MUDFLOWERPOT.id();
    }
}
