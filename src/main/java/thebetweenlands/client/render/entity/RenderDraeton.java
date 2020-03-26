package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDraetonBalloon;
import thebetweenlands.client.render.model.entity.ModelDraetonCarriage;
import thebetweenlands.client.render.model.entity.ModelShambler;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderDraeton extends Render<EntityDraeton> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/draeton_carriage.png");
	private static final ResourceLocation TEXTURE_BALLOON = new ResourceLocation(ModInfo.ID, "textures/entity/draeton_balloon.png");
	private static final ResourceLocation TEXTURE_SHAMBLER = new ResourceLocation("thebetweenlands:textures/entity/shambler.png");

	private final ModelDraetonCarriage modelCarriage = new ModelDraetonCarriage();
	private final ModelDraetonBalloon modelBalloon = new ModelDraetonBalloon();
	private final ModelShambler modelShambler = new ModelShambler();

	public RenderDraeton(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityDraeton entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		if(this.renderManager.isDebugBoundingBox()) {
			GlStateManager.depthMask(false);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();

			for(DraetonPhysicsPart part : entity.physicsParts) {
				double pinterpX = part.prevX + (part.x - part.prevX) * partialTicks - this.renderManager.renderPosX;
				double pinterpY = part.prevY + (part.y - part.prevY) * partialTicks - this.renderManager.renderPosY;
				double pinterpZ = part.prevZ + (part.z - part.prevZ) * partialTicks - this.renderManager.renderPosZ;

				AxisAlignedBB aabb = part.getAabb().offset(pinterpX - x - part.x, pinterpY - y - part.y, pinterpZ - z - part.z);

				RenderGlobal.drawBoundingBox(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ, 0, 1, 0, 1);
			}

			Vec3d balloonPos = entity.getBalloonPos(partialTicks);

			double binterpX = balloonPos.x - this.renderManager.renderPosX;
			double binterpY = balloonPos.y - this.renderManager.renderPosY;
			double binterpZ = balloonPos.z - this.renderManager.renderPosZ;

			AxisAlignedBB balloonAabb = new AxisAlignedBB(-0.3f, -0.3f, -0.3f, 0.3f, 0.3f, 0.3f).offset(binterpX - x, binterpY - y, binterpZ - z);
			RenderGlobal.drawBoundingBox(balloonAabb.minX, balloonAabb.minY, balloonAabb.minZ, balloonAabb.maxX, balloonAabb.maxY, balloonAabb.maxZ, 0, 0, 1, 1);

			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}


		for(int i = 0; i < 8; i++) {
			GlStateManager.pushMatrix();

			Vec3d balloonPos = entity.getBalloonRopeConnection(i, partialTicks);

			double binterpX = balloonPos.x - this.renderManager.renderPosX;
			double binterpY = balloonPos.y - this.renderManager.renderPosY;
			double binterpZ = balloonPos.z - this.renderManager.renderPosZ;

			Vec3d connectionPoint = entity.getCarriageRopeConnection(i, partialTicks);

			GlStateManager.translate(connectionPoint.x, connectionPoint.y, connectionPoint.z);

			this.renderConnection(tessellator, buffer, 0, 0, 0, binterpX - x - connectionPoint.x, binterpY - y - connectionPoint.y + 0.05f, binterpZ - z - connectionPoint.z);

			GlStateManager.popMatrix();
		}

		for(DraetonPhysicsPart part : entity.physicsParts) {
			GlStateManager.pushMatrix();

			Vec3d pullPoint = entity.getPullPoint(part, partialTicks);
			GlStateManager.translate(pullPoint.x, pullPoint.y, pullPoint.z);

			if(part.type == DraetonPhysicsPart.Type.PULLER) {
				Entity pullerEntity = part.getEntity();
				if(pullerEntity != null) {
					double dinterpX = pullerEntity.lastTickPosX + (pullerEntity.posX - pullerEntity.lastTickPosX) * partialTicks - this.renderManager.renderPosX;
					double dinterpY = pullerEntity.lastTickPosY + (pullerEntity.posY - pullerEntity.lastTickPosY) * partialTicks - this.renderManager.renderPosY;
					double dinterpZ = pullerEntity.lastTickPosZ + (pullerEntity.posZ - pullerEntity.lastTickPosZ) * partialTicks - this.renderManager.renderPosZ;

					this.renderConnection(tessellator, buffer, 0, 0, 0, dinterpX - x - pullPoint.x, dinterpY - y - pullPoint.y + 0.25f, dinterpZ - z - pullPoint.z);
				}
			} else if(entity.upgradeAnchorPart.isEnabled()) {
				double dinterpX = part.prevX + (part.x - part.prevX) * partialTicks - this.renderManager.renderPosX;
				double dinterpY = part.prevY + (part.y - part.prevY) * partialTicks - this.renderManager.renderPosY;
				double dinterpZ = part.prevZ + (part.z - part.prevZ) * partialTicks - this.renderManager.renderPosZ;

				this.renderConnection(tessellator, buffer, 0, 0, 0, dinterpX - x - pullPoint.x, dinterpY - y - pullPoint.y + 0.25f, dinterpZ - z - pullPoint.z);

				GlStateManager.translate(dinterpX - x - pullPoint.x, dinterpY - y - pullPoint.y, dinterpZ - z - pullPoint.z);
				GlStateManager.rotate(90, 1, 0, 0);
				GlStateManager.translate(0, -1f, -0.135f);
				GlStateManager.disableCull();

				this.bindTexture(TEXTURE_SHAMBLER);
				this.modelShambler.renderTongueEnd(0.0625F);

				GlStateManager.enableCull();
			}

			GlStateManager.popMatrix();
		}

		Vec3d balloonPos = entity.getBalloonPos(partialTicks);

		double binterpX = balloonPos.x - this.renderManager.renderPosX;
		double binterpY = balloonPos.y - this.renderManager.renderPosY;
		double binterpZ = balloonPos.z - this.renderManager.renderPosZ;

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		GlStateManager.pushMatrix();
		GlStateManager.translate(binterpX - x, binterpY - y, binterpZ - z);
		GlStateManager.scale(-1, -1, 1);

		GlStateManager.rotate(entityYaw, 0, 1, 0);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		GlStateManager.rotate(entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks, 0, 0, 1);

		GlStateManager.translate(0, 22.0f / 16.0f, 0);

		this.bindTexture(TEXTURE_BALLOON);
		this.modelBalloon.render(0.0625f);

		GlStateManager.popMatrix();

		GlStateManager.translate(0, 1.5F, 0);
		GlStateManager.scale(-1, -1, 1);

		GlStateManager.rotate(entityYaw, 0, 1, 0);
		GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1, 0, 0);
		GlStateManager.rotate(entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks, 0, 0, 1);

		float timeSinceHit = entity.getTimeSinceHit() - partialTicks;
		float damageTaken = entity.getDamageTaken() - partialTicks;

		if (damageTaken < 0.0F) {
			damageTaken = 0.0F;
		}

		if (timeSinceHit > 0.0F) {
			GlStateManager.rotate(MathHelper.sin(timeSinceHit) * timeSinceHit * damageTaken / 10.0F, 0, 0, 1);
		}


		GlStateManager.rotate(180, 0, 1, 0);

		this.bindEntityTexture(entity);
		this.modelCarriage.renderCarriage(0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableBlend();

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();

		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	protected void renderConnection(Tessellator tessellator, BufferBuilder buffer, double x1, double y1, double z1, double x2, double y2, double z2) {
		double x = 0;
		double y = 0;
		double z = 0;

		double startX = x1;
		double startY = y1;
		double startZ = z1;
		double endX = x2;
		double endY = y2;
		double endZ = z2;

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
				r = 29.0f / 255.0f;
				g = 72.0f / 255.0f;
				b = 29.0f / 255.0f;
			} else {
				r = 21.0f / 255.0f;
				g = 52.0f / 255.0f;
				b = 21.0f / 255.0f;
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
				r = 29.0f / 255.0f;
				g = 72.0f / 255.0f;
				b = 29.0f / 255.0f;
			} else {
				r = 21.0f / 255.0f;
				g = 52.0f / 255.0f;
				b = 21.0f / 255.0f;
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

	@Override
	protected ResourceLocation getEntityTexture(EntityDraeton entity) {
		return TEXTURE;
	}
}