package thebetweenlands;

import java.util.ArrayList;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import thebetweenlands.lib.ModInfo;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands {
	/*@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPROXY_LOCATION, serverSide = ModInfo.COMMONPROXY_LOCATION)
	public static CommonProxy proxy;

	@Instance(ModInfo.ID)
	public static TheBetweenlands instance;*/

	/// Network ///
	//	public static SimpleNetworkWrapper networkWrapper;
	//	public static final SidedPacketHandler sidedPacketHandler = new SidedPacketHandler();
	//	public static final IDPacketObjectSerializer packetRegistry = new IDPacketObjectSerializer();
	//	@SidedProxy(modId = ModInfo.ID, clientSide = ModInfo.CLIENTPACKETPROXY_LOCATION, serverSide = ModInfo.COMMONPACKETPROXY_LOCATION)
	//	public static CommonPacketProxy packetProxy;
	//	public static File dir;
	//	private static byte nextPacketId = 0;

	public static ArrayList<String> unlocalizedNames = new ArrayList<>();
	public static boolean isShadersModInstalled = false;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		/*//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);
		dir = event.getModConfigurationDirectory();

		//BL Registry
		BLFluidRegistry.init();
		BLBlockRegistry.init();
		BLBiomeRegistry.init();
		BLItemRegistry.init();
		BLEntityRegistry.init();

		GameRegistry.registerWorldGenerator(new WorldGenDruidCircle(), 0);

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

		DimensionManager.registerProviderType(ModInfo.DIMENSION_ID, WorldProviderBetweenlands.class, true);
		DimensionManager.registerDimension(ModInfo.DIMENSION_ID, ModInfo.DIMENSION_ID);

		//Message Registry
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
		networkWrapper.registerMessage(MessageSyncEnvironmentEvent.class, MessageSyncEnvironmentEvent.class, 4, Side.CLIENT);
		networkWrapper.registerMessage(MessageWeedwoodRowboatInput.class, MessageWeedwoodRowboatInput.class, 5, Side.SERVER);
		networkWrapper.registerMessage(MessageLoadAspects.class, MessageLoadAspects.class, 6, Side.CLIENT);
		BLEntityPropertiesRegistry.HANDLER.registerPacket(networkWrapper, 7);
		BetweenlandsChunkData.CHUNK_SYNC_HANDLER.registerPacket(networkWrapper, 8);

		sidedPacketHandler.setProxy(packetProxy).setNetworkWrapper(networkWrapper, 20, 21).setPacketSerializer(packetRegistry);

		//Packet Registry
		registerPacket(PacketDruidAltarProgress.class);
		registerPacket(PacketDruidTeleportParticle.class);
		registerPacket(PacketSnailHatchParticle.class);
		registerPacket(PacketTickspeed.class);
		registerPacket(PacketRevengeTarget.class);
		registerPacket(PacketAttackTarget.class);
		registerPacket(PacketWeedWoodBushRustle.class);
		registerPacket(PacketGemProc.class);
		registerPacket(PacketEquipment.class);
		registerPacket(PacketPowerRingHit.class);
		registerPacket(PacketRecruitmentState.class);
		registerPacket(PacketPlayIdleSound.class);
		registerPacket(PacketFlightState.class);
		registerPacket(PacketRingInput.class);*/
	}

	/*private static void registerPacket(Class<? extends IPacket> packetClass) {
		packetRegistry.registerPacket(packetClass, nextPacketId++);
	}*/

	@EventHandler
	public void init(FMLInitializationEvent event) {
		// Remove all the door recipes.
		// This is needed otherwise our doors will not be craftable due to the
		// recipe ordering
		/*List<IRecipe> doorRecipes = new ArrayList<IRecipe>();
		for (IRecipe recipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList())
			if (recipe != null) {
				ItemStack stack = recipe.getRecipeOutput();
				if (stack != null && stack.getItem() == Items.wooden_door)
					doorRecipes.add(recipe);
			}
		for (IRecipe recipe : doorRecipes)
			CraftingManager.getInstance().getRecipeList().remove(recipe);

		proxy.registerTileEntities();
		proxy.init();

		PotionHelper.initPotionArray();
		PotionHelper.registerPotions();

		FMLCommonHandler.instance().bus().register(ConfigHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(DecayEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLFluidRegistry.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new ArmorHandler());
		MinecraftForge.EVENT_BUS.register(new OverworldItemEventHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerLanternEventHandler());
		MinecraftForge.EVENT_BUS.register(DecayEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RottenFoodHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new PlayerPortalHandler());
		MinecraftForge.EVENT_BUS.register(PowerRingHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(PowerRingHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new PageDiscoveringEvent());
		FMLCommonHandler.instance().bus().register(EnvironmentEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EnvironmentEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new BonemealEventHandler());
		MinecraftForge.EVENT_BUS.register(new SiltCrabClipHandler());
		MinecraftForge.EVENT_BUS.register(new MiscEntitySyncHandler());
		MinecraftForge.EVENT_BUS.register(AttackDamageHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ElixirCommonHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ElixirCommonHandler.INSTANCE);
		BLEntityPropertiesRegistry.HANDLER.registerHandler();
		FMLCommonHandler.instance().bus().register(MobSpawnHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(MobSpawnHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(WorldDataBase.WORLD_UNLOAD_HANDLER);
		MinecraftForge.EVENT_BUS.register(BetweenlandsChunkData.CHUNK_SYNC_HANDLER);
		MinecraftForge.EVENT_BUS.register(PlayerItemEventHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(PlayerItemEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLItemRegistry.amulet);
		FMLCommonHandler.instance().bus().register(BLItemRegistry.amulet);
		MinecraftForge.EVENT_BUS.register(EntitySpawnHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ItemEquipmentHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(ItemEquipmentHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(DebugHandlerChunkData.INSTANCE);
		MinecraftForge.EVENT_BUS.register(ChunkDataBase.CHUNK_DATA_HANDLER);
		FMLCommonHandler.instance().bus().register(ChunkDataBase.CHUNK_DATA_HANDLER);
		MinecraftForge.EVENT_BUS.register(PlayerLocationHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(PlayerLocationHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RecruitmentRingHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(RecruitmentRingHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(BLItemRegistry.ringOfFlight);
		MinecraftForge.EVENT_BUS.register(new VolarPadGlideHandler());
		MinecraftForge.EVENT_BUS.register(BLItemRegistry.volarkite);

		BLRecipes.init();
		TeleporterHandler.init();

		// Add the other door recipes back
		CraftingManager.getInstance().getRecipeList().addAll(doorRecipes);

		RecurrentComplexModule.init();*/
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		/*proxy.postInit();
		if (ConfigHandler.DEBUG) {
			System.out.println("==================================================");
			for (String name : unlocalizedNames) {
				System.out.println("needs translation: " + name);
			}
			System.out.println("==================================================");
		}
		try {
			Class.forName("shadersmod.client.Shaders");
			isShadersModInstalled = true;
		} catch (ClassNotFoundException e) {}*/
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

	/*@InstanceFactory
	public static TheBetweenlands createInstance() {
		TheBetweenlandsPreconditions.check();
		return new TheBetweenlands();
	}*/
}
