package thebetweenlands.client.render.shader.base;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

public class CShaderGroup extends ShaderGroup {
	private IResourceManager pResourceManager;
	private List pListShaders;
	private final CShader wrapper;
	
	public CShaderGroup(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation resourceLocation, CShader wrapper) throws JsonException {
		super(textureManager, resourceManager, frameBuffer, resourceLocation);
		this.wrapper = wrapper;
	}
	
	//Called after CTOR
	@Override
	public void func_152765_a(TextureManager textureManager, ResourceLocation resourceLocation) throws JsonException {
		try {
			Field rmField = ReflectionHelper.findField(this.getClass().getSuperclass(), "resourceManager", "field_148033_b", "b");
			rmField.setAccessible(true);
			this.pResourceManager = (IResourceManager) rmField.get(this);
			Field slField = ReflectionHelper.findField(this.getClass().getSuperclass(), "listShaders", "field_148031_d", "d");
			slField.setAccessible(true);
			this.pListShaders = (List) slField.get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.func_152765_a(textureManager, resourceLocation);
    }
	
	@Override
	public Shader addShader(String name, Framebuffer framebufferIn, Framebuffer framebufferOut) throws JsonException {
		Shader shader = new CShaderInt(this.pResourceManager, name, framebufferIn, framebufferOut, ((ResourceManagerWrapper)this.pResourceManager).getWrapper());
		this.pListShaders.add(this.pListShaders.size(), shader);
		return shader;
	}
	
	@Override
	public void loadShaderGroup(float partialTicks) {
		super.loadShaderGroup(partialTicks);
		
		//Pop off the GL_TEXTURE matrix
		GL11.glPopMatrix();
		this.wrapper.postShader(this, partialTicks);
		//Push matrix to prevent stack underflow
		GL11.glPushMatrix();
    }
}
