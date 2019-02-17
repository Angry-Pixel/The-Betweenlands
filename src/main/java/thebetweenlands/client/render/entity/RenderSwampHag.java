package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelSwampHag;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

@OnlyIn(Dist.CLIENT)
public class RenderSwampHag extends RenderLiving<EntitySwampHag> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/swamp_hag.png");

	public RenderSwampHag(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSwampHag(), 0.5F);
		this.addLayer(new LayerOverlay<EntitySwampHag>(this, new ResourceLocation("thebetweenlands:textures/entity/swamp_hag_eyes.png")).setGlow(true));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySwampHag entity) {
		return TEXTURE;
	}


	@Override
	protected void preRenderCallback(EntitySwampHag entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(0.74F, 0.74F, 0.74F);
	}
}
