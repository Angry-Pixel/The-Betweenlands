package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelPeatMummy;
import thebetweenlands.common.entity.mobs.EntityClimberBase;

public class RenderClimber extends RenderClimberBase<EntityClimberBase> {
	public RenderClimber(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelPeatMummy(), 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityClimberBase entity) {
		return new ResourceLocation("thebetweenlands:textures/entity/peat_mummy.png");
	}
}
