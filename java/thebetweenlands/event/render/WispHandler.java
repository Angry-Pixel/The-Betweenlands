package thebetweenlands.event.render;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.client.render.shader.GeometryBuffer;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.tileentity.TileEntityWispRenderer;
import thebetweenlands.entities.particles.EntityWispFX;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.utils.MathUtils;

public class WispHandler {
	public static final WispHandler INSTANCE = new WispHandler();

	public final ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>> tileList = new ArrayList<Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d>>();

	private int sphereDispList = -2;

	//TODO: Temporary solution for the water rendering order, fixed in 1.8
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderWorld(RenderWorldLastEvent event) {
		GL11.glPushMatrix();
		for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
			Vector3d pos = e.getValue();
			e.getKey().getKey().doRender(e.getKey().getValue(), pos.x, pos.y, pos.z, event.partialTicks);

			double rx = pos.x + RenderManager.renderPosX + 0.5D;
			double ry = pos.y + RenderManager.renderPosY + 0.5D;
			double rz = pos.z + RenderManager.renderPosZ + 0.5D;

			float size = (1.0F - MathHelper.sin(MathUtils.PI / 16 * MathHelper.clamp_float(EntityWispFX.getDistanceToViewer(rx, ry, rz, event.partialTicks), 10, 20))) * 1.2F;

			int wispMeta = e.getKey().getValue().getBlockMetadata();
			for(int i = 0; i < 2; i++) {
				int color = BLBlockRegistry.wisp.colors[wispMeta + i];
				float r = (color >> 16 & 0xFF) / 255F;
				float g = (color >> 8 & 0xFF) / 255F;
				float b = (color & 0xFF) / 255F;
				if(ShaderHelper.INSTANCE.canUseShaders()) {
					ShaderHelper.INSTANCE.addDynLight(new LightSource(rx, ry, rz, 
							i == 0 ? size : size * 0.5F,
									r * (i == 0 ? 3.5F : 1.0F),
									g * (i == 0 ? 3.5F : 1.0F),
									b * (i == 0 ? 3.5F : 1.0F)));
				}
			}
		}
		GL11.glPopMatrix();

		/*if(this.sphereDispList == -2) {
			this.sphereDispList = GL11.glGenLists(1);
			GL11.glNewList(this.sphereDispList, GL11.GL_COMPILE);
			new Sphere().draw(6, 30, 30);
			GL11.glEndList();
		}

		if(ShaderHelper.INSTANCE.canUseShaders() && this.sphereDispList >= 0) {
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);
			GL11.glColor4f(0.0F, 0.4F + (float)(Math.sin(System.nanoTime() / 500000000.0F) + 1.0F) * 0.2F, 0.8F - (float)(Math.cos(System.nanoTime() / 400000000.0F) + 1.0F) * 0.2F, 1.0F);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);

			MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
			if(shader != null) {
				GeometryBuffer gBuffer = shader.getGeometryBuffer("repellerShield");
				gBuffer.bind();
				gBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);

				//Render to G-Buffer 1
				for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
					Vector3d pos = e.getValue();
					GL11.glPushMatrix();
					GL11.glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
					GL11.glCallList(this.sphereDispList);
					GL11.glPopMatrix();
				}
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);

			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(true);
		} else if(this.sphereDispList >= 0) {
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.3F);
			for(Entry<Entry<TileEntityWispRenderer, TileEntityWisp>, Vector3d> e : this.tileList) {
				Vector3d pos = e.getValue();
				GL11.glPushMatrix();
				GL11.glTranslated(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5);
				GL11.glCallList(this.sphereDispList);
				GL11.glPopMatrix();
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}*/

		//TODO: Remove this once the repeller shield is implemented!!!
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
			if(shader != null) {
				GeometryBuffer repellerShieldBuffer = shader.getGeometryBuffer("repellerShield");
				repellerShieldBuffer.bind();
				repellerShieldBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);
				GeometryBuffer gasParticlesBuffer = shader.getGeometryBuffer("gasParticles");
				gasParticlesBuffer.bind();
				gasParticlesBuffer.clear(0.0F, 0.0F, 0.0F, 0.0F);
				Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
			}
		}

		this.tileList.clear();
	}
}
