package thebetweenlands.client.gui.book.widget.text;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.gui.book.HerbloreManualScreen;
import thebetweenlands.client.gui.book.widget.BookWidget;

public class TextWidget extends BookWidget {
	private TextContainer textContainer;
	private final Component text;
	private float scale = 1.0F;
	private int pageNumber = 0;
	private ResourceLocation font = Style.DEFAULT_FONT;

	public TextWidget(int x, int y, Component text) {
		super(x, y, 130 - x, 144);
		this.textContainer = new TextContainer(this.width, this.height, text, Style.DEFAULT_FONT);
		this.text = text;
		this.init();
	}

	public TextWidget(int x, int y, Component text, int pageNumber, int width, int height) {
		super(x, y, width, height);
		this.textContainer = new TextContainer(this.width, this.height, text, Style.DEFAULT_FONT);
		this.text = text;
		this.init();
		this.pageNumber = pageNumber;
	}

	public TextWidget(int x, int y, Component text, ResourceLocation font) {
		super(x, y, 130 - x, 144);
		this.text = text;
		this.font = font;
		this.textContainer = new TextContainer(this.width, this.height, text, font);
		this.init();
	}

	@Override
	public void setPageToRight() {
		super.setPageToRight();
		this.textContainer = new TextContainer(this.width, this.height, this.text, this.font);
		this.init();
	}

	public void init() {
		this.textContainer.setCurrentScale(this.scale).setCurrentColor(0x606060);
		this.textContainer.registerTag(new FormatTags.TagNewLine());
		this.textContainer.registerTag(new FormatTags.TagNewPage());
		this.textContainer.registerTag(new FormatTags.TagScale(1.0F));
		this.textContainer.registerTag(new FormatTags.TagColor(0x606060));
		this.textContainer.registerTag(new FormatTags.TagTooltip(Component.empty()));
		this.textContainer.registerTag(new FormatTags.TagSimple("bold", ChatFormatting.BOLD));
		this.textContainer.registerTag(new FormatTags.TagSimple("obfuscated", ChatFormatting.OBFUSCATED));
		this.textContainer.registerTag(new FormatTags.TagSimple("italic", ChatFormatting.ITALIC));
		this.textContainer.registerTag(new FormatTags.TagSimple("strikethrough", ChatFormatting.STRIKETHROUGH));
		this.textContainer.registerTag(new FormatTags.TagSimple("underline", ChatFormatting.UNDERLINE));
		this.textContainer.registerTag(new FormatTags.TagPagelink());
		this.textContainer.registerTag(new FormatTags.TagRainbow());
		this.textContainer.registerTag(new FormatTags.TagFont());

		try {
			this.textContainer.parse();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void setupWidget(HerbloreManualScreen screen) {
		super.setupWidget(screen);
		this.textContainer = new TextContainer(this.width, this.height, this.text, this.font);
		this.init();
	}

	@Override
	protected void renderBookWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		TextContainer.TextPage page = this.textContainer.getPages().get(this.pageNumber);
		page.render(graphics, this.getX(), this.getY());
		page.renderTooltips(graphics, this.getX(), this.getY(), mouseX, mouseY);
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return false;
	}

	public TextWidget setTextWidth(int width) {
		this.width = width;
		return this;
	}

	public TextWidget setTextHeight(int height) {
		this.height = height;
		return this;
	}
}
