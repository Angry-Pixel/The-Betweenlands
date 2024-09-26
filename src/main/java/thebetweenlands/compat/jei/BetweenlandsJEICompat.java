package thebetweenlands.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.ModIds;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.library.plugins.vanilla.ingredients.subtypes.PotionSubtypeInterpreter;
import mezz.jei.library.util.ResourceLocationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import thebetweenlands.client.gui.screen.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.WeedwoodCraftingMenu;
import thebetweenlands.common.item.datamaps.CompostableItem;
import thebetweenlands.common.item.recipe.BubblerCrabPotFilterRecipe;
import thebetweenlands.common.item.recipe.SiltCrabPotFilterRecipe;
import thebetweenlands.common.registries.*;
import thebetweenlands.compat.CompostRecipe;
import thebetweenlands.compat.jei.interpreter.AspectSubtypeInterpreter;
import thebetweenlands.compat.jei.interpreter.BucketSubtypeInterpreter;
import thebetweenlands.compat.jei.interpreter.ElixirSubtypeInterpreter;
import thebetweenlands.compat.jei.recipes.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class BetweenlandsJEICompat implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return TheBetweenlands.prefix("betweenlands");
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(BlockRegistry.ANIMATOR.toStack(), AnimatorRecipeCategory.ANIMATOR);
		registration.addRecipeCatalyst(BlockRegistry.CRAB_POT_FILTER.toStack(), BubblerCrabPotRecipeCategory.FILTER);
		registration.addRecipeCatalyst(BlockRegistry.COMPOST_BIN.toStack(), CompostRecipeCategory.COMPOST);
		registration.addRecipeCatalyst(BlockRegistry.DRUID_ALTAR.toStack(), DruidAltarRecipeCategory.ALTAR);
		registration.addRecipeCatalyst(BlockRegistry.MORTAR.toStack(), MortarRecipeCategory.MORTAR);
		registration.addRecipeCatalyst(BlockRegistry.PURIFIER.toStack(), PurifierRecipeCategory.PURIFIER);
		registration.addRecipeCatalyst(BlockRegistry.CRAB_POT_FILTER.toStack(), SiltCrabPotRecipeCategory.FILTER);
		registration.addRecipeCatalyst(BlockRegistry.SMOKING_RACK.toStack(), SmokingRackRecipeCategory.SMOKING_RACK);
		registration.addRecipeCatalyst(BlockRegistry.STEEPING_POT.toStack(), SteepingPotRecipeCategory.STEEPING_POT);
		registration.addRecipeCatalyst(ItemRegistry.SILK_BUNDLE.toStack(), SteepingPotRecipeCategory.STEEPING_POT);

		registration.addRecipeCatalyst(BlockRegistry.WEEDWOOD_CRAFTING_TABLE.toStack(), RecipeTypes.CRAFTING);
		registration.addRecipeCatalyst(BlockRegistry.SULFUR_FURNACE.toStack(), RecipeTypes.SMELTING);
		registration.addRecipeCatalyst(BlockRegistry.DUAL_SULFUR_FURNACE.toStack(), RecipeTypes.SMELTING);
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new AnimatorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new BubblerCrabPotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new CompostRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new DruidAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new MortarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new PurifierRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SiltCrabPotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SmokingRackRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
		registration.addRecipeCategories(new SteepingPotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(AnimatorRecipeCategory.ANIMATOR, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.ANIMATOR_RECIPE.get()).stream().map(RecipeHolder::value).toList());
		registration.addRecipes(BubblerCrabPotRecipeCategory.FILTER, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get()).stream().map(RecipeHolder::value).filter(recipe -> recipe instanceof BubblerCrabPotFilterRecipe).map(BubblerCrabPotFilterRecipe.class::cast).toList());
		registration.addRecipes(CompostRecipeCategory.COMPOST, this.collectCompostRecipes(registration.getIngredientManager()));
		registration.addRecipes(DruidAltarRecipeCategory.ALTAR, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.DRUID_ALTAR_RECIPE.get()).stream().map(RecipeHolder::value).toList());
		registration.addRecipes(MortarRecipeCategory.MORTAR, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.MORTAR_RECIPE.get()).stream().map(RecipeHolder::value).toList());
		registration.addRecipes(PurifierRecipeCategory.PURIFIER, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.PURIFIER_RECIPE.get()).stream().map(RecipeHolder::value).toList());
		registration.addRecipes(SiltCrabPotRecipeCategory.FILTER, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get()).stream().map(RecipeHolder::value).filter(recipe -> recipe instanceof SiltCrabPotFilterRecipe).map(SiltCrabPotFilterRecipe.class::cast).toList());
		registration.addRecipes(SmokingRackRecipeCategory.SMOKING_RACK, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.SMOKING_RECIPE.get()).stream().map(RecipeHolder::value).toList());
		registration.addRecipes(SteepingPotRecipeCategory.STEEPING_POT, Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(RecipeRegistry.STEEPING_POT_RECIPE.get()).stream().map(RecipeHolder::value).toList());
	}

	private List<CompostRecipe> collectCompostRecipes(IIngredientManager manager) {
		Collection<ItemStack> allIngredients = manager.getAllItemStacks();
		IIngredientHelper<ItemStack> ingredientHelper = manager.getIngredientHelper(VanillaTypes.ITEM_STACK);

		return allIngredients.stream()
			.mapMulti((itemStack, consumer) -> {
				CompostableItem item = itemStack.getItem().builtInRegistryHolder().getData(DataMapRegistry.COMPOSTABLE);
				if (item != null) {
					String ingredientUid = ((ItemLike) ingredientHelper.getUid(itemStack, UidContext.Recipe)).asItem().getDescriptionId();
					String ingredientUidPath = ResourceLocationUtil.sanitizePath(ingredientUid);
					ResourceLocation recipeUid = ResourceLocation.fromNamespaceAndPath(ModIds.JEI_ID, ingredientUidPath);
					CompostRecipe recipe = new CompostRecipe(itemStack.getItem(), item.time(), item.amount(), recipeUid);
					consumer.accept(recipe);
				}
			})
			.map(CompostRecipe.class::cast)
			.sorted(Comparator.comparingInt(CompostRecipe::time))
			.toList();
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(PurifierScreen.class, 81, 31, 28, 23, PurifierRecipeCategory.PURIFIER);
		registration.addRecipeClickArea(MortarScreen.class, 43, 66, 90, 14, MortarRecipeCategory.MORTAR);
		registration.addRecipeClickArea(AnimatorScreen.class, 51, 49, 72, 25, AnimatorRecipeCategory.ANIMATOR);
		registration.addRecipeClickArea(DruidAltarScreen.class, 70, 5, 38, 27, DruidAltarRecipeCategory.ALTAR);
		registration.addRecipeClickArea(DruidAltarScreen.class, 52, 24, 27, 38, DruidAltarRecipeCategory.ALTAR);
		registration.addRecipeClickArea(DruidAltarScreen.class, 99, 24, 27, 38, DruidAltarRecipeCategory.ALTAR);
		registration.addRecipeClickArea(DruidAltarScreen.class, 70, 51, 38, 27, DruidAltarRecipeCategory.ALTAR);
		registration.addRecipeClickArea(SmokingRackScreen.class, 99, 34, 16, 52, SmokingRackRecipeCategory.SMOKING_RACK);
		registration.addRecipeClickArea(CrabPotFilterScreen.class, 73, 58, 22, 15, BubblerCrabPotRecipeCategory.FILTER);
		registration.addRecipeClickArea(CrabPotFilterScreen.class, 73, 28, 22, 15, SiltCrabPotRecipeCategory.FILTER);
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(ItemRegistry.ASPECTRUS_FRUIT.get(), AspectSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.GREEN_ASPECT_VIAL.get(), AspectSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.ORANGE_ASPECT_VIAL.get(), AspectSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.ORANGE_ELIXIR.get(), ElixirSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.GREEN_ELIXIR.get(), ElixirSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.WEEDWOOD_BUCKET.get(), BucketSubtypeInterpreter.INSTANCE);
		registration.registerSubtypeInterpreter(ItemRegistry.SYRMORITE_BUCKET.get(), BucketSubtypeInterpreter.INSTANCE);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		registration.addRecipeTransferHandler(WeedwoodCraftingMenu.class, MenuRegistry.WEEDWOOD_CRAFTING_TABLE.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
	}
}
