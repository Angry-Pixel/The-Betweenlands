package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelRockSnotGrabber;
import thebetweenlands.common.entity.mobs.EntityRockSnotTendril;

@SideOnly(Side.CLIENT)
public class RenderRockSnotTendril extends Render<EntityRockSnotTendril> {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/rock_snot_grabber.png");
	public static final ResourceLocation VERTICAL_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_vertical_ring.png");
	public final ModelRockSnotGrabber GRABBER_MODEL = new ModelRockSnotGrabber();

	public RenderRockSnotTendril(RenderManager rendermanagerIn) {
		super(rendermanagerIn);
	}

	@Override
	public void doRender(EntityRockSnotTendril entity, double x, double y, double z, float yaw, float partialTicks) {
		super.doRender(entity, x, y, z, yaw, partialTicks);
		if(entity.getParentEntity() != null) {
			float smoothedYaw = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
			float smoothedPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
			GlStateManager.pushMatrix();
			GlStateManager.translate((float) x, (float) y + 0.125F, (float) z);
			if (entity.getExtending())
				GlStateManager.rotate(smoothedYaw + 180F, 0F, 1F, 0F);
			else
				GlStateManager.rotate(smoothedYaw, 0F, 1F, 0F);
			GlStateManager.rotate(smoothedPitch + 90F, 1F, 0F, 0F);
			bindTexture(TEXTURE);
			GRABBER_MODEL.render(entity, 0, 0, entity.ticksExisted, 0F, 0F, 0.0625F);
			GlStateManager.popMatrix();
	
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1F, 1F, 1F, 1F);
	
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
	
			bindTexture(VERTICAL_RING_TEXTURE);
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			for (int part = 0; part < 8; part++) {
				buildRingQuads(buffer, x, y, z, x + entity.getParentEntity().posX - entity.posX, y + entity.getParentEntity().posY - entity.posY, z + entity.getParentEntity().posZ - entity.posZ, 45F * part, 0.125D, 0.125D, 0.25D, 0.25D);
			}
			tessellator.draw();
	
			GlStateManager.depthMask(true);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}
	}
	
	public static void buildRingQuads(BufferBuilder buffer, double x, double y, double z, double xp, double yp, double zp, double angle, double offsetXInner, double offsetZInner, double offsetXInnerP, double offsetZInnerP) {
		double startAngle = Math.toRadians(angle);
		double endAngle = Math.toRadians(angle + 45D);

		double startAngleP = Math.toRadians(angle + 180);
		double endAngleP = Math.toRadians(angle + 235D);

		double offSetXIn1 = -Math.sin(startAngle) * offsetXInner;
		double offSetZIn1 = Math.cos(startAngle) * offsetZInner;
		double offSetXIn2 = -Math.sin(endAngle) * offsetXInner;
		double offSetZIn2 = Math.cos(endAngle) * offsetZInner;

		double offSetXIn1P = -Math.sin(startAngleP) * offsetXInnerP;
		double offSetZIn1P = Math.cos(startAngleP) * offsetZInnerP;
		double offSetXIn2P = -Math.sin(endAngleP) * offsetXInnerP;
		double offSetZIn2P = Math.cos(endAngleP) * offsetZInnerP;

		buffer.pos(xp - offSetXIn1P, yp + 0.0625F, zp + offSetZIn1P).tex(1, 1).endVertex();
		buffer.pos(x - offSetXIn1, y + 0.125F, z + offSetZIn1).tex(1, 0).endVertex();
		buffer.pos(x - offSetXIn2, y + 0.125F, z + offSetZIn2).tex(0, 0).endVertex();
		buffer.pos(xp - offSetXIn2P, yp + 0.0625F, zp + offSetZIn2P).tex(0, 1).endVertex();

		buffer.pos(xp + offSetXIn1P, yp + 0.0625F, zp + offSetZIn1P).tex(1, 1).endVertex();
		buffer.pos(x + offSetXIn1, y + 0.125F, z + offSetZIn1).tex(1, 0).endVertex();
		buffer.pos(x + offSetXIn2, y + 0.125F, z + offSetZIn2).tex(0, 0).endVertex();
		buffer.pos(xp + offSetXIn2P, yp + 0.0625F, zp + offSetZIn2P).tex(0, 1).endVertex();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityRockSnotTendril entity) {
		return null;//TEXTURE;
	}
}
