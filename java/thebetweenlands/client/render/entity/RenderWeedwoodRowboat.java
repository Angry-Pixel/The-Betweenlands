package thebetweenlands.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thebetweenlands.client.model.entity.ModelWeedwoodRowboat;
import thebetweenlands.entities.rowboat.EntityWeedwoodRowboat;
import thebetweenlands.lib.ModInfo;

public class RenderWeedwoodRowboat extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID + ":textures/entity/weedwoodRowboat.png");

	public static boolean notRenderingPilot = true;

	private ModelWeedwoodRowboat model;

	private int maskId = -1;

	public RenderWeedwoodRowboat() {
		model = new ModelWeedwoodRowboat();
		shadowSize = 0;
	}

	public void doRender(Entity entity, double x, double y, double z, float yaw, float delta) {
		EntityWeedwoodRowboat rowboat = (EntityWeedwoodRowboat) entity;
		renderPilot(rowboat, delta);
		GL11.glPushMatrix();
		GL11.glTranslated(x, y + 1, z);
		GL11.glRotatef(270 - yaw, 0, 1, 0);
		float timeSinceHit = rowboat.getTimeSinceHit() - delta;
		float damageTaken = rowboat.getDamageTaken() - delta;
		if (damageTaken < 0) {
			damageTaken = 0;
		}
		if (timeSinceHit > 0) {
			GL11.glRotatef(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10 * rowboat.getForwardDirection(), 0, 0, 1);
		}
		GL11.glPushMatrix();
		bindEntityTexture(entity);
		GL11.glScalef(-1, -1, 1);
		model.render(rowboat, 0.0625F, delta);
		GL11.glPopMatrix();
		renderWaterMask();
		GL11.glPopMatrix();
	}

	private void renderPilot(EntityWeedwoodRowboat boat, float delta) {
		Entity pilot = boat.riddenByEntity;
		if (pilot == null) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		if (pilot != mc.thePlayer || mc.gameSettings.thirdPersonView != 0) {
			notRenderingPilot = false;
			renderManager.renderEntitySimple(pilot, delta);
			notRenderingPilot = true;
		}
	}

	private void renderWaterMask() {
		if (maskId == -1) {
			maskId = GLAllocation.generateDisplayLists(1);
			GL11.glNewList(maskId, GL11.GL_COMPILE);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			double y = -0.687;
			double midWidth = 0.55;
			double midDepth = 0.65;
			double endWidth = 0.4;
			double endDepth = 0.16;
			double endOffset = 0.81;
			tessellator.addVertex(-midWidth, y, midDepth);
			tessellator.addVertex(midWidth, y, midDepth);
			tessellator.addVertex(midWidth, y, -midDepth);
			tessellator.addVertex(-midWidth, y, -midDepth);
			tessellator.addVertex(-endWidth, y, endDepth - endOffset);
			tessellator.addVertex(endWidth, y, endDepth - endOffset);
			tessellator.addVertex(endWidth, y, -endDepth - endOffset);
			tessellator.addVertex(-endWidth, y, -endDepth - endOffset);
			tessellator.addVertex(-endWidth, y, endDepth + endOffset);
			tessellator.addVertex(endWidth, y, endDepth + endOffset);
			tessellator.addVertex(endWidth, y, -endDepth + endOffset);
			tessellator.addVertex(-endWidth, y, -endDepth + endOffset);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColorMask(false, false, false, false);
			tessellator.draw();
			GL11.glColorMask(true, true, true, true);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEndList();
		}
		GL11.glCallList(maskId);
	}

	protected ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}
