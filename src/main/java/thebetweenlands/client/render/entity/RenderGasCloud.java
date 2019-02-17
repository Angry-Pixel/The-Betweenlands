package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.client.render.particle.BatchedParticleRenderer;
import thebetweenlands.client.render.particle.DefaultParticleBatches;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityShallowbreath;

@OnlyIn(Dist.CLIENT)
public class RenderGasCloud extends Render<EntityShallowbreath> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/gas_cloud.png");

	public RenderGasCloud(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityShallowbreath entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
		GlStateManager.depthMask(false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			ShaderHelper.INSTANCE.require();
			
			//Use animated shader texture
			GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
		} else {
			//Use static texture
			this.bindTexture(TEXTURE);
		}

		if(Minecraft.getInstance().getRenderViewEntity() != null) {
			BatchedParticleRenderer.INSTANCE.renderBatch(DefaultParticleBatches.GAS_CLOUDS, Minecraft.getInstance().getRenderViewEntity(), partialTicks);
		}
		
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableLighting();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityShallowbreath entity) {
		return null;
	}
}
