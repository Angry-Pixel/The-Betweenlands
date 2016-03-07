package thebetweenlands.manual.widgets;

import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

/**
 * Created by Bart on 8-8-2015.
 */
public class CraftingRecipeWidget extends ManualWidgetsBase {
    public static int height = 54;
    public static int width = 116;
    private static ResourceLocation craftingGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/craftingGrid.png");
    ArrayList<ItemStack> outputs = new ArrayList<>();
    private int currentRecipe = 0;

    public CraftingRecipeWidget( ItemStack output, int xStart, int yStart) {
        super( xStart, yStart);
        if (getRecipe(output) != null)
            this.outputs.add(output);
    }

    public CraftingRecipeWidget(ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(xStart, yStart);
        for (ItemStack output : outputs)
            if (getRecipe(output) != null)
                this.outputs.add(output);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawForeGround() {
        int newX = xStart + 1;
        int newY = yStart + 1;
        if (outputs.size() > 0) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            manual.mc.renderEngine.bindTexture(craftingGrid);
            manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            IRecipe recipe = getRecipe(outputs.get(currentRecipe));
            String recipeType = "";
            if (recipe != null) {

                if (recipe instanceof ShapedRecipes) {
                    ShapedRecipes shaped = (ShapedRecipes) recipe;
                    recipeType = StatCollector.translateToLocal("manual.widget.crafting.shaped.recipe");
                    for (int y = 0; y < shaped.recipeHeight; y++)
                        for (int x = 0; x < shaped.recipeWidth; x++)
                            renderItem(newX + 18 * x, newY + 18 * y, shaped.recipeItems[y * shaped.recipeWidth + x], false, true, manual.manualType);
                } else if (recipe instanceof ShapedOreRecipe) {
                    ShapedOreRecipe shaped = (ShapedOreRecipe) recipe;
                    recipeType = StatCollector.translateToLocal("manual.widget.crafting.shaped.recipe");
                    int width = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 4);
                    int height = ReflectionHelper.getPrivateValue(ShapedOreRecipe.class, shaped, 5);

                    for (int y = 0; y < height; y++)
                        for (int x = 0; x < width; x++) {
                            Object input = shaped.getInput()[y * width + x];
                            if (input != null)
                                renderItem(newX + 18 * x, newY + 18 * y, input instanceof ItemStack ? (ItemStack) input : ((ArrayList<ItemStack>) input).get(0), false, true, manual.manualType);
                        }
                } else if (recipe instanceof ShapelessRecipes) {
                    ShapelessRecipes shapeless = (ShapelessRecipes) recipe;
                    recipeType = StatCollector.translateToLocal("manual.widget.crafting.shapeless.recipe");
                    drawGrid:
                    {
                        for (int y = 0; y < 3; y++)
                            for (int x = 0; x < 3; x++) {
                                int index = y * 3 + x;
                                if (index >= shapeless.recipeItems.size())
                                    break drawGrid;
                                renderItem(newX + 18 * x, newY + 18 * y, (ItemStack) shapeless.recipeItems.get(index), false, true, manual.manualType);
                            }
                    }
                } else if (recipe instanceof ShapelessOreRecipe) {
                    ShapelessOreRecipe shapeless = (ShapelessOreRecipe) recipe;
                    recipeType = StatCollector.translateToLocal("manual.widget.crafting.shapeless.recipe");
                    drawGrid:
                    {
                        for (int y = 0; y < 3; y++)
                            for (int x = 0; x < 3; x++) {
                                int index = y * 3 + x;
                                if (index >= shapeless.getRecipeSize())
                                    break drawGrid;
                                Object input = shapeless.getInput().get(index);
                                if (input != null)
                                    renderItem(newX + 18 * x, newY + 18 * y, input instanceof ItemStack ? (ItemStack) input : ((ArrayList<ItemStack>) input).get(0), false, true, manual.manualType);
                            }
                    }
                }
                renderItem(newX + 94, newY + 18, recipe.getRecipeOutput(), false, false, manual.manualType);
            }

            if (mouseX >= xStart + 61 && mouseX <= xStart + 61 + 22 && mouseY >= yStart + 19 && mouseY <= yStart + 19 + 15) {
                ArrayList<String> processTooltip = new ArrayList<>();
                processTooltip.add(recipeType);
                renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
            }
        } else
            isEmpty = true;
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
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateScreen() {
        super.updateScreen();
        if (manual.untilUpdate % 200 == 0) {
            if (currentRecipe + 1 < outputs.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
        }
    }

    public IRecipe getRecipe(ItemStack output) {
        for (Object obj : CraftingManager.getInstance().getRecipeList()) {
            if (obj instanceof IRecipe) {
                if (matches(((IRecipe) obj).getRecipeOutput(), output))
                    return (IRecipe) obj;
            }
        }
        return null;
    }

    private boolean matches(ItemStack itemStack1, ItemStack itemstack2) {
        return itemStack1 != null && itemstack2 != null && itemstack2.getItem() == itemStack1.getItem() && itemstack2.getItemDamage() == itemStack1.getItemDamage();
    }
}
