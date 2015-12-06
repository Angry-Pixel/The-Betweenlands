package thebetweenlands.herblore.elixirs;

import java.util.ArrayList;
import java.util.List;

import thebetweenlands.herblore.aspects.AspectRegistry;
import thebetweenlands.herblore.aspects.IAspect;
import thebetweenlands.herblore.elixirs.effects.ElixirEffect;

public class ElixirRecipes {
	private static final List<ElixirRecipe> REGISTERED_RECIPES = new ArrayList<ElixirRecipe>();

	public static void registerRecipe(ElixirRecipe recipe) {
		REGISTERED_RECIPES.add(recipe);
	}

	public static ElixirRecipe getFromEffect(ElixirEffect effect) {
		for(ElixirRecipe recipe : REGISTERED_RECIPES) {
			if(recipe.positiveElixir == effect || recipe.negativeElixir == effect) {
				return recipe;
			}
		}
		return null;
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
		registerRecipe(new ElixirRecipe("Test Elixir", 0xFF0000FF, 0xFFFF0000, 0xFF332902,      //Name & Infusion colors
				2000, 200,                                                                      //Infusion times
				1200,                                                                           //Base duration
				ElixirRegistry.EFFECT_TEST, ElixirRegistry.EFFECT_TEST,                         //Result elixirs
				AspectRegistry.ARMANIIS, AspectRegistry.FERGALAZ,								//Strength aspect & duration aspect
				new IAspect[]{AspectRegistry.BYARIIS}));                                        //Required aspects


		//Result items are placeholders
		registerRecipe(new ElixirRecipe("Elixir of Strength", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_STRENGTH, ElixirRegistry.EFFECT_WEAKNESS,
				AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Nimblefeet Elixir", 0xFF0000FF, 0xFFacaef0, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_NIMBLEFEET, ElixirRegistry.EFFECT_LUMBERING,
				AspectRegistry.YUNUGAZ, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Elixir of Healing", 0xFF0000FF, 0xFF1cd67d, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_HEALING, ElixirRegistry.EFFECT_DRAINING,
				AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Elixir of Ripening", 0xFF0000FF, 0xFF8dbdee, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_RIPENING, ElixirRegistry.EFFECT_DECAY,
				AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS, AspectRegistry.DAYUNIIS}));

		registerRecipe(new ElixirRecipe("Toughskin Elixir", 0xFF0000FF, 0xFF4311b1, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_TOUGHSKIN, ElixirRegistry.EFFECT_POISONSTING,
				AspectRegistry.YEOWYNN, AspectRegistry.CELAWYNN,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.YEOWYNN, AspectRegistry.CELAWYNN}));

		registerRecipe(new ElixirRecipe("Elixir of Feasting", 0xFF0000FF, 0xFFc57118, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_FEASTING, ElixirRegistry.EFFECT_STARVATION,
				AspectRegistry.CELAWYNN, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.CELAWYNN, AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Hunter's Sense Brew", 0xFF0000FF, 0xFF6f175d, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_HUNTERSSENSE, ElixirRegistry.EFFECT_DRUNKYARD,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.FIRNALAZ}));

		registerRecipe(new ElixirRecipe("Masking Brew", 0xFF0000FF, 0xFF28ccd5, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_MASKING, ElixirRegistry.EFFECT_STENCHING,
				AspectRegistry.ARMANIIS, AspectRegistry.DAYUNIIS,
				new IAspect[]{AspectRegistry.DAYUNIIS, AspectRegistry.ARMANIIS, AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Swiftarm Brew", 0xFF0000FF, 0xFFe8fc5b, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SWIFTARM, ElixirRegistry.EFFECT_SLUGARM,
				AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.YUNUGAZ, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Brew of the Cat's Eye", 0xFF0000FF, 0xFF7aaa19, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_CATSEYES, ElixirRegistry.EFFECT_BLINDMAN,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of Sagittarius", 0xFF0000FF, 0xFFea6731, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SAGITTARIUS, ElixirRegistry.EFFECT_WEAKBOW,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspect[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.BYARIIS, AspectRegistry.ORDANIIS, AspectRegistry.ARMANIIS}));

		registerRecipe(new ElixirRecipe("Spiderbreed Draught", 0xFF0000FF, 0xFF71c230, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SPIDERBREED, ElixirRegistry.EFFECT_BASILISK,
				AspectRegistry.AZUWYNN, AspectRegistry.YIHINREN,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.FERGALAZ, AspectRegistry.DAYUNIIS, AspectRegistry.YIHINREN, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Lightweight Draught", 0xFF0000FF, 0xFF6528da, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_LIGHTWEIGHT, ElixirRegistry.EFFECT_HEAVYWEIGHT,
				AspectRegistry.YUNUGAZ, AspectRegistry.YIHINREN,
				new IAspect[]{AspectRegistry.AZUWYNN, AspectRegistry.BYRGINAZ, AspectRegistry.YUNUGAZ, AspectRegistry.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of the Unclouded", 0xFF0000FF, 0xFF0d9ddf, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_UNCLOUDED, ElixirRegistry.EFFECT_FOGGEDMIND,
				AspectRegistry.GEOLIIRGAZ, AspectRegistry.DAYUNIIS,
				new IAspect[]{AspectRegistry.DAYUNIIS, AspectRegistry.FREIWYNN, AspectRegistry.GEOLIIRGAZ, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

		//Special potions
		/*registerRecipe(new ElixirRecipe("Shapeshifter's draught of Gillsgrowth", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				200, 
				ElixirRegistry.EFFECT_GILLSGROWTH, ElixirRegistry.EFFECT_DEFORMED,
				AspectRegistry.BYRGINAZ, AspectRegistry.YIHINREN,
				new IAspect[]{AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Shapeshifter's draught of Wings", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				200, 
				ElixirRegistry.EFFECT_WINGS, ElixirRegistry.EFFECT_LIMBLESS,
				AspectRegistry.YUNUGAZ, AspectRegistry.YIHINREN,
				new IAspect[]{AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Hunter's Sense Masterbrew", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				200, 
				ElixirRegistry.EFFECT_HUNTERSSENSEMASTER, ElixirRegistry.EFFECT_ISOLATEDSENSES,
				AspectRegistry.DAYUNIIS, AspectRegistry.YIHINREN,
				new IAspect[]{AspectRegistry.BYARIIS}));*/
	}
}
