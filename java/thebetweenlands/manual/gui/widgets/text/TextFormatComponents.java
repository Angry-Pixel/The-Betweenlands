package thebetweenlands.manual.gui.widgets.text;

import net.minecraft.util.EnumChatFormatting;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextArea;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TextFormat;
import thebetweenlands.manual.gui.widgets.text.TextContainer.TooltipArea;

public class TextFormatComponents {
	public static class TextFormatScale extends TextFormat {
		private float scale;

		public TextFormatScale(float scale) {
			super("scale");
			this.scale = scale;
		}

		@Override
		TextFormat create() {
			return new TextFormatScale(1.0F);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.scale = Float.parseFloat(argument);
			container.setCurrentScale(this.scale);
		}

		@Override
		void pop(TextContainer container, TextFormat previous) {
			container.setCurrentScale(((TextFormatScale)previous).scale);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}

	public static class TextFormatColor extends TextFormat {
		private int color;

		public TextFormatColor(int color) {
			super("color");
			this.color = color;
		}

		@Override
		TextFormat create() {
			return new TextFormatColor(0x808080);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.color = Integer.decode(argument);
			container.setCurrentColor(this.color);
		}

		@Override
		void pop(TextContainer container, TextFormat previous) {
			container.setCurrentColor(((TextFormatColor)previous).color);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}

	public static class TextFormatTooltip extends TextFormat {
		private String text;
		private TextArea area;

		public TextFormatTooltip(String text) {
			super("tooltip");
			this.text = text;
		}

		@Override
		TextFormat create() {
			return new TextFormatTooltip("N/A");
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			this.text = argument;
			this.area = area;
			container.addTooltipArea(new TooltipArea(area, this.text));
		}

		@Override
		void pop(TextContainer container, TextFormat previous) {
			container.removeTooltipArea(new TooltipArea(this.area, this.text));
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.SECOND;
		}
	}

	public static class TextFormatSimple extends TextFormat {
		private final EnumChatFormatting format;
		private final String name;

		public TextFormatSimple(String name, EnumChatFormatting format) {
			super(name);
			this.name = name;
			this.format = format;
		}

		@Override
		TextFormat create() {
			return new TextFormatSimple(this.name, this.format);
		}

		@Override
		void push(TextContainer container, TextFormat previous, String argument, TextArea area) {
			container.addFormatting(this.format);
		}

		@Override
		void pop(TextContainer container, TextFormat previous) {
			container.removeFormatting(this.format);
		}

		@Override
		EnumPushOrder getPushOrder() {
			return EnumPushOrder.FIRST;
		}
	}
}
