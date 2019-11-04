package thebetweenlands.client.render.sprite;

import java.io.IOException;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class GLTextureObjectWrapper extends AbstractTexture {
	private final int id;

	public GLTextureObjectWrapper(int id) {
		this.id = id;
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException { }

	@Override
	public int getGlTextureId() {
		return this.id;
	}

	@Override
	public void deleteGlTexture() {
		GlStateManager.deleteTexture(this.id);
	}
}
