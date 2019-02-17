package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelTermite;
import thebetweenlands.common.entity.mobs.EntityTermite;

public class RenderTermite extends RenderLiving<EntityTermite> {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/termite.png");

	public RenderTermite(RenderManager renderManager) {
		super(renderManager, new ModelTermite(), 0.5F);
	}

	@Override
	protected void preRenderCallback(EntityTermite entity, float partialTickTime) {
		if(entity.getAttribute(EntityTermite.SMALL).getValue() == 1) {
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			this.shadowSize = 0.25F;
		} else {
			this.shadowSize = 0.5F;
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTermite entity) {
		return texture;
	}
}
