package thebetweenlands.manual.widgets.text;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.manual.widgets.ManualWidgetsBase;
import thebetweenlands.manual.widgets.text.TextContainer.TextPage;
import thebetweenlands.manual.widgets.text.TextFormatComponents.*;

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
    private boolean useCustomFont = false;

    @SideOnly(Side.CLIENT)
    public TextWidget(int xStart, int yStart, String unlocalizedText) {
        super(xStart, yStart);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), false);
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
        this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), false);
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
        this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), false);
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
        this.textContainer = new TextContainer(width, height, StatCollector.translateToLocal(unlocalizedText), false);
        this.text = StatCollector.translateToLocal(unlocalizedText);
        if (!StatCollector.canTranslate(unlocalizedText) || text.equals("")) {
            TheBetweenlands.unlocalizedNames.add(unlocalizedText);
        }
        this.scale = scale;
        this.init();
    }

    @SideOnly(Side.CLIENT)
    public TextWidget(int xStart, int yStart, String text, boolean useCustomFont, boolean isLocalized) {
        super(xStart, yStart);
        this.text = isLocalized ? text : StatCollector.translateToLocal(text);
        width = 130 - xStart;
        height = 144;
        this.textContainer = new TextContainer(width, height, text, useCustomFont);
        if (!isLocalized && (!StatCollector.canTranslate(text) || text.equals(""))) {
            TheBetweenlands.unlocalizedNames.add(text);
        }
        this.useCustomFont = useCustomFont;
        this.init();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(width, height, text, useCustomFont);
        this.init();
    }

    @SideOnly(Side.CLIENT)
    public void init() {
        this.textContainer.setCurrentScale(scale).setCurrentColor(0x808080).setCurrentFormat("");
        this.textContainer.registerFormat(new TextFormatNewLine());
        this.textContainer.registerFormat(new TextFormatScale(1.0F));
        this.textContainer.registerFormat(new TextFormatColor(0x808080));
        this.textContainer.registerFormat(new TextFormatTooltip("N/A"));
        this.textContainer.registerFormat(new TextFormatSimple("bold", EnumChatFormatting.BOLD));
        this.textContainer.registerFormat(new TextFormatSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
        this.textContainer.registerFormat(new TextFormatSimple("italic", EnumChatFormatting.ITALIC));
        this.textContainer.registerFormat(new TextFormatSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
        this.textContainer.registerFormat(new TextFormatSimple("underline", EnumChatFormatting.UNDERLINE));
        this.textContainer.registerFormat(new TextFormatPagelink());
        this.textContainer.registerFormat(new TextFormatRainbow());

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
        this.textContainer = new TextContainer(width, height, text, useCustomFont);
        this.init();
    }
}
