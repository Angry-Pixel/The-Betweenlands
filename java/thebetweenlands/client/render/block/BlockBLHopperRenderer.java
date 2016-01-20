package thebetweenlands.client.render.block;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BlockBLHopper;
import thebetweenlands.proxy.ClientProxy;

/**
 * Created by Bart on 20/01/2016.
 */
public class BlockBLHopperRenderer implements ISimpleBlockRenderingHandler {
    //Might needs to me changed

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        float f1;
        double d0 = 0.625D;

        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
        tessellator.draw();

        IIcon iicon = BlockBLHopper.getHopperIcon("hopper_outside");
        IIcon iicon1 = BlockBLHopper.getHopperIcon("hopper_inside");
        f1 = 0.125F;

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, (double)(-1.0F + f1), 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, (double)(1.0F - f1), 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, (double)(-1.0F + f1), iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, (double)(1.0F - f1), iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, -1.0D + d0, 0.0D, iicon1);
        tessellator.draw();

        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, iicon);
        tessellator.draw();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        int i1 = BlockBLHopper.getDirectionFromMetadata(world.getBlockMetadata(x, y, z));
        double d0 = 0.625D;
        renderer.setRenderBounds(0.0D, d0, 0.0D, 1.0D, 1.0D, 1.0D);

        renderer.renderStandardBlock(block, x, y, z);

        float f1;


        tessellator.setBrightness(block.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        int j1 = block.colorMultiplier(renderer.blockAccess, x, y, z);
        float f = (float) (j1 >> 16 & 255) / 255.0F;
        f1 = (float) (j1 >> 8 & 255) / 255.0F;
        float f2 = (float) (j1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable) {
            float f3 = (f * 30.0F + f1 * 59.0F + f2 * 11.0F) / 100.0F;
            float f4 = (f * 30.0F + f1 * 70.0F) / 100.0F;
            float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
            f = f3;
            f1 = f4;
            f2 = f5;
        }

        tessellator.setColorOpaque_F(f, f1, f2);


        IIcon iicon = BlockBLHopper.getHopperIcon("hopper_outside");
        IIcon iicon1 = BlockBLHopper.getHopperIcon("hopper_inside");
        f1 = 0.125F;


        renderer.renderFaceXPos(block, (double) ((float) x - 1.0F + f1), (double) y, (double) z, iicon);
        renderer.renderFaceXNeg(block, (double) ((float) x + 1.0F - f1), (double) y, (double) z, iicon);
        renderer.renderFaceZPos(block, (double) x, (double) y, (double) ((float) z - 1.0F + f1), iicon);
        renderer.renderFaceZNeg(block, (double) x, (double) y, (double) ((float) z + 1.0F - f1), iicon);
        renderer.renderFaceYPos(block, (double) x, (double) ((float) y - 1.0F) + d0, (double) z, iicon1);

        renderer.setOverrideBlockTexture(iicon);
        double d3 = 0.25D;
        double d4 = 0.25D;
        renderer.setRenderBounds(d3, d4, d3, 1.0D - d3, d0 - 0.002D, 1.0D - d3);


        renderer.renderStandardBlock(block, x, y, z);


        double d1 = 0.375D;
        double d2 = 0.25D;
        renderer.setOverrideBlockTexture(iicon);

        if (i1 == 0) {
            renderer.setRenderBounds(d1, 0.0D, d1, 1.0D - d1, 0.25D, 1.0D - d1);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (i1 == 2) {
            renderer.setRenderBounds(d1, d4, 0.0D, 1.0D - d1, d4 + d2, d3);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (i1 == 3) {
            renderer.setRenderBounds(d1, d4, 1.0D - d3, 1.0D - d1, d4 + d2, 1.0D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (i1 == 4) {
            renderer.setRenderBounds(0.0D, d4, d1, d3, d4 + d2, 1.0D - d1);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (i1 == 5) {
            renderer.setRenderBounds(1.0D - d3, d4, d1, 1.0D, d4 + d2, 1.0D - d1);
            renderer.renderStandardBlock(block, x, y, z);
        }

        renderer.clearOverrideBlockTexture();
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    @Override
    public int getRenderId() {
        return ClientProxy.BlockRenderIDs.HOPPER.id();
    }
}
