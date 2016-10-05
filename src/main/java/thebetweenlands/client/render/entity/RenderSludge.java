package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelSludge;
import thebetweenlands.common.entity.mobs.EntitySludge;

@SideOnly(Side.CLIENT)
public class RenderSludge extends RenderLiving<EntitySludge> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");

	public RenderSludge(RenderManager renderManager) {
		super(renderManager, new ModelSludge(), 1.0F);
	}

	@Override
	protected void preRenderCallback(EntitySludge sludge, float partialTickTime) {
		float squishFactor = (sludge.prevSquishFactor + (sludge.squishFactor - sludge.prevSquishFactor) * partialTickTime) / 1.5F;
		float scale = 1.0F / (squishFactor + 1.0F);
		GlStateManager.scale(scale, 1.0F / scale, scale);
		GlStateManager.depthMask(false);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySludge entity) {
		return TEXTURE;
	}
}
