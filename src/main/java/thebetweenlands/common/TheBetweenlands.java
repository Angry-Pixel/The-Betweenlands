package thebetweenlands.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.common.block.farming.BlockGenericDugSoil;
import thebetweenlands.common.block.plant.BlockWeedwoodBush;
import thebetweenlands.common.capability.base.EntityCapabilityHandler;
import thebetweenlands.common.capability.base.ItemCapabilityHandler;
import thebetweenlands.common.command.CommandAspectDiscovery;
import thebetweenlands.common.command.CommandBLEvent;
import thebetweenlands.common.command.CommandReloadRecipes;
import thebetweenlands.common.command.CommandResetAspects;
import thebetweenlands.common.handler.*;
import thebetweenlands.common.herblore.elixir.ElixirEffectRegistry;
import thebetweenlands.common.item.equipment.ItemRingOfFlight;
import thebetweenlands.common.item.shields.ItemDentrothystShield;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.GameruleRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.MessageRegistry;
import thebetweenlands.common.registries.RecipeRegistry;
import thebetweenlands.common.registries.Registries;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.biome.spawning.MobSpawnHandler;
import thebetweenlands.common.world.gen.feature.structure.WorldGenDruidCircle;
import thebetweenlands.common.world.storage.BetweenlandsChunkStorage;
import thebetweenlands.common.world.storage.WorldStorageImpl;
import thebetweenlands.core.TheBetweenlandsPreconditions;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, acceptedMinecraftVersions = ModInfo.MC_VERSIONS, certificateFingerprint = "${fingerprint}")
public class TheBetweenlands {
	@Instance(ModInfo.ID)
	public static TheBetweenlands INSTANCE;

	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;

	public static final Registries REGISTRIES = new Registries();

	public static DimensionType dimensionType;
	public static SimpleNetworkWrapper networkWrapper;
	public static Logger logger;

	public static boolean isToughAsNailsModInstalled = false;

	public static final String DIMENSION_NAME = "betweenlands";
	
	@EventHandler
	public void onFingerprintViolation(FMLFingerprintViolationEvent event) {
		Logger fpLogger = LogManager.getLogger("Betweenlands Fingerprint Violation");
		fpLogger.warn("Invalid fingerprint for The Betweenlands detected!");
		fpLogger.warn("The Betweenlands jar file was either tampered with or");
		fpLogger.warn("is corrupted. Please remove the file and (re)download");
		fpLogger.warn("the mod from the official CurseForge project at");
		fpLogger.warn("https://www.curseforge.com/minecraft/mc-mods/angry-pixel-the-betweenlands-mod");
    }
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		//Configuration File
		BetweenlandsConfig.path = event.getSuggestedConfigurationFile().getPath();

		BetweenlandsAPI.init();

		dimensionType = DimensionType.register(DIMENSION_NAME, "_betweenlands", BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId, WorldProviderBetweenlands.class, false);
		DimensionManager.registerDimension(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId, dimensionType);

		REGISTRIES.preInit();

		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);

		/// Network ///
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);

		MessageRegistry.preInit();

		//Renderers
		proxy.registerItemAndBlockRenderers();
		proxy.preInit();
		
		MinecraftForge.EVENT_BUS.register(BlockRegistry.class);
		MinecraftForge.EVENT_BUS.register(ItemRegistry.class);
		MinecraftForge.EVENT_BUS.register(RecipeRegistry.class);
		MinecraftForge.EVENT_BUS.register(BiomeRegistry.class);
		MinecraftForge.EVENT_BUS.register(SoundRegistry.class);
		MinecraftForge.EVENT_BUS.register(ElixirEffectRegistry.class);
	}

	@InstanceFactory
	public static TheBetweenlands createInstance() {
		TheBetweenlandsPreconditions.check();
		return new TheBetweenlands();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		REGISTRIES.init();

		proxy.init();

		this.registerEventHandlers();

		GameRegistry.registerWorldGenerator(new WorldGenDruidCircle(), 0);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();

		isToughAsNailsModInstalled = Loader.isModLoaded("toughasnails") || Loader.isModLoaded("ToughAsNails");

		/*if (ConfigHandler.DEBUG.debug) {
			System.out.println("==================================================");
			for (String name : unlocalizedNames) {
				System.out.println("needs translation: " + name);
			}
			System.out.println("==================================================");
		}*/
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBLEvent());
		event.registerServerCommand(new CommandResetAspects());
		//event.registerServerCommand(new CommandDecay());
		//event.registerServerCommand(new CommandFindPage());
		event.registerServerCommand(new CommandAspectDiscovery());
		/*if (ConfigHandler.DEBUG.debug) {
			event.registerServerCommand(new CommandTickSpeed());
		}*/
		GameruleRegistry.INSTANCE.onServerStarting(event);
		event.registerServerCommand(new CommandReloadRecipes());
	}

	/**
	 * Register event handlers here
	 */
	private void registerEventHandlers() {
		proxy.registerEventHandlers();

		WorldStorageImpl.register();
		
		MinecraftForge.EVENT_BUS.register(BetweenlandsConfig.class);
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
	}
}
