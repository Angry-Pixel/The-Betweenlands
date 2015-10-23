package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelTarminion;
import thebetweenlands.entities.mobs.EntityTarminion;

public class RenderTarminion extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/tarminion.png");
	public final ResourceLocation textureDrip = new ResourceLocation("thebetweenlands:textures/entity/tarminionOverlay.png");
	private final ModelTarminion renderPassModel = new ModelTarminion();

	public RenderTarminion() {
		super(new ModelTarminion(), 0.16F);
	}
	
	@Override
	protected int shouldRenderPass(EntityLivingBase entityliving, int pass, float partialTickTime) {
		EntityTarminion tarminion = (EntityTarminion) entityliving;
		
			if (tarminion.isInvisible())
				GL11.glDepthMask(false);
			else
				GL11.glDepthMask(true);

			if (pass == 0) {
				float scrollTimer = tarminion.ticksExisted + partialTickTime;
				bindTexture(textureDrip);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				float yScroll = scrollTimer * 0.008F;
				GL11.glTranslatef(0F, -yScroll, 0.0F);
				setRenderPassModel(renderPassModel);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				float colour = 0.5F;
				GL11.glColor4f(colour, colour, colour, 1.0F);
				GL11.glDisable(GL11.GL_LIGHTING);
				return 1;
			}

			if (pass == 1) {
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glEnable(GL11.GL_LIGHTING);
			}

		return -1;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}
