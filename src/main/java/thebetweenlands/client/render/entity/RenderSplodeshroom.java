package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.entity.ModelSplodeshroom;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.EntitySplodeshroom;
import thebetweenlands.common.lib.ModInfo;

public class RenderSplodeshroom extends RenderLiving<EntitySplodeshroom> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/splodeshroom.png");
	public static final ResourceLocation TEXTURE_GAS = new ResourceLocation(ModInfo.ID, "textures/particle/gas_cloud.png");

	public RenderSplodeshroom(RenderManager manager) {
		super(manager, new ModelSplodeshroom(), 0F);
	}

	@Override
	public void doRender(EntitySplodeshroom entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		if (entity.getHasExploded()) {
			GlStateManager.disableLighting();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
			GlStateManager.depthMask(false);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
				ShaderHelper.INSTANCE.require();
				// Use animated shader texture
				GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
			} else {
				// Use static texture
				this.bindTexture(TEXTURE_GAS);
			}

			if (Minecraft.getMinecraft().getRenderViewEntity() != null)
				BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.GAS_CLOUDS, Minecraft.getMinecraft().getRenderViewEntity(), partialTicks);

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			GlStateManager.enableLighting();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySplodeshroom entity) {
		return TEXTURE;
	}
}