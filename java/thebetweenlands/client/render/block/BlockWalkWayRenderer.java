package thebetweenlands.client.render.block;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
public class BlockWalkWayRenderer implements ISimpleBlockRenderingHandler {

    public static ModelWalkway modelWalkway = new ModelWalkway();

    public static ModelConverter modelConverterWalkway = null;

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    	if(modelConverterWalkway == null) {
    		modelConverterWalkway = new ModelConverter(
    	            modelWalkway,
    	            0.065D,
    	            new ModelConverter.TextureMap(128, 128, BLBlockRegistry.blockWalkWay.icon),
    	            true
    	    );
    	}
    	
    	Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		tessellator.startDrawingQuads();
		Tessellator.instance.addTranslation(0, 1.2F, 0);
    	modelConverterWalkway.renderWithTessellator(Tessellator.instance);
    	 Tessellator.instance.addTranslation(0, -1.2F, 0);
    	tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
    	if(modelConverterWalkway == null) {
    		modelConverterWalkway = new ModelConverter(
    	            modelWalkway,
    	            0.065D,
    	            new ModelConverter.TextureMap(128, 128, BLBlockRegistry.blockWalkWay.icon),
    	            true
    	    );
    	}
    	
    	Minecraft mc = Minecraft.getMinecraft();
		if(world != null && mc.thePlayer != null) {
			Tessellator.instance.setBrightness(world.getLightBrightnessForSkyBlocks(
					(int)(mc.thePlayer.posX), (int)(mc.thePlayer.posY), (int)(mc.thePlayer.posZ), 0));
		}
    	
    	Tessellator.instance.addTranslation(x+0.5F, y+1.5F, z+0.5F);
        modelConverterWalkway.renderWithTessellator(Tessellator.instance);
        Tessellator.instance.addTranslation(-x-0.5F, -y-1.5F, -z-0.5F);
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
