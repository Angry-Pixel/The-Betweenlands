package thebetweenlands.client.render.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.entity.mobs.EntityWallFace;

@SideOnly(Side.CLIENT)
public abstract class RenderWallFace<T extends EntityWallFace> extends RenderLiving<T> {
	public RenderWallFace(RenderManager renderManager, ModelBase model, float shadow) {
		super(renderManager, model, shadow);
	}

	@Override
	protected void preRenderCallback(T entity, float partialTickTime) {
		GlStateManager.translate(0, -entity.width / 2, 0);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTickTime, 1, 0, 0);
		GlStateManager.translate(0, entity.width / 2, 0);
	}

	@Override
	protected void applyRotations(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
		GlStateManager.rotate(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
	}
}
