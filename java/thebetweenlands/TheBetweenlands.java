package thebetweenlands;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.entities.BLEntityRegistry;
import thebetweenlands.event.debugging.DebugHandler;
import thebetweenlands.event.player.DecayEventHandler;
import thebetweenlands.event.player.OctineArmorHandler;
import thebetweenlands.event.player.RottenFoodHandler;
import thebetweenlands.event.player.TorchPlaceEventHandler;
import thebetweenlands.event.render.FireflyHandler;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.event.render.ShaderHandler;
import thebetweenlands.event.render.WispHandler;
import thebetweenlands.event.world.ThemHandler;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.network.base.SidedPacketHandler;
import thebetweenlands.network.base.impl.CommonPacketProxy;
import thebetweenlands.network.base.impl.IDPacketObjectSerializer;
import thebetweenlands.network.message.MessageSyncPlayerDecay;
import thebetweenlands.network.message.MessageSyncWeather;
import thebetweenlands.network.packets.PacketAnimatorProgress;
import thebetweenlands.network.packets.PacketDruidAltarProgress;
import thebetweenlands.network.packets.PacketDruidTeleportParticle;
import thebetweenlands.network.packets.PacketSnailHatchParticle;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.recipes.RecipeHandler;
import thebetweenlands.utils.PotionHelper;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
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
	
	@Instance(ModInfo.ID)
	public static TheBetweenlands instance;

	/// Network ///
	public static SimpleNetworkWrapper networkWrapper;
	public static final SidedPacketHandler sidedPacketHandler = new SidedPacketHandler();
	public static final IDPacketObjectSerializer packetRegistry = new IDPacketObjectSerializer(); 
	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPACKETPROXY_LOCATION, serverSide = ModInfo.COMMONPACKETPROXY_LOCATION)
	public static CommonPacketProxy packetProxy;
	
	/**
	 * True for debug mode
	 * Keys:
	 * - F: Fullbright
	 * - C: Fast flight
	 */
	public static boolean DEBUG = true;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {

		//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);

		//BL Registry
		BLFluidRegistry.init();
		BLBlockRegistry.init();
		BLBiomeRegistry.init();
		BLItemRegistry.init();
		BLEntityRegistry.init();

		GameRegistry.registerWorldGenerator(new WorlGenDruidCircle(), 0);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
		DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);

		//Message Registry
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
        networkWrapper.registerMessage(MessageSyncPlayerDecay.class, MessageSyncPlayerDecay.class, 2, Side.CLIENT);
        networkWrapper.registerMessage(MessageSyncPlayerDecay.class, MessageSyncPlayerDecay.class, 3, Side.SERVER);
        networkWrapper.registerMessage(MessageSyncWeather.class, MessageSyncWeather.class, 4, Side.CLIENT);
        
        sidedPacketHandler.setProxy(packetProxy).setNetworkWrapper(networkWrapper, 20, 21).setPacketSerializer(packetRegistry);
        
        //Packet Registry
        packetRegistry.registerPacket(PacketAnimatorProgress.class, (byte) 0);
        packetRegistry.registerPacket(PacketDruidAltarProgress.class, (byte) 1);
        packetRegistry.registerPacket(PacketDruidTeleportParticle.class, (byte) 2);
        packetRegistry.registerPacket(PacketSnailHatchParticle.class, (byte) 3);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.registerTileEntities();
		proxy.preInit();

        PotionHelper.initPotionArray();
        PotionHelper.registerPotions();

		FMLCommonHandler.instance().bus().register(ConfigHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(DecayEventHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(ThemHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FogHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLFluidRegistry.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new OctineArmorHandler());
		MinecraftForge.EVENT_BUS.register(new TorchPlaceEventHandler());
		MinecraftForge.EVENT_BUS.register(DecayEventHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(WispHandler.INSTANCE);
        MinecraftForge.EVENT_BUS.register(FireflyHandler.INSTANCE);
        FMLCommonHandler.instance().bus().register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ShaderHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RottenFoodHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLItemRegistry.weedwoodBow);

		if(DEBUG) {
			FMLCommonHandler.instance().bus().register(DebugHandler.INSTANCE);
			MinecraftForge.EVENT_BUS.register(DebugHandler.INSTANCE);
		}

		RecipeHandler.init();
	}
}
