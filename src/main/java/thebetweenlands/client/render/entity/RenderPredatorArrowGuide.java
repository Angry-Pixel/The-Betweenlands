package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.entity.projectiles.EntityPredatorArrowGuide;

public class RenderPredatorArrowGuide extends Render<EntityPredatorArrowGuide> {
	public RenderPredatorArrowGuide(RenderManager renderManager) {
		super(renderManager);
	}

	//I like this.
	//Simple and easy to maintain.

	@Override
	protected ResourceLocation getEntityTexture(EntityPredatorArrowGuide entity) {
		return null;
	}
}
