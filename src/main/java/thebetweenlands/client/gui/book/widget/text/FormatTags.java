package thebetweenlands.client.gui.book.widget.text;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.client.gui.book.widget.text.TextContainer.RangedTag;
import thebetweenlands.client.gui.book.widget.text.TextContainer.Tag;
import thebetweenlands.client.gui.book.widget.text.TextContainer.TextArea;
import thebetweenlands.client.gui.book.widget.text.TextContainer.TooltipArea;

import java.util.ArrayList;
import java.util.List;

public class FormatTags {

	public static class TagNewLine extends Tag {
		public TagNewLine() {
			super("nl");
		}

		@Override
		Tag create() {
			return new TagNewLine();
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				container.newLine();
				return true;
			}
			return false;
		}
	}

	public static class TagNewPage extends Tag {
		public TagNewPage() {
			super("np");
		}

		@Override
		Tag create() {
			return new TagNewPage();
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				container.newPage();
				return true;
			}
			return false;
		}
	}

	public static class TagScale extends RangedTag {
		private float scale;

		public TagScale(float scale) {
			super("scale");
			this.scale = scale;
		}

		@Override
		RangedTag create() {
			return new TagScale(this.scale);
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				this.scale = Float.parseFloat(argument);
				container.setCurrentScale(this.scale);
				return true;
			}
			return false;
		}

		@Override
		void pop(TextContainer container, RangedTag previous) {
			container.setCurrentScale(((TagScale) previous).scale);
		}
	}

	public static class TagColor extends RangedTag {
		private int color;

		public TagColor(int color) {
			super("color");
			this.color = color;
		}

		@Override
		RangedTag create() {
			return new TagColor(0x808080);
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				this.color = Integer.decode(argument);
				container.setCurrentColor(this.color);
				return true;
			}
			return false;
		}

		@Override
		void pop(TextContainer container, RangedTag previous) {
			container.setCurrentColor(((TagColor) previous).color);
		}
	}

	public static class TagTooltip extends RangedTag {
		private Component text;
		private final List<TooltipArea> tooltipAreas = new ArrayList<>();

		public TagTooltip(Component text) {
			super("tooltip");
			this.text = text;
		}

		@Override
		RangedTag create() {
			return new TagTooltip(Component.empty());
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 1) {
				this.text = Component.literal(argument);
				TooltipArea newArea = new TooltipArea(area, this.text);
				this.tooltipAreas.add(newArea);
				container.addTextArea(newArea);
				return true;
			}
			return false;
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			//Add space to previous text area
			if (!this.tooltipAreas.isEmpty()) {
				TextArea prev = this.tooltipAreas.getLast();
				prev.setBounds(prev);
			}
			TooltipArea newArea = new TooltipArea(area, this.text);
			this.tooltipAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void pop(TextContainer container, RangedTag previous) {
			for (TooltipArea tooltipArea : this.tooltipAreas) {
				container.removeTextArea(tooltipArea);
			}
		}
	}

	public static class TagSimple extends RangedTag {
		private final ChatFormatting format;
		private final String name;

		public TagSimple(String name, ChatFormatting format) {
			super(name);
			this.name = name;
			this.format = format;
		}

		@Override
		RangedTag create() {
			return new TagSimple(this.name, this.format);
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				container.addFormatting(this.format);
				return true;
			}
			return false;
		}

		@Override
		void pop(TextContainer container, RangedTag previous) {
			container.removeFormatting(this.format);
		}
	}

	public static class TagPagelink extends RangedTag {
		private String page;
		private final List<PagelinkArea> pagelinkAreas = new ArrayList<>();

		public static class PagelinkArea extends TextArea {
			public final String page;

			public PagelinkArea(TextArea area, String page) {
				super(area);
				this.page = page;
			}

			@Override
			public boolean equals(Object object) {
				if (object instanceof PagelinkArea area) {
					return super.equals(area) && area.page.equals(this.page);
				}
				return false;
			}
		}

		public TagPagelink() {
			super("pagelink");
		}

		@Override
		RangedTag create() {
			return new TagPagelink();
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 1) {
				this.page = argument;
				PagelinkArea newArea = new PagelinkArea(area, this.page);
				this.pagelinkAreas.add(newArea);
				container.addTextArea(newArea);
				return true;
			}
			return false;
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			//Add space to previous text area
			if (!this.pagelinkAreas.isEmpty()) {
				TextArea prev = this.pagelinkAreas.getLast();
				prev.setBounds(prev);
			}
			PagelinkArea newArea = new PagelinkArea(area, this.page);
			this.pagelinkAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void pop(TextContainer container, RangedTag previous) {
			for (PagelinkArea pagelinkArea : this.pagelinkAreas) {
				container.removeTextArea(pagelinkArea);
			}
		}
	}

	public static class TagRainbow extends RangedTag {
		public TagRainbow() {
			super("rb");
		}

		@Override
		RangedTag create() {
			return new TagRainbow();
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 1) {
				area.addProperty("rainbow");
				return true;
			}
			return false;
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			area.addProperty("rainbow");
		}
	}

	public static class TagFont extends RangedTag {
		private ResourceLocation font;

		public TagFont() {
			super("font");
		}

		@Override
		Tag create() {
			return new TagFont();
		}

		@Override
		boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
			if (pass == 0) {
				this.font = ResourceLocation.parse(argument);
				container.setCurrentFont(this.font);
				return true;
			}
			return false;
		}

		@Override
		void pop(TextContainer container, TextContainer.RangedTag previous) {
			if (previous != null) {
				container.setCurrentFont(((TagFont) previous).font);
			} else {
				container.setCurrentFont(container.getDefaultFont());
			}
		}
	}
}
