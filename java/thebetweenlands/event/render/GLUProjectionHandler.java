package thebetweenlands.event.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thebetweenlands.utils.GLUProjection;

public class GLUProjectionHandler {
	public static final GLUProjectionHandler INSTANCE = new GLUProjectionHandler();
	
	private final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
	private final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
	private final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, this.modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, this.projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, this.viewport);
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		GLUProjection.getInstance().updateMatrices(this.viewport, this.modelview, this.projection, 
				(float)sr.getScaledWidth() / (float)Minecraft.getMinecraft().displayWidth, 
				(float)sr.getScaledHeight() / (float)Minecraft.getMinecraft().displayHeight);
	}
}
