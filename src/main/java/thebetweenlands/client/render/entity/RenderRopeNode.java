package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.util.LightingUtil;

public class RenderRopeNode extends Render<EntityRopeNode> {
	private Frustum frustum;

	public RenderRopeNode(RenderManager renderManager) {
		super(renderManager);
		this.frustum = new Frustum();
	}

	@Override
	public void doRender(EntityRopeNode ropeNode, double x, double y, double z, float yaw, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();

		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.color(1, 0.3F, 0.1F, 1.0F);
		LightingUtil.INSTANCE.setLighting(255);

		AxisAlignedBB boundingBox = ropeNode.getEntityBoundingBox().offset(-ropeNode.posX, -ropeNode.posY, -ropeNode.posZ);

		if(ropeNode.getNextNode() == null) {
			boundingBox = boundingBox.grow(0.025D, 0.025D, 0.025D);
			GlStateManager.color(1, 0.8F, 0.05F, 1.0F);
		}
		
		buffer.setTranslation(x, y, z);
		buffer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
		buffer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
		tessellator.draw();
		buffer.setTranslation(0.0D, 0.0D, 0.0D);

		LightingUtil.INSTANCE.revert();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();

		double camPosX = this.interpolate(ropeNode.lastTickPosX, ropeNode.posX, partialTicks) - x;
		double camPosY = this.interpolate(ropeNode.lastTickPosY, ropeNode.posY, partialTicks) - y;
		double camPosZ = this.interpolate(ropeNode.lastTickPosZ, ropeNode.posZ, partialTicks) - z;

		this.frustum.setPosition(camPosX, camPosY, camPosZ);

		Entity prevNode = ropeNode.getPreviousNode();

		if(prevNode != null) {
			if(!this.renderManager.getEntityRenderObject(prevNode).shouldRender(prevNode, this.frustum, camPosX, camPosY, camPosZ)) {
				//Previous node not rendered, render rope
				this.renderConnection(ropeNode, prevNode, tessellator, buffer, x, y, z, partialTicks);
			}
		}

		Entity nextNode = ropeNode.getNextNode();

		if(nextNode != null) {
			this.renderConnection(ropeNode, nextNode, tessellator, buffer, x, y, z, partialTicks);
		}

		GlStateManager.popMatrix();
	}

	protected double interpolate(double prev, double now, double partialTicks) {
		return prev + (now - prev) * partialTicks;
	}

	protected void renderConnection(Entity node1, Entity node2, Tessellator tessellator, BufferBuilder buffer, double x, double y, double z, float partialTicks) {
		if(node2 != null) {
			double camPosX = this.interpolate(node1.prevPosX - x, node1.posX - x, partialTicks);
			double camPosY = this.interpolate(node1.prevPosY - y, node1.posY - y, partialTicks);
			double camPosZ = this.interpolate(node1.prevPosZ - z, node1.posZ - z, partialTicks);

			double startX = x;
			double startY = y;
			double startZ = z;
			double endX = this.interpolate(node2.prevPosX - camPosX, node2.posX - camPosX, partialTicks);
			double endY = this.interpolate(node2.prevPosY - camPosY, node2.posY - camPosY, partialTicks);
			if(node2 instanceof EntityRopeNode == false) {
				endY += node2.getEyeHeight() / 2.0D;
			}
			double endZ = this.interpolate(node2.prevPosZ - camPosZ, node2.posZ - camPosZ, partialTicks);

			double diffX = (double)((float)(endX - startX));
			double diffY = (double)((float)(endY - startY));
			double diffZ = (double)((float)(endZ - startZ));

			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();

			buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			for (int i = 0; i <= 24; ++i) {
				float r;
				float g;
				float b;

				if (i % 2 == 0) {
					r = 0.0F;
					g = 0.4F;
					b = 0.0F;
				} else {
					r = 0.0F;
					g = 0.28F;
					b = 0.0F;
				}

				float percentage = (float)i / 24.0F;
				double yMult = endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;

				buffer.pos(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
				buffer.pos(x + diffX * (double)percentage + 0.025D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.025D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
			}
			tessellator.draw();

			buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
			for (int i = 0; i <= 24; ++i) {
				float r;
				float g;
				float b;

				if (i % 2 == 0) {
					r = 0.0F;
					g = 0.4F;
					b = 0.0F;
				} else {
					r = 0.0F;
					g = 0.28F;
					b = 0.0F;
				}

				float percentage = (float)i / 24.0F;
				double yMult = endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;

				buffer.pos(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.025D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
				buffer.pos(x + diffX * (double)percentage + 0.025D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage + 0.025D).color(r, g, b, 1).endVertex();
			}
			tessellator.draw();

			GlStateManager.enableLighting();
			GlStateManager.enableTexture2D();
			GlStateManager.enableCull();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRopeNode entity) {
		return null;
	}
}
