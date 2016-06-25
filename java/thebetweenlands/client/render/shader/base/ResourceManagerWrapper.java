package thebetweenlands.client.render.shader.base;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceManagerWrapper implements IResourceManager {
	private static final Field RESOURCE_DOMAIN_FIELD = ReflectionHelper.findField(ResourceLocation.class, "resourceDomain", "field_110626_a", "a");
	private static final Field RESOURCE_PATH_FIELD = ReflectionHelper.findField(ResourceLocation.class, "resourcePath", "field_110625_b", "b");

	private final IResourceManager parent;
	private final WorldShader wrapper;

	public ResourceManagerWrapper(IResourceManager parent, WorldShader wrapper) {
		this.parent = parent;
		this.wrapper = wrapper;
	}

	public WorldShader getWrapper() {
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
			try {
				RESOURCE_DOMAIN_FIELD.set(location, redirected.getResourceDomain());
				RESOURCE_PATH_FIELD.set(location, redirected.getResourcePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
