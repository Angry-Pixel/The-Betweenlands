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
import thebetweenlands.common.entity.EntityDecayPitBigFloor;
@SideOnly(Side.CLIENT)
public class RenderDecayPitBigFloor extends Render<EntityDecayPitBigFloor> {

	public static final ResourceLocation OUTER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_outer_ring.png");
	public static final ResourceLocation INNER_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_inner_ring.png");
	public static final ResourceLocation OUTER_MASK_MUD_TILE_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_outer_gear_mask.png");
	public static final ResourceLocation INNER_MASK_MUD_TILE_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_inner_gear_mask.png");
	public static final ResourceLocation MASK_MUD_TILE_TEXTURE_HOLE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_gear_mask_hole.png");
	public static final ResourceLocation VERTICAL_RING_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/decay_pit_vertical_ring.png");

	public RenderDecayPitBigFloor(RenderManager manager) {
		super(manager);
	}

	@Override
	public void doRender(EntityDecayPitBigFloor entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float ring_rotate = entity.animationTicksPrev + (entity.animationTicks - entity.animationTicksPrev) * partialTicks;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(1F, 1F, 1F, 1F);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		bindTexture(OUTER_MASK_MUD_TILE_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int part = 0; part < 24; part++) {
			buildRingQuads(buffer, x, y + 1D + 0.001D, z, 15F * part, 7.5D, 7.5D, 4.25D, 4.25D, false);
		}
		tessellator.draw();

		bindTexture(INNER_MASK_MUD_TILE_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int part = 0; part < 24; part++) {
			buildRingQuads(buffer, x, y + 0.001F, z, 15F * part, 4.25D, 4.25D, 2.75D, 2.75D, false);
		}
		tessellator.draw();

		bindTexture(VERTICAL_RING_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int part = 0; part < 24; part++) {
			buildRingQuads(buffer, x, y + 1D + 0.001D, z, 15F * part, 7.5D, 7.5D, 4.25D, 4.25D, true);
		}
		tessellator.draw();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.5, 0.5, 0);
		GlStateManager.rotate(ring_rotate, 0, 0, 1);
		GlStateManager.translate(-0.5, -0.5, 0);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		bindTexture(OUTER_RING_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int part = 0; part < 24; part++) {
			buildRingQuads(buffer, x, y + 1D + 0.003D, z, 15F * part, 7.5D, 7.5D, 4.24D, 4.24D, false);
		}
		tessellator.draw();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.5, 0.5, 0);
		GlStateManager.rotate(-ring_rotate, 0, 0, 1);
		GlStateManager.translate(-0.5, -0.5, 0);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		bindTexture(INNER_RING_TEXTURE);
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int part = 0; part < 24; part++) {
			buildRingQuads(buffer, x, y + 0.003F, z, 15F * part, 4.25D, 4.25D, 2.75D, 2.75D, false);
		}
		tessellator.draw();

		GlStateManager.matrixMode(GL11.GL_TEXTURE);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);

		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		
		GlStateManager.popMatrix();
	}

	private void buildRingQuads(BufferBuilder buffer, double x, double y, double z, double angle, double offsetXOuter, double offsetZOuter, double offsetXInner, double offsetZInner, boolean innerWall) {
		double startAngle = Math.toRadians(angle);
		double endAngle = Math.toRadians(angle + 15D);
		double offSetXOut1 = -Math.sin(startAngle) * offsetXOuter;
		double offSetZOut1 = Math.cos(startAngle) * offsetZOuter;
		double offSetXIn1 = -Math.sin(startAngle) * offsetXInner;
		double offSetZIn1 = Math.cos(startAngle) * offsetZInner;

		double offSetXOut2 = -Math.sin(endAngle) * offsetXOuter;
		double offSetZOut2 = Math.cos(endAngle) * offsetZOuter;
		double offSetXIn2 = -Math.sin(endAngle) * offsetXInner;
		double offSetZIn2 = Math.cos(endAngle) * offsetZInner;

		if (!innerWall) {
			buffer.pos(x + offSetXOut1, y, z + offSetZOut1).tex(offSetZOut1 / offsetZOuter / 2 + 0.5, 1 - (offSetXOut1 / offsetXOuter / 2 + 0.5)).endVertex();
			buffer.pos(x + offSetXIn1, y, z + offSetZIn1).tex(offSetZIn1 / offsetZOuter / 2 + 0.5, 1 - (offSetXIn1 / offsetXOuter / 2 + 0.5)).endVertex();
			buffer.pos(x + offSetXIn2, y, z + offSetZIn2).tex(offSetZIn2 / offsetZOuter / 2 + 0.5, 1 - (offSetXIn2 / offsetXOuter / 2 + 0.5)).endVertex();
			buffer.pos(x + offSetXOut2, y, z + offSetZOut2).tex(offSetZOut2 / offsetZOuter / 2 + 0.5, 1 - (offSetXOut2 / offsetXOuter / 2 + 0.5)).endVertex();
		} else {
			buffer.pos(x + offSetXIn1, y, z + offSetZIn1).tex(1, 1).endVertex();
			buffer.pos(x + offSetXIn1, y-1F, z + offSetZIn1).tex(1, 0).endVertex();
			buffer.pos(x + offSetXIn2, y-1F, z + offSetZIn2).tex(0, 0).endVertex();
			buffer.pos(x + offSetXIn2, y, z + offSetZIn2).tex(0, 1).endVertex();
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDecayPitBigFloor entity) {
		return null;
	}
}