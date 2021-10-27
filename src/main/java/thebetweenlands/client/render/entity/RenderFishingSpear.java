package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelFishingSpear;
import thebetweenlands.common.entity.projectiles.EntityFishingSpear;

@SideOnly(Side.CLIENT)
public class RenderFishingSpear extends Render<EntityFishingSpear> {
	public final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/fishing_spear.png");
	public static final ModelFishingSpear SPEAR_MODEL = new ModelFishingSpear();
	
	public RenderFishingSpear(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(EntityFishingSpear entity, double x, double y, double z, float entityYaw, float partialTicks) {
		bindEntityTexture(entity);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks - 45.0F, 0.0F, 0.0F, 1.0F);
		float shake = (float) entity.arrowShake - partialTicks;

		if (shake > 0.0F) {
			float f10 = -MathHelper.sin(shake * 3.0F) * shake;
			GlStateManager.rotate(f10, 0.0F, 0.0F, 1.0F);
		}

		GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.translate(0F, 0.25F, 0F);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		switch (entity.getType()) {
		case 0:
			SPEAR_MODEL.renderBasic(entity, partialTicks);
			break;
		case 1:
			SPEAR_MODEL.renderAmphibious(entity, partialTicks);
			break;
		case 2:
			SPEAR_MODEL.renderRobustAmphibious(entity, partialTicks);
			break;
		}

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFishingSpear entity) {
		return TEXTURE;
	}

}
