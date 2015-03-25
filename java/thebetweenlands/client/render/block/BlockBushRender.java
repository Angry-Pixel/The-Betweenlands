package thebetweenlands.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.blocks.BlockWeedWoodBush;

public class BlockBushRender extends TileEntitySpecialRenderer {

	ResourceLocation fancyTexture, fastTexture, stick;

	public BlockBushRender() {
		fancyTexture = new ResourceLocation("thebetweenlands", "textures/blocks/weedWoodLeavesFancy.png");
		fastTexture = new ResourceLocation("thebetweenlands", "textures/blocks/weedWoodLeavesFast.png");
		stick = new ResourceLocation("thebetweenlands", "textures/items/weedWoodStick.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		if (Minecraft.isFancyGraphicsEnabled())
			Minecraft.getMinecraft().renderEngine.bindTexture(fancyTexture);
		else
			Minecraft.getMinecraft().renderEngine.bindTexture(fastTexture);
		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		float mini = 0F, minj = 0F, mink = 0F, maxi = 0F, maxj = 0F, maxk = 0F;
		tessellator.startDrawingQuads();
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord - 1, tileentity.yCoord, tileentity.zCoord) instanceof BlockWeedWoodBush))
			mini = -0.25F;
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord + 1, tileentity.yCoord, tileentity.zCoord) instanceof BlockWeedWoodBush))
			maxi = 0.25F;
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord, tileentity.yCoord - 1, tileentity.zCoord) instanceof BlockWeedWoodBush))
			minj = -0.25F;
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord, tileentity.yCoord + 1, tileentity.zCoord) instanceof BlockWeedWoodBush))
			maxj = 0.25F;
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord - 1) instanceof BlockWeedWoodBush))
			mink = -0.25F;
		if (tileentity.hasWorldObj() && (tileentity.getWorldObj().getBlock(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord + 1) instanceof BlockWeedWoodBush))
			maxk = 0.25F;

		// Right Side
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.25 + mink, 1, 0);

		// Right-Top Side
		tessellator.addVertexWithUV(0, 0.75, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0.25, 1, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0.25, 1, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0, 0.75, 0.25 + mink, 1, 0);

		// Right-Bottom Side
		tessellator.addVertexWithUV(0.25, 0.0, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0.0, 0.25, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25 + mink, 1, 0);

		// Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25 + mink, 0, 0);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25 + mink, 0, 1);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75 + maxk, 1, 1);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75 + maxk, 1, 0);

		// Left-Top Side
		tessellator.addVertexWithUV(0.75, 1, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(1, 0.75, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0.75, 1, 0.25 + mink, 1, 0);

		// Left-Bottom Side
		tessellator.addVertexWithUV(1, 0.25, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.25 + mink, 1, 0);

		// Front Side
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 0, 0, 0);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 0, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 0, 1, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 0, 1, 0);

		// Front-Right Side
		tessellator.addVertexWithUV(0.0, 0.25 + minj, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.75 + maxj, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 0, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 0, 1, 0);

		// Front-Left Side
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 0.0, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 0.0, 0, 1);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.25, 1, 1);
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.25, 1, 0);

		// Front-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 0, 0, 0);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 0, 1, 0);

		// Front-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 0.0, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 0.0, 1, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25, 1, 0);

		// Back Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.25 + minj, 1, 0, 0);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75 + maxj, 1, 0, 1);
		tessellator.addVertexWithUV(0.25 + mini, 0.75 + maxj, 1, 1, 1);
		tessellator.addVertexWithUV(0.25 + mini, 0.25 + minj, 1, 1, 0);

		// Back-Top Side
		tessellator.addVertexWithUV(0.25 + mini, 0.75, 1, 0, 0);
		tessellator.addVertexWithUV(0.75 + maxi, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75, 1, 1);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75, 1, 0);

		// Back-Left Side
		tessellator.addVertexWithUV(1, 0.25 + minj, 0.75, 0, 0);
		tessellator.addVertexWithUV(1, 0.75 + maxj, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.75 + maxj, 1, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.25 + minj, 1, 1, 0);

		// Back-Right Side
		tessellator.addVertexWithUV(0.25, 0.25 + minj, 1, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.75 + maxj, 1, 0, 1);
		tessellator.addVertexWithUV(0, 0.75 + maxj, 0.75, 1, 1);
		tessellator.addVertexWithUV(0, 0.25 + minj, 0.75, 1, 0);

		// Back-Bottom Side
		tessellator.addVertexWithUV(0.25 + mini, 0, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75 + maxi, 0, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.25, 1, 1, 1);
		tessellator.addVertexWithUV(0.25 + mini, 0.25, 1, 1, 0);

		// Top Side
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 1, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0.25 + mini, 1, 0.25 + mink, 1, 0);

		// Bottom Side
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.75 + maxk, 0, 0);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.75 + maxk, 0, 1);
		tessellator.addVertexWithUV(0.25 + mini, 0.0, 0.25 + mink, 1, 1);
		tessellator.addVertexWithUV(0.75 + maxi, 0.0, 0.25 + mink, 1, 0);

		// Corners
		tessellator.addVertexWithUV(1, 0.25, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.25, 1, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75, 0, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.75, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.75, 1, 0);

		tessellator.addVertexWithUV(0.75, 1, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 1, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.75, 0, 0, 0);
		tessellator.addVertexWithUV(0.75, 1, 0.25, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.75, 0, 1, 0);

		tessellator.addVertexWithUV(0.0, 0.25, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.25, 0.0, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.25, 1, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.75, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.75, 1, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.75, 1, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.25, 1, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, 1, 0);

		tessellator.addVertexWithUV(0.25, 1, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.75, 0.0, 0, 1);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, 1, 0);

		if (Minecraft.isFancyGraphicsEnabled()) {
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, 0, 0);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.9, 0.5, 1.1, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);

			tessellator.addVertexWithUV(0.9, 0.5, 1.1, 0, 0);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.1, 0.5, -0.1, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);

			tessellator.addVertexWithUV(0.1, 0.5, 0.7, 0, 0);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.9, 0.5, 0.3, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);

			tessellator.addVertexWithUV(0.9, 0.5, 0.3, 0, 0);
			tessellator.addVertexWithUV(0.3, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.1, 0.5, 0.7, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);

			tessellator.addVertexWithUV(0.3, 0.5, 1.1, 0, 0);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.9, 0.5, -0.1, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);

			tessellator.addVertexWithUV(0.9, 0.5, -0.1, 0, 0);
			tessellator.addVertexWithUV(0.5, 1.1, 0.5, 1, 0);
			tessellator.addVertexWithUV(0.3, 0.5, 1.1, 1, 1);
			tessellator.addVertexWithUV(0.5, -0.1, 0.5, 0, 1);
		}

		tessellator.draw();
		if (Minecraft.isFancyGraphicsEnabled()) {
			Minecraft.getMinecraft().renderEngine.bindTexture(stick);
			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, -0.3F, 0.5F);
			GL11.glRotatef(45F, 0F, 0F, 1F);
			GL11.glScalef(0.8F, 0.8F, 0.8F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0.5F, 0.3F, 0.5F);
			GL11.glRotatef(45F, 0F, 1F, 0F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);

			GL11.glTranslatef(-0.1F, -0.2F, -0.1F);
			GL11.glRotatef(65F, 0F, 1F, 0F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);

			GL11.glTranslatef(0.3F, 0.5F, -0.2F);
			GL11.glRotatef(75F, 1F, 0F, 0F);
			GL11.glScalef(0.7F, 0.7F, 0.7F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);

			GL11.glTranslatef(-0.2F, -0.2F, 0.0F);
			GL11.glRotatef(125F, 0F, 1F, 0F);
			GL11.glRotatef(25F, -1F, 0F, 0F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(1.2F, -0.2F, 0.5F);
			GL11.glRotatef(125F, 0F, 1F, 0F);
			GL11.glRotatef(25F, -1F, 0F, 0F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0.9F, 0.2F, 0.9F);
			GL11.glRotatef(115F, 0F, 1F, 0F);
			GL11.glRotatef(25F, -1F, 0F, 0F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0.6F, 0.4F, 0.4F);
			GL11.glRotatef(125F, 0F, -1F, 0F);
			GL11.glRotatef(175F, 1F, 0F, 0F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glTranslatef(0.6F, 0.4F, 0.4F);
			GL11.glRotatef(140F, 0F, -1F, 0F);
			GL11.glRotatef(15F, 0F, 0F, -1F);
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			ItemRenderer.renderItemIn2D(tessellator, 0F, 0F, 1F, 1F, 255, 255, 0.0625F);
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}