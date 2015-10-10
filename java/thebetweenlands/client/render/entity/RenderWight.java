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

@SideOnly(Side.CLIENT)
public class RenderWight extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/wight.png");
	
	private static final ModelWight model = new ModelWight();
	
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
		
		if(pass == 0) {
			GL11.glScalef(0.9F, 0.9F, 0.9F);
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
