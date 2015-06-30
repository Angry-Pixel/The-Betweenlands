package thebetweenlands.event.debugging;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.IntBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.gui.GuiDebugMenu;
import thebetweenlands.core.TheBetweenlandsClassTransformer;
import thebetweenlands.event.render.FogHandler;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.network.packets.PacketTickspeed;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToFindFieldException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DebugHandler {
	public static final DebugHandler INSTANCE = new DebugHandler();

	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0");

	private static final Field SLEEP_PER_TICK_FIELD;
	static {
		Field sleepPerTickField = null;
		try {
			sleepPerTickField = ReflectionHelper.findField(MinecraftServer.class, TheBetweenlandsClassTransformer.SLEEP_PER_TICK);
		} catch (UnableToFindFieldException e) {
			System.out.println("MinecraftServer was not transformed!");
		}
		SLEEP_PER_TICK_FIELD = sleepPerTickField;
	}

	private static Minecraft mc = Minecraft.getMinecraft();

	public String worldFolderName = ".debug_world";

	public String worldName = "Debug World";

	public boolean fullBright = false;

	private boolean fastFlight = false;

	public boolean denseFog = false;

	public boolean ignoreStart = true;

	private float lightTable[];

	public GuiScreen previousGuiScreen;

	public boolean isInDebugWorld = false;

	private boolean shouldRecreateBetweenlands = false;

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (!ConfigHandler.DEBUG || !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			return;
		}
		if (mc.theWorld != null) {
			if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
				fullBright = !fullBright;
				if (fullBright) {
					if (lightTable == null) {
						lightTable = new float[mc.theWorld.provider.lightBrightnessTable.length];
					}
					for (int i = 0; i < mc.theWorld.provider.lightBrightnessTable.length; i++) {
						lightTable[i] = mc.theWorld.provider.lightBrightnessTable[i];
					}
					for (int i = 0; i < mc.theWorld.provider.lightBrightnessTable.length; i++) {
						mc.theWorld.provider.lightBrightnessTable[i] = 1.0f;
					}
				} else {
					for (int i = 0; i < mc.theWorld.provider.lightBrightnessTable.length; i++) {
						mc.theWorld.provider.lightBrightnessTable[i] = lightTable[i];
					}
				}
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
				fastFlight = !fastFlight;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
				denseFog = !denseFog;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
				DecayManager.resetDecay(mc.thePlayer);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
				ignoreStart = !ignoreStart;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_U) && mc.isIntegratedServerRunning() && isInDebugWorld && !shouldRecreateBetweenlands) {
				shouldRecreateBetweenlands = true;
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_M)) {
			if (!(mc.currentScreen instanceof GuiDebugMenu)) {
				previousGuiScreen = mc.currentScreen;
				mc.displayGuiScreen(new GuiDebugMenu());
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
			saveAndLogImage(mc.getTextureMapBlocks().getGlTextureId(), "atlas-block.png", "block atlas");
			saveAndLogImage(mc.renderEngine.getTexture(TextureMap.locationItemsTexture).getGlTextureId(), "atlas-item.png", "item atlas");
		}
	}

	private void saveAndLogImage(int glTextureId, String fileName, String name) {
		File outFile = saveImage(glTextureId, fileName);
        ChatComponentText chatComponent = new ChatComponentText("Saved " + name);
		chatComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, outFile.getAbsolutePath()));
        chatComponent.getChatStyle().setUnderlined(true);
		mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
	}

	private File saveImage(int glTextureId, String file) {
		GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
		IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(4194304);
		GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH, dataBuffer);
		int width = dataBuffer.get();
		GL11.glGetTexLevelParameter(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT, dataBuffer);
		int height = dataBuffer.get();
		dataBuffer.clear();
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, dataBuffer);
		int[] pixels = new int[width * height];
		dataBuffer.get(pixels);
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		try {
			File out = new File(Minecraft.getMinecraft().mcDataDir.getCanonicalPath(), file);
			ImageIO.write(img, "png", out);
			return out;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (!ConfigHandler.DEBUG || event.phase == TickEvent.Phase.START) {
			return;
		}
		if (mc.thePlayer != null) {
			if (fastFlight) {
				mc.thePlayer.capabilities.setFlySpeed(1.0f);
			} else {
				mc.thePlayer.capabilities.setFlySpeed(0.1f);
			}
		}
		if (shouldRecreateBetweenlands && mc.isIntegratedServerRunning()) {
			shouldRecreateBetweenlands = false;
			WorldInfo worldInfo = mc.getIntegratedServer().worldServers[0].getWorldInfo();
			mc.loadWorld(null);
			ISaveFormat saveLoader = mc.getSaveLoader();
			saveLoader.flushCache();
			saveLoader.deleteWorldDirectory(DebugHandler.INSTANCE.worldFolderName + File.separatorChar + "DIM" + ConfigHandler.DIMENSION_ID);
			WorldSettings worldSettings = new WorldSettings(worldInfo);
			worldSettings.enableCommands();
			mc.launchIntegratedServer(DebugHandler.INSTANCE.worldFolderName, DebugHandler.INSTANCE.worldName, worldSettings);
			isInDebugWorld = true;
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if (ConfigHandler.DEBUG && !mc.gameSettings.showDebugInfo) {
			mc.fontRenderer.drawString("Debug", 2, 2, 0xFFFFFFFF);
			mc.fontRenderer.drawString("Decay: " + DecayManager.getDecayLevel(mc.thePlayer), 2, 10, 0xFFFFFFFF);
			mc.fontRenderer.drawString("Corruption: " + DecayManager.getCorruptionLevel(mc.thePlayer), 2, 18, 0xFFFFFFFF);
			float fog = FogHandler.INSTANCE.getCurrentFogStart() + (FogHandler.INSTANCE.getCurrentFogEnd() - FogHandler.INSTANCE.getCurrentFogStart()) / 2;
			mc.fontRenderer.drawString("Fog: " + DECIMAL_FORMAT.format(fog) + (FogHandler.INSTANCE.hasDenseFog() ? " (D)" : "") + (mc.thePlayer.posY < WorldProviderBetweenlands.LAYER_HEIGHT - 10 ? " (C)" : ""), 2, 26, 0xFFFFFFFF);
			float lightLevel = 0.0F;
			World world = mc.theWorld;
			if (world != null) {
				WorldProvider provider = world.provider;
				lightLevel += provider.lightBrightnessTable[0];
			}
			mc.fontRenderer.drawString("Base Light: " + lightLevel + (mc.thePlayer.posY < WorldProviderBetweenlands.LAYER_HEIGHT - 10 ? " (C)" : ""), 2, 34, 0xFFFFFFFF);
			String activeEvents = "";
			if (world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands) world.provider;
				EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
				for (EnvironmentEvent eevent : eeRegistry.getEvents().values()) {
					if (eevent.isActive()) {
						activeEvents += StatCollector.translateToLocal(eevent.getLocalizationEventName()) + ", ";
					}
				}
			}
			if (activeEvents.length() > 2) {
				activeEvents = activeEvents.substring(0, activeEvents.length() - 2);
			} else {
				activeEvents = "None";
			}
			mc.fontRenderer.drawString("Active events: " + activeEvents, 2, 42, 0xFFFFFFFF);
			mc.fontRenderer.drawString("Tick speed: " + DECIMAL_FORMAT.format(ClientProxy.debugTimer.getTicksPerSecond()), 2, 50, 0xFFFFFFFF);
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (!event.world.isRemote && event.entity instanceof EntityPlayerMP) {
			TheBetweenlands.networkWrapper.sendTo(TheBetweenlands.sidedPacketHandler.wrapPacket(new PacketTickspeed(1000F / getSleepPerTick())), (EntityPlayerMP) event.entity);
		}
	}

	public static long getSleepPerTick() {
		if (SLEEP_PER_TICK_FIELD == null) {
			return 50L;
		}
		try {
			return (long) SLEEP_PER_TICK_FIELD.get(MinecraftServer.getServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 50L;
	}

	public static void setSleepPerTick(long sleepPerTick) {
		if (SLEEP_PER_TICK_FIELD == null) {
			return;
		}
		try {
			SLEEP_PER_TICK_FIELD.set(MinecraftServer.getServer(), sleepPerTick);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void onMinecraftFinishedStarting() {
		if (ConfigHandler.DEBUG && ConfigHandler.DEBUG_MENU_ON_START) {
			mc.displayGuiScreen(new GuiDebugMenu());
		}
	}

	public static EntityPlayerMP createPlayerForUser(ServerConfigurationManager serverConfigurationManager, GameProfile gameProfile) {
		UUID uuid = EntityPlayer.func_146094_a(gameProfile);
		ArrayList sameUUIDPlayers = Lists.newArrayList();
		for (int i = 0; i < serverConfigurationManager.playerEntityList.size(); i++) {
			EntityPlayerMP player = (EntityPlayerMP) serverConfigurationManager.playerEntityList.get(i);
			if (player.getUniqueID().equals(uuid)) {
				sameUUIDPlayers.add(player);
			}
		}
		Iterator<EntityPlayerMP> playerIterator = sameUUIDPlayers.iterator();
		while (playerIterator.hasNext()) {
			EntityPlayerMP player = playerIterator.next();
			player.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
		}
		int dimensionId = INSTANCE.isInDebugWorld ? ConfigHandler.DIMENSION_ID : 0;
		MinecraftServer mcServer = serverConfigurationManager.getServerInstance();
		ItemInWorldManager itemInWorldManager;
		WorldServer world = mcServer.worldServerForDimension(dimensionId);
		if (mcServer.isDemo()) {
			itemInWorldManager = new DemoWorldManager(world);
		} else {
			itemInWorldManager = new ItemInWorldManager(world);
		}
		return new EntityPlayerMP(mcServer, world, gameProfile, itemInWorldManager);
	}

	@SubscribeEvent
	public void onWorldEventUnload(WorldEvent.Unload event) {
		if (isInDebugWorld) {
			isInDebugWorld = false;
		}
	}
}
