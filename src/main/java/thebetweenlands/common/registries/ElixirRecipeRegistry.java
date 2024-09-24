package thebetweenlands.common.registries;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.herblore.elixir.ElixirRecipe;

import java.util.List;

public class ElixirRecipeRegistry {

	public static final ResourceKey<ElixirRecipe> STRENGTH = registerKey("strength");
	public static final ResourceKey<ElixirRecipe> NIMBLEFEET = registerKey("nimblefeet");
	public static final ResourceKey<ElixirRecipe> HEALING = registerKey("healing");
	public static final ResourceKey<ElixirRecipe> RIPENING = registerKey("ripening");
	public static final ResourceKey<ElixirRecipe> TOUGHSKIN = registerKey("toughskin");
	public static final ResourceKey<ElixirRecipe> FEASTING = registerKey("feasting");
	public static final ResourceKey<ElixirRecipe> HUNTERS_SENSE = registerKey("hunters_sense");
	public static final ResourceKey<ElixirRecipe> MASKING = registerKey("masking");
	public static final ResourceKey<ElixirRecipe> SWIFTARM = registerKey("swiftarm");
	public static final ResourceKey<ElixirRecipe> CATS_EYE = registerKey("cats_eye");
	public static final ResourceKey<ElixirRecipe> SAGITTARIUS = registerKey("sagittarius");
	public static final ResourceKey<ElixirRecipe> SPIDERBREED = registerKey("spiderbreed");
	public static final ResourceKey<ElixirRecipe> LIGHTWEIGHT = registerKey("lightweight");
	public static final ResourceKey<ElixirRecipe> UNCLOUDED = registerKey("unclouded");


	public static ResourceKey<ElixirRecipe> registerKey(String name) {
		return ResourceKey.create(BLRegistries.Keys.ELIXIR_RECIPES, TheBetweenlands.prefix(name));
	}

	public static void bootstrap(BootstrapContext<ElixirRecipe> context) {
		context.register(STRENGTH, new ElixirRecipe(0xFF0000FF, 0xFFFF0000, 0xFF332902,
			2000, 200,
			2400, 10800,
			240, 1200,
			ElixirEffectRegistry.EFFECT_STRENGTH, ElixirEffectRegistry.EFFECT_WEAKNESS,
			AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.ORDANIIS)));

		context.register(NIMBLEFEET, new ElixirRecipe(0xFF0000FF, 0xFFacaef0, 0xFF332902,
			2000, 200,
			2400, 10800,
			300, 2000,
			ElixirEffectRegistry.EFFECT_NIMBLEFEET, ElixirEffectRegistry.EFFECT_LUMBERING,
			AspectTypeRegistry.YUNUGAZ, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.YUNUGAZ)));

		context.register(HEALING, new ElixirRecipe(0xFF0000FF, 0xFF1cd67d, 0xFF332902,
			2000, 200,
			1200, 6000,
			160, 240,
			ElixirEffectRegistry.EFFECT_HEALING, ElixirEffectRegistry.EFFECT_DRAINING,
			AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.ORDANIIS)));

		context.register(RIPENING, new ElixirRecipe(0xFF0000FF, 0xFF8dbdee, 0xFF332902,
			2000, 200,
			2400, 15600,
			400, 1200,
			ElixirEffectRegistry.EFFECT_RIPENING, ElixirEffectRegistry.EFFECT_DECAY,
			AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.DAYUNIIS)));

		context.register(TOUGHSKIN, new ElixirRecipe(0xFF0000FF, 0xFF4311b1, 0xFF332902,
			2000, 200,
			2400, 15600,
			160, 240,
			ElixirEffectRegistry.EFFECT_TOUGHSKIN, ElixirEffectRegistry.EFFECT_POISONSTING,
			AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.CELAWYNN,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.CELAWYNN)));

		context.register(FEASTING, new ElixirRecipe(0xFF0000FF, 0xFFc57118, 0xFF332902,
			2000, 200,
			2400, 13200,
			400, 1200,
			ElixirEffectRegistry.EFFECT_FEASTING, ElixirEffectRegistry.EFFECT_STARVATION,
			AspectTypeRegistry.CELAWYNN, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.CELAWYNN, AspectTypeRegistry.YEOWYNN, AspectTypeRegistry.ORDANIIS)));


		//Tier 2
		context.register(HUNTERS_SENSE, new ElixirRecipe(0xFF0000FF, 0xFF6f175d, 0xFF332902,
			2000, 200,
			3000, 12000,
			400, 1200,
			ElixirEffectRegistry.EFFECT_HUNTERSSENSE, ElixirEffectRegistry.EFFECT_DRUNKARD,
			AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS,
			List.of(AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.FIRNALAZ)));

		context.register(MASKING, new ElixirRecipe(0xFF0000FF, 0xFF28ccd5, 0xFF332902,
			2000, 200,
			2400, 15600,
			600, 6000,
			ElixirEffectRegistry.EFFECT_MASKING, ElixirEffectRegistry.EFFECT_STENCHING,
			AspectTypeRegistry.ARMANIIS, AspectTypeRegistry.DAYUNIIS,
			List.of(AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.ARMANIIS, AspectTypeRegistry.BYARIIS)));

		context.register(SWIFTARM, new ElixirRecipe(0xFF0000FF, 0xFFe8fc5b, 0xFF332902,
			2000, 200,
			2400, 10800,
			200, 1200,
			ElixirEffectRegistry.EFFECT_SWIFTARM, ElixirEffectRegistry.EFFECT_SLUGARM,
			AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.ORDANIIS,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.YUNUGAZ, AspectTypeRegistry.ORDANIIS)));

		context.register(CATS_EYE, new ElixirRecipe(0xFF0000FF, 0xFF7aaa19, 0xFF332902,
			2000, 200,
			2400, 9600,
			160, 240,
			ElixirEffectRegistry.EFFECT_CATSEYES, ElixirEffectRegistry.EFFECT_BLINDMAN,
			AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS,
			List.of(AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.GEOLIIRGAZ)));

		//Tier 3
		context.register(SAGITTARIUS, new ElixirRecipe(0xFF0000FF, 0xFFea6731, 0xFF332902,
			2000, 200,
			3000, 12000,
			400, 1200,
			ElixirEffectRegistry.EFFECT_SAGITTARIUS, ElixirEffectRegistry.EFFECT_WEAKBOW,
			AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS,
			List.of(AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.BYARIIS, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.ARMANIIS)));

		context.register(SPIDERBREED, new ElixirRecipe(0xFF0000FF, 0xFF71c230, 0xFF332902,
			2000, 200,
			1200, 12000,
			40, 200,
			ElixirEffectRegistry.EFFECT_SPIDERBREED, ElixirEffectRegistry.EFFECT_BASILISK,
			AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.YIHINREN,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.FERGALAZ, AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.YIHINREN, AspectTypeRegistry.YUNUGAZ)));

		context.register(LIGHTWEIGHT, new ElixirRecipe(0xFF0000FF, 0xFF6528da, 0xFF332902,
			2000, 200,
			1200, 9600,
			200, 1200,
			ElixirEffectRegistry.EFFECT_LIGHTWEIGHT, ElixirEffectRegistry.EFFECT_HEAVYWEIGHT,
			AspectTypeRegistry.YUNUGAZ, AspectTypeRegistry.YIHINREN,
			List.of(AspectTypeRegistry.AZUWYNN, AspectTypeRegistry.BYRGINAZ, AspectTypeRegistry.YUNUGAZ, AspectTypeRegistry.GEOLIIRGAZ, AspectTypeRegistry.YIHINREN)));

		context.register(UNCLOUDED, new ElixirRecipe(0xFF0000FF, 0xFF0d9ddf, 0xFF332902,
			2000, 200,
			1200, 10800,
			120, 240,
			ElixirEffectRegistry.EFFECT_UNCLOUDED, ElixirEffectRegistry.EFFECT_FOGGEDMIND,
			AspectTypeRegistry.GEOLIIRGAZ, AspectTypeRegistry.DAYUNIIS,
			List.of(AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.FREIWYNN, AspectTypeRegistry.GEOLIIRGAZ, AspectTypeRegistry.ORDANIIS, AspectTypeRegistry.YUNUGAZ)));

		//Special potions
		/*context.register(new ElixirRecipe("Shapeshifter's draught of Gillsgrowth", 0xFF0000FF, 0xFFFF0000, 0xFF332902,
				2000, 200,
				200,
				ElixirEffectRegistry.EFFECT_GILLSGROWTH, ElixirEffectRegistry.EFFECT_DEFORMED,
				AspectTypeRegistry.BYRGINAZ, AspectTypeRegistry.YIHINREN,
				List.of(AspectTypeRegistry.BYARIIS)));

		context.register(new ElixirRecipe("Shapeshifter's draught of Wings", 0xFF0000FF, 0xFFFF0000, 0xFF332902,
				2000, 200,
				200,
				ElixirEffectRegistry.EFFECT_WINGS, ElixirEffectRegistry.EFFECT_LIMBLESS,
				AspectTypeRegistry.YUNUGAZ, AspectTypeRegistry.YIHINREN,
				List.of(AspectTypeRegistry.BYARIIS)));

		context.register(new ElixirRecipe("Hunter's Sense Masterbrew", 0xFF0000FF, 0xFFFF0000, 0xFF332902,
				2000, 200,
				200,
				ElixirEffectRegistry.EFFECT_HUNTERSSENSEMASTER, ElixirEffectRegistry.EFFECT_ISOLATEDSENSES,
				AspectTypeRegistry.DAYUNIIS, AspectTypeRegistry.YIHINREN,
				List.of(AspectTypeRegistry.BYARIIS)));*/
	}
}
