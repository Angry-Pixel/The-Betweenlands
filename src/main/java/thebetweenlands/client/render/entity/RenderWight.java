package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelWight;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityWight;
import thebetweenlands.common.world.event.EventWinter;

public class RenderWight extends RenderLiving<EntityWight> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/wight.png");
	public static final ResourceLocation TEXTURE_FROSTY = new ResourceLocation("thebetweenlands:textures/entity/wight_frosty.png");
	
	public static final ModelWight MODEL = new ModelWight();
	public static final ModelWight MODEL_HEAD_ONLY = new ModelWight().setRenderHeadOnly(true);

	public RenderWight(RenderManager rendermanagerIn) {
		super(rendermanagerIn, MODEL, 0.5f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWight entity) {
		if(EventWinter.isFroooosty(entity.world)) {
			return TEXTURE_FROSTY;
		}
		return TEXTURE;
	}

	@Override
	protected void preRenderCallback(EntityWight entity, float partialTicks) {
		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(entity.posX, entity.posY, entity.posZ, 10.0f, -1, -1, -1));
		}

		float scale = 0.9F / 40F * (entity.getGrowthFactor(partialTicks));
		GlStateManager.scale(0.9F, scale, 0.9F);

		if(entity.isVolatile()) {
			GlStateManager.scale(0.5D, 0.5D, 0.5D);
			GlStateManager.translate(0, 1.0D, 0);
		}
	}

	@Override
	public void doRender(EntityWight entity, double x, double y, double z, float entityYaw, float partialTicks) {
		if(!entity.isVolatile()) {
			GlStateManager.pushMatrix();
			GlStateManager.disableBlend();
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.color(1, 1, 1, 1);

			super.doRender(entity, x, y, z, entityYaw, partialTicks);

			GlStateManager.colorMask(true, true, true, true);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1F, 1F, 1F, 1F - entity.getHidingAnimation(partialTicks) * 0.5F);

			super.doRender(entity, x, y, z, entityYaw, partialTicks);

			GlStateManager.popMatrix();
		} else {
			this.mainModel = MODEL_HEAD_ONLY;

			GlStateManager.pushMatrix();
			GlStateManager.disableBlend();
			GlStateManager.colorMask(false, false, false, false);
			GlStateManager.color(1, 1, 1, 1);

			if (entity.getRidingEntity() != null) {
				GlStateManager.translate(x, y, z);
				GlStateManager.enableRescaleNormal();
				GlStateManager.scale(-1.0F, -1.0F, 1.0F);
				GlStateManager.rotate((float) (entity.ticksExisted + partialTicks) / 30.0F * 360.0F, 0, 1, 0);
				GlStateManager.rotate(180, 0, 1, 0);
				GlStateManager.translate(0, -entity.getRidingEntity().getEyeHeight() + 1.65D, 0.8D);
				GlStateManager.scale(0.5D, 0.5D, 0.5D);

				this.bindEntityTexture(entity);

				MODEL_HEAD_ONLY.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);

				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1F, 1F, 1F, 0.4F);

				MODEL_HEAD_ONLY.render(entity, 0.0F, 0.0F, entity.ticksExisted + partialTicks, 0.0F, 0.0F, 0.0625F);
			} else {
				super.doRender(entity, x, y, z, entityYaw, partialTicks);

				GlStateManager.colorMask(true, true, true, true);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1F, 1F, 1F, 0.4F);

				super.doRender(entity, x, y, z, entityYaw, partialTicks);
			}

			GlStateManager.popMatrix();

			this.mainModel = MODEL;
		}
	}
}
