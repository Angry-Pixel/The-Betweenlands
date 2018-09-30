package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.BufferBuilder;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.client.render.shader.GeometryBuffer;
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
			ShaderHelper.INSTANCE.require();
			
			//Use animated shader texture
			GlStateManager.bindTexture(ShaderHelper.INSTANCE.getWorldShader().getGasTexture());
		} else {
			//Use static texture
			this.bindTexture(TEXTURE);
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
		this.renderGasParticles(buffer, entity, partialTicks);
		tessellator.draw();

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableLighting();
		
		if(ShaderHelper.INSTANCE.isWorldShaderActive()) {
			WorldRenderHandler.GAS_CLOUDS.add(Pair.of(Pair.of(this, entity), new Vec3d(x, y, z)));
		}
	}

	public void renderGasParticles(BufferBuilder buffer, EntityGasCloud entity, float partialTicks) {
		for (Object obj: entity.gasParticles) {
			ParticleGasCloud particle = (ParticleGasCloud) obj;

			if(!entity.isEntityAlive()) {
				int[] gasColor = entity.getGasColor();
				float fade = (1.0F - (entity.deathTime / 80.0F));
				particle.setAlphaF((float)Math.pow(fade, 2) * gasColor[3] / 255.0F);
				particle.setRBGColorF(gasColor[0] / 255.0F * fade, gasColor[1] / 255.0F * fade, gasColor[2] / 255.0F * fade);
			}

			particle.renderParticleFullTexture(buffer, Minecraft.getMinecraft().player, partialTicks,
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
