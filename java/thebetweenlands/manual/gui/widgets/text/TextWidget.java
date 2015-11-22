package thebetweenlands.manual.gui.widgets.text;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextPage;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextSegment;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatColor;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatNewLine;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatPagelink;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatPagelink.PagelinkArea;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatRainbow;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatScale;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatSimple;
import thebetweenlands.manual.gui.widgets.text.TextFormatComponents.TextFormatTooltip;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
	private TextContainer textContainer;
	private String text;
	private float scale = 1.0f;

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText) {
		super(manual, xStart, yStart);
		this.textContainer = new TextContainer(116, 150, StatCollector.translateToLocal(/*"manual.text.test2"*/unlocalizedText));
		this.text = StatCollector.translateToLocal(unlocalizedText);
		this.init();
	}

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText, float scale) {
		super(manual, xStart, yStart);
		this.textContainer = new TextContainer(116, 150, StatCollector.translateToLocal(unlocalizedText));
		this.text = StatCollector.translateToLocal(unlocalizedText);
		this.scale = scale;
		this.init();
	}

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String text, boolean localized) {
		super(manual, xStart, yStart);
		this.textContainer = new TextContainer(116, 150, localized ? text : StatCollector.translateToLocal(text));
		this.text = localized ? text : StatCollector.translateToLocal(text);
		this.init();
	}

	@Override
	public void setPageToRight() {
		super.setPageToRight();
		this.textContainer = new TextContainer(116, 144, text);
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
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeGround() {
		//TODO: Implement proper page handling
		int pageOffset = 0;
		for(TextPage page : this.textContainer.getPages()) {
			int segmentIndex = 0;
			for(TextSegment segment : page.getSegments()) {
				segmentIndex++;
				GL11.glPushMatrix();
				GL11.glScalef(segment.scale, segment.scale, 1.0F);
				int color = segment.color;
				//Just a random test
				if(segment.getProperties(String.class).contains("rainbow")) {
					double time = (System.nanoTime() / 500000000.0D + segmentIndex / 2.0D);
					int red = (int)((Math.cos(time)+1.0D)/2.0D * 255);
					int green = (int)((Math.cos(time + Math.PI * 2.0D / 3.0D)+1.0D)/2.0D * 255);
					int blue = (int)((Math.cos(time + Math.PI * 4.0D / 3.0D)+1.0D)/2.0D * 255);
					color = (red << 16) | (green << 8) | (blue);
				}
				Minecraft.getMinecraft().fontRenderer.drawString(segment.text, MathHelper.ceiling_float_int((segment.x + this.xStart + pageOffset) / segment.scale), MathHelper.ceiling_float_int((segment.y + this.yStart) / segment.scale), (int) (color));
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
			}
			page.renderTooltips(this.xStart + pageOffset, this.yStart, mouseX, mouseY);
			//page.renderBounds(this.xStart + pageOffset, this.yStart);
			pageOffset += 148;
		}
	}

	@Override
	public void mouseClicked(int x, int y, int mouseButton) {
		super.mouseClicked(x, y, mouseButton);
		//TODO: Implement proper page handling
		int pageOffset = 0;
		for(TextPage page : this.textContainer.getPages()) {
			int pageX = this.xStart + pageOffset;
			int pageY = this.yStart;
			for(PagelinkArea area : page.getTextAreas(PagelinkArea.class)) {
				if(area.isInside(pageX, pageY, x, y)) {
					ManualEntry entry = this.manual.getEntryFromName(((PagelinkArea)area).page);
					if(entry != null) {
						this.manual.changeTo(entry);
						return;
					}
				}
			}
			pageOffset += 148;
		}
	}
}
