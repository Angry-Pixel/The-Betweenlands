package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelSnailEgg;
import thebetweenlands.entities.mobs.EntityDragonFly;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
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
		GL11.glScalef(2.5F, 2.5F, 2.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return texture;
	}
}