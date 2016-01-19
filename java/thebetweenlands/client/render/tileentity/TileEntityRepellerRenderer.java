package thebetweenlands.client.render.tileentity;

import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
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

		double xOff = Math.sin(meta / 2.0F * Math.PI) * 0.12F;
		double zOff = Math.cos(meta / 2.0F * Math.PI) * 0.12F;

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

		if(tileRepeller.hasShimmerstone()) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(x + 0.5F + xOff), (float)(y + 1.15F), (float)(z + 0.5F - zOff));
			GL11.glScaled(0.008F, 0.008F, 0.008F);
			float rot = ((float)Math.sin((tileRepeller.renderTicks + partialTicks) / 40.0F) + 1.0F) / 10.0F + 1.4F;
			this.renderShine(rot, 20, 
					1.0F, 0.8F, 0.0F, 0.0F,
					1.0F, 0.8F, 0.0F, 1.0F);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPopMatrix();
		}

		if(tileRepeller.isRunning()) {
			WorldRenderHandler.INSTANCE.repellerShields.add(new SimpleEntry(new Vector3d(x + 0.5F + xOff, y + 1.15F, z + 0.5F - zOff), tileRepeller.getRadius(partialTicks)));
		}
	}

	private void renderShine(float rotation, int iterations, float or, float og, float ob, float oa, float ir, float ig, float ib, float ia) {
		Random random = new Random(432L);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glPushMatrix();
		float f1 = rotation;
		float f2 = 0.0f;
		if( f1 > 0.8F ) {
			f2 = (f1 - 0.8F) / 0.2F;
		}
		Tessellator tessellator = Tessellator.instance;
		for( int i = 0; (float) i < iterations; ++i ) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			tessellator.setColorRGBA_F(ir, ig, ib, ia * f2);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA_F(or, og, ob, oa * f2);
			tessellator.addVertex(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.addVertex(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.addVertex(0.0D, (double) pos1, (double) (1.0F * pos2));
			tessellator.addVertex(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.draw();
		}
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		RenderHelper.enableStandardItemLighting();
	}
}
