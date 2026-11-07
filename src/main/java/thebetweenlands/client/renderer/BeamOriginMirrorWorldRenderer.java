package thebetweenlands.client.renderer;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public final class BeamOriginMirrorWorldRenderer implements ResourceManagerReloadListener {
	public static final BeamOriginMirrorWorldRenderer INSTANCE = new BeamOriginMirrorWorldRenderer();

	private BeamOriginMirrorWorldRenderer() {
		// Unit
	}
	
	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		// Invalidate VBOs
	}
}
