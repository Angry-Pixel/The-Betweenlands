package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import thebetweenlands.common.entity.projectiles.EntityGlowingGoop;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderGlowingGoop extends RenderSnowball<EntityGlowingGoop> {
	public RenderGlowingGoop(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, Item.getItemFromBlock(BlockRegistry.GLOWING_GOOP), itemRenderer);
	}
}
