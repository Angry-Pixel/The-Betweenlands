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
		private TextArea area;
		private List<TooltipArea> additionalAreas = new ArrayList<TooltipArea>();

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
			this.area = area.withSpace();
			container.addTextArea(new TooltipArea(this.area, this.text));
		}

		@Override
		void expand(TextContainer container, TextArea area) {
			TooltipArea newArea = new TooltipArea(area.withSpace(), this.text);
			this.additionalAreas.add(newArea);
			container.addTextArea(newArea);
		}

		@Override
		void pop(TextContainer container, TextFormatTag previous) {
			container.removeTextArea(new TooltipArea(this.area, this.text));
			for(TooltipArea additionalArea : this.additionalAreas) {
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
}
