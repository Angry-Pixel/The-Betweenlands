package thebetweenlands.client.render.shader.base;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.vecmath.Matrix4f;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.client.util.JsonException;
import thebetweenlands.event.render.FogHandler;

public class CShaderInt extends Shader {
	private Matrix4f pProjectionMatrix;
	private List pListAuxFramebuffers;
	private List pListAuxNames;
	private List pListAuxWidths;
	private List pListAuxHeights;
	private final WorldShader wrapper;
	private final String shaderName;

	private static final Field f_listAuxFramebuffers;
	private static final Field f_listAuxNames;
	private static final Field f_listAuxWidths;
	private static final Field f_listAuxHeights;
	private static final Field f_projectionMatrix;

	static {
		f_listAuxFramebuffers = ReflectionHelper.findField(Shader.class, "listAuxFramebuffers", "field_148048_d", "d");
		f_listAuxFramebuffers.setAccessible(true);
		f_listAuxNames = ReflectionHelper.findField(Shader.class, "listAuxNames", "field_148049_e", "e");
		f_listAuxNames.setAccessible(true);
		f_listAuxWidths = ReflectionHelper.findField(Shader.class, "listAuxWidths", "field_148046_f", "f");
		f_listAuxWidths.setAccessible(true);
		f_listAuxHeights = ReflectionHelper.findField(Shader.class, "listAuxHeights", "field_148047_g", "g");
		f_listAuxHeights.setAccessible(true);
		f_projectionMatrix = ReflectionHelper.findField(Shader.class, "projectionMatrix", "field_148053_h", "h");
		f_projectionMatrix.setAccessible(true);
	}

	public CShaderInt(IResourceManager resourceLocation, String shaderName,
			Framebuffer frameBufferIn, Framebuffer frameBufferOut, WorldShader wrapper)
					throws JsonException {
		super(resourceLocation, shaderName, frameBufferIn, frameBufferOut);
		this.shaderName = shaderName;
		this.wrapper = wrapper;
		this.wrapper.addShader(this);
		try {
			this.pListAuxFramebuffers = (List) f_listAuxFramebuffers.get(this);
			this.pListAuxNames = (List) f_listAuxNames.get(this);
			this.pListAuxWidths = (List) f_listAuxWidths.get(this);
			this.pListAuxHeights = (List) f_listAuxHeights.get(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getName() {
		return this.shaderName;
	}

	public ShaderUniform getUniform(String name) {
		return this.getShaderManager().func_147991_a(name);
	}

	@Override
	public void loadShader(float partialTicks) {
		try {
			this.pProjectionMatrix = (Matrix4f) f_projectionMatrix.get(this);
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
		float framebufferTextureWidth = (float)this.framebufferOut.framebufferTextureWidth;
		float framebufferTextureHeight = (float)this.framebufferOut.framebufferTextureHeight;
		GL11.glViewport(0, 0, (int)framebufferTextureWidth, (int)framebufferTextureHeight);

		//Add samplers
		this.getShaderManager().func_147992_a("s_diffuse", this.framebufferIn);
		for(Entry<String, Object> samplerEntry : this.wrapper.getSamplers().entrySet()) {
			this.getShaderManager().func_147992_a("s_" + samplerEntry.getKey(), samplerEntry.getValue());
		}

		//Add auxiliary targets
		for (int i = 0; i < this.pListAuxFramebuffers.size(); ++i) {
			this.getShaderManager().func_147992_a((String)this.pListAuxNames.get(i), this.pListAuxFramebuffers.get(i));
			this.getShaderManager().func_147984_b("u_auxSize" + i).func_148087_a((float)((Integer)this.pListAuxWidths.get(i)).intValue(), (float)((Integer)this.pListAuxHeights.get(i)).intValue());
		}

		//Add other uniforms
		this.getShaderManager().func_147984_b("u_projMat").func_148088_a(this.pProjectionMatrix);
		this.getShaderManager().func_147984_b("u_inSize").func_148087_a((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
		this.getShaderManager().func_147984_b("u_outSize").func_148087_a(framebufferTextureWidth, framebufferTextureHeight);
		this.getShaderManager().func_147984_b("u_time").func_148090_a(partialTicks);
		Minecraft minecraft = Minecraft.getMinecraft();
		this.getShaderManager().func_147984_b("u_screenSize").func_148087_a((float)minecraft.displayWidth, (float)minecraft.displayHeight);
		//Just to make sure the correct fog mode is used in case another mod changes the fog mode after rendering the world
		this.getShaderManager().func_147984_b("u_fogMode").func_148090_a(FogHandler.INSTANCE.getCurrentFogMode());

		//Update shader
		this.wrapper.updateShader(this);

		//Upload samplers
		this.getShaderManager().func_147995_c();

		//Clear FBO
		this.framebufferOut.framebufferClear();
		this.framebufferOut.bindFramebuffer(false);

		//Just to make sure the correct fog values are used in case another mod changes the fog values after rendering the world
		GL11.glFogf(GL11.GL_FOG_START, FogHandler.INSTANCE.getCurrentFogStart());
		GL11.glFogf(GL11.GL_FOG_END, FogHandler.INSTANCE.getCurrentFogEnd());

		//Render texture over whole screen
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorOpaque_I(-1);
		tessellator.addVertex(0.0D, (double)framebufferTextureHeight, 500.0D);
		tessellator.addVertex((double)framebufferTextureWidth, (double)framebufferTextureHeight, 500.0D);
		tessellator.addVertex((double)framebufferTextureWidth, 0.0D, 500.0D);
		tessellator.addVertex(0.0D, 0.0D, 500.0D);
		tessellator.draw();

		//Unbind textures and FBO
		this.getShaderManager().func_147993_b();
		this.framebufferOut.unbindFramebuffer();
		this.framebufferIn.unbindFramebufferTexture();

		//Unbind auxiliary targets
		Iterator itAuxTargets = this.pListAuxFramebuffers.iterator();
		while (itAuxTargets.hasNext()) {
			Object object = itAuxTargets.next();
			if (object instanceof Framebuffer) {
				((Framebuffer)object).unbindFramebufferTexture();
			}
		}
	}
}
