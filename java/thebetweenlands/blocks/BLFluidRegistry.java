package thebetweenlands.blocks;

import java.lang.reflect.Field;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BLFluidRegistry {
	public static final BLFluidRegistry INSTANCE = new BLFluidRegistry();

	public static Fluid swampWater = new Fluid("swampWater").setDensity(1000).setViscosity(1000).setUnlocalizedName("swampWater");

	private BLFluidRegistry() {
	}

	public static void init() {
		try {
			for (Field f : BLFluidRegistry.class.getDeclaredFields()) {
				Object obj = f.get(null);
				if (obj instanceof Fluid)
					FluidRegistry.registerFluid((Fluid) obj);
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
			swampWater.setIcons(BLBlockRegistry.swampWater.getBlockTextureFromSide(0), BLBlockRegistry.swampWater.getBlockTextureFromSide(1));
		}
	}
}