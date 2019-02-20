package thebetweenlands.common;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.Registries;
import thebetweenlands.compat.tmg.TMGEquipmentInventory;

//TODO 1.13 Move to mods.toml
@Mod(ModInfo.ID/*, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = ModInfo.MC_VERSIONS, certificateFingerprint = "${fingerprint}", dependencies = ModInfo.DEPENDENCIES, serverSideOnly = ModInfo.SERVER_ONLY*/)
public class TheBetweenlands {
	public static TheBetweenlands instance;

	//TODO 1.13 Remove proxies and use DistExecutor instead
	//@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	//public static CommonProxy proxy;

	public static final Registries REGISTRIES = new Registries();

	public static DimensionType dimensionType;

	//TODO 1.13 New network code
	//public static SimpleNetworkWrapper networkWrapper;

	public static final Logger logger = LogManager.getLogger();

	public static boolean isToughAsNailsModInstalled = false;

	public static final String DIMENSION_NAME = "betweenlands";

	public TheBetweenlands() {
		//TODO 1.13 New coremod code
		//TheBetweenlandsPreconditions.check();

		//TODO 1.13 New configs
		//Load config before it is changed
		//ConfigHelper.loadExistingConfig();

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onFingerprintViolation);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupCommon);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
		
		FMLJavaModLoadingContext.get().getModEventBus().register(BlockRegistry.class);
		FMLJavaModLoadingContext.get().getModEventBus().register(ItemRegistry.class);
		
		instance = this;
	}

	//TODO 1.13 Does this fire?
	private void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		Logger fpLogger = LogManager.getLogger("Betweenlands Fingerprint Violation");
		fpLogger.warn("Invalid fingerprint for The Betweenlands detected!");
		fpLogger.warn("The Betweenlands jar file was either tampered with or");
		fpLogger.warn("is corrupted. Please remove the file and (re)download");
		fpLogger.warn("the mod from the official CurseForge project at");
		fpLogger.warn("https://www.curseforge.com/minecraft/mc-mods/angry-pixel-the-betweenlands-mod");
	}

	private void setupCommon(FMLCommonSetupEvent event) {
		//Run in parallel, don't interact with game state.

		//TODO 1.13 New configs
		//Configuration File
		//ConfigHelper.init();

		BetweenlandsAPI.init();

		this.registerEventHandlers();

		//TODO 1.13 Network GUI handler
		//NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		/// Network ///
		//networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);

		//MessageRegistry.preInit();

		//TODO 1.13 Init registries via events
		//REGISTRIES.init();

		//TODO 1.13 Remove proxies
		//proxy.init();

		this.registerEventHandlers();

		//TODO 1.13 WorldGenerator move to Feature
		//overworldDim.swampBiome.addFeature(...);
		//GameRegistry.registerWorldGenerator(new WorldGenDruidCircle(), 0);
		//for(biome : blDim) biome.addFeature(...);
		//GameRegistry.registerWorldGenerator(new WorldGenWaystone(), 0);

		DeferredWorkQueue.runLater(this::postSetup);
	}

	private void setupClient(FMLClientSetupEvent event) {
		//TODO 1.13 Move proxy post init here
		//proxy.postInit();

		//TODO 1.13 Init registries via events
		//REGISTRIES.preInit();

		//TODO 1.13 Remove proxies
		//Renderers
		//proxy.registerItemAndBlockRenderers();
		//proxy.preInit();
	}

	private void postSetup() {
		//Called after the common setup, still can't interact with game state

		//TODO 1.13 Do we still need postSetup? This code can all be moved to setupCommon...
		isToughAsNailsModInstalled = ModList.get().isLoaded("toughasnails") || ModList.get().isLoaded("ToughAsNails");

		if (ModList.get().isLoaded("tombmanygraves2api")) {
			new TMGEquipmentInventory();
		}

		/*if (ConfigHandler.DEBUG.debug) {
			System.out.println("==================================================");
			for (String name : unlocalizedNames) {
				System.out.println("needs translation: " + name);
			}
			System.out.println("==================================================");
		}*/
	}
	
	//Use SubscribeEvent because when using lambdas the listener receives the event for every single registry...
	@SubscribeEvent
	public static void registerDimension(RegistryEvent.Register<ModDimension> event) {
		ModDimension modDimension = new ModDimension() {
			@Override
			public Function<DimensionType, ? extends Dimension> getFactory() {
				//TODO 1.13 Dimension factory
				return (dimensionType) -> {
					TheBetweenlands.dimensionType = dimensionType;
					//return new BetweenlandsDimension(dimensionType);
					return null;
				};
			}
		};
		
		modDimension.setRegistryName(new ResourceLocation(ModInfo.ID, DIMENSION_NAME));
		
		event.getRegistry().register(modDimension);
		
		DimensionManager.registerDimension(new ResourceLocation(ModInfo.ID, DIMENSION_NAME), modDimension, null);
	}

	//TODO 1.13 Commands
	/*@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBLEvent());
		event.registerServerCommand(new CommandResetAspects());
		event.registerServerCommand(new CommandDecay());
		event.registerServerCommand(new CommandAspectDiscovery());
		GameruleRegistry.INSTANCE.onServerStarting(event);
		event.registerServerCommand(new CommandReloadRecipes());
	}*/

	/**
	 * Register event handlers here
	 */
	private void registerEventHandlers() {
		//TODO 1.13 Events
		//proxy.registerEventHandlers();

		//TODO 1.13 Events
		/*MinecraftForge.EVENT_BUS.register(BlockRegistry.class);
		MinecraftForge.EVENT_BUS.register(ItemRegistry.class);
		MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);
		MinecraftForge.EVENT_BUS.register(BiomeRegistry.class);
		MinecraftForge.EVENT_BUS.register(SoundRegistry.class);
		MinecraftForge.EVENT_BUS.register(ElixirEffectRegistry.class);

		WorldStorageImpl.registerCapability();
		MinecraftForge.EVENT_BUS.register(WorldStorageImpl.Handler.class);
		MinecraftForge.EVENT_BUS.register(ConfigHelper.class);
		MinecraftForge.EVENT_BUS.register(ItemBLShield.EventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(WorldEventHandler.class);
		MinecraftForge.EVENT_BUS.register(BetweenlandsChunkStorage.class);
		MinecraftForge.EVENT_BUS.register(new AnvilEventHandler());
		MinecraftForge.EVENT_BUS.register(EnvironmentEventHandler.class);
		MinecraftForge.EVENT_BUS.register(EntityCapabilityHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemCapabilityHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerDecayHandler.class);
		MinecraftForge.EVENT_BUS.register(AspectSyncHandler.class);
		MinecraftForge.EVENT_BUS.register(MobSpawnHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BlockBreakHandler.class);
		MinecraftForge.EVENT_BUS.register(LocationHandler.class);
		MinecraftForge.EVENT_BUS.register(AttackDamageHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemEquipmentHandler.class);
		MinecraftForge.EVENT_BUS.register(EntitySpawnHandler.class);
		MinecraftForge.EVENT_BUS.register(ArmorHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemRingOfFlight.class);
		MinecraftForge.EVENT_BUS.register(PuppetHandler.class);
		MinecraftForge.EVENT_BUS.register(OverworldItemHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerPortalHandler.class);
		MinecraftForge.EVENT_BUS.register(FoodSicknessHandler.class);
		MinecraftForge.EVENT_BUS.register(BlockGenericDugSoil.class);
		MinecraftForge.EVENT_BUS.register(ElixirCommonHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BlockWeedwoodBush.class);
		MinecraftForge.EVENT_BUS.register(ItemDentrothystShield.class);
		MinecraftForge.EVENT_BUS.register(EnvironmentEventOverridesHandler.class);
		MinecraftForge.EVENT_BUS.register(AdvancementHandler.class);
		MinecraftForge.EVENT_BUS.register(FuelHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerJoinWorldHandler.class);
		MinecraftForge.EVENT_BUS.register(PlayerRespawnHandler.class);
		MinecraftForge.EVENT_BUS.register(CustomEntityBlockCollisionsHandler.class);
		MinecraftForge.EVENT_BUS.register(PotionRootBound.class);
		MinecraftForge.EVENT_BUS.register(BossHandler.class);
		MinecraftForge.EVENT_BUS.register(ItemMagicItemMagnet.class);
		MinecraftForge.EVENT_BUS.register(EntityWeedwoodRowboat.class);
		MinecraftForge.EVENT_BUS.register(GameruleRegistry.class);*/
	}
}
