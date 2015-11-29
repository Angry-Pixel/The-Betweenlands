package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.recipes.DruidAltarRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 28/11/2015.
 */
public class DruidAltarWidget extends ManualWidgetsBase {
    public static int height = 74;
    public static int width = 74;
    private static ResourceLocation druidAltarGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/druidAltarGrid.png");
    ArrayList<ItemStack> outputs = new ArrayList<>();

    int progress = 0;
    int currentRecipe = 0;


    public DruidAltarWidget(ItemStack output, int xStart, int yStart) {
        super(xStart, yStart);
        if (DruidAltarRecipe.getDruidAltarRecipe(output) != null)
            this.outputs.add(output);
    }

    public DruidAltarWidget(ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(xStart, yStart);
        for (ItemStack output : outputs)
            if (DruidAltarRecipe.getDruidAltarRecipe(output) != null)
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
            manual.mc.renderEngine.bindTexture(druidAltarGrid);
            manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            renderItem(newX + 1, newY + 1, DruidAltarRecipe.getDruidAltarRecipe(outputs.get(currentRecipe)).input1, false);
            renderItem(newX + 57, newY + 1, DruidAltarRecipe.getDruidAltarRecipe(outputs.get(currentRecipe)).input2, false);
            renderItem(newX + 1, newY + 57, DruidAltarRecipe.getDruidAltarRecipe(outputs.get(currentRecipe)).input3, false);
            renderItem(newX + 57, newY + 57, DruidAltarRecipe.getDruidAltarRecipe(outputs.get(currentRecipe)).input4, false);

            renderItem(newX + 29, newY + 29, outputs.get(currentRecipe), false);

            /*if (mouseX >= xStart + 25 && mouseX <= xStart + 47 && mouseY >= yStart + 22 && mouseY <= yStart + 38) {
                ArrayList<String> processTooltip = new ArrayList<>();
                processTooltip.add(StatCollector.translateToLocal("manual.widget.purifier.recipe"));
                processTooltip.add(processTimeSecondsString.replace(".seconds.", "21.6"));
                renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
            }*/


            if (DruidAltarRecipe.getDruidAltarRecipe(outputs.get(currentRecipe)) == null) {
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
