package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSprite;
import thebetweenlands.common.entity.EntityAngryPebble;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderAngryPebble extends RenderSprite<EntityAngryPebble> {
	public RenderAngryPebble(RenderManager renderManager, ItemRenderer itemRenderer) {
		super(renderManager, ItemRegistry.ANGRY_PEBBLE, itemRenderer);
	}
}
