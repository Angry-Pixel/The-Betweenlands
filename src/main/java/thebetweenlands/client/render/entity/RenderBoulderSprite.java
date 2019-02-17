package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelBoulderSprite;
import thebetweenlands.common.entity.mobs.EntityBoulderSprite;

@OnlyIn(Dist.CLIENT)
public class RenderBoulderSprite extends RenderLiving<EntityBoulderSprite> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/boulder_sprite.png");

	public RenderBoulderSprite(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelBoulderSprite(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoulderSprite entity) {
		return TEXTURE;
	}
}
