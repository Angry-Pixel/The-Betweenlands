package thebetweenlands.client.renderer.entity.volarkite;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import thebetweenlands.client.model.entity.volarkite.PlayerVolarkiteModel;

public class PlayerVolarkiteRenderer extends PlayerRenderer {

	public PlayerVolarkiteRenderer(EntityRendererProvider.Context context, boolean useSlimModel) {
		super(context, useSlimModel);
		this.model = new PlayerVolarkiteModel(context.bakeLayer(useSlimModel ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), useSlimModel);
		this.layers.removeIf(layer -> layer instanceof PlayerItemInHandLayer<?, ?>);
	}
}
