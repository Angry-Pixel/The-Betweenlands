package thebetweenlands.common.registries;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.fluidattributes.BetweenlandsFluidAttributes;

public class FluidRegistry {
	// Base minecraft water resorces
	public static final ResourceLocation WATER_FLOW_TEX = new ResourceLocation("block/water_flow");
	public static final ResourceLocation WATER_STILL_TEX = new ResourceLocation("block/water_still");
	public static final ResourceLocation WATER_OVERLAY_TEX = new ResourceLocation("block/water_overlay");
	
	// Betweenslands fluids
	public static final ResourceLocation SWAMPWATER_FLOW_TEX = new ResourceLocation(TheBetweenlands.ID, "fluid/swamp_water_flow");
	public static final ResourceLocation SWAMPWATER_STILL_TEX = new ResourceLocation(TheBetweenlands.ID, "fluid/swamp_water_still");
	
	// Fluid lists
	public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, TheBetweenlands.ID);
	
	// Fluid inits
	// - Landscape
	public static final RegistryObject<FlowingFluid> SWAMP_WATER_STILL = FLUIDS.register("swamp_water", () -> new ForgeFlowingFluid.Source(FluidRegistry.WATER_LIKE_PROPERTIES));
	public static final RegistryObject<FlowingFluid> SWAMP_WATER_FLOW = FLUIDS.register("swamp_water_flow", () -> new ForgeFlowingFluid.Flowing(FluidRegistry.WATER_LIKE_PROPERTIES));
	
	// Fluid Properties
	public static final FluidAttributes.Builder SWAMP_WATER_ATTRIBUTES = BetweenlandsFluidAttributes.SwampWater.builder(SWAMPWATER_STILL_TEX, SWAMPWATER_FLOW_TEX).density(1000).viscosity(1000).sound(SoundEvents.BUCKET_FILL, SoundEvents.BUCKET_EMPTY).color(0xffffffff).overlay(WATER_OVERLAY_TEX);
	public static final ForgeFlowingFluid.Properties WATER_LIKE_PROPERTIES = new ForgeFlowingFluid.Properties( () -> SWAMP_WATER_STILL.get(), () -> SWAMP_WATER_FLOW.get(), SWAMP_WATER_ATTRIBUTES).block(BlockRegistry.SWAMP_WATER_BLOCK).levelDecreasePerBlock(1).slopeFindDistance(7).canMultiply().bucket(ItemRegistry.SWAMP_WATER_BUCKET);
	
	// Register fluid list
	public static void register(IEventBus eventBus) {
		FLUIDS.register(eventBus);
	}
}
