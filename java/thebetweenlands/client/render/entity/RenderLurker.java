package thebetweenlands.client.render.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelLurker;
import thebetweenlands.entities.mobs.EntityLurker;

public class RenderLurker extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/lurker.png");

	public RenderLurker() {
		super(new ModelLurker(), 0.5F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entity, float headPitch, float yaw, float partialRenderTicks) {
		super.rotateCorpse(entity, headPitch, yaw, partialRenderTicks);
		GL11.glRotatef(((EntityLurker) entity).getRotationPitch(partialRenderTicks), 1, 0, 0);
	}
}