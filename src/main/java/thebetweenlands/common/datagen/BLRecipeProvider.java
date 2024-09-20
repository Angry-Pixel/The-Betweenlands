package thebetweenlands.common.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.builders.*;
import thebetweenlands.common.datagen.tags.BLItemTagProvider;
import thebetweenlands.common.item.recipe.AnadiaSmokingRecipe;
import thebetweenlands.common.item.recipe.AnadiaTrimmingRecipe;
import thebetweenlands.common.item.recipe.MortarAspectrusRecipe;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.FluidRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.LootTableRegistry;

import java.util.concurrent.CompletableFuture;

public class BLRecipeProvider extends RecipeProvider {
	public BLRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
		DruidAltarRecipeBuilder.assembly(ItemRegistry.SWAMP_TALISMAN)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_1)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_2)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_3)
			.requires(ItemRegistry.SWAMP_TALISMAN_PIECE_4)
			.save(output);

		DruidAltarRecipeBuilder.reversion()
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.requires(ItemTags.SAPLINGS).requires(ItemTags.SAPLINGS)
			.save(output, TheBetweenlands.prefix("sapling_altar_reversion"));

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.BUBBLER_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		FishTrimmingRecipeBuilder.trimming(ItemRegistry.SILT_CRAB)
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.outputs(new ItemStack(ItemRegistry.CRAB_STICK.get(), 2))
			.outputs(ItemRegistry.SILT_CRAB_CLAW)
			.save(output);

		output.accept(TheBetweenlands.prefix("trimming/anadia"), new AnadiaTrimmingRecipe(), null);

		AnimatorRecipeBuilder.animator(ItemRegistry.TAR_BEAST_HEART)
			.setResultStack(ItemRegistry.ANIMATED_TAR_BEAST_HEART)
			.requiredFuel(32)
			.requiredLife(32)
			.save(output);

//		AnimatorRecipeBuilder.animator(ItemRegistry.INANIMATE_TARMINION)
//			.setResultStack(ItemRegistry.TARMINION)
//			.requiredFuel(8)
//			.requiredLife(8)
//			.setRenderEntity(EntityRegistry.TARMINION.get())
//			.save(output);
//
//		AnimatorRecipeBuilder.animator(ItemRegistry.SPORES)
//			.requiredFuel(8)
//			.requiredLife(4)
//			.setResultEntity(EntityRegistry.SPORELING.get())
//			.setRenderEntity(EntityRegistry.SPORELING.get())
//			.save(output, TheBetweenlands.prefix("sporeling"));
//
//		AnimatorRecipeBuilder.animator(BlockRegistry.ROOT_POD)
//			.requiredFuel(10)
//			.requiredLife(6)
//			.setResultEntity(EntityRegistry.ROOT_SPRITE.get())
//			.setRenderEntity(EntityRegistry.ROOT_SPRITE.get())
//			.save(output, TheBetweenlands.prefix("root_sprite"));

		AnimatorRecipeBuilder.animator(ItemRegistry.SMALL_SPIRIT_TREE_FACE_MASK)
			.setResultStack(ItemRegistry.ANIMATED_SMALL_SPIRIT_TREE_FACE_MASK)
			.requiredFuel(24)
			.requiredLife(24)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.INANIMATE_ANGRY_PEBBLE)
			.setResultStack(ItemRegistry.ANGRY_PEBBLE)
			.requiredFuel(1)
			.requiredLife(1)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.SLUDGE_WORM_EGG_SAC)
			.setResultStack(ItemRegistry.SLUDGE_WORM_ARROW)
			.requiredFuel(6)
			.requiredLife(3)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.SNOT)
			.setResultStack(ItemRegistry.SNOT_POD)
			.requiredFuel(1)
			.requiredLife(1)
			.save(output);

		AnimatorRecipeBuilder.animator(ItemRegistry.ITEM_SCROLL)
			.requiredFuel(16)
			.requiredLife(16)
			.setResultLoot(LootTableRegistry.SCROLL)
			.save(output, TheBetweenlands.prefix("scroll"));

		AnimatorRecipeBuilder.animator(ItemRegistry.FABRICATED_SCROLL)
			.requiredFuel(16)
			.requiredLife(16)
			.setResultLoot(LootTableRegistry.FABRICATED_SCROLL)
			.save(output, TheBetweenlands.prefix("fabricated_scroll"));

		MortarRecipeBuilder.grinding(ItemRegistry.ALGAE_CLUMP, ItemRegistry.GROUND_ALGAE).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.ANGLER_TOOTH, ItemRegistry.GROUND_ANGLER_TOOTH).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.AQUA_MIDDLE_GEM, ItemRegistry.GROUND_AQUA_MIDDLE_GEM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.ARROW_ARUM_LEAF, ItemRegistry.GROUND_ARROW_ARUM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BLACK_HAT_MUSHROOM, ItemRegistry.GROUND_BLACKHAT_MUSHROOM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CRIMSON_SNAIL_SHELL, ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BLUE_EYED_GRASS_FLOWERS, ItemRegistry.GROUND_BLUE_EYED_GRASS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BLUE_IRIS_PETAL, ItemRegistry.GROUND_BLUE_IRIS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BOG_BEAN_FLOWER_DROP, ItemRegistry.GROUND_BOG_BEAN).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BONESET_FLOWERS, ItemRegistry.GROUND_BONESET).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BOTTLE_BRUSH_GRASS_BLADES, ItemRegistry.GROUND_BOTTLE_BRUSH_GRASS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BROOMSEDGE_LEAVES, ItemRegistry.GROUND_BROOMSEDGE).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BULB_CAPPED_MUSHROOM, ItemRegistry.GROUND_BULB_CAPPED_MUSHROOM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BUTTON_BUSH_FLOWERS, ItemRegistry.GROUND_BUTTON_BUSH).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CARDINAL_FLOWER_PETALS, ItemRegistry.GROUND_CARDINAL_FLOWER).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CATTAIL_HEAD, ItemRegistry.GROUND_CATTAIL).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CAVE_GRASS_BLADES, ItemRegistry.GROUND_CAVE_GRASS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CAVE_MOSS_CLUMP, ItemRegistry.GROUND_CAVE_MOSS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.COPPER_IRIS_PETALS, ItemRegistry.GROUND_COPPER_IRIS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CRIMSON_MIDDLE_GEM, ItemRegistry.GROUND_CRIMSON_MIDDLE_GEM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.DEEP_WATER_CORAL_STALK, ItemRegistry.GROUND_DEEP_WATER_CORAL).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.DRIED_SWAMP_REED, ItemRegistry.GROUND_DRIED_SWAMP_REED).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.FLATHEAD_MUSHROOM, ItemRegistry.GROUND_FLATHEAD_MUSHROOM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.GOLDEN_CLUB_FLOWERS, ItemRegistry.GROUND_GOLDEN_CLUB).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.GREEN_MIDDLE_GEM, ItemRegistry.GROUND_GREEN_MIDDLE_GEM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.LICHEN_CLUMP, ItemRegistry.GROUND_LICHEN).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MARSH_HIBISCUS_FLOWER, ItemRegistry.GROUND_MARSH_HIBUSCUS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MARSH_MALLOW_FLOWER, ItemRegistry.GROUND_MARSH_MALLOW).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MARSH_MARIGOLD_FLOWER_DROP, ItemRegistry.GROUND_MARSH_MARIGOLD).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MILKWEED_FLOWER, ItemRegistry.GROUND_MILKWEED).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MIRE_CORAL_STALK, ItemRegistry.GROUND_MIRE_CORAL).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.OCHRE_SNAIL_SHELL, ItemRegistry.GROUND_OCHRE_SNAIL_SHELL).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.MOSS_CLUMP, ItemRegistry.GROUND_MOSS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.NETTLE_LEAF, ItemRegistry.GROUND_NETTLE).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.PHRAGMITE_STEMS, ItemRegistry.GROUND_PHRAGMITES).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.PICKERELWEED_FLOWER, ItemRegistry.GROUND_PICKERELWEED).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.SHOOT_LEAVES, ItemRegistry.GROUND_SHOOTS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.SLUDGECREEP_LEAVES, ItemRegistry.GROUND_SLUDGECREEP).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.SOFT_RUSH_LEAVES, ItemRegistry.GROUND_SOFT_RUSH).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.SUNDEW_HEAD, ItemRegistry.GROUND_SUNDEW).save(output);
		MortarRecipeBuilder.grinding(BlockRegistry.SWAMP_KELP, ItemRegistry.GROUND_SWAMP_KELP).save(output);
		MortarRecipeBuilder.grinding(Ingredient.of(BlockRegistry.GIANT_ROOT, ItemRegistry.TANGLED_ROOT), ItemRegistry.GROUND_ROOTS).save(output);
		MortarRecipeBuilder.grinding(Ingredient.of(BlockRegistry.WEEDWOOD_BARK, BlockRegistry.WEEDWOOD, BlockRegistry.WEEDWOOD_LOG, ItemRegistry.DRY_BARK), ItemRegistry.GROUND_WEEDWOOD_BARK).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.HANGER_DROP, ItemRegistry.GROUND_HANGER).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.WATER_WEEDS_DROP, ItemRegistry.GROUND_WATER_WEEDS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.VENUS_FLY_TRAP_HEAD, ItemRegistry.GROUND_VENUS_FLY_TRAP).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.VOLARPAD_LEAF, ItemRegistry.GROUND_VOLARPAD).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.THORN_BRANCH, ItemRegistry.GROUND_THORNS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.POISON_IVY_LEAF, ItemRegistry.GROUND_POISON_IVY).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.PITCHER_PLANT_TRAP, ItemRegistry.GROUND_PITCHER_PLANT).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.LEAF, ItemRegistry.GROUND_LEAF).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BLADDERWORT_FLOWER_DROP, ItemRegistry.GROUND_BLADDERWORT_FLOWER).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.BLADDERWORT_STALK_DROP, ItemRegistry.GROUND_BLADDERWORT_STALK).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.EDGE_SHROOM_GILLS, ItemRegistry.GROUND_EDGE_SHROOM).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.EDGE_MOSS_CLUMP, ItemRegistry.GROUND_EDGE_MOSS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.EDGE_LEAF_DROP, ItemRegistry.GROUND_EDGE_LEAF).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.ROTBULB_STALK, ItemRegistry.GROUND_ROTBULB).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.PALE_GRASS_BLADES, ItemRegistry.GROUND_PALE_GRASS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.STRING_ROOT_FIBERS, ItemRegistry.GROUND_STRING_ROOTS).save(output);
		MortarRecipeBuilder.grinding(ItemRegistry.CRYPTWEED_BLADES, ItemRegistry.GROUND_CRYPTWEED).save(output);
		MortarRecipeBuilder.grinding(BlockRegistry.BETWEENSTONE_PEBBLE, ItemRegistry.GROUND_BETWEENSTONE_PEBBLE).save(output);

		MortarRecipeBuilder.grinding(Ingredient.of(BlockRegistry.LIMESTONE, BlockRegistry.POLISHED_LIMESTONE), new ItemStack(ItemRegistry.LIMESTONE_FLUX.get(), 3)).save(output);
		MortarRecipeBuilder.grinding(Ingredient.of(
			ItemRegistry.SKULL_MASK, ItemRegistry.WIGHTS_BANE, ItemRegistry.SLUDGE_SLICER, ItemRegistry.CRITTER_CRUNCHER, ItemRegistry.HAG_HACKER,
			ItemRegistry.VOODOO_DOLL, ItemRegistry.SWIFT_PICK, ItemRegistry.MAGIC_ITEM_MAGNET, ItemRegistry.RING_OF_DISPERSION,
			ItemRegistry.RING_OF_ASCENT,ItemRegistry.RING_OF_POWER, ItemRegistry.RING_OF_GATHERING, ItemRegistry.RING_OF_RECRUITMENT,
			ItemRegistry.RING_OF_SUMMONING, ItemRegistry.GEM_SINGER, ItemRegistry.MIST_STAFF, ItemRegistry.SHADOW_STAFF, ItemRegistry.AMULET_SOCKET
		), ItemRegistry.LOOT_SCRAPS).save(output);
		MortarRecipeBuilder.grinding(Ingredient.of(ItemRegistry.TINY_SLUDGE_WORM, ItemRegistry.TINY_SLUDGE_WORM_HELPER), ItemRegistry.FISH_BAIT).save(output);

		output.accept(TheBetweenlands.prefix("mortar/aspectrus_fruit"), new MortarAspectrusRecipe(), null);

		SmokingRackRecipeBuilder.smoking(ItemRegistry.RAW_ANADIA_MEAT, ItemRegistry.SMOKED_ANADIA_MEAT, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.BARNACLE, ItemRegistry.SMOKED_BARNACLE, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.CRAB_STICK, ItemRegistry.SMOKED_CRAB_STICK, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.RAW_FROG_LEGS, ItemRegistry.SMOKED_FROG_LEGS, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.PUFFSHROOM_TENDRIL, ItemRegistry.SMOKED_PUFFSHROOM_TENDRIL, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.SILT_CRAB_CLAW, ItemRegistry.SMOKED_SILT_CRAB_CLAW, 200).save(output);
		SmokingRackRecipeBuilder.smoking(ItemRegistry.RAW_SNAIL_FLESH, ItemRegistry.SMOKED_SNAIL_FLESH, 200).save(output);
		SmokingRackRecipeBuilder.smoking(BlockRegistry.SWAMP_REED, ItemRegistry.DRIED_SWAMP_REED, 200).save(output);
		SmokingRackRecipeBuilder.smoking(Ingredient.of(BlockRegistry.WEEDWOOD_LOG, BlockRegistry.WEEDWOOD_BARK), ItemRegistry.DRY_BARK, 400).save(output);
		output.accept(TheBetweenlands.prefix("smoking_rack/anadia"), new AnadiaSmokingRecipe(), null);

		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.DULL_LAVENDER_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_BLUE_EYED_GRASS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.MAROON_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.SHADOW_GREEN_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_PALE_GRASS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.CAMELOT_MAGENTA_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_MILKWEED).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.SAFFRON_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_GOLDEN_CLUB).requires(ItemRegistry.GROUND_MARSH_MARIGOLD).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.CARIBBEAN_GREEN_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_DEEP_WATER_CORAL).requires(ItemRegistry.GROUND_PALE_GRASS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.VIVID_TANGERINE_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_MARSH_HIBUSCUS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.CHAMPAGNE_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_BUTTON_BUSH).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.RAISIN_BLACK_DYE_STILL.get(), 1000).requires(ItemRegistry.CREMAINS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.SUSHI_GREEN_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_SWAMP_KELP).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.ELM_CYAN_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_DEEP_WATER_CORAL).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.CADMIUM_GREEN_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_POISON_IVY).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.LAVENDER_BLUE_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_BLUE_EYED_GRASS).requires(ItemRegistry.GROUND_PICKERELWEED).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.BROWN_RUST_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_COPPER_IRIS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.MIDNIGHT_PURPLE_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_PICKERELWEED).requires(ItemRegistry.CREMAINS).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.FISH_OIL_STILL.get(), FluidRegistry.PEWTER_GREY_DYE_STILL.get(), 1000).requires(ItemRegistry.GROUND_ANGLER_TOOTH).requires(ItemRegistry.CREMAINS).save(output);

		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.FISH_OIL_STILL.get(), 1000).requires(ItemRegistry.ANADIA_REMAINS, 4).save(output);

		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.NETTLE_SOUP_STILL.get(), 1000).requires(ItemRegistry.BLACK_HAT_MUSHROOM).requires(ItemRegistry.FLATHEAD_MUSHROOM).requires(Ingredient.of(BlockRegistry.NETTLE, BlockRegistry.FLOWERED_NETTLE)).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.NETTLE_TEA_STILL.get(), 1000).requires(ItemRegistry.NETTLE_LEAF, 4).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.PHEROMONE_EXTRACT_STILL.get(), 1000).requires(ItemRegistry.PHEROMONE_THORAXES).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.SWAMP_BROTH_STILL.get(), 1000).requires(ItemRegistry.RAW_SNAIL_FLESH).requires(ItemRegistry.SAP_BALL).requires(ItemRegistry.FLATHEAD_MUSHROOM).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.STURDY_STOCK_STILL.get(), 1000).requires(ItemRegistry.RAW_SNAIL_FLESH).requires(ItemRegistry.BARNACLE).requires(ItemRegistry.RAW_ANADIA_MEAT).requires(ItemRegistry.GROUND_ARROW_ARUM).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.PEAR_CORDIAL_STILL.get(), 1000).requires(ItemRegistry.MIDDLE_FRUIT).requires(ItemRegistry.SLUDGE_BALL).requires(ItemRegistry.SAP_SPIT).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.SHAMANS_BREW_STILL.get(), 1000).requires(BlockRegistry.WISP).requires(ItemRegistry.BULB_CAPPED_MUSHROOM).requires(ItemRegistry.GROUND_MIRE_CORAL).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.LAKE_BROTH_STILL.get(), 1000).requires(ItemRegistry.SILT_CRAB_CLAW).requires(ItemRegistry.BARNACLE).requires(ItemRegistry.GROUND_ALGAE).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.SHELL_STOCK_STILL.get(), 1000).requires(ItemRegistry.RAW_SNAIL_FLESH).requires(ItemRegistry.GROUND_CRIMSON_SNAIL_SHELL, 2).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.FROG_LEG_EXTRACT_STILL.get(), 1000).requires(ItemRegistry.RAW_FROG_LEGS, 4).save(output);
		SteepingPotRecipeBuilder.steeping(FluidRegistry.CLEAN_WATER_STILL.get(), FluidRegistry.WITCH_TEA_STILL.get(), 1000).requires(ItemRegistry.TINY_SLUDGE_WORM).requires(ItemRegistry.CHIROMAW_WING).requires(ItemRegistry.RAW_FROG_LEGS).requires(ItemRegistry.YELLOW_DOTTED_FUNGUS).save(output);

		CrabPotFilterRecipeBuilder.siltCrab(Ingredient.of(BlockRegistry.FILTERED_SILT, BlockRegistry.MUD), BlockRegistry.SILT).save(output);
		CrabPotFilterRecipeBuilder.siltCrab(BlockRegistry.SWAMP_DIRT, BlockRegistry.MUD).save(output);
		CrabPotFilterRecipeBuilder.siltCrab(BlockRegistry.MOSS, BlockRegistry.PEAT).save(output);
		CrabPotFilterRecipeBuilder.siltCrab(BlockRegistry.WEEDWOOD_BARK, BlockRegistry.ROTTEN_BARK).save(output);
		CrabPotFilterRecipeBuilder.siltCrab(ItemRegistry.SAP_BALL, ItemRegistry.SLUDGE_BALL).save(output);
		CrabPotFilterRecipeBuilder.siltCrab(ItemRegistry.CREMAINS, ItemRegistry.SULFUR).save(output);

		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.SILT, BlockRegistry.FILTERED_SILT).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.MUD, BlockRegistry.SWAMP_DIRT).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.SWAMP_DIRT, BlockRegistry.PURIFIED_SWAMP_DIRT).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.AQUA_MIDDLE_GEM_ORE, ItemRegistry.AQUA_MIDDLE_GEM).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.GREEN_MIDDLE_GEM_ORE, ItemRegistry.GREEN_MIDDLE_GEM).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE, ItemRegistry.CRIMSON_MIDDLE_GEM).save(output);
		CrabPotFilterRecipeBuilder.bubblerCrab(ItemRegistry.SLUDGE_BALL, ItemRegistry.SAP_BALL).save(output);
//		CrabPotFilterRecipeBuilder.bubblerCrab(ItemRegistry.ROOT_POD, new ItemStack(BlockRegistry.ROOT, 4)).save(output);

		PurifierRecipeBuilder.purifying(Ingredient.of(BlockRegistry.MOSSY_CRAGROCK_TOP, BlockRegistry.MOSSY_CRAGROCK_BOTTOM), BlockRegistry.CRAGROCK).save(output);
		PurifierRecipeBuilder.purifying(BlockRegistry.CRIMSON_MIDDLE_GEM_ORE, ItemRegistry.CRIMSON_MIDDLE_GEM).save(output);
		PurifierRecipeBuilder.purifying(BlockRegistry.GREEN_MIDDLE_GEM_ORE, ItemRegistry.GREEN_MIDDLE_GEM).save(output);
		PurifierRecipeBuilder.purifying(BlockRegistry.AQUA_MIDDLE_GEM_ORE, ItemRegistry.AQUA_MIDDLE_GEM).save(output);
		PurifierRecipeBuilder.purifying(BlockRegistry.SWAMP_DIRT, BlockRegistry.PURIFIED_SWAMP_DIRT).save(output);
		PurifierRecipeBuilder.purifying(ItemRegistry.DIRTY_DENTROTHYST_VIAL, ItemRegistry.GREEN_DENTROTHYST_VIAL).save(output);
		PurifierRecipeBuilder.purifying(ItemRegistry.DIRTY_SILK_BUNDLE, ItemRegistry.SILK_BUNDLE).save(output);
		//PurifierRecipeBuilder.purifying(BLItemTagProvider.ITEM_FRAMES, ItemRegistry.ITEM_FRAME).save(output);
		PurifierRecipeBuilder.purifying(BLItemTagProvider.FILTERED_SILT_GLASS, BlockRegistry.FILTERED_SILT_GLASS).save(output);
		PurifierRecipeBuilder.purifying(BLItemTagProvider.MUD_BRICK_SHINGLES, BlockRegistry.MUD_BRICK_SHINGLES).save(output);
		//TODO weedwood rowboat and pouch custom recipes
	}
}
