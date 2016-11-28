package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityGasCloud;

@SideOnly(Side.CLIENT)
public class RenderGasCloud extends Render<EntityGasCloud> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/gas_cloud.png");

	public RenderGasCloud(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityGasCloud entity, double x, double y, double z, float yaw, float partialTicks) {
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.004F);
		GlStateManager.depthMask(false);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			//Use animated shader texture
			GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
		} else {
			//Use static texture
			this.bindTexture(TEXTURE);
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		this.renderGasParticles(buffer, entity, partialTicks);
		tessellator.draw();
		
		GlStateManager.depthMask(true);
		GlStateManager.color(0.0F, 1.0F, 0.0F, 1.0F);
		
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			//Render particles to gas fbo
			ShaderHelper.INSTANCE.getWorldShader().getGasParticleBuffer().bind();
			
			//GlStateManager.disableBlend();
			
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
			this.renderGasParticles(buffer, entity, partialTicks);
			tessellator.draw();
			
			//GlStateManager.enableBlend();
			
			Minecraft.getMinecraft().getFramebuffer().bindFramebuffer(false);
		}

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableLighting();
	}
	
	private void renderGasParticles(VertexBuffer buffer, EntityGasCloud entity, float partialTicks) {
		for (Object obj: entity.gasParticles) {
			ParticleGasCloud particle = (ParticleGasCloud) obj;
			particle.renderParticleFullTexture(buffer, Minecraft.getMinecraft().thePlayer, partialTicks,
					ActiveRenderInfo.getRotationX(),
					ActiveRenderInfo.getRotationXZ(),
					ActiveRenderInfo.getRotationZ(),
					ActiveRenderInfo.getRotationYZ(),
					ActiveRenderInfo.getRotationXY());
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGasCloud entity) {
		return null;
	}
}
