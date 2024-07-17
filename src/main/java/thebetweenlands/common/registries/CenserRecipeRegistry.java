package thebetweenlands.common.registries;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.recipes.CenserRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.recipe.censer.*;

import javax.annotation.Nullable;

public class CenserRecipeRegistry {

	public static final DeferredRegister<CenserRecipe<?>> RECIPES = DeferredRegister.create(BLRegistries.Keys.CENSER_RECIPES, TheBetweenlands.ID);

	public static final DeferredHolder<CenserRecipe<?>, AspectCenserRecipe> ASPECT = RECIPES.register("apsect", AspectCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, CremainsCenserRecipe> CREMAINS = RECIPES.register("cremains", CremainsCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, DungeonFogCenserRecipe> DUNGEON_FOG = RECIPES.register("dungeon_fog", DungeonFogCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, ElixirCenserRecipe> ELIXIR = RECIPES.register("elixir", ElixirCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, FumigantCenserRecipe> FUMIGANT = RECIPES.register("fumigant", FumigantCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, PlantTonicCenserRecipe> PLANT_TONIC = RECIPES.register("plant_tonic", PlantTonicCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, SapBallCenserRecipe> SAP_BALL = RECIPES.register("sap_ball", SapBallCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, StagnantWaterCenserRecipe> STAGNANT_WATER = RECIPES.register("stagnant_water", StagnantWaterCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, SwampWaterCenserRecipe> SWAMP_WATER = RECIPES.register("swamp_water", SwampWaterCenserRecipe::new);
	public static final DeferredHolder<CenserRecipe<?>, WeepingBluePetalCenserRecipe> WEEPING_BLUE_PETAL = RECIPES.register("weeping_blue_petal", WeepingBluePetalCenserRecipe::new);

	@Nullable
	public static CenserRecipe<?> getRecipe(ItemStack input) {
		if (!input.isEmpty()) {
			for (CenserRecipe<?> recipe : BLRegistries.CENSER_RECIPES) {
				if (recipe.matchesInput(input)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Nullable
	public static CenserRecipe<?> getRecipeWithSecondaryInput(ItemStack secondaryStack) {
		if (!secondaryStack.isEmpty()) {
			for (CenserRecipe<?> recipe : BLRegistries.CENSER_RECIPES) {
				if (recipe.matchesSecondaryInput(secondaryStack)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Nullable
	public static CenserRecipe<?> getRecipe(FluidStack input) {
		if (input != null && input.getAmount() > 0) {
			for (CenserRecipe<?> recipe : BLRegistries.CENSER_RECIPES) {
				if (recipe.matchesInput(input)) {
					return recipe;
				}
			}
		}
		return null;
	}
}
