package thebetweenlands.manual.gui.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.StatCollector;
import thebetweenlands.manual.gui.GuiManualBase;

/**
 * Created by Bart on 12-8-2015.
 */
public class TextWidget extends ManualWidgetsBase {
	private static class TooltipArea {
		private final int x, y, width, height;
		private final String text;
		private TooltipArea(int x, int y, int width, int height, String text) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.text = text;
		}
		private boolean isInside(int mouseX, int mouseY) {
			return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
		}
	}

	private static class TextComponentType {
		private final String type, argument;
		private final boolean closing;
		private TextComponentType(String type, String argument, boolean closing) {
			this.type = type;
			this.argument = argument;
			this.closing = closing;
		}
	}

	public String unparsedText;

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String unlocalizedText) {
		super(manual, xStart, yStart);
		this.unparsedText = StatCollector.translateToLocal(unlocalizedText);
	}

	public TextWidget(GuiManualBase manual, int xStart, int yStart, String text, boolean localized) {
		super(manual, xStart, yStart);
		this.unparsedText = localized ? text : StatCollector.translateToLocal(text);
	}


	@Override
	@SideOnly(Side.CLIENT)
	public void drawForeGround() {
		Stack<Float> scaleStack = new Stack<Float>();
		Stack<Integer> colorStack = new Stack<Integer>();
		Stack<TooltipArea> tooltipStack = new Stack<TooltipArea>();
		Map<Integer, List<String>> componentMap = new HashMap<Integer, List<String>>();
		char[] textChars = this.unparsedText.toCharArray();
		StringBuilder parsedTextBuffer = new StringBuilder(textChars.length);
		StringBuilder textComponentBuffer = new StringBuilder();
		boolean componentBody = false;
		boolean wasSpace = false;
		int wordIndex = 1;
		for(int i = 0; i < textChars.length; i++) {
			char currentChar = textChars[i];
			if(currentChar == '<') {
				componentBody = true;
			}
			if(!componentBody) {
				parsedTextBuffer.append(currentChar);
			} else if(currentChar != '<' && currentChar != '>') {
				textComponentBuffer.append(currentChar);
			}
			if(!componentBody) {
				if(currentChar == ' ' && !wasSpace) {
					wasSpace = true;
				} else if(currentChar != ' ' && wasSpace) {
					wasSpace = false;
					wordIndex++;
				}
			}
			if(currentChar == '>' && componentBody) {
				if(!wasSpace && parsedTextBuffer.length() > 0 && i + 1 < textChars.length && textChars[i + 1] != ' ') {
					parsedTextBuffer.append(" ");
					wasSpace = true;
				}
				componentBody = false;
				String textComponent = textComponentBuffer.toString();
				List<String> componentList = componentMap.get(wordIndex);
				if(componentList == null) {
					componentList = new ArrayList<String>();
					componentMap.put(wordIndex, componentList);
				}
				componentList.add(textComponent);
				textComponentBuffer = new StringBuilder();
			}
		}
		String parsedText = parsedTextBuffer.toString();
		String[] words = parsedText.split(" ");
		wordIndex = 0;
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int spaceWidth = fontRenderer.getStringWidth(" ");
		int fontHeight = fontRenderer.FONT_HEIGHT;
		int xOffset = 90;
		int yOffset = 25;
		int xOffsetMax = 120;
		int xCursor = 0;
		int yCursor = 0;
		float currentScale = 1.0F;
		scaleStack.push(currentScale);
		int currentColor = 0x808080;
		colorStack.push(currentColor);
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.length() > 0) {
				int strWidth = fontRenderer.getStringWidth(word);
				if(xCursor + strWidth > xOffsetMax) {
					xCursor = 0;
					yCursor += fontHeight;
				}

				List<TextComponentType> wordComponentTypes = this.getTextComponentTypesForWord(componentMap, wordIndex);

				for(TextComponentType componentType : wordComponentTypes) {
					if(componentType.type.equals("scale")) {
						if(!componentType.closing) {
							float scale = Float.parseFloat(componentType.argument);
							scaleStack.push(scale);
							currentScale = scale;
						} else {
							scaleStack.pop();
							currentScale = scaleStack.peek();
						}
					}
				}

				int renderX = (int) ((xOffset + xCursor) / currentScale);
				int renderY = (int) ((yOffset + yCursor) / currentScale);
				int renderStrWidth = (int) (strWidth * currentScale);
				int renderSpaceWidth = (int) (spaceWidth * currentScale);
				int renderStrHeight = (int) (fontHeight * currentScale);

				for(TextComponentType componentType : wordComponentTypes) {
					switch(componentType.type) {
					case "color":
						if(!componentType.closing) {
							int color = Integer.decode(componentType.argument);
							colorStack.push(color);
							currentColor = color;
						} else {
							colorStack.pop();
							currentColor = colorStack.peek();
						}
						break;
					case "tooltip":
						if(!componentType.closing) {
							tooltipStack.push(new TooltipArea(xOffset + xCursor, yOffset + yCursor, renderStrWidth, renderStrHeight, componentType.argument));
						} else {
							tooltipStack.pop();
						}
						break;
					}
				}

				if(tooltipStack.size() > 0) {
					Iterator<TooltipArea> tooltipIT = tooltipStack.iterator();
					List<String> tooltipLines = new ArrayList<String>();
					while(tooltipIT.hasNext()) {
						TooltipArea tooltip = tooltipIT.next();
						if(tooltip.isInside(this.mouseX, this.mouseY)) {
							tooltipLines.add(tooltip.text);
						}
					}
					ManualWidgetsBase.renderTooltip(this.mouseX, this.mouseY, tooltipLines, 0xffffff, 0xf0100010);
				}

				GL11.glPushMatrix();
				GL11.glScalef(currentScale, currentScale, 1.0F);
				fontRenderer.drawString(word, renderX, renderY, currentColor);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();

				xCursor += renderStrWidth;
				xCursor += renderSpaceWidth;

				wordIndex++;
			} else {
				xCursor += spaceWidth;
			}
		}
	}

	private List<TextComponentType> getTextComponentTypesForWord(Map<Integer, List<String>> componentMap, int wordIndex) {
		List<TextComponentType> componentTypeList = new ArrayList<TextComponentType>();
		List<String> textComponents = componentMap.get(wordIndex);
		if(textComponents != null) {
			for(String textComponent : textComponents) {
				if(textComponent != null) {
					String componentType = textComponent;
					String argument = null;
					if(textComponent.contains(":")) {
						componentType = textComponent.split(":")[0];
						argument = textComponent.split(":")[1];
					}
					boolean isClosing = false;
					if(componentType.startsWith("/")) {
						isClosing = true;
						componentType = componentType.substring(1, componentType.length());
					}
					componentTypeList.add(new TextComponentType(componentType, argument, isClosing));
				}
			}
		}
		return componentTypeList;
	}

	/*
        int widthLine = 0;
        if (text != null) {
            String[] words = text.split(" ");
            int lineNumber = 0;
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

            int tooltipWidth = 0;
            boolean makingTooltip = false;
            boolean makingTooltipWord = false;

            boolean italic = false;
            boolean bold = false;
            boolean underlined = false;
            boolean strikerThrough = false;
            boolean obfuscated = false;

            String toolTipWord = "";
            int tooltipStartX = 0;
            int tooltipStartY = 0;

            float scale = 1f;

            int height = fontRenderer.FONT_HEIGHT;

            for (String word : words) {
                int widthWord = (int)Math.ceil(fontRenderer.getStringWidth(word + " ") * scale);

                word = "" + (italic?EnumChatFormatting.ITALIC:"") + (bold?EnumChatFormatting.BOLD:"") + (underlined?EnumChatFormatting.UNDERLINE:"") + (strikerThrough?EnumChatFormatting.STRIKETHROUGH:"")  + (obfuscated?EnumChatFormatting.OBFUSCATED:"") + word + " ";
                if(word.contains("<end>")) {
                    tooltipWidth = 0;
                    makingTooltip = false;
                    makingTooltipWord = false;

                    italic = false;
                    bold = false;
                    underlined = false;
                    strikerThrough = false;
                    obfuscated = false;
                    word ="";
                    scale = 1f;
                } else if(makingTooltipWord) {
                    if(word.contains(">")){
                        makingTooltipWord = false;
                        toolTipWord += " " + word.replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    } else
                        toolTipWord += " " + word.replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    word = "";
                } else if (word.contains("/n/")) {
                    lineNumber++;
                    widthLine = 0;
                    word = "";
                } else if (word.contains("<color:")) {
                    color = Integer.decode(word.replace("<color:", "").replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, ""));
                    word = "";
                } else if (word.contains("</color>")) {
                    color = unchangedColor;
                    word = "";
                } else if (word.contains("<tooltip:")) {
                    if(!word.contains(">"))
                        makingTooltipWord = true;
                    toolTipWord = word.replace("<tooltip:", "").replace(">", "").replace(" ", "").replace("" + EnumChatFormatting.ITALIC, "").replace("" + EnumChatFormatting.BOLD, "").replace("" + EnumChatFormatting.UNDERLINE, "").replace("" + EnumChatFormatting.STRIKETHROUGH, "").replace("" + EnumChatFormatting.OBFUSCATED, "");
                    makingTooltip = true;
                    word = "";
                    tooltipStartX = xStart + widthLine;
                    tooltipStartY = yStart + lineNumber * 10;
                } else if (word.contains("</tooltip>")) {
                    makingTooltip = false;
                    word = "";
                } else  if (word.contains("<italic>")){
                    italic = true;
                    word = "";
                } else  if (word.contains("</italic>")){
                    italic = false;
                    word = "";
                } else  if (word.contains("<bold>")){
                    bold = true;
                    word = "";
                } else  if (word.contains("</bold>")){
                    bold = false;
                    word = "";
                }else  if (word.contains("<underlined>")){
                    underlined = true;
                    word = "";
                } else  if (word.contains("</underlined>")){
                    underlined = false;
                    word = "";
                }else  if (word.contains("<strikerThrough>")){
                    strikerThrough = true;
                    word = "";
                } else  if (word.contains("</strikerThrough>")){
                    strikerThrough = false;
                    word = "";
                } else  if (word.contains("<obfuscated>")){
                    obfuscated = true;
                    word = "";
                } else  if (word.contains("</obfuscated>")){
                    obfuscated = false;
                    word = "";
                } else if (word.contains("<scale:")){
                    scale = Float.parseFloat(word.replace("<scale:", "").replace(">", ""));
                    GL11.glScalef(scale, scale, scale);
                    height *= scale;
                    word = "";
                } else if (word.contains("</scale>")){
                    GL11.glScalef(1f, 1f, 1f);
                    height = fontRenderer.FONT_HEIGHT;
                    scale = 1f;
                    word = "";
                } else if (widthLine + widthWord <= GuiManualBase.WIDTH - unchangedXStart) {
                    widthLine += widthWord;
                    if (makingTooltip) {
                        tooltipWidth += widthWord;
                    }
                } else {
                    lineNumber++;
                    widthLine = widthWord;
                }

                fontRenderer.drawString(word, xStart + widthLine - widthWord, yStart + lineNumber * height, color);
                if (!makingTooltip && tooltipWidth > 0 && mouseX >= tooltipStartX && mouseX <= tooltipStartX + tooltipWidth && mouseY >= tooltipStartY && mouseY <= tooltipStartY + 9) {
                    ArrayList<String> toolTipText = new ArrayList<>();
                    toolTipText.add(toolTipWord);
                    ManualWidgetsBase.renderTooltip(mouseX, mouseY, toolTipText, 0xffffff, 0xf0100010);
                    tooltipWidth = 0;
                    toolTipWord = "";
                    tooltipStartX = 0;
                    tooltipStartY = 0;
                }
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glScalef(1f, 1f, 1f);
            }
        }
	 */

}
