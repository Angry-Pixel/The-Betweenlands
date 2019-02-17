package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.model.entity.ModelRootSprite;
import thebetweenlands.common.entity.mobs.EntityRootSprite;
import thebetweenlands.common.lib.ModInfo;

@OnlyIn(Dist.CLIENT)
public class RenderRootSprite extends RenderLiving<EntityRootSprite> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/root_sprite.png");

	public RenderRootSprite(RenderManager renderManager) {
		super(renderManager, new ModelRootSprite(), 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRootSprite entity) {
		return TEXTURE;
	}
}
