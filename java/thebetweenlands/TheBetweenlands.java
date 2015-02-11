package thebetweenlands;

import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.sound.BLSoundRegistry;
import thebetweenlands.entities.BLEntityRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.proxy.CommonProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class TheBetweenlands
{
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;

	@Instance(ModInfo.ID)
	public static TheBetweenlands instance;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		proxy.initRenderers();
		MinecraftForge.EVENT_BUS.register(new BLSoundRegistry());

		//BL Registry
		BLItemRegistry.init();
		BLBlockRegistry.init();
		BLEntityRegistry.init();

		// ConfigHandler.init(event.getSuggestedConfigurationFile()); -- Leave those there, we may need them.
	}

	@EventHandler
	public static void init(FMLInitializationEvent event) {
		//BL Registry
		proxy.registerTileEntities();

		//Reciepes.init();
		// For ores GameRegistry.registerWorldGenerator(new WORLDGENNAMEGOESHERE());
		//
		// new GuiHandler();

		// DimensionManager.registerProviderType(Universe.dimensionId, WorldProviderUniverse.class, false);
		// DimensionManager.registerDimension(Universe.dimensionId, Universe.dimensionId);
	}
}
