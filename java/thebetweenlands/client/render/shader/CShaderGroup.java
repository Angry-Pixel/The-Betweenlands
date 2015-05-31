package thebetweenlands.client.render.shader;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.List;

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
			Field rmField = ReflectionHelper.findField(this.getClass().getSuperclass(), "resourceManager", "field_148033_b");
			rmField.setAccessible(true);
			this.pResourceManager = (IResourceManager) rmField.get(this);
			Field slField = ReflectionHelper.findField(this.getClass().getSuperclass(), "listShaders", "field_148031_d");
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
}
