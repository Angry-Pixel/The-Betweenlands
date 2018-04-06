package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.book.GuiManualHerblore;
import thebetweenlands.common.herblore.book.ManualManager;
import thebetweenlands.common.herblore.book.Page;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.util.AspectIconRenderer;

@SideOnly(Side.CLIENT)
public class ButtonWidget extends ManualWidgetBase {
    public int pageNumber;
    public int color = 0x808080;
    public boolean isHidden;
    public int width = 100;
    public int height = 16;
    int currentItem;
    boolean renderSomething = true;
    boolean doMathWithIndexPages = true;
    private ArrayList<ItemStack> items = new ArrayList<>();
    private IAspectType aspect;
    private TextContainer textContainer;
    private ResourceLocation resourceLocation;
    private Page page;

    public ButtonWidget(int xStart, int yStart, Page page) {
        super(xStart, yStart);
        this.pageNumber = page.pageNumber;
        this.page = page;
        if (page.pageItems.size() > 0)
            this.items.addAll(page.pageItems);
        else if (page.pageAspects.size() > 0) {
            aspect = page.pageAspects.get(0);
        } else if (page.resourceLocation != null) {
            this.resourceLocation = new ResourceLocation(page.resourceLocation);
        }
        this.textContainer = new TextContainer(84, 22, page.pageName, Minecraft.getMinecraft().fontRenderer);

        this.isHidden = page.isHidden;
        this.init();
    }

    public ButtonWidget(int xStart, int yStart, int width, int height, int pageNumber, boolean doMathWithIndexPages) {
        super(xStart, yStart);
        this.width = width;
        this.height = height;
        this.pageNumber = pageNumber;
        renderSomething = false;
        this.doMathWithIndexPages = doMathWithIndexPages;
    }

    @Override
    public void init(GuiManualHerblore manual) {
        super.init(manual);
        if (page != null)
            this.pageNumber = page.pageNumber;
        if (isHidden)
            this.isHidden = !ManualManager.hasFoundPage(manual.player, page.unlocalizedPageName, manual.manualType);
    }


    @Override
    public void setPageToRight() {
        super.setPageToRight();
        if (renderSomething) {
            this.textContainer = new TextContainer(84, 22, page.pageName, Minecraft.getMinecraft().fontRenderer);
            this.init();
        }
    }

    public void init() {
        this.textContainer.setCurrentScale(1f).setCurrentColor(color);
        this.textContainer.registerTag(new FormatTags.TagScale(1.0F));
        this.textContainer.registerTag(new FormatTags.TagColor(0x808080));
        this.textContainer.registerTag(new FormatTags.TagTooltip("N/A"));
        this.textContainer.registerTag(new FormatTags.TagSimple("bold", TextFormatting.BOLD));
        this.textContainer.registerTag(new FormatTags.TagSimple("obfuscated", TextFormatting.OBFUSCATED));
        this.textContainer.registerTag(new FormatTags.TagSimple("italic", TextFormatting.ITALIC));
        this.textContainer.registerTag(new FormatTags.TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
        this.textContainer.registerTag(new FormatTags.TagSimple("underline", TextFormatting.UNDERLINE));

        try {
            this.textContainer.parse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        if (renderSomething) {
            if (items.size() > 0)
                renderItem(xStart, yStart, items.get(currentItem), false, false, manual.manualType);
            else if (aspect != null) {
                AspectIconRenderer.renderIcon(xStart, yStart, 16, 16, aspect.getIcon());
            } else if (resourceLocation != null) {
                Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
                manual.drawTexture(xStart, yStart, 16, 16, page.textureWidth, page.textureHeight, page.xStartTexture, page.xEndTexture, page.yStartTexture, page.yEndTexture);
            }
            if (isHidden) {
                color = 0x666666;
            } else {
                color = 0x808080;
            }

            TextContainer.TextPage page = this.textContainer.getPages().get(0);
            page.render(this.xStart + 18, this.yStart + 2);
        }
    }

    @Override
    public void drawToolTip() {
        if (aspect != null) {
            if (mouseX >= xStart && mouseX <= xStart + 16 && mouseY >= yStart && mouseY <= yStart + 16) {
                List<String> tooltipData = new ArrayList<>();
                tooltipData.add(aspect.getName());
                tooltipData.add(TextFormatting.GRAY + aspect.getType());
                renderTooltip(mouseX, mouseY, tooltipData, 0xffffff, 0xf0100010);
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        if (mouseButton == 2 && x >= xStart && x <= xStart + 16 && y >= yStart && y <= yStart + height) {
            if (currentItem + 1 < items.size()) {
                currentItem++;
            } else
                currentItem = 0;
            drawForeGround();
            manual.untilUpdate = 0;
        } else if (mouseButton == 0 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height && !isHidden) {
            manual.changeTo(pageNumber, doMathWithIndexPages);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual.untilUpdate % 20 == 0) {
            if (currentItem + 1 < items.size()) {
                currentItem++;
            } else
                currentItem = 0;
            drawForeGround();
        }
    }

    @Override
    public void resize() {
        super.resize();
        if (renderSomething) {
            this.textContainer = new TextContainer(84, 22, page.pageName, Minecraft.getMinecraft().fontRenderer);
            this.init();
        }
    }
}

