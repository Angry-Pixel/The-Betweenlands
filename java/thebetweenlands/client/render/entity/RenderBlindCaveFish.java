package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelBlindCaveFish;

public class RenderBlindCaveFish extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/blindCaveFish.png");

	public RenderBlindCaveFish() {
		super(new ModelBlindCaveFish(), 0.5F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		GL11.glTranslatef(0F, 0.1F, -0.1F);
		GL11.glScalef(0.2F, 0.2F, 0.2F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}