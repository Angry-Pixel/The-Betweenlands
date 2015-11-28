package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.manual.GuiManualBase;
import thebetweenlands.manual.ManualManager;
import thebetweenlands.manual.Page;
import thebetweenlands.manual.widgets.text.TextContainer;
import thebetweenlands.manual.widgets.text.TextContainer.TextPage;
import thebetweenlands.manual.widgets.text.TextFormatComponents;

import java.util.ArrayList;

/**
 * Created by Bart on 6-11-2015.
 */
public class ButtonWidget extends ManualWidgetsBase {
    private ArrayList<ItemStack> items = new ArrayList<>();
    public int pageNumber;
    private TextContainer textContainer;

    private ResourceLocation resourceLocation;
    private Page page;

    public boolean isHidden;

    public static int width = 100;
    public static int height = 16;
    int currentItem;

    public ButtonWidget(int xStart, int yStart, Page page) {
        super(xStart, yStart);
        this.pageNumber = page.pageNumber;
        this.page = page;
        if (page.pageItems.size() > 0)
            this.items.addAll(page.pageItems);
        else if (page.resourceLocation != null)
            this.resourceLocation = new ResourceLocation(page.resourceLocation);
        this.textContainer = new TextContainer(100, 16, page.pageName);
        this.isHidden = page.isHidden;
        this.init();
    }

    @Override
    public void init(GuiManualBase manual) {
        super.init(manual);
        if (isHidden)
            this.isHidden = !ManualManager.hasFoundPage(manual.player, page.unlocalizedPageName);
    }


    @Override
    public void setPageToRight() {
        super.setPageToRight();
        this.textContainer = new TextContainer(100, 16, page.pageName);
        this.init();
    }

    public void init() {
        this.textContainer.setCurrentScale(1f).setCurrentColor(0x808080).setCurrentFormat("");
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatScale(1.0F));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatColor(0x808080));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatTooltip("N/A"));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("bold", EnumChatFormatting.BOLD));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("italic", EnumChatFormatting.ITALIC));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
        this.textContainer.registerFormat(new TextFormatComponents.TextFormatSimple("underline", EnumChatFormatting.UNDERLINE));

        try {
            this.textContainer.parse();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        if (items.size() > 0)
            renderItem(xStart, yStart, items.get(currentItem), false);
        else if (resourceLocation != null) {
            Minecraft.getMinecraft().renderEngine.bindTexture(resourceLocation);
            manual.drawTexture(xStart, yStart, 16, 16, page.textureWidth, page.textureHeight, page.xStartTexture, page.xEndTexture, page.yStartTexture, page.yEndTexture);
        }
        if (isHidden){
            GL11.glColor3f(0.545f, 0.0f, 0.0f);
        }

        TextPage page = this.textContainer.getPages().get(0);
        page.render(this.xStart + 18, this.yStart + 4);
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + 16 && y >= yStart && y <= yStart + height) {
            if (currentItem + 1 < items.size()) {
                currentItem++;
            } else
                currentItem = 0;
            drawForeGround();
            manual.untilUpdate = 0;
        } else if (mouseButton == 0 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height && !isHidden) {
            manual.changeTo(pageNumber);
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
        this.textContainer = new TextContainer(116, 144, page.pageName);
        this.init();
    }
}
