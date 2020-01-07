package thebetweenlands.common.herblore.book.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
    public boolean isPageHidden;
    public int width = 100;
    public int height = 16;
    int currentItem;
    boolean renderSomething = true;
    boolean doMathWithIndexPages = true;
    private List<ItemStack> items = NonNullList.create();
    private IAspectType aspect;
    private TextContainer textContainer;
    private ResourceLocation resourceLocation;
    private Page page;
    private boolean isFullyDisovered = false;

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

        this.isPageHidden = page.isHidden;
        this.initTextContainer();
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
        
        if (this.page != null) {
        	this.pageNumber = this.page.pageNumber;
        	
        	if(!this.page.isHidden) {
        		this.isPageHidden = false;
        	} else {
        		this.isPageHidden = !ManualManager.hasFoundPage(manual.player, page.unlocalizedPageName, manual.manualType);
        		//This code is a mess and this is the only way to do this without rewriting a ton.
        		//Hidden pages are considered to always be ingredient pages..
        		this.isFullyDisovered = ManualManager.isFullyDiscovered(manual.player, page.unlocalizedPageName);
        	}
        	
        	this.initTextContainer();
        }
    }


    @Override
    public void setPageToRight() {
        super.setPageToRight();
        if (renderSomething) {
            this.initTextContainer();
        }
    }

    public void initTextContainer() {
    	if (this.page != null && this.manual != null) {
        	String text = this.page.pageName;
        	if(!this.isPageHidden) {
        		text = "<underline>" + text + "</underline>";
        	}
        	if(this.isFullyDisovered) {
        		text = "<color:0x559030>" + text + "</color>";
        	}
        	this.textContainer = new TextContainer(84, 22, text, Minecraft.getMinecraft().fontRenderer);
        
        	this.textContainer.setCurrentScale(1f).setCurrentColor(0x606060);
            this.textContainer.registerTag(new FormatTags.TagScale(1.0F));
            this.textContainer.registerTag(new FormatTags.TagColor(0x606060));
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
    	} else {
    		this.textContainer = null;
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

            if(this.textContainer != null) {
            	 TextContainer.TextPage page = this.textContainer.getPages().get(0);
                 page.render(this.xStart + 18, this.yStart + 2);
            }
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
        } else if (mouseButton == 0 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height && !isPageHidden) {
            List<Page> visiblePages = manual.currentCategory.getVisiblePages();
            int targetPage = -1;
            for(int i = 0; i < visiblePages.size(); i++) {
            	if(visiblePages.get(i).pageNumber == this.pageNumber) {
            		targetPage = i;
            	}
            }
            if(targetPage >= 0) {
            	//no idea what doMathWithIndexPages does but this only works with 'false'
            	manual.changeTo(targetPage + 1, false);
            }
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
            this.initTextContainer();
        }
    }
}

