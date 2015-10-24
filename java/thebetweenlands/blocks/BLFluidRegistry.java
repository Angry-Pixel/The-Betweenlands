package thebetweenlands.blocks;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import thebetweenlands.items.BLItemRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BLFluidRegistry {
	public static final BLFluidRegistry INSTANCE = new BLFluidRegistry();

	private static final ArrayList<Fluid> REGISTERED_FLUIDS = new ArrayList<Fluid>();

	public static Fluid swampWater = new Fluid("swampWater").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWater");
	public static Fluid swampWaterWaterWeeds = new Fluid("swampWaterWaterWeeds").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterWaterWeeds");
	public static Fluid swampWaterWaterWeedsSmall = new Fluid("swampWaterWaterWeedsSmall").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterWaterWeedsSmall");
	public static Fluid swampWaterMireCoral = new Fluid("swampWaterMireCoral").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterMireCoral");
	public static Fluid swampWaterReed = new Fluid("swampWaterReed").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterReed");
	public static Fluid swampWaterBogBean = new Fluid("swampWaterBogBean").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterBogBean");
	public static Fluid swampWaterGoldenClub = new Fluid("swampWaterGoldenClub").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterGoldenClub");
	public static Fluid swampWaterMarshMarigold = new Fluid("swampWaterMarshMarigold").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterMarshMarigold");
	public static Fluid swampWaterStalk = new Fluid("swampWaterStalk").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterStalk");
	public static Fluid swampWaterRoot = new Fluid("swampWaterRoot").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterRoot");
	public static Fluid swampWaterDeepWaterCoral = new Fluid("swampWaterDeepWaterCoral").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWaterDeepWaterCoral");
	public static Fluid swampWaterLifeCrystal = new Fluid("lifeCrystal").setDensity(1000).setViscosity(1000).setUnlocalizedName("lifeCrystal");
	public static Fluid tarFluid = new Fluid("tarFluid").setDensity(2000).setViscosity(2000).setUnlocalizedName("tarFluid");
	public static Fluid stagnantWater = new Fluid("stagnantWater").setDensity(1200).setViscosity(1200).setLuminosity(8).setUnlocalizedName("stagnantWater");


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

		FluidContainerRegistry.registerFluidContainer(tarFluid, new ItemStack(BLItemRegistry.weedwoodBucketTar), new ItemStack(BLItemRegistry.weedwoodBucket));
		FluidContainerRegistry.registerFluidContainer(swampWater, new ItemStack(BLItemRegistry.weedwoodBucketWater), new ItemStack(BLItemRegistry.weedwoodBucket));
		FluidContainerRegistry.registerFluidContainer(stagnantWater, new ItemStack(BLItemRegistry.weedwoodStagnantWater), new ItemStack(BLItemRegistry.weedwoodBucket));
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