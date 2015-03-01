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
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			char var5 = 61680;
			int var6 = var5 % 65536;
			int var7 = var5 / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
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