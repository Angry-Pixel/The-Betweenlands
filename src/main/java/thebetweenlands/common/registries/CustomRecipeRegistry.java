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

import thebetweenlands.common.recipe.custom.CustomAnimatorRecipe;
import thebetweenlands.common.recipe.custom.CustomCompostBinRecipe;
import thebetweenlands.common.recipe.custom.CustomDruidAltarRecipe;
import thebetweenlands.common.recipe.custom.CustomPestleAndMortarRecipe;
import thebetweenlands.common.recipe.custom.CustomPurifierRecipe;
import thebetweenlands.common.recipe.custom.CustomRecipe;
import thebetweenlands.util.config.ConfigHandler;

public class CustomRecipeRegistry {
	private CustomRecipeRegistry() { }

	private static final List<CustomRecipe> RECIPE_TYPES = new ArrayList<>();

	public static void preInit() {
		RECIPE_TYPES.add(new CustomAnimatorRecipe());
		RECIPE_TYPES.add(new CustomPurifierRecipe());
		RECIPE_TYPES.add(new CustomCompostBinRecipe());
		RECIPE_TYPES.add(new CustomDruidAltarRecipe());
		RECIPE_TYPES.add(new CustomPestleAndMortarRecipe());
	}

	public static void loadCustomRecipes() {
		File cfgFile = new File(ConfigHandler.path);
		File customRecipesFile = new File(cfgFile.getParentFile(), "thebetweenlands" + File.separator + "recipes.json");
		if(customRecipesFile.exists()) {
			try(JsonReader jsonReader = new JsonReader(new FileReader(customRecipesFile))) {
				JsonObject jsonObj = new JsonParser().parse(jsonReader).getAsJsonObject();
				for(CustomRecipe recipe : RECIPE_TYPES) {
					if(jsonObj.has(recipe.getName())) {
						JsonArray arr = jsonObj.get(recipe.getName()).getAsJsonArray();
						arr.forEach(element -> {
							recipe.loadEntries(element);
							recipe.register();
						});
					}
				}
			} catch (Exception e) {
				Throwables.propagate(e);
			}
		}
	}
}
