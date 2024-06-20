package thebetweenlands.common;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import thebetweenlands.client.ClientEvents;
import thebetweenlands.client.gui.AmateMapRenderer;
import thebetweenlands.client.rendering.BetweenlandsSkyRenderer;
import thebetweenlands.common.ambientsounds.BetweenlandsSoundManager;
import thebetweenlands.common.datagen.DataGenerators;
import thebetweenlands.common.networking.BetweenlandsPacketHandler;
import thebetweenlands.common.registries.*;
import thebetweenlands.common.world.BetweenlandsBiomeProvider;
import thebetweenlands.common.world.BiomeDecoratorEvent.RegisterBiomeDecoratorEvent;
import thebetweenlands.common.world.ChunkGeneratorBetweenlands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thebetweenlands.common.world.GenLayersEvent.RegisterGenLayersEvent;
import thebetweenlands.common.world.LegacyBiomeSource;
import thebetweenlands.common.world.noisegenerators.genlayers.ProviderGenLayerBetweenlands;

import java.util.stream.Collectors;

@Mod("thebetweenlands")
public class TheBetweenlands
{
	// Mod Vars
	public static final  String NAME = "Betweenlands 1.18 Remake";
	public static final  String ID = "thebetweenlands";
	public static final  String VERSION = "1.0";

	// Random global objects
	public static AmateMapRenderer amateMapRenderer;
	
	// Betweenlands time (for shaders)
	public static int Time;				// Time in seconds
	public static float FractinalTime;	// Fractinal second
	
	// debug values
	public static float apeture = 0.53f;		// start point of fog
	public static float range = 0.4f;			// how far the fog reatches up to cover the sky
	public static float rotation = 0.0f;			// a rotation value sent to the shader to save proc time
	public static int amateMapRendererKey;		// Was thinking about making a lib for custom maps, this is a placeholder for that

	// Old debug values for shader handler, still used by debug command
	public static float distmul[] = {0.1f,0.1f,0.1f,0.1f};

	// Config vars
	public static boolean useVanillaBiomes = true;		// if false all normal biomes no longer get registered, for if someone wants to replace all biomes in datapack
	// (also making away for biomes to individually be modified by data pack)

	// Betweenlands sound manager
	public static BetweenlandsSoundManager soundManager = new BetweenlandsSoundManager();

	public BetweenlandsSkyRenderer skyrenderer = new BetweenlandsSkyRenderer();
	public int loopstate = 0;
	
	// Betweenlands Level loader
	public boolean isBetweenlandsLoeaded = false;
	
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

	// World provider settings untill i make a class for them
	public static int LAYER_HEIGHT = 120;
	public static int CAVE_WATER_HEIGHT = 15;
    
    public TheBetweenlands(IEventBus eventbus) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ClientEvents.initClient(eventbus);
		}

    	// Register mod contents
		SoundRegistry.register(eventbus);
    	ParticleRegistry.register(eventbus);
    	BlockRegistry.BLOCKS.register(eventbus);
    	ItemRegistry.register(eventbus);
    	FluidRegistry.register(eventbus);
    	
    	EntityRegistry.register(eventbus);
    	
        // rebuilding features and carvers
		PlacementRegistry.register(eventbus);
        FeatureRegistries.register(eventbus);
    	CarverRegistry.register(eventbus);
    	
    	BiomeRegistry.register(eventbus);
    	
    	//Blocks.AZALEA_LEAVES = bob;
    	
        // Register the setup method for modloading
    	eventbus.addListener(this::setup);
        // Register the processIMC method for modloading
    	eventbus.addListener(this::processIMC);
		// Gen layers event
		eventbus.addListener(this::genLayersEvent);
    	// Betweenlands cave & underwater ambience register
    	//eventbus.addListener(this::betweenlandsAmbienceHandler);
		// Data generation
		eventbus.addListener(DataGenerators::gatherData);

		DimensionRegistries.register(eventbus);

        // Register mod to event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

	// For whenever I make the gen layers lib a public thing (not sure about the legalitys)
	private void genLayersEvent(final RegisterGenLayersEvent event) {
		event.RegisterProvider(new ResourceLocation(TheBetweenlands.ID,"gen_layers_betweenlands"), ProviderGenLayerBetweenlands::new);
	}

	// Add configured feature registering
	private void setup(final FMLCommonSetupEvent event)
    {
		// Post RegisterGenLayersEvent
		//MinecraftForge.EVENT_BUS.register(new RegisterGenLayersEvent());

		TheBetweenlands.LOGGER.info("RegisterGenLayersEvent posted");
		ModLoader.get().postEvent(new RegisterGenLayersEvent());
		TheBetweenlands.LOGGER.info("RegisterGenLayersEvent concluded");

		TheBetweenlands.LOGGER.info("RegisterBiomeDecoratorEvent posted");
		ModLoader.get().postEvent(new RegisterBiomeDecoratorEvent());
		TheBetweenlands.LOGGER.info("RegisterBiomeDecoratorEvent concluded");

    	// Server and client setup
		BetweenlandsPacketHandler.init();


        // Block color setup
		//ModBlockGrassColors.register();
		//ModBlockFluidColors.register();
        ItemColorRegistry.register();

        // Register biome source and chunk generator
		Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(TheBetweenlands.ID, "legacy_biomeprovider"), LegacyBiomeSource.CODEC);
     	Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(TheBetweenlands.ID, "betweenlands_biomeprovider"), BetweenlandsBiomeProvider.CODEC);
        Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(TheBetweenlands.ID, "the_betweenlands_chunkgen"), ChunkGeneratorBetweenlands.CODEC);
        
        // Block render types setup
        // todo: move this to block types (me too lazy)

        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.PORTAL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_GRASS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.DEAD_SWAMP_GRASS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.MOSS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.POISON_IVY.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_WEEDWOOD_TREE.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.WEEDWOOD_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.RUBBER_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.NIBBLETWIG_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_NIBBLETWIG_TREE.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_SAP_TREE.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_RUBBER_TREE.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.LEAVES_HEARTHGROVE_TREE.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SAP_SAPLING.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.THORNS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.CAVE_MOSS.get(), RenderType.cutoutMipped());
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_REED.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.HANGER.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.SWAMP_TALLGRASS.get(), RenderType.cutoutMipped());
		ItemBlockRenderTypes.setRenderLayer(BlockRegistry.BULB_CAPPED_MUSHROOM_CAP.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_FLOW.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(FluidRegistry.SWAMP_WATER_STILL.get(), RenderType.translucent());
    }

	//TODO: probably unnecessary
    private void processIMC(final InterModProcessEvent event)
    {
        // Recive from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().map(m->m.messageSupplier().get()).collect(Collectors.toList()));
    }
}


