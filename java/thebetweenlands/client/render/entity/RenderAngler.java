package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.model.entity.ModelAngler;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.impl.LightSource;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.utils.LightingUtil;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class RenderAngler extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/angler.png");
	private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/anglerGlow.png");

	public RenderAngler() {
		super(new ModelAngler(), 0.5F);
		setRenderPassModel(new ModelAngler());
	}

	protected int setAnglerEyeBrightness(EntityAngler entity, int pass, float partialTickTime) {
		if(pass == 1) {
			if(ConfigHandler.USE_SHADER && ConfigHandler.FIREFLY_LIGHTING) {
				double xOff = Math.sin(Math.toRadians(-entity.renderYawOffset)) * 0.8f;
				double zOff = Math.cos(Math.toRadians(-entity.renderYawOffset)) * 0.8f;
				ShaderHelper.INSTANCE.addDynLight(new LightSource(entity.posX + xOff, entity.posY + 0.95f, entity.posZ + zOff, 
						1.0f, 
						10.0f / 255.0f, 
						30.0f / 255.0f, 
						30.0f / 255.0f));
			}
			
			bindTexture(eyeTexture);
			float var4 = 1.0F;
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
			LightingUtil.INSTANCE.setLighting(255);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, var4);
			return 1;
		} else if(pass == 2) {
			LightingUtil.INSTANCE.revert();
			GL11.glDisable(GL11.GL_BLEND);
		}
		return -1;
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
		return setAnglerEyeBrightness((EntityAngler) entity, pass, partialTickTime);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		GL11.glTranslatef(0F, 0.5F, 0F);
		EntityAngler angler = (EntityAngler) entityliving;
		if(angler.isGrounded() && !angler.isLeaping()) {
			GL11.glRotatef(90F, 0F, 0F, 1F);
			GL11.glTranslatef(-0.7F, 0.7F, 0F);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}