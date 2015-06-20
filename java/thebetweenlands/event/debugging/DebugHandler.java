package thebetweenlands.event.debugging;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import org.lwjgl.input.Keyboard;

import thebetweenlands.event.render.FogHandler;
import thebetweenlands.manager.DecayManager;
import thebetweenlands.utils.confighandler.ConfigHandler;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DebugHandler {
	public static final DebugHandler INSTANCE = new DebugHandler();

	/////// DEBUG ///////
	public boolean fullBright = false;
	private boolean fastFlight = false;
	public boolean denseFog = false;
	private float lightTable[];
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if(!ConfigHandler.DEBUG || Minecraft.getMinecraft().theWorld == null) return;
		if(Keyboard.isKeyDown(Keyboard.KEY_F)){
			this.fullBright = !this.fullBright;
			if(this.fullBright) {
				if(this.lightTable == null) {
					this.lightTable = new float[Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length];
				}
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					this.lightTable[i] = Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i];
				}
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i] = 1.0f;
				}
			} else {
				for(int i = 0; i < Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable.length; i++) {
					Minecraft.getMinecraft().theWorld.provider.lightBrightnessTable[i] = this.lightTable[i];
				}
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_C)) {
			this.fastFlight = !this.fastFlight;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			this.denseFog = !this.denseFog;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			DecayManager.resetDecay(Minecraft.getMinecraft().thePlayer);
		}
		/*if(Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			try {
				Minecraft mc = Minecraft.getMinecraft();
				if(mc.entityRenderer.theShaderGroup == null) {
					CShader shaderWrapper = new MainShader(
							mc.getTextureManager(),
							mc.getResourceManager(), mc.getFramebuffer(),
							new ResourceLocation("thebetweenlands:shaders/config/blmain.json"),
							new ResourceLocation("thebetweenlands:shaders/program/"),
							new ResourceLocation("thebetweenlands:textures/shader/")
							);
					this.currentShader = shaderWrapper;
					mc.entityRenderer.theShaderGroup = shaderWrapper.getShaderGroup();
					mc.entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
					this.useShader = true;
				} else {
					mc.entityRenderer.deactivateShader();
					this.useShader = false;
				}
			} catch(Exception ex) {
				this.useShader = false;
				ex.printStackTrace();
			}
		}*/
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTick(TickEvent event) {
		if(!ConfigHandler.DEBUG || Minecraft.getMinecraft().thePlayer == null) return;
		if(this.fastFlight) {
			Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(1.0f);
		} else {
			Minecraft.getMinecraft().thePlayer.capabilities.setFlySpeed(0.1f);
		}
	}
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderOverlay(RenderGameOverlayEvent.Text event) {
		if (ConfigHandler.DEBUG && !Minecraft.getMinecraft().gameSettings.showDebugInfo) {
			Minecraft.getMinecraft().fontRenderer.drawString("Debug", 2, 2, 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawString("Decay: " + DecayManager.getDecayLevel(Minecraft.getMinecraft().thePlayer), 2, 10, 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawString("Corruption: " + DecayManager.getCorruptionLevel(Minecraft.getMinecraft().thePlayer), 2, 18, 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRenderer.drawString("Fog: " + (float)(FogHandler.INSTANCE.getCurrentFogStart() + (FogHandler.INSTANCE.getCurrentFogEnd() - FogHandler.INSTANCE.getCurrentFogStart()) / 2.0D) + (FogHandler.INSTANCE.hasDenseFog() ? " (D)" : "") + (Minecraft.getMinecraft().thePlayer.posY < (WorldProviderBetweenlands.LAYER_HEIGHT - 10) ? " (C)" : ""), 2, 26, 0xFFFFFFFF);
			float lightLevel = 0.0F;
			World world = Minecraft.getMinecraft().theWorld;
			if(world != null) {
				WorldProvider provider = world.provider;
				lightLevel += provider.lightBrightnessTable[0];
			}
			Minecraft.getMinecraft().fontRenderer.drawString("Base Light: " + lightLevel + (Minecraft.getMinecraft().thePlayer.posY < (WorldProviderBetweenlands.LAYER_HEIGHT - 10) ? " (C)" : ""), 2, 34, 0xFFFFFFFF);
			String activeEvents = "";
			if(world.provider instanceof WorldProviderBetweenlands) {
				WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
				EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
				for(EnvironmentEvent eevent : eeRegistry.getEvents().values()) {
					if(eevent.isActive()) {
						activeEvents += eevent.getEventName() + ", ";
					}
				}
			}
			if(activeEvents.length() > 2) {
				activeEvents = activeEvents.substring(0, activeEvents.length() - 2);
			} else {
				activeEvents = "None";
			}
			Minecraft.getMinecraft().fontRenderer.drawString("Active events: " + activeEvents, 2, 42, 0xFFFFFFFF);
		}
	}
}
