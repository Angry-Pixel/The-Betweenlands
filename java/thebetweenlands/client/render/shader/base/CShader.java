package thebetweenlands.client.render.shader.base;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CShader {
	private ShaderGroup shaderGroup = null;
	private final ResourceLocation shaderDescription;
	private final ResourceLocation shaderPath;
	private final ResourceLocation assetsPath;
	private final IResourceManager resourceManager;
	private final Framebuffer frameBuffer;
	private final TextureManager textureManager;
	private final HashMap<String, Object> samplers = new HashMap<String, Object>();
	private final HashMap<String, CShaderInt> shaderMap = new HashMap<String, CShaderInt>();
	
	/**
	 * Creates a new shader wrapper. The wrapper will handle all the shader locations.
	 * @param textureManager		TextureManager that is used to handle the textures
	 * @param resourceManager		ResourceManager that is used to load the files
	 * @param frameBuffer			Input framebuffer
	 * @param shaderDescription		Path to the shader description file
	 * @param shaderPath			Path to the folder/package that contains the vertex/fragment shaders and the shader config
	 * @param assetsPath			Path to the folder/package that contains the assets that are used for auxiliary targets
	 */
	public CShader(TextureManager textureManager,
			IResourceManager resourceManager, Framebuffer frameBuffer,
			ResourceLocation shaderDescription, ResourceLocation shaderPath,
			ResourceLocation assetsPath) {
		this.shaderDescription = shaderDescription;
		this.shaderPath = shaderPath;
		this.frameBuffer = frameBuffer;
		this.resourceManager = resourceManager;
		this.textureManager = textureManager;
		this.assetsPath = assetsPath;
	}
	
	public final ResourceLocation getShaderDescription() {
		return this.shaderDescription;
	}
	
	public final ResourceLocation getShaderPath() {
		return this.shaderPath;
	}
	
	public final ResourceLocation getAssetsPath() {
		return this.assetsPath;
	}
	
	public final HashMap<String, Object> getSamplers() {
		return this.samplers;
	}
	
	protected final void addShader(CShaderInt shader) {
		this.shaderMap.put(shader.getName(), shader);
	}
	
	/**
	 * Returns the shader group. Creates a new one if it hasn't been created yet.
	 * @return
	 * @throws JsonException
	 */
	public final ShaderGroup getShaderGroup() throws JsonException {
		if(this.shaderGroup == null) {
			this.shaderGroup = new CShaderGroup(this.textureManager, 
					new ResourceManagerWrapper(this.resourceManager, this), 
					this.frameBuffer, this.shaderDescription, this);
		}
		return this.shaderGroup;
	}
	
	/**
	 * Updates the given sampler. Adds a new entry if no sampler can be found.
	 * @param name				Name of the sampler, used in the shaders (uniform sampler2D <name>)
	 * @param frameBuffer		Framebuffer of the texture that is used for the sampler
	 * @return
	 */
	public final CShader updateSampler(String name, Framebuffer frameBuffer) {
		this.samplers.put(name, frameBuffer);
		return this;
	}
	
	/**
	 * Updates the given sampler. Adds a new entry if no sampler can be found.
	 * @param name				Name of the sampler, used in the shaders (uniform sampler2D <name>)
	 * @param texture			The texture object that is used for the sampler
	 * @return
	 */
	public final CShader updateSampler(String name, ITextureObject texture) {
		this.samplers.put(name, texture);
		return this;
	}
	
	/**
	 * Updates the given sampler. Adds a new entry if no sampler can be found.
	 * @param name				Name of the sampler, used in the shaders (uniform sampler2D <name>)
	 * @param texture			The texture that is used for the sampler
	 * @return
	 */
	public final CShader updateSampler(String name, int texture) {
		this.samplers.put(name, texture);
		return this;
	}
	
	/**
	 * Returns a map of shaders and found shader uniforms.
	 * @param name		Name of the uniform
	 * @return
	 */
	public final Map<CShaderInt, ShaderUniform> getShaderUniforms(String name) {
		HashMap<CShaderInt, ShaderUniform> uniformMap = new HashMap<CShaderInt, ShaderUniform>();
		for(Entry<String, CShaderInt> shaderEntry : this.shaderMap.entrySet()) {
			uniformMap.put(shaderEntry.getValue(), shaderEntry.getValue().getUniform(name));
		}
		return uniformMap;
	}
	
	/**
	 * Returns the uniform of the given shader.
	 * @param shaderName		Name of the shader
	 * @param uniformName		Name of the uniform
	 * @return
	 */
	public final ShaderUniform getShaderUniform(String shaderName, String uniformName) {
		CShaderInt shader = this.shaderMap.get(shaderName);
		if(shader != null) {
			return shader.getUniform(uniformName);
		}
		return null;
	}
	
	/**
	 * This method is called when a shader is being update before it's bound to the framebuffer.
	 * @param shader		The shader that is being updated
	 */
	public void updateShader(CShaderInt shader) { }
	
	/**
	 * This method is called after the shader has been applied.
	 * @param shadershaderGroup		The shader group that has been applied
	 */
	public void postShader(CShaderGroup shaderGroup, float partialTicks) { }
}
