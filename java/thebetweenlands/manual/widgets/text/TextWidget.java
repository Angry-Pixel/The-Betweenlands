package thebetweenlands.manual.widgets.text;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.manual.widgets.ManualWidgetsBase;
import thebetweenlands.manual.widgets.text.FormatTags.*;
import thebetweenlands.manual.widgets.text.TextContainer.TextPage;

/**
 * Created by Bart on 12-8-2015.
 */
@SideOnly(Side.CLIENT)
public class TextWidget extends ManualWidgetsBase {
	private TextContainer textContainer;
	private String text;
	private float scale = 1.0f;
	private int width;
	private int height;
	private int pageNumber = 0;

	@SideOnly(Side.CLIENT)
	public TextWidget(int xStart, int yStart, String unlocalizedText) {
		super(xStart, yStart);
		width = 130 - xStart;
		height = 144;
		this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
		this.text = StatCollector.translateToLocal(unlocalizedText);
		if (!StatCollector.canTranslate(unlocalizedText) || text.equals("")) {
			TheBetweenlands.unlocalizedNames.add(unlocalizedText);
		}
		this.init();
	}

	@SideOnly(Side.CLIENT)
	public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber) {
		super(xStart, yStart);
		width = 130 - xStart;
		height = 144;
		this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
		this.text = StatCollector.translateToLocal(unlocalizedText);
		if (!StatCollector.canTranslate(unlocalizedText) || text.equals("")) {
			TheBetweenlands.unlocalizedNames.add(unlocalizedText);
		}
		this.init();
		this.pageNumber = pageNumber;
	}

	public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber, int width, int height) {
		super(xStart, yStart);
		this.width = width;
		this.height = height;
		this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
		this.text = StatCollector.translateToLocal(unlocalizedText);
		if (!StatCollector.canTranslate(unlocalizedText) || text.equals("")) {
			TheBetweenlands.unlocalizedNames.add(unlocalizedText);
		}
		this.init();
		this.pageNumber = pageNumber;
	}

	@SideOnly(Side.CLIENT)
	public TextWidget(int xStart, int yStart, String unlocalizedText, float scale) {
		super(xStart, yStart);
		width = 130 - xStart;
		height = 144;
		this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
		this.text = StatCollector.translateToLocal(unlocalizedText);
		if (!StatCollector.canTranslate(unlocalizedText) || text.equals("")) {
			TheBetweenlands.unlocalizedNames.add(unlocalizedText);
		}
		this.scale = scale;
		this.init();
	}

	@SideOnly(Side.CLIENT)
	public TextWidget(int xStart, int yStart, String text, boolean isLocalized) {
		super(xStart, yStart);
		this.text = isLocalized ? text : StatCollector.translateToLocal(text);
		width = 130 - xStart;
		height = 144;
		this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRenderer);
		if (!isLocalized && (!StatCollector.canTranslate(text) || text.equals(""))) {
			TheBetweenlands.unlocalizedNames.add(text);
		}
		this.init();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setPageToRight() {
		super.setPageToRight();
		this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRenderer);
		this.init();
	}

	@SideOnly(Side.CLIENT)
	public void init() {
		this.textContainer.setCurrentScale(scale).setCurrentColor(0x808080);
		this.textContainer.registerTag(new TagNewLine());
		this.textContainer.registerTag(new TagNewPage());
		this.textContainer.registerTag(new TagScale(1.0F));
		this.textContainer.registerTag(new TagColor(0x808080));
		this.textContainer.registerTag(new TagTooltip("N/A"));
		this.textContainer.registerTag(new TagSimple("bold", EnumChatFormatting.BOLD));
		this.textContainer.registerTag(new TagSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
		this.textContainer.registerTag(new TagSimple("italic", EnumChatFormatting.ITALIC));
		this.textContainer.registerTag(new TagSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
		this.textContainer.registerTag(new TagSimple("underline", EnumChatFormatting.UNDERLINE));
		this.textContainer.registerTag(new TagPagelink());
		this.textContainer.registerTag(new TagRainbow());
		this.textContainer.registerTag(new TagFont());

		try {
			this.textContainer.parse();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeGround() {
		TextPage page = this.textContainer.getPages().get(pageNumber);
		page.render(this.xStart, this.yStart);
		page.renderTooltips(this.xStart, this.yStart, mouseX, mouseY);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void resize() {
		super.resize();
		this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRenderer);
		this.init();
	}


	public TextWidget setWidth(int width) {
		this.width = width;
		return this;
	}
}
