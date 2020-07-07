package thebetweenlands.client.gui.inventory.runeweavingtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import thebetweenlands.api.rune.IGuiRuneToken;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerContext;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.RuneMenuDrawingContext;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.serverbound.MessageSetRuneWeavingTableConfiguration;
import thebetweenlands.util.ColoredItemRenderer;

public class DefaultRuneGui extends Gui implements IRuneGui {
	public static class Token implements IGuiRuneToken {
		private final IRuneGui gui;
		private final int tokenIndex, x, y, w, h;
		private final boolean output;

		public Token(IRuneGui gui, int tokenIndex, int x, int y, int w, int h, boolean output) {
			this.gui = gui;
			this.tokenIndex = tokenIndex;
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
		public int getTokenIndex() {
			return this.tokenIndex;
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

	public static final ResourceLocation GUI_RUNE_MENU = new ResourceLocation("thebetweenlands:textures/gui/rune/rune_menu.png");
	public static final ResourceLocation GUI_RUNE_ROPE = new ResourceLocation("thebetweenlands:textures/gui/rune/rune_rope.png");
	public static final ResourceLocation GUI_RUNE_TOKENS = new ResourceLocation("thebetweenlands:textures/gui/rune/rune_tokens.png");

	protected final Minecraft mc = Minecraft.getMinecraft();
	protected final FontRenderer fontRenderer = this.mc.fontRenderer;
	protected final RenderItem itemRender = this.mc.getRenderItem();

	protected int width, height;

	protected final RuneMenuType menu;
	protected IRuneContainerContext context;
	protected IRuneContainer container;

	protected List<Token> inputTokens = new ArrayList<>();
	protected List<Token> outputTokens = new ArrayList<>();

	protected int updateCounter;

	protected int maxXSize = 166;
	protected int maxYSize = 216;

	protected int xSize = this.maxXSize;
	protected int ySize = this.maxYSize;

	protected Map<String, ITokenRenderer> tokenRenderers = new HashMap<>();
	protected ITokenRenderer unknownTokenRenderer = null;

	protected static final ResourceLocation UNKNOWN_TOKEN_DESCRIPTOR = new ResourceLocation(ModInfo.ID, "N/A");

	protected TextContainer title;
	protected TextContainer description;

	protected int currentDescriptionPageIndex = 0;

	protected boolean hasMultipleConfigurations = false;
	protected int additionalConfigurationsHeight = 0;

	int currentConfigurationIndex = 0;

	protected static interface ITokenRenderer {
		public void render(int centerX, int centerY);
	}

	public DefaultRuneGui(RuneMenuType menu) {
		this.menu = menu;

		this.addDefaultTokenRenderer(UNKNOWN_TOKEN_DESCRIPTOR, 27, 1, 8, 10);

		this.addDefaultTokenRenderer(RuneTokenDescriptors.BLOCK, 0, 0, 8, 10);
		this.addDefaultTokenRenderer(RuneTokenDescriptors.ENTITY, 9, 0, 8, 10);
		this.addDefaultTokenRenderer(RuneTokenDescriptors.POSITION, 18, 0, 8, 10);
		this.addDefaultTokenRenderer(RuneTokenDescriptors.RAY, 0, 11, 8, 10);
	}

	protected void addDefaultTokenRenderer(ResourceLocation descriptor, int minU, int minV, int width, int height) {
		this.tokenRenderers.put(String.format("%s.%s", descriptor.getNamespace(), descriptor.getPath()), new ITokenRenderer() {
			@Override
			public void render(int centerX, int centerY) {
				mc.getTextureManager().bindTexture(GUI_RUNE_TOKENS);
				
				float x = centerX - width / 2;
				float y = centerY - height / 2;
				
				float scale = 0.0285714285F;
				
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
				bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + height) * scale)).endVertex();
				bufferbuilder.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + height) * scale)).endVertex();
				bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + 0) * scale)).endVertex();
				bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + 0) * scale)).endVertex();
				tessellator.draw();
			}
		});
	}

	@Override
	public void init(IRuneContainer container, int width, int height) {
		this.context = container.getContext();
		this.container = container;
		this.width = width;
		this.height = height;

		this.createGui();
	}

	protected void createGui() {
		this.currentDescriptionPageIndex = 0;

		this.hasMultipleConfigurations = container.getBlueprint().getConfigurations().size() > 1;
		if(this.hasMultipleConfigurations) {
			this.additionalConfigurationsHeight = 12;

			this.currentConfigurationIndex = Math.max(0, this.container.getBlueprint().getConfigurations().indexOf(this.context.getConfiguration()));
		}

		INodeConfiguration config = this.context.getConfiguration();

		int tokensHeight = 0;

		if(!config.getInputs().isEmpty()) {
			tokensHeight += 22;
		}

		if(!config.getOutputs().isEmpty()) {
			tokensHeight += 22;
		}

		this.title = new TextContainer(this.xSize - 8 - 20, 80, I18n.format(String.format("rune.%s.configuration.%d.title", container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId())), this.fontRenderer);

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

		this.description = new TextContainer(this.xSize - 8 - 4, this.maxYSize - 6 - this.title.getPages().get(0).getTextHeight() - 50 - this.additionalConfigurationsHeight, I18n.format(String.format("rune.%s.configuration.%d.description", container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId())), this.fontRenderer);

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

		this.ySize = (int) (this.title.getPages().get(0).getTextHeight() + 20 + tokensHeight + this.description.getPages().get(0).getTextHeight()) + this.additionalConfigurationsHeight;

		int tokensYOffset = 0;

		int xOffOutputs = this.fontRenderer.getStringWidth(I18n.format("rune.gui.outputs")) + 2;

		this.outputTokens.clear();
		int x = 4;
		for(int i = 0; i < config.getOutputs().size(); i++) {
			this.outputTokens.add(new Token(this, i, xOffOutputs + x, this.ySize - 3 - 20 - tokensYOffset - this.additionalConfigurationsHeight, 16, 16, true));
			x += 18;
		}

		if(!this.outputTokens.isEmpty()) {
			tokensYOffset += 20;
		}

		int xOffInputs = this.fontRenderer.getStringWidth(I18n.format("rune.gui.inputs")) + 2;

		x = 4;
		this.inputTokens.clear();
		for(int i = 0; i < config.getInputs().size(); i++) {
			this.inputTokens.add(new Token(this, i, xOffInputs + x, this.ySize - 3 - 20 - tokensYOffset - this.additionalConfigurationsHeight, 16, 16, false));
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
	public void drawToken(IGuiRuneToken token, int centerX, int centerY, RuneMenuDrawingContext.Token context) {
		if(token instanceof Token) {
			this.drawToken((Token) token, centerX, centerY);
		}
	}

	protected void drawToken(Token token, int centerX, int centerY) {
		String desc;
		if(token.isOutput()) {
			desc = this.context.getConfiguration().getOutputs().get(token.getTokenIndex()).getDescriptor();
			//Gui.drawRect(centerX - token.w / 2, centerY - token.h / 2, centerX + token.w / 2, centerY + token.h / 2, 0xFF0000FF);
		} else {
			desc = this.context.getConfiguration().getInputs().get(token.getTokenIndex()).getDescriptor();
			//Gui.drawRect(centerX - token.w / 2, centerY - token.h / 2, centerX + token.w / 2, centerY + token.h / 2, 0xFFFF0000);
		}

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1, 1);

		ITokenRenderer renderer = null;
		if(desc != null) {
			renderer = this.tokenRenderers.get(desc);
		} else {
			renderer = this.tokenRenderers.get(UNKNOWN_TOKEN_DESCRIPTOR.toString());
		}
		if(renderer != null) {
			renderer.render(centerX, centerY);
		}
	}

	@Override
	public void drawTokenTooltip(IGuiRuneToken token, int centerX, int centerY, int mouseX, int mouseY, RuneMenuDrawingContext.Tooltip context) {
		if(token instanceof Token) {
			this.drawTokenTooltip((Token) token, centerX, centerY, mouseX, mouseY);
		}
	}

	protected void drawTokenTooltip(Token token, int centerX, int centerY, int mouseX, int mouseY) {
		List<String> text = new ArrayList<>();

		String descriptor;

		if(token.isOutput()) {
			descriptor = this.context.getConfiguration().getOutputs().get(token.getTokenIndex()).getDescriptor();
		} else {
			descriptor = this.context.getConfiguration().getInputs().get(token.getTokenIndex()).getDescriptor();
		}

		if(descriptor != null) {
			text.add(TextFormatting.RESET + "     " + I18n.format(String.format("rune.token.%s", descriptor)));
		} else {
			text.add(TextFormatting.RESET + "     " + I18n.format("rune.token.unknown"));
		}

		text.add(TextFormatting.RESET + "     " + TextFormatting.DARK_PURPLE + (token.isOutput() ? I18n.format("rune.output") : I18n.format("rune.input")));

		if(descriptor != null) {
			if(token.isOutput()) {
				if(I18n.hasKey(String.format("rune.%s.configuration.%d.output.%d.description", this.container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId(), token.getTokenIndex()))) {
					text.addAll(ItemTooltipHandler.splitTooltip(TextFormatting.GRAY + I18n.format(String.format("rune.%s.configuration.%d.output.%d.description", this.container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId(), token.getTokenIndex())), 0));
				}
			} else {
				if(I18n.hasKey(String.format("rune.%s.configuration.%d.input.%d.description", this.container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId(), token.getTokenIndex()))) {
					text.addAll(ItemTooltipHandler.splitTooltip(TextFormatting.GRAY + I18n.format(String.format("rune.%s.configuration.%d.input.%d.description", this.container.getContext().getRuneItemStack().getTranslationKey(), this.context.getConfiguration().getId(), token.getTokenIndex())), 0));
				}
			}
		}

		this.drawHoveringText(text, mouseX, mouseY, this.fontRenderer, (tx, ty) -> {
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			this.drawToken(token, tx + 12, ty + 13);
		});

		GlStateManager.enableDepth();
		GlStateManager.disableLighting();
		GlStateManager.color(1, 1, 1, 1);
	}

	@Override
	public void drawTokenConnection(IGuiRuneToken token, int targetX, int targetY, RuneMenuDrawingContext.Connection context) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 280.0F);
		drawHangingRope(this.updateCounter, token.getCenterX(), token.getCenterY(), targetX, targetY, 0.0F, this.zLevel);
		GlStateManager.popMatrix();
	}

	@Override
	public boolean onKeyTyped(char typedChar, int keyCode, boolean handled) {
		return false;
	}

	@Override
	public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton, boolean handled) {
		int x = this.getMinX();
		int y = this.getMinY();

		//TODO Sounds?
		if(mouseX >= x + this.xSize - 6 - 40 && mouseX < x + this.xSize - 6 - 40 + 13
				&& mouseY >= y + 8 && mouseY < y + 8 + 7) {
			this.currentDescriptionPageIndex = Math.max(this.currentDescriptionPageIndex - 1, 0);
			return true;
		}

		if(mouseX >= x + this.xSize - 6 - 13 && mouseX < x + this.xSize - 6 - 13 + 13
				&& mouseY >= y + 8 && mouseY < y + 8 + 7) {
			this.currentDescriptionPageIndex = Math.min(this.currentDescriptionPageIndex + 1, this.description.getPages().size() - 1);
			return true;
		}

		//this.drawTexturedModalRect512(x + this.xSize / 2 - 8 - 13, y + this.ySize - 13, 452, 0, 13, 7);
		//this.drawTexturedModalRect512(x + this.xSize / 2 + 8, y + this.ySize - 13, 452, 29, 13, 7);

		if(mouseX >= x + this.xSize / 2 - 8 - 13 && mouseX < x + this.xSize / 2 - 8 - 13 + 13
				&& mouseY >= y + this.ySize - 13 && mouseY < y + this.ySize - 13 + 7) {
			this.currentConfigurationIndex = Math.max(this.currentConfigurationIndex - 1, 0);
			this.context.setConfiguration(this.container.getBlueprint().getConfigurations().get(this.currentConfigurationIndex));
			this.createGui();
			TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneWeavingTableConfiguration(this.context.getRuneIndex(), this.currentConfigurationIndex));
			return true;
		}

		if(mouseX >= x + this.xSize / 2 + 8 && mouseX < x + this.xSize / 2 + 8 + 13
				&& mouseY >= y + this.ySize - 13 && mouseY < y + this.ySize - 13 + 7) {
			this.currentConfigurationIndex = Math.min(this.currentConfigurationIndex + 1, this.container.getBlueprint().getConfigurations().size() - 1);
			this.context.setConfiguration(this.container.getBlueprint().getConfigurations().get(this.currentConfigurationIndex));
			this.createGui();
			TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneWeavingTableConfiguration(this.context.getRuneIndex(), this.currentConfigurationIndex));
			return true;
		}

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
	public boolean onStartTokenLinking(IGuiRuneToken token, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public boolean onStartTokenUnlinking(IGuiRuneToken token, int mouseX, int mouseY) {
		return false;
	}

	@Override
	public IGuiRuneToken getInputToken(int tokenIndex) {
		return this.inputTokens.get(tokenIndex);
	}

	@Override
	public Collection<Token> getInputTokens() {
		return this.inputTokens;
	}

	@Override
	public IGuiRuneToken getOutputToken(int tokenIndex) {
		return this.outputTokens.get(tokenIndex);
	}

	@Override
	public Collection<Token> getOutputTokens() {
		return this.outputTokens;
	}

	@Override
	public int getMinX() {
		return this.menu == RuneMenuType.PRIMARY ? this.context.getRuneWeavingTableGui().getMinX() - 185 : this.context.getRuneWeavingTableGui().getMaxX() + 19;
	}

	@Override
	public int getMinY() {
		return this.context.getRuneWeavingTableGui().getMinY() + 70 - this.ySize / 2;
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

		this.mc.getTextureManager().bindTexture(GUI_RUNE_MENU);

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

		//Page left/right
		if(this.description.getPages().size() > 1) {
			if(this.currentDescriptionPageIndex > 0) {
				this.drawTexturedModalRect512(x + this.xSize - 6 - 40, y + 8, 452, 0, 13, 7);
			}
			if(this.currentDescriptionPageIndex < this.description.getPages().size() - 1) {
				this.drawTexturedModalRect512(x + this.xSize - 6 - 13, y + 8, 452, 29, 13, 7);
			}
		}

		//Configuration left/right
		if(this.hasMultipleConfigurations) {
			if(this.currentConfigurationIndex > 0) {
				this.drawTexturedModalRect512(x + this.xSize / 2 - 8 - 13, y + this.ySize - 13, 452, 0, 13, 7);
			}
			if(this.currentConfigurationIndex < this.container.getBlueprint().getConfigurations().size() - 1) {
				this.drawTexturedModalRect512(x + this.xSize / 2 + 8, y + this.ySize - 13, 452, 29, 13, 7);
			}
		}

		ItemStack stack = this.context.getRuneItemStack();

		ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, stack, x + 4, y + 4, 1, 1, 1, 1);

		//Page left/right number
		if(this.description.getPages().size() > 1) {
			this.fontRenderer.drawString(String.valueOf(this.currentDescriptionPageIndex + 1), x + this.xSize - 6 - 40 + 21 - this.fontRenderer.getStringWidth(String.valueOf(this.currentDescriptionPageIndex + 1)) / 2, y + 8, 0xFF3d3d3d);
		}

		//Configuration left/right
		if(this.hasMultipleConfigurations) {
			this.fontRenderer.drawString(String.valueOf(this.currentConfigurationIndex + 1), x + this.xSize / 2 + 1 - this.fontRenderer.getStringWidth(String.valueOf(this.currentConfigurationIndex + 1)) / 2, y + this.ySize - 13, 0xFF3d3d3d);
		}

		this.title.getPages().get(0).render(x + 4 + 20, y + 8);

		if(this.currentDescriptionPageIndex < this.description.getPages().size()) {
			this.description.getPages().get(this.currentDescriptionPageIndex).render(x + 4, y + 16 + this.title.getPages().get(0).getTextHeight());
		}


		//TODO Improve input/output tokens GUI
		int tokensYOffset = 0;

		if(!this.outputTokens.isEmpty()) {
			this.fontRenderer.drawString(I18n.format("rune.gui.outputs"), x + 4, y + 4 + this.ySize - 3 - 20 - this.additionalConfigurationsHeight, 0xFF3d3d3d);
			tokensYOffset += 20;
		}

		if(!this.inputTokens.isEmpty()) {
			this.fontRenderer.drawString(I18n.format("rune.gui.inputs"), x + 4, y + 4 + this.ySize - 3 - 20 - tokensYOffset - this.additionalConfigurationsHeight, 0xFF3d3d3d);
		}

		for(Token token : this.inputTokens) {
			this.drawToken(token, token.getCenterX(), token.getCenterY());
		}

		for(Token token : this.outputTokens) {
			this.drawToken(token, token.getCenterX(), token.getCenterY());
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

		for(Token token : this.inputTokens) {
			if(token.isInside(token.getCenterX(), token.getCenterY(), mouseX, mouseY)) {
				this.drawTokenTooltip(token, token.getCenterX(), token.getCenterY(), mouseX, mouseY);
			}
		}

		for(Token token : this.outputTokens) {
			if(token.isInside(token.getCenterX(), token.getCenterY(), mouseX, mouseY)) {
				this.drawTokenTooltip(token, token.getCenterX(), token.getCenterY(), mouseX, mouseY);
			}
		}

		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_RUNE_MENU);
	}

	public static void drawHangingRope(int updateCounter, float sx, float sy, float ex, float ey, float hang, double zLevel) {
		Minecraft mc = Minecraft.getMinecraft();

		mc.getTextureManager().bindTexture(GUI_RUNE_ROPE);

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
		float y2 = Math.max(y1, y3) + hang + (float)Math.sin((updateCounter + mc.getRenderPartialTicks()) / 25.0F) * 1.5f;

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

				buffer.pos(pxc2 - offX, pyc2 - offY, zLevel).tex(endU11, endV1).endVertex();
				buffer.pos(pxc1 - offX, pyc1 - offY, zLevel).tex(endU11, endV2).endVertex();
				buffer.pos(pxc2, pyc2, zLevel).tex(endU12, endV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(endU12, endV2).endVertex();
			}

			if(i > 0) {
				buffer.pos(pxc2, pyc2, zLevel).tex(u, ropeV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(u, ropeV2).endVertex();

				u += (float) Math.sqrt((x-px)*(x-px) + (y-py)*(y-py)) / 16.0F;
			}

			if(i == pieces) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2, pyc2, zLevel).tex(endU21, endV1).endVertex();
				buffer.pos(pxc1, pyc1, zLevel).tex(endU21, endV2).endVertex();
				buffer.pos(pxc2 + offX, pyc2 + offY, zLevel).tex(endU22, endV1).endVertex();
				buffer.pos(pxc1 + offX, pyc1 + offY, zLevel).tex(endU22, endV2).endVertex();
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

	protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font, @Nullable BiConsumer<Integer, Integer> renderer) {
		if (!textLines.isEmpty())
		{
			int mouseX = x;
			int mouseY = y;

			int screenWidth = this.width;
			int screenHeight = this.height;
			int maxTextWidth = -1;

			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();
			int tooltipTextWidth = 0;

			for (String textLine : textLines)
			{
				int textLineWidth = font.getStringWidth(textLine);

				if (textLineWidth > tooltipTextWidth)
				{
					tooltipTextWidth = textLineWidth;
				}
			}

			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth)
			{
				tooltipX = mouseX - 16 - tooltipTextWidth;
				if (tooltipX < 4) // if the tooltip doesn't fit on the screen
				{
					if (mouseX > screenWidth / 2)
					{
						tooltipTextWidth = mouseX - 12 - 8;
					}
					else
					{
						tooltipTextWidth = screenWidth - 16 - mouseX;
					}
					needsWrap = true;
				}
			}

			if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
			{
				tooltipTextWidth = maxTextWidth;
				needsWrap = true;
			}

			if (needsWrap)
			{
				int wrappedTooltipWidth = 0;
				List<String> wrappedTextLines = new ArrayList<String>();
				for (int i = 0; i < textLines.size(); i++)
				{
					String textLine = textLines.get(i);
					List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
					if (i == 0)
					{
						titleLinesCount = wrappedLine.size();
					}

					for (String line : wrappedLine)
					{
						int lineWidth = font.getStringWidth(line);
						if (lineWidth > wrappedTooltipWidth)
						{
							wrappedTooltipWidth = lineWidth;
						}
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textLines = wrappedTextLines;

				if (mouseX > screenWidth / 2)
				{
					tooltipX = mouseX - 16 - tooltipTextWidth;
				}
				else
				{
					tooltipX = mouseX + 12;
				}
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textLines.size() > 1)
			{
				tooltipHeight += (textLines.size() - 1) * 10;
				if (textLines.size() > titleLinesCount) {
					tooltipHeight += 2; // gap between title lines and next lines
				}
			}

			if (tooltipY < 4)
			{
				tooltipY = 4;
			}
			else if (tooltipY + tooltipHeight + 4 > screenHeight)
			{
				tooltipY = screenHeight - tooltipHeight - 4;
			}

			final int zLevel = 300;
			int backgroundColor = 0xF0100010;
			int borderColorStart = 0x505000FF;
			int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4, tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1, borderColorStart, borderColorStart);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3, tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

			int tooltipYStart = tooltipY;

			for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber)
			{
				String line = textLines.get(lineNumber);
				font.drawStringWithShadow(line, (float)tooltipX, (float)tooltipY, -1);

				if (lineNumber + 1 == titleLinesCount)
				{
					tooltipY += 2;
				}

				tooltipY += 10;
			}

			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();

			if(renderer != null) {
				renderer.accept(tooltipX - 4, tooltipYStart - 4);
			}
		}
	}

	public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor, int endColor)
	{
		float startAlpha = (float)(startColor >> 24 & 255) / 255.0F;
		float startRed   = (float)(startColor >> 16 & 255) / 255.0F;
		float startGreen = (float)(startColor >>  8 & 255) / 255.0F;
		float startBlue  = (float)(startColor       & 255) / 255.0F;
		float endAlpha   = (float)(endColor   >> 24 & 255) / 255.0F;
		float endRed     = (float)(endColor   >> 16 & 255) / 255.0F;
		float endGreen   = (float)(endColor   >>  8 & 255) / 255.0F;
		float endBlue    = (float)(endColor         & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(right,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.pos( left,    top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.pos( left, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
		buffer.pos(right, bottom, zLevel).color(  endRed,   endGreen,   endBlue,   endAlpha).endVertex();
		tessellator.draw();

		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}
}
