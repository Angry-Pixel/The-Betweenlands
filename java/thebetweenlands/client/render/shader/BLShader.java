package thebetweenlands.client.render.shader;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.util.JsonException;

import org.lwjgl.opengl.GL11;

import thebetweenlands.event.debugging.DebugHandler;

public class BLShader extends Shader {
	private BLShaderManager pShaderManager;
	private Matrix4f pProjectionMatrix;
	private List pListAuxFramebuffers;
	private List pListAuxNames;
	private List pListAuxWidths;
	private List pListAuxHeights;

	public BLShader(IResourceManager resourceLocation, String shaderName,
			Framebuffer frameBufferIn, Framebuffer frameBufferOut)
					throws JsonException {
		super(resourceLocation, shaderName, frameBufferIn, frameBufferOut);
		try {
			//Big reflection mess, sorry xD
			{
				this.pShaderManager = new BLShaderManager(resourceLocation, shaderName);
				Field f = this.getClass().getSuperclass().getDeclaredField("manager");
				f.setAccessible(true);
				f.set(this, this.pShaderManager);
			}
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("listAuxFramebuffers");
				f.setAccessible(true);
				this.pListAuxFramebuffers = (List) f.get(this);
			}
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("listAuxNames");
				f.setAccessible(true);
				this.pListAuxNames = (List) f.get(this);
			}
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("listAuxWidths");
				f.setAccessible(true);
				this.pListAuxWidths = (List) f.get(this);
			}
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("listAuxHeights");
				f.setAccessible(true);
				this.pListAuxHeights = (List) f.get(this);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void loadShader(float partialTicks) {
		try {
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("projectionMatrix");
				f.setAccessible(true);
				this.pProjectionMatrix = (Matrix4f) f.get(this);
			}
			{
				Field f = this.getClass().getSuperclass().getDeclaredField("manager");
				f.setAccessible(true);
				this.pShaderManager = (BLShaderManager) f.get(this);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		///////////////// COPY DEPTH BUFFER ///////////////////
		DebugHandler.depthBuffer.bindFramebuffer(false);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.pShaderManager.depthBuffer.framebufferTexture);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
				this.pShaderManager.depthBuffer.framebufferTextureWidth, 
				this.pShaderManager.depthBuffer.framebufferTextureHeight, 
				0);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		this.framebufferIn.unbindFramebuffer();
		float f1 = (float)this.framebufferOut.framebufferTextureWidth;
		float f2 = (float)this.framebufferOut.framebufferTextureHeight;
		GL11.glViewport(0, 0, (int)f1, (int)f2);
		this.pShaderManager.func_147992_a("DiffuseSampler", this.framebufferIn);

		//////////////////// ADD DEPTH SAMPLER ///////////////////////
		this.pShaderManager.func_147992_a("DSampler", this.pShaderManager.depthBuffer);

		for (int i = 0; i < this.pListAuxFramebuffers.size(); ++i)
		{
			System.out.println("Adding sampler: " + (String)this.pListAuxNames.get(i));
			this.pShaderManager.func_147992_a((String)this.pListAuxNames.get(i), this.pListAuxFramebuffers.get(i));
			this.pShaderManager.func_147984_b("AuxSize" + i).func_148087_a((float)((Integer)this.pListAuxWidths.get(i)).intValue(), (float)((Integer)this.pListAuxHeights.get(i)).intValue());
		}

		this.pShaderManager.func_147984_b("ProjMat").func_148088_a(this.pProjectionMatrix);
		this.pShaderManager.func_147984_b("InSize").func_148087_a((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
		this.pShaderManager.func_147984_b("OutSize").func_148087_a(f1, f2);
		this.pShaderManager.func_147984_b("Time").func_148090_a(partialTicks);
		Minecraft minecraft = Minecraft.getMinecraft();
		this.pShaderManager.func_147984_b("ScreenSize").func_148087_a((float)minecraft.displayWidth, (float)minecraft.displayHeight);
		this.pShaderManager.func_147995_c();
		this.framebufferOut.framebufferClear();
		this.framebufferOut.bindFramebuffer(false);

		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(-1);
		tessellator.addVertex(0.0D, (double)f2, 500.0D);
		tessellator.addVertex((double)f1, (double)f2, 500.0D);
		tessellator.addVertex((double)f1, 0.0D, 500.0D);
		tessellator.addVertex(0.0D, 0.0D, 500.0D);
		tessellator.draw();
		this.pShaderManager.func_147993_b();
		this.framebufferOut.unbindFramebuffer();
		this.framebufferIn.unbindFramebufferTexture();

		Iterator iterator = this.pListAuxFramebuffers.iterator();

		while (iterator.hasNext())
		{
			Object object = iterator.next();

			if (object instanceof Framebuffer)
			{
				((Framebuffer)object).unbindFramebufferTexture();
			}
		}
	}
}
