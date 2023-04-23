package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSporeMinionFloat;
import thebetweenlands.common.entity.mobs.EntitySporeMinion;

public class RenderSporeMinion extends RenderLiving<EntitySporeMinion> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/spore_minion.png");
	public static final ResourceLocation TEXTURE_RED = new ResourceLocation("thebetweenlands:textures/entity/spore_minion_red.png");
	public static final ResourceLocation TEXTURE_GREEN = new ResourceLocation("thebetweenlands:textures/entity/spore_minion_green.png");
	public static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("thebetweenlands:textures/entity/spore_minion_blue.png");
	private static final  ModelSporeMinionFloat MODEL = new ModelSporeMinionFloat();
	public RenderSporeMinion(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.3F);
	}

	@Override
	protected void preRenderCallback(EntitySporeMinion spore, float partialTickTime) {
	//	if (spore.getIsFalling())
		//	GlStateManager.rotate(spore.smoothedAngle(partialTickTime), 0, 1, 0);
	}
	
	@Override
	public void doRender(EntitySporeMinion entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		boolean isVisible = this.isVisible(entity);
		boolean isTranslucentToPlayer = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if (!isVisible && !isTranslucentToPlayer)
			return;

		boolean useBrightness = this.setDoRenderBrightness(entity, partialTicks);

		if (isTranslucentToPlayer)
			GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

		boolean useTeamColors = false;

		if (this.renderOutlines) {
			useTeamColors = this.setScoreTeamColor(entity);
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		bindTexture(getEntityTexture(entity));
		GlStateManager.pushMatrix();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		GlStateManager.color(1F, 1F, 1F, 1F);

		GlStateManager.disableCull();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x, y + 1.25F, z);
		GlStateManager.scale(-0.75, -0.75, 0.75);
		GlStateManager.rotate(entity.getIsFalling() ? entity.smoothedAngle(partialTicks) : 180 + entityYaw, 0, 1, 0);
		GlStateManager.pushMatrix();
		// if (entity.getType() == 2) {
		int size = entity.getInflateSize();
		GlStateManager.translate(0F, 0F - (float) (size * 0.012F), 0F);
		GlStateManager.scale(0.9F + (float) (size * 0.009F), 0.9F + (float) (size * 0.009F),
				0.9F + (float) (size * 0.009F));
		GlStateManager.rotate(entityYaw + (float) (size) * 7.2F + partialTicks, 0, 1, 0);
		// } else {
		// GlStateManager.translate(0F, 0.2F, 0F);
		// GlStateManager.scale(0.9F, 0.9F, 0.9F);
		// }

		MODEL.renderSpore(0.0625F);
		GlStateManager.popMatrix();
		MODEL.renderTendrils(0.0625F);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		if (useTeamColors)
			this.unsetScoreTeamColor();

		if (isTranslucentToPlayer)
			GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);

		if (useBrightness)
			this.unsetBrightness();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySporeMinion entity) {
		switch (entity.getType()) {
		case 0:
			return TEXTURE_RED;
		case 1:
			return TEXTURE_GREEN;
		case 2:
			return TEXTURE_BLUE;
		case 3:
			return TEXTURE;
		}
		return TEXTURE;
	}
}
