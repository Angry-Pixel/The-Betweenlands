package thebetweenlands.client.render.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.model.block.ModelMossBed;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.ModelConverter;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

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
        if (modelConverterMossBed == null)
            modelConverterMossBed = new ModelConverter(modelMossBed, 0.065D, true );

        int meta = world.getBlockMetadata(x, y, z);
        if (meta <= 3) {
			Tessellator.instance.setColorRGBA_F(1, 1, 1, 1);
			Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(x, y, z, 0));
			Tessellator.instance.addTranslation(x + 0.5F, y + 1.5F, z + 0.5F);
			ModelConverter.Model model = modelConverterMossBed.getModel().rotate(meta == 1 || meta == 3 ? 90F * meta : meta == 2 ? 180F * meta : - 180F, 0f, 1f, 0f, new ModelConverter.Vec3(0, 0, 0));
			for (ModelConverter.Box box : model.getBoxes()) {
				for (ModelConverter.Quad quad : box.getQuads()) {
					for (int i = 0; i < 4; i++) {
						ModelConverter.Vec3 vec = quad.getVertices()[i];
						Tessellator.instance.addVertexWithUV(vec.x, vec.y, vec.z, vec.getU(BLBlockRegistry.mossBed.bedIcon, 128), vec.getV(BLBlockRegistry.mossBed.bedIcon, 128));
					}
				}
			}
			Tessellator.instance.addTranslation(-x - 0.5F, -y - 1.5F, -z - 0.5F);
        }
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
