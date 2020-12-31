package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelStalker;
import thebetweenlands.common.entity.mobs.EntityStalker;

public class RenderStalker extends RenderClimberBase<EntityStalker> {
	public RenderStalker(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelStalker(), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityStalker entity) {
		return new ResourceLocation("thebetweenlands:textures/entity/stalker.png");
	}
}
