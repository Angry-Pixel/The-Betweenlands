package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelTarBeast;
import thebetweenlands.entities.mobs.EntityTarBeast;
import thebetweenlands.utils.LightingUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTarBeast extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/tarBeast.png");
	private final ResourceLocation eyeTexture = new ResourceLocation("thebetweenlands:textures/entity/tarBeastEyes.png");
	private final ResourceLocation overlayTexture = new ResourceLocation("thebetweenlands:textures/entity/tarBeastOverlay.png");
	public RenderTarBeast() {
		super(new ModelTarBeast(), 0.7F);
		setRenderPassModel(new ModelTarBeast());
	}

	protected int setTarBeastEyeBrightness(EntityTarBeast entity, int pass, float partialTickTime) {
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
	
	@Override
	protected int shouldRenderPass(EntityLivingBase entityliving, int pass, float partialTickTime) {
		EntityTarBeast tarBeast = (EntityTarBeast) entityliving;
			if (tarBeast.isInvisible())
				GL11.glDepthMask(false);
			else
				GL11.glDepthMask(true);

			if (pass == 1) {
				float scrollTimer = tarBeast.ticksExisted + partialTickTime;
				bindTexture(overlayTexture);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				float yScroll = scrollTimer * 0.002F;
				GL11.glTranslatef(0F, -yScroll, 0.0F);
				setRenderPassModel(renderPassModel);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glEnable(GL11.GL_BLEND);
				float colour = 0.5F;
				GL11.glColor4f(colour, colour, colour, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
				return 1;
			}

			if (pass == 2) {
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
			}

		return setTarBeastEyeBrightness((EntityTarBeast) entityliving, pass, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}