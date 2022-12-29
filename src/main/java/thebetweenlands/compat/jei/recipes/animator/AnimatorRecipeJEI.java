package thebetweenlands.compat.jei.recipes.animator;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
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
import thebetweenlands.compat.jei.BetweenlandsJEIPlugin;
import thebetweenlands.util.TranslationHelper;

public class AnimatorRecipeJEI implements IRecipeWrapper {
    private IAnimatorRecipe animatorRecipe;
    private final ItemStack input;
    private int requiredFuel, requiredLife;
    private ItemStack result;
    private String entityName = null;
    private Entity entity = null;
    private ITickTimer tickTimer;
    private ResourceLocation lootTableName = null;
    private IGuiIngredient<ItemStack> guiIngredient;

    public static final DecimalFormat LIFE_CRYSTAL_PERCENTAGE = new DecimalFormat("##%");

    static {
        LIFE_CRYSTAL_PERCENTAGE.setRoundingMode(RoundingMode.CEILING);
    }

    public AnimatorRecipeJEI(IAnimatorRecipe animatorRecipe) {
        this.animatorRecipe = animatorRecipe;
        if (animatorRecipe instanceof AnimatorRecipe) {
            AnimatorRecipe recipe = (AnimatorRecipe) animatorRecipe;
            input = recipe.getInput();
            requiredFuel = recipe.getRequiredFuel(input);
            requiredLife = recipe.getRequiredLife(input);
            result = recipe.getResult(input);
            lootTableName = recipe.getLootTable();
        } else {
            ToolRepairAnimatorRecipe recipe = (ToolRepairAnimatorRecipe) animatorRecipe;
            input = new ItemStack(recipe.getTool());
            result = recipe.getResult(input);
        }
    }

    /*public AnimatorRecipeJEI(AnimatorRecipe animatorRecipe, World world) {
        this(animatorRecipe);
    }*/

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
        l.add(ImmutableList.of(new ItemStack(ItemRegistry.LIFE_CRYSTAL), new ItemStack(ItemRegistry.LIFE_CRYSTAL_FRAGMENT)));
        l.add(Collections.singletonList(ItemMisc.EnumItemMisc.SULFUR.create(1)));
        ingredients.setInputLists(VanillaTypes.ITEM, l);

        if (result != null)
            ingredients.setOutput(VanillaTypes.ITEM, result);
        if (lootTableName != null){
            ingredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(LootTableRegistry.getItemsFromTable(lootTableName, Minecraft.getMinecraft().world, true)));
        }
    }

    public void setGuiIngredient(IGuiIngredient<ItemStack> guiIngredient) {
        this.guiIngredient = guiIngredient;
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (entity == null && animatorRecipe.getSpawnEntityClass(input) != null) {
            try {
                entity = animatorRecipe.getSpawnEntityClass(input).getConstructor(new Class[]{World.class}).newInstance(Minecraft.getMinecraft().world);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            if (entity != null) {
                entityName = entity.getName();
                tickTimer = BetweenlandsJEIPlugin.jeiHelper.getGuiHelper().createTickTimer(40, 360, false);
            }
        }
        if (entity != null) {
            if (entity.world == null) entity.world = Minecraft.getMinecraft().world;

            final ScaledResolution scaledresolution = new ScaledResolution(minecraft);
            int i1 = scaledresolution.getScaledWidth();
            int j1 = scaledresolution.getScaledHeight();
            final int k1 = Mouse.getX() * i1 / minecraft.displayWidth;
            final int l1 = j1 - ((Mouse.getY() * j1) / minecraft.displayHeight) - 1;
            int posY = (l1 - mouseY);
            int posX = (k1 - mouseX);

            doGlScissor(minecraft, posX + 42, posY + 12, 24, 24);
            
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate(54, 34 + getOffset(entity), 200);
            float scale = getScale(entity);
            GlStateManager.scale(-scale, scale, scale);

            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            float rot = tickTimer.getValue();
            GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);

            RenderHelper.enableStandardItemLighting();

            GlStateManager.translate(0.0F, entity.getYOffset(), 0.0F);
            minecraft.getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.popMatrix();

            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            RenderHelper.enableGUIStandardItemLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            
            int fontX = 54 - (minecraft.fontRenderer.getStringWidth(entityName) / 2);
            Gui.drawRect(fontX - 1, -6, fontX + minecraft.fontRenderer.getStringWidth(entityName) + 1, 10 - 6, 0xAA000000);
            minecraft.fontRenderer.drawStringWithShadow(entityName, fontX, -5, 0xFFFFFF);
        }
    }

    public static void doGlScissor(Minecraft mc, int x, int y, int width, int height) {
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;

        if( k == 0 ) {
            k = 1000;
        }

        while( scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240 ) {
            ++scaleFactor;
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * scaleFactor, mc.displayHeight - (y + height) * scaleFactor, width * scaleFactor, height * scaleFactor);
    }

    private float getScale(Entity entity) {
        float width = entity.width;
        float height = entity.height;
        if (width <= height) {
            if (height < 0.7) return 35.0F;
            else if (height < 0.9) return 28.0F;
            else if (height < 1) return 25.0F;
            else if (height < 2) return 12.0F;
            else if (height < 2.5) return 10.0F;
            else if (height < 3) return 8.5F;
            else if (height < 4) return 4.0F;
            else return 3.0F;
        } else {
            if (width < 1) return 28.0F;
            else if (width < 2) return 17.0F;
            else if (width < 3) return 3.0F;
            else return 2.0F;
        }
    }

    private float getOffset(Entity entity) {
        float height = entity.height;
        if (height < 0.7) return 0.0F;
        else if (height < 0.9) return 0.3F;
        else if (height < 1) return 0.8F;
        else if (height < 2) return 1.8F;
        else if (height < 3) return 1.0F;
        else if (height < 4) return 0.5F;
        else return -0.8F;
    }

    @Nullable
    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        ArrayList<String> processTooltip = new ArrayList<>();
        if (mouseX >= 18 && mouseX <= 51 && mouseY >= 42 && mouseY <= 66) {
            float lifeAmount = guiIngredient != null ? animatorRecipe.getRequiredLife(guiIngredient.getDisplayedIngredient()): requiredLife;
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.life", LIFE_CRYSTAL_PERCENTAGE.format((lifeAmount / 128F))));
        }
        if (mouseX >= 57 && mouseX <= 90 && mouseY >= 42 && mouseY <= 66) {
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.fuel", guiIngredient != null ? animatorRecipe.getRequiredFuel(guiIngredient.getDisplayedIngredient()): requiredFuel));
        }
        if (entityName != null && mouseX >= 37 && mouseX <= 71 && mouseY >= 7 && mouseY <= 41) {
            processTooltip.add(TranslationHelper.translateToLocal("jei.thebetweenlands.animator.entity_spawn", entityName));
        }
        return processTooltip;
    }
}
