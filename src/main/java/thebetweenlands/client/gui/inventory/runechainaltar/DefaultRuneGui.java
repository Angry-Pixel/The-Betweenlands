package thebetweenlands.client.gui.inventory.runechainaltar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.gui.IGuiRuneMark;
import thebetweenlands.api.rune.gui.IRuneContainer;
import thebetweenlands.api.rune.gui.IRuneContainerContext;
import thebetweenlands.api.rune.gui.IRuneGui;
import thebetweenlands.api.rune.gui.RuneMenuDrawingContext;
import thebetweenlands.api.rune.gui.RuneMenuType;
import thebetweenlands.api.rune.impl.RuneMarkDescriptors;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.util.ColoredItemRenderer;

public class DefaultRuneGui extends Gui implements IRuneGui {
	public static class Mark implements IGuiRuneMark {
		private final IRuneGui gui;
		private final int markIndex, x, y, w, h;
		private final boolean output;

		public Mark(IRuneGui gui, int markIndex, int x, int y, int w, int h, boolean output) {
			this.gui = gui;
			this.markIndex = markIndex;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.output = output;
		}

		public boolean isOutput() {
			return this.output;
		}

		@Override
		public boolean isInside(int centerX, int centerY, int mouseX, int mouseY) {
			return mouseX >= centerX - this.w / 2 && mouseX < centerX + this.w / 2 && mouseY >= centerY - this.h / 2 && mouseY < centerY + this.h / 2;
		}

		@Override
		public int getMarkIndex() {
			return this.markIndex;
		}

		@Override
		public int getCenterX() {
			return this.gui.getMinX() + this.x + this.w / 2;
		}

		@Override
		public int getCenterY() {
			return this.gui.getMinY() + this.y + this.h / 2;
		}

		@Override
		public boolean isInteractable() {
			return true;
		}
	}

	protected final Minecraft mc = Minecraft.getMinecraft();
	protected final FontRenderer fontRenderer = this.mc.fontRenderer;
	protected final RenderItem itemRender = this.mc.getRenderItem();

	protected int width, height;

	protected final RuneMenuType menu;
	protected IRuneContainerContext context;
	protected IRuneContainer container;

	protected List<Mark> inputMarks = new ArrayList<>();
	protected List<Mark> outputMarks = new ArrayList<>();

	protected int updateCounter;
	
	protected int maxXSize = 166;
	protected int maxYSize = 216;
	
	protected int xSize = this.maxXSize;
	protected int ySize = this.maxYSize;

	protected Map<String, IMarkRenderer> markRenderers = new HashMap<>();
	protected IMarkRenderer unknownMarkRenderer = null;

	protected static final ResourceLocation UNKNOWN_MARK_DESCRIPTOR = new ResourceLocation(ModInfo.ID, "N/A");

	protected TextContainer title;
	protected TextContainer description;
	
	protected static interface IMarkRenderer {
		public void render(int centerX, int centerY);
	}

	public DefaultRuneGui(RuneMenuType menu) {
		this.menu = menu;

		this.addDefaultMarkRenderer(UNKNOWN_MARK_DESCRIPTOR, 414, 110, 12, 14);

		this.addDefaultMarkRenderer(RuneMarkDescriptors.BLOCK, 414, 94, 12, 14);
		this.addDefaultMarkRenderer(RuneMarkDescriptors.BLOCK, 430, 94, 12, 14);
		this.addDefaultMarkRenderer(RuneMarkDescriptors.ENTITY, 446, 94, 12, 14);
	}

	protected void addDefaultMarkRenderer(ResourceLocation descriptor, int minU, int minV, int width, int height) {
		this.markRenderers.put(String.format("%s.%s", descriptor.getNamespace(), descriptor.getPath()), new IMarkRenderer() {
			@Override
			public void render(int centerX, int centerY) {
				mc.getTextureManager().bindTexture(GuiRuneChainAltar.GUI_RUNE_CHAIN_ALTAR);
				drawTexturedModalRect512(centerX - width / 2, centerY - height / 2, minU, minV, width, height);
			}
		});
	}

	@Override
	public void init(IRuneContainer container, int width, int height) {
		this.context = container.getContext();
		this.container = container;
		this.width = width;
		this.height = height;

		this.title = new TextContainer(this.xSize - 8 - 20, 80, I18n.format(String.format("rune.%s.title", container.getContext().getRuneItemStack().getTranslationKey())), this.fontRenderer);

		this.title.setCurrentScale(1).setCurrentColor(0xFF3d3d3d);

		this.title.registerTag(new FormatTags.TagNewLine());
		this.title.registerTag(new FormatTags.TagTooltip("N/A"));
		this.title.registerTag(new FormatTags.TagSimple("bold", TextFormatting.BOLD));
		this.title.registerTag(new FormatTags.TagSimple("obfuscated", TextFormatting.OBFUSCATED));
		this.title.registerTag(new FormatTags.TagSimple("italic", TextFormatting.ITALIC));
		this.title.registerTag(new FormatTags.TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
		this.title.registerTag(new FormatTags.TagSimple("underline", TextFormatting.UNDERLINE));
		this.title.registerTag(new FormatTags.TagRainbow());

		this.title.parse();

		this.description = new TextContainer(this.xSize - 8 - 4, this.maxYSize - 6 - this.title.getPages().get(0).getTextHeight(), I18n.format(String.format("rune.%s.description", container.getContext().getRuneItemStack().getTranslationKey())), this.fontRenderer);

		this.description.setCurrentScale(1).setCurrentColor(0xFF3d3d3d);

		this.description.registerTag(new FormatTags.TagNewLine());
		this.description.registerTag(new FormatTags.TagTooltip("N/A"));
		this.description.registerTag(new FormatTags.TagSimple("bold", TextFormatting.BOLD));
		this.description.registerTag(new FormatTags.TagSimple("obfuscated", TextFormatting.OBFUSCATED));
		this.description.registerTag(new FormatTags.TagSimple("italic", TextFormatting.ITALIC));
		this.description.registerTag(new FormatTags.TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
		this.description.registerTag(new FormatTags.TagSimple("underline", TextFormatting.UNDERLINE));
		this.description.registerTag(new FormatTags.TagRainbow());

		this.description.parse();

		this.ySize = (int) (this.title.getPages().get(0).getTextHeight() + 20 + 45 + this.description.getPages().get(0).getTextHeight());
		
		//TODO Implement this proper
		INodeConfiguration config = container.getConfiguration();

		int xOffInputs = this.fontRenderer.getStringWidth(I18n.format("rune.gui.inputs")) + 2;
		
		int x = 4;
		for(int i = 0; i < config.getInputs().size(); i++) {
			this.inputMarks.add(new Mark(this, i, xOffInputs + x, this.ySize - 3 - 40, 16, 16, false));
			x += 18;
		}

		int xOffOutputs = this.fontRenderer.getStringWidth(I18n.format("rune.gui.outputs")) + 2;
		
		x = 4;
		for(int i = 0; i < config.getOutputs().size(); i++) {
			this.outputMarks.add(new Mark(this, i, xOffOutputs + x, this.ySize - 3 - 20, 16, 16, true));
			x += 18;
		}
	}

	@Override
	public IRuneContainer getContainer() {
		return this.container;
	}

	@Override
	public void close() {

	}

	@Override
	public void update() {
		this.updateCounter++;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY) {
		this.drawMenuBackground();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		this.drawMenu(mouseX, mouseY);
	}

	@Override
	public void drawMark(IGuiRuneMark mark, int centerX, int centerY, RuneMenuDrawingContext.Mark context) {
		this.drawMark((Mark) mark, centerX, centerY);
	}

	protected void drawMark(Mark mark, int centerX, int centerY) {
		String desc;
		if(mark.isOutput()) {
			desc = this.container.getConfiguration().getOutputs().get(mark.getMarkIndex()).getDescriptor();
			//Gui.drawRect(centerX - mark.w / 2, centerY - mark.h / 2, centerX + mark.w / 2, centerY + mark.h / 2, 0xFF0000FF);
		} else {
			desc = this.container.getConfiguration().getInputs().get(mark.getMarkIndex()).getDescriptor();
			//Gui.drawRect(centerX - mark.w / 2, centerY - mark.h / 2, centerX + mark.w / 2, centerY + mark.h / 2, 0xFFFF0000);
		}

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);

		IMarkRenderer renderer = null;
		if(desc != null) {
			renderer = this.markRenderers.get(desc);
		} else {
			renderer = this.markRenderers.get(UNKNOWN_MARK_DESCRIPTOR.toString());
		}
		if(renderer != null) {
			renderer.render(centerX, centerY);
		}
	}

	@Override
	public void drawMarkTooltip(IGuiRuneMark mark, int centerX, int centerY, int mouseX, int mouseY, RuneMenuDrawingContext.Tooltip context) {
		this.drawMarkTooltip((Mark) mark, centerX, centerY, mouseX, mouseY);
	}

	protected void drawMarkTooltip(Mark mark, int centerX, int centerY, int mouseX, int mouseY) {
		List<String> text = new ArrayList<>();

		String descriptor;

		if(mark.isOutput()) {
			descriptor = this.container.getConfiguration().getOutputs().get(mark.getMarkIndex()).getDescriptor();
		} else {
			descriptor = this.container.getConfiguration().getInputs().get(mark.getMarkIndex()).getDescriptor();
		}

		if(descriptor != null) {
			text.add(TextFormatting.RESET + "     " + I18n.format(String.format("rune.mark.%s", descriptor)));
		} else {
			text.add(TextFormatting.RESET + "     " + I18n.format("rune.mark.unknown"));
		}

		text.add(TextFormatting.RESET + "     " + TextFormatting.DARK_PURPLE + (mark.isOutput() ? "Output" : "Input"));

		if(descriptor != null && I18n.hasKey(String.format("rune.mark.%s.description", descriptor))) {
			text.add(TextFormatting.GRAY + I18n.format(String.format("rune.mark.%s.description", descriptor)));
		}

		this.drawHoveringText(text, mouseX, mouseY, this.fontRenderer);

		GlStateManager.disableDepth();
		this.drawMark(mark, mouseX + 12 + 9, mouseY - 12 + 10);
		GlStateManager.enableDepth();

		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public void drawMarkConnection(IGuiRuneMark mark, int targetX, int targetY, RuneMenuDrawingContext.Connection context) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 280.0F);
		this.drawHangingRope(mark.getCenterX(), mark.getCenterY(), targetX, targetY);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean onKeyTyped(char typedChar, int keyCode, boolean handled) {
		return false;
	}

	@Override
	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton, boolean handled) {
		return false;
	}

	@Override
	public boolean onMouseReleased(int mouseX, int mouseY, int state, boolean handled) {
		return false;
	}

	@Override
	public void onMouseInput() {

	}

	@Override
	public void onParentSizeSet(int w, int h) {
		this.width = w;
		this.height = h;
	}

	@Override
	public boolean onStartMarkLinking(IGuiRuneMark mark, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public boolean onStartMarkUnlinking(IGuiRuneMark mark, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public IGuiRuneMark getInputMark(int markIndex) {
		return this.inputMarks.get(markIndex);
	}

	@Override
	public Collection<Mark> getInputMarks() {
		return this.inputMarks;
	}

	@Override
	public IGuiRuneMark getOutputMark(int markIndex) {
		return this.outputMarks.get(markIndex);
	}

	@Override
	public Collection<Mark> getOutputMarks() {
		return this.outputMarks;
	}

	@Override
	public int getMinX() {
		return this.menu == RuneMenuType.PRIMARY ? this.context.getRuneChainAltarGui().getMinX() - 185 : this.context.getRuneChainAltarGui().getMaxX() + 19;
	}

	@Override
	public int getMinY() {
		return this.context.getRuneChainAltarGui().getMinY() + 70 - this.ySize / 2;
	}

	@Override
	public int getMaxX() {
		return this.getMinX() + this.xSize;
	}

	@Override
	public int getMaxY() {
		return this.getMinY() + this.ySize;
	}

	protected void drawMenuBackground() {
		int x = this.getMinX();
		int y = this.getMinY();

		//Top left corner
		this.drawTexturedModalRect512(x, y, 212, 94, 3, 3);
		//Top bar
		this.drawTexturedModalRect512(x + 3, y, 215, 94, this.xSize - 6, 3);
		//Top right corner
		this.drawTexturedModalRect512(x + 3 + this.xSize - 6, y, 383, 94, 3, 3);
		//Right bar
		this.drawTexturedModalRect512(x + 3 + this.xSize - 6, y + 3, 383, 97, 3, this.ySize - 6);
		//Bottom right corner
		this.drawTexturedModalRect512(x + 3 + this.xSize - 6, y + 3 + this.ySize - 6, 383, 313, 3, 3);
		//Bottom bar
		this.drawTexturedModalRect512(x + 3, y + 3 + this.ySize - 6, 215, 313, this.xSize - 6, 3);
		//Bottom left corner
		this.drawTexturedModalRect512(x, y + 3 + this.ySize - 6, 212, 313, 3, 3);
		//Left bar
		this.drawTexturedModalRect512(x, y + 3, 212, 97, 3, this.ySize - 6);

		//Background
		this.drawTexturedModalRect512(x + 3, y + 3, 215, 97, this.xSize - 6, this.ySize - 6);

		ItemStack stack = this.context.getRuneItemStack();

		ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, stack, x + 4, y + 4, 1, 1, 1, 1);

		/*this.fontRenderer.drawString(TextFormatting.UNDERLINE + stack.getDisplayName(), x + 24, y + 8, 0xFF404040);

		//TODO Remove all this and implement it properly with multiple pages etc.

		INodeBlueprint<?, RuneExecutionContext> bp = this.container.getBlueprint();

		this.fontRenderer.drawString("Configs: " + bp.getConfigurations().size(), x + 4, y + 22, 0xFF404040);
		int i = 1;
		for(INodeConfiguration config : bp.getConfigurations()) {
			this.fontRenderer.drawString(" " + config.getId() + ") Marks: " + config.getInputs().size() + "/" + config.getOutputs().size(), x + 4, y + 22 + i * 10, 0xFF404040);
			i++;
		}*/

		this.title.getPages().get(0).render(x + 4 + 20, y + 8);

		this.description.getPages().get(0).render(x + 4, y + 16 + this.title.getPages().get(0).getTextHeight());
		
		//TODO
		this.fontRenderer.drawString(I18n.format("rune.gui.inputs"), x + 4, y + 4 + this.ySize - 3 - 40, 0xFF3d3d3d);
		this.fontRenderer.drawString(I18n.format("rune.gui.outputs"), x + 4, y + 4 + this.ySize - 3 - 20, 0xFF3d3d3d);
		
		for(Mark mark : this.inputMarks) {
			this.drawMark(mark, mark.getCenterX(), mark.getCenterY());
		}

		for(Mark mark : this.outputMarks) {
			this.drawMark(mark, mark.getCenterX(), mark.getCenterY());
		}

		GlStateManager.color(1, 1, 1, 1);
	}

	protected void drawMenu(int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1, 1);

		/*if(this.menu == RuneMenuType.PRIMARY) {
			int mx = Mouse.getX() * this.width / this.mc.displayWidth;
			int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;

			this.zLevel = 280.0F;
			this.drawHangingRope(x + width - 10, y + height - 10, mx - 10, my - 10);
			this.zLevel = 0;
		}*/

		for(Mark mark : this.inputMarks) {
			if(mark.isInside(mark.getCenterX(), mark.getCenterY(), mouseX, mouseY)) {
				this.drawMarkTooltip(mark, mark.getCenterX(), mark.getCenterY(), mouseX, mouseY);
			}
		}

		for(Mark mark : this.outputMarks) {
			if(mark.isInside(mark.getCenterX(), mark.getCenterY(), mouseX, mouseY)) {
				this.drawMarkTooltip(mark, mark.getCenterX(), mark.getCenterY(), mouseX, mouseY);
			}
		}

		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GuiRuneChainAltar.GUI_RUNE_CHAIN_ALTAR);
	}

	protected void drawHangingRope(float sx, float sy, float ex, float ey) {
		this.mc.getTextureManager().bindTexture(GuiRuneChainAltar.GUI_RUNE_CHAIN_ALTAR_ROPE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float x1 = sx;
		float y1 = sy;

		float x3 = ex;
		float y3 = ey;

		if(x1 - x3 >= 0.0F && x1 - x3 < 1.0F) {
			x3 = x1 + 1;
		} else if (x1 - x3 < 0.0F && x1 - x3 > -1.0F) {
			x3 = x1 - 1;
		}

		float x2 = (x1 + x3) / 2.0F;
		float y2 = Math.max(y1, y3) + 0.0F + (float)Math.sin((this.updateCounter + this.mc.getRenderPartialTicks()) / 25.0F) * 1.5f;

		//Fit parabola
		float a1 = -x1*x1 + x2*x2;
		float b1 = -x1 + x2;
		float d1 = -y1 + y2;
		float a2 = -x2*x2 + x3*x3;
		float b2 = -x2 + x3;
		float d2 = -y2 + y3;
		float b3 = -b2 / b1;
		float a3 = b3 * a1 + a2;
		float d = b3 * d1 + d2;
		float a = d / a3;
		float b = (d1 - a1 * a) / b1;
		float c = y1 - a * x1*x1 - b * x1;

		float px = x1;
		float py = y1;

		float width = 2F;

		float pxc1 = x1 - width;
		float pyc1 = y1;
		float pxc2 = x1 + width;
		float pyc2 = y1;

		boolean isTowardsRight = x1 < x3;

		float ropeV1 = isTowardsRight ? 0 : 0.5F;
		float ropeV2 = isTowardsRight ? 0.5F : 0;

		float endV1 = isTowardsRight ? 0.5F : 1.0F;
		float endV2 = isTowardsRight ? 1.0F : 0.5F;

		float endU11 = isTowardsRight ? 0 : 1.0F;
		float endU12 = isTowardsRight ? 0.5F : 0.5F;

		float endU21 = isTowardsRight ? 0.5F : 0.5F;
		float endU22 = isTowardsRight ? 1.0F : 0;

		float u = 0;

		buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

		int pieces = 32;

		for(int i = -1; i <= pieces; i++) {
			float x = x1 + (x3 - x1) / (float) (pieces - 1) * i;
			float y = a * x*x + b * x + c;

			float sideX = y - py;
			float sideY = -(x - px);
			float sideDirLength = (float) Math.sqrt(sideX*sideX + sideY*sideY);
			sideX *= width / sideDirLength;
			sideY *= width / sideDirLength;

			float xc1 = x - sideX;
			float yc1 = y - sideY;
			float xc2 = x + sideX;
			float yc2 = y + sideY;

			if(i == 1) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2 - offX, pyc2 - offY, this.zLevel).tex(endU11, endV1).endVertex();
				buffer.pos(pxc1 - offX, pyc1 - offY, this.zLevel).tex(endU11, endV2).endVertex();
				buffer.pos(pxc2, pyc2, this.zLevel).tex(endU12, endV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(endU12, endV2).endVertex();
			}

			if(i > 0) {
				buffer.pos(pxc2, pyc2, this.zLevel).tex(u, ropeV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(u, ropeV2).endVertex();

				u += (float) Math.sqrt((x-px)*(x-px) + (y-py)*(y-py)) / 16.0F;
			}

			if(i == pieces) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2, pyc2, this.zLevel).tex(endU21, endV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(endU21, endV2).endVertex();
				buffer.pos(pxc2 + offX, pyc2 + offY, this.zLevel).tex(endU22, endV1).endVertex();
				buffer.pos(pxc1 + offX, pyc1 + offY, this.zLevel).tex(endU22, endV2).endVertex();
			}

			px = x;
			py = y;

			pxc1 = xc1;
			pyc1 = yc1;
			pxc2 = xc2;
			pyc2 = yc2;
		}

		tessellator.draw();
	}

	/**
	 * Same as {@link #drawTexturedModalRect(int, int, int, int, int, int)} but for 512x512 textures
	 */
	protected void drawTexturedModalRect512(float x, float y, int minU, int minV, int width, int height) {
		float scale = 0.001953125F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		tessellator.draw();
	}

	protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font) {
		net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(textLines, x, y, this.width, this.height, -1, font);
	}
}
