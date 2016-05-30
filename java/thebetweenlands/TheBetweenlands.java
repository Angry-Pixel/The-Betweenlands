package thebetweenlands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.InstanceFactory;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.command.CommandAspectDiscovery;
import thebetweenlands.command.CommandBLEvent;
import thebetweenlands.command.CommandDecay;
import thebetweenlands.command.CommandFindPage;
import thebetweenlands.command.CommandResetAspects;
import thebetweenlands.command.CommandTickSpeed;
import thebetweenlands.entities.BLEntityRegistry;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.event.debugging.DebugHandlerChunkData;
import thebetweenlands.event.elixirs.ElixirCommonHandler;
import thebetweenlands.event.entity.AttackDamageHandler;
import thebetweenlands.event.entity.EntitySpawnHandler;
import thebetweenlands.event.entity.MiscEntitySyncHandler;
import thebetweenlands.event.entity.PageDiscoveringEvent;
import thebetweenlands.event.entity.PowerRingHandler;
import thebetweenlands.event.entity.RecruitmentRingHandler;
import thebetweenlands.event.entity.VolarPadGlideHandler;
import thebetweenlands.event.item.ItemEquipmentHandler;
import thebetweenlands.event.player.ArmorHandler;
import thebetweenlands.event.player.BonemealEventHandler;
import thebetweenlands.event.player.DecayEventHandler;
import thebetweenlands.event.player.OverworldItemEventHandler;
import thebetweenlands.event.player.FoodSicknessEventHandler;
import thebetweenlands.event.player.PlayerLanternEventHandler;
import thebetweenlands.event.player.PlayerLocationHandler;
import thebetweenlands.event.player.PlayerPortalHandler;
import thebetweenlands.event.player.RottenFoodHandler;
import thebetweenlands.event.player.SiltCrabClipHandler;
import thebetweenlands.event.world.EnvironmentEventHandler;
import thebetweenlands.herblore.elixirs.ElixirEffectRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.mods.RecurrentComplexModule;
import thebetweenlands.network.base.IPacket;
import thebetweenlands.network.base.SidedPacketHandler;
import thebetweenlands.network.base.impl.CommonPacketProxy;
import thebetweenlands.network.base.impl.IDPacketObjectSerializer;
import thebetweenlands.network.message.MessageLoadAspects;
import thebetweenlands.network.message.MessageOpenPouch;
import thebetweenlands.network.message.MessagePouchNaming;
import thebetweenlands.network.message.MessageSyncEnvironmentEvent;
import thebetweenlands.network.message.MessageWeedwoodRowboatInput;
import thebetweenlands.network.packet.client.PacketEquipment;
import thebetweenlands.network.packet.client.PacketFlightState;
import thebetweenlands.network.packet.client.PacketPlayIdleSound;
import thebetweenlands.network.packet.client.PacketRecruitmentState;
import thebetweenlands.network.packet.client.PacketRingInput;
import thebetweenlands.network.packet.server.PacketAttackTarget;
import thebetweenlands.network.packet.server.PacketDruidAltarProgress;
import thebetweenlands.network.packet.server.PacketDruidTeleportParticle;
import thebetweenlands.network.packet.server.PacketGemProc;
import thebetweenlands.network.packet.server.PacketPowerRingHit;
import thebetweenlands.network.packet.server.PacketRevengeTarget;
import thebetweenlands.network.packet.server.PacketSnailHatchParticle;
import thebetweenlands.network.packet.server.PacketTickspeed;
import thebetweenlands.network.packet.server.PacketWeedWoodBushRustle;
import thebetweenlands.precondition.TheBetweenlandsPreconditions;
import thebetweenlands.proxy.CommonProxy;
import thebetweenlands.recipes.BLRecipes;
import thebetweenlands.utils.PotionHelper;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.BLGamerules;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.biomes.base.BLBiomeRegistry;
import thebetweenlands.world.biomes.spawning.MobSpawnHandler;
import thebetweenlands.world.feature.structure.WorldGenDruidCircle;
import thebetweenlands.world.storage.chunk.BetweenlandsChunkData;
import thebetweenlands.world.storage.chunk.ChunkDataBase;
import thebetweenlands.world.storage.world.WorldDataBase;
import thebetweenlands.world.teleporter.TeleporterHandler;

@Mod(modid = ModInfo.ID, name = ModInfo.NAME, version = ModInfo.VERSION, guiFactory = ModInfo.CONFIG_GUI)
public class TheBetweenlands {
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
	public static File dir;
	private static byte nextPacketId = 0;

	public static ArrayList<String> unlocalizedNames = new ArrayList<>();
	public static boolean isShadersModInstalled = false;

	@EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		//Configuration File
		ConfigHandler.INSTANCE.loadConfig(event);
		dir = event.getModConfigurationDirectory();

		//BL Registry
		BLFluidRegistry.init();
		BLBlockRegistry.init();
		BLBiomeRegistry.init();
		BLItemRegistry.init();
		BLEntityRegistry.init();
		
		PotionHelper.initPotionArray();
		ElixirEffectRegistry.registerElixirs();

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
		networkWrapper.registerMessage(MessagePouchNaming.class, MessagePouchNaming.class, 9, Side.SERVER);
		networkWrapper.registerMessage(MessageOpenPouch.class, MessageOpenPouch.class, 10, Side.SERVER);
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
		registerPacket(PacketRingInput.class);
	}

	private static void registerPacket(Class<? extends IPacket> packetClass) {
		packetRegistry.registerPacket(packetClass, nextPacketId++);
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void init(FMLInitializationEvent event) {
		// Remove all the door recipes.
		// This is needed otherwise our doors will not be craftable due to the
		// recipe ordering
		List<IRecipe> doorRecipes = new ArrayList<IRecipe>();
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

		FMLCommonHandler.instance().bus().register(ConfigHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(DecayEventHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(BLFluidRegistry.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new ArmorHandler());
		MinecraftForge.EVENT_BUS.register(new OverworldItemEventHandler());
		MinecraftForge.EVENT_BUS.register(new PlayerLanternEventHandler());
		MinecraftForge.EVENT_BUS.register(DecayEventHandler.INSTANCE);
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
		MinecraftForge.EVENT_BUS.register(FoodSicknessEventHandler.INSTANCE);
		FMLCommonHandler.instance().bus().register(FoodSicknessEventHandler.INSTANCE);
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

		RecurrentComplexModule.init();
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
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
		} catch (ClassNotFoundException e) {}
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandBLEvent());
		event.registerServerCommand(new CommandResetAspects());
		event.registerServerCommand(new CommandDecay());
		event.registerServerCommand(new CommandFindPage());
		event.registerServerCommand(new CommandAspectDiscovery());
		if (ConfigHandler.DEBUG) {
			event.registerServerCommand(new CommandTickSpeed());
		}
		BLGamerules.INSTANCE.onServerStarting(event);
	}

	@InstanceFactory
	public static TheBetweenlands createInstance() {
		TheBetweenlandsPreconditions.check();
		return new TheBetweenlands();
	}
}
