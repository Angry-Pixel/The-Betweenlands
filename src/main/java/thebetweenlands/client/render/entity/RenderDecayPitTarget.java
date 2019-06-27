package thebetweenlands.client.render.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDecayPitChain;
import thebetweenlands.client.render.model.entity.ModelDecayPitPlug;
import thebetweenlands.client.render.model.entity.ModelShieldCog;
import thebetweenlands.common.entity.EntityDecayPitTarget;
import thebetweenlands.common.entity.EntityDecayPitTargetPart;
import thebetweenlands.common.lib.ModInfo;
@SideOnly(Side.CLIENT)
public class RenderDecayPitTarget extends Render<EntityDecayPitTarget> {
	public static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_plug.png");
	private final ModelDecayPitPlug PLUG_MODEL = new ModelDecayPitPlug();
	public static final ResourceLocation CHAIN_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/entity/decay_pit_chain.png");
	private final ModelDecayPitChain CHAIN_MODEL = new ModelDecayPitChain();
	public static final ResourceLocation COG_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/barrishee.png");
	private final ModelShieldCog COG_MODEL = new ModelShieldCog();

	public static final ResourceLocation OUTER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/outer_ring.png");
	public static final ResourceLocation INNER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/inner_ring.png");
	public static final ResourceLocation MASK_HACK_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/gear_mask.png");

	public RenderDecayPitTarget(RenderManager manager) {
		super(manager);
	}

	@Override
    public void doRender(EntityDecayPitTarget entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float scroll = entity.animationTicksChainPrev * 0.0078125F + (entity.animationTicksChain * 0.0078125F - entity.animationTicksChainPrev * 0.0078125F) * partialTicks;
		double offsetY = entity.height - entity.getProgress();
		double smoothedMainX = entity.prevPosX + (entity.posX - entity.prevPosX ) * partialTicks;
		double smoothedMainY = entity.prevPosY + (entity.posY - entity.prevPosY ) * partialTicks;
		double smoothedMainZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ ) * partialTicks;

		bindTexture(TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + offsetY -3.5D, z);
		GlStateManager.scale(-1F, -1F, 1F);
		PLUG_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();	

		bindTexture(CHAIN_TEXTURE);
		for (int len = 0; len <= entity.getLength(); len++) {
			renderChainpart(entity, x + 1D, y + offsetY + len + 1.5D, z, 0F, 0F);
			renderChainpart(entity, x - 1D, y + offsetY + len + 1.5D, z, 0F, 180F);
			renderChainpart(entity, x, y + offsetY + len + 1.5D, z + 1D, 0F, 90F);
			renderChainpart(entity, x, y + offsetY + len + 1.5D, z - 1D, 0F, 270F);
		}

		bindTexture(COG_TEXTURE);
		for (EntityDecayPitTargetPart part : entity.shield_array) {
			float floatate = part.prevRotationYaw + (part.rotationYaw - part.prevRotationYaw) * partialTicks;
			double smoothedX = part.prevPosX  + (part.posX - part.prevPosX ) * partialTicks;
			double smoothedY = part.prevPosY  + (part.posY - part.prevPosY ) * partialTicks;
			double smoothedZ = part.prevPosZ  + (part.posZ - part.prevPosZ ) * partialTicks;
			if (part != entity.target && part != entity.bottom && part != entity.chain_1 && part != entity.chain_2 && part != entity.chain_3 && part != entity.chain_4 && part != entity.inner_ring && part != entity.outer_ring)
				renderCogShield(part, x + smoothedX - smoothedMainX, y + smoothedY - smoothedMainY, z + smoothedZ - smoothedMainZ, floatate);
		}

		float ring_rotate = entity.animationTicksPrev + (entity.animationTicks - entity.animationTicksPrev) * partialTicks;

		for (int part = 0; part < 24; part++) {
			rendertopVertex(entity, x, y + 1 + 0.001F, z, 15F * part, 6.5D, 6.5D, 4.25D, 4.25D, true, ring_rotate);
			rendertopVertex(entity, x, y + 0.001F, z, 15F *part, 4.25D, 4.25D, 2.25D, 2.25D, false, -ring_rotate);
		}

		// debug boxes for parts without models
		GlStateManager.pushMatrix();
		for (EntityDecayPitTargetPart part : entity.shield_array)
			if (part == entity.target)
				renderDebugBoundingBox(part, x, y, z, entityYaw, partialTicks, part.posX - entity.posX, part.posY - entity.posY, part.posZ - entity.posZ);
		GlStateManager.popMatrix();
	}
	
	private void rendertopVertex(EntityDecayPitTarget entity, double x, double y, double z, double angle, double offsetXOuter, double offsetZOuter, double offsetXInner, double offsetZInner, boolean renderVertical, float rotate) {

		double startAngle = Math.toRadians(angle);
		double endAngle = Math.toRadians(angle + 15D);
		double offSetXOut1 = -Math.sin(startAngle) * offsetXOuter;
		double offSetZOut1 = Math.cos(startAngle) * offsetZOuter;
		double offSetXIn1 = -Math.sin(startAngle) * offsetXInner;
		double offSetZIn1 = Math.cos(startAngle) * offsetZInner;

		double offSetXOut2 = -Math.sin(endAngle) * offsetXOuter;
		double offSetZOut2 = Math.cos(endAngle) * offsetXOuter;
		double offSetXIn2 = -Math.sin(endAngle) * offsetXInner;
		double offSetZIn2 = Math.cos(endAngle) * offsetXInner;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		// GlStateManager.disableLighting();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);

		GL11.glEnable(GL11.GL_STENCIL_TEST);
		if (angle >= 360)
			GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glStencilMask(0xFF);
		GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);

		bindTexture(MASK_HACK_TEXTURE);
		GlStateManager.color(0F, 0F, 0F, 1.0F);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		if (!renderVertical) {
			buffer.pos(offSetXOut1, 0F, offSetZOut1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(0, 1).endVertex();
			buffer.pos(offSetXOut2, 0F, offSetZOut2).tex(1, 1).endVertex();
		}
		if (renderVertical) {
			buffer.pos(offSetXOut1, 0F, offSetZOut1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(0, 1).endVertex();
			buffer.pos(offSetXOut2, 0F, offSetZOut2).tex(1, 1).endVertex();

			buffer.pos(offSetXIn1, 0F, offSetZIn1).tex(1, 0).endVertex();
			buffer.pos(offSetXIn1, -1F, offSetZIn1).tex(0, 0).endVertex();
			buffer.pos(offSetXIn2, -1F, offSetZIn2).tex(0, 1).endVertex();
			buffer.pos(offSetXIn2, 0F, offSetZIn2).tex(1, 1).endVertex();
		}
		tessellator.draw();

		if (angle >= 360)
			GL11.glStencilMask(0x00);
		GL11.glStencilFunc(GL11.GL_NOTEQUAL, 0, 0xFF);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		if (renderVertical) {
			GlStateManager.rotate(rotate, 0F, 1F, 0F);
			bindTexture(OUTER_RING_TEXTURE);
			buffer.pos(6.5D, 0.01F, 6.5D).tex(1, 0).endVertex();
			buffer.pos(6.5D, 0.01F, -6.5D).tex(0, 0).endVertex();
			buffer.pos(-6.5D, 0.01F, -6.5D).tex(0, 1).endVertex();
			buffer.pos(-6.5D, 0.01F, 6.5D).tex(1, 1).endVertex();
		}
		if (!renderVertical) {
			GlStateManager.rotate(rotate, 0F, 1F, 0F);
			bindTexture(INNER_RING_TEXTURE);
			buffer.pos(4.25D, 0.01F, 4.25D).tex(1, 0).endVertex();
			buffer.pos(4.25D, 0.01F, -4.25D).tex(0, 0).endVertex();
			buffer.pos(-4.25D, 0.01F, -4.25D).tex(0, 1).endVertex();
			buffer.pos(-4.25D, 0.01F, 4.25D).tex(1, 1).endVertex();
		}
		tessellator.draw();

		GL11.glDisable(GL11.GL_STENCIL_TEST);

		GlStateManager.popMatrix();
		// GlStateManager.enableLighting();
	}

	private void renderCogShield(EntityDecayPitTargetPart entity, double x, double y, double z, float angle) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 1.5F, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		COG_MODEL.render(0.0625F);
		GlStateManager.popMatrix();
	}

	private void renderChainpart(EntityDecayPitTarget entity, double x, double y, double z, float scroll, float angle) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + scroll, z);
		GlStateManager.scale(-1F, -1F, 1F);
		GlStateManager.rotate(angle, 0F, 1F, 0F);
		CHAIN_MODEL.render(entity, 0.0625F);
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitTarget entity) {
		return null;
	}

	private void renderDebugBoundingBox(Entity entity, double x, double y, double z, float yaw, float partialTicks, double xOff, double yOff, double zOff) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
		AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - entity.posX + x + xOff, axisalignedbb.minY - entity.posY + y + yOff, axisalignedbb.minZ - entity.posZ + z + zOff, axisalignedbb.maxX - entity.posX + x + xOff, axisalignedbb.maxY - entity.posY + y + yOff, axisalignedbb.maxZ - entity.posZ + z + zOff);
		RenderGlobal.drawSelectionBoundingBox(axisalignedbb1, 1F, 1F, 1F, 1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}

}