package thebetweenlands.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.datamaps.CompostableItem;
import thebetweenlands.common.item.recipe.BubblerCrabPotFilterRecipe;
import thebetweenlands.common.item.recipe.SiltCrabPotFilterRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataMapRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.compat.CompostRecipe;
import thebetweenlands.compat.emi.recipes.*;

import java.util.List;

@EmiEntrypoint
public class BetweenlandsEMICompat implements EmiPlugin {

	public static final BLRecipeCategory ANIMATOR = new BLRecipeCategory("animator", BlockRegistry.ANIMATOR);
	public static final BLRecipeCategory BUBBLER_CRAB_FILTER = new BLRecipeCategory("bubbler_crab_pot_filter", BlockRegistry.CRAB_POT_FILTER);
	public static final BLRecipeCategory COMPOST = new BLRecipeCategory("compost", BlockRegistry.COMPOST_BIN);
	public static final BLRecipeCategory DRUID_ALTAR = new BLRecipeCategory("druid_altar", BlockRegistry.DRUID_ALTAR);
	public static final BLRecipeCategory MORTAR = new BLRecipeCategory("mortar", BlockRegistry.MORTAR);
	public static final BLRecipeCategory PURIFIER = new BLRecipeCategory("purifier", BlockRegistry.PURIFIER);
	public static final BLRecipeCategory SILT_CRAB_FILTER = new BLRecipeCategory("silt_crab_pot_filter", BlockRegistry.CRAB_POT_FILTER);
	public static final BLRecipeCategory SMOKING_RACK = new BLRecipeCategory("smoking_rack", BlockRegistry.SMOKING_RACK);
	public static final BLRecipeCategory STEEPING_POT = new BLRecipeCategory("steeping_pot", BlockRegistry.STEEPING_POT);

	@Override
	@SuppressWarnings("unchecked")
	public void register(EmiRegistry registry) {
		registry.addCategory(ANIMATOR);
		registry.addWorkstation(ANIMATOR, EmiStack.of(BlockRegistry.ANIMATOR));
		this.getAllRecipesFor(RecipeRegistry.ANIMATOR_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiAnimatorRecipe(holder)));

		registry.addCategory(COMPOST);
		registry.addWorkstation(COMPOST, EmiStack.of(BlockRegistry.COMPOST_BIN));
		BuiltInRegistries.ITEM.holders().mapMulti((ref, consumer) -> {
			CompostableItem item = ref.getData(DataMapRegistry.COMPOSTABLE);
			if (item != null) {
				ResourceLocation recipeUid = TheBetweenlands.prefix("composting_" + ref.getKey().location().toString().replace(':','_'));
				CompostRecipe recipe = new CompostRecipe(ref.value(), item.time(), item.amount(), recipeUid);
				consumer.accept(recipe);
			}
		}).map(CompostRecipe.class::cast).forEach(holder -> registry.addRecipe(new EmiCompostRecipe(holder)));

		registry.addCategory(DRUID_ALTAR);
		registry.addWorkstation(DRUID_ALTAR, EmiStack.of(BlockRegistry.DRUID_ALTAR));
		this.getAllRecipesFor(RecipeRegistry.DRUID_ALTAR_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiDruidAltarRecipe(holder)));

		registry.addCategory(MORTAR);
		registry.addWorkstation(MORTAR, EmiStack.of(BlockRegistry.MORTAR));
		this.getAllRecipesFor(RecipeRegistry.MORTAR_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiMortarRecipe(holder)));

		registry.addCategory(PURIFIER);
		registry.addWorkstation(PURIFIER, EmiStack.of(BlockRegistry.PURIFIER));
		this.getAllRecipesFor(RecipeRegistry.PURIFIER_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiPurifierRecipe(holder)));

		registry.addCategory(SMOKING_RACK);
		registry.addWorkstation(SMOKING_RACK, EmiStack.of(BlockRegistry.SMOKING_RACK));
		this.getAllRecipesFor(RecipeRegistry.SMOKING_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiSmokingRackRecipe(holder)));

		registry.addCategory(STEEPING_POT);
		registry.addWorkstation(STEEPING_POT, EmiStack.of(BlockRegistry.STEEPING_POT));
		this.getAllRecipesFor(RecipeRegistry.STEEPING_POT_RECIPE.get()).forEach(holder -> registry.addRecipe(new EmiSteepingPotRecipe(holder)));

		registry.addCategory(BUBBLER_CRAB_FILTER);
		registry.addWorkstation(BUBBLER_CRAB_FILTER, EmiStack.of(BlockRegistry.CRAB_POT_FILTER));
		this.getAllRecipesFor(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get()).stream().filter(holder -> holder.value() instanceof BubblerCrabPotFilterRecipe).map(RecipeHolder.class::cast).forEach(holder -> registry.addRecipe(new EmiBubblerCrabFilterRecipe((RecipeHolder<BubblerCrabPotFilterRecipe>)holder)));

		registry.addCategory(SILT_CRAB_FILTER);
		registry.addWorkstation(SILT_CRAB_FILTER, EmiStack.of(BlockRegistry.CRAB_POT_FILTER));
		this.getAllRecipesFor(RecipeRegistry.CRAB_POT_FILTER_RECIPE.get()).stream().filter(holder -> holder.value() instanceof SiltCrabPotFilterRecipe).map(RecipeHolder.class::cast).forEach(holder -> registry.addRecipe(new EmiSiltCrabFilterRecipe((RecipeHolder<SiltCrabPotFilterRecipe>)holder)));
	}

	private <I extends RecipeInput, T extends Recipe<I>> List<RecipeHolder<T>> getAllRecipesFor(RecipeType<T> type) {
		return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type);
	}
}
