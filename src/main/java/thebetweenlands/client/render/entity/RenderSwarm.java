package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.render.model.entity.ModelSwarm;
import thebetweenlands.common.entity.mobs.EntitySwarm;

public class RenderSwarm extends RenderLiving<EntitySwarm> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/swarm.png");

	protected static final ModelSwarm MODEL = new ModelSwarm();

	private float partialTicks;

	public RenderSwarm(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0);
	}

	@Override
	public void doRender(EntitySwarm entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.partialTicks = partialTicks;
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected float getDeathMaxRotation(EntitySwarm entityLivingBaseIn) {
		return 0;
	}	

	@Override
	protected void applyRotations(EntitySwarm entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
		//no
	}

	@Override
	protected void renderModel(EntitySwarm entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
		boolean isVisible = this.isVisible(entity);
		boolean isTranslucent = !isVisible && !entity.isInvisibleToPlayer(Minecraft.getMinecraft().player);

		if(isVisible || isTranslucent) {
			if(!this.bindEntityTexture(entity)) {
				return;
			}

			if(isTranslucent) {
				GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}

			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

			GlStateManager.pushMatrix();

			GlStateManager.scale(0.4f, 0.4f, 0.4f);

			int count = (int)Math.round(entity.getSwarmSize() * 4);

			float offset = (float) Math.abs(Math.sin(ageInTicks * 0.01f) * 0.05f) + 0.15f;

			float radius = count == 1 ? 0 : (count / 4.0f * 0.45f + 0.2f);

			float entityYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * this.partialTicks;

			float move = Math.max((1 - limbSwingAmount / 0.3f), 0);

			for(int i = 0; i < count; i++) {
				float time1 = ageInTicks;

				float ox = MathHelper.cos(i / 4.0f * (float) Math.PI * 2) * radius + MathHelper.sin(time1 * 0.1f * (1 + i / 4.0f * 0.3f) + i * 0.34f) * offset;
				float oy = i / 4.0f * 0.5f + 1.5f + MathHelper.sin(time1 * 0.2f * (1 + i / 4.0f * 0.2f) + i * 1.5f) * offset;
				float oz = MathHelper.sin(i / 4.0f * (float) Math.PI * 2) * radius + MathHelper.sin(time1 * 0.05f * (1 + i / 4.0f * 0.5f) - i * 2.4f) * offset;

				GlStateManager.pushMatrix();
				GlStateManager.translate(ox, oy, oz);
				GlStateManager.rotate(entityYaw + 180, 0, 1, 0);
				GlStateManager.translate(0, 0, (1 - move) * 0.5f);

				this.mainModel.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);

				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();

			GlStateManager.disableBlend();

			if(isTranslucent) {
				GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySwarm entity) {
		return TEXTURE;
	}
}
