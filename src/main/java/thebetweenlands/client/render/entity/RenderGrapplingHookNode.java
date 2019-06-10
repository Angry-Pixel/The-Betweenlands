package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import thebetweenlands.client.render.model.entity.ModelRopeNode;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.client.render.particle.entity.ParticleBeam;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.util.LightingUtil;

public class RenderGrapplingHookNode extends Render<EntityGrapplingHookNode> {
	private Frustum frustum;

	protected static final ResourceLocation TEXTURE_ROPE = new ResourceLocation("thebetweenlands:textures/entity/grappling_hook_rope.png");
	protected static final ResourceLocation TEXTURE_SHAMBLER = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");

	protected static final ModelRopeNode nodeModel = new ModelRopeNode();
	protected static final ModelShambler shamblerModel = new ModelShambler();

	protected final RenderItem renderItem;
	protected final ItemStack anglerTooth;

	public RenderGrapplingHookNode(RenderManager renderManager) {
		super(renderManager);
		this.frustum = new Frustum();
		this.renderItem = Minecraft.getMinecraft().getRenderItem();
		this.anglerTooth = EnumItemMisc.ANGLER_TOOTH.create(1);
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

		GlStateManager.color(1, 1, 1, 1);

		LightingUtil.INSTANCE.revert();
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();

		double camPosX = this.interpolate(entity.lastTickPosX, entity.posX, partialTicks) - x;
		double camPosY = this.interpolate(entity.lastTickPosY, entity.posY, partialTicks) - y;
		double camPosZ = this.interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - z;

		this.frustum.setPosition(camPosX, camPosY, camPosZ);

		Entity prevNode = entity.getPreviousNodeClient();

		if(prevNode instanceof EntityGrapplingHookNode) {
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

		if(nextNode instanceof EntityGrapplingHookNode) {
			this.renderConnection(entity, nextNode, tessellator, buffer, x, y, z, partialTicks);

			double dx = this.interpolate(entity.lastTickPosX, entity.posX, partialTicks) - this.interpolate(nextNode.lastTickPosX, nextNode.posX, partialTicks);
			double dy = this.interpolate(entity.lastTickPosY, entity.posY, partialTicks) - this.interpolate(nextNode.lastTickPosY, nextNode.posY, partialTicks);
			double dz = this.interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - this.interpolate(nextNode.lastTickPosZ, nextNode.posZ, partialTicks);

			GlStateManager.pushMatrix();

			GlStateManager.translate(x, y, z);

			GlStateManager.rotate(-(float)Math.toDegrees(Math.atan2(dz, dx)), 0, 1, 0);
			GlStateManager.rotate((float)Math.toDegrees(Math.atan2(Math.sqrt(dx * dx + dz * dz), -dy)) + 180, 0, 0, 1);

			if(prevNode == null) {
				//Last node, render shambler tongue
				this.bindTexture(TEXTURE_SHAMBLER);

				GlStateManager.pushMatrix();

				GlStateManager.scale(1.1D, 1.1D, 1.1D);

				GlStateManager.rotate(-90, 1, 0, 0);

				GlStateManager.translate(0, -1, 0.1D);

				shamblerModel.renderTongueEnd(0.0625F);

				GlStateManager.popMatrix();
			} else {
				//Render bone hooks
				this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

				GlStateManager.disableLighting();

				GlStateManager.pushMatrix();
				GlStateManager.scale(0.25D, 0.25D, 0.25D);

				GlStateManager.pushMatrix();
				GlStateManager.translate(0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				this.renderItem.renderItem(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.rotate(180, 0, 1, 0);
				this.renderItem.renderItem(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.rotate(90, 0, 1, 0);

				GlStateManager.pushMatrix();
				GlStateManager.translate(0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				this.renderItem.renderItem(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.pushMatrix();
				GlStateManager.translate(-0.4, 0, 0);
				GlStateManager.rotate(180, 0, 0, 1);
				GlStateManager.rotate(180, 0, 1, 0);
				this.renderItem.renderItem(this.anglerTooth, TransformType.FIXED);
				GlStateManager.popMatrix();

				GlStateManager.popMatrix();

				GlStateManager.enableLighting();
				
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();

			this.bindEntityTexture(entity);
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

			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGrapplingHookNode entity) {
		return TEXTURE_ROPE;
	}
}
