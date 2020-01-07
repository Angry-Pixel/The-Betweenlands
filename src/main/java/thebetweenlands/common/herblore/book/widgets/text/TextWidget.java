package thebetweenlands.common.herblore.book.widgets.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagColor;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagFont;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagNewLine;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagNewPage;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagPagelink;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagRainbow;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagScale;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagSimple;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.TagTooltip;
import thebetweenlands.util.TranslationHelper;

@SideOnly(Side.CLIENT)
public class TextWidget extends ManualWidgetBase {
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
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
        this.text = I18n.format(unlocalizedText);
        if (!I18n.hasKey(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
    }

    @SideOnly(Side.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
        this.text = I18n.format(unlocalizedText);
        if (!I18n.hasKey(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
        this.pageNumber = pageNumber;
    }

    public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber, int width, int height) {
        super(xStart, yStart);
        this.width = width;
        this.height = height;
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
        this.text = I18n.format(unlocalizedText);
        if (!I18n.hasKey(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.init();
        this.pageNumber = pageNumber;
    }

    @SideOnly(Side.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText, float scale) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRenderer);
        this.text = I18n.format(unlocalizedText);
        if (!I18n.hasKey(unlocalizedText) || text.equals("")) {
        	TranslationHelper.addUnlocalizedString(unlocalizedText);
        }
        this.scale = scale;
        this.init();
    }

    @SideOnly(Side.CLIENT)
    public TextWidget(int xStart, int yStart, String text, boolean isLocalized) {
        super(xStart, yStart);
        this.text = isLocalized ? text : I18n.format(text);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRenderer);
        if (!isLocalized && (!I18n.hasKey(text) || text.equals(""))) {
        	TranslationHelper.addUnlocalizedString(text);
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
        this.textContainer.setCurrentScale(scale).setCurrentColor(0x606060);
        this.textContainer.registerTag(new TagNewLine());
        this.textContainer.registerTag(new TagNewPage());
        this.textContainer.registerTag(new TagScale(1.0F));
        this.textContainer.registerTag(new TagColor(0x606060));
        this.textContainer.registerTag(new TagTooltip("N/A"));
        this.textContainer.registerTag(new TagSimple("bold", TextFormatting.BOLD));
        this.textContainer.registerTag(new TagSimple("obfuscated", TextFormatting.OBFUSCATED));
        this.textContainer.registerTag(new TagSimple("italic", TextFormatting.ITALIC));
        this.textContainer.registerTag(new TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
        this.textContainer.registerTag(new TagSimple("underline", TextFormatting.UNDERLINE));
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
        TextContainer.TextPage page = this.textContainer.getPages().get(pageNumber);
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
