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
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> DYE_STILL = FLUIDS.register("dye", () -> new BaseFlowingFluid.Source(FluidRegistry.DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> DYE_FLOW = FLUIDS.register("flowing_dye", () -> new BaseFlowingFluid.Flowing(FluidRegistry.DYE_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CLEAN_WATER_STILL = FLUIDS.register("clean_water", () -> new BaseFlowingFluid.Source(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CLEAN_WATER_FLOW = FLUIDS.register("flowing_clean_water", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FISH_OIL_STILL = FLUIDS.register("fish_oil", () -> new BaseFlowingFluid.Source(FluidRegistry.FISH_OIL_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FISH_OIL_FLOW = FLUIDS.register("flowing_fish_oil", () -> new BaseFlowingFluid.Flowing(FluidRegistry.FISH_OIL_PROPERTIES));

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
	public static final BaseFlowingFluid.Properties DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, DYE_STILL, DYE_FLOW).bucket(ItemRegistry.DYE_BUCKET);
	public static final BaseFlowingFluid.Properties CLEAN_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CLEAN_WATER, CLEAN_WATER_STILL, CLEAN_WATER_FLOW).bucket(ItemRegistry.CLEAN_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties FISH_OIL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FISH_OIL, FISH_OIL_STILL, FISH_OIL_FLOW).bucket(ItemRegistry.FISH_OIL_BUCKET);

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
