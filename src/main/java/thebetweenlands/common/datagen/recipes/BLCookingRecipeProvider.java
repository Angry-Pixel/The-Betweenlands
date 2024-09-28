package thebetweenlands.common.datagen.recipes;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.DataComponentRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BLCookingRecipeProvider {

	public static void buildRecipes(RecipeOutput output, HolderLookup.Provider registries) {
		smelting(output, BlockRegistry.CHISELED_CRAGROCK, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_CHISELED_CRAGROCK, 0.1F);
		smelting(output, BlockRegistry.BETWEENSTONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_BETWEENSTONE_BRICKS, 0.1F);
		smelting(output, BlockRegistry.BETWEENSTONE_TILES, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_BETWEENSTONE_TILES, 0.1F);
		smelting(output, BlockRegistry.LIMESTONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_LIMESTONE_BRICKS, 0.1F);
		smelting(output, BlockRegistry.CRAGROCK_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_CRAGROCK_BRICKS, 0.1F);
		smelting(output, BlockRegistry.CRAGROCK_TILES, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_CRAGROCK_TILES, 0.1F);
		smelting(output, BlockRegistry.LIMESTONE, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_LIMESTONE, 0.1F);
		smelting(output, BlockRegistry.BETWEENSTONE, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMOOTH_BETWEENSTONE, 0.1F);
		smelting(output, BlockRegistry.CRAGROCK, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMOOTH_CRAGROCK, 0.1F);
		smelting(output, BlockRegistry.GREEN_DENTROTHYST, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_GREEN_DENTROTHYST, 0.3F);
		smelting(output, BlockRegistry.ORANGE_DENTROTHYST, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_ORANGE_DENTROTHYST, 0.3F);
		smelting(output, BlockRegistry.PITSTONE, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMOOTH_PITSTONE, 0.1F);
		smelting(output, BlockRegistry.SILT, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SILT_GLASS, 0.2F);
		smelting(output, BlockRegistry.FILTERED_SILT, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FILTERED_SILT_GLASS, 0.3F);
		smelting(output, BlockRegistry.MUD_TILES, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CRACKED_MUD_TILES, 0.1F);
		smelting(output, BlockRegistry.MOSS, RecipeCategory.DECORATIONS, BlockRegistry.DEAD_MOSS, 0.1F);
		smelting(output, BlockRegistry.LICHEN, RecipeCategory.DECORATIONS, BlockRegistry.DEAD_LICHEN, 0.1F);
		smelting(output, BlockRegistry.WEEDWOOD_BUSH, RecipeCategory.DECORATIONS, BlockRegistry.DEAD_WEEDWOOD_BUSH, 0.1F);
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BlockRegistry.EXTINGUISHED_SULFUR_TORCH), RecipeCategory.DECORATIONS, BlockRegistry.SULFUR_TORCH, 0.0F, 200).unlockedBy("has_torch", has(BlockRegistry.SULFUR_TORCH)).save(output, TheBetweenlands.prefix("reignite_sulfur_torch"));
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(BlockRegistry.DAMP_TORCH), RecipeCategory.DECORATIONS, Blocks.TORCH, 0.0F, 200).unlockedBy("has_torch", has(BlockRegistry.DAMP_TORCH)).save(output, TheBetweenlands.prefix("reignite_torch"));
		smelting(output, BlockRegistry.SWAMP_REED, RecipeCategory.MISC, ItemRegistry.DRIED_SWAMP_REED, 0.1F);
		smelting(output, BlockRegistry.MUD, RecipeCategory.MISC, new ItemStack(ItemRegistry.MUD_BRICK.get(), 4), 0.2F);
		smelting(output, DataComponentIngredient.of(true, DataComponentPredicate.builder().expect(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.RUBBER_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.WEEDWOOD_BUCKET.get()), RecipeCategory.MISC, new ItemStack(ItemRegistry.RUBBER_BALL.get(), 3), 0.5F);
		smelting(output, DataComponentIngredient.of(true, DataComponentPredicate.builder().expect(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.RUBBER_STILL, FluidType.BUCKET_VOLUME))).build(), ItemRegistry.SYRMORITE_BUCKET.get()), RecipeCategory.MISC, ItemRegistry.SOLID_RUBBER_SYRMORITE_BUCKET.get(), 0.5F);
		smelting(output, BlockRegistry.SYRMORITE_ORE, RecipeCategory.MISC, ItemRegistry.SYRMORITE_INGOT, 0.7F);
		smelting(output, Ingredient.of(ItemRegistry.SYRMORITE_HELMET, ItemRegistry.SYRMORITE_CHESTPLATE, ItemRegistry.SYRMORITE_LEGGINGS, ItemRegistry.SYRMORITE_BOOTS), RecipeCategory.MISC, ItemRegistry.SYRMORITE_NUGGET, 0.1F);
		smelting(output, Ingredient.of(ItemRegistry.OCTINE_SWORD, ItemRegistry.OCTINE_PICKAXE, ItemRegistry.OCTINE_AXE, ItemRegistry.OCTINE_SHOVEL), RecipeCategory.MISC, ItemRegistry.OCTINE_NUGGET, 0.1F);
		smelting(output, Ingredient.of(ItemRegistry.VALONITE_HELMET, ItemRegistry.VALONITE_CHESTPLATE, ItemRegistry.VALONITE_LEGGINGS, ItemRegistry.VALONITE_BOOTS, ItemRegistry.VALONITE_SWORD, ItemRegistry.VALONITE_PICKAXE, ItemRegistry.VALONITE_AXE, ItemRegistry.VALONITE_SHOVEL), RecipeCategory.MISC, ItemRegistry.VALONITE_SPLINTER, 0.1F);
		smelting(output, ItemRegistry.MIRE_SNAIL_EGG, RecipeCategory.FOOD, ItemRegistry.COOKED_MIRE_SNAIL_EGG, 0.4F);
		smelting(output, ItemRegistry.RAW_FROG_LEGS, RecipeCategory.FOOD, ItemRegistry.COOKED_FROG_LEGS, 0.3F);
		smelting(output, ItemRegistry.RAW_SNAIL_FLESH, RecipeCategory.FOOD, ItemRegistry.COOKED_SNAIL_FLESH, 0.3F);
		smelting(output, ItemRegistry.KRAKEN_TENTACLE, RecipeCategory.FOOD, new ItemStack(ItemRegistry.KRAKEN_CALAMARI.get(), 5), 0.3F);
		smelting(output, ItemRegistry.SLUDGE_BALL, RecipeCategory.FOOD, ItemRegistry.SLUDGE_JELLO, 0.3F);
		smelting(output, BlockRegistry.SWAMP_KELP, RecipeCategory.FOOD, ItemRegistry.FRIED_SWAMP_KELP, 0.1F);
		smelting(output, ItemRegistry.RAW_ANADIA_MEAT, RecipeCategory.FOOD, ItemRegistry.COOKED_ANADIA_MEAT, 0.3F);
		smelting(output, ItemRegistry.BARNACLE, RecipeCategory.FOOD, ItemRegistry.COOKED_BARNACLE, 0.1F);
		smelting(output, ItemRegistry.RAW_OLM_EGG, RecipeCategory.FOOD, ItemRegistry.COOKED_OLM_EGG, 0.1F);
		smelting(output, ItemRegistry.OLMLETTE_MIXTURE, RecipeCategory.FOOD, ItemRegistry.OLMLETTE, 0.4F);
		smelting(output, BlockRegistry.OCTINE_ORE, RecipeCategory.MISC, ItemRegistry.OCTINE_INGOT, 0.7F);
	}

	private static void smelting(RecipeOutput output, ItemLike input, RecipeCategory category, ItemLike result, float xp) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), category, result, xp, 200).unlockedBy("has_input", has(input)).save(output);
	}

	private static void smelting(RecipeOutput output, Ingredient input, RecipeCategory category, ItemLike result, float xp) {
		SimpleCookingRecipeBuilder.smelting(input, category, result, xp, 200).unlockedBy("has_input", inventoryTrigger(ItemPredicate.Builder.item().of(Arrays.stream(input.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new)))).save(output);
	}

	private static void smelting(RecipeOutput output, ItemLike input, RecipeCategory category, ItemStack result, float xp) {
		SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), category, result, xp, 200).unlockedBy("has_input", has(input)).save(output);
	}

	private static void smelting(RecipeOutput output, Ingredient input, RecipeCategory category, ItemStack result, float xp) {
		SimpleCookingRecipeBuilder.smelting(input, category, result, xp, 200).unlockedBy("has_input", inventoryTrigger(ItemPredicate.Builder.item().of(Arrays.stream(input.getItems()).map(ItemStack::getItem).toArray(ItemLike[]::new)))).save(output);
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike itemLike) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(itemLike));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
		return inventoryTrigger(ItemPredicate.Builder.item().of(tag));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate.Builder... items) {
		return inventoryTrigger(Arrays.stream(items).map(ItemPredicate.Builder::build).toArray(ItemPredicate[]::new));
	}

	protected static Criterion<InventoryChangeTrigger.TriggerInstance> inventoryTrigger(ItemPredicate... predicates) {
		return CriteriaTriggers.INVENTORY_CHANGED
			.createCriterion(new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(predicates)));
	}
}
