package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelBloodSnail;
import thebetweenlands.entities.mobs.EntityBloodSnail;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBloodSnail extends RenderLiving {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/bloodSnail.png");

	public RenderBloodSnail() {
		super(new ModelBloodSnail(), 0.5F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float partialTickTime) {
		EntityBloodSnail snail = (EntityBloodSnail) entityliving;
		if (snail.isClimbing())
			rotateSnail(entityliving);
	}

	protected void rotateSnail(EntityLivingBase entityliving) {
		GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}