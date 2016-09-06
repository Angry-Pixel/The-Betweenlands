package thebetweenlands.common.registries;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	public void preInit() {
		FluidRegistry.preInit();
		BlockRegistry.preInit();
		ItemRegistry.preInit();
		EntityRegistry.preInit();
		BiomeRegistry.preInit();
		SoundRegistry.preInit();
		ModelRegistry.preInit();
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
