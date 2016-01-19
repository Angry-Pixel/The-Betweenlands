package thebetweenlands.client.render.tileentity;

import java.util.AbstractMap.SimpleEntry;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelRepeller;
import thebetweenlands.event.render.WorldRenderHandler;
import thebetweenlands.tileentities.TileEntityRepeller;

public class TileEntityRepellerRenderer extends TileEntitySpecialRenderer {

	private static final ModelRepeller MODEL = new ModelRepeller();

	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/repeller.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
		TileEntityRepeller tileRepeller = (TileEntityRepeller) tile;
		int meta = tile.getBlockMetadata();

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		GL11.glRotatef(meta * 90.0F + 180.0F, 0, 1, 0);
		GL11.glDisable(GL11.GL_CULL_FACE);

		this.bindTexture(TEXTURE);
		MODEL.render();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();

		if(tileRepeller.isRunning()) {
			double xOff = Math.sin(meta / 2.0F * Math.PI) * 0.12F;
			double zOff = Math.cos(meta / 2.0F * Math.PI) * 0.12F;
			WorldRenderHandler.INSTANCE.repellerShields.add(new SimpleEntry(new Vector3d(x + 0.5F + xOff, y + 1.15F, z + 0.5F - zOff), tileRepeller.getRadius(partialTicks)));
		}
	}
}
