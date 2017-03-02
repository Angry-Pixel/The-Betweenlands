package thebetweenlands.compat.jei.recipes.animator;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;
import thebetweenlands.util.TranslationHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AnimatorRecipeJEI extends BlankRecipeWrapper {
    private final ItemStack input;
    private int requiredFuel, requiredLife;
    private ItemStack result = null;
    private String entityName = null;
    private ResourceLocation lootTableName = null;
    private World world = null;

    public AnimatorRecipeJEI(AnimatorRecipe animatorRecipe) {
        input = animatorRecipe.getInput();
        requiredFuel = animatorRecipe.getRequiredFuel(input);
        requiredLife = animatorRecipe.getRequiredLife(input);
        result = animatorRecipe.getResult(input);
        lootTableName = animatorRecipe.getLootTable();
        if (animatorRecipe.getSpawnEntityClass(input) != null) {
            Entity entity = null;
            try {
                entity = (Entity) animatorRecipe.getSpawnEntityClass(input).getConstructor(new Class[]{World.class}).newInstance(Minecraft.getMinecraft().theWorld);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if (entity != null)
                entityName = entity.getName();
        }
    }
    public AnimatorRecipeJEI(AnimatorRecipe animatorRecipe, World world) {
        this(animatorRecipe);
        this.world = world;
    }
    @Override
    public void getIngredients(IIngredients ingredients) {
        ArrayList<ItemStack> l = new ArrayList();
        l.add(input);
        l.add(new ItemStack(ItemRegistry.LIFE_CRYSTAL));
        l.add(ItemMisc.EnumItemMisc.SULFUR.create(1));
        ingredients.setInputs(ItemStack.class, l);
        if (result != null)
            ingredients.setOutput(ItemStack.class, result);
        if (lootTableName != null && world != null){
            ingredients.setOutputs(ItemStack.class, LootTableRegistry.getItemsFromTable(lootTableName, world));
        }
    }


    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> processTooltip = new ArrayList<>();
        if (mouseX >= 18 && mouseX <= 51 && mouseY >= 42 && mouseY <= 66) {
            processTooltip.add(String.format(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.life"), requiredLife));
        }
        if (mouseX >= 57 && mouseX <= 90 && mouseY >= 42 && mouseY <= 66) {
            processTooltip.add(String.format(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.fuel"), requiredFuel));
        }
        if (entityName != null && mouseX >= 19 && mouseX <= 35 && mouseY >= 16 && mouseY <= 32) {
            processTooltip.add(String.format(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.entity_spawn"), entityName));
        }
        return processTooltip;
    }
}
