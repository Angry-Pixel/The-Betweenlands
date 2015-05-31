package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.model.entity.ModelSporeling;
import thebetweenlands.entities.mobs.EntitySporeling;
import thebetweenlands.utils.LightingUtil;

public class RenderSporeling extends RenderLiving {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/sporeling.png");
	private static final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/sporelingGlow.png");

	public RenderSporeling() {
		super(new ModelSporeling(), 0.3F);
		setRenderPassModel(new ModelSporeling());
	}

	protected int setSporelingEyeBrightness(EntitySporeling entity, int pass, float partialTickTime) {
		if(pass == 1){
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

	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
		return setSporelingEyeBrightness((EntitySporeling) entity, pass, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
