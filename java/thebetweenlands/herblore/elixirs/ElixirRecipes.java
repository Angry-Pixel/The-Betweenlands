package thebetweenlands.herblore.elixirs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.items.BLItemRegistry;

public class ElixirRecipes {
	private static final List<ElixirRecipe> REGISTERED_RECIPES = new ArrayList<ElixirRecipe>();

	public static void registerRecipe(ElixirRecipe recipe) {
		REGISTERED_RECIPES.add(recipe);
	}

	public static ElixirRecipe getFromAspects(List<IAspect> aspects) {
		for(ElixirRecipe recipe : REGISTERED_RECIPES) {
			boolean matches = true;
			checkAvailability:
				for(IAspect recipeAspect : recipe.aspects) {
					for(IAspect aspect : aspects) {
						if(aspect == AspectRegistry.BYARIIS 
								|| aspect == recipe.durationAspect 
								|| aspect == recipe.strengthAspect) continue;
						boolean contains = false;
						for(IAspect a : recipe.aspects) {
							if(a == aspect) {
								contains = true;
								break;
							}
						}
						if(!contains) {
							matches = false;
							break checkAvailability;
						}
					}
					if(!aspects.contains(recipeAspect)) {
						matches = false;
						break;
					}
				}
			if(matches) {
				return recipe;
			}
		}
		return null;
	}

	public static void init() {
		//Just for testing
		registerRecipe(new ElixirRecipe("Test Elixir", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.BYARIIS}));


		//Result items are placeholders
		registerRecipe(new ElixirRecipe("Elixir of Strength", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Nimblefeet Elixir", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Elixir of Healing", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Elixir of Ripening", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS, AspectRegistry.DAYUNIIS}));

		registerRecipe(new ElixirRecipe("Toughskin Elixir", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.YEOWYNN, AspectRegistry.CELAWYNN}));

		registerRecipe(new ElixirRecipe("Elixir of Feasting", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.CELAWYNN, AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Hunter's Sense Brew", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.FIRNALAZ}));

		registerRecipe(new ElixirRecipe("Masking Brew", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.DAYUNIIS, AspectRegistry.ARMANIIS, AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Swiftarm Brew", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.YUNUGAZ, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Brew of the Cat's Eye", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of Sagittarius", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.BYARIIS, AspectRegistry.ORDANIIS, AspectRegistry.ARMANIIS}));

		registerRecipe(new ElixirRecipe("Spiderbreed Draught", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.FERGALAZ, AspectRegistry.DAYUNIIS, AspectRegistry.YIHINREN, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Lightweight Draught", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.BYRGINAZ, AspectRegistry.YUNUGAZ, AspectRegistry.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of the Unclouded", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.DAYUNIIS, AspectRegistry.FREIWYNN, AspectRegistry.GEOLIIRGAZ, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

		/*registerRecipe(new ElixirRecipe("Shapeshifter's draught of Gillsgrowth", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Shapeshifter's draught of Wings", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Hunter's Sense Masterbrew", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 2000, 200, 
				new ItemStack(BLItemRegistry.anglerMeatCooked), new ItemStack(BLItemRegistry.anglerMeatRaw), 
				new IAspect[]{AspectRegistry.BYARIIS}));*/
	}
}
