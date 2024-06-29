package thebetweenlands.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;

public class GLTextureObjectWrapper extends AbstractTexture {
	private final int id;

	public GLTextureObjectWrapper(int id) {
		this.id = id;
	}

	@Override
	public void load(ResourceManager manager) { }

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void releaseId() {
		RenderSystem.deleteTexture(this.id);
	}
}
