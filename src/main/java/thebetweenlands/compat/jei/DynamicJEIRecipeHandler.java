package thebetweenlands.compat.jei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DynamicJEIRecipeHandler {
	private DynamicJEIRecipeHandler() { }

	private static final Map<Integer, List<IRecipeWrapper>> DYNAMIC_RECIPES = new HashMap<>();

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		World world = event.getWorld();
		List<IRecipeWrapper> dynamicRecipes = getDynamicRecipes(world);
		if(!dynamicRecipes.isEmpty()) {
			for(IRecipeWrapper recipe : dynamicRecipes) {
				BetweenlandsJEIPlugin.jeiRuntime.getRecipeRegistry().addRecipe(recipe);
			}
			DYNAMIC_RECIPES.put(world.provider.getDimension(), dynamicRecipes);
		}
	}

	@SubscribeEvent
	public static void onWorldUnload(WorldEvent.Unload event) {
		World world = event.getWorld();
		List<IRecipeWrapper> dynamicRecipes = DYNAMIC_RECIPES.get(world.provider.getDimension());
		if(dynamicRecipes != null) {
			for(IRecipeWrapper recipe : dynamicRecipes) {
				BetweenlandsJEIPlugin.jeiRuntime.getRecipeRegistry().removeRecipe(recipe);
			}
			DYNAMIC_RECIPES.remove(world.provider.getDimension());
		}
	}

	/**
	 * Returns a list of dynamic recipes for the specified world
	 * @param world
	 * @return
	 */
	private static List<IRecipeWrapper> getDynamicRecipes(World world) {
		List<IRecipeWrapper> dynamicRecipes = new ArrayList<>();

		//TODO Animator recipes need to be synced to clients
		//Add animator recipes
		//dynamicRecipes.addAll(AnimatorRecipeMaker.getRecipesRuntime(world));

		return dynamicRecipes;
	}
}
