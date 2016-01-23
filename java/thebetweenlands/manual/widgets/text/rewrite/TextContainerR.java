package thebetweenlands.manual.widgets.text.rewrite;

import java.util.ArrayList;
import java.util.List;

public class TextContainerR {
	private final int pageWidth, pageHeight;
	private final String unparsedText;
	private final ITextContainerFont font;

	public TextContainerR(final int pageWidth, final int pageHeight, final String text, final ITextContainerFont font) {
		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;
		this.unparsedText = text;
		this.font = font;
	}

	class ParsedTag {
		final String text;
		final boolean close;
		final int index;

		ParsedTag(String text, boolean close, int index) {
			this.text = text;
			this.close = close;
			this.index = index;
		}
	}

	class ParsedSegment {
		final String text;
		final boolean significant;
		final List<ParsedTag> tags = new ArrayList<ParsedTag>();

		/**
		 * @param text Text
		 * @param significant Whether this segment is significant for word wrapping
		 */
		ParsedSegment(String text, boolean significant) {
			this.text = text;
			this.significant = significant;
		}
	}

	/**
	 * List of either significant or non-significant segments for word wrapping
	 */
	class WrapSegment {
		final boolean significant;
		final List<ParsedSegment> segments = new ArrayList<ParsedSegment>();

		WrapSegment(boolean significant) {
			this.significant = significant;
		}
	}

	public void parse() {
		final StringBuilder strippedTextBuilder = new StringBuilder();
		StringBuilder tagTextBuilder = null;
		final List<ParsedTag> tagList = new ArrayList<ParsedTag>();
		final char[] textCharArray = this.unparsedText.toCharArray();
		char prevChar = 0;
		boolean inTag = false;
		int strippedTextIndex = 0;
		for(int i = 0; i < textCharArray.length; i++) {
			final char currentChar = textCharArray[i];
			prevChar = currentChar;
			boolean addChar = true;

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

			if(addChar) {
				if(!inTag) {
					strippedTextBuilder.append(currentChar);
					strippedTextIndex++;
				} else {
					tagTextBuilder.append(currentChar);
				}
			}
		}

		final String strippedText = strippedTextBuilder.toString();

		final List<ParsedTag>[] tagsArray = new ArrayList[strippedText.length() + 1];
		for(ParsedTag tag : tagList) {
			if(tagsArray[tag.index] == null) {
				tagsArray[tag.index] = new ArrayList<ParsedTag>();
			}
			tagsArray[tag.index].add(tag);
		}

		/*System.out.println(this.unparsedText);
		System.out.println(strippedText);
		System.out.println("Tags:");
		for(ParsedTag tag : tagList) {
			System.out.println("   - " + tag.text + " " + tag.close + " " + tag.index);
		}

		final StringBuilder reconstBuilder = new StringBuilder(strippedText);
		for(int i = tagList.size() - 1; i >= 0; --i) {
			ParsedTag tag = tagList.get(i);
			reconstBuilder.insert(tag.index, "<" + (tag.close ? "/" : "") + tag.text + ">");
		}
		final String reconst = reconstBuilder.toString();
		System.out.println(reconst);*/

		final char[] strippedTextCharArray = strippedText.toCharArray();

		final List<ParsedSegment> parsedSegments = new ArrayList<ParsedSegment>();

		StringBuilder segmentTextBuilder = new StringBuilder();

		boolean wasSpacing = false;

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
						parsedSegments.add(new ParsedSegment(prevSegmentText, true));
					}
				} else {
					if(wasSpacing) {
						String prevSpacingText = segmentTextBuilder.toString();
						segmentTextBuilder = new StringBuilder();
						parsedSegments.add(new ParsedSegment(prevSpacingText, false));
					}
				}
			} else {
				String prevSegmentText = segmentTextBuilder.toString();
				segmentTextBuilder = new StringBuilder();
				ParsedSegment seg = new ParsedSegment(prevSegmentText, !wasSpacing);
				seg.tags.addAll(tags);
				parsedSegments.add(seg);
			}
			wasSpacing = isSpacing;

			segmentTextBuilder.append(currentChar);

			if(i == strippedTextCharArray.length - 1) {
				parsedSegments.add(new ParsedSegment(segmentTextBuilder.toString(), !isSpacing));
			}
		}

		/*System.out.print(this.unparsedText);
		System.out.println();
		for(ParsedSegment seg : segments) {
			System.out.print(seg.text + (!seg.tags.isEmpty() ? seg.tags.size() : ""));
		}
		System.out.println();*/

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

		System.out.print(this.unparsedText);
		System.out.println();
		for(WrapSegment wrapSeg : wrapSegments) {
			System.out.print("Wrap segment (" + wrapSeg.significant + "): ");
			System.out.println();

			for(ParsedSegment seg : wrapSeg.segments) {
				System.out.print("   - Text: " + seg.text + " Tags: " + seg.tags.size());
				System.out.println();
			}
		}
	}

	private boolean isSpacing(char c) {
		return c == ' ';
	}
}
