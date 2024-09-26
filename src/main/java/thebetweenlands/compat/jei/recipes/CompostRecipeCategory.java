package thebetweenlands.compat.jei.recipes;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompostRecipeCategory implements IRecipeCategory<CompostRecipeCategory.CompostRecipe> {

	public static final RecipeType<CompostRecipe> COMPOST = RecipeType.create(TheBetweenlands.ID, "compost", CompostRecipe.class);
	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;

	public CompostRecipeCategory(IGuiHelper helper) {
		ResourceLocation location = TheBetweenlands.prefix("textures/gui/viewer/compost_grid.png");
		this.background = helper.createDrawable(location, 0, 0, 58, 21);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, BlockRegistry.COMPOST_BIN.toStack());
		this.localizedName = Component.translatable("jei.thebetweenlands.recipe.compost");
	}

	@Override
	public RecipeType<CompostRecipe> getRecipeType() {
		return COMPOST;
	}

	@Override
	public Component getTitle() {
		return this.localizedName;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, CompostRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 1, 2).addItemStack(new ItemStack(recipe.input()));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 43, 2).addItemStack(ItemRegistry.COMPOST.toStack());
	}

	@Override
	public void draw(CompostRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
		if (mouseX >= 18 && mouseX <= 39 && mouseY >= 3 && mouseY <= 18) {
			int minutes = (recipe.time() / 20) / 60;
			int seconds = (recipe.time() / 20) % 60;

			List<Component> tooltips = new ArrayList<>();
			if (seconds > 0 && minutes > 0) {
				tooltips.add(Component.translatable("jei.thebetweenlands.time", minutes, seconds));
			} else if (minutes > 0) {
				tooltips.add(Component.translatable("jei.thebetweenlands.time.minutes", minutes));
			} else {
				tooltips.add(Component.translatable("jei.thebetweenlands.time.seconds", minutes));
			}
			tooltips.add(Component.translatable("jei.thebetweenlands.compost.amount", recipe.amount()));

			graphics.renderTooltip(Minecraft.getInstance().font, tooltips, Optional.empty(), (int) mouseX, (int) mouseY);
		}
	}

	@Nullable
	@Override
	public ResourceLocation getRegistryName(CompostRecipe recipe) {
		return recipe.uid();
	}

	public record CompostRecipe(Item input, int time, int amount, ResourceLocation uid) {
	}
}
