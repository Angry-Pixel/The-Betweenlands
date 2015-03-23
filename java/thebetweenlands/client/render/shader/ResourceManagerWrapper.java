package thebetweenlands.client.render.shader;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ResourceManagerWrapper implements IResourceManager {

	private final IResourceManager parent;

	public ResourceManagerWrapper(IResourceManager parent) {
		System.out.println("### Wrapping resource manager ###");
		this.parent = parent;
	}

	@Override
	public Set getResourceDomains() {
		return this.parent.getResourceDomains();
	}

	@Override
	public IResource getResource(ResourceLocation location)
			throws IOException {
		if(location.getResourcePath().startsWith("shaders")) {
			ResourceLocation redirected = new ResourceLocation("thebetweenlands:" + location.getResourcePath());
			System.out.println("Getting resource: " + redirected.getResourceDomain() + ":" + redirected.getResourcePath());
			return this.parent.getResource(redirected);
		}
		System.out.println("Getting resource: " + location.getResourceDomain() + ":" + location.getResourcePath());
		return this.parent.getResource(location);
	}

	@Override
	public List getAllResources(ResourceLocation location)
			throws IOException {
		if(location.getResourcePath().startsWith("shaders")) {
			ResourceLocation redirected = new ResourceLocation("thebetweenlands:" + location.getResourcePath());
			System.out.println("Getting resources: " + redirected.getResourceDomain() + ":" + redirected.getResourcePath());
			return this.parent.getAllResources(redirected);
		}
		System.out.println("Getting resources: " + location.getResourceDomain() + ":" + location.getResourcePath());
		return this.parent.getAllResources(location);
	}

}
