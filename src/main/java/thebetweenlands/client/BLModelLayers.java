package thebetweenlands.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import thebetweenlands.common.TheBetweenlands;

public class BLModelLayers {

	public static final ModelLayerLocation CENSER = register("censer");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_1 = register("deepman_simulacrum_1");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_2 = register("deepman_simulacrum_2");
	public static final ModelLayerLocation DEEPMAN_SIMULACRUM_3 = register("deepman_simulacrum_3");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_1 = register("lake_cavern_simulacrum_1");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_2 = register("lake_cavern_simulacrum_2");
	public static final ModelLayerLocation LAKE_CAVERN_SIMULACRUM_3 = register("lake_cavern_simulacrum_3");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_1 = register("rootman_simulacrum_1");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_2 = register("rootman_simulacrum_2");
	public static final ModelLayerLocation ROOTMAN_SIMULACRUM_3 = register("rootman_simulacrum_3");

	private static ModelLayerLocation register(String name) {
		return register(name, "main");
	}

	private static ModelLayerLocation register(String name, String layer) {
		return new ModelLayerLocation(TheBetweenlands.prefix(name), layer);
	}
}
