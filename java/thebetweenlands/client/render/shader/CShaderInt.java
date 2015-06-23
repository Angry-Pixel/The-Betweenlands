package thebetweenlands.client.render.shader;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.client.util.JsonException;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Matrix4f;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class CShaderInt extends Shader {
	private CShaderManager pShaderManager;
	private Matrix4f pProjectionMatrix;
	private List pListAuxFramebuffers;
	private List pListAuxNames;
	private List pListAuxWidths;
	private List pListAuxHeights;
	private final CShader wrapper;
	private final String shaderName;
	
	public CShaderInt(IResourceManager resourceLocation, String shaderName,
			Framebuffer frameBufferIn, Framebuffer frameBufferOut, CShader wrapper)
					throws JsonException {
		super(resourceLocation, shaderName, frameBufferIn, frameBufferOut);
		this.shaderName = shaderName;
		this.wrapper = wrapper;
		this.wrapper.addShader(this);
		try {
			{
				this.pShaderManager = new CShaderManager(resourceLocation, shaderName);
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "manager", "field_148051_c", "c");
				f.setAccessible(true);
				f.set(this, this.pShaderManager);
			}
			{
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "listAuxFramebuffers", "field_148048_d", "d");
				f.setAccessible(true);
				this.pListAuxFramebuffers = (List) f.get(this);
			}
			{
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "listAuxNames", "field_148049_e", "e");
				f.setAccessible(true);
				this.pListAuxNames = (List) f.get(this);
			}
			{
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "listAuxWidths", "field_148046_f", "f");
				f.setAccessible(true);
				this.pListAuxWidths = (List) f.get(this);
			}
			{
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "listAuxHeights", "field_148047_g", "g");
				f.setAccessible(true);
				this.pListAuxHeights = (List) f.get(this);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getName() {
		return this.shaderName;
	}
	
	public ShaderUniform getUniform(String name) {
		return this.pShaderManager.func_147991_a(name);
	}
	
	@Override
	public void loadShader(float partialTicks) {
		try {
			{
				Field f = ReflectionHelper.findField(this.getClass().getSuperclass(), "projectionMatrix", "field_148053_h", "h");
				f.setAccessible(true);
				this.pProjectionMatrix = (Matrix4f) f.get(this);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}

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

		for(Entry<String, Object> samplerEntry : this.wrapper.getSamplers().entrySet()) {
			this.pShaderManager.func_147992_a(samplerEntry.getKey(), samplerEntry.getValue());
		}

		for (int i = 0; i < this.pListAuxFramebuffers.size(); ++i) {
			this.pShaderManager.func_147992_a((String)this.pListAuxNames.get(i), this.pListAuxFramebuffers.get(i));
			this.pShaderManager.func_147984_b("AuxSize" + i).func_148087_a((float)((Integer)this.pListAuxWidths.get(i)).intValue(), (float)((Integer)this.pListAuxHeights.get(i)).intValue());
		}

		this.pShaderManager.func_147984_b("ProjMat").func_148088_a(this.pProjectionMatrix);
		this.pShaderManager.func_147984_b("InSize").func_148087_a((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
		this.pShaderManager.func_147984_b("OutSize").func_148087_a(f1, f2);
		this.pShaderManager.func_147984_b("Time").func_148090_a(partialTicks);
		Minecraft minecraft = Minecraft.getMinecraft();
		this.pShaderManager.func_147984_b("ScreenSize").func_148087_a((float)minecraft.displayWidth, (float)minecraft.displayHeight);
		this.wrapper.updateShader(this);
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
