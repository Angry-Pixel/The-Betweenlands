package thebetweenlands.herblore.elixirs;

import java.util.ArrayList;
import java.util.List;

import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.herblore.aspects.IAspectType;
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

	public static ElixirRecipe getFromAspects(List<IAspectType> aspects) {
		for(ElixirRecipe recipe : REGISTERED_RECIPES) {
			boolean matches = true;
			checkAvailability:
				for(IAspectType recipeAspect : recipe.aspects) {
					for(IAspectType aspect : aspects) {
						if(aspect == AspectManager.BYARIIS 
								|| aspect == recipe.durationAspect 
								|| aspect == recipe.strengthAspect) continue;
						boolean contains = false;
						for(IAspectType a : recipe.aspects) {
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
				AspectManager.ARMANIIS, AspectManager.FERGALAZ,								//Strength aspect & duration aspect
				new IAspectType[]{AspectManager.BYARIIS}));                                        //Required aspects


		//Result items are placeholders
		registerRecipe(new ElixirRecipe("Elixir of Strength", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_STRENGTH, ElixirRegistry.EFFECT_WEAKNESS,
				AspectManager.AZUWYNN, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Nimblefeet Elixir", 0xFF0000FF, 0xFFacaef0, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_NIMBLEFEET, ElixirRegistry.EFFECT_LUMBERING,
				AspectManager.YUNUGAZ, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.ORDANIIS, AspectManager.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Elixir of Healing", 0xFF0000FF, 0xFF1cd67d, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_HEALING, ElixirRegistry.EFFECT_DRAINING,
				AspectManager.YEOWYNN, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.YEOWYNN, AspectManager.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Elixir of Ripening", 0xFF0000FF, 0xFF8dbdee, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_RIPENING, ElixirRegistry.EFFECT_DECAY,
				AspectManager.DAYUNIIS, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.YEOWYNN, AspectManager.ORDANIIS, AspectManager.DAYUNIIS}));

		registerRecipe(new ElixirRecipe("Toughskin Elixir", 0xFF0000FF, 0xFF4311b1, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_TOUGHSKIN, ElixirRegistry.EFFECT_POISONSTING,
				AspectManager.YEOWYNN, AspectManager.CELAWYNN,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.YEOWYNN, AspectManager.CELAWYNN}));

		registerRecipe(new ElixirRecipe("Elixir of Feasting", 0xFF0000FF, 0xFFc57118, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_FEASTING, ElixirRegistry.EFFECT_STARVATION,
				AspectManager.CELAWYNN, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.CELAWYNN, AspectManager.YEOWYNN, AspectManager.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Hunter's Sense Brew", 0xFF0000FF, 0xFF6f175d, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_HUNTERSSENSE, ElixirRegistry.EFFECT_DRUNKYARD,
				AspectManager.FREIWYNN, AspectManager.DAYUNIIS,
				new IAspectType[]{AspectManager.FREIWYNN, AspectManager.DAYUNIIS, AspectManager.ORDANIIS, AspectManager.FIRNALAZ}));

		registerRecipe(new ElixirRecipe("Masking Brew", 0xFF0000FF, 0xFF28ccd5, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_MASKING, ElixirRegistry.EFFECT_STENCHING,
				AspectManager.ARMANIIS, AspectManager.DAYUNIIS,
				new IAspectType[]{AspectManager.DAYUNIIS, AspectManager.ARMANIIS, AspectManager.BYARIIS}));

		registerRecipe(new ElixirRecipe("Swiftarm Brew", 0xFF0000FF, 0xFFe8fc5b, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SWIFTARM, ElixirRegistry.EFFECT_SLUGARM,
				AspectManager.AZUWYNN, AspectManager.ORDANIIS,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.DAYUNIIS, AspectManager.YUNUGAZ, AspectManager.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Brew of the Cat's Eye", 0xFF0000FF, 0xFF7aaa19, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_CATSEYES, ElixirRegistry.EFFECT_BLINDMAN,
				AspectManager.FREIWYNN, AspectManager.DAYUNIIS,
				new IAspectType[]{AspectManager.FREIWYNN, AspectManager.DAYUNIIS, AspectManager.ORDANIIS, AspectManager.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of Sagittarius", 0xFF0000FF, 0xFFea6731, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SAGITTARIUS, ElixirRegistry.EFFECT_WEAKBOW,
				AspectManager.FREIWYNN, AspectManager.DAYUNIIS,
				new IAspectType[]{AspectManager.FREIWYNN, AspectManager.DAYUNIIS, AspectManager.BYARIIS, AspectManager.ORDANIIS, AspectManager.ARMANIIS}));

		registerRecipe(new ElixirRecipe("Spiderbreed Draught", 0xFF0000FF, 0xFF71c230, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_SPIDERBREED, ElixirRegistry.EFFECT_BASILISK,
				AspectManager.AZUWYNN, AspectManager.YIHINREN,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.FERGALAZ, AspectManager.DAYUNIIS, AspectManager.YIHINREN, AspectManager.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Lightweight Draught", 0xFF0000FF, 0xFF6528da, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_LIGHTWEIGHT, ElixirRegistry.EFFECT_HEAVYWEIGHT,
				AspectManager.YUNUGAZ, AspectManager.YIHINREN,
				new IAspectType[]{AspectManager.AZUWYNN, AspectManager.BYRGINAZ, AspectManager.YUNUGAZ, AspectManager.GEOLIIRGAZ}));

		registerRecipe(new ElixirRecipe("Draught of the Unclouded", 0xFF0000FF, 0xFF0d9ddf, 0xFF332902, 
				2000, 200, 
				1200,
				ElixirRegistry.EFFECT_UNCLOUDED, ElixirRegistry.EFFECT_FOGGEDMIND,
				AspectManager.GEOLIIRGAZ, AspectManager.DAYUNIIS,
				new IAspectType[]{AspectManager.DAYUNIIS, AspectManager.FREIWYNN, AspectManager.GEOLIIRGAZ, AspectManager.ORDANIIS, AspectManager.YUNUGAZ}));

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
