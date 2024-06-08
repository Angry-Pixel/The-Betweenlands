package thebetweenlands.client.gui;

import net.minecraft.client.gui.MapRenderer;
import thebetweenlands.TheBetweenlands;

import java.util.ArrayList;
import java.util.List;

public class MapHanderer {
    // Storage of map renderers
    public static List<MapRenderer> mapRenderers = new ArrayList<>();

    public static int registerRenderer(MapRenderer renderer) {
        TheBetweenlands.LOGGER.info("Registering map render handler " + renderer.getClass().getName());
        mapRenderers.add(renderer);
        return mapRenderers.size()-1;
    }
}
