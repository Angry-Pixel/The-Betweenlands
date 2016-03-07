package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.recipes.PurifierRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 9-8-2015.
 */
public class PurifierRecipeWidget extends ManualWidgetsBase {
    public static int height = 58;
    public static int width = 82;
    private static ResourceLocation purifierGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/purifierGrid.png");
    ArrayList<ItemStack> outputs = new ArrayList<>();

    int progress = 0;
    int currentRecipe = 0;


    public PurifierRecipeWidget(ItemStack output, int xStart, int yStart) {
        super(xStart, yStart);
        if (PurifierRecipe.getRecipeInput(output) != null)
            this.outputs.add(output);
    }

    public PurifierRecipeWidget(ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(xStart, yStart);
        for (ItemStack output : outputs)
            if (PurifierRecipe.getRecipeInput(output) != null)
                this.outputs.add(output);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        if (outputs.size() > 0) {
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

            renderItem(newX, newY, PurifierRecipe.getRecipeInput(outputs.get(currentRecipe)), false, true, manual.manualType);
            renderItem(newX, newY + 40, new ItemStack(BLItemRegistry.itemsGeneric, 1, ItemGeneric.EnumItemGeneric.SULFUR.id), false, true, manual.manualType);
            renderItem(newX + 60, newY + 20, outputs.get(currentRecipe), false, false, manual.manualType);

            if (mouseX >= xStart + 25 && mouseX <= xStart + 47 && mouseY >= yStart + 22 && mouseY <= yStart + 38) {
                ArrayList<String> processTooltip = new ArrayList<>();
                processTooltip.add(StatCollector.translateToLocal("manual.widget.purifier.recipe"));
                processTooltip.add(processTimeSecondsString.replace(".seconds.", "21.6"));
                renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
            }


            if (PurifierRecipe.getRecipeInput(outputs.get(currentRecipe)) == null) {
                outputs.remove(currentRecipe);
                currentRecipe = 0;
            }
        } else
            isEmpty = true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual.untilUpdate % 20 == 0) {
            if (progress <= 22) {
                progress++;
                drawForeGround();
            } else {
                progress = 0;
                if (currentRecipe + 1 < outputs.size())
                    currentRecipe++;
                else
                    currentRecipe = 0;
            }
        }
    }

    @Override
    public void mouseClicked(int x, int y, int mouseButton) {
        super.mouseClicked(x, y, mouseButton);
        if (mouseButton == 2 && x >= xStart && x <= xStart + width && y >= yStart && y <= yStart + height) {
            if (currentRecipe + 1 < outputs.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            progress = 0;
        }
    }
}
