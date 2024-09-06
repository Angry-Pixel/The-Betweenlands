package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.fluid.ColoredFluid;
import thebetweenlands.util.BLDyeColor;
import thebetweenlands.util.DrinkableBrew;

public class FluidRegistry {

	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, TheBetweenlands.ID);

	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SWAMP_WATER_STILL = FLUIDS.register("swamp_water", () -> new BaseFlowingFluid.Source(FluidRegistry.SWAMP_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SWAMP_WATER_FLOW = FLUIDS.register("flowing_swamp_water", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SWAMP_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> STAGNANT_WATER_STILL = FLUIDS.register("stagnant_water", () -> new BaseFlowingFluid.Source(FluidRegistry.STAGNANT_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> STAGNANT_WATER_FLOW = FLUIDS.register("flowing_stagnant_water", () -> new BaseFlowingFluid.Flowing(FluidRegistry.STAGNANT_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> TAR_STILL = FLUIDS.register("tar", () -> new BaseFlowingFluid.Source(FluidRegistry.TAR_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> TAR_FLOW = FLUIDS.register("flowing_tar", () -> new BaseFlowingFluid.Flowing(FluidRegistry.TAR_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> RUBBER_STILL = FLUIDS.register("rubber", () -> new BaseFlowingFluid.Source(FluidRegistry.RUBBER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> RUBBER_FLOW = FLUIDS.register("flowing_rubber", () -> new BaseFlowingFluid.Flowing(FluidRegistry.RUBBER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FOG_STILL = FLUIDS.register("fog", () -> new BaseFlowingFluid.Source(FluidRegistry.FOG_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FOG_FLOW = FLUIDS.register("flowing_fog", () -> new BaseFlowingFluid.Flowing(FluidRegistry.FOG_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SHALLOWBREATH_STILL = FLUIDS.register("shallowbreath", () -> new BaseFlowingFluid.Source(FluidRegistry.SHALLOWBREATH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SHALLOWBREATH_FLOW = FLUIDS.register("flowing_shallowbreath", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SHALLOWBREATH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CLEAN_WATER_STILL = FLUIDS.register("clean_water", () -> new BaseFlowingFluid.Source(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CLEAN_WATER_FLOW = FLUIDS.register("flowing_clean_water", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FISH_OIL_STILL = FLUIDS.register("fish_oil", () -> new BaseFlowingFluid.Source(FluidRegistry.FISH_OIL_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FISH_OIL_FLOW = FLUIDS.register("flowing_fish_oil", () -> new BaseFlowingFluid.Flowing(FluidRegistry.FISH_OIL_PROPERTIES));

	public static final DeferredHolder<Fluid, ColoredFluid.Source> DULL_LAVENDER_DYE_STILL = FLUIDS.register("dull_lavender_dye", () -> new ColoredFluid.Source(BLDyeColor.DULL_LAVENDER.getColorValue(), FluidRegistry.DULL_LAVENDER_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> DULL_LAVENDER_DYE_FLOW = FLUIDS.register("flowing_dull_lavender_dye", () -> new ColoredFluid.Flowing(BLDyeColor.DULL_LAVENDER.getColorValue(), FluidRegistry.DULL_LAVENDER_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> MAROON_DYE_STILL = FLUIDS.register("maroon_dye", () -> new ColoredFluid.Source(BLDyeColor.MAROON.getColorValue(), FluidRegistry.MAROON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> MAROON_DYE_FLOW = FLUIDS.register("flowing_maroon_dye", () -> new ColoredFluid.Flowing(BLDyeColor.MAROON.getColorValue(), FluidRegistry.MAROON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SHADOW_GREEN_DYE_STILL = FLUIDS.register("shadow_green_dye", () -> new ColoredFluid.Source(BLDyeColor.SHADOW_GREEN.getColorValue(), FluidRegistry.SHADOW_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SHADOW_GREEN_DYE_FLOW = FLUIDS.register("flowing_shadow_green_dye", () -> new ColoredFluid.Flowing(BLDyeColor.SHADOW_GREEN.getColorValue(), FluidRegistry.SHADOW_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> CAMELOT_MAGENTA_DYE_STILL = FLUIDS.register("camelot_magenta_dye", () -> new ColoredFluid.Source(BLDyeColor.CAMELOT_MAGENTA.getColorValue(), FluidRegistry.CAMELOT_MAGENTA_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> CAMELOT_MAGENTA_DYE_FLOW = FLUIDS.register("flowing_camelot_magenta_dye", () -> new ColoredFluid.Flowing(BLDyeColor.CAMELOT_MAGENTA.getColorValue(), FluidRegistry.CAMELOT_MAGENTA_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SAFFRON_DYE_STILL = FLUIDS.register("saffron_dye", () -> new ColoredFluid.Source(BLDyeColor.SAFFRON.getColorValue(), FluidRegistry.SAFFRON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SAFFRON_DYE_FLOW = FLUIDS.register("flowing_saffron_dye", () -> new ColoredFluid.Flowing(BLDyeColor.SAFFRON.getColorValue(), FluidRegistry.SAFFRON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> CARIBBEAN_GREEN_DYE_STILL = FLUIDS.register("caribbean_green_dye", () -> new ColoredFluid.Source(BLDyeColor.CARIBBEAN_GREEN.getColorValue(), FluidRegistry.CARIBBEAN_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> CARIBBEAN_GREEN_DYE_FLOW = FLUIDS.register("flowing_caribbean_green_dye", () -> new ColoredFluid.Flowing(BLDyeColor.CARIBBEAN_GREEN.getColorValue(), FluidRegistry.CARIBBEAN_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> VIVID_TANGERINE_DYE_STILL = FLUIDS.register("vivid_tangerine_dye", () -> new ColoredFluid.Source(BLDyeColor.VIVID_TANGERINE.getColorValue(), FluidRegistry.VIVID_TANGERINE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> VIVID_TANGERINE_DYE_FLOW = FLUIDS.register("flowing_vivid_tangerine_dye", () -> new ColoredFluid.Flowing(BLDyeColor.VIVID_TANGERINE.getColorValue(), FluidRegistry.VIVID_TANGERINE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> CHAMPAGNE_DYE_STILL = FLUIDS.register("champagne_dye", () -> new ColoredFluid.Source(BLDyeColor.CHAMPAGNE.getColorValue(), FluidRegistry.CHAMPAGNE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> CHAMPAGNE_DYE_FLOW = FLUIDS.register("flowing_champagne_dye", () -> new ColoredFluid.Flowing(BLDyeColor.CHAMPAGNE.getColorValue(), FluidRegistry.CHAMPAGNE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> RAISIN_BLACK_DYE_STILL = FLUIDS.register("raisin_black_dye", () -> new ColoredFluid.Source(BLDyeColor.RAISIN_BLACK.getColorValue(), FluidRegistry.RAISIN_BLACK_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> RAISIN_BLACK_DYE_FLOW = FLUIDS.register("flowing_raisin_black_dye", () -> new ColoredFluid.Flowing(BLDyeColor.RAISIN_BLACK.getColorValue(), FluidRegistry.RAISIN_BLACK_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SUSHI_GREEN_DYE_STILL = FLUIDS.register("sushi_green_dye", () -> new ColoredFluid.Source(BLDyeColor.SUSHI_GREEN.getColorValue(), FluidRegistry.SUSHI_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SUSHI_GREEN_DYE_FLOW = FLUIDS.register("flowing_sushi_green_dye", () -> new ColoredFluid.Flowing(BLDyeColor.SUSHI_GREEN.getColorValue(), FluidRegistry.SUSHI_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> ELM_CYAN_DYE_STILL = FLUIDS.register("elm_cyan_dye", () -> new ColoredFluid.Source(BLDyeColor.ELM_CYAN.getColorValue(), FluidRegistry.ELM_CYAN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> ELM_CYAN_DYE_FLOW = FLUIDS.register("flowing_elm_cyan_dye", () -> new ColoredFluid.Flowing(BLDyeColor.ELM_CYAN.getColorValue(), FluidRegistry.ELM_CYAN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> CADMIUM_GREEN_DYE_STILL = FLUIDS.register("cadmium_green_dye", () -> new ColoredFluid.Source(BLDyeColor.CADMIUM_GREEN.getColorValue(), FluidRegistry.CADMIUM_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> CADMIUM_GREEN_DYE_FLOW = FLUIDS.register("flowing_cadmium_green_dye", () -> new ColoredFluid.Flowing(BLDyeColor.CADMIUM_GREEN.getColorValue(), FluidRegistry.CADMIUM_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> LAVENDER_BLUE_DYE_STILL = FLUIDS.register("lavender_blue_dye", () -> new ColoredFluid.Source(BLDyeColor.LAVENDER_BLUE.getColorValue(), FluidRegistry.LAVENDER_BLUE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> LAVENDER_BLUE_DYE_FLOW = FLUIDS.register("flowing_lavender_blue_dye", () -> new ColoredFluid.Flowing(BLDyeColor.LAVENDER_BLUE.getColorValue(), FluidRegistry.LAVENDER_BLUE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> BROWN_RUST_DYE_STILL = FLUIDS.register("brown_rust_dye", () -> new ColoredFluid.Source(BLDyeColor.BROWN_RUST.getColorValue(), FluidRegistry.BROWN_RUST_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> BROWN_RUST_DYE_FLOW = FLUIDS.register("flowing_brown_rust_dye", () -> new ColoredFluid.Flowing(BLDyeColor.BROWN_RUST.getColorValue(), FluidRegistry.BROWN_RUST_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> MIDNIGHT_PURPLE_DYE_STILL = FLUIDS.register("midnight_purple_dye", () -> new ColoredFluid.Source(BLDyeColor.MIDNIGHT_PURPLE.getColorValue(), FluidRegistry.MIDNIGHT_PURPLE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> MIDNIGHT_PURPLE_DYE_FLOW = FLUIDS.register("flowing_midnight_purple_dye", () -> new ColoredFluid.Flowing(BLDyeColor.MIDNIGHT_PURPLE.getColorValue(), FluidRegistry.MIDNIGHT_PURPLE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> PEWTER_GREY_DYE_STILL = FLUIDS.register("pewter_grey_dye", () -> new ColoredFluid.Source(BLDyeColor.PEWTER_GREY.getColorValue(), FluidRegistry.PEWTER_GREY_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> PEWTER_GREY_DYE_FLOW = FLUIDS.register("flowing_pewter_grey_dye", () -> new ColoredFluid.Flowing(BLDyeColor.PEWTER_GREY.getColorValue(), FluidRegistry.PEWTER_GREY_DYE_PROPERTIES));

	public static final DeferredHolder<Fluid, ColoredFluid.Source> NETTLE_SOUP_STILL = FLUIDS.register("nettle_soup", () -> new ColoredFluid.Source(DrinkableBrew.NETTLE_SOUP.getColorValue(), FluidRegistry.NETTLE_SOUP_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> NETTLE_SOUP_FLOW = FLUIDS.register("flowing_nettle_soup", () -> new ColoredFluid.Flowing(DrinkableBrew.NETTLE_SOUP.getColorValue(), FluidRegistry.NETTLE_SOUP_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> NETTLE_TEA_STILL = FLUIDS.register("nettle_tea", () -> new ColoredFluid.Source(DrinkableBrew.NETTLE_TEA.getColorValue(), FluidRegistry.NETTLE_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> NETTLE_TEA_FLOW = FLUIDS.register("flowing_nettle_tea", () -> new ColoredFluid.Flowing(DrinkableBrew.NETTLE_TEA.getColorValue(), FluidRegistry.NETTLE_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> PHEROMONE_EXTRACT_STILL = FLUIDS.register("pheromone_extract", () -> new ColoredFluid.Source(DrinkableBrew.PHEROMONE_EXTRACT.getColorValue(), FluidRegistry.PHEROMONE_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> PHEROMONE_EXTRACT_FLOW = FLUIDS.register("flowing_pheromone_extract", () -> new ColoredFluid.Flowing(DrinkableBrew.PHEROMONE_EXTRACT.getColorValue(), FluidRegistry.PHEROMONE_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SWAMP_BROTH_STILL = FLUIDS.register("swamp_broth", () -> new ColoredFluid.Source(DrinkableBrew.SWAMP_BROTH.getColorValue(), FluidRegistry.SWAMP_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SWAMP_BROTH_FLOW = FLUIDS.register("flowing_swamp_broth", () -> new ColoredFluid.Flowing(DrinkableBrew.SWAMP_BROTH.getColorValue(), FluidRegistry.SWAMP_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> STURDY_STOCK_STILL = FLUIDS.register("sturdy_stock", () -> new ColoredFluid.Source(DrinkableBrew.STURDY_STOCK.getColorValue(), FluidRegistry.STURDY_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> STURDY_STOCK_FLOW = FLUIDS.register("flowing_sturdy_stock", () -> new ColoredFluid.Flowing(DrinkableBrew.STURDY_STOCK.getColorValue(), FluidRegistry.STURDY_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> PEAR_CORDIAL_STILL = FLUIDS.register("pear_cordial", () -> new ColoredFluid.Source(DrinkableBrew.PEAR_CORDIAL.getColorValue(), FluidRegistry.PEAR_CORDIAL_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> PEAR_CORDIAL_FLOW = FLUIDS.register("flowing_pear_cordial", () -> new ColoredFluid.Flowing(DrinkableBrew.PEAR_CORDIAL.getColorValue(), FluidRegistry.PEAR_CORDIAL_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SHAMANS_BREW_STILL = FLUIDS.register("shamans_brew", () -> new ColoredFluid.Source(DrinkableBrew.SHAMANS_BREW.getColorValue(), FluidRegistry.SHAMANS_BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SHAMANS_BREW_FLOW = FLUIDS.register("flowing_shamans_brew", () -> new ColoredFluid.Flowing(DrinkableBrew.SHAMANS_BREW.getColorValue(), FluidRegistry.SHAMANS_BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> LAKE_BROTH_STILL = FLUIDS.register("lake_broth", () -> new ColoredFluid.Source(DrinkableBrew.LAKE_BROTH.getColorValue(), FluidRegistry.LAKE_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> LAKE_BROTH_FLOW = FLUIDS.register("flowing_lake_broth", () -> new ColoredFluid.Flowing(DrinkableBrew.LAKE_BROTH.getColorValue(), FluidRegistry.LAKE_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> SHELL_STOCK_STILL = FLUIDS.register("shell_stock", () -> new ColoredFluid.Source(DrinkableBrew.SHELL_STOCK.getColorValue(), FluidRegistry.SHELL_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> SHELL_STOCK_FLOW = FLUIDS.register("flowing_shell_stock", () -> new ColoredFluid.Flowing(DrinkableBrew.SHELL_STOCK.getColorValue(), FluidRegistry.SHELL_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> FROG_LEG_EXTRACT_STILL = FLUIDS.register("frog_leg_extract", () -> new ColoredFluid.Source(DrinkableBrew.FROG_LEG_EXTRACT.getColorValue(), FluidRegistry.FROG_LEG_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> FROG_LEG_EXTRACT_FLOW = FLUIDS.register("flowing_frog_leg_extract", () -> new ColoredFluid.Flowing(DrinkableBrew.FROG_LEG_EXTRACT.getColorValue(), FluidRegistry.FROG_LEG_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Source> WITCH_TEA_STILL = FLUIDS.register("witch_tea", () -> new ColoredFluid.Source(DrinkableBrew.WITCH_TEA.getColorValue(), FluidRegistry.WITCH_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, ColoredFluid.Flowing> WITCH_TEA_FLOW = FLUIDS.register("flowing_witch_tea", () -> new ColoredFluid.Flowing(DrinkableBrew.WITCH_TEA.getColorValue(), FluidRegistry.WITCH_TEA_PROPERTIES));

	public static final BaseFlowingFluid.Properties SWAMP_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SWAMP_WATER, SWAMP_WATER_STILL, SWAMP_WATER_FLOW).block(BlockRegistry.SWAMP_WATER).bucket(ItemRegistry.SWAMP_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties STAGNANT_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.STAGNANT_WATER, STAGNANT_WATER_STILL, STAGNANT_WATER_FLOW).block(BlockRegistry.STAGNANT_WATER).bucket(ItemRegistry.STAGNANT_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties TAR_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.TAR, TAR_STILL, TAR_FLOW).block(BlockRegistry.TAR).bucket(ItemRegistry.TAR_BUCKET);
	public static final BaseFlowingFluid.Properties RUBBER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.RUBBER, RUBBER_STILL, RUBBER_FLOW).block(BlockRegistry.RUBBER).bucket(ItemRegistry.RUBBER_BUCKET);
	public static final BaseFlowingFluid.Properties FOG_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FOG, FOG_STILL, FOG_FLOW);
	public static final BaseFlowingFluid.Properties SHALLOWBREATH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHALLOWBREATH, SHALLOWBREATH_STILL, SHALLOWBREATH_FLOW);
	public static final BaseFlowingFluid.Properties CLEAN_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CLEAN_WATER, CLEAN_WATER_STILL, CLEAN_WATER_FLOW).bucket(ItemRegistry.CLEAN_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties FISH_OIL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FISH_OIL, FISH_OIL_STILL, FISH_OIL_FLOW).bucket(ItemRegistry.FISH_OIL_BUCKET);

	public static final BaseFlowingFluid.Properties DULL_LAVENDER_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, DULL_LAVENDER_DYE_STILL, DULL_LAVENDER_DYE_FLOW).bucket(ItemRegistry.DULL_LAVENDER_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties MAROON_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, MAROON_DYE_STILL, MAROON_DYE_FLOW).bucket(ItemRegistry.MAROON_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SHADOW_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, SHADOW_GREEN_DYE_STILL, SHADOW_GREEN_DYE_FLOW).bucket(ItemRegistry.SHADOW_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CAMELOT_MAGENTA_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, CAMELOT_MAGENTA_DYE_STILL, CAMELOT_MAGENTA_DYE_FLOW).bucket(ItemRegistry.CAMELOT_MAGENTA_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SAFFRON_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, SAFFRON_DYE_STILL, SAFFRON_DYE_FLOW).bucket(ItemRegistry.SAFFRON_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CARIBBEAN_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, CARIBBEAN_GREEN_DYE_STILL, CARIBBEAN_GREEN_DYE_FLOW).bucket(ItemRegistry.CARIBBEAN_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties VIVID_TANGERINE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, VIVID_TANGERINE_DYE_STILL, VIVID_TANGERINE_DYE_FLOW).bucket(ItemRegistry.VIVID_TANGERINE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CHAMPAGNE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, CHAMPAGNE_DYE_STILL, CHAMPAGNE_DYE_FLOW).bucket(ItemRegistry.CHAMPAGNE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties RAISIN_BLACK_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, RAISIN_BLACK_DYE_STILL, RAISIN_BLACK_DYE_FLOW).bucket(ItemRegistry.RAISIN_BLACK_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SUSHI_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, SUSHI_GREEN_DYE_STILL, SUSHI_GREEN_DYE_FLOW).bucket(ItemRegistry.SUSHI_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties ELM_CYAN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, ELM_CYAN_DYE_STILL, ELM_CYAN_DYE_FLOW).bucket(ItemRegistry.ELM_CYAN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CADMIUM_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, CADMIUM_GREEN_DYE_STILL, CADMIUM_GREEN_DYE_FLOW).bucket(ItemRegistry.CADMIUM_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties LAVENDER_BLUE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, LAVENDER_BLUE_DYE_STILL, LAVENDER_BLUE_DYE_FLOW).bucket(ItemRegistry.LAVENDER_BLUE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties BROWN_RUST_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, BROWN_RUST_DYE_STILL, BROWN_RUST_DYE_FLOW).bucket(ItemRegistry.BROWN_RUST_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties MIDNIGHT_PURPLE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, MIDNIGHT_PURPLE_DYE_STILL, MIDNIGHT_PURPLE_DYE_FLOW).bucket(ItemRegistry.MIDNIGHT_PURPLE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties PEWTER_GREY_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, PEWTER_GREY_DYE_STILL, PEWTER_GREY_DYE_FLOW).bucket(ItemRegistry.PEWTER_GREY_DYE_BUCKET);

	public static final BaseFlowingFluid.Properties NETTLE_SOUP_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, NETTLE_SOUP_STILL, NETTLE_SOUP_FLOW).bucket(ItemRegistry.NETTLE_SOUP_BUCKET);
	public static final BaseFlowingFluid.Properties NETTLE_TEA_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, NETTLE_TEA_STILL, NETTLE_TEA_FLOW).bucket(ItemRegistry.NETTLE_TEA_BUCKET);
	public static final BaseFlowingFluid.Properties PHEROMONE_EXTRACT_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, PHEROMONE_EXTRACT_STILL, PHEROMONE_EXTRACT_FLOW).bucket(ItemRegistry.PHEROMONE_EXTREACT_BUCKET);
	public static final BaseFlowingFluid.Properties SWAMP_BROTH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, SWAMP_BROTH_STILL, SWAMP_BROTH_FLOW).bucket(ItemRegistry.SWAMP_BROTH_BUCKET);
	public static final BaseFlowingFluid.Properties STURDY_STOCK_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, STURDY_STOCK_STILL, STURDY_STOCK_FLOW).bucket(ItemRegistry.STURDY_STOCK_BUCKET);
	public static final BaseFlowingFluid.Properties PEAR_CORDIAL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, PEAR_CORDIAL_STILL, PEAR_CORDIAL_FLOW).bucket(ItemRegistry.PEAR_CORDIAL_BUCKET);
	public static final BaseFlowingFluid.Properties SHAMANS_BREW_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, SHAMANS_BREW_STILL, SHAMANS_BREW_FLOW).bucket(ItemRegistry.SHAMANS_BREW_BUCKET);
	public static final BaseFlowingFluid.Properties LAKE_BROTH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, LAKE_BROTH_STILL, LAKE_BROTH_FLOW).bucket(ItemRegistry.LAKE_BROTH_BUCKET);
	public static final BaseFlowingFluid.Properties SHELL_STOCK_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, SHELL_STOCK_STILL, SHELL_STOCK_FLOW).bucket(ItemRegistry.SHELL_STOCK_BUCKET);
	public static final BaseFlowingFluid.Properties FROG_LEG_EXTRACT_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, FROG_LEG_EXTRACT_STILL, FROG_LEG_EXTRACT_FLOW).bucket(ItemRegistry.FROG_LEG_EXTRACT_BUCKET);
	public static final BaseFlowingFluid.Properties WITCH_TEA_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, WITCH_TEA_STILL, WITCH_TEA_FLOW).bucket(ItemRegistry.WITCH_TEA_BUCKET);


}
