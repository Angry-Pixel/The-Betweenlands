package thebetweenlands.client.render.shader;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceManagerWrapper implements IResourceManager {

	private final IResourceManager parent;
	private final CShader wrapper;
	
	public ResourceManagerWrapper(IResourceManager parent, CShader wrapper) {
		this.parent = parent;
		this.wrapper = wrapper;
	}

	public CShader getWrapper() {
		return this.wrapper;
	}
	
	@Override
	public Set getResourceDomains() {
		return this.parent.getResourceDomains();
	}

	@Override
	public IResource getResource(ResourceLocation location)
			throws IOException {
		if(location.getResourcePath().startsWith("shaders/post/")) {
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getShaderDescription().getResourceDomain() + ":" + this.wrapper.getShaderDescription().getResourcePath());
			return this.parent.getResource(redirected);
		} else if(location.getResourcePath().startsWith("shaders/program/")) {
			String fileName = location.getResourcePath().replace("shaders/program/", "");
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getShaderPath().getResourceDomain() + ":" + this.wrapper.getShaderPath().getResourcePath() + fileName);
			return this.parent.getResource(redirected);
		} else if(location.getResourcePath().startsWith("textures/effect/")) {
			String fileName = location.getResourcePath().replace("textures/effect/", "");
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getAssetsPath().getResourceDomain() + ":" + this.wrapper.getAssetsPath().getResourcePath() + fileName);
			return this.parent.getResource(redirected);
		}
		return this.parent.getResource(location);
	}

	@Override
	public List getAllResources(ResourceLocation location)
			throws IOException {
		if(location.getResourcePath().startsWith("shaders/post/")) {
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getShaderDescription().getResourceDomain() + ":" + this.wrapper.getShaderDescription().getResourcePath());
			return this.parent.getAllResources(redirected);
		} else if(location.getResourcePath().startsWith("shaders/program/")) {
			String fileName = location.getResourcePath().replace("shaders/program/", "");
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getShaderPath().getResourceDomain() + ":" + this.wrapper.getShaderPath().getResourcePath() + fileName);
			return this.parent.getAllResources(redirected);
		} else if(location.getResourcePath().startsWith("textures/effect/")) {
			String fileName = location.getResourcePath().replace("textures/effect/", "");
			ResourceLocation redirected = new ResourceLocation(this.wrapper.getAssetsPath().getResourceDomain() + ":" + this.wrapper.getAssetsPath().getResourcePath() + fileName);
			return this.parent.getAllResources(redirected);
		}
		return this.parent.getAllResources(location);
	}

}
