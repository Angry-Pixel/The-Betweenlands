package thebetweenlands.client.render.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.render.item.ItemRender;

public class BlockBushRender extends TileEntitySpecialRenderer {

	ResourceLocation texture, stick;

	public BlockBushRender() {
		texture = new ResourceLocation("thebetweenlands", "textures/blocks/weedWoodLeavesFancy.png");
		stick = new ResourceLocation("thebetweenlands", "textures/items/weedWoodStick.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		Tessellator tessellator = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(0.25, 0.25, 0, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.75, 0, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.75, 0, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.75, 0, 0, 0);
		tessellator.addVertexWithUV(0.25, 1, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.75, 1, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.75, 0, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.0, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.25, 0.0, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.0, 0.25, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.75, 0, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.25, 0, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75, 0.0, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.25, 1, 0);

		tessellator.addVertexWithUV(1, 0.25, 0.25, 0, 0);
		tessellator.addVertexWithUV(1, 0.75, 0.25, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.75, 1, 0);

		tessellator.addVertexWithUV(1, 0.25, 0.75, 0, 0);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.75, 1, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 1, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.25, 1, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.75, 1, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.25, 1, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.25, 1, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(0, 0.75, 0.75, 1, 1);
		tessellator.addVertexWithUV(0, 0.25, 0.75, 1, 0);

		tessellator.addVertexWithUV(0, 0.25, 0.75, 0, 0);
		tessellator.addVertexWithUV(0, 0.75, 0.75, 0, 1);
		tessellator.addVertexWithUV(0, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0, 0.25, 0.25, 1, 0);

		tessellator.addVertexWithUV(0, 0.75, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.25, 1, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.25, 1, 0.25, 1, 1);
		tessellator.addVertexWithUV(0, 0.75, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.25, 1, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75, 1, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75, 1, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.25, 1, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.75, 1, 0.75, 0, 0);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75, 1, 0.25, 1, 0);

		tessellator.addVertexWithUV(1, 0.25, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.0, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.0, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.0, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.0, 0.25, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.0, 0.25, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.0, 0.25, 1, 0);

		tessellator.addVertexWithUV(0.25, 0.75, 1, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(0.75, 1, 0.75, 1, 1);
		tessellator.addVertexWithUV(0.25, 1, 0.75, 1, 0);

		tessellator.addVertexWithUV(0.25, 0, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75, 0, 0.75, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 1, 1, 1);
		tessellator.addVertexWithUV(0.25, 0.25, 1, 1, 0);

		tessellator.addVertexWithUV(0.75, 0.75, 0, 0, 0);
		tessellator.addVertexWithUV(0.75, 1, 0.25, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.75, 0, 1, 0);

		tessellator.addVertexWithUV(1, 0.25, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.25, 0, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 1, 1);
		tessellator.addVertexWithUV(0.75, 0.25, 0.0, 1, 0);

		tessellator.addVertexWithUV(0.25, 1, 0.25, 0, 0);
		tessellator.addVertexWithUV(0.25, 0.75, 0.0, 0, 1);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, 1, 1);
		tessellator.addVertexWithUV(0.0, 0.75, 0.25, 1, 0);

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

		tessellator.addVertexWithUV(0.75, 0.25, 1, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.0, 0.75, 0, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.75, 1, 1);
		tessellator.addVertexWithUV(1, 0.25, 0.75, 1, 0);

		tessellator.addVertexWithUV(0.75, 1, 0.75, 0, 0);
		tessellator.addVertexWithUV(0.75, 0.75, 1, 0, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 1, 1);
		tessellator.addVertexWithUV(1, 0.75, 0.75, 1, 0);

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