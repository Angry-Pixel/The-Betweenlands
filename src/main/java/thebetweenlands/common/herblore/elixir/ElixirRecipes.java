package thebetweenlands.common.herblore.elixir;

import java.util.ArrayList;
import java.util.List;

import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.common.herblore.elixir.effects.ElixirEffect;
import thebetweenlands.common.registries.AspectRegistry;

public class ElixirRecipes {
	private static final List<ElixirRecipe> REGISTERED_RECIPES = new ArrayList<>();

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
						if(aspect == AspectRegistry.BYARIIS 
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


	public static List<ElixirRecipe> getFromAspect(IAspectType aspectType){
		List<ElixirRecipe> recipes = new ArrayList<>();
		for (ElixirRecipe recipe:REGISTERED_RECIPES){
			for(IAspectType recipeAspect : recipe.aspects) {
				if (recipeAspect == aspectType)
					recipes.add(recipe);
			}
		}
		return recipes;
	}

	public static void init() {
		//Tier 1
		registerRecipe(new ElixirRecipe("Elixir of Strength", 0xFF0000FF, 0xFFFF0000, 0xFF332902, 
				2000, 200, 
				2400, 10800,
				240, 1200,
				ElixirEffectRegistry.EFFECT_STRENGTH, ElixirEffectRegistry.EFFECT_WEAKNESS,
				AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Nimblefeet Elixir", 0xFF0000FF, 0xFFacaef0, 0xFF332902, 
				2000, 200, 
				2400, 10800,
				300, 2000,
				ElixirEffectRegistry.EFFECT_NIMBLEFEET, ElixirEffectRegistry.EFFECT_LUMBERING,
				AspectRegistry.YUNUGAZ, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Elixir of Healing", 0xFF0000FF, 0xFF1cd67d, 0xFF332902, 
				2000, 200, 
				1200, 6000,
				160, 240,
				ElixirEffectRegistry.EFFECT_HEALING, ElixirEffectRegistry.EFFECT_DRAINING,
				AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Elixir of Ripening", 0xFF0000FF, 0xFF8dbdee, 0xFF332902, 
				2000, 200, 
				2400, 15600,
				400, 1200,
				ElixirEffectRegistry.EFFECT_RIPENING, ElixirEffectRegistry.EFFECT_DECAY,
				AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS, AspectRegistry.DAYUNIIS}));

		registerRecipe(new ElixirRecipe("Toughskin Elixir", 0xFF0000FF, 0xFF4311b1, 0xFF332902, 
				2000, 200, 
				2400, 15600,
				160, 240,
				ElixirEffectRegistry.EFFECT_TOUGHSKIN, ElixirEffectRegistry.EFFECT_POISONSTING,
				AspectRegistry.YEOWYNN, AspectRegistry.CELAWYNN,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.YEOWYNN, AspectRegistry.CELAWYNN}));

		registerRecipe(new ElixirRecipe("Elixir of Feasting", 0xFF0000FF, 0xFFc57118, 0xFF332902, 
				2000, 200, 
				2400, 13200,
				400, 1200,
				ElixirEffectRegistry.EFFECT_FEASTING, ElixirEffectRegistry.EFFECT_STARVATION,
				AspectRegistry.CELAWYNN, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.CELAWYNN, AspectRegistry.YEOWYNN, AspectRegistry.ORDANIIS}));


		//Tier 2
		registerRecipe(new ElixirRecipe("Hunter's Sense Brew", 0xFF0000FF, 0xFF6f175d, 0xFF332902, 
				2000, 200, 
				3000, 12000,
				400, 1200,
				ElixirEffectRegistry.EFFECT_HUNTERSSENSE, ElixirEffectRegistry.EFFECT_DRUNKARD,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspectType[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.FIRNALAZ}));

		registerRecipe(new ElixirRecipe("Masking Brew", 0xFF0000FF, 0xFF28ccd5, 0xFF332902, 
				2000, 200, 
				2400, 15600,
				600, 6000,
				ElixirEffectRegistry.EFFECT_MASKING, ElixirEffectRegistry.EFFECT_STENCHING,
				AspectRegistry.ARMANIIS, AspectRegistry.DAYUNIIS,
				new IAspectType[]{AspectRegistry.DAYUNIIS, AspectRegistry.ARMANIIS, AspectRegistry.BYARIIS}));

		registerRecipe(new ElixirRecipe("Swiftarm Brew", 0xFF0000FF, 0xFFe8fc5b, 0xFF332902, 
				2000, 200, 
				2400, 10800,
				200, 1200,
				ElixirEffectRegistry.EFFECT_SWIFTARM, ElixirEffectRegistry.EFFECT_SLUGARM,
				AspectRegistry.AZUWYNN, AspectRegistry.ORDANIIS,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.YUNUGAZ, AspectRegistry.ORDANIIS}));

		registerRecipe(new ElixirRecipe("Brew of the Cat's Eye", 0xFF0000FF, 0xFF7aaa19, 0xFF332902, 
				2000, 200, 
				2400, 9600,
				160, 240,
				ElixirEffectRegistry.EFFECT_CATSEYES, ElixirEffectRegistry.EFFECT_BLINDMAN,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspectType[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.ORDANIIS, AspectRegistry.GEOLIIRGAZ}));

		//Tier 3
		registerRecipe(new ElixirRecipe("Draught of Sagittarius", 0xFF0000FF, 0xFFea6731, 0xFF332902, 
				2000, 200, 
				3000, 12000,
				400, 1200,
				ElixirEffectRegistry.EFFECT_SAGITTARIUS, ElixirEffectRegistry.EFFECT_WEAKBOW,
				AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS,
				new IAspectType[]{AspectRegistry.FREIWYNN, AspectRegistry.DAYUNIIS, AspectRegistry.BYARIIS, AspectRegistry.ORDANIIS, AspectRegistry.ARMANIIS}));

		registerRecipe(new ElixirRecipe("Spiderbreed Draught", 0xFF0000FF, 0xFF71c230, 0xFF332902, 
				2000, 200, 
				1200, 12000,
				40, 200,
				ElixirEffectRegistry.EFFECT_SPIDERBREED, ElixirEffectRegistry.EFFECT_BASILISK,
				AspectRegistry.AZUWYNN, AspectRegistry.YIHINREN,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.FERGALAZ, AspectRegistry.DAYUNIIS, AspectRegistry.YIHINREN, AspectRegistry.YUNUGAZ}));

		registerRecipe(new ElixirRecipe("Lightweight Draught", 0xFF0000FF, 0xFF6528da, 0xFF332902, 
				2000, 200, 
				1200, 9600,
				200, 1200,
				ElixirEffectRegistry.EFFECT_LIGHTWEIGHT, ElixirEffectRegistry.EFFECT_HEAVYWEIGHT,
				AspectRegistry.YUNUGAZ, AspectRegistry.YIHINREN,
				new IAspectType[]{AspectRegistry.AZUWYNN, AspectRegistry.BYRGINAZ, AspectRegistry.YUNUGAZ, AspectRegistry.GEOLIIRGAZ, AspectRegistry.YIHINREN}));

		registerRecipe(new ElixirRecipe("Draught of the Unclouded", 0xFF0000FF, 0xFF0d9ddf, 0xFF332902, 
				2000, 200, 
				1200, 10800,
				120, 240,
				ElixirEffectRegistry.EFFECT_UNCLOUDED, ElixirEffectRegistry.EFFECT_FOGGEDMIND,
				AspectRegistry.GEOLIIRGAZ, AspectRegistry.DAYUNIIS,
				new IAspectType[]{AspectRegistry.DAYUNIIS, AspectRegistry.FREIWYNN, AspectRegistry.GEOLIIRGAZ, AspectRegistry.ORDANIIS, AspectRegistry.YUNUGAZ}));

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
