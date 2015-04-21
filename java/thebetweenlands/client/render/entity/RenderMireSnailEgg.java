package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelSnailEgg;
import thebetweenlands.entities.mobs.EntityMireSnailEgg;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMireSnailEgg extends RenderLiving {
	private static final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/mireSnailEgg.png");

	public RenderMireSnailEgg() {
		super(new ModelSnailEgg(), 0.1F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		EntityMireSnailEgg egg = (EntityMireSnailEgg)entityliving;
		GL11.glScalef(2.5F + egg.pulseFloat, 2.5F + egg.pulseFloat, 2.5F + egg.pulseFloat);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}