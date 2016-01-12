package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.entities.EntityRopeNode;
import thebetweenlands.utils.LightingUtil;

@SideOnly(Side.CLIENT)
public class RenderRopeNode extends Render {

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
		if(entity instanceof EntityRopeNode) {
			EntityRopeNode ropeNode = (EntityRopeNode) entity;
			Entity nextNode = ropeNode.getNextNode();

			Tessellator tessellator = Tessellator.instance;

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			LightingUtil.INSTANCE.setLighting(255);
			GL11.glColor4f(1F, 0.3F, 0.1F, 1);
			Render.renderAABB(AxisAlignedBB.getBoundingBox(-0.05D+x, -0.05D+y, -0.05D+z, 0.05D+x, 0.05D+y, 0.05D+z));
			LightingUtil.INSTANCE.revert();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_LIGHTING);

			if(nextNode != null) {
				double startX = x;
				double startY = y;
				double startZ = z;
				double endX = this.interpolate(nextNode.prevPosX - RenderManager.renderPosX, nextNode.posX - RenderManager.renderPosX, partialTicks);
				double endY = this.interpolate(nextNode.prevPosY - RenderManager.renderPosY, nextNode.posY - RenderManager.renderPosY, partialTicks);
				if(nextNode instanceof EntityRopeNode == false) {
					endY += nextNode.getEyeHeight() / 2.0D;
				}
				if(nextNode == Minecraft.getMinecraft().renderViewEntity) {
					endY -= nextNode.posY - nextNode.boundingBox.minY;
					endY += 0.7D;
				}
				double endZ = this.interpolate(nextNode.prevPosZ - RenderManager.renderPosZ, nextNode.posZ - RenderManager.renderPosZ, partialTicks);

				double diffX = (double)((float)(endX - startX));
				double diffY = (double)((float)(endY - startY));
				double diffZ = (double)((float)(endZ - startZ));

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_CULL_FACE);

				tessellator.startDrawing(5);
				for (int i = 0; i <= 24; ++i) {
					if (i % 2 == 0) {
						tessellator.setColorRGBA_F(0.0F, 0.4F, 0.0F, 1.0F);
					} else {
						tessellator.setColorRGBA_F(0.0F, 0.28F, 0.0F, 1.0F);
					}

					float percentage = (float)i / 24.0F;
					double yMult = endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;
					tessellator.addVertex(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage);
					tessellator.addVertex(x + diffX * (double)percentage + 0.025D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.025D, z + diffZ * (double)percentage);
				}

				tessellator.draw();
				tessellator.startDrawing(5);

				for (int i = 0; i <= 24; ++i) {
					if (i % 2 == 0) {
						tessellator.setColorRGBA_F(0.0F, 0.4F, 0.0F, 1.0F);
					} else {
						tessellator.setColorRGBA_F(0.0F, 0.28F, 0.0F, 1.0F);
					}

					float percentage = (float)i / 24.0F;
					double yMult = endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;
					tessellator.addVertex(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.025D, z + diffZ * (double)percentage);
					tessellator.addVertex(x + diffX * (double)percentage + 0.025D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage + 0.025D);
				}

				tessellator.draw();
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_CULL_FACE);
			}
		}
	}

	private double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}
}
