package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelSporeling;
import thebetweenlands.common.entity.mobs.EntitySporeling;

public class RenderSporeling extends RenderLiving<EntitySporeling> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sporeling.png");

	public RenderSporeling(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSporeling(), 0.3F);
		this.addLayer(new LayerOverlay<>(this, new ResourceLocation("thebetweenlands:textures/entity/sporeling_glow.png")).setGlow(true));

	}

	@Override
	protected void preRenderCallback(EntitySporeling sporeling, float partialTickTime) {
		if (sporeling.getIsFalling())
			GlStateManager.rotate(sporeling.smoothedAngle(partialTickTime), 0, 1, 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySporeling entity) {
		return TEXTURE;
	}
}
