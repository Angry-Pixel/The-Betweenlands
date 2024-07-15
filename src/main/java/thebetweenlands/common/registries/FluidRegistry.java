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
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> BREW_STILL = FLUIDS.register("brew", () -> new BaseFlowingFluid.Source(FluidRegistry.BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> BREW_FLOW = FLUIDS.register("flowing_brew", () -> new BaseFlowingFluid.Flowing(FluidRegistry.BREW_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> CLEAN_WATER_STILL = FLUIDS.register("clean_water", () -> new BaseFlowingFluid.Source(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> CLEAN_WATER_FLOW = FLUIDS.register("flowing_clean_water", () -> new BaseFlowingFluid.Flowing(FluidRegistry.CLEAN_WATER_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Source> FISH_OIL_STILL = FLUIDS.register("fish_oil", () -> new BaseFlowingFluid.Source(FluidRegistry.FISH_OIL_PROPERTIES));
	public static final DeferredHolder<Fluid, BaseFlowingFluid.Flowing> FISH_OIL_FLOW = FLUIDS.register("flowing_fish_oil", () -> new BaseFlowingFluid.Flowing(FluidRegistry.FISH_OIL_PROPERTIES));

	public static final BaseFlowingFluid.Properties SWAMP_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SWAMP_WATER, SWAMP_WATER_STILL, SWAMP_WATER_FLOW).block(BlockRegistry.SWAMP_WATER).bucket(ItemRegistry.SWAMP_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties STAGNANT_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.STAGNANT_WATER, STAGNANT_WATER_STILL, STAGNANT_WATER_FLOW).bucket(ItemRegistry.STAGNANT_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties TAR_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.TAR, TAR_STILL, TAR_FLOW).bucket(ItemRegistry.TAR_BUCKET);
	public static final BaseFlowingFluid.Properties RUBBER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.RUBBER, RUBBER_STILL, RUBBER_FLOW).bucket(ItemRegistry.RUBBER_BUCKET);
	public static final BaseFlowingFluid.Properties FOG_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FOG, FOG_STILL, FOG_FLOW);
	public static final BaseFlowingFluid.Properties SHALLOWBREATH_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.SHALLOWBREATH, SHALLOWBREATH_STILL, SHALLOWBREATH_FLOW);
	public static final BaseFlowingFluid.Properties DYE_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.DYE, DYE_STILL, DYE_FLOW).bucket(ItemRegistry.DYE_BUCKET);
	public static final BaseFlowingFluid.Properties BREW_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.BREW, BREW_STILL, BREW_FLOW).bucket(ItemRegistry.BREW_BUCKET);
	public static final BaseFlowingFluid.Properties CLEAN_WATER_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.CLEAN_WATER, CLEAN_WATER_STILL, CLEAN_WATER_FLOW).bucket(ItemRegistry.CLEAN_WATER_BUCKET);
	public static final BaseFlowingFluid.Properties FISH_OIL_PROPERTIES = new BaseFlowingFluid.Properties(FluidTypeRegistry.FISH_OIL, FISH_OIL_STILL, FISH_OIL_FLOW).bucket(ItemRegistry.FISH_OIL_BUCKET);

}
