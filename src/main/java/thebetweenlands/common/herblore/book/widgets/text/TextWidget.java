package thebetweenlands.common.herblore.book.widgets.text;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;
import net.minecraft.client.resources.I18n;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags.*;
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
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRendererObj);
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
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRendererObj);
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
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRendererObj);
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
        this.textContainer = new TextContainer(width, height, I18n.format(unlocalizedText), Minecraft.getMinecraft().fontRendererObj);
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
        this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRendererObj);
        if (!isLocalized && (!I18n.hasKey(text) || text.equals(""))) {
        	TranslationHelper.addUnlocalizedString(text);
        }
        this.init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRendererObj);
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
        this.textContainer.registerTag(new TagSimple("bold", ChatFormatting.BOLD));
        this.textContainer.registerTag(new TagSimple("obfuscated", ChatFormatting.OBFUSCATED));
        this.textContainer.registerTag(new TagSimple("italic", ChatFormatting.ITALIC));
        this.textContainer.registerTag(new TagSimple("strikethrough", ChatFormatting.STRIKETHROUGH));
        this.textContainer.registerTag(new TagSimple("underline", ChatFormatting.UNDERLINE));
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
        this.textContainer = new TextContainer(width, height, text, Minecraft.getMinecraft().fontRendererObj);
        this.init();
    }


    public TextWidget setWidth(int width) {
        this.width = width;
        return this;
    }
}
