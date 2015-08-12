package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.manual.gui.GuiManualBase;
import thebetweenlands.recipes.PurifierRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 9-8-2015.
 */
public class PurifierRecipeWidget extends ManualWidgetsBase {
    public static int height = 58;
    public static int width = 82;
    private static ResourceLocation purifierGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/purifierGrid.png");
    ArrayList<ItemStack> inputs = new ArrayList<>();

    int progress = 0;
    int untilUpdate = 0;
    int currentRecipe = 0;


    public PurifierRecipeWidget(GuiManualBase manual, ItemStack input, int xStart, int yStart) {
        super(manual, xStart, yStart);
        this.inputs.add(input);
    }

    public PurifierRecipeWidget(GuiManualBase manual, ArrayList<ItemStack> inputs, int xStart, int yStart) {
        super(manual, xStart, yStart);
        this.inputs = inputs;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        int newX = xStart + 1;
        int newY = yStart + 1;


        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        manual.mc.renderEngine.bindTexture(purifierGrid);
        manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
        manual.mc.renderEngine.bindTexture(icons);
        manual.drawTexturedModalRect(xStart + 25, yStart + 22, 0, 0, progress, 16);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderItem(newX, newY, inputs.get(currentRecipe), false);
        renderItem(newX, newY + 40, new ItemStack(BLItemRegistry.materialsBL, 1, ItemMaterialsBL.EnumMaterialsBL.SULFUR.ordinal()), false);
        renderItem(newX + 60, newY + 20, PurifierRecipe.getRecipeOutput(inputs.get(currentRecipe)), false);

        if (mouseX >= xStart + 25 && mouseX <= xStart + 47 && mouseY >= yStart + 22 && mouseY <= yStart + 38) {
            ArrayList<String> processTooltip = new ArrayList<>();
            processTooltip.add(StatCollector.translateToLocal("manual.widget.purifier.recipe"));
            processTooltip.add(processTimeSecondsString.replace(".seconds.", "21.6"));
            renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        if (untilUpdate >= 20) {
            if (progress <= 22) {
                progress++;
                drawForeGround();
            } else {
                progress = 0;
                if (currentRecipe + 1 < inputs.size())
                    currentRecipe++;
                else
                    currentRecipe = 0;
            }
            untilUpdate = 0;
        } else untilUpdate++;
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        if (mouseButton == 2 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height) {
            if (currentRecipe + 1 < inputs.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            untilUpdate = 0;
            progress = 0;
        }
    }
}
