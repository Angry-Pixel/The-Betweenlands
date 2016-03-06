package thebetweenlands.client.render.tileentity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.block.ModelItemCage;
import thebetweenlands.tileentities.TileEntityItemCage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityItemCageRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/itemCagePower.png");
	private static final ResourceLocation CAGE_TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/itemCage.png");
	private final ModelItemCage model = new ModelItemCage();

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityItemCage swordStone = (TileEntityItemCage) tile;
		int type = swordStone.type;
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);

		int brightness = 0;
		brightness = tile.getWorldObj().getLightBrightnessForSkyBlocks(tile.xCoord, tile.yCoord, tile.zCoord, 0);
		int lightmapX = brightness % 65536;
		int lightmapY = brightness / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX / 1.0F, (float)lightmapY / 1.0F);

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScaled(1, -1, -1);
		float f1 = ticks;
		bindTexture(FORCE_TEXTURE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float f2 = f1 * 0.0015F;
		float f3 = f1 * 0.0015F;
		GL11.glTranslatef(f2, f3, f2);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		float f4 = 0.5F;
		GL11.glColor4f(f4, f4, f4, 1.0F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		model.renderBars();
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScaled(1, -1, -1);
		bindTexture(CAGE_TEXTURE);
		model.renderSolid();
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
