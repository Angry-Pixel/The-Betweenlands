package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelSpikeBlock;
import thebetweenlands.tileentities.TileEntitySpikeTrap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySpikeTrapRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spikeBlock.png");

	private final ModelSpikeBlock model = new ModelSpikeBlock();

	@Override
	public final void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntitySpikeTrap spikes = (TileEntitySpikeTrap) tile;
		int meta = tile.getBlockMetadata();
		int brightness = 0;

		switch(meta) {
		case 2:
			brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord - 1, 0);
			break;
		case 3:
			brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord + 1, 0);
			break;
		case 4:
			brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord - 1, tile.yCoord, tile.zCoord, 0);
			break;
		case 5:
			brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord + 1, tile.yCoord, tile.zCoord, 0);
			break;
		}

		int lightmapX = brightness % 65536;
		int lightmapY = brightness / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX / 1.0F, (float)lightmapY / 1.0F);

		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScaled(-1, -1, 1);

		model.render(spikes);
		GL11.glPopMatrix();
	}
}