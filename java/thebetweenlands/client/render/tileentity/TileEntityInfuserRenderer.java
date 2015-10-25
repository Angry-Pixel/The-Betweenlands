package thebetweenlands.client.render.tileentity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.model.block.ModelInfuser;
import thebetweenlands.tileentities.TileEntityInfuser;
import thebetweenlands.utils.ItemRenderHelper;

@SideOnly(Side.CLIENT)
public class TileEntityInfuserRenderer extends TileEntitySpecialRenderer {

	private final ModelInfuser model = new ModelInfuser();
	public static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/infuser.png");
	private RenderManager renderManager;
	private double viewRot;

	public TileEntityInfuserRenderer() {
		renderManager = RenderManager.instance;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
		TileEntityInfuser infuser = (TileEntityInfuser) tile;
		int meta = infuser.getBlockMetadata();
		viewRot = 180D + Math.toDegrees(Math.atan2(renderManager.viewerPosX - infuser.xCoord - 0.5D, renderManager.viewerPosZ - infuser.zCoord - 0.5D));
		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glScalef(1F, -1F, -1F);
		switch (meta) {
		case 2:
			GL11.glRotatef(180F, 0.0F, 1F, 0F);
			break;
		case 3:
			GL11.glRotatef(0F, 0.0F, 1F, 0F);
			break;
		case 4:
			GL11.glRotatef(90F, 0.0F, 1F, 0F);
			break;
		case 5:
			GL11.glRotatef(-90F, 0.0F, 1F, 0F);
			break;
		}
		model.render();
		GL11.glPushMatrix();
		GL11.glRotatef(infuser.getStirProgress() * 4, 0.0F, 1F, 0F);
		model.renderSpoon();
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		// TODO this here for debug please leave
		renderStirCount("Evap: " + infuser.getEvaporation() + " Temp: "+ infuser.getTemperature() + " Time: " + infuser.getInfusionTime(), x, y, z);

		int amount = infuser.waterTank.getFluidAmount();
		int capacity = infuser.waterTank.getCapacity();
		float size = 1F / capacity * amount;
		if (amount >= 100) {
			Tessellator tess = Tessellator.instance;
			IIcon waterIcon = ((BlockSwampWater)BLBlockRegistry.swampWater).getWaterIcon(1);
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(770, 771);
			bindTexture(TextureMap.locationBlocksTexture);
			float tx = (float) x + 0.0F;
			float ty = (float) (y + 0.35F + size * 0.5F);
			float tz = (float) z + 0.0F;
			tess.addTranslation(tx, ty, tz);
			tess.startDrawingQuads();
			if(!infuser.hasInfusion())
				tess.setColorRGBA_F(0.2F, 0.6F, 0.4F, 1.0F);
			else
				tess.setColorRGBA_F(0.5F, 0.0F, 0.5F, 1.0F);
			tess.addVertexWithUV(0.1875, 0, 0.1875, waterIcon.getMinU(), waterIcon.getMinV());
			tess.addVertexWithUV(0.1875, 0, 0.8125, waterIcon.getMinU(), waterIcon.getMaxV());
			tess.addVertexWithUV(0.8125, 0, 0.8125, waterIcon.getMaxU(), waterIcon.getMaxV());
			tess.addVertexWithUV(0.8125, 0, 0.1875, waterIcon.getMaxU(), waterIcon.getMinV());
			tess.draw();
			tess.addTranslation(-tx, -ty, -tz);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		int itemBob = infuser.getItemBob();
		int stirProgress = infuser.getStirProgress();
		float crystalRotation = infuser.getCrystalRotation();
		double itemY = y + 0.3D + size * 0.5D;
		Random rand = new Random();
		rand.setSeed((long) (tile.xCoord + tile.yCoord + tile.zCoord));
		for(int i = 0; i <= TileEntityInfuser.MAX_INGREDIENTS; i++) {
			float randRot = rand.nextFloat() * 360.0F;
			renderItemInSlot(infuser, i, x + 0.5D - 0.2D + rand.nextFloat() * 0.4D, itemY, z + 0.5D - 0.2D + rand.nextFloat() * 0.4D, amount >= 100 ? (i % 2 == 0 ? (itemBob * 0.01D) : ((-itemBob + 20) * 0.01D)) : 0.0D, (stirProgress < 90 && amount >= 100 ? viewRot - stirProgress * 4D + 45D : viewRot + 45D) + randRot);
		}
		renderItemInSlot(infuser, TileEntityInfuser.MAX_INGREDIENTS + 1, x + 0.5, y + 1.43D, z + 0.5D, itemBob * 0.01D, crystalRotation);
	}
	private void renderItemInSlot(TileEntityInfuser infuser, int slotIndex, double x, double y, double z, double itemBob, double rotation) {
		if (infuser.getStackInSlot(slotIndex) != null) {
			GL11.glPushMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslated(x, y, z);
			GL11.glPushMatrix();
			GL11.glScaled(0.15D, 0.15D, 0.15D);
			GL11.glTranslated(0D, itemBob, 0D);
			GL11.glRotated(rotation, 0, 1, 0);
			ItemRenderHelper.renderItem(infuser.getStackInSlot(slotIndex), 0);
			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}

	private void renderStirCount(String count, double x, double y, double z) {
		float scale = 0.02666667F;
		float height = 0.8F;

		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + height + 0.75F, z + 0.5F);
		GL11.glNormal3f(0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
		GL11.glScalef(-scale, -scale, scale);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Tessellator tessellator = Tessellator.instance;
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		tessellator.startDrawingQuads();
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		int width = fontrenderer.getStringWidth(count) / 2;
		tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
		tessellator.addVertex(-width - 1, -1, 0.0D);
		tessellator.addVertex(-width - 1, 8, 0.0D);
		tessellator.addVertex(width + 1, 8, 0.0D);
		tessellator.addVertex(width + 1, -1, 0.0D);
		tessellator.draw();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		fontrenderer.drawString(count, -fontrenderer.getStringWidth(count) / 2, 0, 553648127);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		fontrenderer.drawString(count, -fontrenderer.getStringWidth(count) / 2, 0, -1);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPopMatrix();
	}
}
