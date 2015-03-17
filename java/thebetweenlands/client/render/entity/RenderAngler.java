package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelAngler;
import thebetweenlands.entities.mobs.EntityAngler;

public class RenderAngler extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/angler.png");
	private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/anglerGlow.png");

	public RenderAngler() {
		super(new ModelAngler(), 0.5F);
		this.setRenderPassModel(new ModelAngler());
	}

	protected int setAnglerEyeBrightness(EntityAngler entity, int pass, float partialTickTime) {
		if (pass != 0) {
			return -1;
		} else {
			bindTexture(eyeTexture);
			float var4 = 1.0F;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) 100, (float) 100);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var4);
			return 1;
		}
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
		return this.setAnglerEyeBrightness((EntityAngler) entity, pass, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}