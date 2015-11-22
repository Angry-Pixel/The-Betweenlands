package thebetweenlands.manual.gui.widgets.text;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.EnumChatFormatting;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextArea;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextFormat;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextFormatTag;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TooltipArea;

public class TextFormatComponents {
	public static class TextFormatNewLine extends TextFormat {
		public TextFormatNewLine() {
			super("nl");
		}

		@Override
		TextFormat create() {
			return new TextFormatNewLine();
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			container.nextLine();
		}
	}

	public static class TextFormatScale extends TextFormatTag {
		private float scale;

		public TextFormatScale(float scale) {
			super("scale");
			this.scale = scale;
		}

		@Override
		TextFormatTag create() {
			return new TextFormatScale(1.0F);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.scale = Float.parseFloat(argument);
			container.setCurrentScale(this.scale);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			container.setCurrentScale(((TextFormatScale)previous).scale);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}

	public static class TextFormatColor extends TextFormatTag {
		private int color;

		public TextFormatColor(int color) {
			super("color");
			this.color = color;
		}

		@Override
		TextFormatTag create() {
			return new TextFormatColor(0x808080);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.color = Integer.decode(argument);
			container.setCurrentColor(this.color);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			container.setCurrentColor(((TextFormatColor)previous).color);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}

	public static class TextFormatTooltip extends TextFormatTag {
		private String text;
		private List<TooltipArea> tooltipAreas = new ArrayList<TooltipArea>();

		public TextFormatTooltip(String text) {
			super("tooltip");
			this.text = text;
		}

		@Override
		TextFormatTag create() {
			return new TextFormatTooltip("N/A");
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.text = argument;
			TooltipArea newArea = new TooltipArea(area, this.text);
			this.tooltipAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			//Add space to previous text area
			if(this.tooltipAreas.size() > 0) {
				TextArea prev = this.tooltipAreas.get(this.tooltipAreas.size() - 1);
				prev.setBounds(prev.withSpace());
			}
			TooltipArea newArea = new TooltipArea(area, this.text);
			this.tooltipAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			for(TooltipArea additionalArea : this.tooltipAreas) {
				container.removeTextArea(additionalArea);
			}
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.SECOND;
		}
	}

	public static class TextFormatSimple extends TextFormatTag {
		private final EnumChatFormatting format;
		private final String name;

		public TextFormatSimple(String name, EnumChatFormatting format) {
			super(name);
			this.name = name;
			this.format = format;
		}

		@Override
		TextFormatTag create() {
			return new TextFormatSimple(this.name, this.format);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			container.addFormatting(this.format);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			container.removeFormatting(this.format);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}

	public static class TextFormatPagelink extends TextFormatTag {
		private String page;
		private List<PagelinkArea> pagelinkAreas = new ArrayList<PagelinkArea>();

		public static class PagelinkArea extends TextArea {
			public final String page;

			public PagelinkArea(TextArea area, String page) {
				super(area);
				this.page = page;
			}

			@Override
			public boolean equals(Object object) {
				if(object instanceof PagelinkArea) {
					PagelinkArea area = (PagelinkArea) object;
					return super.equals(area) && area.page.equals(this.page);
				}
				return false;
			}
		}

		public TextFormatPagelink() {
			super("pagelink");
		}

		@Override
		TextFormatTag create() {
			return new TextFormatPagelink();
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.page = argument;
			PagelinkArea newArea = new PagelinkArea(area, this.page);
			this.pagelinkAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			//Add space to previous text area
			if(this.pagelinkAreas.size() > 0) {
				TextArea prev = this.pagelinkAreas.get(this.pagelinkAreas.size() - 1);
				prev.setBounds(prev.withSpace());
			}
			PagelinkArea newArea = new PagelinkArea(area, this.page);
			this.pagelinkAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			for(PagelinkArea additionalArea : this.pagelinkAreas) {
				container.removeTextArea(additionalArea);
			}
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.SECOND;
		}
	}
}
