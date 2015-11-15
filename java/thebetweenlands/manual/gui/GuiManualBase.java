package thebetweenlands.manual.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.entries.ManualEntryItem;
import thebetweenlands.manual.gui.entries.ManualEntryRegistry;


/**
 * Created by Bart on 8-8-2015.
 */
public class GuiManualBase extends GuiScreen {
    public static final int HEIGHT = 180;
    public static final int WIDTH = 146;
    private static ResourceLocation pageLeft = new ResourceLocation("thebetweenlands:textures/gui/manual/blankPageLeft.png");
    private static ResourceLocation pageRight = new ResourceLocation("thebetweenlands:textures/gui/manual/blankPageRight.png");
    //public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();

    public int xStartLeftPage;
    public int xStartRightPage;
    public int yStart;


    public ManualEntry entry;

    private EntityPlayer player;


    @Override
    public void initGui() {
        player = mc.thePlayer;
        xStartLeftPage = width / 2 - WIDTH;
        xStartRightPage = width / 2;
        yStart = (height - HEIGHT) / 2;
        ManualEntryRegistry.init(this);
        if (player.getHeldItem() == null || player.getHeldItem().getItem() != BLItemRegistry.manual)
            mc.displayGuiScreen(null);
        if (player.getHeldItem().getTagCompound() != null && player.getHeldItem().stackTagCompound.hasKey("entry") && player.getHeldItem().stackTagCompound.getString("entry") != null) {
            changeTo(getEntryFromName(player.getHeldItem().stackTagCompound.getString("entry")));
        } else {
            changeTo(ManualEntryRegistry.main);
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mouseX, int mouseY, float renderPartials) {
        super.drawScreen(mouseX, mouseY, renderPartials);
        if (entry != null) {
            mc.renderEngine.bindTexture(pageLeft);
            drawTexturedModalRect(xStartLeftPage, yStart, 0, 0, WIDTH, HEIGHT);
            if (entry.currentPage - 2 >= 0) {
                if (!(mouseX >= xStartLeftPage + 112 && mouseX <= xStartLeftPage + 112 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 9))
                    drawTexturedModalRect(xStartLeftPage + 112, yStart + 160, 147, 0, 19, 9);
                else
                    drawTexturedModalRect(xStartLeftPage + 112, yStart + 160, 147, 9, 19, 9);
            }
            mc.renderEngine.bindTexture(pageRight);
            drawTexturedModalRect(xStartRightPage, yStart, 0, 0, WIDTH, HEIGHT);
            if (entry.currentPage + 2 < entry.pages.size()) {
                if (!(mouseX >= xStartRightPage + 14 && mouseX <= xStartRightPage + 14 + 19 && mouseY >= yStart + 160 && mouseY <= yStart + 160 + 9))
                    drawTexturedModalRect(xStartRightPage + 14, yStart + 160, 147, 0, 19, 9);
                else
                    drawTexturedModalRect(xStartRightPage + 14, yStart + 160, 147, 9, 19, 9);
            }
            entry.draw(mouseX, mouseY);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        entry.clear();
    }

    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE: {
                mc.displayGuiScreen(null);
                break;
            }
            case Keyboard.KEY_0: {
                changeTo(ManualEntryRegistry.main);
                break;
            }
            case Keyboard.KEY_1: {
                changeTo(ManualEntryRegistry.itemList);
                break;
            }
        }
        if (entry != null)
            entry.keyTyped(c, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        if (entry != null) {
            if (button == 0 && x >= xStartRightPage + 14 && x <= xStartRightPage + 14 + 19 && y >= yStart + 160 && y <= yStart + 160 + 9) {
                entry.nextPage();
            } else if (button == 0 && x >= xStartLeftPage + 112 && x <= xStartLeftPage + 122 + 19 && y >= yStart + 160 && y <= yStart + 160 + 9) {
                entry.previousPage();
            }
            entry.mouseClicked(x, y, button);
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (entry != null)
            entry.updateScreen();
    }

    public void changeTo(ManualEntry entry) {
        if (this.entry != null)
            this.entry.clear();
        this.entry = entry;
        if (player.getHeldItem() != null && this.entry != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("entry", this.entry.unlocalizedEntryName);
            player.getHeldItem().stackTagCompound = tag;
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }


    public void drawString(String string, int x, int y, int color) {
        fontRendererObj.drawString(string, x, y, color);
    }


    public ManualEntry getEntryFromItem(ItemStack item) {
        for (ManualEntryItem entry : ManualEntryRegistry.itemEntries) {
            for (ItemStack stack : entry.items)
                if (matches(stack, item)) {
                    return entry;
                }
        }

        return null;
    }

    private ManualEntry getEntryFromName(String entryName) {
        for (ManualEntry entry : ManualEntryRegistry.ENTRIES) {
            if (entry.unlocalizedEntryName.equals(entryName)) {
                return entry;
            }
        }
        return ManualEntryRegistry.main;
    }

    private boolean matches(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack2.getItem() == itemStack1.getItem() && itemStack2.getItemDamage() == itemStack1.getItemDamage();
    }

}
