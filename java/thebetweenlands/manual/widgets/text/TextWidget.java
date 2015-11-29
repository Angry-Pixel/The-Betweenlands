package thebetweenlands.manual.widgets.text;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.manual.widgets.ManualWidgetsBase;
import thebetweenlands.manual.widgets.text.TextContainer.TextPage;
import thebetweenlands.manual.widgets.text.TextFormatComponents.*;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
    private TextContainer textContainer;
    private String text;
    private float scale = 1.0f;

    private int pageNumber = 0;

    public TextWidget(int xStart, int yStart, String unlocalizedText) {
        super(xStart, yStart);
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 150, StatCollector.translateToLocal(unlocalizedText));
        this.text = StatCollector.translateToLocal(unlocalizedText);
        if (!StatCollector.canTranslate(unlocalizedText))
            System.out.println("Needs translation: " + unlocalizedText);
        if (text.equals(""))
            System.out.println("The translation for: " + unlocalizedText + " is empty");
        this.init();
    }

    public TextWidget(int xStart, int yStart, String unlocalizedText, int pageNumber) {
        super(xStart, yStart);
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 150, StatCollector.translateToLocal(unlocalizedText));
        this.text = StatCollector.translateToLocal(unlocalizedText);
        if (!StatCollector.canTranslate(unlocalizedText))
            System.out.println("Needs translation: " + unlocalizedText);
        if (text.equals(""))
            System.out.println("The translation for: " + unlocalizedText + " is empty");
        this.init();
        this.pageNumber = pageNumber;
    }

    public TextWidget(int xStart, int yStart, String unlocalizedText, float scale) {
        super(xStart, yStart);
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 150, StatCollector.translateToLocal(unlocalizedText));
        this.text = StatCollector.translateToLocal(unlocalizedText);
        if (!StatCollector.canTranslate(unlocalizedText))
            System.out.println("Needs translation: " + unlocalizedText);
        if (text.equals(""))
            System.out.println("The translation for: " + unlocalizedText + " is empty");
        this.scale = scale;
        this.init();
    }

    public TextWidget(int xStart, int yStart, String text, boolean localized) {
        super(xStart, yStart);
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 150, localized ? text : StatCollector.translateToLocal(text));
        this.text = localized ? text : StatCollector.translateToLocal(text);
        this.init();
    }

    @Override
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 144, text);
        this.init();
    }

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
        //this.textContainer.renderBounds();
        //this.textContainer.render();
        //this.textContainer.renderTooltips(mouseX, mouseY);

        TextPage page = this.textContainer.getPages().get(pageNumber);
        page.render(this.xStart, this.yStart);
        page.renderTooltips(this.xStart, this.yStart, mouseX, mouseY);
        //page.renderBounds(this.xStart + pageOffset, this.yStart);
    }


    @Override
    public void resize() {
        super.resize();
        this.textContainer = new TextContainer(/*this.xStart, this.yStart, */116, 144, text);
        this.init();
    }
}
