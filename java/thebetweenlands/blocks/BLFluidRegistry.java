package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BLFluidRegistry {
	public static final BLFluidRegistry INSTANCE = new BLFluidRegistry();

	private static final ArrayList<Fluid> REGISTERED_FLUIDS = new ArrayList<>();

	public static Fluid swampWater = new Fluid("swampWater").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWater");
	public static Fluid swampWaterWaterWeeds = new Fluid("swampWaterWaterWeeds").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterWaterWeeds");
	public static Fluid swampWaterMireCoral = new Fluid("swampWaterMireCoral").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterMireCoral");
	public static Fluid swampWaterReed = new Fluid("swampWaterReed").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterReed");
	public static Fluid swampWaterBogBean = new Fluid("swampWaterBogBean").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterBogBean");
	public static Fluid swampWaterGoldenClub = new Fluid("swampWaterGoldenClub").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterGoldenClub");
	public static Fluid swampWaterMarshMarigold = new Fluid("swampWaterMarshMarigold").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterMarshMarigold");
	public static Fluid swampWaterStalk = new Fluid("swampWaterStalk").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterStalk");
	public static Fluid swampWaterRoot = new Fluid("swampWaterRoot").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterRoot");


	private BLFluidRegistry() {
	}

	public static void init() {
		try {
			for (Field f : BLFluidRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Fluid) {
					FluidRegistry.registerFluid((Fluid) obj);
					REGISTERED_FLUIDS.add((Fluid)obj);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

		//FluidContainerRegistry.registerFluidContainer(swampWater, new ItemStack(Items.water_bucket), new ItemStack(Items.bucket));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void postStitch(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			for(Fluid fluid : REGISTERED_FLUIDS) {
				fluid.setIcons(BLBlockRegistry.swampWater.getBlockTextureFromSide(0), BLBlockRegistry.swampWater.getBlockTextureFromSide(1));
			}
		}
	}
}