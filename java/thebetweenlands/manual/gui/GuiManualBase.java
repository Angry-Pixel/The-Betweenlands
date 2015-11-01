package thebetweenlands.manual.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.manual.gui.entries.ManualEntry;
import thebetweenlands.manual.gui.entries.ManualEntryRegistry;
import thebetweenlands.manual.gui.widgets.*;
import thebetweenlands.recipes.RecipeHandler;

import java.util.ArrayList;


/**
 * Created by Bart on 8-8-2015.
 */
public class GuiManualBase extends GuiScreen {
    public static final int HEIGHT = 202;
    public static final int WIDTH = 127;
    private static ResourceLocation pageLeft = new ResourceLocation("thebetweenlands:textures/gui/manual/blankPageLeft.png");
    private static ResourceLocation pageRight = new ResourceLocation("thebetweenlands:textures/gui/manual/blankPageRight.png");
    //public ArrayList<ManualWidgetsBase> widgets = new ArrayList<>();

    public int xStartLeftPage;
    public int xStartRightPage;
    public int yStart;

    public int currentPage = 0;

    public ManualEntry entry;


    @Override
    public void initGui() {
        xStartLeftPage = width / 2 - WIDTH;
        xStartRightPage = width / 2;
        yStart = (height - HEIGHT) / 2;
        ManualEntryRegistry.init(this);
        entry = ManualEntryRegistry.entry1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mouseX, int mouseY, float renderPartials) {
        super.drawScreen(mouseX, mouseY, renderPartials);
        mc.renderEngine.bindTexture(pageLeft);
        drawTexturedModalRect(xStartLeftPage, yStart, 0, 0, WIDTH, HEIGHT);
        mc.renderEngine.bindTexture(pageRight);
        drawTexturedModalRect(xStartRightPage, yStart, 0, 0, WIDTH, HEIGHT);
        entry.draw(mouseX, mouseY);
    }


    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE: {
                mc.displayGuiScreen(null);
                break;
            }
            case Keyboard.KEY_0:{
                entry = ManualEntryRegistry.entry;
                break;
            }
            case Keyboard.KEY_1:{
                entry = ManualEntryRegistry.entry1;
                break;
            }
            case Keyboard.KEY_2:{
                entry = ManualEntryRegistry.entry2;
                break;
            }
            case Keyboard.KEY_3:{
                entry = ManualEntryRegistry.entry3;
                break;
            }
            case Keyboard.KEY_4:{
                entry = ManualEntryRegistry.entry4;
                break;
            }
            case Keyboard.KEY_5:{
                entry = ManualEntryRegistry.entry5;
                break;
            }
            case Keyboard.KEY_6:{
                entry = ManualEntryRegistry.entry6;
                break;
            }
            case Keyboard.KEY_7:{
                entry = ManualEntryRegistry.entry7;
                break;
            }
            case Keyboard.KEY_8:{
                entry = ManualEntryRegistry.entry8;
                break;
            }
            case Keyboard.KEY_9:{
                entry = ManualEntryRegistry.entry9;
                break;
            }
        }
        entry.keyTyped(c, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        if (button == 0) {
            entry.nextPage();
        } else if (button == 1) {
            entry.previousPage();
        }
        entry.mouseClicked(x, y, button);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        entry.updateScreen();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }


    public void drawString(String string, int x, int y, int color) {
        fontRendererObj.drawString(string, x, y, color);
    }

}
