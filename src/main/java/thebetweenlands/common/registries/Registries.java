package thebetweenlands.common.registries;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	public final BlockRegistry blockRegistry = new BlockRegistry();
	public final TileEntityRegistry tileEntityRegistry = new TileEntityRegistry();
	public final ItemRegistry itemRegistry = new ItemRegistry();
	public final RecipeRegistry recipeRegistry = new RecipeRegistry();
	public final EntityRegistry entityRegistry = new EntityRegistry();
	public final FluidRegistry fluidRegistry = new FluidRegistry();
	public final BiomeRegistry biomeRegistry = new BiomeRegistry();

	public void preInit() {
		this.blockRegistry.preInit();
		this.itemRegistry.preInit();
		this.entityRegistry.preInit();
		this.fluidRegistry.preInit();
		this.biomeRegistry.preInit();
	}

	public void init() {
		this.blockRegistry.init();
		this.itemRegistry.init();
		this.recipeRegistry.init();
		this.tileEntityRegistry.init();
	}
}
