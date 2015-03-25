package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelDragonFly;
import thebetweenlands.entities.mobs.EntityDragonFly;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDragonFly extends RenderLiving {
	private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/entity/dragonFly.png");

	public RenderDragonFly() {
		super(new ModelDragonFly(), 0.5F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float f) {
		scaleDragonFly((EntityDragonFly) entityliving, f);
	}

	protected void scaleDragonFly(EntityDragonFly dragonFly, float partialTickTime) {
		GL11.glScalef(0.6F, 0.6F, 0.6F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}

}
