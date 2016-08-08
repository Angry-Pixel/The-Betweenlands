package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.entity.layer.LayerGlow;
import thebetweenlands.client.render.model.entity.ModelSporeling;
import thebetweenlands.common.entity.mobs.EntitySporeling;

public class RenderSporeling extends RenderLiving<EntitySporeling> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sporeling.png");

	public RenderSporeling(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSporeling(), 0.3F);
		this.addLayer(new LayerGlow(this, new ResourceLocation("thebetweenlands:textures/entity/sporeling_glow.png")));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySporeling entity) {
		return TEXTURE;
	}
}
