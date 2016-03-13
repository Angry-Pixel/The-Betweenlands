package thebetweenlands.client.render.tileentity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.model.block.ModelSpawnerCrystal;
import thebetweenlands.tileentities.spawner.MobSpawnerBaseLogicBL;
import thebetweenlands.tileentities.spawner.TileEntityBLSpawner;
import thebetweenlands.utils.LightingUtil;

public class TileEntityBLSpawnerRenderer extends TileEntitySpecialRenderer {
	private ModelSpawnerCrystal spawnerCrystalModel = new ModelSpawnerCrystal();
	private final static ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/spawnerCrystal.png");

	public static void renderSpawnerMob(MobSpawnerBaseLogicBL spawnerLogic, double x, double y, double z, float partialTicks) {
		Entity entity = spawnerLogic.getCachedEntity();
		if (entity != null) {
			entity.setWorld(spawnerLogic.getSpawnerWorld());
			float scale = 0.4375F;
			GL11.glTranslatef(0.0F, 0.4F, 0.0F);
			GL11.glRotatef((float)(spawnerLogic.lastEntityRotation + (spawnerLogic.entityRotation - spawnerLogic.lastEntityRotation) * (double)partialTicks) * 10.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.4F, 0.0F);
			GL11.glScalef(scale, scale, scale);
			entity.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
			RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
		TileEntityBLSpawner te = (TileEntityBLSpawner)tileEntity;
		float interpolatedCounter = -(te.counter + (te.counter - te.lastCounter) * partialTicks);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		LightingUtil.INSTANCE.setLighting(255);

		float counter1 = interpolatedCounter;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.475F, y + 0.38F + (float)Math.sin(counter1) / 5.0F, z + 0.475F);
		GL11.glScalef(0.3F, 0.3F, 0.3F);
		renderSpawnerMob(te.getSpawnerLogic(), 0, 0, 0, partialTicks);
		GL11.glPopMatrix();

		this.bindTexture(TEXTURE);

		GL11.glPushMatrix();
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glTranslated(x + 0.45F, y + 1.8F + (float)Math.sin(counter1) / 5.0F, z + 0.45F);
		GL11.glTranslatef(0.025F, -0.5F, 0.025F);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef((float)counter1 * 180, 0, 1, 0);
		GL11.glScalef(2.5F, 2.5F, 2.5F);
		GL11.glColor4f(1, 4 + (float)Math.sin(counter1) * 3, (float)Math.sin(counter1) * 2, 0.5F);
		this.spawnerCrystalModel.render();
		GL11.glPopMatrix();

		float counter2 = interpolatedCounter / 1.5F;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.475F, y + 1.5F + (float)Math.sin(counter2 * 3) / 2.0F - 0.5F, z + 0.475F);
		GL11.glTranslatef(0.025F, -0.5F, 0.025F);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef((float)counter2 * 720, 0, 1, 0);
		GL11.glTranslatef(0.5F, 0, 0);
		GL11.glScalef(2.25F, 2.25F, 2.25F);
		GL11.glScalef(0.2F + (float)Math.sin(counter2) * (float)Math.sin(counter2) / 2, 0.2F + (float)Math.sin(counter2) * (float)Math.sin(counter2) / 2, 0.2F + (float)Math.sin(counter2) * (float)Math.sin(counter2) / 2);
		GL11.glColor4f(1, 4 + (float)Math.sin(counter2) * 3, (float)Math.sin(counter2) * 2, (float)Math.cos(counter2) * (float)Math.cos(counter2) * 2);
		this.spawnerCrystalModel.render();
		GL11.glPopMatrix();

		float counter3 = interpolatedCounter / 2.0F;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.475F, y + 1.5F - (float)Math.cos(counter3 * 3) / 2.0F, z + 0.475F);
		GL11.glTranslatef(0.025F, -0.5F, 0.025F);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef((float)counter1 * 720, 0, 1, 0);
		GL11.glTranslatef(0, 0, 0.5F);
		GL11.glScalef(2.25F, 2.25F, 2.25F);
		GL11.glScalef(0.2F + (float)Math.sin(counter3) * (float)Math.sin(counter3) / 2, 0.2F + (float)Math.sin(counter3) * (float)Math.sin(counter3) / 2, 0.2F + (float)Math.sin(counter3) * (float)Math.sin(counter3) / 2);
		GL11.glColor4f(1, 4 + (float)Math.sin(counter3) * 3, (float)Math.sin(counter3) * 2, (float)Math.cos(counter3) * (float)Math.cos(counter3) * 2);
		this.spawnerCrystalModel.render();
		GL11.glPopMatrix();

		float counter4 = interpolatedCounter / 2.5F;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.475F, y + 1.5F + (float)Math.cos(counter4 * 3) / 2.0F - 0.5F, z + 0.475F);
		GL11.glTranslatef(0.025F, -0.5F, 0.025F);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef((float)counter1 * 720, 0, 1, 0);
		GL11.glTranslatef(0.5F, 0, 0.5F);
		GL11.glScalef(2.25F, 2.25F, 2.25F);
		GL11.glScalef(0.2F + (float)Math.sin(counter4) * (float)Math.sin(counter4) / 2, 0.2F + (float)Math.sin(counter4) * (float)Math.sin(counter4) / 2, 0.2F + (float)Math.sin(counter4) * (float)Math.sin(counter4) / 2);
		GL11.glColor4f(1, 4 + (float)Math.sin(counter4) * 3, (float)Math.sin(counter4) * 2, (float)Math.cos(counter4) * (float)Math.cos(counter4) * 2);
		this.spawnerCrystalModel.render();
		GL11.glPopMatrix();

		float counter5 = interpolatedCounter / 3.0F;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.475F, y + 1.5F + (float)Math.cos(counter5 * 3) / 2.0F - 0.5F, z + 0.475F);
		GL11.glTranslatef(0.025F, -0.5F, 0.025F);
		GL11.glRotatef(180F, 0F, 0F, 1F);
		GL11.glRotatef((float)counter1 * 720, 0, 1, 0);
		GL11.glTranslatef(0F, 0, -0.5F);
		GL11.glScalef(2.25F, 2.25F, 2.25F);
		GL11.glScalef(0.2F + (float)Math.sin(counter5) * (float)Math.sin(counter5) / 2, 0.2F + (float)Math.sin(counter5) * (float)Math.sin(counter5) / 2, 0.2F + (float)Math.sin(counter5) * (float)Math.sin(counter5) / 2);
		GL11.glColor4f(1, 4 + (float)Math.sin(counter5) * 3, (float)Math.sin(counter5) * 2, (float)Math.cos(counter5) * (float)Math.cos(counter5) * 2);
		this.spawnerCrystalModel.render();
		GL11.glPopMatrix();

		LightingUtil.INSTANCE.revert();
	}
}
