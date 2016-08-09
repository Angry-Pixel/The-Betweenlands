package thebetweenlands.client.render.shader.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import thebetweenlands.util.config.ConfigHandler;

public class CShaderGroup extends ShaderGroup {
	private IResourceManager pResourceManager;
	private List pListShaders;
	private final WorldShader wrapper;

	public CShaderGroup(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation resourceLocation, WorldShader wrapper) throws JsonSyntaxException, IOException {
		super(textureManager, resourceManager, frameBuffer, resourceLocation);
		this.wrapper = wrapper;
	}

	@Override
	public void parseGroup(TextureManager textureManager, ResourceLocation resourceLocation) throws JsonSyntaxException, IOException {
		try {
			//TODO: Mappings!
			Field rmField = ReflectionHelper.findField(this.getClass().getSuperclass(), "resourceManager", "field_148033_b", "b");
			rmField.setAccessible(true);
			this.pResourceManager = (IResourceManager) rmField.get(this);
			Field slField = ReflectionHelper.findField(this.getClass().getSuperclass(), "listShaders", "field_148031_d", "d");
			slField.setAccessible(true);
			this.pListShaders = (List) slField.get(this);
		} catch (Exception e) {
			throw new RuntimeException("Failed to get underlying ShaderGroup fields", e);
		}
		try {
			super.parseGroup(textureManager, resourceLocation);
		} catch(Exception ex) {
			RuntimeException exception = new RuntimeException("Failed to load shader " + resourceLocation, ex);
			if(ConfigHandler.debug)
				exception.printStackTrace();
			else
				throw exception;
		}
	}

	@Override
	public Shader addShader(String name, Framebuffer framebufferIn, Framebuffer framebufferOut) throws IOException {
		Shader shader = new CShaderInt(this.pResourceManager, name, framebufferIn, framebufferOut, ((ResourceManagerWrapper)this.pResourceManager).getWrapper());
		this.pListShaders.add(this.pListShaders.size(), shader);
		return shader;
	}

	@Override
	public void loadShaderGroup(float partialTicks) {
		super.loadShaderGroup(partialTicks);

		//Pop off the GL_TEXTURE matrix
		GlStateManager.popMatrix();
		this.wrapper.postShader(this, partialTicks);
		//Push matrix to prevent stack underflow
		GlStateManager.pushMatrix();
	}
}
