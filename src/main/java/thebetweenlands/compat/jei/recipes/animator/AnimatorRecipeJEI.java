package thebetweenlands.compat.jei.recipes.animator;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.animator.ToolRepairAnimatorRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnimatorRecipeJEI implements IRecipeWrapper {
    private IAnimatorRecipe animatorRecipe;
    private final ItemStack input;
    private int requiredFuel, requiredLife;
    private ItemStack result = null;
    private String entityName = null;
    private ResourceLocation lootTableName = null;
    private IGuiIngredient<ItemStack> guiIngredient;

    public AnimatorRecipeJEI(IAnimatorRecipe animatorRecipe) {
        this.animatorRecipe = animatorRecipe;
        if (animatorRecipe instanceof AnimatorRecipe) {
            AnimatorRecipe recipe = (AnimatorRecipe) animatorRecipe;
            input = recipe.getInput();
            requiredFuel = recipe.getRequiredFuel(input);
            requiredLife = recipe.getRequiredLife(input);
            result = recipe.getResult(input);
            lootTableName = recipe.getLootTable();
            if (recipe.getSpawnEntityClass(input) != null) {
                Entity entity = null;
                try {
                    entity = recipe.getSpawnEntityClass(input).getConstructor(new Class[]{World.class}).newInstance(Minecraft.getMinecraft().world);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (entity != null)
                    entityName = entity.getName();
            }
        } else {
            ToolRepairAnimatorRecipe recipe = (ToolRepairAnimatorRecipe) animatorRecipe;
            input = new ItemStack(recipe.getTool());
            result = recipe.getResult(input);
        }
    }

    public AnimatorRecipeJEI(AnimatorRecipe animatorRecipe, World world) {
        this(animatorRecipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<List<ItemStack>> l = new ArrayList<>();
        if (animatorRecipe instanceof ToolRepairAnimatorRecipe) {
            ArrayList<ItemStack> inputs = new ArrayList<>();
            int c = Math.max(1, MathHelper.ceil(((double) input.getMaxDamage()) / 15D));
            for (int i = c; i < input.getMaxDamage(); i += c) {
                ItemStack newInput = input.copy();
                newInput.setItemDamage(i);
                inputs.add(newInput);
            }
            l.add(inputs);
        } else {
            l.add(Collections.singletonList(input));
        }
        l.add(Collections.singletonList(new ItemStack(ItemRegistry.LIFE_CRYSTAL)));
        l.add(Collections.singletonList(ItemMisc.EnumItemMisc.SULFUR.create(1)));
        ingredients.setInputLists(ItemStack.class, l);

        if (result != null)
            ingredients.setOutput(ItemStack.class, result);
        if (lootTableName != null){
            ingredients.setOutputLists(ItemStack.class, Collections.singletonList(LootTableRegistry.getItemsFromTable(lootTableName, Minecraft.getMinecraft().world, true)));
        }
    }

    public void setGuiIngredient(IGuiIngredient<ItemStack> guiIngredient) {
        this.guiIngredient = guiIngredient;
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> processTooltip = new ArrayList<>();
        if (mouseX >= 18 && mouseX <= 51 && mouseY >= 42 && mouseY <= 66) {
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.life", guiIngredient != null ? animatorRecipe.getRequiredLife(guiIngredient.getDisplayedIngredient()): requiredLife));
        }
        if (mouseX >= 57 && mouseX <= 90 && mouseY >= 42 && mouseY <= 66) {
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.fuel", guiIngredient != null ? animatorRecipe.getRequiredFuel(guiIngredient.getDisplayedIngredient()): requiredFuel));
        }
        if (entityName != null && mouseX >= 19 && mouseX <= 35 && mouseY >= 16 && mouseY <= 32) {
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.entity_spawn", entityName));
        }
        return processTooltip;
    }
}
