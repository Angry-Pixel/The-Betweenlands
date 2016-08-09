package thebetweenlands.client.render.shader.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
		//TODO: Mappings!
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
					throws IOException {
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

	/**
	 * Returns the name of this shader
	 * @return
	 */
	public String getName() {
		return this.shaderName;
	}

	/**
	 * Returns a uniform with the specified name.
	 * Returns null if no such uniform was found
	 * @param name
	 * @return
	 */
	public ShaderUniform getUniform(String name) {
		return this.getShaderManager().getShaderUniform(name);
	}

	/**
	 * Updates the uniform with the specified name
	 * @param name
	 */
	public void updateUniform(String name, Consumer<ShaderUniform> consumer) {
		ShaderUniform uniform = this.getUniform(name);
		if(uniform != null)
			consumer.accept(uniform);
	}

	@Override
	public void loadShader(float time) {
		try {
			this.pProjectionMatrix = (Matrix4f) f_projectionMatrix.get(this);
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();
		GlStateManager.disableDepth();
		GlStateManager.disableAlpha();
		GlStateManager.disableFog();
		GlStateManager.disableLighting();
		GlStateManager.disableColorMaterial();
		GlStateManager.enableTexture2D();
		GlStateManager.bindTexture(0);

		this.framebufferIn.unbindFramebuffer();
		float framebufferTextureWidth = (float)this.framebufferOut.framebufferTextureWidth;
		float framebufferTextureHeight = (float)this.framebufferOut.framebufferTextureHeight;
		GlStateManager.viewport(0, 0, (int)framebufferTextureWidth, (int)framebufferTextureHeight);

		//Add samplers
		this.getShaderManager().addSamplerTexture("s_diffuse", this.framebufferIn);
		for(Entry<String, Object> samplerEntry : this.wrapper.getSamplers().entrySet()) {
			this.getShaderManager().addSamplerTexture("s_" + samplerEntry.getKey(), samplerEntry.getValue());
		}

		//Add auxiliary targets
		for (int i = 0; i < this.pListAuxFramebuffers.size(); ++i) {
			this.getShaderManager().addSamplerTexture((String)this.pListAuxNames.get(i), this.pListAuxFramebuffers.get(i));
			this.getShaderManager().getShaderUniformOrDefault("u_auxSize" + i).set((float)((Integer)this.pListAuxWidths.get(i)).intValue(), (float)((Integer)this.pListAuxHeights.get(i)).intValue());
		}

		//Add other uniforms
		this.getShaderManager().getShaderUniformOrDefault("u_projMat").set(this.pProjectionMatrix);
		this.getShaderManager().getShaderUniformOrDefault("u_inSize").set((float)this.framebufferIn.framebufferTextureWidth, (float)this.framebufferIn.framebufferTextureHeight);
		this.getShaderManager().getShaderUniformOrDefault("u_outSize").set(framebufferTextureWidth, framebufferTextureHeight);
		this.getShaderManager().getShaderUniformOrDefault("u_time").set(time);
		Minecraft minecraft = Minecraft.getMinecraft();
		this.getShaderManager().getShaderUniformOrDefault("u_screenSize").set((float)minecraft.displayWidth, (float)minecraft.displayHeight);
		//Just to make sure the correct fog mode is used in case another mod changes the fog mode after rendering the world
		//TODO: Requires fog implementation
		this.getShaderManager().getShaderUniformOrDefault("u_fogMode").set(/*FogHandler.INSTANCE.getCurrentFogMode()*/GL11.GL_EXP);

		//Update shader
		//TODO: Check this later
		this.wrapper.updateShader(this);

		//Upload samplers
		this.getShaderManager().useShader();
		//Not sure why this is suddenly required in 1.10.2, but it seems to be necessary to prevent a stack overflow!??
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		//this.framebufferIn.bindFramebufferTexture();
		//Clear FBO
		this.framebufferOut.framebufferClear();
		this.framebufferOut.bindFramebuffer(false);

		//Just to make sure the correct fog values are used in case another mod changes the fog values after rendering the world
		//TODO: Requires fog implementation
		GlStateManager.setFogStart(/*FogHandler.INSTANCE.getCurrentFogStart()*/6);
		GlStateManager.setFogEnd(/*FogHandler.INSTANCE.getCurrentFogEnd()*/50);
		GlStateManager.setFogDensity(/*FogHandler.INSTANCE.getCurrentFogDensity()*/0.05F);

		//Render texture over whole screen
		GlStateManager.depthMask(false);
		GlStateManager.colorMask(true, true, true, true);
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vb = tessellator.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		vb.pos(0.0D, (double)framebufferTextureHeight, 500.0D).color(255, 255, 255, 255).endVertex();
		vb.pos((double)framebufferTextureWidth, (double)framebufferTextureHeight, 500.0D).color(255, 255, 255, 255).endVertex();
		vb.pos((double)framebufferTextureWidth, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
		vb.pos(0.0D, 0.0D, 500.0D).color(255, 255, 255, 255).endVertex();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.colorMask(true, true, true, true);

		//Unbind textures and FBO
		this.getShaderManager().endShader();
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
