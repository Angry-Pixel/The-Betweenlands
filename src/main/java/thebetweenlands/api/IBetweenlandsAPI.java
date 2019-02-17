package thebetweenlands.api;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.common.herblore.aspect.IItemStackMatcher;

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

	/**
	 * Registers a druid altar recipe
	 * @param recipe
	 */
	public void registerDruidAltarRecipe(IDruidAltarRecipe recipe);

	/**
	 * Unregisters a druid altar recipe
	 */
	public void unregisterDruidAltarRecipe(IDruidAltarRecipe recipe);

	/**
	 * Returns a list of all druid altar recipes
	 * @return
	 */
	public List<IDruidAltarRecipe> getDruidAltarRecipes();

	/**
	 * Registers a pestle and mortar recipe
	 * @param recipe
	 */
	public void registerPestleAndMortarRecipe(IPestleAndMortarRecipe recipe);

	/**
	 * Unregisters a pestle and mortar recipe
	 */
	public void unregisterPestleAndMortarRecipe(IPestleAndMortarRecipe recipe);

	/**
	 * Returns a list of all pestle and mortar recipes
	 * @return
	 */
	public List<IPestleAndMortarRecipe> getPestleAndMortarRecipes();

	/**
	 * Registers an entity to be equippable with amulets
	 * @param entity
	 */
	public void registerAmuletSupportingEntity(Class<? extends EntityLivingBase> entity);

	/**
	 * Unregisters an entity from being equippable with amulets
	 * @param entity
	 */
	public void unregisterAmuletSupportingEntity(Class<? extends EntityLivingBase> entity);

	/**
	 * Adds an item to the overworld item whitelist
	 * @param id The ID of the predicate
	 * @param predicate The predicate that returns whether an item is whitelisted
	 */
	public void registerWhitelistedOverworldItem(ResourceLocation id, Predicate<ItemStack> predicate);

	/**
	 * Removes an item from the overworld item whitelist
	 * @param id The ID of the predicate
	 * @param predicate The predicate that returns whether an item is whitelisted
	 */
	public void unregisterWhitelistedOverworldItem(ResourceLocation id);

	/**
	 * Registers an aspect type
	 * <p>Must be called during INITIALIZATION</p>
	 * @param aspect
	 * @param tier
	 * @param group
	 * @param baseAmount
	 */
	public void registerAspectType(IAspectType aspect, int tier, int group, int baseAmount);

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
