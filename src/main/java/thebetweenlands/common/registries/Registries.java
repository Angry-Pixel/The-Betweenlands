package thebetweenlands.common.registries;

public class Registries {
	public static final Registries INSTANCE = new Registries();

	private FluidRegistry fluidRegistry;
	private BlockRegistry blockRegistry;
	private ItemRegistry itemRegistry;
	private EntityRegistry entityRegistry;
	private BiomeRegistry biomeRegistry;
	private TileEntityRegistry tileEntityRegistry;
	private RecipeRegistry recipeRegistry;
	private SoundRegistry soundRegistry;
	private ModelRegistry modelRegistry;
	private AspectRegistry aspectRegistry;

	public void preInit() {
		(this.fluidRegistry = new FluidRegistry()).preInit();
		(this.blockRegistry = new BlockRegistry()).preInit();
		(this.itemRegistry = new ItemRegistry()).preInit();
		(this.entityRegistry = new EntityRegistry()).preInit();
		(this.biomeRegistry = new BiomeRegistry()).preInit();
		this.tileEntityRegistry = new TileEntityRegistry();
		this.recipeRegistry = new RecipeRegistry();
		this.soundRegistry = new SoundRegistry();
		(this.modelRegistry = new ModelRegistry()).preInit();
		this.aspectRegistry = new AspectRegistry();
	}

	public void init() {
		//this.blockRegistry.init();
		//this.itemRegistry.init();
		this.recipeRegistry.init();
		this.tileEntityRegistry.init();
		this.aspectRegistry.init();
	}
}
