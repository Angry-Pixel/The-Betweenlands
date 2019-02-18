package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.entity.layer.LayerOverlay;
import thebetweenlands.client.render.model.entity.ModelPyrad;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityPyrad;

@OnlyIn(Dist.CLIENT)
public class RenderPyrad extends RenderLiving<EntityPyrad> {
	public final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/pyrad.png");

	protected final LayerOverlay<EntityPyrad> glow;

	public RenderPyrad(RenderManager manager) {
		super(manager, new ModelPyrad(), 0.5F);
		this.addLayer(this.glow = new LayerOverlay<EntityPyrad>(this, new ResourceLocation("thebetweenlands:textures/entity/pyrad_glow.png")).setGlow(true));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPyrad entity) {
		return TEXTURE;
	}

	@Override
	public void doRender(EntityPyrad entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.glow.setAlpha(entity.getActiveTicks(partialTicks) / 60.0F + (!entity.isActive() ? entity.getHitTicks(partialTicks) / 20.0F * 0.45F : 0.0F));

		GlStateManager.pushMatrix();
		GlStateManager.translatef(0, (float)(Math.sin((entity.ticksExisted + partialTicks) / 10.0F) / 4.0F - 0.25F) * entity.getActiveTicks(partialTicks) / 60.0F - 0.9F * (1.0F - entity.getActiveTicks(partialTicks) / 60.0F), 0);
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		GlStateManager.popMatrix();

		double rx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double ry = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double rz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

		if(!entity.isActive() && entity.getHitTicks(partialTicks) > 8 && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			float hitTicks = entity.getHitTicks(partialTicks);
			float brightness = 1.0F - (float)(Math.cos((hitTicks - 8) / 12.0F * 2.0F * Math.PI) + 1.0F) / 2.0F;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry + 0.5D, rz,
					1.8f,
					134.0f / 255.0f * 3.0F * brightness,
					214.0f / 255.0f * 3.0F * brightness,
					55.0f / 255.0f * 3.0F * brightness));
		}

		if(entity.getGlowTicks(partialTicks) > 0 && ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			float glow = entity.getGlowTicks(partialTicks) / 10.0F;
			ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(rx, ry + entity.getEyeHeight(), rz,
					4.75F * glow + 1.0F,
					134.0f / 255.0f * 10.0F * glow,
					214.0f / 255.0f * 10.0F * glow,
					55.0f / 255.0f * 10.0F * glow));
		}
	}
}
