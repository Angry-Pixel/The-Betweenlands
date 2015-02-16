package thebetweenlands;

import net.minecraftforge.common.DimensionManager;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.entities.BLEntityRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.handler.AltarPacketHandler;
import thebetweenlands.network.packet.AltarCraftingProgressMessage;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.BLBiomeRegistry;
import thebetweenlands.world.feature.structure.WorlGenDruidCircle;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands
{
    @SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
    public static CommonProxy proxy;
    public static SimpleNetworkWrapper networkWrapper;

    @Instance(ModInfo.ID)
    public static TheBetweenlands instance;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent event) {

        //Configuration File
        ConfigHandler.INSTANCE.loadConfig(event);

        //BL Registry
        BLItemRegistry.init();
        BLBlockRegistry.init();
        BLEntityRegistry.init();
        BLBiomeRegistry.init();

        GameRegistry.registerWorldGenerator(new WorlGenDruidCircle(), 0);
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        //TODO: Just temporary to test some stuff
        DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
        DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);

        // Simple Altar packet
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("thebetweenlands");
        networkWrapper.registerMessage(AltarPacketHandler.class, AltarCraftingProgressMessage.class, 0, Side.CLIENT);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        //BL Registry
        proxy.registerTileEntities();
        proxy.registerRenderInformation();
        FMLCommonHandler.instance().bus().register(ConfigHandler.INSTANCE);
        //Reciepes.init();
        // For ores GameRegistry.registerWorldGenerator(new WORLDGENNAMEGOESHERE());

        // DimensionManager.registerProviderType(Universe.dimensionId, WorldProviderUniverse.class, false);
        // DimensionManager.registerDimension(Universe.dimensionId, Universe.dimensionId);
    }
}
