package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.model.entity.ModelRopeNode;
import thebetweenlands.client.render.particle.entity.ParticleBeam;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.util.LightingUtil;

public class RenderGrapplingHookNode extends Render<EntityGrapplingHookNode> {
	private Frustum frustum;

	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/grappling_hook_rope.png");
	
	protected static final ModelRopeNode nodeModel = new ModelRopeNode();
	
	public RenderGrapplingHookNode(RenderManager renderManager) {
		super(renderManager);
		this.frustum = new Frustum();
	}

	@Override
	public void doRender(EntityGrapplingHookNode entity, double x, double y, double z, float yaw, float partialTicks) {
		this.bindEntityTexture(entity);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 0.35F);
		LightingUtil.INSTANCE.setLighting(255);

		AxisAlignedBB boundingBox = entity.getEntityBoundingBox().offset(-entity.posX, -entity.posY, -entity.posZ);

		if(entity.getNextNodeClient() == null) {
			boundingBox = boundingBox.grow(0.025D, 0.025D, 0.025D);
			GlStateManager.color(0.25F, 1.0F, 0.25F, 0.35F);
		}
		
		/*GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		nodeModel.render(entity, 0, 0, 0, 0, 0, 0.0625F);
		GlStateManager.popMatrix();*/
		
		GlStateManager.color(1, 1, 1, 1);

		LightingUtil.INSTANCE.revert();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();

		double camPosX = this.interpolate(entity.lastTickPosX, entity.posX, partialTicks) - x;
		double camPosY = this.interpolate(entity.lastTickPosY, entity.posY, partialTicks) - y;
		double camPosZ = this.interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - z;

		this.frustum.setPosition(camPosX, camPosY, camPosZ);

		Entity prevNode = entity.getPreviousNodeClient();

		if(prevNode != null) {
			if(!this.renderManager.getEntityRenderObject(prevNode).shouldRender(prevNode, this.frustum, camPosX, camPosY, camPosZ)) {
				//Previous node not rendered, render rope
				GlStateManager.pushMatrix();
				double renderOffsetX = this.interpolate(prevNode.lastTickPosX - entity.lastTickPosX, prevNode.posX - entity.posX, partialTicks);
				double renderOffsetY = this.interpolate(prevNode.lastTickPosY - entity.lastTickPosY, prevNode.posY - entity.posY, partialTicks);
				double renderOffsetZ = this.interpolate(prevNode.lastTickPosZ - entity.lastTickPosZ, prevNode.posZ - entity.posZ, partialTicks);
				GlStateManager.translate(renderOffsetX, renderOffsetY, renderOffsetZ);
				this.renderConnection(prevNode, entity, tessellator, buffer, x, y, z, partialTicks);
				GlStateManager.popMatrix();
			}
		}

		Entity nextNode = entity.getNextNodeClient();

		if(nextNode != null) {
			this.renderConnection(entity, nextNode, tessellator, buffer, x, y, z, partialTicks);
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
			double endZ = this.interpolate(node2.prevPosZ - camPosZ, node2.posZ - camPosZ, partialTicks);
			if(node2.getControllingPassenger() != null) {
				Entity controller = node2.getControllingPassenger();
				
				double yaw = this.interpolate(controller.prevRotationYaw, controller.rotationYaw, partialTicks);
				
				double rotX = -Math.cos(Math.toRadians(-yaw)) * 0.25D;
				double rotZ = Math.sin(Math.toRadians(-yaw)) * 0.25D;
				
				endX = this.interpolate(controller.lastTickPosX - camPosX, controller.posX - camPosX, partialTicks) + rotX;
				endY = this.interpolate(controller.lastTickPosY - camPosY, controller.posY - camPosY, partialTicks) + controller.height / 2;
				endZ = this.interpolate(controller.lastTickPosZ - camPosZ, controller.posZ - camPosZ, partialTicks) + rotZ;
			}

			double diffX = (double)((float)(endX - startX));
			double diffY = (double)((float)(endY - startY));
			double diffZ = (double)((float)(endZ - startZ));

			GlStateManager.enableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			
			ParticleBeam.buildBeam(x + diffX, y + diffY, z + diffZ, new Vec3d(-diffX, -diffY, -diffZ), 0.05F, 0, 2F,
					ActiveRenderInfo.getRotationX(), ActiveRenderInfo.getRotationZ(), ActiveRenderInfo.getRotationYZ(), ActiveRenderInfo.getRotationXY(), ActiveRenderInfo.getRotationXZ(),
					(vx, vy, vz, u, v) -> {
						buffer.pos(vx, vy, vz).tex(u, v).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
					});
			
			tessellator.draw();
			
			/*buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);
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
				double yMult = percentage;//endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;

				buffer.pos(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
				buffer.pos(x + diffX * (double)percentage + 0.1D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.1D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
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
				double yMult = percentage;//endY < startY ? percentage*Math.sqrt(percentage) : percentage * percentage;

				buffer.pos(x + diffX * (double)percentage + 0.0D, y + diffY * (double)(yMult + percentage) * 0.5D + 0.1D, z + diffZ * (double)percentage).color(r, g, b, 1).endVertex();
				buffer.pos(x + diffX * (double)percentage + 0.1D, y + diffY * (double)(yMult + percentage) * 0.5D, z + diffZ * (double)percentage + 0.025D).color(r, g, b, 1).endVertex();
			}
			tessellator.draw();*/

			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGrapplingHookNode entity) {
		return TEXTURE;
	}
}
