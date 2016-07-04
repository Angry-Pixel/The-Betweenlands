package thebetweenlands.client.render.render.entity.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSwampHag;
import thebetweenlands.client.render.render.entity.layer.LayerGlow;
import thebetweenlands.common.entity.mobs.EntitySwampHag;

@SideOnly(Side.CLIENT)
public class RenderSwampHag extends RenderLiving<EntitySwampHag> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/swamp_hag.png");

	public RenderSwampHag(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSwampHag(), 0.5F);
		this.addLayer(new LayerGlow(this, new ResourceLocation("thebetweenlands:textures/entity/swamp_hag_eyes.png")));
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
