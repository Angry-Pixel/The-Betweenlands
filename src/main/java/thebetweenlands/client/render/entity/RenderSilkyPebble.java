package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import thebetweenlands.common.entity.projectiles.EntitySilkyPebble;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderSilkyPebble extends RenderSnowball<EntitySilkyPebble> {
	public RenderSilkyPebble(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, ItemRegistry.SILKY_PEBBLE, itemRenderer);
	}
}