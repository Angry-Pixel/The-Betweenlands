package thebetweenlands.client.render.shader;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;

public class BLShaderGroup extends ShaderGroup {
	private IResourceManager pResourceManager;
	private List pListShaders;
	
	public BLShaderGroup(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation resourceLocation) throws JsonException {
		super(textureManager, resourceManager, frameBuffer, resourceLocation);
		System.out.println("Loaded shader group: " + resourceLocation.getResourcePath());
	}
	
	@Override
	public void func_152765_a(TextureManager textureManager, ResourceLocation resourceLocation) throws JsonException {
		try {
			Field rmField = this.getClass().getSuperclass().getDeclaredField("resourceManager");
			rmField.setAccessible(true);
			this.pResourceManager = (IResourceManager) rmField.get(this);
			Field slField = this.getClass().getSuperclass().getDeclaredField("listShaders");
			slField.setAccessible(true);
			this.pListShaders = (List) slField.get(this);
			System.out.println("### Successfully retrieved shader group fields ###");
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.func_152765_a(textureManager, resourceLocation);
    }
	
	@Override
	public Shader addShader(String p_148023_1_, Framebuffer p_148023_2_, Framebuffer p_148023_3_) throws JsonException {
		System.out.println("Adding shader: " + p_148023_1_);
		Shader shader = new BLShader(this.pResourceManager, p_148023_1_, p_148023_2_, p_148023_3_);
		this.pListShaders.add(this.pListShaders.size(), shader);
		return shader;
	}
}
