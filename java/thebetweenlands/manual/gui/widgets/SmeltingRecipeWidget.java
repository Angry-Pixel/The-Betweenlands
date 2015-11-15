package thebetweenlands.manual.gui.widgets;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.manual.gui.GuiManualBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Bart on 9-8-2015.
 */
public class SmeltingRecipeWidget extends ManualWidgetsBase {
    public static int height = 54;
    public static int width = 82;
    private static ResourceLocation furnaceGrid = new ResourceLocation("thebetweenlands:textures/gui/manual/furnaceGrid.png");
    ArrayList<ItemStack> outputs = new ArrayList<>();

    int progress = 0;
    int untilUpdate = 0;
    int currentRecipe = 0;

    public SmeltingRecipeWidget(GuiManualBase manual, ItemStack output, int xStart, int yStart) {
        super(manual, xStart, yStart);
        if (getSmeltingIngredient(output) != null)
            this.outputs.add(output);
    }

    public SmeltingRecipeWidget(GuiManualBase manual, ArrayList<ItemStack> outputs, int xStart, int yStart) {
        super(manual, xStart, yStart);
        for (ItemStack output : outputs)
            if (getSmeltingIngredient(output) != null)
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
            manual.mc.renderEngine.bindTexture(furnaceGrid);
            manual.drawTexturedModalRect(xStart, yStart, 0, 0, width, height);
            manual.mc.renderEngine.bindTexture(icons);
            manual.drawTexturedModalRect(xStart + 25, yStart + 17, 0, 0, progress, 16);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            renderItem(newX, newY, getSmeltingIngredient(outputs.get(currentRecipe)), false);
            renderItem(newX + 60, newY + 16, outputs.get(currentRecipe), false);
            renderItem(newX, newY + 36, new ItemStack(BLItemRegistry.itemsGeneric, 1, ItemGeneric.EnumItemGeneric.SULFUR.ordinal()), true);
            ArrayList<String> specialToolTips = new ArrayList<>();
            specialToolTips.add(burnTimeString.replace(".time.", "800"));
            addSpecialItemTooltip(newX, newY + 36, new ItemStack(BLItemRegistry.itemsGeneric, 1, ItemGeneric.EnumItemGeneric.SULFUR.ordinal()), specialToolTips);

            if (mouseX >= xStart + 25 && mouseX <= xStart + 47 && mouseY >= yStart + 17 && mouseY <= yStart + 33) {
                ArrayList<String> processTooltip = new ArrayList<>();
                processTooltip.add(StatCollector.translateToLocal("manual.widget.smelting.recipe"));
                processTooltip.add(processTimeSecondsString.replace(".seconds.", "10"));
                renderTooltip(mouseX, mouseY, processTooltip, 0xffffff, 0xf0100010);
            }
        } else
            isEmpty = true;
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
                if (currentRecipe + 1 < outputs.size())
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
            if (currentRecipe + 1 < outputs.size()) {
                currentRecipe++;
            } else
                currentRecipe = 0;
            drawForeGround();
            untilUpdate = 0;
            progress = 0;
        }
    }


    public ItemStack getSmeltingIngredient(ItemStack output) {
        Iterator iterator = FurnaceRecipes.smelting().getSmeltingList().entrySet().iterator();
        Map.Entry entry;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            entry = (Map.Entry) iterator.next();
        }
        while (!this.matches((ItemStack) entry.getValue(), output));
        return (ItemStack) entry.getKey();
    }

    private boolean matches(ItemStack itemStack1, ItemStack itemstack2) {
        return itemstack2.getItem() == itemStack1.getItem() && (itemstack2.getItemDamage() == 32767 || itemstack2.getItemDamage() == itemStack1.getItemDamage());
    }


}
