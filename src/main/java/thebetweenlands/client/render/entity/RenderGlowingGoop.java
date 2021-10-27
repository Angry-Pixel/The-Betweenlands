package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.item.Item;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.projectiles.EntityGlowingGoop;
import thebetweenlands.common.registries.BlockRegistry;

public class RenderGlowingGoop extends RenderSnowball<EntityGlowingGoop> {
	public RenderGlowingGoop(RenderManager renderManager, RenderItem itemRenderer) {
		super(renderManager, Item.getItemFromBlock(BlockRegistry.GLOWING_GOOP), itemRenderer);
	}
	
    public void doRender(EntityGlowingGoop entity, double x, double y, double z, float entityYaw, float partialTicks) {
    	super.doRender(entity, x, y, z, entityYaw, partialTicks);
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			double rx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
			double ry = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
			double rz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry, rz, 8.0F, 1F, 1F, 0));
		}
	}
}
