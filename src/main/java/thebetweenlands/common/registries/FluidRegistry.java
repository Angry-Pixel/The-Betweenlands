package thebetweenlands.common.registries;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.CaseFormat;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidRegistry {
	private FluidRegistry() { }
	
	/**
	 * A fluid that can be used by multiple blocks
	 */
	public static class FluidMultipleBlocks extends Fluid {
		public FluidMultipleBlocks(String fluidName, ResourceLocation still, ResourceLocation flowing) {
			super(fluidName, still, flowing);
		}

		@Override
		public Fluid setBlock(Block block) {
			return this;
		}

		/**
		 * Sets the original block of this fluid
		 * @param block
		 * @return
		 */
		public Fluid setOriginalBlock(Block block) {
			this.block = block;
			return this;
		}
	}

	public static final FluidMultipleBlocks SWAMP_WATER = (FluidMultipleBlocks) new FluidMultipleBlocks("swamp_water", new ResourceLocation("thebetweenlands:fluids/swamp_water_still"), new ResourceLocation("thebetweenlands:fluids/swamp_water_flowing")).setDensity(1000).setViscosity(1000);
	public static final Fluid STAGNANT_WATER = new Fluid("stagnant_water", new ResourceLocation("thebetweenlands:fluids/stagnant_water_still"), new ResourceLocation("thebetweenlands:fluids/stagnant_water_flowing")).setDensity(1000).setViscosity(1000);
	public static final Fluid TAR = new Fluid("tar", new ResourceLocation("thebetweenlands:fluids/tar_still"), new ResourceLocation("thebetweenlands:fluids/tar_flowing")).setDensity(2000).setViscosity(2000).setTemperature(330);
	public static final Fluid RUBBER = new Fluid("rubber", new ResourceLocation("thebetweenlands:fluids/rubber_still"), new ResourceLocation("thebetweenlands:fluids/rubber_flowing")).setDensity(1200).setViscosity(1500);
	public static final Fluid FOG = new Fluid("fog", new ResourceLocation("thebetweenlands:fluids/fog"), new ResourceLocation("thebetweenlands:fluids/fog")).setDensity(2).setViscosity(10).setGaseous(true);
	public static final Fluid SHALLOWBREATH = new Fluid("shallowbreath", new ResourceLocation("thebetweenlands:fluids/shallowbreath"), new ResourceLocation("thebetweenlands:fluids/shallowbreath")).setDensity(2).setViscosity(10).setGaseous(true);
	public static final Fluid DYED_WATER = new Fluid("dyed_water", new ResourceLocation("thebetweenlands:fluids/dyed_water_still"), new ResourceLocation("thebetweenlands:fluids/dyed_water_flowing")) {
		@Override
		public int getColor(net.minecraftforge.fluids.FluidStack stack) {
			if(stack.tag != null && stack.tag.hasKey("color")) {
				return stack.tag.getInteger("color") | 0xFF000000;
			}
			return 0xFFFFFFFF;
		}
	}.setDensity(1000).setViscosity(1000);

	public static final List<Fluid> REGISTERED_FLUIDS = new ArrayList<Fluid>();

	public static void preInit() {
		try {
			for (Field f : FluidRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Fluid) {
					Fluid fluid = (Fluid) obj;
					net.minecraftforge.fluids.FluidRegistry.registerFluid(fluid);
					if(fluid != FOG && fluid != SHALLOWBREATH) {
						net.minecraftforge.fluids.FluidRegistry.addBucketForFluid(fluid);
					}
					REGISTERED_FLUIDS.add((Fluid)obj);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
		SWAMP_WATER.setOriginalBlock(BlockRegistry.SWAMP_WATER);
	}
}
