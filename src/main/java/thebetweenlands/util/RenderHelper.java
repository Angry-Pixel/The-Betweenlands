package thebetweenlands.util;

import java.util.List;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class RenderHelper {

	@SuppressWarnings("unchecked")
	public static <T extends LayerRenderer<?>> T getRenderLayer(RenderLivingBase<?> renderer, Class<T> cls, boolean subclasses) {
		try {
			List<? extends LayerRenderer<?>> layers = renderer.layerRenderers;
			for(LayerRenderer<?> layer : layers) {
				if(subclasses ? cls.isInstance(layer) : cls == layer.getClass()) {
					return (T) layer;
				}
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public static boolean doesRendererHaveLayer(RenderLivingBase<?> renderer, Class<? extends LayerRenderer<?>> cls, boolean subclasses) {
		return getRenderLayer(renderer, cls, subclasses) != null;
	}
}
