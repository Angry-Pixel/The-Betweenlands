package thebetweenlands.common.registries;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Throwables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import thebetweenlands.api.recipes.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.recipe.custom.*;
import thebetweenlands.common.recipe.custom.CustomRecipes.InvalidRecipeException;

public class CustomRecipeRegistry {
	private CustomRecipeRegistry() { }

	private static final List<CustomRecipes<?>> RECIPE_TYPES = new ArrayList<>();

	public static CustomRecipes<IAnimatorRecipe> animatorRecipes;
	public static CustomRecipes<IAnimatorRecipe> animatorRepairableRecipes;
	public static CustomRecipes<IPurifierRecipe> purifiedRecipes;
	public static CustomRecipes<ICompostBinRecipe> compostBinRecipes;
	public static CustomRecipes<IDruidAltarRecipe> druidAltarRecipes;
	public static CustomRecipes<IPestleAndMortarRecipe> pestleAndMortarRecipes;
	public static CustomRecipes<ISmokingRackRecipe> smokingRackRecipes;
	public static CustomRecipes<ICrabPotFilterRecipeSilt> crabPotFilterRecipesSilt;
	public static CustomRecipes<ICrabPotFilterRecipeBubbler> crabPotFilterRecipesBubbler;
	public static CustomRecipes<ISteepingPotRecipe> steepingPot;

	public static void preInit() {
		RECIPE_TYPES.add(animatorRecipes = new CustomAnimatorRecipes());
		RECIPE_TYPES.add(animatorRepairableRecipes = new CustomAnimatorRepairableRecipes());
		RECIPE_TYPES.add(purifiedRecipes = new CustomPurifierRecipes());
		RECIPE_TYPES.add(compostBinRecipes = new CustomCompostBinRecipes());
		RECIPE_TYPES.add(druidAltarRecipes = new CustomDruidAltarRecipes());
		RECIPE_TYPES.add(pestleAndMortarRecipes = new CustomPestleAndMortarRecipes());
		RECIPE_TYPES.add(smokingRackRecipes = new CustomSmokingRackRecipes());
		RECIPE_TYPES.add(crabPotFilterRecipesSilt = new CustomCrabPotFilterRecipesSilt());
		RECIPE_TYPES.add(crabPotFilterRecipesBubbler = new CustomCrabPotFilterRecipesBubbler());
		RECIPE_TYPES.add(steepingPot = new CustomSteepingPotRecipes());
	}

	public static boolean loadCustomRecipes() {
		unregisterCustomRecipes();

		for(CustomRecipes<?> recipe : RECIPE_TYPES) {
			recipe.clear();
		}

		File customRecipesFile = new File(BetweenlandsConfig.configDir, "recipes.json");
		boolean noError = true;
		if(customRecipesFile.exists()) {
			try(JsonReader jsonReader = new JsonReader(new FileReader(customRecipesFile))) {
				JsonObject jsonObj = new JsonParser().parse(jsonReader).getAsJsonObject();
				for(CustomRecipes<?> recipes : RECIPE_TYPES) {
					if(jsonObj.has(recipes.getName())) {
						try {
							JsonArray arr = jsonObj.get(recipes.getName()).getAsJsonArray();
							recipes.parse(arr);
						} catch(InvalidRecipeException ex) {
							TheBetweenlands.logger.throwing(ex);
							noError = false;
						}
					}
				}
			} catch (Exception e) {
				Throwables.throwIfUnchecked(e);
				throw new RuntimeException(e);
			}
		}

		registerCustomRecipes();
		return noError;
	}

	public static void registerCustomRecipes() {
		for(CustomRecipes<?> recipes : RECIPE_TYPES) {
			recipes.registerRecipes();
		}
	}

	public static void unregisterCustomRecipes() {
		for(CustomRecipes<?> recipes : RECIPE_TYPES) {
			recipes.unregisterRecipes();
		}
	}
}
