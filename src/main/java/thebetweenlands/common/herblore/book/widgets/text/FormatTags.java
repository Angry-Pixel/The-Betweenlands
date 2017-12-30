package thebetweenlands.common.herblore.book.widgets.text;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.TextFormatting;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.RangedTag;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.Tag;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.TextArea;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.TooltipArea;

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
            if(pass == 0) {
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
            if(pass == 0) {
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
            if(pass == 0) {
                this.scale = Float.parseFloat(argument);
                container.setCurrentScale(this.scale);
                return true;
            }
            return false;
        }

        @Override
        void pop(TextContainer container, RangedTag previous) {
            container.setCurrentScale(((TagScale)previous).scale);
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
            if(pass == 0) {
                this.color = Integer.decode(argument);
                container.setCurrentColor(this.color);
                return true;
            }
            return false;
        }

        @Override
        void pop(TextContainer container, RangedTag previous) {
            container.setCurrentColor(((TagColor)previous).color);
        }
    }

    public static class TagTooltip extends RangedTag {
        private String text;
        private List<TooltipArea> tooltipAreas = new ArrayList<TooltipArea>();

        public TagTooltip(String text) {
            super("tooltip");
            this.text = text;
        }

        @Override
        RangedTag create() {
            return new TagTooltip("N/A");
        }

        @Override
        boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
            if(pass == 1) {
                this.text = argument;
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
            if(this.tooltipAreas.size() > 0) {
                TextArea prev = this.tooltipAreas.get(this.tooltipAreas.size() - 1);
                prev.setBounds(prev);
            }
            TooltipArea newArea = new TooltipArea(area, this.text);
            this.tooltipAreas.add(newArea);
            container.addTextArea(newArea);
        }

        @Override
        void pop(TextContainer container, RangedTag previous) {
            for(TooltipArea tooltipArea : this.tooltipAreas) {
                container.removeTextArea(tooltipArea);
            }
        }
    }

    public static class TagSimple extends RangedTag {
        private final TextFormatting format;
        private final String name;

        public TagSimple(String name, TextFormatting format) {
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
            if(pass == 0) {
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

        public TagPagelink() {
            super("pagelink");
        }

        @Override
        RangedTag create() {
            return new TagPagelink();
        }

        @Override
        boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
            if(pass == 1) {
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
            if(this.pagelinkAreas.size() > 0) {
                TextArea prev = this.pagelinkAreas.get(this.pagelinkAreas.size() - 1);
                prev.setBounds(prev);
            }
            PagelinkArea newArea = new PagelinkArea(area, this.page);
            this.pagelinkAreas.add(newArea);
            container.addTextArea(newArea);
        }

        @Override
        void pop(TextContainer container, RangedTag previous) {
            for(PagelinkArea pagelinkArea : this.pagelinkAreas) {
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
            if(pass == 1) {
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
        private FontRenderer font;

        public TagFont() {
            super("font");
        }

        @Override
        Tag create() {
            return new TagFont();
        }

        @Override
        boolean push(TextContainer container, Tag previous, String argument, TextArea area, int pass) {
            if(pass == 0) {
                FontRenderer font;
                //Just two fonts for now, the vanilla one and the custom one for herblore book titles
                switch(argument) {
                    default:
                    case "default":
                        font = container.getDefaultFont();
                        break;
                    case "custom":
                        font = TheBetweenlands.proxy.getCustomFontRenderer();
                        break;
                }
                this.font = font;
                container.setCurrentFont(font);
                return true;
            }
            return false;
        }

        @Override
        void pop(TextContainer container, RangedTag previous) {
            if(previous != null) {
                container.setCurrentFont(((TagFont)previous).font);
            } else {
                container.setCurrentFont(container.getDefaultFont());
            }
        }
    }
}
