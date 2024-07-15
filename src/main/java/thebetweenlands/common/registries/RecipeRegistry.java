package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.recipe.DruidAltarRecipe;
import thebetweenlands.common.items.recipe.PurifierRecipe;

public class RecipeRegistry {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TheBetweenlands.ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DruidAltarRecipe>> DRUID_ALTAR_SERIALIZER = RECIPE_SERIALIZERS.register("druid_altar", DruidAltarRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PurifierRecipe>> PURIFIER_SERIALIZER = RECIPE_SERIALIZERS.register("purifier", PurifierRecipe.Serializer::new);

	public static final DeferredHolder<RecipeType<?>, RecipeType<DruidAltarRecipe>> DRUID_ALTAR_RECIPE = RECIPE_TYPES.register("druid_altar", () -> RecipeType.simple(TheBetweenlands.prefix("druid_altar")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<PurifierRecipe>> PURIFIER_RECIPE = RECIPE_TYPES.register("purifier", () -> RecipeType.simple(TheBetweenlands.prefix("purifier")));
}
