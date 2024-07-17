package thebetweenlands.common.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.recipes.DruidAltarRecipe;
import thebetweenlands.api.recipes.MortarRecipe;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.items.recipe.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class RecipeRegistry {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TheBetweenlands.ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TheBetweenlands.ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DruidAltarAssemblyRecipe>> DRUID_ALTAR_ASSEMBLY_SERIALIZER = RECIPE_SERIALIZERS.register("druid_altar_assembly", DruidAltarAssemblyRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DruidAltarReversionRecipe>> DRUID_ALTAR_REVERSION_SERIALIZER = RECIPE_SERIALIZERS.register("druid_altar_reversion", DruidAltarReversionRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarGrindRecipe>> MORTAR_GRIND_SERIALIZER = RECIPE_SERIALIZERS.register("mortar_grind", MortarGrindRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarAspectrusRecipe>> MORTAR_ASPECTRUS_SERIALIZER = RECIPE_SERIALIZERS.register("mortar_aspectrus", MortarAspectrusRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PurifierRecipe>> PURIFIER_SERIALIZER = RECIPE_SERIALIZERS.register("purifier", PurifierRecipe.Serializer::new);

	public static final DeferredHolder<RecipeType<?>, RecipeType<DruidAltarRecipe>> DRUID_ALTAR_RECIPE = RECIPE_TYPES.register("druid_altar", () -> RecipeType.simple(TheBetweenlands.prefix("druid_altar")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<MortarRecipe>> MORTAR_RECIPE = RECIPE_TYPES.register("mortar", () -> RecipeType.simple(TheBetweenlands.prefix("mortar")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<PurifierRecipe>> PURIFIER_RECIPE = RECIPE_TYPES.register("purifier", () -> RecipeType.simple(TheBetweenlands.prefix("purifier")));
}
