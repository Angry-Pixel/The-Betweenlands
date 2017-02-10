package thebetweenlands.common;

import java.util.Collections;
import java.util.List;

import thebetweenlands.api.IBetweenlandsAPI;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.purifier.PurifierRecipe;

public class BetweenlandsAPI implements IBetweenlandsAPI {
	private static IBetweenlandsAPI instance;

	/**
	 * Returns the instance of the API
	 * @return
	 */
	public static IBetweenlandsAPI getInstance() {
		return instance;
	}

	static void init() {
		instance = new BetweenlandsAPI();
	}

	@Override
	public void registerPurifierRecipe(IPurifierRecipe recipe) {
		PurifierRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterPurifierRecipe(IPurifierRecipe recipe) {
		PurifierRecipe.removeRecipe(recipe);
	}

	@Override
	public List<IPurifierRecipe> getPurifierRecipes() {
		return Collections.unmodifiableList(PurifierRecipe.getRecipeList());
	}

	@Override
	public void registerAnimatorRecipe(IAnimatorRecipe recipe) {
		AnimatorRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterAnimatorRecipe(IAnimatorRecipe recipe) {
		AnimatorRecipe.removeRecipe(recipe);
	}

	@Override
	public List<IAnimatorRecipe> getAnimatorRecipes() {
		return Collections.unmodifiableList(AnimatorRecipe.getRecipes());
	}

	@Override
	public void registerCompostBinRecipe(ICompostBinRecipe recipe) {
		CompostRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterCompostBinRecipe(ICompostBinRecipe recipe) {
		CompostRecipe.removeRecipe(recipe);
	}

	@Override
	public List<ICompostBinRecipe> getCompostBinRecipes() {
		return Collections.unmodifiableList(CompostRecipe.RECIPES);
	}
}
