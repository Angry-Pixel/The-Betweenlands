package thebetweenlands.event.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.impl.GeometryBuffer;
import thebetweenlands.client.render.tileentity.TileEntityWispRenderer;
import thebetweenlands.tileentities.TileEntityWisp;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Map.Entry;

public class WispHandler {
	public static final WispHandler INSTANCE = new WispHandler();
	
	public ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>> tileList = new ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>>();
	
	//TODO: Temporary solution for the water rendering order, fixed in 1.8
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		GL11.glPushMatrix();
		for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
			Vector3d pos = e.getValue();
			e.getKey().getKey().doRender(e.getKey().getValue(), pos.x, pos.y, pos.z, event.partialTicks);
		}
		GL11.glPopMatrix();
		
		
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
		GL11.glColor4f(0.0F, 0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F, 0.8F, 1.0F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		
		GeometryBuffer gBuffer = ShaderHelper.INSTANCE.getCurrentShader().getGeometryBuffer("GBuffer1");
		gBuffer.bind();
		gBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);
		
		//Render to G-Buffer 1
		for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
			Vector3d pos = e.getValue();
			GL11.glPushMatrix();
			GL11.glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
			new Sphere().draw(6, 20, 20);
			GL11.glPopMatrix();
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		
		this.tileList.clear();
	}
}
