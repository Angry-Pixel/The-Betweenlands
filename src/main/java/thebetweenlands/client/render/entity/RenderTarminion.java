package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.entity.layer.LayerAnimatedOverlay;
import thebetweenlands.client.render.model.entity.ModelTarminion;
import thebetweenlands.common.entity.mobs.EntityTarminion;


public class RenderTarminion extends RenderLiving<EntityTarminion> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tarminion.png");
	public static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/tarminion_overlay.png");

	public RenderTarminion(RenderManager manager) {
		super(manager, new ModelTarminion(), 0.16F);
		this.addLayer(new LayerAnimatedOverlay<EntityTarminion>(this, OVERLAY_TEXTURE));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTarminion entity) {
		return TEXTURE;
	}
}
