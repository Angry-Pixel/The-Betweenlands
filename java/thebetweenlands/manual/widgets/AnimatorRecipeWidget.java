package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.recipes.misc.AnimatorRecipe;

import java.util.ArrayList;

/**
 * Created by Bart on 09/12/2015.
 */
public class AnimatorRecipeWidget extends ManualWidgetsBase {
    public static int width = 108;
    public static int height = 67;
    private static ResourceLocation animatorGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/animatorGrid.png");
    public boolean input = true;
    ArrayList<ItemStack> outputs = new ArrayList<>();
    int progress = 0;
    int currentRecipe = 0;

    public AnimatorRecipeWidget(ItemStack output, int xStart, int yStart) {
        super(xStart, yStart);
        if (AnimatorRecipe.getRecipeFromOutput(output) != null)
            outputs.add(output);
    }

    public AnimatorRecipeWidget(ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(xStart, yStart);
        for (ItemStack output : outputs)
            if (AnimatorRecipe.getRecipeFromOutput(output) != null)
                this.outputs.add(output);
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        if (outputs.size() > 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            manual.mc.renderEngine.bindTexture(animatorGrid);
            manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            AnimatorRecipe recipe = AnimatorRecipe.getRecipeFromOutput(outputs.get(currentRecipe));
            if (recipe != null) {
                renderItem(xStart + 46, yStart + 16, outputs.get(currentRecipe), false, false, manual.manualType);
                renderItem(xStart + 1, yStart + 16, recipe.input, false, true, manual.manualType);
                renderItem(xStart + 1, yStart + 50, new ItemStack(BLItemRegistry.lifeCrystal), false, true, manual.manualType);
                renderItem(xStart + 91, yStart + 50, ItemGeneric.createStack(ItemGeneric.EnumItemGeneric.SULFUR), false, true, manual.manualType);
            }
            if (mouseX >= xStart + 18 && mouseX <= xStart + 89 && mouseY >= yStart + 42 && mouseY <= yStart + 66) {
                ArrayList<String> recipeToolTips = new ArrayList<>();
                recipeToolTips.add(StatCollector.translateToLocal("manual.widget.animator.recipe"));
                renderTooltip(mouseX, mouseY, recipeToolTips, 0xffffff, 0xf0100010);
            }
        } else
            isEmpty = true;
    }


    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual.untilUpdate % 20 == 0) {
            if (progress <= 21) {
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
