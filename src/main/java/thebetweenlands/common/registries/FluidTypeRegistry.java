package thebetweenlands.common.registries;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import thebetweenlands.common.TheBetweenlands;

//TODO fluids have a LOT more flexibility nowadays. Perhaps we should look into making our fluids more unique
public class FluidTypeRegistry {
	public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TheBetweenlands.ID);

	public static final DeferredHolder<FluidType, FluidType> SWAMP_WATER = FLUID_TYPES.register("swamp_water", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.swamp_water")
		.fallDistanceModifier(0.0F)
		.canExtinguish(true)
		.canSwim(false) //haha
		.canHydrate(true)
		.canConvertToSource(true)
		.supportsBoating(true)
		.pathType(PathType.WATER)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		.canHydrate(true)));

	public static final DeferredHolder<FluidType, FluidType> STAGNANT_WATER = FLUID_TYPES.register("stagnant_water", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.stagnant_water")
		.fallDistanceModifier(0.0F)
		.canExtinguish(true)
		.canSwim(false)
		.canConvertToSource(true)
		.supportsBoating(true)
		.pathType(PathType.DANGER_OTHER)
		.adjacentPathType(null)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)
		.canHydrate(true)));

	public static final DeferredHolder<FluidType, FluidType> TAR = FLUID_TYPES.register("tar", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.tar")
		.canSwim(false)
		.motionScale(0.002D)
		.density(2000)
		.viscosity(2000)
		.temperature(330)
		.pathType(PathType.DANGER_OTHER)
		.adjacentPathType(null)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> RUBBER = FLUID_TYPES.register("rubber", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.rubber")
		.canSwim(false)
		.motionScale(0.002D)
		.density(1200)
		.viscosity(1500)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> FOG = FLUID_TYPES.register("fog", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.fog")
		.density(2)
		.viscosity(10)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SHALLOWBREATH = FLUID_TYPES.register("shallowbreath", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.shallowbreath")
		.density(2)
		.viscosity(10)
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CLEAN_WATER = FLUID_TYPES.register("clean_water", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.clean_water")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> FISH_OIL = FLUID_TYPES.register("fish_oil", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.fish_oil")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> DULL_LAVENDER_DYE = FLUID_TYPES.register("dull_lavender_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.dull_lavender_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> MAROON_DYE = FLUID_TYPES.register("maroon_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.maroon_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SHADOW_GREEN_DYE = FLUID_TYPES.register("shadow_green_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.shadow_green_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CAMELOT_MAGENTA_DYE = FLUID_TYPES.register("camelot_magenta_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.camelot_magenta_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SAFFRON_DYE = FLUID_TYPES.register("saffron_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.saffron_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CARIBBEAN_GREEN_DYE = FLUID_TYPES.register("caribbean_green_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.caribbean_green_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> VIVID_TANGERINE_DYE = FLUID_TYPES.register("vivid_tangerine_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.vivid_tangerine_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CHAMPAGNE_DYE = FLUID_TYPES.register("champagne_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.champagne_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> RAISIN_BLACK_DYE = FLUID_TYPES.register("raisin_black_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.raisin_black_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SUSHI_GREEN_DYE = FLUID_TYPES.register("sushi_green_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.sushi_green_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> ELM_CYAN_DYE = FLUID_TYPES.register("elm_cyan_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.elm_cyan_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> CADMIUM_GREEN_DYE = FLUID_TYPES.register("cadmium_green_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.cadmium_green_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> LAVENDER_BLUE_DYE = FLUID_TYPES.register("lavender_blue_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.lavender_blue_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> BROWN_RUST_DYE = FLUID_TYPES.register("brown_rust_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.brown_rust_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> MIDNIGHT_PURPLE_DYE = FLUID_TYPES.register("midnight_purple_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.midnight_purple_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> PEWTER_GREY_DYE = FLUID_TYPES.register("pewter_grey_dye", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.pewter_grey_dye")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> NETTLE_SOUP = FLUID_TYPES.register("nettle_soup", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.nettle_soup")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> NETTLE_TEA = FLUID_TYPES.register("nettle_tea", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.nettle_tea")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> PHEROMONE_EXTRACT = FLUID_TYPES.register("pheromone_extract", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.pheromone_extract")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SWAMP_BROTH = FLUID_TYPES.register("swamp_broth", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.swamp_broth")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> STURDY_STOCK = FLUID_TYPES.register("sturdy_stock", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.sturdy_stock")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> PEAR_CORDIAL = FLUID_TYPES.register("pear_cordial", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.pear_cordial")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SHAMANS_BREW = FLUID_TYPES.register("shamans_brew", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.shamans_brew")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> LAKE_BROTH = FLUID_TYPES.register("lake_broth", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.lake_broth")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> SHELL_STOCK = FLUID_TYPES.register("shell_stock", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.shell_stock")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> FROG_LEG_EXTRACT = FLUID_TYPES.register("frog_leg_extract", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.frog_leg_extract")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));

	public static final DeferredHolder<FluidType, FluidType> WITCH_TEA = FLUID_TYPES.register("witch_tea", () -> new FluidType(FluidType.Properties.create()
		.descriptionId("block.thebetweenlands.witch_tea")
		.sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
		.sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
		.sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH)));
}
