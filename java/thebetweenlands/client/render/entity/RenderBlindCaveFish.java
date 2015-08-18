package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelAngler;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.entities.mobs.EntityAngler;
import thebetweenlands.entities.mobs.EntityDragonFly;
import thebetweenlands.utils.LightingUtil;
import thebetweenlands.utils.confighandler.ConfigHandler;

public class RenderBlindCaveFish extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/blindCaveFish.png");

	public RenderBlindCaveFish() {
		super(new ModelAngler(), 0.5F);
		setRenderPassModel(new ModelAngler());
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		GL11.glTranslatef(0F, 0.5F, 0F);
		EntityAngler angler = (EntityAngler) entityliving;
		if(angler.isGrounded() && !angler.isLeaping()) {
			GL11.glRotatef(90F, 0F, 0F, 1F);
			GL11.glTranslatef(-0.7F, 0.7F, 0F);
		}
		
		scaleDragonFly((EntityDragonFly) entityliving, f);
	}

	protected void scaleDragonFly(EntityDragonFly dragonFly, float partialTickTime) {
		GL11.glScalef(0.2F, 0.2F, 0.2F);
	}	

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}