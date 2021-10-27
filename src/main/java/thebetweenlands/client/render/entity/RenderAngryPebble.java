package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import thebetweenlands.common.entity.projectiles.EntityAngryPebble;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderAngryPebble extends RenderSnowball<EntityAngryPebble> {
	public RenderAngryPebble(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, ItemRegistry.ANGRY_PEBBLE, itemRenderer);
	}
}
