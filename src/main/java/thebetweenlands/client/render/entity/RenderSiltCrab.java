package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelSiltCrab;
import thebetweenlands.common.entity.mobs.EntitySiltCrab;

@OnlyIn(Dist.CLIENT)
public class RenderSiltCrab extends RenderLiving<EntitySiltCrab> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/silt_crab.png");

	public RenderSiltCrab(RenderManager manager) {
		super(manager, new ModelSiltCrab(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySiltCrab entity) {
		return TEXTURE;
	}
}