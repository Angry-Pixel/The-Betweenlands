package thebetweenlands.common.registries;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		BlockRegistry.preInit();
		ItemRegistry.preInit();
		EntityRegistry.preInit();
		BiomeRegistry.preInit();
		SoundRegistry.preInit();
		CapabilityRegistry.preInit();
		
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ModelRegistry.preInit();
			AmbienceRegistry.preInit();
		}
	}

	public void init() {
		//this.blockRegistry.init();
		//this.itemRegistry.init();
		RecipeRegistry.init();
		TileEntityRegistry.init();
		AspectRegistry.init();
		FluidRegistry.init();
	}
}
