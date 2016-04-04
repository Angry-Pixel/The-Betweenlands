package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.utils.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderWight extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/wight.png");

	private static final ModelWight model = new ModelWight();
	private static final ModelWight modelHeadOnly = new ModelWight().setRenderHeadOnly(true);

	public RenderWight() {
		super(model, 0.5F);
		this.renderPassModel = model;
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float partialTickTime) {
		if(ShaderHelper.INSTANCE.canUseShaders()) {
			ShaderHelper.INSTANCE.addDynLight(new LightSource(entityliving.posX, entityliving.posY, entityliving.posZ, 
					10.0f, 
					-1, 
					-1, 
					-1));
		}

		GL11.glColor4f(0, 0, 0, 0);
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entityliving, int pass, float partialTickTime) {
		EntityWight wight = (EntityWight) entityliving;

		if(wight.isVolatile()) {
			this.setRenderPassModel(this.modelHeadOnly);
			if(pass == 0) {
				GL11.glScalef(0.9F, 0.9F, 0.9F);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColorMask(false, false, false, false);
				GL11.glColor4f(1, 1, 1, 1);
				GL11.glPushMatrix();
				GL11.glTranslated(0, 1.65D, 0);
				GL11.glScaled(0.5D, 0.5D, 0.5D);
				if(wight.ridingEntity != null) {
					GL11.glRotated((wight.ticksExisted + partialTickTime) / 30.0D * 360.0D, 0, 1, 0);
					GL11.glRotated(180, 0, 1, 0);
					GL11.glTranslated(0, 0, 0.8D);
				}
				LightingUtil.INSTANCE.setLighting(255);
				return 1;
			} else if(pass == 1) {
				GL11.glColorMask(true, true, true, true);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(1F, 1F, 1F, 0.4F);
				return 1;
			} else if(pass == 2) {
				GL11.glPopMatrix();
				LightingUtil.INSTANCE.revert();
			}

			return -1;
		}

		this.setRenderPassModel(this.model);

		if(pass == 0) {
			GL11.glScalef(0.9F, 0.9F, 0.9F);
			GL11.glTranslated(0, 0.175D, 0);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glColorMask(false, false, false, false);
			GL11.glColor4f(1, 1, 1, 1);
			return 1;
		} else if(pass == 1) {
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F, 1F, 1F, 1F - wight.getAnimation() * 0.5F);
			return 1;
		}

		return -1;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
