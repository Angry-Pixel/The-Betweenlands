package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelWraithPusher;
import thebetweenlands.tileentities.TileEntityWraithPusher;

@SideOnly(Side.CLIENT)
public class TileEntityWraithPusherRenderer extends TileEntitySpecialRenderer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/wraithPusher.png");

	private final ModelWraithPusher model = new ModelWraithPusher();

	@Override
	public final void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityWraithPusher wraith = (TileEntityWraithPusher) tile;
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
		switch (meta) {
		case 2:
			GL11.glRotatef(0F, 0.0F, 1F, 0F);
			break;
		case 3:
			GL11.glRotatef(180F, 0.0F, 1F, 0F);
			break;
		case 4:
			GL11.glRotatef(-90F, 0.0F, 1F, 0F);
			break;
		case 5:
			GL11.glRotatef(90F, 0.0F, 1F, 0F);
			break;
		}

		model.render(wraith);
		GL11.glPopMatrix();
	}
}