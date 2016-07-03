package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidRegistry {
	/**
	 * A fluid that can be used by multiple blocks
	 */
	static class FluidMultipleBlocks extends Fluid {
		public FluidMultipleBlocks(String fluidName, ResourceLocation still, ResourceLocation flowing) {
			super(fluidName, still, flowing);
		}

		@Override
		public Fluid setBlock(Block block) {
			return this;
		}
	}

	public static final Fluid SWAMP_WATER = new FluidMultipleBlocks("swampWater", new ResourceLocation("thebetweenlands:fluids/swamp_water_still"), new ResourceLocation("thebetweenlands:fluids/swamp_water_flowing")).setDensity(1000).setViscosity(1000);

	private static final ArrayList<Fluid> REGISTERED_FLUIDS = new ArrayList<Fluid>();

	public void preInit() {
		try {
			for (Field f : FluidRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Fluid) {
					net.minecraftforge.fluids.FluidRegistry.registerFluid((Fluid) obj);
					REGISTERED_FLUIDS.add((Fluid)obj);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
