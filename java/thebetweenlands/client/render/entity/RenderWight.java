package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.impl.LightSource;
import thebetweenlands.entities.mobs.EntityWight;
import thebetweenlands.utils.confighandler.ConfigHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWight extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/wight.png");

	public RenderWight() {
		super(new ModelWight(), 0.5F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		scaleWight((EntityWight) entityliving, f);
		
		if(ConfigHandler.USE_SHADER) {
			ShaderHelper.INSTANCE.addDynLight(new LightSource(entityliving.posX, entityliving.posY, entityliving.posZ, 
					10.0f, 
					-1, 
					-1, 
					-1));
		}
	}

	protected void scaleWight(EntityWight wight, float partialTickTime) {
		GL11.glScalef(0.9F, 0.9F, 0.9F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F, 1F, 1F, 1F - wight.getAnimation() * 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
