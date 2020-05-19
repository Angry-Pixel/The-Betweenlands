package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelAnadia;
import thebetweenlands.common.entity.mobs.EntityAnadia;

@SideOnly(Side.CLIENT)
public class RenderAnadia extends RenderLiving<EntityAnadia> {
	public final static ResourceLocation TEXTURE_1 = new ResourceLocation("thebetweenlands:textures/entity/anadia_1.png");
	public final static ResourceLocation TEXTURE_2 = new ResourceLocation("thebetweenlands:textures/entity/anadia_2.png");
	public final static ResourceLocation TEXTURE_3 = new ResourceLocation("thebetweenlands:textures/entity/anadia_3.png");
	public final static ModelAnadia ANADIA_MODEL = new ModelAnadia();

	public RenderAnadia(RenderManager manager) {
		super(manager, ANADIA_MODEL, 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAnadia entity) {
		return TEXTURE_1;
	}

	@Override
	public void doRender(EntityAnadia anadia, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(anadia, x, y, z, entityYaw, partialTicks);
		float smoothedYaw = anadia.prevRotationYaw + (anadia.rotationYaw - anadia.prevRotationYaw) * partialTicks;
		float smoothedPitch = anadia.prevRotationPitch + (anadia.rotationPitch - anadia.prevRotationPitch) * partialTicks;
		float scale = anadia.getFishSize();
		shadowSize = scale * 0.5F;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + scale * 1.5F, z);
		GlStateManager.scale(scale, -scale, -scale);
		GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
		GlStateManager.rotate(smoothedPitch, 1F, 0F, 0F);
		switch (anadia.getHeadType()) {
			case 0:
				bindTexture(TEXTURE_1);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3);
				ANADIA_MODEL.renderHead(anadia.getHeadType(), 0.0625F);
				break;
		}

		switch (anadia.getBodyType()) {
			case 0:
				bindTexture(TEXTURE_1);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3);
				ANADIA_MODEL.renderBody(anadia.getBodyType(), 0.0625F);
				break;
		}

		switch (anadia.getTailType()) {
			case 0:
				bindTexture(TEXTURE_1);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
			case 1:
				bindTexture(TEXTURE_2);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
			case 2:
				bindTexture(TEXTURE_3);
				ANADIA_MODEL.renderTail(anadia.getTailType(), 0.0625F);
				break;
		} 
		GlStateManager.popMatrix();
		ANADIA_MODEL.setLivingAnimations(anadia, anadia.limbSwing, anadia.limbSwingAmount, partialTicks);
	}
}
