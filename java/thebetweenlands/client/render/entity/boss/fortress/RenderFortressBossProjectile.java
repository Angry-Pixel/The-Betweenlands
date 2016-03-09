package thebetweenlands.client.render.entity.boss.fortress;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelSwordEnergy;

public class RenderFortressBossProjectile extends Render {
	private static final ResourceLocation FORCE_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
	private static final ModelSwordEnergy MODEL = new ModelSwordEnergy();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime) {
		renderBullet(entity, x, y, z, rotationYaw, partialTickTime);
	}

	public void renderBullet(Entity energyBall, double x, double y, double z, float rotationYaw, float partialTickTime) {
		float ticks = (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y - 0.6D * 0.6D, z);
		FMLClientHandler.instance().getClient().getTextureManager().bindTexture(FORCE_TEXTURE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float interpTicks = ticks + partialTickTime;
		float uOffset = interpTicks * 0.01F;
		float vOffset = interpTicks * 0.01F;
		GL11.glTranslatef(uOffset, vOffset, 0.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		GL11.glScaled(0.6D, 0.6D, 0.6D);
		if(energyBall.ridingEntity != null)
			GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glColor4f(0.8F, 0.0F, 0.4F, 1.0F);
		MODEL.render(0.0625F);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
