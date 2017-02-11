package thebetweenlands.common;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import thebetweenlands.api.IBetweenlandsAPI;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.event.handler.OverworldItemHandler;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.IItemStackMatcher;
import thebetweenlands.common.item.equipment.ItemAmulet;
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
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		PurifierRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterPurifierRecipe(IPurifierRecipe recipe) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		PurifierRecipe.removeRecipe(recipe);
	}

	@Override
	public List<IPurifierRecipe> getPurifierRecipes() {
		return Collections.unmodifiableList(PurifierRecipe.getRecipeList());
	}

	@Override
	public void registerAnimatorRecipe(IAnimatorRecipe recipe) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AnimatorRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterAnimatorRecipe(IAnimatorRecipe recipe) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AnimatorRecipe.removeRecipe(recipe);
	}

	@Override
	public List<IAnimatorRecipe> getAnimatorRecipes() {
		return Collections.unmodifiableList(AnimatorRecipe.getRecipes());
	}

	@Override
	public void registerCompostBinRecipe(ICompostBinRecipe recipe) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		CompostRecipe.addRecipe(recipe);
	}

	@Override
	public void unregisterCompostBinRecipe(ICompostBinRecipe recipe) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		CompostRecipe.removeRecipe(recipe);
	}

	@Override
	public List<ICompostBinRecipe> getCompostBinRecipes() {
		return Collections.unmodifiableList(CompostRecipe.RECIPES);
	}

	@Override
	public void registerAmuletSupportingEntity(Class<? extends EntityLivingBase> entity) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		ItemAmulet.SUPPORTED_ENTITIES.add(entity);
	}

	@Override
	public void unregisterAmuletSupportingEntity(Class<? extends EntityLivingBase> entity) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		ItemAmulet.SUPPORTED_ENTITIES.remove(entity);
	}

	@Override
	public void registerWhitelistedOverworldItem(Item item) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		OverworldItemHandler.WHITELIST.add(item);
	}

	@Override
	public void unregisterWhitelistedOverworldItem(Item item) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		OverworldItemHandler.WHITELIST.remove(item);
	}

	@Override
	public void registerAspectType(IAspectType aspect, int tier, int group, float baseAmount) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AspectManager.registerAspect(aspect, tier, group, baseAmount);
	}

	@Override
	public void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, int tier, int group,
			float amountMultiplier, float amountVariation, int aspectCount) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AspectManager.addStaticAspectsToItem(item, matcher, tier, group, amountMultiplier, amountVariation, aspectCount);
	}
}
