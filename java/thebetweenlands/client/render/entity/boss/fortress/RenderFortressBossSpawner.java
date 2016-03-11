package thebetweenlands.client.render.entity.boss.fortress;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.entity.ModelWight;
import thebetweenlands.entities.mobs.boss.fortress.EntityFortressBossSpawner;

public class RenderFortressBossSpawner extends Render {
	private static final ModelWight MODEL = new ModelWight();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		EntityFortressBossSpawner spawner = (EntityFortressBossSpawner) entity;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(x, y + 1.5F, z);
		this.bindTexture(new ResourceLocation("thebetweenlands:textures/entity/wight.png"));
		GL11.glRotated(180, 1, 0, 0);
		float progress = 1.0F - (float)spawner.spawnDelay / spawner.maxSpawnDelay;
		GL11.glColor4f(progress, progress, progress, 1);
		MODEL.render(entity, 0, 0, entity.ticksExisted + partialTicks, 0, 0, 0.065F);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
		return null;
	}
}
