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


    public void addWidgets() {
        /*if (currentPage == 0) {
            ArrayList<IRecipe> recipes = new ArrayList<>();
            recipes.add(RecipeHandler.weedwoodPickAxeRecipe);
            recipes.add(RecipeHandler.betweenstonePickAxeRecipe);
            recipes.add(RecipeHandler.octinePickAxeRecipe);
            recipes.add(RecipeHandler.valonitePickAxeRecipe);
            widgets.add(new CraftingRecipeWidget(this, recipes, xStartLeftPage + 4, yStart + 10));
            ArrayList<IRecipe> recipes1 = new ArrayList<>();
            recipes1.add(RecipeHandler.weedwoodAxeRecipe);
            recipes1.add(RecipeHandler.betweenstoneAxeRecipe);
            recipes1.add(RecipeHandler.octineAxeRecipe);
            recipes1.add(RecipeHandler.valoniteAxeRecipe);
            widgets.add(new CraftingRecipeWidget(this, recipes1, xStartRightPage + 4, yStart + 10));
            ArrayList<IRecipe> recipes2 = new ArrayList<>();
            recipes2.add(RecipeHandler.weedwoodShovelRecipe);
            recipes2.add(RecipeHandler.betweenstoneShovelRecipe);
            recipes2.add(RecipeHandler.octineShovelRecipe);
            recipes2.add(RecipeHandler.valoniteShovelRecipe);
            widgets.add(new CraftingRecipeWidget(this, recipes2, xStartLeftPage + 4, yStart + 10 + CraftingRecipeWidget.height + 4));
            ArrayList<IRecipe> recipes3 = new ArrayList<>();
            recipes3.add(RecipeHandler.weedwoodSwordRecipe);
            recipes3.add(RecipeHandler.betweenstoneSwordRecipe);
            recipes3.add(RecipeHandler.octineSwordRecipe);
            recipes3.add(RecipeHandler.valoniteSwordRecipe);
            widgets.add(new CraftingRecipeWidget(this, recipes3, xStartRightPage + 4, yStart + 10 + CraftingRecipeWidget.height + 4));
            widgets.add(new CraftingRecipeWidget(this, RecipeHandler.weedwoodBowRecipe, xStartLeftPage + 4, yStart + 10 + CraftingRecipeWidget.height * 2 + 8));
            ArrayList<IRecipe> recipes4 = new ArrayList<>();
            recipes4.add(RecipeHandler.anglerToothArrowRecipe);
            recipes4.add(RecipeHandler.octineArrowRecipe);
            widgets.add(new CraftingRecipeWidget(this, recipes4, xStartRightPage + 4, yStart + 10 + CraftingRecipeWidget.height * 2 + 8));
        } else if (currentPage == 1) {
            widgets.add(new CraftingRecipeWidget(this, RecipeHandler.weedwoodDoorRecipe, xStartLeftPage + 4, yStart + 10));
            widgets.add(new SmeltingRecipeWidget(this, new ItemStack(Blocks.cobblestone), xStartRightPage + 10, yStart + 10));
            widgets.add(new PurifierRecipeWidget(this, new ItemStack(BLBlockRegistry.aquaMiddleGemOre), xStartRightPage + 10, yStart + 14 + SmeltingRecipeWidget.height));
        } else if (currentPage == 2) {
            widgets.add(new CompostRecipeWidget(this, xStartLeftPage + 4, yStart + 10));
            widgets.add(new PestleAndMortarRecipeWidget(this, xStartRightPage + 2, yStart + 10, new ItemStack(BLBlockRegistry.weedwoodBark)));
        }*/
    }


    @Override
    protected void keyTyped(char c, int key) {
        switch (key) {
            case Keyboard.KEY_ESCAPE: {
                mc.displayGuiScreen(null);
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
