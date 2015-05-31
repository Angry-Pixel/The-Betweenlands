package thebetweenlands.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.model.entity.ModelSwampHag;
import thebetweenlands.entities.mobs.EntitySwampHag;
import thebetweenlands.utils.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderSwampHag extends RenderLiving {

	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/swampHag.png");
	private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/swampHagEyes.png");

	public RenderSwampHag() {
		super(new ModelSwampHag(), 0.5F);
		setRenderPassModel(new ModelSwampHag());
	}

	protected int setSwampHagEyeBrightness(EntitySwampHag entity, int pass, float partialTickTime) {
		if (pass == 1) {
			bindTexture(eyeTexture);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
			LightingUtil.INSTANCE.setLighting(255);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			return 1;
		} else if (pass == 2) {
			LightingUtil.INSTANCE.revert();
			GL11.glDisable(GL11.GL_BLEND);
		}

		return -1;
	}

    protected void preRenderCallback(EntityLivingBase entity, float par2) {
    	GL11.glScalef(0.74F, 0.74F, 0.74F);
    }

	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime) {
		return setSwampHagEyeBrightness((EntitySwampHag) entity, pass, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
