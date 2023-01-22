package thebetweenlands.common;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.IBetweenlandsAPI;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.entity.IEntityMusicProvider;
import thebetweenlands.api.recipes.IAnimatorRecipe;
import thebetweenlands.api.recipes.ICompostBinRecipe;
import thebetweenlands.api.recipes.IDruidAltarRecipe;
import thebetweenlands.api.recipes.IPestleAndMortarRecipe;
import thebetweenlands.api.recipes.IPurifierRecipe;
import thebetweenlands.client.handler.MusicHandler;
import thebetweenlands.common.handler.OverworldItemHandler;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.aspect.IItemStackMatcher;
import thebetweenlands.common.item.equipment.ItemAmulet;
import thebetweenlands.common.recipe.misc.AnimatorRecipe;
import thebetweenlands.common.recipe.misc.CompostRecipe;
import thebetweenlands.common.recipe.misc.DruidAltarRecipe;
import thebetweenlands.common.recipe.mortar.PestleAndMortarRecipe;
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

	@Override
	public void registerDruidAltarRecipe(IDruidAltarRecipe recipe) {
		DruidAltarRecipe.addRecipe(recipe);		
	}

	@Override
	public void unregisterDruidAltarRecipe(IDruidAltarRecipe recipe) {
		DruidAltarRecipe.removeRecipe(recipe);		
	}

	@Override
	public List<IDruidAltarRecipe> getDruidAltarRecipes() {
		return DruidAltarRecipe.getRecipes();
	}

	@Override
	public void registerPestleAndMortarRecipe(IPestleAndMortarRecipe recipe) {
		PestleAndMortarRecipe.addRecipe(recipe);		
	}

	@Override
	public void unregisterPestleAndMortarRecipe(IPestleAndMortarRecipe recipe) {
		PestleAndMortarRecipe.removeRecipe(recipe);	
	}

	@Override
	public List<IPestleAndMortarRecipe> getPestleAndMortarRecipes() {
		return PestleAndMortarRecipe.getRecipes();
	}

	@Override
	public void registerAmuletSupportingEntity(Class<? extends EntityLivingBase> entity) {
		ItemAmulet.SUPPORTED_ENTITIES.add(entity);
	}

	@Override
	public void unregisterAmuletSupportingEntity(Class<? extends EntityLivingBase> entity) {
		ItemAmulet.SUPPORTED_ENTITIES.remove(entity);
	}
	
	@Override
	public void registerWhitelistedOverworldItem(ResourceLocation id, Predicate<ItemStack> predicate) {
		OverworldItemHandler.TOOL_WHITELIST.put(id, predicate);
	}

	@Override
	public void unregisterWhitelistedOverworldItem(ResourceLocation id) {
		OverworldItemHandler.TOOL_WHITELIST.remove(id);
	}

	@Override
	public void registerAspectType(IAspectType aspect, int tier, int group, int baseAmount) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AspectManager.registerAspect(aspect, tier, group, baseAmount);
	}

	@Override
	public void addStaticAspectsToItem(ItemStack item, IItemStackMatcher matcher, int tier, int group,
			float amountMultiplier, float amountVariation, int aspectCount) {
		Preconditions.checkState(Loader.instance().isInState(LoaderState.INITIALIZATION), "Must be called during INITIALIZATION");
		AspectManager.addStaticAspectsToItem(item, matcher, tier, group, amountMultiplier, amountVariation, aspectCount);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean registerEntityMusicProvider(Class<? extends Entity> entityCls, IEntityMusicProvider musicProvider) {
		return MusicHandler.INSTANCE.registerEntityMusicProvider(entityCls, musicProvider);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean unregisterEntityMusicProvider(Class<? extends Entity> entityCls, IEntityMusicProvider musicProvider) {
		return MusicHandler.INSTANCE.unregisterEntityMusicProvider(entityCls, musicProvider);
	}
}
