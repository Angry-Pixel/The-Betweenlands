package thebetweenlands.common.registries;

import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.fluid.BetweenlandsFluidType;

//TODO fluids have a LOT more flexibility nowadays. Perhaps we should look into making our fluids more unique
public class FluidTypeRegistry {
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<FluidType, FluidType> SWAMP_WATER = FLUID_TYPES.register("swamp_water", () -> new BetweenlandsFluidType("swamp_water", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.swamp_water")
		.fallDistanceModifier(0.0F)
		.canExtinguish(true)
		.canConvertToSource(true)
		.supportsBoating(true)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		.canHydrate(true)));

	public static final DeferredHolder<FluidType, FluidType> STAGNANT_WATER = FLUID_TYPES.register("stagnant_water", () -> new BetweenlandsFluidType("stagnant_water", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.stagnant_water")
		.fallDistanceModifier(0.0F)
		.canExtinguish(true)
		.canConvertToSource(true)
		.supportsBoating(true)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		.canHydrate(true)));

	public static final DeferredHolder<FluidType, FluidType> TAR = FLUID_TYPES.register("tar", () -> new BetweenlandsFluidType("tar", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.tar")
		.density(2000)
		.viscosity(2000)
		.temperature(330)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> RUBBER = FLUID_TYPES.register("rubber", () -> new BetweenlandsFluidType("rubber", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.rubber")
		.density(1200)
		.viscosity(1500)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> FOG = FLUID_TYPES.register("fog", () -> new BetweenlandsFluidType("fog", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.fog")
		.density(2)
		.viscosity(10)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SHALLOWBREATH = FLUID_TYPES.register("shallowbreath", () -> new BetweenlandsFluidType("shallowbreath", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.shallowbreath")
		.density(2)
		.viscosity(10)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> DYE = FLUID_TYPES.register("dye", () -> new BetweenlandsFluidType("dye_fluid", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> BREW = FLUID_TYPES.register("brew", () -> new BetweenlandsFluidType("drinkable_brew", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.brew")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CLEAN_WATER = FLUID_TYPES.register("clean_water", () -> new BetweenlandsFluidType("clean_water", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.clean_water")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> FISH_OIL = FLUID_TYPES.register("fish_oil", () -> new BetweenlandsFluidType("fish_oil", FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.fish_oil")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));
}
