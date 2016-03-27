package thebetweenlands.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Bart on 23/03/2016.
 */
public class GuiLorePage extends GuiScreen {
	private static final double WIDTH = 165.0D*1.3D;
	private static final double HEIGHT = 200.0D*1.3D;
	public int xStart;
	public int yStart;
	private ResourceLocation page;

	public GuiLorePage(ItemStack stack) {
		String name = "";
		if (stack != null && stack.getTagCompound() != null && stack.getTagCompound().hasKey("name"))
			name = stack.getTagCompound().getString("name");
		page = new ResourceLocation("thebetweenlands:textures/gui/lore/" + name + ".png");
	}

	@Override
	public void initGui() {
		xStart = (width - (int) WIDTH) / 2;
		yStart = (height - (int) HEIGHT) / 2;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void drawScreen(int mouseX, int mouseY, float renderPartials) {
		super.drawScreen(mouseX, mouseY, renderPartials);

		if (page != null) {
			mc.renderEngine.bindTexture(page);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
			drawTexture(xStart, yStart, (int) WIDTH, (int) HEIGHT, WIDTH, HEIGHT, 0.0D, WIDTH, 0.0D, HEIGHT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		}
	}

	/**
	 * Drawing a scalable texture
	 *
	 * @param xStart        the x coordinate to start drawing
	 * @param yStart        the y coordinate to start drawing
	 * @param width         the width for drawing
	 * @param height        the height for drawing
	 * @param textureWidth  the width of the texture
	 * @param textureHeight the height of the texture
	 * @param textureXStart the x start in the texture
	 * @param textureXEnd   the x end in the texture
	 * @param textureYStart the y start in the texture
	 * @param textureYEnd   the y end in the texture
	 */
	private void drawTexture(int xStart, int yStart, int width, int height, double textureWidth, double textureHeight, double textureXStart, double textureXEnd, double textureYStart, double textureYEnd) {
		double umin = 1.0D / textureWidth * textureXStart;
		double umax = 1.0D / textureWidth * textureXEnd;
		double vmin = 1.0D / textureHeight * textureYStart;
		double vmax = 1.0D / textureHeight * textureYEnd;
		Tessellator.instance.startDrawingQuads();
		Tessellator.instance.addVertexWithUV(xStart, yStart, 0, umin, vmin);
		Tessellator.instance.addVertexWithUV(xStart, yStart + height, 0, umin, vmax);
		Tessellator.instance.addVertexWithUV(xStart + width, yStart + height, 0, umax, vmax);
		Tessellator.instance.addVertexWithUV(xStart + width, yStart, 0, umax, vmin);
		Tessellator.instance.draw();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		mc.displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
