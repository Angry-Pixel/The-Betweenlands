package thebetweenlands.util;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderHelper {
	private static Field fieldLayerRenderers = ReflectionHelper.findField(RenderLivingBase.class, "layerRenderers", "field_177097_h", "i");

	@SuppressWarnings("unchecked")
	public static <T extends LayerRenderer<?>> T getRenderLayer(RenderLivingBase<?> renderer, Class<T> cls, boolean subclasses) {
		try {
			List<LayerRenderer<?>> layers = (List<LayerRenderer<?>>) fieldLayerRenderers.get(renderer);
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
