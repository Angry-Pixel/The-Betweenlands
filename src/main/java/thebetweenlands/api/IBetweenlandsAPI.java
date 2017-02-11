package thebetweenlands.api;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.herblore.aspect.IItemStackMatcher;

public interface IBetweenlandsAPI {
	/**
	 * Registers a purifier recipe
	 * <p>Must be called during INITIALIZATION</p>
	 * @param recipe
	 */
	public void registerPurifierRecipe(IPurifierRecipe recipe);

	/**
	 * Unregisters a purified recipe
	 * <p>Must be called during INITIALIZATION</p>
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
	 * <p>Must be called during INITIALIZATION</p>
	 * @param recipe
	 */
	public void registerAnimatorRecipe(IAnimatorRecipe recipe);

	/**
	 * Unregisters an animator recipe
	 * <p>Must be called during INITIALIZATION</p>
	 */
	public void unregisterAnimatorRecipe(IAnimatorRecipe recipe);

	/**
	 * Returns a list of all animator recipes
	 * @return
	 */
	public List<IAnimatorRecipe> getAnimatorRecipes();

	/**
	 * Registers a compost bin recipe
	 * <p>Must be called during INITIALIZATION</p>
	 * @param recipe
	 */
	public void registerCompostBinRecipe(ICompostBinRecipe recipe);

	/**
	 * Unregisters a compost bin recipe
	 * <p>Must be called during INITIALIZATION</p>
	 */
	public void unregisterCompostBinRecipe(ICompostBinRecipe recipe);

	/**
	 * Returns a list of all compost bin recipes
	 * @return
	 */
	public List<ICompostBinRecipe> getCompostBinRecipes();

	/**
	 * Registers an entity to be equippable with amulets
	 * <p>Must be called during INITIALIZATION</p>
	 * @param entity
	 */
	public void registerAmuletSupportingEntity(Class<? extends EntityLivingBase> entity);

	/**
	 * Unregisters an entity from being equippable with amulets
	 * <p>Must be called during INITIALIZATION</p>
	 * @param entity
	 */
	public void unregisterAmuletSupportingEntity(Class<? extends EntityLivingBase> entity);

	/**
	 * Adds an item to the overworld item whitelist
	 * <p>Must be called during INITIALIZATION</p>
	 * @param item
	 */
	public void registerWhitelistedOverworldItem(Item item);

	/**
	 * Removes an item from the overworld item whitelist
	 * <p>Must be called during INITIALIZATION</p>
	 * @param item
	 */
	public void unregisterWhitelistedOverworldItem(Item item);

	/**
	 * Registers an aspect type
	 * <p>Must be called during INITIALIZATION</p>
	 * @param aspect
	 * @param tier
	 * @param group
	 * @param baseAmount
	 */
	public void registerAspectType(IAspectType aspect, int tier, int group, float baseAmount);

	/**
	 * Adds static aspects to an item
	 * <p>Must be called during INITIALIZATION</p>
	 * @param item
	 * @param matcher
	 * @param tier
	 * @param type
	 * @param amountMultiplier
	 * @param amountVariation
	 * @param aspectCount
	 */
	public void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, int tier, int group, float amountMultiplier, float amountVariation, int aspectCount);
}
