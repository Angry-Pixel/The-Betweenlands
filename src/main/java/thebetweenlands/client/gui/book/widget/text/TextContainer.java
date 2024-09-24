package thebetweenlands.client.gui.book.widget.text;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.*;

public class TextContainer {

	private final double pageWidth, pageHeight;
	private final String unparsedText;
	private final Map<String, Tag> textTagComponents = new HashMap<>();
	private final List<TextPage> pages = new ArrayList<>();
	private final ResourceLocation font;
	private final Style defaultStyle;

	public TextContainer(final double pageWidth, final double pageHeight, final Component text) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.unparsedText = text.getString();
		this.defaultStyle = text.getStyle();
		this.font = Style.DEFAULT_FONT;
		this.currentFont = Style.DEFAULT_FONT;
	}

	public TextContainer(final double pageWidth, final double pageHeight, final Component text, final ResourceLocation font) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.unparsedText = text.getString();
		this.defaultStyle = text.getStyle();
		this.font = font;
		this.currentFont = font;
	}

	public double getWidth() {
		return this.pageWidth;
	}

	public double getHeight() {
		return this.pageHeight;
	}

	public List<TextPage> getPages() {
		return this.pages;
	}

	public ResourceLocation getDefaultFont() {
		return this.font;
	}

	public static class TextArea {
		public final TextPage page;
		public double x, y, width, height;
		private List<Object> properties = new ArrayList<>();

		protected TextArea(TextPage page, double x, double y, double width, double height) {
			this.page = page;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		protected TextArea(TextArea area) {
			this(area.page, area.x, area.y, area.width, area.height, area.properties);
		}

		private TextArea(TextPage page, double x, double y, double width, double height, List<Object> properties) {
			this(page, x, y, width, height);
			this.properties = properties;
		}

		public boolean isInside(double offsetX, double offsetY, double testX, double testY) {
			return testX >= x + offsetX && testX < x + offsetX + width && testY >= y + offsetY && testY < y + offsetY + height;
		}

		public TextArea setBounds(TextArea area) {
			this.x = area.x;
			this.y = area.y;
			this.width = area.width;
			this.height = area.height;
			return this;
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof TextArea area) {
				return area.page.equals(this.page) && area.x == this.x && area.y == this.y && area.width == this.width && area.height == this.height;
			}
			return false;
		}

		public void addProperty(Object obj) {
			this.properties.add(obj);
		}

		public boolean removeProperty(Object obj) {
			return this.properties.remove(obj);
		}

		public List<Object> getProperties() {
			return this.properties;
		}

		@SuppressWarnings("unchecked")
		public <T> List<T> getProperties(Class<T> type) {
			List<T> lst = new ArrayList<>();
			for (Object obj : this.properties) {
				if (type.isAssignableFrom(obj.getClass())) {
					lst.add((T) obj);
				}
			}
			return lst;
		}
	}

	public static class TextSegment extends TextArea {
		public final String text;
		public final int color;
		public final float scale;
		public final List<ChatFormatting> formats;
		public final ResourceLocation font;
		public final Style defaultStyle;

		public TextSegment(TextPage page, String text, double x, double y, double width, double height, int color, float scale, List<ChatFormatting> formats, Style defaultStyle, ResourceLocation font) {
			super(page, x, y, width, height);
			this.text = text;
			this.color = color;
			this.scale = scale;
			this.formats = formats;
			this.defaultStyle = defaultStyle;
			this.font = font;
		}

		private TextSegment(TextPage page, String text, double x, double y, double width, double height, int color, float scale, List<ChatFormatting> formats, ResourceLocation font, Style defaultStyle, List<Object> properties) {
			super(page, x, y, width, height, properties);
			this.text = text;
			this.color = color;
			this.scale = scale;
			this.formats = formats;
			this.defaultStyle = defaultStyle;
			this.font = font;
		}
	}

	/**
	 * A tag that can change properties of the text to render
	 */
	public static abstract class Tag {
		public final String type;

		public Tag(String type) {
			this.type = type;
		}

		/**
		 * Called when the tag is pushed.
		 *
		 * @param container Text container
		 * @param previous  Previous tag
		 * @param arguments Tag arguments
		 * @param area      Text area
		 * @param pass      Push pass (0: before text area, 1: after text area)
		 * @return True if the format should be pushed onto the stack
		 */
		boolean push(TextContainer container, Tag previous, String arguments, TextArea area, int pass) {
			return false;
		}

		/**
		 * Creates a new instance of this tag
		 */
		abstract Tag create();
	}

	/**
	 * A tag that ranges over several characters. < tag > range < /tag >
	 */
	public static abstract class RangedTag extends Tag {
		public RangedTag(String type) {
			super(type);
		}

		/**
		 * Called for each word if this tag is active
		 *
		 * @param container Text container
		 * @param area      Text area
		 */
		void expand(TextContainer container, TextArea area) {
		}

		/**
		 * Called when the tag is popped from the stack
		 *
		 * @param previous Previous tag
		 */
		void pop(TextContainer container, RangedTag previous) {
		}
	}

	public static class TextPage {
		private final List<TextSegment> textSegments = new ArrayList<>();
		private final List<TextArea> textAreas = new ArrayList<>();

		public final double width, height;

		private TextPage(double width, double height) {
			this.width = width;
			this.height = height;
		}

		public double getTextWidth() {
			double furthest = 0.0D;
			for (TextSegment segment : this.textSegments) {
				if (segment.x + segment.width > furthest)
					furthest = segment.x + segment.width;
			}
			return furthest;
		}

		public double getTextHeight() {
			double furthest = 0.0D;
			for (TextSegment segment : this.textSegments) {
				if (segment.y + segment.height > furthest)
					furthest = segment.y + segment.height;
			}
			return furthest;
		}

		public List<TextSegment> getSegments() {
			return this.textSegments;
		}

		public List<TextArea> getTextAreas() {
			return this.textAreas;
		}

		@SuppressWarnings("unchecked")
		public <T extends TextArea> List<T> getTextAreas(Class<T> type) {
			List<T> lst = new ArrayList<>();
			for (TextArea area : this.textAreas) {
				if (type.isAssignableFrom(area.getClass())) {
					lst.add((T) area);
				}
			}
			return lst;
		}

		public void render(GuiGraphics graphics, double x, double y) {
			for (TextSegment segment : this.textSegments) {
				graphics.pose().pushPose();
				graphics.pose().translate(segment.x + x, segment.y + y, 0.0D);
				graphics.pose().scale(segment.scale, segment.scale, 1.0F);
				graphics.drawString(Minecraft.getInstance().font, FormattedCharSequence.forward(segment.text, segment.defaultStyle.applyFormats(segment.formats.toArray(ChatFormatting[]::new)).withFont(segment.font)), 0, 0, segment.color, false);
				graphics.setColor(1, 1, 1, 1);
				graphics.pose().popPose();
			}
		}

		public void renderBounds(GuiGraphics graphics, double x, double y) {
			graphics.pose().pushPose();
			graphics.pose().translate(x, y, 0);
			graphics.pose().scale((float) (this.width / 10.0F), (float) (this.height / 10.0F), 1.0F);
			graphics.fill(0, 0, 10, 10, 0x20FF0000);
			graphics.pose().popPose();
			for (TextSegment segment : this.textSegments) {
				graphics.pose().popPose();
				graphics.pose().pushPose();
				graphics.pose().translate(segment.x + x, segment.y + y, 0);
				graphics.pose().scale((float) (segment.width / 10.0F), (float) (segment.height / 10.0F), 1.0F);
				graphics.fill(0, 0, 10, 10, 0x250000FF);
				graphics.pose().popPose();
				graphics.setColor(1, 1, 1, 1);
				graphics.pose().popPose();
			}
			for (TextArea ta : this.getTextAreas()) {
				if (ta instanceof TooltipArea) {
					graphics.pose().pushPose();
					graphics.pose().translate(ta.x + x, ta.y + y, 0);
					graphics.pose().scale((float) (ta.width / 10.0F), (float) (ta.height / 10.0F), 1.0F);
					graphics.fill(0, 0, 10, 10, 0x6000FF00);
					graphics.pose().popPose();
				}
			}
		}

		public void renderTooltips(GuiGraphics graphics, double x, double y, double mouseX, double mouseY) {
			List<Component> tooltipLines = new ArrayList<>();
			for (TextArea ta : this.getTextAreas()) {
				if (ta instanceof TooltipArea) {
					if (ta.isInside(x, y, mouseX, mouseY)) {
						tooltipLines.add(((TooltipArea) ta).text);
					}
				}
			}
			if (!tooltipLines.isEmpty()) {
				graphics.renderTooltip(Minecraft.getInstance().font, tooltipLines, Optional.empty(), (int) mouseX, (int) mouseY);
			}
		}
	}

	/**
	 * Hovering over a tooltip area will cause a tooltip to show up
	 */
	public static class TooltipArea extends TextArea {
		public final Component text;

		public TooltipArea(TextArea area, Component text) {
			super(area);
			this.text = text;
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof TooltipArea area) {
				return super.equals(area) && area.text.equals(this.text);
			}
			return false;
		}
	}

	private record ParsedTag(String text, boolean close, int index) {
	}

	private static class ParsedSegment {
		final String text;
		final boolean significant;
		final List<ParsedTag> tags = new ArrayList<>();

		private ParsedSegment(String text, boolean significant) {
			this.text = text;
			this.significant = significant;
		}
	}

	private static class WrapSegment {
		final boolean significant;
		final List<ParsedSegment> segments = new ArrayList<>();

		private WrapSegment(boolean significant) {
			this.significant = significant;
		}
	}

	public void registerTag(Tag tag) {
		this.textTagComponents.put(tag.type, tag);
	}

	public void newLine() {
		this.newLine++;
	}

	public void newPage() {
		this.newPage++;
	}

	/**
	 * Parses the text. Throws an error if parsing fails
	 */
	public void parse() {
		//// Strip and store tags ////
		final StringBuilder strippedTextBuilder = new StringBuilder();
		StringBuilder tagTextBuilder = null;
		final List<ParsedTag> tagList = new ArrayList<>();
		final char[] textCharArray = (this.unparsedText + " P").toCharArray();
		char prevChar = 0;
		char prevChar2 = 0;
		boolean inTag = false;
		int strippedTextIndex = 0;
		final char escapeChar = '\\';
		for (int i = 0; i < textCharArray.length; i++) {
			final char currentChar = textCharArray[i];
			final char nextChar = i < textCharArray.length - 1 ? textCharArray[i + 1] : 0;
			final char nextChar2 = i < textCharArray.length - 2 ? textCharArray[i + 2] : 0;
			boolean addChar = true;
			final boolean escapable = this.isEscapable(currentChar);
			final boolean escaped = prevChar == escapeChar && prevChar2 != prevChar;
			prevChar2 = prevChar;
			prevChar = currentChar;
			if (currentChar == escapeChar && nextChar == escapeChar && this.isEscapable(nextChar2)) {
				addChar = false;
			}

			if (escapable && !escaped) {
				if (currentChar == '<') {
					if (inTag) {
						throw new RuntimeException("Already in tag");
					}
					inTag = true;
					addChar = false;
					tagTextBuilder = new StringBuilder();
				} else if (currentChar == '>') {
					if (!inTag) {
						throw new RuntimeException("Not in tag");
					}
					inTag = false;
					addChar = false;
					String tagText = tagTextBuilder.toString();
					boolean close = false;
					if (tagText.startsWith("/")) {
						close = true;
						tagText = tagText.substring(1);
					}
					tagList.add(new ParsedTag(tagText, close, strippedTextIndex));
				}
			} else if (escapable && strippedTextIndex > 0) {
				strippedTextBuilder.delete(strippedTextIndex - 1, strippedTextIndex);
				strippedTextIndex--;
			}

			if (addChar) {
				if (!inTag) {
					strippedTextBuilder.append(currentChar);
					strippedTextIndex++;
				} else {
					tagTextBuilder.append(currentChar);
				}
			}
		}
		if (inTag) {
			throw new RuntimeException("Unclosed tag in text: " + this.unparsedText);
		}
		final String strippedText = strippedTextBuilder.toString();
		//noinspection unchecked
		final List<ParsedTag>[] tagsArray = new ArrayList[strippedText.length() + 1];
		for (ParsedTag tag : tagList) {
			if (tagsArray[tag.index] == null) {
				tagsArray[tag.index] = new ArrayList<>();
			}
			tagsArray[tag.index].add(tag);
		}

		//// Parse segments ////
		final char[] strippedTextCharArray = strippedText.toCharArray();
		final List<ParsedSegment> parsedSegments = new ArrayList<>();
		StringBuilder segmentTextBuilder = new StringBuilder();
		boolean wasSpacing = false;
		final List<ParsedTag> prevTags = new ArrayList<>();
		for (int i = 0; i < strippedTextCharArray.length; i++) {
			final char currentChar = strippedTextCharArray[i];
			final List<ParsedTag> tags = tagsArray[i];
			final boolean hasTags = tags != null && !tags.isEmpty();
			final boolean isSpacing = this.isSpacing(currentChar);

			if (!hasTags) {
				if (isSpacing) {
					if (!wasSpacing) {
						String prevSegmentText = segmentTextBuilder.toString();
						segmentTextBuilder = new StringBuilder();
						ParsedSegment seg = new ParsedSegment(prevSegmentText, true);
						seg.tags.addAll(prevTags);
						prevTags.clear();
						parsedSegments.add(seg);
					}
				} else {
					if (wasSpacing) {
						String prevSpacingText = segmentTextBuilder.toString();
						segmentTextBuilder = new StringBuilder();
						ParsedSegment seg = new ParsedSegment(prevSpacingText, false);
						seg.tags.addAll(prevTags);
						prevTags.clear();
						parsedSegments.add(seg);
					}
				}
			} else {
				String prevSegmentText = segmentTextBuilder.toString();
				segmentTextBuilder = new StringBuilder();
				ParsedSegment seg = new ParsedSegment(prevSegmentText, !wasSpacing);
				seg.tags.addAll(prevTags);
				prevTags.clear();
				prevTags.addAll(tags);
				parsedSegments.add(seg);
			}
			wasSpacing = isSpacing;

			if (i != strippedTextCharArray.length - 2) segmentTextBuilder.append(currentChar);
			if (i == strippedTextCharArray.length - 2) {
				parsedSegments.add(new ParsedSegment(segmentTextBuilder.toString(), !isSpacing));
			}
		}

		//// Group segments by wrap significance ////
		final List<WrapSegment> wrapSegments = new ArrayList<>();
		final List<ParsedSegment> segmentGroup = new ArrayList<>();
		boolean significant = false;
		for (ParsedSegment seg : parsedSegments) {
			if (wrapSegments.isEmpty() && segmentGroup.isEmpty())
				significant = seg.significant;
			if (significant == seg.significant) {
				segmentGroup.add(seg);
			} else {
				WrapSegment wrapSegment = new WrapSegment(significant);
				wrapSegment.segments.addAll(segmentGroup);
				wrapSegments.add(wrapSegment);
				segmentGroup.clear();
				segmentGroup.add(seg);
				significant = seg.significant;
			}
		}
		if (!segmentGroup.isEmpty()) {
			WrapSegment wrapSegment = new WrapSegment(significant);
			wrapSegment.segments.addAll(segmentGroup);
			wrapSegments.add(wrapSegment);
			segmentGroup.clear();
		}

		//// Build text pages and segments from wrap segments ////
		this.pages.clear();
		this.pages.addAll(this.build(wrapSegments));
	}

	private final List<ChatFormatting> textFormatList = new ArrayList<>();
	private final List<TextArea> textAreas = new ArrayList<>();
	private float currentScale = 1.0F;
	private int currentColor = 0;
	private int newLine = 0;
	private int newPage = 0;
	private ResourceLocation currentFont;

	private List<TextPage> build(List<WrapSegment> wrapSegments) {
		String currentTagType = null;
		ParsedSegment currentSegment = null;
		try {
			final Map<String, Stack<RangedTag>> textTagComponentStacks = new HashMap<>();
			for (Map.Entry<String, Tag> e : this.textTagComponents.entrySet()) {
				Tag tag = e.getValue();
				if (tag instanceof RangedTag) this.getTagStack(tag.type, textTagComponentStacks).push((RangedTag) tag);
			}
			final List<TextPage> pages = new ArrayList<>();
			TextPage currentPage = new TextPage(this.pageWidth, this.pageHeight);
			pages.add(currentPage);
			double cursorX = 0.0D;
			double cursorY = 0.0D;
			double lineHeight = 0.0D;
			WrapSegment prevWrapSegment = null;
			final List<TextSegment> prevWrapTextSegments = new ArrayList<>();
			boolean newLine = false;
			for (int w = 0; w < wrapSegments.size() - 1; w++) {
				WrapSegment wrapSegment = wrapSegments.get(w);
				final List<TextSegment> wrapTextSegments = new ArrayList<>();

				for (ParsedSegment segment : wrapSegment.segments) {
					currentSegment = segment;

					final List<ParsedTag> tags = segment.tags;
					final List<String> usedTags = new ArrayList<>();

					///// Push/Pop tags /////
					if (!tags.isEmpty()) {
						for (ParsedTag tag : tags) {
							String tagText = tag.text;
							String tagType;
							String tagArguments = null;
							if (tagText.contains(":")) {
								String[] tagData = tagText.split(":");
								tagType = tagData[0];
								tagArguments = tagData[1];
							} else {
								tagType = tagText;
							}
							currentTagType = tagType;
							usedTags.add(tagType);
							Tag textTag = this.textTagComponents.get(tagType);
							if (textTag == null) {
								throw new RuntimeException("Unknown tag: " + tagType);
							} else {
								Stack<RangedTag> tagStack = this.getTagStack(tagType, textTagComponentStacks);
								if (tag.close) {
									tagStack.pop().pop(this, tagStack.peek());
								} else {
									Tag newTag = textTag.create();
									if (newTag.push(this, newTag instanceof RangedTag ? tagStack.peek() : null, tagArguments, null, 0) && newTag instanceof RangedTag) {
										tagStack.push((RangedTag) newTag);
									}
								}
							}
						}
					}

					//// Create text area ////
					double strWidth = Minecraft.getInstance().font.width(segment.text) * this.currentScale;
					double strHeight = Minecraft.getInstance().font.lineHeight * this.currentScale;
					boolean nextLine = cursorX + strWidth > this.pageWidth;
					if (segment.significant) {
						newLine = false;
					}
					if (nextLine || this.newLine > 0) {
						cursorX = 0.0D;
						cursorY += lineHeight * (this.newLine + (nextLine ? 1 : 0));
						lineHeight = 0.0D;
						newLine = true;
						if (prevWrapSegment != null && nextLine && this.newLine == 0) {
							//Remove previous insignificant segments
							for (TextSegment wrapTextSegment : prevWrapTextSegments) {
								wrapTextSegment.page.textSegments.remove(wrapTextSegment);
							}
						}
					}
					if (!segment.text.isEmpty() && strHeight > lineHeight) {
						lineHeight = strHeight;
					}
					boolean nextPage = cursorY + strHeight > this.pageHeight;
					if (nextPage || this.newPage > 0) {
						currentPage.textAreas.addAll(this.textAreas);
						this.textAreas.clear();
						for (int i = 0; i < (this.newPage + (nextPage ? 1 : 0)); i++) {
							currentPage = new TextPage(this.pageWidth, this.pageHeight);
							pages.add(currentPage);
						}
						this.newPage = 0;
						lineHeight = 0.0D;
						newLine = false;
						cursorX = 0.0D;
						cursorY = 0.0D;
					}
					TextArea area = new TextArea(currentPage, cursorX, cursorY, strWidth, strHeight);

					//// Push tags in second pass ////
					if (!tags.isEmpty()) {
						for (ParsedTag tag : tags) {
							String tagText = tag.text;
							String tagType;
							String tagArguments = null;
							if (tagText.contains(":")) {
								String[] tagData = tagText.split(":");
								tagType = tagData[0];
								tagArguments = tagData[1];
							} else {
								tagType = tagText;
							}
							currentTagType = tagType;
							Tag textTag = this.textTagComponents.get(tagType);
							if (textTag == null) {
								throw new RuntimeException("Unknown tag: " + tagType);
							} else {
								Stack<RangedTag> tagStack = this.getTagStack(tagType, textTagComponentStacks);
								if (!tag.close) {
									Tag newTag = textTag.create();
									if (newTag.push(this, newTag instanceof RangedTag ? tagStack.peek() : null, tagArguments, area, 1) && newTag instanceof RangedTag) {
										tagStack.push((RangedTag) newTag);
									}
								}
							}
						}
					}

					//// Expand currently active tags ////
					for (Stack<RangedTag> activeTagStack : textTagComponentStacks.values()) {
						if (activeTagStack.size() > 1) {
							for (int c = 1; c < activeTagStack.size(); c++) {
								RangedTag tag = activeTagStack.get(c);
								if (!usedTags.contains(tag.type)) {
									tag.expand(this, area);
								}
							}
						}
					}

					//Only add X offset and segment at start of new line if the segment's width is significant when word wrapping
					if (!newLine || segment.significant || this.newLine > 0) {
						//// Create text segment ////
						TextSegment textSegment = new TextSegment(area.page, segment.text, cursorX, cursorY, strWidth, strHeight, this.currentColor, this.currentScale, this.textFormatList, this.defaultStyle, this.currentFont);
						if (!segment.text.isEmpty()) {
							currentPage.textSegments.add(textSegment);
							currentPage.textAreas.addAll(this.textAreas);
						}
						if (!segment.significant) {
							wrapTextSegments.add(textSegment);
						}
						this.textAreas.clear();

						cursorX += strWidth;
					} else {
						//Remove insignificant segments
						for (TextSegment wrapTextSegment : wrapTextSegments) {
							wrapTextSegment.page.textSegments.remove(wrapTextSegment);
						}
					}

					if (segment.significant) {
						newLine = false;
					}
					this.newLine = 0;
				}
				prevWrapSegment = wrapSegment;
				prevWrapTextSegments.clear();
				prevWrapTextSegments.addAll(wrapTextSegments);
			}

			return pages;
		} catch (Exception ex) {
			if (ex instanceof EmptyStackException) {
				throw new RuntimeException("Stack underflow. Stack type: " + currentTagType + " Word: " + currentSegment.text, ex);
			}
			throw ex;
		}
	}

	public TextContainer setCurrentFont(ResourceLocation font) {
		if (font == null) {
			this.currentFont = this.font;
		} else {
			this.currentFont = font;
		}
		return this;
	}

	public TextContainer setCurrentScale(float scale) {
		this.currentScale = scale;
		return this;
	}

	public TextContainer addFormatting(ChatFormatting format) {
		this.textFormatList.add(format);
		return this;
	}

	public TextContainer removeFormatting(ChatFormatting format) {
		this.textFormatList.remove(format);
		return this;
	}

	public TextContainer setCurrentColor(int color) {
		this.currentColor = color;
		return this;
	}

	public TextContainer addTextArea(TextArea area) {
		this.textAreas.add(area);
		return this;
	}

	public TextContainer removeTextArea(TextArea area) {
		for (TextPage page : this.pages) {
			page.textAreas.removeIf(ta -> ta.equals(area));
		}
		return this;
	}

	private Stack<RangedTag> getTagStack(String component, Map<String, Stack<RangedTag>> stackMap) {
		return stackMap.computeIfAbsent(component, k -> new Stack<>());
	}

	private boolean isSpacing(char c) {
		return c == ' ';
	}

	private boolean isEscapable(char c) {
		return c == '<' || c == '>';
	}
}
