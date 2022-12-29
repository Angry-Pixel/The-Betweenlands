package thebetweenlands.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.item.misc.ItemLoreScrap;
import thebetweenlands.common.lib.ModInfo;

public class GuiLorePage extends GuiScreen {
	protected static final double WIDTH = 165.0D * 1.3D;
	protected static final double HEIGHT = 200.0D * 1.3D;

	protected int xStart;
	protected int yStart;
	protected ResourceLocation pageTexture;

	public GuiLorePage(ItemStack stack) {
		String name = ItemLoreScrap.getPageName(stack);
		if(name != null) {
			this.pageTexture = new ResourceLocation(ModInfo.ID, "textures/gui/lore/" + name + ".png");
		}
	}

	@Override
	public void initGui() {
		this.xStart = (this.width - (int) WIDTH) / 2;
		this.yStart = (this.height - (int) HEIGHT) / 2;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawScreen(int mouseX, int mouseY, float renderPartials) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, renderPartials);

		if (this.pageTexture != null) {
			GlStateManager.color(1F, 1F, 1F, 1F);
			this.mc.renderEngine.bindTexture(this.pageTexture);
			drawTexture(xStart, yStart, (int) WIDTH, (int) HEIGHT, WIDTH, HEIGHT, 0.0D, WIDTH, 0.0D, HEIGHT);
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

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		buffer.pos(xStart, yStart, 0).tex(umin, vmin).endVertex();
		buffer.pos(xStart, yStart + height, 0).tex(umin, vmax).endVertex();
		buffer.pos(xStart + width, yStart + height, 0).tex(umax, vmax).endVertex();
		buffer.pos(xStart + width, yStart, 0).tex(umax, vmin).endVertex();
		tessellator.draw();
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		this.mc.displayGuiScreen(null);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}
