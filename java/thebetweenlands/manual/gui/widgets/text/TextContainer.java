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

import com.google.common.primitives.Booleans;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
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

		@Override
		public boolean equals(Object object) {
			if(object instanceof TextArea) {
				TextArea area = (TextArea) object;
				return area.x == this.x && area.y == this.y && area.width == this.width && area.height == this.height;
			}
			return false;
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

		@Override
		public boolean equals(Object object) {
			if(object instanceof TooltipArea) {
				TooltipArea area = (TooltipArea) object;
				return super.equals(area) && area.text == this.text;
			}
			return super.equals(object);
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

	public static class TextSegment {
		public final String text;
		public final int x, y, color;
		public final float scale;

		public TextSegment(String text, int x, int y, int color, float scale) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.color = color;
			this.scale = scale;
		}
	}

	private final int width, height, xOffset, yOffset;
	private final String unparsedText;
	private static final char DELIMITER = Character.MAX_VALUE;
	private float currentScale = 1.0F;
	private int currentColor = 1;
	private String currentFormat = "";
	private final List<EnumChatFormatting> textFormatList = new ArrayList<EnumChatFormatting>();
	private final List<TextArea> textAreas = new ArrayList<TextArea>();
	private final List<TextSegment> textSegments = new ArrayList<TextSegment>();
	private final Map<String, TextFormat> textFormatComponents = new HashMap<String, TextFormat>();
	private final Map<String, Stack<TextFormat>> textFormatComponentStacks = new HashMap<String, Stack<TextFormat>>();
	private final Map<Integer, List<String>> componentMap = new HashMap<Integer, List<String>>();
	private String parsedText = null;
	private Exception parserError = null;
	private String[] words;
	private boolean[] spaceIndices;

	public TextContainer(int xOffset, int yOffset, int width, int height, String unparsedText) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.width = width;
		this.height = height;
		this.unparsedText = unparsedText;
	}

	/**
	 * Returns the text segments that have been built after calling {@link TextContainer#build()}
	 * @return
	 */
	public List<TextSegment> getTextSegments() {
		return this.textSegments;
	}

	/**
	 * Sets the current text segment scale
	 * @param scale
	 * @return
	 */
	public TextContainer setCurrentScale(float scale) {
		this.currentScale = scale;
		return this;
	}

	/**
	 * Sets the current text segment color
	 * @param color
	 * @return
	 */
	public TextContainer setCurrentColor(int color) {
		this.currentColor = color;
		return this;
	}

	/**
	 * Sets the current text segment format
	 * @param format
	 * @return
	 */
	public TextContainer setCurrentFormat(String format) {
		this.currentFormat = format;
		return this;
	}

	/**
	 * Adds a chat format to the current text segment
	 * @param format
	 * @return
	 */
	public TextContainer addFormatting(EnumChatFormatting format) {
		this.textFormatList.add(format);
		return this;
	}

	/**
	 * Removes a chat format from the current text segment
	 * @param format
	 * @return
	 */
	public TextContainer removeFormatting(EnumChatFormatting format) {
		this.textFormatList.remove(format);
		return this;
	}

	/**
	 * Adds a text area
	 * @param area
	 * @return
	 */
	public TextContainer addTextArea(TextArea area) {
		this.textAreas.add(area);
		return this;
	}

	/**
	 * Removes a text area
	 * @param area
	 * @return
	 */
	public TextContainer removeTextArea(TextArea area) {
		Iterator<TextArea> taIT = this.textAreas.iterator();
		while(taIT.hasNext()) {
			TextArea ta = taIT.next();
			if(ta.equals(area)) {
				taIT.remove();	
			}
		}
		return this;
	}

	/**
	 * Returns a list of all text areas
	 * @return
	 */
	public List<TextArea> getTextAreas() {
		return this.textAreas;
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

	/**
	 * Registers a text format
	 * @param format
	 */
	public void registerFormat(TextFormat format) {
		this.textFormatComponents.put(format.type, format);
		this.getFormatStack(format.type, this.textFormatComponentStacks).push(format);
	}

	private boolean isDelimiter(char c) {
		return c == ' ' || c == Character.SPACE_SEPARATOR || c == DELIMITER;
	}

	/**
	 * Builds and parses the text component
	 */
	public void parse() throws Exception {
		this.componentMap.clear();

		char[] textChars = (this.unparsedText).toCharArray();
		StringBuilder parsedTextBuffer = new StringBuilder(textChars.length);
		StringBuilder textComponentBuffer = new StringBuilder();
		boolean componentBody = false;
		boolean wasSpace = false;
		boolean firstComponent = true;
		boolean firstWord = true;
		boolean wasFix = false;
		boolean startWithDelimiter = false;
		int wordIndex = 1;
		for(int i = 0; i < textChars.length; i++) {
			char currentChar = textChars[i];
			if(i == 0 && isDelimiter(currentChar)) {
				startWithDelimiter = true;
			}
			if(currentChar == '<') {
				componentBody = true;
			}
			if(!componentBody) {
				parsedTextBuffer.append(currentChar);
			} else if(currentChar != '<' && currentChar != '>') {
				textComponentBuffer.append(currentChar);
			}
			boolean wasFirstComponent = firstComponent;
			if(!componentBody) {
				if(isDelimiter(currentChar) && !wasSpace) {
					wasSpace = true;
				} else if(!isDelimiter(currentChar) && wasSpace) {
					wasSpace = false;
					firstComponent = false;
					wordIndex++;
					if(firstWord && isDelimiter(textChars[i - 1])) {
						wordIndex--;
					}
					firstWord = false;
				}
			}
			if(currentChar == '>' && componentBody) {
				if(!wasSpace && parsedTextBuffer.length() > 0 && i + 1 < textChars.length && !isDelimiter(textChars[i + 1])) {
					parsedTextBuffer.append(DELIMITER);
					wasSpace = true;
				}
				if(i + 1 >= textChars.length || (!isDelimiter(textChars[i + 1]) && textChars[i + 1] != '>' && textChars[i + 1] != '<' && wasFirstComponent)) {
					firstComponent = false;
				}
				componentBody = false;
				String textComponent = textComponentBuffer.toString();
				int mapWordIndex = (wasFirstComponent || startWithDelimiter ? wordIndex - 1 : wordIndex);
				List<String> componentList = componentMap.get(mapWordIndex);
				if(componentList == null) {
					componentList = new ArrayList<String>();
					componentMap.put(mapWordIndex, componentList);
				}
				componentList.add(textComponent);
				textComponentBuffer = new StringBuilder();
			}
		}
		this.parsedText = parsedTextBuffer.toString();
		this.words = this.getSplitWords(this.parsedText);
		this.spaceIndices = this.getSplitWordSpaces(this.parsedText);
		this.build();
	}

	private void build() throws Exception {
		String currentStackType = null;
		int excpWordIndex = 0;
		try {
			this.textAreas.clear();
			this.textSegments.clear();
			List<TextArea> tmpTextAreas = new ArrayList<TextArea>();
			//The END appendix is required to pop the last formats off the stack
			String[] words = this.getSplitWords(this.parsedText + " END");
			int wordIndex = 0;
			FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
			int defaultSpaceWidth = fontRenderer.getStringWidth(" ");
			int fontHeight = fontRenderer.FONT_HEIGHT;
			int xOffsetMax = this.width;
			int xCursor = 0;
			int yCursor = 0;
			int lastWordXCursor = 0;
			int currentFontHeight = 0;
			int lastFontHeight = -1;
			for(int i = 0; i < words.length; i++) {
				String word = words[i];
				if(word.length() > 0) {
					List<String> usedFormats = new ArrayList<String>();
					List<TextFormatType> wordComponentTypes = this.getTextFormatTypesForWord(this.componentMap, wordIndex);
					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = this.textFormatComponents.get(componentType.type);
						usedFormats.add(textFormat.type);
						if(textFormat != null) {
							Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, this.textFormatComponentStacks);
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

					currentFontHeight = renderStrHeight > currentFontHeight ? renderStrHeight : currentFontHeight;
					if(lastFontHeight == -1) {
						lastFontHeight = currentFontHeight;
					}

					if(i < this.spaceIndices.length && this.spaceIndices[i] && xCursor + renderStrWidth > xOffsetMax) {
						xCursor = 0;
						lastWordXCursor = 0;
						yCursor += lastFontHeight;
						lastFontHeight = currentFontHeight;
						currentFontHeight = 0;
					}

					int additionalSpaceWidth = i < this.spaceIndices.length && this.spaceIndices[i] ? (xCursor - lastWordXCursor) : 0;
					TextArea currentTextArea = new TextArea(this.xOffset + xCursor, this.yOffset + yCursor - 1, renderStrWidth, renderStrHeight + 1, additionalSpaceWidth, i < this.spaceIndices.length && this.spaceIndices[i] ? (renderSpaceWidth - 1) : -1);

					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = this.textFormatComponents.get(componentType.type);
						usedFormats.add(textFormat.type);
						if(textFormat != null && textFormat.getPushOrder() == EnumPushOrder.SECOND) {
							Stack<TextFormat> componentStacks = this.getFormatStack(componentType.type, this.textFormatComponentStacks);
							if(!componentType.closing) {
								TextFormat newFormat = textFormat.create();
								newFormat.push(this, componentStacks.peek(), componentType.argument, currentTextArea);
								componentStacks.push(newFormat);
							}
						}
					}

					for(Stack<TextFormat> activeFormatStack : this.textFormatComponentStacks.values()) {
						if(activeFormatStack.size() > 1) {
							for(int c = 1; c < activeFormatStack.size(); c++) {
								TextFormat format = activeFormatStack.get(c);
								if(!usedFormats.contains(format.type)) {
									format.expand(this, currentTextArea);
								}
							}
						}
					}

					for(TextArea ta : this.getTextAreas()) {
						if(!tmpTextAreas.contains(ta)) {
							tmpTextAreas.add(ta);
						}
					}

					//Ignore END appendix
					if(i < words.length - 1) {
						this.textSegments.add(new TextSegment(word, this.xOffset + xCursor, this.yOffset + yCursor, this.currentColor, this.currentScale));
					}

					xCursor += renderStrWidth;
					if(i < this.spaceIndices.length && this.spaceIndices[i]) xCursor += renderSpaceWidth;

					lastWordXCursor = xCursor;

					wordIndex++;
				} else {
					if(i < this.spaceIndices.length && this.spaceIndices[i]) xCursor += defaultSpaceWidth;
				}
			}
			this.textAreas.addAll(tmpTextAreas);
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

	private String[] getSplitWords(String parsedText) {
		String[] words = parsedText.split(" ");
		List<String> subWords = new ArrayList<String>();
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.indexOf(DELIMITER) >= 0) {
				String[] splitWords = word.split("" + DELIMITER);
				for(int c = 0; c < splitWords.length; c++) {
					subWords.add(splitWords[c]);
				}
			} else {
				subWords.add(word);
			}
		}
		return subWords.toArray(new String[0]);
	}

	private boolean[] getSplitWordSpaces(String parsedText) {
		String[] words = parsedText.split(" ");
		List<String> subWords = new ArrayList<String>();
		List<Boolean> spaceIndices = new ArrayList<Boolean>();
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.indexOf(DELIMITER) >= 0) {
				String[] splitWords = word.split("" + DELIMITER);
				for(int c = 0; c < splitWords.length; c++) {
					subWords.add(splitWords[c]);
					spaceIndices.add(false);
				}
			} else {
				subWords.add(word);
				spaceIndices.add(true);
			}
		}
		spaceIndices.add(false);
		return Booleans.toArray(spaceIndices);
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
	 */
	public void render() {
		if(this.hasParsingFailed()) {
			Minecraft.getMinecraft().fontRenderer.drawString("Failed parsing: " + this.parserError.getMessage(), this.xOffset, this.yOffset, 0xFFFF0000);
			return;
		}

		for(TextSegment segment : this.textSegments) {
			GL11.glPushMatrix();
			GL11.glScalef(segment.scale, segment.scale, 1.0F);
			Minecraft.getMinecraft().fontRenderer.drawString(segment.text, (int)(segment.x / segment.scale), (int)(segment.y / segment.scale), segment.color);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glPopMatrix();
		}
	}

	/**
	 * Renders the bounds of this container
	 */
	public void renderBounds() {
		Gui.drawRect(this.xOffset, this.yOffset, this.xOffset + this.width, this.yOffset + this.height, 0x80FF0000);
	}

	/**
	 * Renders all tooltips
	 */
	public void renderTooltips(int mouseX, int mouseY) {
		List<String> tooltipLines = new ArrayList<String>();
		for(TextArea ta : this.getTextAreas()) {
			if(ta instanceof TooltipArea) {
				if(ta.isInside(mouseX, mouseY)) {
					tooltipLines.add(((TooltipArea)ta).text);
				}
			}
		}
		if(tooltipLines.size() > 0) {
			ManualWidgetsBase.renderTooltip(mouseX, mouseY, tooltipLines, 0xffffff, 0xf0100010);
		}
	}
}
