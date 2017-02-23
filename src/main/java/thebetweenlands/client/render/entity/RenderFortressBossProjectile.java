package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSwordEnergy;
import thebetweenlands.common.entity.mobs.EntityFortressBossProjectile;

public class RenderFortressBossProjectile extends Render<EntityFortressBossProjectile> {
	protected static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	protected static final ModelSwordEnergy MODEL = new ModelSwordEnergy();

	public RenderFortressBossProjectile(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityFortressBossProjectile entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderProjectile(entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderProjectile(EntityFortressBossProjectile entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		float ticks = entity.ticksExisted + partialTickTime;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y - 0.6D * 0.6D, z);
		this.bindTexture(TEXTURE);
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		float uOffset = ticks * 0.01F;
		float vOffset = ticks * 0.01F;
		GlStateManager.translate(uOffset, vOffset, 0.0F);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ONE);
		GlStateManager.scale(0.6D, 0.6D, 0.6D);
		if(entity.getRidingEntity() != null) {
			GlStateManager.disableCull();
		}
		if(!entity.isDeflectable()) {
			GlStateManager.color(0.8F, 0.6F, 0.4F, 1.0F);
		} else {
			GlStateManager.color(0.15F, 1.0F, 0.35F, 1.0F);
		}
		MODEL.render(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFortressBossProjectile entity) {
		return null;
	}
}
