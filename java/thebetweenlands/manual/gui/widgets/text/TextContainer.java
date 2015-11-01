package thebetweenlands.manual.gui.widgets.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.EnumChatFormatting;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextFormat.EnumPushOrder;

public class TextContainer {
	public static class TextArea {
		public final int x, y, width, height;
		private final int additionalLeftWidth, additionalRightWidth;
		public TextArea(int x, int y, int width, int height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.additionalLeftWidth = 0;
			this.additionalRightWidth = 0;
		}
		private TextArea(int x, int y, int width, int height, int additionalLeftWidth, int additionalRightWidth) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.additionalLeftWidth = additionalLeftWidth;
			this.additionalRightWidth = additionalRightWidth;
		}
		public boolean isInside(int mouseX, int mouseY) {
			return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
		}
		public TextArea withSpace() {
			return new TextArea(this.x - this.additionalLeftWidth, this.y, this.width + this.additionalRightWidth + this.additionalLeftWidth, this.height);
		}
	}

	public static class TooltipArea extends TextArea {
		public final String text;
		public TooltipArea(int x, int y, int width, int height, String text) {
			super(x, y, width, height);
			this.text = text;
		}
		public TooltipArea(TextArea area, String text) {
			super(area.x, area.y, area.width, area.height);
			this.text = text;
		}
	}

	private static class TextFormatType {
		private final String type, argument;
		private final boolean closing;
		private TextFormatType(String type, String argument, boolean closing) {
			this.type = type;
			this.argument = argument;
			this.closing = closing;
		}
	}

	public static abstract class TextFormat {
		public static enum EnumPushOrder {
			FIRST, SECOND
		}

		public final String type;

		public TextFormat(String type) {
			this.type = type;
		}

		/**
		 * Called when the format is pushed on the stack.
		 * @param container Text container
		 * @param previous Previous format
		 * @param argument Format argument
		 * @param area Text area
		 */
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) { }

		/**
		 * Called for each word if this format is active
		 * @param container Text container
		 * @param area Text area
		 */
		void expand(TextContainer container, TextArea area) { }

		/**
		 * Called when the format is popped from the stack
		 * @param previous Previous format
		 */
		void pop(TextContainer container, TextFormat previous) { }

		/**
		 * Creates a new instance of this format
		 * @return
		 */
		abstract TextFormat create();

		/**
		 * Determines when {@link TextFormat#push(TextFormat, String)} should be called
		 * @return
		 */
		abstract EnumPushOrder getPushOrder();
	}

	private final int width, height, xOffset, yOffset;
	private final String unparsedText;

	public TextContainer(int xOffset, int yOffset, int width, int height, String unparsedText) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
		this.unparsedText = unparsedText;
	}

	private float currentScale = 1.0F;
	private int currentColor = 1;
	private String currentFormat = "";
	private List<EnumChatFormatting> textFormatList = new ArrayList<EnumChatFormatting>();
	private List<TooltipArea> tooltipAreas = new ArrayList<TooltipArea>();

	public TextContainer setCurrentScale(float scale) {
		this.currentScale = scale;
		return this;
	}

	public TextContainer setCurrentColor(int color) {
		this.currentColor = color;
		return this;
	}

	public TextContainer setCurrentFormat(String format) {
		this.currentFormat = format;
		return this;
	}

	public TextContainer addFormatting(EnumChatFormatting format) {
		this.textFormatList.add(format);
		return this;
	}

	public TextContainer removeFormatting(EnumChatFormatting format) {
		this.textFormatList.remove(format);
		return this;
	}

	public TextContainer addTooltipArea(TooltipArea area) {
		this.tooltipAreas.add(area);
		return this;
	}

	public TextContainer removeTooltipArea(TooltipArea area) {
		Iterator<TooltipArea> taIT = this.tooltipAreas.iterator();
		while(taIT.hasNext()) {
			TooltipArea ta = taIT.next();
			if(ta.x == area.x && ta.y == area.y && ta.width == area.width && ta.height == area.height && ta.text.equals(area.text)) {
				taIT.remove();	
			}
		}
		return this;
	}

	public List<TooltipArea> getTooltipAreas() {
		return this.tooltipAreas;
	}

	private Map<String, TextFormat> textFormatComponents = new HashMap<String, TextFormat>();
	private Map<String, Stack<TextFormat>> textFormatComponentStacks = new HashMap<String, Stack<TextFormat>>();
	private Map<Integer, List<String>> componentMap = new HashMap<Integer, List<String>>();
	private String parsedText = null;
	private Exception parserError = null;

	/**
	 * Registers a text format
	 * @param format
	 */
	public void registerFormat(TextFormat format) {
		this.textFormatComponents.put(format.type, format);
		this.getFormatStack(format.type, this.textFormatComponentStacks).push(format);
	}

	/**
	 * Builds and parses the text component
	 */
	public void parse() throws Exception {
		this.componentMap.clear();

		char[] textChars = this.unparsedText.toCharArray();
		StringBuilder parsedTextBuffer = new StringBuilder(textChars.length);
		StringBuilder textComponentBuffer = new StringBuilder();
		boolean componentBody = false;
		boolean wasSpace = false;
		boolean firstComponent = true;
		boolean firstWord = true;
		boolean wasFix = false;
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
			boolean wasFirstComponent = firstComponent;
			boolean wasFirstWord = firstWord;
			if(!componentBody) {
				if((currentChar == Character.SPACE_SEPARATOR || currentChar == ' ') && !wasSpace) {
					wasSpace = true;
				} else if(currentChar != Character.SPACE_SEPARATOR && currentChar != ' ' && wasSpace) {
					wasSpace = false;
					firstComponent = false;
					firstWord = false;
					wordIndex++;
					if(wasFirstWord && (textChars[i - 1] == Character.SPACE_SEPARATOR || textChars[i - 1] == ' ')) {
						wordIndex--;
					}
				}
			}
			if(currentChar == '>' && componentBody) {
				if(!wasSpace && parsedTextBuffer.length() > 0 && i + 1 < textChars.length && textChars[i + 1] != Character.SPACE_SEPARATOR && textChars[i + 1] != ' ') {
					parsedTextBuffer.append(" ");
					wasSpace = true;
				}
				componentBody = false;
				String textComponent = textComponentBuffer.toString();
				int mapWordIndex = wasFirstComponent ? wordIndex - 1 : wordIndex;
				List<String> componentList = componentMap.get(mapWordIndex);
				if(componentList == null) {
					componentList = new ArrayList<String>();
					componentMap.put(mapWordIndex, componentList);
				}
				componentList.add(textComponent);
				textComponentBuffer = new StringBuilder();
				firstComponent = false;
			}
		}
		this.parsedText = parsedTextBuffer.toString();
		this.verify();
	}

	private void verify() throws Exception {
		String currentStackType = null;
		int excpWordIndex = 0;
		try {
			String[] words = parsedText.split(" ");
			int wordIndex = 0;
			for(int i = 0; i < words.length; i++) {
				String word = words[i];
				if(word.length() > 0) {
					List<String> usedFormats = new ArrayList<String>();
					List<TextFormatType> wordComponentTypes = this.getTextFormatTypesForWord(componentMap, wordIndex);
					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = textFormatComponents.get(componentType.type);
						currentStackType = textFormat.type;
						usedFormats.add(textFormat.type);
						if(textFormat != null) {
							Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, textFormatComponentStacks);
							if(componentType.closing) {
								componentStacks.pop();
								componentStacks.peek();
							} else {
								if(textFormat.getPushOrder() == EnumPushOrder.FIRST) {
									TextFormat newFormat = textFormat.create();
									componentStacks.peek();
									componentStacks.push(newFormat);
								}
							}
						}
					}
					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = textFormatComponents.get(componentType.type);
						currentStackType = textFormat.type;
						usedFormats.add(textFormat.type);
						if(textFormat != null && textFormat.getPushOrder() == EnumPushOrder.SECOND) {
							Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, textFormatComponentStacks);
							if(!componentType.closing) {
								TextFormat newFormat = textFormat.create();
								componentStacks.peek();
								componentStacks.push(newFormat);
							}
						}
					}
					wordIndex++;
					excpWordIndex = wordIndex;
				}
			}
		} catch(Exception ex) {
			this.parserError = new Exception("Stack underflow. Stack type: " + currentStackType + " Word index: " + excpWordIndex, ex);
			throw this.parserError;
		}
		for(Entry<String, Stack<TextFormat>> e : this.textFormatComponentStacks.entrySet()) {
			if(e.getValue().size() > 1) {
				this.parserError = new Exception("Stack overflow. Stack type: " + e.getKey() + " Stack size: " + e.getValue().size() + " Word index: " + excpWordIndex);
				throw this.parserError;
			}
		}
	}

	/**
	 * Returns true if the parsing failed
	 * @return
	 */
	public boolean hasParsingFailed() {
		return this.parserError != null;
	}

	/**
	 * Renders the text
	 * @param mouseX Mouse X
	 * @param mouseY Mouse Y
	 */
	public void render(int mouseX, int mouseY) {
		if(this.hasParsingFailed()) {
			Minecraft.getMinecraft().fontRenderer.drawString("Failed parsing: " + this.parserError.getMessage(), this.xOffset, this.yOffset, 0xFFFF0000);
			return;
		}

		String[] words = parsedText.split(" ");
		int wordIndex = 0;
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int defaultSpaceWidth = fontRenderer.getStringWidth(" ");
		int fontHeight = fontRenderer.FONT_HEIGHT;
		int xOffsetMax = this.width;
		int xCursor = 0;
		int yCursor = 0;
		int lastWordXCursor = 0;
		int lastFontHeight = (int)(fontHeight * this.currentScale);
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.length() > 0) {
				List<String> usedFormats = new ArrayList<String>();
				List<TextFormatType> wordComponentTypes = this.getTextFormatTypesForWord(componentMap, wordIndex);
				for(TextFormatType componentType : wordComponentTypes) {
					TextFormat textFormat = textFormatComponents.get(componentType.type);
					usedFormats.add(textFormat.type);
					if(textFormat != null) {
						Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, textFormatComponentStacks);
						if(componentType.closing) {
							componentStacks.pop().pop(this, componentStacks.peek());
						} else {
							if(textFormat.getPushOrder() == EnumPushOrder.FIRST) {
								TextFormat newFormat = textFormat.create();
								newFormat.push(this, componentStacks.peek(), componentType.argument, null);
								componentStacks.push(newFormat);
							}
						}
					}
				}

				int spaceWidth = 0;

				if(this.textFormatList.size() > 0) {
					this.sortFormatList(this.textFormatList);
					StringBuilder formatStringBuilder = new StringBuilder();
					for(EnumChatFormatting format : this.textFormatList) {
						formatStringBuilder.append(format.toString());
					}
					String formatString = formatStringBuilder.toString();
					word = formatString + word;
					spaceWidth = fontRenderer.getStringWidth(formatString + " ");
				} else {
					spaceWidth = fontRenderer.getStringWidth(" ");
				}

				int strWidth = fontRenderer.getStringWidth(word);
				int renderStrWidth = (int) (strWidth * this.currentScale);
				int renderSpaceWidth = (int) (spaceWidth * this.currentScale);
				int renderStrHeight = (int) (fontHeight * this.currentScale);

				if(xCursor + renderStrWidth > xOffsetMax) {
					xCursor = 0;
					lastWordXCursor = 0;
					yCursor += lastFontHeight;
					lastFontHeight = renderStrHeight;
				}

				int renderX = (int) ((xOffset + xCursor) / this.currentScale);
				int renderY = (int) ((yOffset + yCursor) / this.currentScale);

				int additionalSpaceWidth = xCursor - lastWordXCursor;
				TextArea currentTextArea = new TextArea(this.xOffset + xCursor, this.yOffset + yCursor - 1, renderStrWidth, renderStrHeight + 1, additionalSpaceWidth, renderSpaceWidth - 1);

				for(TextFormatType componentType : wordComponentTypes) {
					TextFormat textFormat = textFormatComponents.get(componentType.type);
					usedFormats.add(textFormat.type);
					if(textFormat != null && textFormat.getPushOrder() == EnumPushOrder.SECOND) {
						Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, textFormatComponentStacks);
						if(!componentType.closing) {
							TextFormat newFormat = textFormat.create();
							newFormat.push(this, componentStacks.peek(), componentType.argument, currentTextArea);
							componentStacks.push(newFormat);
						}
					}
				}

				for(Stack<TextFormat> activeFormatStack : textFormatComponentStacks.values()) {
					if(activeFormatStack.size() > 1) {
						for(int c = 1; c < activeFormatStack.size(); c++) {
							TextFormat format = activeFormatStack.get(c);
							if(!usedFormats.contains(format.type)) {
								format.expand(this, currentTextArea);
							}
						}
					}
				}

				List<String> tooltipLines = new ArrayList<String>();
				for(TooltipArea ta : this.getTooltipAreas()) {
					if(ta.isInside(mouseX, mouseY)) {
						tooltipLines.add(ta.text);
					}
				}
				if(tooltipLines.size() > 0) {
					ManualWidgetsBase.renderTooltip(mouseX, mouseY, tooltipLines, 0xffffff, 0xf0100010);
				}

				GL11.glPushMatrix();
				GL11.glScalef(this.currentScale, this.currentScale, 1.0F);
				fontRenderer.drawString(word, renderX, renderY, this.currentColor);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();

				xCursor += renderStrWidth;
				xCursor += renderSpaceWidth;

				lastWordXCursor = xCursor;

				wordIndex++;
			} else {
				xCursor += defaultSpaceWidth;
			}
		}
	}

	private Stack<TextFormat> getFormatStack(String component, Map<String, Stack<TextFormat>> stackMap) {
		Stack<TextFormat> stack = stackMap.get(component);
		if(stack == null) {
			stack = new Stack<TextFormat>();
			stackMap.put(component, stack);
		}
		return stack;
	}

	private List<TextFormatType> getTextFormatTypesForWord(Map<Integer, List<String>> componentMap, int wordIndex) {
		List<TextFormatType> componentTypeList = new ArrayList<TextFormatType>();
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
					componentTypeList.add(new TextFormatType(componentType, argument, isClosing));
				}
			}
		}
		return componentTypeList;
	}

	private void sortFormatList(List<EnumChatFormatting> list) {
		Comparator<EnumChatFormatting> formatComparator = new Comparator<EnumChatFormatting>() {
			@Override
			public int compare(EnumChatFormatting f1, EnumChatFormatting f2) {
				if(f1.isColor() && f2.isColor()) {
					return 0;
				} else if(f1.isColor()) {
					return 1;
				} else {
					return -1;
				}
			}
		};
		Collections.sort(list, formatComparator);
	}
}
