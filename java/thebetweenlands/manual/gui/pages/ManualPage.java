package thebetweenlands.manual.gui.pages;

import java.util.ArrayList;
import java.util.Collections;

import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;

/**
 * Created by Bart on 11-8-2015.
 */
public class ManualPage {
	public int x;
	public int y;
	public int pageNumber;

	public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();

	boolean rightPage = false;

	public ManualEntry entry;

	public ManualPage(ManualWidgetsBase... widgets) {
		Collections.addAll(this.widgets, widgets);
	}

	public ArrayList<ManualPage> setManualEntry(ManualEntry entry) {
		ArrayList<ManualPage> pages = new ArrayList<>();
		this.entry = entry;

		/*for (ManualWidgetsBase widget : widgets) {
            if (widget instanceof TextWidget) {
                FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                String[] words = ((TextWidget) widget).unparsedText.split(" ");

                int widthLine = 0;
                String text = "";
                int page = 0;

                int heightPage = 0;

                int textHeight = fontRenderer.FONT_HEIGHT;

                boolean makingItalic = false;
                boolean makingBold = false;
                boolean makingUnderlined = false;
                boolean makingStrikerThrough = false;
                boolean makingObfuscated = false;
                boolean makingColor = false;
                String color = "";
                boolean makingTooltip = false;
                String tooltip = "";
                boolean makingScaled = false;
                String scaleWord = "";
                float scale = 1f;


                for (String word : words) {
                    int widthWord = (int) Math.ceil(fontRenderer.getStringWidth(word + " ") * scale);


                    if (word.contains("/n/")) {
                        heightPage += textHeight;
                        widthLine = 0;
                    } else if (word.contains("<color:")) {
                        color = word;
                        makingColor = true;
                        widthLine += 0;
                    } else if (word.contains("</color>")) {
                        color = "";
                        makingColor = false;
                        widthLine += 0;
                    } else if (word.contains("<tooltip:")) {
                        tooltip = word;
                        makingTooltip = true;
                        widthLine += 0;
                    } else if (word.contains("</tooltip>")) {
                        tooltip = "";
                        makingTooltip = false;
                        widthLine += 0;
                    } else if (word.contains("<italic>")) {
                        makingItalic = true;
                        widthLine += 0;
                    } else if (word.contains("</italic>")) {
                        makingItalic = false;
                        widthLine += 0;
                    } else if (word.contains("<bold>")) {
                        makingBold = true;
                        widthLine += 0;
                    } else if (word.contains("</bold>")) {
                        makingBold = false;
                        widthLine += 0;
                    } else if (word.contains("<underlined>")) {
                        makingUnderlined = true;
                        widthLine += 0;
                    } else if (word.contains("</underlined>")) {
                        makingUnderlined = false;
                        widthLine += 0;
                    } else if (word.contains("<strikerThrough>")) {
                        makingStrikerThrough = true;
                        widthLine += 0;
                    } else if (word.contains("</strikerThrough>")) {
                        makingStrikerThrough = false;
                        widthLine += 0;
                    } else if (word.contains("<obfuscated>")) {
                        makingObfuscated = true;
                        widthLine += 0;
                    } else if (word.contains("</obfuscated>")) {
                        makingObfuscated = false;
                        widthLine += 0;
                    } else if (word.contains("<scale:")) {
                        makingScaled = true;
                        widthLine += 0;
                        scaleWord = word;
                        scale = Float.parseFloat(word.replace("<scale:", "").replace(">", ""));
                    } else if (word.contains("</scale>")) {
                        makingScaled = false;
                        scale = 1f;
                        widthLine += 0;
                        scaleWord = "";
                    } else if (widthLine + widthWord <= GuiManualBase.WIDTH - widget.unchangedXStart) {
                        widthLine += widthWord;
                    } else {
                        heightPage += fontRenderer.FONT_HEIGHT;
                        if (heightPage > GuiManualBase.HEIGHT - widget.yStart - 5) {
                            heightPage = 0;
                            if (page > 0)
                                pages.add(new ManualPage(new TextWidget(widget.manual, widget.unchangedXStart, widget.yStart, text)));
                            else
                                ((TextWidget) widget).unparsedText = text + " <end>";
                            page++;
                            text = "" + (makingScaled ? " " + scaleWord + " " : "") + (makingItalic ? " <italic>" : "") + (makingBold ? " <bold>" : "") + (makingColor ? " " + color : "") + (makingObfuscated ? " <obfuscated>" : "") + (makingStrikerThrough ? " <strikerThrough>" : "") + (makingTooltip ? " " + tooltip : "") + (makingUnderlined ? " <underlined>" : "");
                        }
                        widthLine = widthWord;
                    }
                    text += word + " ";
                }
                if (page > 0 && (widthLine > 0 || heightPage > 0))
                    pages.add(new ManualPage(new TextWidget(widget.manual, 5, 5, text)));
            }
        }*/
		return pages;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
		rightPage = pageNumber % 2 == 0;

		for (ManualWidgetsBase widget : widgets)
			if (rightPage)
				widget.setPageToRight();
	}


	public void clear() {
		widgets.clear();
	}

	public void draw(int mouseX, int mouseY) {
		for (ManualWidgetsBase widget : widgets)
			widget.draw(mouseX, mouseY);
	}

	public void keyTyped(char c, int key) {
		for (ManualWidgetsBase widget : widgets)
			widget.keyTyped(c, key);
	}

	public void mouseClicked(int x, int y, int button) {
		for (ManualWidgetsBase widget : widgets)
			widget.mouseClicked(x, y, button);
	}

	public void updateScreen() {
		for (ManualWidgetsBase widget : widgets)
			widget.updateScreen();
	}
}
