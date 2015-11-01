package thebetweenlands.manual.gui.widgets.text;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatColor;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatScale;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatSimple;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatTooltip;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
	private final TextContainer textContainer;

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText) {
		super(manual, xStart, yStart);
		this.textContainer = new TextContainer(this.xStart, this.yStart, 120, 0, StatCollector.translateToLocal(unlocalizedText));
		this.init();
	}

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String text, boolean localized) {
		super(manual, xStart, yStart);
		this.textContainer = new TextContainer(this.xStart, this.yStart, 120, 0, localized ? text : StatCollector.translateToLocal(text));
		this.init();
	}

	public void init() {
		this.textContainer.setCurrentScale(1.0F).setCurrentColor(0x808080).setCurrentFormat("");
		this.textContainer.registerFormat(new TextFormatScale(1.0F));
		this.textContainer.registerFormat(new TextFormatColor(0x808080));
		this.textContainer.registerFormat(new TextFormatTooltip("N/A"));
		this.textContainer.registerFormat(new TextFormatSimple("bold", EnumChatFormatting.BOLD));
		this.textContainer.registerFormat(new TextFormatSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
		this.textContainer.registerFormat(new TextFormatSimple("italic", EnumChatFormatting.ITALIC));
		this.textContainer.registerFormat(new TextFormatSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
		this.textContainer.registerFormat(new TextFormatSimple("underline", EnumChatFormatting.UNDERLINE));

		try {
			this.textContainer.parse();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeGround() {
		this.textContainer.render(this.mouseX, this.mouseY);
	}
}
