package thebetweenlands.common.herblore.book.widgets.text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import thebetweenlands.common.herblore.book.widgets.ManualWidgetBase;

public class TextContainer {
    private final double pageWidth, pageHeight;
    private final String unparsedText;
    private final Map<String, Tag> textTagComponents = new HashMap<String, Tag>();
    private final List<TextPage> pages = new ArrayList<TextPage>();
    private final FontRenderer defaultFont;

    public TextContainer(final double pageWidth, final double pageHeight, final String text, final FontRenderer defaultFont) {
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.unparsedText = text;
        this.defaultFont = defaultFont;
        this.currentFont = defaultFont;
    }

    /**
     * Returns the width of this container
     * @return
     */
    public double getWidth() {
        return this.pageWidth;
    }

    /**
     * Returns the height of this container
     * @return
     */
    public double getHeight() {
        return this.pageHeight;
    }

    /**
     * Returns the text pages
     * @return
     */
    public List<TextPage> getPages() {
        return this.pages;
    }

    /**
     * Returns the default font
     * @return
     */
    public FontRenderer getDefaultFont() {
        return this.defaultFont;
    }

    /**
     * A text area is an area of the text and holds some additional properties
     */
    public static class TextArea {
        public final TextPage page;
        public double x, y, width, height;
        private List<Object> properties = new ArrayList<Object>();

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

        /**
         * Checks whether the specified point is inside this text area
         * @param offsetX Offset X
         * @param offsetY Offset Y
         * @param testX Test X
         * @param testY Test Y
         * @return
         */
        public boolean isInside(double offsetX, double offsetY, double testX, double testY) {
            return testX >= x + offsetX && testX < x + offsetX + width && testY >= y + offsetY && testY < y + offsetY + height;
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
            return this;
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

    /**
     * A text segment is a text area with the additional properties: text, color, scale and font
     */
    public static class TextSegment extends TextArea {
        public final String text;
        public final int color;
        public final float scale;
        public final FontRenderer font;

        public TextSegment(TextPage page, String text, double x, double y, double width, double height, int color, float scale, FontRenderer font) {
            super(page, x, y, width, height);
            this.text = text;
            this.color = color;
            this.scale = scale;
            this.font = font;
        }

        private TextSegment(TextPage page, String text, double x, double y, double width, double height, int color, float scale, FontRenderer font, List<Object> properties) {
            super(page, x, y, width, height, properties);
            this.text = text;
            this.color = color;
            this.scale = scale;
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
         * @param container Text container
         * @param previous Previous tag
         * @param arguments Tag arguments
         * @param area Text area
         * @param pass Push pass (0: before text area, 1: after text area)
         * @return True if the format should be pushed onto the stack
         */
        boolean push(TextContainer container, Tag previous, String arguments, TextArea area, int pass) {
            return false;
        }

        /**
         * Creates a new instance of this tag
         * @return
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
         * @param container Text container
         * @param area Text area
         */
        void expand(TextContainer container, TextArea area) { }

        /**
         * Called when the tag is popped from the stack
         * @param previous Previous tag
         */
        void pop(TextContainer container, RangedTag previous) { }
    }

    /**
     * A text page contains a list of text segments and text areas
     */
    public static class TextPage {
        private final List<TextSegment> textSegments = new ArrayList<TextSegment>();
        private final List<TextArea> textAreas = new ArrayList<TextArea>();

        public final double width, height;

        private TextPage(double width, double height) {
            this.width = width;
            this.height = height;
        }

        /**
         * Returns the width of the area that contains text
         * @return
         */
        public double getTextWidth() {
            double furthest = 0.0D;
            for(TextSegment segment : this.textSegments) {
                if(segment.x + segment.width > furthest)
                    furthest = segment.x + segment.width;
            }
            return furthest;
        }

        /**
         * Returns the height of the area that contains text
         * @return
         */
        public double getTextHeight() {
            double furthest = 0.0D;
            for(TextSegment segment : this.textSegments) {
                if(segment.y + segment.height > furthest)
                    furthest = segment.y + segment.height;
            }
            return furthest;
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
        public void render(double x, double y) {
            for(TextSegment segment : this.textSegments) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(segment.x + x, segment.y + y, 0.0D);
                GlStateManager.scale(segment.scale, segment.scale, 1.0F);
                segment.font.drawString(segment.text, 0, 0, segment.color);
                GlStateManager.color(1, 1, 1, 1);
                GlStateManager.popMatrix();
            }
        }

        /**
         * Renders the bounds of this page
         * @param x Page X
         * @param y Page Y
         */
        public void renderBounds(double x, double y) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(this.width / 10.0D, this.height / 10.0D, 1.0D);
            Gui.drawRect(0, 0, 10, 10, 0x20FF0000);
            GlStateManager.popMatrix();
            for(TextSegment segment : this.textSegments) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(segment.x + x, segment.y + y, 0);
                GlStateManager.scale(segment.width / 10.0D, segment.height / 10.0D, 1.0D);
                Gui.drawRect(0, 0, 10, 10, 0x250000FF);
                GlStateManager.popMatrix();
                GlStateManager.color(1, 1, 1, 1);
            }
            for(TextArea ta : this.getTextAreas()) {
                if(ta instanceof TooltipArea) {
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(ta.x + x, ta.y + y, 0);
                    GlStateManager.scale(ta.width / 10.0D, ta.height / 10.0D, 1.0D);
                    Gui.drawRect(0, 0, 10, 10, 0x6000FF00);
                    GlStateManager.popMatrix();
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
        public void renderTooltips(double x, double y, double mouseX, double mouseY) {
            List<String> tooltipLines = new ArrayList<String>();
            for(TextArea ta : this.getTextAreas()) {
                if(ta instanceof TooltipArea) {
                    if(ta.isInside(x, y, mouseX, mouseY)) {
                        tooltipLines.add(((TooltipArea)ta).text);
                    }
                }
            }
            if(tooltipLines.size() > 0) {
                ManualWidgetBase.renderTooltip((int)mouseX, (int)mouseY, tooltipLines, 0xffffff, 0xf0100010);
            }
        }
    }

    /**
     * Hovering over a tooltip area will cause a tooltip to show up
     */
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

    /**
     * A parsed tag
     */
    private class ParsedTag {
        final String text;
        final boolean close;
        final int index;

        private ParsedTag(String text, boolean close, int index) {
            this.text = text;
            this.close = close;
            this.index = index;
        }
    }

    /**
     * A parsed text segment with tags
     */
    private class ParsedSegment {
        final String text;
        final boolean significant;
        final List<ParsedTag> tags = new ArrayList<ParsedTag>();

        /**
         * @param text Text
         * @param significant Whether this segment is significant for word wrapping
         */
        private ParsedSegment(String text, boolean significant) {
            this.text = text;
            this.significant = significant;
        }
    }

    /**
     * List of either significant or non-significant segments for word wrapping
     */
    private class WrapSegment {
        final boolean significant;
        final List<ParsedSegment> segments = new ArrayList<ParsedSegment>();

        private WrapSegment(boolean significant) {
            this.significant = significant;
        }
    }

    /**
     * Registers a tag
     * @param tag
     */
    public void registerTag(Tag tag) {
        this.textTagComponents.put(tag.type, tag);
    }

    /**
     * Adds a new line
     */
    public void newLine() {
        this.newLine++;
    }

    /**
     * Adds a new page
     */
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
        final List<ParsedTag> tagList = new ArrayList<ParsedTag>();
        final char[] textCharArray = (this.unparsedText + " P").toCharArray();
        char prevChar = 0;
        char prevChar2 = 0;
        boolean inTag = false;
        int strippedTextIndex = 0;
        final char escapeChar = '\\';
        for(int i = 0; i < textCharArray.length; i++) {
            final char currentChar = textCharArray[i];
            final char nextChar = i < textCharArray.length - 1 ? textCharArray[i+1] : 0;
            final char nextChar2 = i < textCharArray.length - 2 ? textCharArray[i+2] : 0;
            boolean addChar = true;
            final boolean escapable = this.isEscapable(currentChar);
            final boolean escaped = prevChar == escapeChar && prevChar2 != prevChar;
            prevChar2 = prevChar;
            prevChar = currentChar;
            if(currentChar == escapeChar && nextChar == escapeChar && this.isEscapable(nextChar2)) {
                addChar = false;
            }

            if(escapable && !escaped) {
                if(currentChar == '<') {
                    if(inTag) {
                        throw new RuntimeException("Already in tag");
                    }
                    inTag = true;
                    addChar = false;
                    tagTextBuilder = new StringBuilder();
                } else if(currentChar == '>'){
                    if(!inTag) {
                        throw new RuntimeException("Not in tag");
                    }
                    inTag = false;
                    addChar = false;
                    String tagText = tagTextBuilder.toString();
                    boolean close = false;
                    if(tagText.startsWith("/")) {
                        close = true;
                        tagText = tagText.substring(1, tagText.length());
                    }
                    tagList.add(new ParsedTag(tagText, close, strippedTextIndex));
                }
            } else if(escapable && escaped && strippedTextIndex > 0) {
                strippedTextBuilder.delete(strippedTextIndex-1, strippedTextIndex);
                strippedTextIndex--;
            }

            if(addChar) {
                if(!inTag) {
                    strippedTextBuilder.append(currentChar);
                    strippedTextIndex++;
                } else {
                    tagTextBuilder.append(currentChar);
                }
            }
        }
        if(inTag) {
            throw new RuntimeException("Unclosed tag");
        }
        final String strippedText = strippedTextBuilder.toString();
        final List<ParsedTag>[] tagsArray = new ArrayList[strippedText.length() + 1];
        for(ParsedTag tag : tagList) {
            if(tagsArray[tag.index] == null) {
                tagsArray[tag.index] = new ArrayList<ParsedTag>();
            }
            tagsArray[tag.index].add(tag);
        }

        //// Parse segments ////
        final char[] strippedTextCharArray = strippedText.toCharArray();
        final List<ParsedSegment> parsedSegments = new ArrayList<ParsedSegment>();
        StringBuilder segmentTextBuilder = new StringBuilder();
        boolean wasSpacing = false;
        final List<ParsedTag> prevTags = new ArrayList<ParsedTag>();
        for(int i = 0; i < strippedTextCharArray.length; i++) {
            final char currentChar = strippedTextCharArray[i];
            final List<ParsedTag> tags = tagsArray[i];
            final boolean hasTags = tags != null && !tags.isEmpty();
            final boolean isSpacing = this.isSpacing(currentChar);

            if(!hasTags) {
                if(isSpacing) {
                    if(!wasSpacing) {
                        String prevSegmentText = segmentTextBuilder.toString();
                        segmentTextBuilder = new StringBuilder();
                        ParsedSegment seg = new ParsedSegment(prevSegmentText, true);
                        seg.tags.addAll(prevTags);
                        prevTags.clear();
                        parsedSegments.add(seg);
                    }
                } else {
                    if(wasSpacing) {
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

            if(i != strippedTextCharArray.length - 2) segmentTextBuilder.append(currentChar);
            if(i == strippedTextCharArray.length - 2) {
                parsedSegments.add(new ParsedSegment(segmentTextBuilder.toString(), !isSpacing));
            }
        }

        //// Group segments by wrap significance ////
        final List<WrapSegment> wrapSegments = new ArrayList<WrapSegment>();
        final List<ParsedSegment> segmentGroup = new ArrayList<ParsedSegment>();
        boolean significant = false;
        for(ParsedSegment seg : parsedSegments) {
            if(wrapSegments.isEmpty() && segmentGroup.isEmpty())
                significant = seg.significant;
            if(significant == seg.significant) {
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
        if(!segmentGroup.isEmpty()) {
            WrapSegment wrapSegment = new WrapSegment(significant);
            wrapSegment.segments.addAll(segmentGroup);
            wrapSegments.add(wrapSegment);
            segmentGroup.clear();
        }

        //// Build text pages and segments from wrap segments ////
        this.pages.clear();
        this.pages.addAll(this.build(wrapSegments));
    }

    private final List<TextFormatting> textFormatList = new ArrayList<TextFormatting>();
    private final List<TextArea> textAreas = new ArrayList<TextArea>();
    private float currentScale = 1.0F;
    private int currentColor = 0;
    private int newLine = 0;
    private int newPage = 0;
    private FontRenderer currentFont;

    private List<TextPage> build(List<WrapSegment> wrapSegments) {
        String currentTagType = null;
        ParsedSegment currentSegment = null;
        try {
            final Map<String, Stack<RangedTag>> textTagComponentStacks = new HashMap<String, Stack<RangedTag>>();
            for(Map.Entry<String, Tag> e : this.textTagComponents.entrySet()) {
                Tag tag = e.getValue();
                if(tag instanceof RangedTag) this.getTagStack(tag.type, textTagComponentStacks).push((RangedTag) tag);
            }
            final List<TextSegment> textSegments = new ArrayList<TextSegment>();
            final List<TextPage> pages = new ArrayList<TextPage>();
            TextPage currentPage = new TextPage(this.pageWidth, this.pageHeight);
            pages.add(currentPage);
            double cursorX = 0.0D;
            double cursorY = 0.0D;
            double lineHeight = 0.0D;
            WrapSegment prevWrapSegment = null;
            final List<TextSegment> prevWrapTextSegments = new ArrayList<TextSegment>();
            boolean newLine = false;
            for(int w = 0; w < wrapSegments.size() - 1; w++) {
                WrapSegment wrapSegment = wrapSegments.get(w);
                double wrapSegmentWidth = 0.0D;
                double wrapSegmentHeight = 0.0D;
                final List<TextSegment> wrapTextSegments = new ArrayList<TextSegment>();

                for(ParsedSegment segment : wrapSegment.segments) {
                    currentSegment = segment;

                    final List<ParsedTag> tags = segment.tags;
                    final List<TextArea> textAreas = new ArrayList<TextArea>();
                    final List<String> usedTags = new ArrayList<String>();

                    ///// Push/Pop tags /////
                    if(!tags.isEmpty()) {
                        for(ParsedTag tag : tags) {
                            String tagText = tag.text;
                            String tagType = null;
                            String tagArguments = null;
                            if(tagText.contains(":")) {
                                String[] tagData = tagText.split(":");
                                tagType = tagData[0];
                                tagArguments = tagData[1];
                            } else {
                                tagType = tagText;
                            }
                            currentTagType = tagType;
                            usedTags.add(tagType);
                            Tag textTag = this.textTagComponents.get(tagType);
                            if(textTag == null) {
                                throw new RuntimeException("Unknown tag: " + tagType);
                            } else {
                                Stack<RangedTag> tagStack = this.getTagStack(tagType, textTagComponentStacks);
                                if(tag.close) {
                                    tagStack.pop().pop(this, tagStack.peek());
                                } else {
                                    Tag newTag = textTag.create();
                                    if(newTag.push(this, newTag instanceof RangedTag ? tagStack.peek() : null, tagArguments, null, 0) && newTag instanceof RangedTag) {
                                        tagStack.push((RangedTag)newTag);
                                    }
                                }
                            }
                        }
                    }

                    //// Create text area ////
                    String prefixed = "";
                    if(this.textFormatList.size() > 0) {
                        this.sortFormatList(this.textFormatList);
                        StringBuilder formatStringBuilder = new StringBuilder();
                        for(TextFormatting format : this.textFormatList) {
                            formatStringBuilder.append(format.toString());
                        }
                        prefixed = formatStringBuilder.toString();
                    }
                    String word = prefixed + segment.text;
                    double strWidth = this.currentFont.getStringWidth(word) * this.currentScale;
                    double strHeight = this.currentFont.FONT_HEIGHT * this.currentScale;
                    boolean nextLine = cursorX + strWidth > this.pageWidth;
                    if(segment.significant) {
                        newLine = false;
                    }
                    if(nextLine || this.newLine > 0) {
                        cursorX = 0.0D;
                        cursorY += lineHeight * (this.newLine + (nextLine ? 1 : 0));
                        lineHeight = 0.0D;
                        newLine = true;
                        if(prevWrapSegment != null && nextLine && this.newLine == 0) {
                            //Remove previous insignificant segments
                            for(TextSegment wrapTextSegment : prevWrapTextSegments) {
                                wrapTextSegment.page.textSegments.remove(wrapTextSegment);
                            }
                        }
                    }
                    if(segment.text.length() > 0 && strHeight > lineHeight) {
                        lineHeight = strHeight;
                    }
                    boolean nextPage = cursorY + strHeight > this.pageHeight;
                    if(nextPage || this.newPage > 0) {
                        currentPage.textAreas.addAll(this.textAreas);
                        this.textAreas.clear();
                        for(int i = 0; i < (this.newPage + (nextPage ? 1 : 0)); i++) {
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
                    if(!tags.isEmpty()) {
                        for(ParsedTag tag : tags) {
                            String tagText = tag.text;
                            String tagType = null;
                            String tagArguments = null;
                            if(tagText.contains(":")) {
                                String[] tagData = tagText.split(":");
                                tagType = tagData[0];
                                tagArguments = tagData[1];
                            } else {
                                tagType = tagText;
                            }
                            currentTagType = tagType;
                            Tag textTag = this.textTagComponents.get(tagType);
                            if(textTag == null) {
                                throw new RuntimeException("Unknown tag: " + tagType);
                            } else {
                                Stack<RangedTag> tagStack = this.getTagStack(tagType, textTagComponentStacks);
                                if(!tag.close) {
                                    Tag newTag = textTag.create();
                                    if(newTag.push(this, newTag instanceof RangedTag ? tagStack.peek() : null, tagArguments, area, 1) && newTag instanceof RangedTag) {
                                        tagStack.push((RangedTag)newTag);
                                    }
                                }
                            }
                        }
                    }

                    //// Expand currently active tags ////
                    for(Stack<RangedTag> activeTagStack : textTagComponentStacks.values()) {
                        if(activeTagStack.size() > 1) {
                            for(int c = 1; c < activeTagStack.size(); c++) {
                                RangedTag tag = activeTagStack.get(c);
                                if(!usedTags.contains(tag.type)) {
                                    tag.expand(this, area);
                                }
                            }
                        }
                    }

                    //Only add X offset and segment at start of new line if the segment's width is significant when word wrapping
                    if(!newLine || segment.significant || this.newLine > 0) {
                        //// Create text segment ////
                        TextSegment textSegment = new TextSegment(area.page, word, cursorX, cursorY, strWidth, strHeight, this.currentColor, this.currentScale, this.currentFont);
                        if(word.length() > 0) {
                            currentPage.textSegments.add(textSegment);
                            currentPage.textAreas.addAll(this.textAreas);
                        }
                        if(!segment.significant) {
                            wrapTextSegments.add(textSegment);
                        }
                        this.textAreas.clear();

                        cursorX += strWidth;
                    } else if(newLine && !segment.significant || this.newLine == 0) {
                        //Remove insignificant segments
                        for(TextSegment wrapTextSegment : wrapTextSegments) {
                            wrapTextSegment.page.textSegments.remove(wrapTextSegment);
                        }
                    }

                    if(segment.significant) {
                        newLine = false;
                    }
                    this.newLine = 0;
                }
                prevWrapSegment = wrapSegment;
                prevWrapTextSegments.clear();
                prevWrapTextSegments.addAll(wrapTextSegments);
            }

            return pages;
        } catch(Exception ex) {
            if(ex instanceof EmptyStackException) {
                throw new RuntimeException("Stack underflow. Stack type: " + currentTagType + " Word: " + currentSegment.text, ex);
            }
            throw ex;
        }
    }

    /**
     * Sets the current text segment font
     * @param font
     * @return
     */
    public TextContainer setCurrentFont(FontRenderer font) {
        if(font == null) {
            this.currentFont = this.defaultFont;
        } else {
            this.currentFont = font;
        }
        return this;
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
     * Adds a chat format to the current text segment
     * @param format
     * @return
     */
    public TextContainer addFormatting(TextFormatting format) {
        this.textFormatList.add(format);
        return this;
    }

    /**
     * Removes a chat format from the current text segment
     * @param format
     * @return
     */
    public TextContainer removeFormatting(TextFormatting format) {
        this.textFormatList.remove(format);
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
     * Adds a text area to the current page
     * @param area
     * @return
     */
    public TextContainer addTextArea(TextArea area) {
        this.textAreas.add(area);
        return this;
    }

    /**
     * Removes a text area from all pages
     * @param area
     * @return
     */
    public TextContainer removeTextArea(TextArea area) {
        for(TextPage page : this.pages) {
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

    private Stack<RangedTag> getTagStack(String component, Map<String, Stack<RangedTag>> stackMap) {
        Stack<RangedTag> stack = stackMap.get(component);
        if(stack == null) {
            stack = new Stack<RangedTag>();
            stackMap.put(component, stack);
        }
        return stack;
    }

    private void sortFormatList(List<TextFormatting> list) {
        Comparator<TextFormatting> formatComparator = new Comparator<TextFormatting>() {
            @Override
            public int compare(TextFormatting f1, TextFormatting f2) {
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

    private boolean isSpacing(char c) {
        return c == ' ';
    }

    private boolean isEscapable(char c) {
        return c == '<' || c == '>';
    }
}
