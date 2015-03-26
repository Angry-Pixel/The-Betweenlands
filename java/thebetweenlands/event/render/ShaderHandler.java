package thebetweenlands.event.render;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thebetweenlands.client.render.shader.impl.MainShader;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ShaderHandler {
	public static final ShaderHandler INSTANCE = new ShaderHandler();
	
	private boolean failedLoading = false;
	private MainShader currentShader;
	private ShaderGroup currentShaderGroup;
	private Method renderHandMethod = null;

	public MainShader getShader() {
		return this.currentShader;
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPreRender(RenderWorldLastEvent event) {
		if(ConfigHandler.USE_SHADER && !this.failedLoading) {
			Minecraft mc = Minecraft.getMinecraft();
			if((this.currentShader == null || mc.entityRenderer.theShaderGroup == null || mc.entityRenderer.theShaderGroup != this.currentShaderGroup)
					&& mc.getFramebuffer() != null && mc.getResourceManager() != null && mc.getTextureManager() != null) {
				MainShader shaderWrapper = this.currentShader;
				if(this.currentShader == null) {
					shaderWrapper = new MainShader(
							mc.getTextureManager(),
							mc.getResourceManager(), mc.getFramebuffer(),
							new ResourceLocation("thebetweenlands:shaders/config/blmain.json"),
							new ResourceLocation("thebetweenlands:shaders/program/"),
							new ResourceLocation("thebetweenlands:textures/shader/")
							);
				}
				try {
					if(ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
						ShaderLinkHelper.setNewStaticShaderLinkHelper();
					}
					mc.entityRenderer.theShaderGroup = shaderWrapper.getShaderGroup();
					this.currentShaderGroup = mc.entityRenderer.theShaderGroup;
					this.currentShader = shaderWrapper;
					mc.entityRenderer.theShaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
				} catch (JsonException e) {
					this.failedLoading = true;
					System.err.println("Failed loading shader");
					e.printStackTrace();
				}
			}
			
			if(MainShader.getActiveShader() != null) {
				MainShader.getActiveShader().updateMatrices();
				MainShader.getActiveShader().updateDepthBuffer(Minecraft.getMinecraft().getFramebuffer());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPostRender(TickEvent.RenderTickEvent event) {
		if(ConfigHandler.USE_SHADER && event.phase == Phase.END) {
			if(MainShader.getActiveShader() != null) {
				MainShader.getActiveShader().clearLights();
			}
		}
	}
}
