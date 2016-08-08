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
import net.minecraftforge.fml.common.registry.GameRegistry;
import thebetweenlands.api.BetweenlandsAPI;
import thebetweenlands.common.event.AnvilEventHandler;
import thebetweenlands.common.event.AspectTestHandler;
import thebetweenlands.common.item.tools.ItemBLShield;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.proxy.CommonProxy;
import thebetweenlands.common.registries.MessageRegistry;
import thebetweenlands.common.registries.Registries;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.feature.structure.WorldGenDruidCircle;
import thebetweenlands.common.world.storage.chunk.ChunkDataBase;
import thebetweenlands.core.TheBetweenlandsPreconditions;
import thebetweenlands.util.config.ConfigHandler;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands {
	public static final Registries REGISTRIES = new Registries();
	@Instance(ModInfo.ID)
	public static TheBetweenlands INSTANCE;
	public static DimensionType dimensionType;
	public static boolean isShadersModInstalled = false;
	public static SimpleNetworkWrapper networkWrapper;
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;
	public static File sourceFile;
	public static ArrayList<String> unlocalizedNames = new ArrayList<>();
	private static File configDir;
	private static int nextMessageId;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);
		configDir = event.getModConfigurationDirectory();
		sourceFile = event.getSourceFile();

		BetweenlandsAPI.init(new BetweenlandsAPIImp());

		dimensionType = DimensionType.register("Betweenlands", "", ConfigHandler.dimensionId, WorldProviderBetweenlands.class, false);

		REGISTRIES.preInit();

		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, proxy);

		//DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
		//DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);*/

		/// Network ///
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);

		MessageRegistry.preInit();

		//Renderers
		proxy.registerItemAndBlockRenderers();
		proxy.preInit();
	}

	@InstanceFactory
	public static TheBetweenlands createInstance() {
		TheBetweenlandsPreconditions.check();
		return new TheBetweenlands();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		REGISTRIES.init();
		MinecraftForge.EVENT_BUS.register(new AnvilEventHandler());

		proxy.init();

		this.registerEventHandlers();

		GameRegistry.registerWorldGenerator(new WorldGenDruidCircle(), 0);


		//TODO: Test
		/*StringWriter strWriter = new StringWriter();
		JsonWriter jsonWriter = new JsonWriter(strWriter);
		ModelConverter converter = new ModelConverter(new ModelSundew(), 0.065D, false);
		Model model = converter.getModel();
		for(Box box : model.getBoxes()) {
			for(AlignedQuad quad : box.getAlignedQuads()) {
				System.out.println("C: " + quad.x + " " + quad.y + " " + quad.z + " D: " + quad.width + " " + quad.height + " R: " + quad.rx + " " + quad.ry + " " + quad.rz);
			}
		}*/
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

	/**
	 * Register event handlers here
	 */
	private void registerEventHandlers() {
		proxy.registerEventHandlers();

		MinecraftForge.EVENT_BUS.register(ChunkDataBase.CHUNK_DATA_HANDLER);
		MinecraftForge.EVENT_BUS.register(ItemBLShield.EventHandler.INSTANCE);
		
		MinecraftForge.EVENT_BUS.register(AspectTestHandler.INSTANCE);
	}
}
