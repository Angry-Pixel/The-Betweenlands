package thebetweenlands.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class LoreScrapScreen extends Screen {

	protected static final double WIDTH = 165.0D * 1.3D;
	protected static final double HEIGHT = 200.0D * 1.3D;

	protected int xStart;
	protected int yStart;
	protected final ResourceLocation pageTexture;

	public LoreScrapScreen(ResourceLocation itemName) {
		super(Component.empty());
		this.pageTexture = ResourceLocation.fromNamespaceAndPath(itemName.getNamespace(), "textures/gui/lore/" + itemName.getPath().replace("_lore_scrap", "") +".png");
	}

	@Override
	protected void init() {
		super.init();
		this.xStart = (int) ((this.width - WIDTH) / 2);
		this.yStart = (int) ((this.height - HEIGHT) / 2);
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		super.render(graphics, mouseX, mouseY, partialTick);
		this.drawTexture(graphics, this.xStart, this.yStart, (int) WIDTH, (int) HEIGHT, WIDTH, HEIGHT, 0, (int) WIDTH, 0, (int) HEIGHT);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.onClose();
		return true;
	}

	private void drawTexture(GuiGraphics graphics, int xStart, int yStart, int width, int height, double textureWidth, double textureHeight, int textureXStart, int textureXEnd, int textureYStart, int textureYEnd) {
		float umin = (float) (1.0F / textureWidth * textureXStart);
		float umax = (float) (1.0F / textureWidth * textureXEnd);
		float vmin = (float) (1.0F / textureHeight * textureYStart);
		float vmax = (float) (1.0F / textureHeight * textureYEnd);

		RenderSystem.setShaderTexture(0, this.pageTexture);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.enableBlend();
		Matrix4f matrix4f = graphics.pose().last().pose();
		BufferBuilder buffer = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		buffer.addVertex(matrix4f, xStart, yStart, 0).setUv(umin, vmin).setColor(-1);
		buffer.addVertex(matrix4f, xStart, yStart + height, 0).setUv(umin, vmax).setColor(-1);
		buffer.addVertex(matrix4f, xStart + width, yStart + height, 0).setUv(umax, vmax).setColor(-1);
		buffer.addVertex(matrix4f, xStart + width, yStart, 0).setUv(umax, vmin).setColor(-1);
		BufferUploader.drawWithShader(buffer.buildOrThrow());
		RenderSystem.disableBlend();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}
