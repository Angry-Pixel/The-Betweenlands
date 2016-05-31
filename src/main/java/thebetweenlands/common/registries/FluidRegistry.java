package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FluidRegistry {
	public static final Fluid SWAMP_WATER = new Fluid("swampWater", new ResourceLocation("thebetweenlands:textures/fluids/swamp_water_still"), new ResourceLocation("thebetweenlands:textures/fluids/swamp_water_flowing")).setDensity(1000).setViscosity(1000);

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
