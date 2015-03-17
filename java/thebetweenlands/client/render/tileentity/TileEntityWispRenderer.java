package thebetweenlands.client.render.tileentity;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class TileEntityWispRenderer extends TileEntitySpecialRenderer {
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		ArrayList<Object> particleList = ((TileEntityWisp)tileEntity).particleList;
		
		GL11.glPushMatrix();
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		Minecraft.getMinecraft().getTextureManager().bindTexture(EntityWispFX.TEXTURE);
		for(Object particle : particleList){
			((EntityWispFX)particle).renderParticle(tessellator, partialTicks, 
					ActiveRenderInfo.rotationX,
					ActiveRenderInfo.rotationXZ,
					ActiveRenderInfo.rotationZ,
					ActiveRenderInfo.rotationYZ,
					ActiveRenderInfo.rotationXY);
		}
		tessellator.draw();

		GL11.glDepthMask(true);
		GL11.glPopMatrix();
		
		double dist = Minecraft.getMinecraft().renderViewEntity.getDistance(x + RenderManager.renderPosX, y + RenderManager.renderPosY, z + RenderManager.renderPosZ);
		if(dist > 50 || dist < 10) {
			return;
		}

		if(System.nanoTime() - ((TileEntityWisp)tileEntity).lastSpawn >= (500f - 500.0f * ConfigHandler.WISP_QUALITY / 100.0f) * 1000000L) {
			((TileEntityWisp)tileEntity).lastSpawn = System.nanoTime();

			int wispMeta = tileEntity.getBlockMetadata();
			int wispcolorOut = 0xFFFF0000;
			int wispcolorIn = 0xFF00FF00;
			switch(wispMeta) {
			case 1:
				//Pink/White
				wispcolorOut = 0xFF7F1659;
				wispcolorIn = 0xFFFFFFFF;
				break;
			case 2:
				//Blue/Pink
				wispcolorOut = 0xFF0707C8;
				wispcolorIn = 0xFFC8077B;
				break;
			case 3:
				//Green/Yellow/White
				wispcolorOut = 0xFF0E2E0B;
				wispcolorIn = 0xFFC8077B;
				break;
			case 4:
				//Red/Yellow/White
				wispcolorOut = 0xFF9A6908;
				wispcolorIn = 0xFF4F0303;
				break;
			}

			particleList.add(new EntityWispFX(
					tileEntity.getWorldObj(), 
					x + 0.5 + RenderManager.renderPosX, 
					y + 0.5F +RenderManager.renderPosY, 
					z + 0.5 + RenderManager.renderPosZ,
					0.0D, 0.0D, 0.0D,
					3.0F, 50, wispcolorOut));

			particleList.add(new EntityWispFX(
					tileEntity.getWorldObj(), 
					x + 0.5 + RenderManager.renderPosX, 
					y + 0.5F + RenderManager.renderPosY, 
					z + 0.5 + RenderManager.renderPosZ,
					0.0D, 0.0D, 0.0D,
					2.0F, 50, wispcolorIn));
		}
	}
}
