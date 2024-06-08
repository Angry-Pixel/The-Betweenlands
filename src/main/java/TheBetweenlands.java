package thebetweenlands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thebetweenlands.client.gui.AmateMapRenderer;
import thebetweenlands.client.rendering.BetweenlandsSkyRenderer;
import thebetweenlands.client.rendering.BetweenlandsSkyShaderHandler;
import thebetweenlands.client.rendering.entitys.RenderGecko;
import thebetweenlands.client.rendering.entitys.RenderSwampHag;
import thebetweenlands.client.rendering.entitys.RenderWight;
import thebetweenlands.client.rendering.model.entity.ModelGecko;
import thebetweenlands.client.rendering.model.entity.ModelSwampHag;
import thebetweenlands.client.rendering.model.entity.ModelWight;
import thebetweenlands.client.rendering.shader.BetweenlandsShaders;
import thebetweenlands.client.rendering.shader.BetweenlandsSkyShaderInstance;
import thebetweenlands.common.ambientsounds.BetweenlandsSoundManager;
import thebetweenlands.common.networking.BetweenlandsPacketHandler;
import thebetweenlands.common.particles.BetweenlandsParticle;
import thebetweenlands.common.particles.BetweenlandsPortalParticle;
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
	public static BetweenlandsSkyShaderHandler skyTextureHandler;
	public BetweenlandsSkyRenderer skyrenderer = new BetweenlandsSkyRenderer();
	public int loopstate = 0;
	
	// Betweenlands Level loader
	public boolean isBetweenlandsLoeaded = false;
	
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

	// World provider settings untill i make a class for them
	public static int LAYER_HEIGHT = 120;
	public static int CAVE_WATER_HEIGHT = 15;

    // Set up event bus
	IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
    
    public TheBetweenlands() {

    	// Register mod contents
		SoundRegistry.register(eventbus);
    	ParticleRegistry.register(eventbus);
    	BlockRegistry.register(eventbus);
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
    	// Client side registration
    	eventbus.addListener(this::doClientStuff);
    	// Colors
    	eventbus.addListener(this::registerBlockColors);
    	// Particles
    	eventbus.addListener(this::particleStuff);
		// shaders
    	eventbus.addListener(this::registershaders);
    	// Render Layer
    	eventbus.addListener(this::registerLayerDefinition);
		// Gen layers event
		eventbus.addListener(this::GenLayersEvent);
    	// Betweenlands cave & underwater ambience register
    	//eventbus.addListener(this::betweenlandsAmbienceHandler);

		DimensionRegistries.register(eventbus);

        // Register mod to event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registershaders(final RegisterShadersEvent event) {
    	//BetweenlandsShaderInstance betweenlandssky = new BetweenlandsShaderInstance(event.getResourceManager(), new ResourceLocation(Betweenlands.ID, "starfield"), BetweenlandsVertexFormats.BETWEENLANDS_SKY);
    	
    	// todo: sky shader "starfield" name is being changed to "betweenlandsSky" 
    	BetweenlandsShaders.preloadShaders(event.getResourceManager());
		event.registerShader(BetweenlandsShaders.BetweenlandsSky, BetweenlandsSkyShaderInstance.onLoad);
    }

	// For whenever I make the gen layers lib a public thing (not sure about the legalitys)
	private void GenLayersEvent(final RegisterGenLayersEvent event) {
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
    
	private void doClientStuff(final FMLClientSetupEvent event) {
    	// Client only setup

		// Amate Map renderer
		TheBetweenlands.amateMapRenderer = new AmateMapRenderer(Minecraft.getInstance().textureManager);

		// Entity renderers
		EntityRenderers.register(EntityRegistry.SWAMP_HAG.get(), RenderSwampHag::new);
		EntityRenderers.register(EntityRegistry.GECKO.get(), RenderGecko::new);
		EntityRenderers.register(EntityRegistry.WIGHT.get(), RenderWight::new);
		
		
		// Compile all rift textures into an atlas
		LOGGER.info("Building rift texture atlas");
		skyTextureHandler = new BetweenlandsSkyShaderHandler(true);
    }
	
	private void registerLayerDefinition(final EntityRenderersEvent.RegisterLayerDefinitions event) {
		
		event.registerLayerDefinition(RenderSwampHag.SWAMP_HAG_MODEL_LAYER, ModelSwampHag::createModelLayer);
		event.registerLayerDefinition(RenderGecko.GECKO_MODEL_LAYER, ModelGecko::createModelLayer);
		event.registerLayerDefinition(RenderWight.WIGHT_MODEL_LAYER, ModelWight::createModelLayer);
	}
    
    @SuppressWarnings("resource")
	private void particleStuff(final ParticleFactoryRegisterEvent event)
    {
    	Minecraft.getInstance().particleEngine.register(ParticleRegistry.SULFUR_GENERIC.get(), BetweenlandsParticle.Helper::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.PORTAL_EFFECT.get(), BetweenlandsPortalParticle.Helper::new);
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Recive from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().map(m->m.messageSupplier().get()).collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void registerBlockColors(ColorHandlerEvent.Block event) {
    	
    	// Changes particle and block colors
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_WEEDWOOD_TREE.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_NIBBLETWIG_TREE.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.LEAVES_RUBBER_TREE.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_GRASS.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.POISON_IVY.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_REED.get());
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.MOSS.get());
		event.getBlockColors().register(BlockColorRegistry.SWAMP_FOLIGE, BlockRegistry.HANGER.get());
		event.getBlockColors().register(BlockColorRegistry.SWAMP_GRASS, BlockRegistry.SWAMP_TALLGRASS.get());
    	
    	// Only effects particle colors
    	event.getBlockColors().register(BlockColorRegistry.SWAMP_WATER, BlockRegistry.SWAMP_WATER_BLOCK.get());
    }
    
    // Ticks every render tick
	// This is some very old code
    @SuppressWarnings("resource")
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void betweenlandsAmbienceHandler(final ClientTickEvent event) {
    	
    	// Get time for shaders
    	//FractinalTime += 1 / Minecraft.getInstance().getFrameTime();
    	//Time = (int) FractinalTime;
    	
    	// Init vars
    	Player player = Minecraft.getInstance().player;
    	if (player!=null) {
    	ClientLevel level = Minecraft.getInstance().level;
    	//double x = player.getX(), y = player.getY(), z = player.getZ();
    	//BlockPos blockpos = new BlockPos(x,y,z);
    	BlockPos eyeblockpos = player.eyeBlockPosition();
    	//render.render
    	//LOGGER.info(ModFluids.SWAMP_WATER_FLOW.get().getTags());
    	
    	// TODO: replace with a more professional solution
    	
    	if (level.dimension() == DimensionRegistries.BETWEENLANDS_DIMENSION_KEY) {
    		
    		rotation += 0.001f;
    		
    		// Sound
    		if (player.isEyeInFluid(FluidTags.WATER) && level.getFluidState(eyeblockpos).getType().isSame(FluidRegistry.SWAMP_WATER_STILL.get().getSource())) {
        		// Water state
        		if (loopstate!=3) {
        			loopstate = 3;
        			soundManager.stopLoopedSound();
        		}
        		soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_WATER_LOOP.get());
        	}
        	else if (eyeblockpos.getY() <= 47)
        	{
        		// Cave state
        		if (loopstate!=2) {
        			loopstate = 2;
        			soundManager.stopLoopedSound();
        		}
        		soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_CAVES_LOOP.get());
        	}
        	else
        	{
        		// Default state
        		if (loopstate!=1) {
        			loopstate = 1;
        			soundManager.stopLoopedSound();
        		}
        		soundManager.playLoopedSound(SoundRegistry.BETWEENLANDS_AMBIENT_SWAMP_LOOP.get());
        	}
    	}
    	else {
    		
    		// Sound
    		// Off state
    		if (loopstate!=0) {
    			loopstate = 0;
    			soundManager.stopLoopedSound();
    		}
    	}
    }
    }
}


