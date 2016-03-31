package thebetweenlands.common;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.InstanceFactory;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.base.SidedPacketHandler;
import thebetweenlands.common.network.base.impl.CommonPacketProxy;
import thebetweenlands.common.network.base.impl.IDPacketObjectSerializer;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.Registries;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.utils.config.ConfigHandler;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands {
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;

	@Instance(ModInfo.ID)
	public static TheBetweenlands instance;

	/// Network ///
	public static SimpleNetworkWrapper networkWrapper;
	public static final SidedPacketHandler SIDED_PACKET_HANDLER = new SidedPacketHandler();
	public static final IDPacketObjectSerializer PACKET_REGISTRY = new IDPacketObjectSerializer();
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPACKETPROXY_LOCATION, serverSide = ModInfo.COMMONPACKETPROXY_LOCATION)
	public static CommonPacketProxy packetProxy;

	private static File configDir;

	public static ArrayList<String> unlocalizedNames = new ArrayList<>();
	public static boolean isShadersModInstalled = false;

	public static final Registries REGISTRIES = new Registries();

	public static DimensionType dimensionType;
	
	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);
		configDir = event.getModConfigurationDirectory();

		dimensionType = DimensionType.register("Betweenlands", "", ConfigHandler.DIMENSION_ID, WorldProviderBetweenlands.class, false);
		
		REGISTRIES.preInit();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		//DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
		//DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);*/

		/// Network ///
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
		SIDED_PACKET_HANDLER.setProxy(packetProxy).setNetworkWrapper(networkWrapper, 20, 21).setPacketSerializer(PACKET_REGISTRY);

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		REGISTRIES.init();

		proxy.init();

		this.registerEventHandlers();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();

		/*if (ConfigHandler.DEBUG) {
			System.out.println("==================================================");
			for (String name : unlocalizedNames) {
				System.out.println("needs translation: " + name);
			}
			System.out.println("==================================================");
		}*/
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		/*event.registerServerCommand(new CommandBLEvent());
		event.registerServerCommand(new CommandResetAspects());
		event.registerServerCommand(new CommandDecay());
		event.registerServerCommand(new CommandFindPage());
		event.registerServerCommand(new CommandAspectDiscovery());
		if (ConfigHandler.DEBUG) {
			event.registerServerCommand(new CommandTickSpeed());
		}
		BLGamerules.INSTANCE.onServerStarting(event);*/
	}

	@InstanceFactory
	public static TheBetweenlands createInstance() {
		//TheBetweenlandsPreconditions.check();
		return new TheBetweenlands();
	}

	/**
	 * Register event handlers here
	 */
	private void registerEventHandlers() {
		proxy.registerEventHandlers();
		
		MinecraftForge.EVENT_BUS.register(ChunkDataBase.CHUNK_DATA_HANDLER);
	}
}
