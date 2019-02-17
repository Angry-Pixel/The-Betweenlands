package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.projectiles.EntitySnailPoisonJet;

@OnlyIn(Dist.CLIENT)
public class RenderSnailPoisonJet extends Render<EntitySnailPoisonJet> {
	public RenderSnailPoisonJet(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntitySnailPoisonJet entity, double x, double y, double z, float entityYaw, float partialTicks) {
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySnailPoisonJet entity) {
		return null;
	}
}