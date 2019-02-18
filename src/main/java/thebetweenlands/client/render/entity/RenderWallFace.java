package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.entity.mobs.EntityWallFace;

@OnlyIn(Dist.CLIENT)
public abstract class RenderWallFace<T extends EntityWallFace> extends RenderLiving<T> {
	public RenderWallFace(RenderManager renderManager, ModelBase model, float shadow) {
		super(renderManager, model, shadow);
	}

	@Override
	protected void preRenderCallback(T entity, float partialTickTime) {
		GlStateManager.translatef(0, (float)-entity.getEyeHeight(), 0);
		GlStateManager.rotatef(entity.rotationPitch, 1, 0, 0);
		GlStateManager.translatef(0, entity.getEyeHeight(), 0);
	}

	@Override
	protected void applyRotations(T entityLiving, float thisFieldWasObfuscatedAndICantBeBotheredToCheckWhatItIs, float rotationYaw, float partialTicks) {
		GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
	}
}
