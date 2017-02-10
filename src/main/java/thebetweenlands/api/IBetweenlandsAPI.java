package thebetweenlands.api;

import java.util.List;

import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;

public interface IBetweenlandsAPI {
	/**
	 * Registers a purifier recipe
	 * @param recipe
	 */
	public void registerPurifierRecipe(IPurifierRecipe recipe);

	/**
	 * Unregisters a purified recipe
	 * @param recipe
	 */
	public void unregisterPurifierRecipe(IPurifierRecipe recipe);

	/**
	 * Returns a list of all purifier recipes
	 * @return
	 */
	public List<IPurifierRecipe> getPurifierRecipes();
	
	/**
	 * Registers an animator recipe
	 * @param recipe
	 */
	public void registerAnimatorRecipe(IAnimatorRecipe recipe);

	/**
	 * Unregisters an animator recipe
	 */
	public void unregisterAnimatorRecipe(IAnimatorRecipe recipe);

	/**
	 * Returns a list of all animator recipes
	 * @return
	 */
	public List<IAnimatorRecipe> getAnimatorRecipes();
	
	/**
	 * Registers a compost bin recipe
	 * @param recipe
	 */
	public void registerCompostBinRecipe(ICompostBinRecipe recipe);

	/**
	 * Unregisters a compost bin recipe
	 */
	public void unregisterCompostBinRecipe(ICompostBinRecipe recipe);
	
	/**
	 * Returns a list of all compost bin recipes
	 * @return
	 */
	public List<ICompostBinRecipe> getCompostBinRecipes();
}
