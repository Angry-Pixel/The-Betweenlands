package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSporeMinion;
import thebetweenlands.common.entity.mobs.EntitySporeMinion;

public class RenderSporeMinion extends RenderLiving<EntitySporeMinion> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/spore_minion.png");

	public RenderSporeMinion(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSporeMinion(), 0.3F);

	}

	@Override
	protected void preRenderCallback(EntitySporeMinion spore, float partialTickTime) {
		if (spore.getIsFalling())
			GlStateManager.rotate(spore.smoothedAngle(partialTickTime), 0, 1, 0);
		GlStateManager.translate(0F, -0.2F, 0F);
		GlStateManager.scale(0.9F, 0.9F, 0.9F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySporeMinion entity) {
		return TEXTURE;
	}
}
