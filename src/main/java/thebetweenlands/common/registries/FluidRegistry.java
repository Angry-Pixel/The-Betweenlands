package thebetweenlands.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

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

	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> DULL_LAVENDER_DYE_STILL = FLUIDS.register("dull_lavender_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.DULL_LAVENDER_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> DULL_LAVENDER_DYE_FLOW = FLUIDS.register("flowing_dull_lavender_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.DULL_LAVENDER_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MAROON_DYE_STILL = FLUIDS.register("maroon_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.MAROON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MAROON_DYE_FLOW = FLUIDS.register("flowing_maroon_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.MAROON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SHADOW_GREEN_DYE_STILL = FLUIDS.register("shadow_green_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.SHADOW_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SHADOW_GREEN_DYE_FLOW = FLUIDS.register("flowing_shadow_green_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SHADOW_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CAMELOT_MAGENTA_DYE_STILL = FLUIDS.register("camelot_magenta_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.CAMELOT_MAGENTA_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CAMELOT_MAGENTA_DYE_FLOW = FLUIDS.register("flowing_camelot_magenta_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CAMELOT_MAGENTA_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SAFFRON_DYE_STILL = FLUIDS.register("saffron_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.SAFFRON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SAFFRON_DYE_FLOW = FLUIDS.register("flowing_saffron_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SAFFRON_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CARIBBEAN_GREEN_DYE_STILL = FLUIDS.register("caribbean_green_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.CARIBBEAN_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CARIBBEAN_GREEN_DYE_FLOW = FLUIDS.register("flowing_caribbean_green_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CARIBBEAN_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> VIVID_TANGERINE_DYE_STILL = FLUIDS.register("vivid_tangerine_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.VIVID_TANGERINE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> VIVID_TANGERINE_DYE_FLOW = FLUIDS.register("flowing_vivid_tangerine_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.VIVID_TANGERINE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CHAMPAGNE_DYE_STILL = FLUIDS.register("champagne_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.CHAMPAGNE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CHAMPAGNE_DYE_FLOW = FLUIDS.register("flowing_champagne_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CHAMPAGNE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> RAISIN_BLACK_DYE_STILL = FLUIDS.register("raisin_black_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.RAISIN_BLACK_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> RAISIN_BLACK_DYE_FLOW = FLUIDS.register("flowing_raisin_black_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.RAISIN_BLACK_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SUSHI_GREEN_DYE_STILL = FLUIDS.register("sushi_green_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.SUSHI_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SUSHI_GREEN_DYE_FLOW = FLUIDS.register("flowing_sushi_green_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SUSHI_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> ELM_CYAN_DYE_STILL = FLUIDS.register("elm_cyan_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.ELM_CYAN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> ELM_CYAN_DYE_FLOW = FLUIDS.register("flowing_elm_cyan_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.ELM_CYAN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CADMIUM_GREEN_DYE_STILL = FLUIDS.register("cadmium_green_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.CADMIUM_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CADMIUM_GREEN_DYE_FLOW = FLUIDS.register("flowing_cadmium_green_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CADMIUM_GREEN_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LAVENDER_BLUE_DYE_STILL = FLUIDS.register("lavender_blue_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.LAVENDER_BLUE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LAVENDER_BLUE_DYE_FLOW = FLUIDS.register("flowing_lavender_blue_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.LAVENDER_BLUE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BROWN_RUST_DYE_STILL = FLUIDS.register("brown_rust_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.BROWN_RUST_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> BROWN_RUST_DYE_FLOW = FLUIDS.register("flowing_brown_rust_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.BROWN_RUST_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> MIDNIGHT_PURPLE_DYE_STILL = FLUIDS.register("midnight_purple_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.MIDNIGHT_PURPLE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> MIDNIGHT_PURPLE_DYE_FLOW = FLUIDS.register("flowing_midnight_purple_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.MIDNIGHT_PURPLE_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PEWTER_GREY_DYE_STILL = FLUIDS.register("pewter_grey_dye", () -> new BaseFlowingFluid.Source(FluidRegistry.PEWTER_GREY_DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> PEWTER_GREY_DYE_FLOW = FLUIDS.register("flowing_pewter_grey_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.PEWTER_GREY_DYE_PROPERTIES));

	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NETTLE_SOUP_STILL = FLUIDS.register("nettle_soup", () -> new BaseFlowingFluid.Source(FluidRegistry.NETTLE_SOUP_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> NETTLE_SOUP_FLOW = FLUIDS.register("flowing_nettle_soup", () -> new BaseFlowingFluid.Flowing(FluidRegistry.NETTLE_SOUP_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> NETTLE_TEA_STILL = FLUIDS.register("nettle_tea", () -> new BaseFlowingFluid.Source(FluidRegistry.NETTLE_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> NETTLE_TEA_FLOW = FLUIDS.register("flowing_nettle_tea", () -> new BaseFlowingFluid.Flowing(FluidRegistry.NETTLE_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PHEROMONE_EXTRACT_STILL = FLUIDS.register("pheromone_extract", () -> new BaseFlowingFluid.Source(FluidRegistry.PHEROMONE_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> PHEROMONE_EXTRACT_FLOW = FLUIDS.register("flowing_pheromone_extract", () -> new BaseFlowingFluid.Flowing(FluidRegistry.PHEROMONE_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SWAMP_BROTH_STILL = FLUIDS.register("swamp_broth", () -> new BaseFlowingFluid.Source(FluidRegistry.SWAMP_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SWAMP_BROTH_FLOW = FLUIDS.register("flowing_swamp_broth", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SWAMP_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> STURDY_STOCK_STILL = FLUIDS.register("sturdy_stock", () -> new BaseFlowingFluid.Source(FluidRegistry.STURDY_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> STURDY_STOCK_FLOW = FLUIDS.register("flowing_sturdy_stock", () -> new BaseFlowingFluid.Flowing(FluidRegistry.STURDY_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> PEAR_CORDIAL_STILL = FLUIDS.register("pear_cordial", () -> new BaseFlowingFluid.Source(FluidRegistry.PEAR_CORDIAL_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> PEAR_CORDIAL_FLOW = FLUIDS.register("flowing_pear_cordial", () -> new BaseFlowingFluid.Flowing(FluidRegistry.PEAR_CORDIAL_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SHAMANS_BREW_STILL = FLUIDS.register("shamans_brew", () -> new BaseFlowingFluid.Source(FluidRegistry.SHAMANS_BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SHAMANS_BREW_FLOW = FLUIDS.register("flowing_shamans_brew", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SHAMANS_BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> LAKE_BROTH_STILL = FLUIDS.register("lake_broth", () -> new BaseFlowingFluid.Source(FluidRegistry.LAKE_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> LAKE_BROTH_FLOW = FLUIDS.register("flowing_lake_broth", () -> new BaseFlowingFluid.Flowing(FluidRegistry.LAKE_BROTH_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> SHELL_STOCK_STILL = FLUIDS.register("shell_stock", () -> new BaseFlowingFluid.Source(FluidRegistry.SHELL_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> SHELL_STOCK_FLOW = FLUIDS.register("flowing_shell_stock", () -> new BaseFlowingFluid.Flowing(FluidRegistry.SHELL_STOCK_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FROG_LEG_EXTRACT_STILL = FLUIDS.register("frog_leg_extract", () -> new BaseFlowingFluid.Source(FluidRegistry.FROG_LEG_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FROG_LEG_EXTRACT_FLOW = FLUIDS.register("flowing_frog_leg_extract", () -> new BaseFlowingFluid.Flowing(FluidRegistry.FROG_LEG_EXTRACT_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> WITCH_TEA_STILL = FLUIDS.register("witch_tea", () -> new BaseFlowingFluid.Source(FluidRegistry.WITCH_TEA_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> WITCH_TEA_FLOW = FLUIDS.register("flowing_witch_tea", () -> new BaseFlowingFluid.Flowing(FluidRegistry.WITCH_TEA_PROPERTIES));

	public static final BaseFlowingFluid.Properties SWAMP_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SWAMP_WATER, SWAMP_WATER_STILL, SWAMP_WATER_FLOW).block(BlockRegistry.SWAMP_WATER).bucket(ItemRegistry.SWAMP_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties STAGNANT_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.STAGNANT_WATER, STAGNANT_WATER_STILL, STAGNANT_WATER_FLOW).block(BlockRegistry.STAGNANT_WATER).bucket(ItemRegistry.STAGNANT_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties TAR_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.TAR, TAR_STILL, TAR_FLOW).block(BlockRegistry.TAR).bucket(ItemRegistry.TAR_BUCKET);
	public static final BaseFlowingFluid.Properties RUBBER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.RUBBER, RUBBER_STILL, RUBBER_FLOW).block(BlockRegistry.RUBBER).bucket(ItemRegistry.RUBBER_BUCKET);
	public static final BaseFlowingFluid.Properties FOG_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FOG, FOG_STILL, FOG_FLOW);
	public static final BaseFlowingFluid.Properties SHALLOWBREATH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHALLOWBREATH, SHALLOWBREATH_STILL, SHALLOWBREATH_FLOW);
	public static final BaseFlowingFluid.Properties CLEAN_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CLEAN_WATER, CLEAN_WATER_STILL, CLEAN_WATER_FLOW).bucket(ItemRegistry.CLEAN_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties FISH_OIL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FISH_OIL, FISH_OIL_STILL, FISH_OIL_FLOW).bucket(ItemRegistry.FISH_OIL_BUCKET);

	public static final BaseFlowingFluid.Properties DULL_LAVENDER_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DULL_LAVENDER_DYE, DULL_LAVENDER_DYE_STILL, DULL_LAVENDER_DYE_FLOW).bucket(ItemRegistry.DULL_LAVENDER_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties MAROON_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.MAROON_DYE, MAROON_DYE_STILL, MAROON_DYE_FLOW).bucket(ItemRegistry.MAROON_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SHADOW_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHADOW_GREEN_DYE, SHADOW_GREEN_DYE_STILL, SHADOW_GREEN_DYE_FLOW).bucket(ItemRegistry.SHADOW_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CAMELOT_MAGENTA_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CAMELOT_MAGENTA_DYE, CAMELOT_MAGENTA_DYE_STILL, CAMELOT_MAGENTA_DYE_FLOW).bucket(ItemRegistry.CAMELOT_MAGENTA_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SAFFRON_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SAFFRON_DYE, SAFFRON_DYE_STILL, SAFFRON_DYE_FLOW).bucket(ItemRegistry.SAFFRON_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CARIBBEAN_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CARIBBEAN_GREEN_DYE, CARIBBEAN_GREEN_DYE_STILL, CARIBBEAN_GREEN_DYE_FLOW).bucket(ItemRegistry.CARIBBEAN_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties VIVID_TANGERINE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.VIVID_TANGERINE_DYE, VIVID_TANGERINE_DYE_STILL, VIVID_TANGERINE_DYE_FLOW).bucket(ItemRegistry.VIVID_TANGERINE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CHAMPAGNE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CHAMPAGNE_DYE, CHAMPAGNE_DYE_STILL, CHAMPAGNE_DYE_FLOW).bucket(ItemRegistry.CHAMPAGNE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties RAISIN_BLACK_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.RAISIN_BLACK_DYE, RAISIN_BLACK_DYE_STILL, RAISIN_BLACK_DYE_FLOW).bucket(ItemRegistry.RAISIN_BLACK_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties SUSHI_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SUSHI_GREEN_DYE, SUSHI_GREEN_DYE_STILL, SUSHI_GREEN_DYE_FLOW).bucket(ItemRegistry.SUSHI_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties ELM_CYAN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.ELM_CYAN_DYE, ELM_CYAN_DYE_STILL, ELM_CYAN_DYE_FLOW).bucket(ItemRegistry.ELM_CYAN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CADMIUM_GREEN_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CADMIUM_GREEN_DYE, CADMIUM_GREEN_DYE_STILL, CADMIUM_GREEN_DYE_FLOW).bucket(ItemRegistry.CADMIUM_GREEN_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties LAVENDER_BLUE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.LAVENDER_BLUE_DYE, LAVENDER_BLUE_DYE_STILL, LAVENDER_BLUE_DYE_FLOW).bucket(ItemRegistry.LAVENDER_BLUE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties BROWN_RUST_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BROWN_RUST_DYE, BROWN_RUST_DYE_STILL, BROWN_RUST_DYE_FLOW).bucket(ItemRegistry.BROWN_RUST_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties MIDNIGHT_PURPLE_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.MIDNIGHT_PURPLE_DYE, MIDNIGHT_PURPLE_DYE_STILL, MIDNIGHT_PURPLE_DYE_FLOW).bucket(ItemRegistry.MIDNIGHT_PURPLE_DYE_BUCKET);
	public static final BaseFlowingFluid.Properties PEWTER_GREY_DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.PEWTER_GREY_DYE, PEWTER_GREY_DYE_STILL, PEWTER_GREY_DYE_FLOW).bucket(ItemRegistry.PEWTER_GREY_DYE_BUCKET);

	public static final BaseFlowingFluid.Properties NETTLE_SOUP_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.NETTLE_SOUP, NETTLE_SOUP_STILL, NETTLE_SOUP_FLOW).bucket(ItemRegistry.NETTLE_SOUP_BUCKET);
	public static final BaseFlowingFluid.Properties NETTLE_TEA_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.NETTLE_TEA, NETTLE_TEA_STILL, NETTLE_TEA_FLOW).bucket(ItemRegistry.NETTLE_TEA_BUCKET);
	public static final BaseFlowingFluid.Properties PHEROMONE_EXTRACT_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.PHEROMONE_EXTRACT, PHEROMONE_EXTRACT_STILL, PHEROMONE_EXTRACT_FLOW).bucket(ItemRegistry.PHEROMONE_EXTRACT_BUCKET);
	public static final BaseFlowingFluid.Properties SWAMP_BROTH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SWAMP_BROTH, SWAMP_BROTH_STILL, SWAMP_BROTH_FLOW).bucket(ItemRegistry.SWAMP_BROTH_BUCKET);
	public static final BaseFlowingFluid.Properties STURDY_STOCK_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.STURDY_STOCK, STURDY_STOCK_STILL, STURDY_STOCK_FLOW).bucket(ItemRegistry.STURDY_STOCK_BUCKET);
	public static final BaseFlowingFluid.Properties PEAR_CORDIAL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.PEAR_CORDIAL, PEAR_CORDIAL_STILL, PEAR_CORDIAL_FLOW).bucket(ItemRegistry.PEAR_CORDIAL_BUCKET);
	public static final BaseFlowingFluid.Properties SHAMANS_BREW_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHAMANS_BREW, SHAMANS_BREW_STILL, SHAMANS_BREW_FLOW).bucket(ItemRegistry.SHAMANS_BREW_BUCKET);
	public static final BaseFlowingFluid.Properties LAKE_BROTH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.LAKE_BROTH, LAKE_BROTH_STILL, LAKE_BROTH_FLOW).bucket(ItemRegistry.LAKE_BROTH_BUCKET);
	public static final BaseFlowingFluid.Properties SHELL_STOCK_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHELL_STOCK, SHELL_STOCK_STILL, SHELL_STOCK_FLOW).bucket(ItemRegistry.SHELL_STOCK_BUCKET);
	public static final BaseFlowingFluid.Properties FROG_LEG_EXTRACT_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FROG_LEG_EXTRACT, FROG_LEG_EXTRACT_STILL, FROG_LEG_EXTRACT_FLOW).bucket(ItemRegistry.FROG_LEG_EXTRACT_BUCKET);
	public static final BaseFlowingFluid.Properties WITCH_TEA_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.WITCH_TEA, WITCH_TEA_STILL, WITCH_TEA_FLOW).bucket(ItemRegistry.WITCH_TEA_BUCKET);


}
