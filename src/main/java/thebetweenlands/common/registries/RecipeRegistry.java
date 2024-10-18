package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.api.recipes.*;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.item.recipe.*;
import thebetweenlands.common.item.recipe.special.*;
import thebetweenlands.util.AspectIngredient;

import javax.annotation.Nullable;

public class RecipeRegistry {

	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, TheBetweenlands.ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, TheBetweenlands.ID);
	public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnadiaSmokingRecipe>> ANADIA_SMOKING_SERIALIZER = RECIPE_SERIALIZERS.register("anadia_smoking", AnadiaSmokingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AnadiaTrimmingRecipe>> ANADIA_TRIMMING_SERIALIZER = RECIPE_SERIALIZERS.register("anadia_trimming", AnadiaTrimmingRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicAnimatorRecipe>> ANIMATOR_SERIALIZER = RECIPE_SERIALIZERS.register("animator", BasicAnimatorRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ToolRepairAnimatorRecipe>> ANIMATOR_TOOL_SERIALIZER = RECIPE_SERIALIZERS.register("animator_tool", ToolRepairAnimatorRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BubblerCrabPotFilterRecipe>> BUBBLER_CRAB_POT_FILTER_SERIALIZER = RECIPE_SERIALIZERS.register("bubbler_crab_pot_filter", BubblerCrabPotFilterRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DruidAltarAssemblyRecipe>> DRUID_ALTAR_ASSEMBLY_SERIALIZER = RECIPE_SERIALIZERS.register("druid_altar_assembly", DruidAltarAssemblyRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DruidAltarReversionRecipe>> DRUID_ALTAR_REVERSION_SERIALIZER = RECIPE_SERIALIZERS.register("druid_altar_reversion", DruidAltarReversionRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FluidSteepingPotRecipe>> FLUID_STEEPING_POT_SERIALIZER = RECIPE_SERIALIZERS.register("fluid_steeping", FluidSteepingPotRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemSteepingPotRecipe>> ITEM_STEEPING_POT_SERIALIZER = RECIPE_SERIALIZERS.register("item_steeping", ItemSteepingPotRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarGrindRecipe>> MORTAR_GRIND_SERIALIZER = RECIPE_SERIALIZERS.register("mortar_grind", MortarGrindRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MortarAspectrusRecipe>> MORTAR_ASPECTRUS_SERIALIZER = RECIPE_SERIALIZERS.register("mortar_aspectrus", MortarAspectrusRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PurifierRecipe>> PURIFIER_SERIALIZER = RECIPE_SERIALIZERS.register("purifier", PurifierRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SiltCrabPotFilterRecipe>> SILT_CRAB_POT_FILTER_SERIALIZER = RECIPE_SERIALIZERS.register("silt_crab_pot_filter", SiltCrabPotFilterRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicSmokingRackRecipe>> SMOKING_SERIALIZER = RECIPE_SERIALIZERS.register("smoking_rack", BasicSmokingRackRecipe.Serializer::new);
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicTrimmingTableRecipe>> TRIMMING_SERIALIZER = RECIPE_SERIALIZERS.register("trimming", BasicTrimmingTableRecipe.Serializer::new);

	public static final DeferredHolder<RecipeType<?>, RecipeType<AnimatorRecipe>> ANIMATOR_RECIPE = RECIPE_TYPES.register("animator", () -> RecipeType.simple(TheBetweenlands.prefix("animator")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<CrabPotFilterRecipe>> CRAB_POT_FILTER_RECIPE = RECIPE_TYPES.register("crab_pot_filter", () -> RecipeType.simple(TheBetweenlands.prefix("crab_pot_filter")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<DruidAltarRecipe>> DRUID_ALTAR_RECIPE = RECIPE_TYPES.register("druid_altar", () -> RecipeType.simple(TheBetweenlands.prefix("druid_altar")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<MortarRecipe>> MORTAR_RECIPE = RECIPE_TYPES.register("mortar", () -> RecipeType.simple(TheBetweenlands.prefix("mortar")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<PurifierRecipe>> PURIFIER_RECIPE = RECIPE_TYPES.register("purifier", () -> RecipeType.simple(TheBetweenlands.prefix("purifier")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<SmokingRackRecipe>> SMOKING_RECIPE = RECIPE_TYPES.register("smoking_rack", () -> RecipeType.simple(TheBetweenlands.prefix("smoking_rack")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<SteepingPotRecipe>> STEEPING_POT_RECIPE = RECIPE_TYPES.register("steeping_pot", () -> RecipeType.simple(TheBetweenlands.prefix("steeping_pot")));
	public static final DeferredHolder<RecipeType<?>, RecipeType<TrimmingTableRecipe>> TRIMMING_TABLE_RECIPE = RECIPE_TYPES.register("trimming_table", () -> RecipeType.simple(TheBetweenlands.prefix("trimming_table")));

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AddWormToRodRecipe>> FISHING_ROD_WORM = RECIPE_SERIALIZERS.register("add_worm_to_fishing_rod", () -> new SimpleCraftingRecipeSerializer<>(AddWormToRodRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CircleGemRecipe>> CIRCLE_GEM = RECIPE_SERIALIZERS.register("add_circle_gem", () -> new SimpleCraftingRecipeSerializer<>(CircleGemRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CoatToolRecipe>> COAT_TOOL = RECIPE_SERIALIZERS.register("coat_tool", () -> new SimpleCraftingRecipeSerializer<>(CoatToolRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CleanToolRecipe>> CLEAN_TOOL = RECIPE_SERIALIZERS.register("clean_tool", () -> new SimpleCraftingRecipeSerializer<>(CleanToolRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DyeLurkerPouchRecipe>> DYE_LURKER_POUCH = RECIPE_SERIALIZERS.register("dye_lurker_pouch", () -> new SimpleCraftingRecipeSerializer<>(DyeLurkerPouchRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EmptyAspectVialRecipe>> EMPTY_ASPECT_VIAL = RECIPE_SERIALIZERS.register("empty_aspect_vial", () -> new SimpleCraftingRecipeSerializer<>(EmptyAspectVialRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HearthgroveTarringRecipe>> HEARTHGROVE_TARRING = RECIPE_SERIALIZERS.register("hearthgrove_tarring", () -> new SimpleCraftingRecipeSerializer<>(HearthgroveTarringRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HerbloreBookCopyRecipe>> HERBLORE_BOOK_COPY = RECIPE_SERIALIZERS.register("copy_herblore_book", () -> new SimpleCraftingRecipeSerializer<>(HerbloreBookCopyRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LifeCrystalRechargeRecipe>> LIFE_CRYSTAL_RECHARGE = RECIPE_SERIALIZERS.register("recharge_life_crystal", () -> new SimpleCraftingRecipeSerializer<>(LifeCrystalRechargeRecipe::new));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<UpgradeFishBaitRecipe>> UPGRADE_FISH_BAIT = RECIPE_SERIALIZERS.register("upgrade_fish_bait", () -> new SimpleCraftingRecipeSerializer<>(UpgradeFishBaitRecipe::new));

	public static final DeferredHolder<IngredientType<?>, IngredientType<AspectIngredient>> ASPECT_INGREDIENT_TYPE = INGREDIENT_TYPES.register("aspect", () -> new IngredientType<>(AspectIngredient.CODEC));

	public static boolean doesSteepingPotUseItem(@Nullable Level level, ItemStack stack) {
		if (level != null) {
			for (RecipeHolder<SteepingPotRecipe> recipe : level.getRecipeManager().getAllRecipesFor(STEEPING_POT_RECIPE.get())) {
				for (Ingredient ingredient : recipe.value().getIngredients()) {
					if (ingredient.test(stack)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
