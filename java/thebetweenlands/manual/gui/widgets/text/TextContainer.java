package thebetweenlands.manual.gui.widgets.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
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
import net.minecraft.util.MathHelper;
import thebetweenlands.manual.gui.widgets.ManualWidgetsBase;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextFormat.EnumPushOrder;

public class TextContainer {
	public static class TextArea {
		public final TextPage page;
		public int x, y, width, height;
		private int additionalLeftWidth, additionalRightWidth;
		private List<Object> properties = new ArrayList<Object>();

		protected TextArea(TextPage page, int x, int y, int width, int height) {
			this.page = page;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.additionalLeftWidth = 0;
			this.additionalRightWidth = 0;
		}

		protected TextArea(TextArea area) {
			this(area.page, area.x, area.y, area.width, area.height, area.additionalLeftWidth, area.additionalRightWidth, area.properties);
		}

		private TextArea(TextPage page, int x, int y, int width, int height, List<Object> properties) {
			this(page, x, y, width, height);
			this.additionalLeftWidth = 0;
			this.additionalRightWidth = 0;
			this.properties = properties;
		}

		private TextArea(TextPage page, int x, int y, int width, int height, int additionalLeftWidth, int additionalRightWidth) {
			this(page, x, y, width, height);
			this.additionalLeftWidth = additionalLeftWidth;
			this.additionalRightWidth = additionalRightWidth;
		}

		private TextArea(TextPage page, int x, int y, int width, int height, int additionalLeftWidth, int additionalRightWidth, List<Object> properties) {
			this(page, x, y, width, height, additionalLeftWidth, additionalRightWidth);
			this.properties = properties;
		}

		/**
		 * Checks whether the specified point is inside this text area
		 * @param offsetX Offset X
		 * @param offsetY Offset Y
		 * @param mouseX Mouse X
		 * @param mouseY Mouse Y
		 * @return
		 */
		public boolean isInside(int offsetX, int offsetY, int mouseX, int mouseY) {
			return mouseX >= x + offsetX && mouseX < x + offsetX + width && mouseY >= y + offsetY && mouseY < y + offsetY + height;
		}

		/**
		 * Sets the bounds of this text area
		 * @param area
		 * @return
		 */
		public TextArea setBounds(TextArea area) {
			this.x = area.x;
			this.y = area.y;
			this.width = area.width;
			this.height = area.height;
			this.additionalLeftWidth = area.additionalLeftWidth;
			this.additionalRightWidth = area.additionalRightWidth;
			return this;
		}

		/**
		 * Returns this text area with space padding applied
		 * @return
		 */
		public TextArea withSpace() {
			return new TextArea(this.page, this.x - this.additionalLeftWidth, this.y, this.width + this.additionalRightWidth + this.additionalLeftWidth, this.height);
		}

		@Override
		public boolean equals(Object object) {
			if(object instanceof TextArea) {
				TextArea area = (TextArea) object;
				return area.page.equals(this.page) && area.x == this.x && area.y == this.y && area.width == this.width && area.height == this.height;
			}
			return false;
		}

		/**
		 * Adds a property to this text area
		 * @param obj Property
		 */
		public void addProperty(Object obj) {
			this.properties.add(obj);
		}

		/**
		 * Removes the specified property from this text area
		 * @param obj Object to remove
		 * @return
		 */
		public boolean removeProperty(Object obj) {
			return this.properties.remove(obj);
		}

		/**
		 * Returns a list of all properties of this text area
		 * @return
		 */
		public List<Object> getProperties() {
			return this.properties;
		}

		/**
		 * Returns a list of all properties of the specified type of this text area
		 * @param type Property class
		 * @return
		 */
		public <T> List<T> getProperties(Class<T> type) {
			List<T> lst = new ArrayList<T>();
			for(Object obj : this.properties) {
				if(type.isAssignableFrom(obj.getClass())) {
					lst.add((T) obj);
				}
			}
			return lst;
		}
	}

	public static class TooltipArea extends TextArea {
		public final String text;

		public TooltipArea(TextArea area, String text) {
			super(area);
			this.text = text;
		}

		@Override
		public boolean equals(Object object) {
			if(object instanceof TooltipArea) {
				TooltipArea area = (TooltipArea) object;
				return super.equals(area) && area.text.equals(this.text);
			}
			return false;
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
		 * Called when the format is pushed.
		 * @param container Text container
		 * @param previous Previous format
		 * @param argument Format argument
		 * @param area Text area
		 */
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) { }

		/**
		 * Creates a new instance of this format
		 * @return
		 */
		abstract TextFormat create();

		/**
		 * Determines when {@link TextFormatTag#push(TextFormatTag, String)} should be called
		 * @return
		 */
		abstract EnumPushOrder getPushOrder();
	}

	public static abstract class TextFormatTag extends TextFormat {
		public TextFormatTag(String type) {
			super(type);
		}

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
		void pop(TextContainer container, TextFormatTag previous) { }
	}

	public static class TextSegment extends TextArea {
		public final String text;
		public final int color;
		public final float scale;

		public TextSegment(TextPage page, String text, int x, int y, int width, int height, int color, float scale) {
			super(page, x, y, width, height);
			this.text = text;
			this.color = color;
			this.scale = scale;
		}

		private TextSegment(TextPage page, String text, int x, int y, int width, int height, int color, float scale, int additionalLeftWidth, int additionalRightWidth, List<Object> properties) {
			super(page, x, y, width, height, additionalLeftWidth, additionalRightWidth, properties);
			this.text = text;
			this.color = color;
			this.scale = scale;
		}
	}

	public static class TextPage {
		private final List<TextSegment> textSegments = new ArrayList<TextSegment>();
		private final List<TextArea> textAreas = new ArrayList<TextArea>();

		public final int width, height;

		private TextPage(int width, int height) { 
			this.width = width;
			this.height = height;
		}

		/**
		 * Returns a list of all text segmetns on this page
		 * @return
		 */
		public List<TextSegment> getSegments() {
			return this.textSegments;
		}

		/**
		 * Returns a list of all text areas on this page
		 * @return
		 */
		public List<TextArea> getTextAreas() {
			return this.textAreas;
		}

		/**
		 * Returns a list of all text areas of the specified type on this page
		 * @param type Type class
		 * @return
		 */
		public <T extends TextArea> List<T> getTextAreas(Class<T> type) {
			List<T> lst = new ArrayList<T>();
			for(TextArea area : this.textAreas) {
				if(type.isAssignableFrom(area.getClass())) {
					lst.add((T) area);
				}
			}
			return lst;
		}

		/**
		 * Renders this page
		 * @param x Page X
		 * @param y Page Y
		 */
		public void render(int x, int y) {
			for(TextSegment segment : this.textSegments) {
				GL11.glPushMatrix();
				GL11.glScalef(segment.scale, segment.scale, 1.0F);
				Minecraft.getMinecraft().fontRenderer.drawString(segment.text, MathHelper.ceiling_float_int((segment.x + x) / segment.scale), MathHelper.ceiling_float_int((segment.y + y) / segment.scale), segment.color);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
			}
		}

		/**
		 * Renders the bounds of this page
		 * @param x Page X
		 * @param y Page Y
		 */
		public void renderBounds(int x, int y) {
			Gui.drawRect(x, y, this.width + x, this.height + y, 0x80FF0000);
			for(TextSegment segment : this.textSegments) {
				GL11.glPushMatrix();
				Gui.drawRect(segment.withSpace().x + x, segment.y + y, segment.withSpace().x + segment.withSpace().width + x, segment.y + segment.height + y, 0x400000FF);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPopMatrix();
			}
			for(TextArea ta : this.getTextAreas()) {
				if(ta instanceof TooltipArea) {
					Gui.drawRect(ta.x + x, ta.y + y, ta.x + ta.width + x, ta.y + ta.height + y, 0x6000FF00);
				}
			}
		}

		/**
		 * Renders all tooltips
		 * @param x Page X
		 * @param y Page Y
		 * @param mouseX Mouse X
		 * @param mouseY Mouse Y
		 */
		public void renderTooltips(int x, int y, int mouseX, int mouseY) {
			List<String> tooltipLines = new ArrayList<String>();
			for(TextArea ta : this.getTextAreas()) {
				if(ta instanceof TooltipArea) {
					if(ta.isInside(x, y, mouseX, mouseY)) {
						tooltipLines.add(((TooltipArea)ta).text);
					}
				}
			}
			if(tooltipLines.size() > 0) {
				ManualWidgetsBase.renderTooltip(mouseX, mouseY, tooltipLines, 0xffffff, 0xf0100010);
			}
		}
	}

	private final int width, height;
	private final String unparsedText;
	private static final char DELIMITER = Character.MAX_VALUE;
	private float currentScale = 1.0F;
	private int currentColor = 1;
	private String currentFormat = "";
	private final List<EnumChatFormatting> textFormatList = new ArrayList<EnumChatFormatting>();
	private final List<TextPage> textPages = new ArrayList<TextPage>();
	private TextPage currentPage;
	private final Map<String, TextFormat> textFormatComponents = new HashMap<String, TextFormat>();
	private final Map<String, Stack<TextFormatTag>> textFormatComponentStacks = new HashMap<String, Stack<TextFormatTag>>();
	private final Map<Integer, List<String>> componentMap = new HashMap<Integer, List<String>>();
	private String parsedText = null;
	private Exception parserError = null;
	private String[] words;
	private boolean[] spaceIndices;
	private int nextLine = 0;

	public TextContainer(int width, int height, String unparsedText) {
		this.width = width;
		this.height = height;
		this.unparsedText = unparsedText;
	}

	/**
	 * Returns the text pages
	 * @return
	 */
	public List<TextPage> getPages() {
		return this.textPages;
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
	 * Adds a text area to the current page
	 * @param area
	 * @return
	 */
	public TextContainer addTextArea(TextArea area) {
		this.currentPage.textAreas.add(area);
		return this;
	}

	/**
	 * Removes a text area from all pages
	 * @param area
	 * @return
	 */
	public TextContainer removeTextArea(TextArea area) {
		for(TextPage page : this.textPages) {
			Iterator<TextArea> taIT = page.textAreas.iterator();
			while(taIT.hasNext()) {
				TextArea ta = taIT.next();
				if(ta.equals(area)) {
					taIT.remove();	
				}
			}
		}
		return this;
	}

	private Stack<TextFormatTag> getFormatStack(String component, Map<String, Stack<TextFormatTag>> stackMap) {
		Stack<TextFormatTag> stack = stackMap.get(component);
		if(stack == null) {
			stack = new Stack<TextFormatTag>();
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
	}

	private boolean isDelimiter(char c) {
		return c == ' ' || c == Character.SPACE_SEPARATOR || c == DELIMITER;
	}

	/**
	 * Builds and parses the text component
	 */
	public void parse() throws Exception {
		this.componentMap.clear();

		char[] textChars = ("START " + this.unparsedText + " END").toCharArray();
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
				int mapWordIndex = /*(wasFirstComponent || startWithDelimiter ? wordIndex - 1 : */wordIndex/*)*/;
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
			this.textFormatList.clear();
			this.textFormatComponentStacks.clear();
			for(Entry<String, TextFormat> e : this.textFormatComponents.entrySet()) {
				TextFormat format = e.getValue();
				if(format instanceof TextFormatTag) this.getFormatStack(format.type, this.textFormatComponentStacks).push((TextFormatTag) format);
			}
			this.textPages.clear();
			this.textPages.add(this.currentPage = new TextPage(this.width, this.height));
			List<TextArea> tmpTextAreas = new ArrayList<TextArea>();
			List<TextArea> combinedAreas = new ArrayList<TextArea>();
			TextSegment offsetSegment = null;
			String[] words = this.getSplitWords(this.parsedText);
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
			int prevOffsetHeight = 0;
			int prevPrevOffsetHeight = 0;
			boolean wasSeperateWord = false;
			for(int i = 0; i < words.length; i++) {
				String word = words[i];
				boolean isSeperateWord = i < this.spaceIndices.length && this.spaceIndices[i];
				boolean isNextSeperateWord = i + 1 < this.spaceIndices.length && this.spaceIndices[i + 1];
				if(word.length() > 0) {
					List<String> usedFormats = new ArrayList<String>();
					List<TextFormatType> wordComponentTypes = this.getTextFormatTypesForWord(this.componentMap, wordIndex);
					excpWordIndex = wordIndex;
					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = this.textFormatComponents.get(componentType.type);
						if(textFormat == null) throw new Exception("Unknown format: " + componentType.type + " Word index: " + excpWordIndex);
						usedFormats.add(textFormat.type);
						if(textFormat != null) {
							Stack<TextFormatTag> componentStacks = this.getFormatStack(componentType.type, this.textFormatComponentStacks);
							if(componentType.closing) {
								componentStacks.pop().pop(this, componentStacks.peek());
							} else {
								if(textFormat.getPushOrder() == EnumPushOrder.FIRST) {
									TextFormat newFormat = textFormat.create();
									newFormat.push(this, newFormat instanceof TextFormatTag ? componentStacks.peek() : null, componentType.argument, null);
									if(newFormat instanceof TextFormatTag) componentStacks.push((TextFormatTag)newFormat);
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
					int renderStrWidth = MathHelper.floor_float(strWidth * this.currentScale);
					int renderSpaceWidth = MathHelper.floor_float(spaceWidth * this.currentScale);
					int renderStrHeight = MathHelper.floor_float(fontHeight * this.currentScale);

					boolean nextLine = (xCursor + renderStrWidth > xOffsetMax) || this.nextLine > 0;

					int prevCurrentFontHeight = currentFontHeight;

					if(nextLine && lastFontHeight == -1) {
						lastFontHeight = currentFontHeight;
					}

					currentFontHeight = renderStrHeight > currentFontHeight ? renderStrHeight : currentFontHeight;

					if(isSeperateWord && !(xCursor > xOffsetMax)) {
						combinedAreas.clear();
					}

					//This offsets combined words to a new line if necessary
					int nextLastFontHeight = lastFontHeight;
					boolean jumped = false;
					if(isSeperateWord && nextLine && !wasSeperateWord && combinedAreas.size() > 0) {
						TextArea firstSegment = offsetSegment;
						offsetSegment = null;
						int offsetX = -firstSegment.x;
						int offsetY = yCursor - firstSegment.y + prevCurrentFontHeight;
						int maxHeight = 0;
						for(TextArea segment : combinedAreas) {
							segment.x += offsetX;
							segment.y += offsetY;
							if(segment.height > maxHeight) maxHeight = segment.height;
						}
						TextArea lastSegment = combinedAreas.get(combinedAreas.size() - 1);
						xCursor = lastSegment.x + lastSegment.withSpace().width;
						lastWordXCursor = xCursor;
						yCursor += prevCurrentFontHeight * (this.nextLine > 0 ? this.nextLine : 1);
						lastFontHeight = currentFontHeight;
						if(yCursor + currentFontHeight >= this.height) {
							xCursor = 0;
							yCursor = 0;
							this.textPages.add(this.currentPage = new TextPage(this.width, this.height));
						}
						currentFontHeight = 0;
						combinedAreas.clear();
						nextLine = xCursor + renderStrWidth > xOffsetMax;
						jumped = true;
						this.nextLine = 0;
					}

					if(isSeperateWord && nextLine) {
						xCursor = 0;
						lastWordXCursor = 0;
						yCursor += lastFontHeight * (this.nextLine > 0 ? this.nextLine : 1);
						lastFontHeight = currentFontHeight;
						if(yCursor + currentFontHeight >= this.height) {
							xCursor = 0;
							yCursor = 0;
							this.textPages.add(this.currentPage = new TextPage(this.width, this.height));
						}
						currentFontHeight = 0;
						this.nextLine = 0;
					}

					if(jumped) lastFontHeight = nextLastFontHeight;

					int additionalSpaceWidth = isSeperateWord ? (xCursor - lastWordXCursor) : 0;
					TextArea currentTextArea = new TextArea(this.currentPage, xCursor, yCursor, renderStrWidth, renderStrHeight, additionalSpaceWidth, isNextSeperateWord || isSeperateWord ? renderSpaceWidth : 0);

					for(TextFormatType componentType : wordComponentTypes) {
						TextFormat textFormat = this.textFormatComponents.get(componentType.type);
						if(textFormat == null) throw new Exception("Unknown format: " + componentType.type + " Word index: " + excpWordIndex);
						usedFormats.add(textFormat.type);
						if(textFormat != null && textFormat.getPushOrder() == EnumPushOrder.SECOND) {
							Stack<TextFormatTag> componentStacks = this.getFormatStack(componentType.type, this.textFormatComponentStacks);
							if(!componentType.closing) {
								TextFormat newFormat = textFormat.create();
								newFormat.push(this, newFormat instanceof TextFormatTag ? componentStacks.peek() : null, componentType.argument, currentTextArea);
								if(newFormat instanceof TextFormatTag) componentStacks.push((TextFormatTag) newFormat);
							}
						}
					}

					for(Stack<TextFormatTag> activeFormatStack : this.textFormatComponentStacks.values()) {
						if(activeFormatStack.size() > 1) {
							for(int c = 1; c < activeFormatStack.size(); c++) {
								TextFormatTag format = activeFormatStack.get(c);
								if(!usedFormats.contains(format.type)) {
									format.expand(this, currentTextArea);
								}
							}
						}
					}

					for(TextArea ta : this.currentPage.textAreas) {
						if(!tmpTextAreas.contains(ta)) {
							if(!isSeperateWord) combinedAreas.add(ta);
							tmpTextAreas.add(ta);
						}
					}

					prevPrevOffsetHeight = prevOffsetHeight;

					//Ignore STARD and END appendix
					if(i != 0 && i < words.length - 1) {
						TextSegment segment = new TextSegment(currentTextArea.page, word, xCursor, yCursor, currentTextArea.width, currentTextArea.height, this.currentColor, this.currentScale, currentTextArea.additionalLeftWidth, currentTextArea.additionalRightWidth, currentTextArea.properties);
						if(!isSeperateWord) {
							if(offsetSegment == null) {
								prevOffsetHeight = prevCurrentFontHeight;
								offsetSegment = segment;
							}
							combinedAreas.add(segment);
						}
						//this.textSegments.add(segment);
						this.currentPage.textSegments.add(segment);

						xCursor += renderStrWidth;
						if(isNextSeperateWord || isSeperateWord) xCursor += renderSpaceWidth;

						lastWordXCursor = xCursor;
					}

					wordIndex++;
				} else {
					if(isNextSeperateWord || isSeperateWord) xCursor += defaultSpaceWidth;
				}
				wasSeperateWord = isSeperateWord;
			}
			for(TextArea area : tmpTextAreas) {
				area.page.textAreas.add(area);
			}
		} catch(Exception ex) {
			if(ex instanceof EmptyStackException) {
				this.parserError = new Exception("Stack underflow. Stack type: " + currentStackType + " Word index: " + excpWordIndex, ex);
				throw this.parserError;
			}
			throw ex;
		}
		for(Entry<String, Stack<TextFormatTag>> e : this.textFormatComponentStacks.entrySet()) {
			if(e.getValue().size() > 1) {
				this.parserError = new Exception("Stack overflow. Stack type: " + e.getKey() + " Stack size: " + e.getValue().size());
				throw this.parserError;
			}
		}
	}

	public void nextLine() {
		this.nextLine++;
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
}
