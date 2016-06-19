package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.mobs.EntityGasCloud;
import thebetweenlands.entities.particles.EntityGasCloudFX;

@SideOnly(Side.CLIENT)
public class RenderGasCloud extends Render {
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX, entity.posY, entity.posZ, 
					2.5f, 
					-1, 
					-1, 
					-1));
		}

		EntityGasCloud gasCloud = (EntityGasCloud) entity;

		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.004F);
		GL11.glDepthMask(false);
		GL11.glColor4f(1, 1, 1, 1);

		if(ShaderHelper.INSTANCE.canUseShaders()) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShaderHelper.INSTANCE.getCurrentShader().getGasTextureID());
		} else {
			this.bindTexture(EntityGasCloudFX.TEXTURE);
		}

		Tessellator tessellator = Tessellator.instance;
		//Render without depth masking to ignore depth test
		tessellator.startDrawingQuads();
		for(Object o : gasCloud.gasParticles) {
			((EntityGasCloudFX)o).renderParticle(tessellator, partialTicks, 
					ActiveRenderInfo.rotationX,
					ActiveRenderInfo.rotationXZ,
					ActiveRenderInfo.rotationZ,
					ActiveRenderInfo.rotationYZ,
					ActiveRenderInfo.rotationXY);
		}
		tessellator.draw();

		//Render to depth buffer
		/*GL11.glDepthMask(true);
		GL11.glColorMask(false, false, false, false);
		tessellator.startDrawingQuads();
		tessellator.setBrightness(0);
		for(EntityGasCloudFX gasCloudParticle : gasCloud.gasParticles) {
			gasCloudParticle.renderParticle(tessellator, partialTicks, 
					ActiveRenderInfo.rotationX,
					ActiveRenderInfo.rotationXZ,
					ActiveRenderInfo.rotationZ,
					ActiveRenderInfo.rotationYZ,
					ActiveRenderInfo.rotationXY);
		}
		tessellator.draw();
		GL11.glColorMask(true, true, true, true);*/

		GL11.glDepthMask(true);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glEnable(GL11.GL_LIGHTING);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
