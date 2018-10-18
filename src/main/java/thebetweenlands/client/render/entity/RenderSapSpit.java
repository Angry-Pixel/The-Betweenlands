package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import thebetweenlands.common.entity.projectiles.EntitySapSpit;
import thebetweenlands.common.registries.ItemRegistry;

public class RenderSapSpit extends RenderSnowball<EntitySapSpit> {
	public RenderSapSpit(RenderManager renderManagerIn) {
		super(renderManagerIn, ItemRegistry.SAP_SPIT, Minecraft.getMinecraft().getRenderItem());
	}
}
