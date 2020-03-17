package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.rowboat.ModelWeedwoodRowboat;
import thebetweenlands.common.entity.EntityWeedwoodDraeton;
import thebetweenlands.common.entity.EntityWeedwoodDraeton.Puller;
import thebetweenlands.common.lib.ModInfo;

@SideOnly(Side.CLIENT)
public class RenderWeedwoodDraeton extends Render<EntityWeedwoodDraeton> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/weedwood_rowboat.png");

	private ModelWeedwoodRowboat model = new ModelWeedwoodRowboat();

	public RenderWeedwoodDraeton(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityWeedwoodDraeton entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.enableRescaleNormal();

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);

		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float frontOffset = 1.4f;
		float frontX = (float) Math.sin(Math.toRadians(-entityYaw)) * frontOffset;
		float frontY = 1.2f;
		float frontZ = (float) Math.cos(Math.toRadians(-entityYaw)) * frontOffset;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(frontX, frontY, frontZ);

		for(Puller puller : entity.pullers) {
			if(puller.dragonfly != null) {
				double dinterpX = puller.dragonfly.lastTickPosX + (puller.dragonfly.posX - puller.dragonfly.lastTickPosX) * partialTicks - this.renderManager.renderPosX;
				double dinterpY = puller.dragonfly.lastTickPosY + (puller.dragonfly.posY - puller.dragonfly.lastTickPosY) * partialTicks - this.renderManager.renderPosY;
				double dinterpZ = puller.dragonfly.lastTickPosZ + (puller.dragonfly.posZ - puller.dragonfly.lastTickPosZ) * partialTicks - this.renderManager.renderPosZ;

				this.renderConnection(tessellator, buffer, 0, 0, 0, dinterpX - x - frontX, dinterpY - y - frontY + 0.25f, dinterpZ - z - frontZ);
			}
		}

		GlStateManager.popMatrix();

		GlStateManager.translate(0, 1.5F, 0);
		GlStateManager.scale(-1, -1, 1);
		
		GlStateManager.rotate(entityYaw, 0, 1, 0);
		GlStateManager.rotate(entity.prevRotationRoll + (entity.rotationRoll - entity.prevRotationRoll) * partialTicks, 0, 0, 1);

		this.bindEntityTexture(entity);

		this.model.renderCarriageBase(0.0625F);

		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.color(1, 1, 1, 1);
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

	@Override
	protected ResourceLocation getEntityTexture(EntityWeedwoodDraeton entity) {
		return TEXTURE;
	}
}