package thebetweenlands.client.render.tile;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.client.render.model.tile.ModelRepeller;
import thebetweenlands.common.block.container.BlockRepeller;
import thebetweenlands.common.tile.TileEntityRepeller;
import thebetweenlands.util.TileEntityHelper;

public class RenderRepeller extends TileEntitySpecialRenderer<TileEntityRepeller> {
	protected static final ModelRepeller MODEL = new ModelRepeller();

	protected static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/tiles/repeller.png");

	@Override
	public void render(TileEntityRepeller tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		EnumFacing facing = TileEntityHelper.getStatePropertySafely(tile, BlockRepeller.class, BlockRepeller.FACING, EnumFacing.NORTH);
		
		double xOff = facing.getFrontOffsetX() * 0.12F;
		double zOff = facing.getFrontOffsetZ() * 0.12F;

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GlStateManager.scale(1F, -1F, -1F);
		GlStateManager.rotate(facing.getHorizontalAngle() + 180.0F, 0, 1, 0);
		GlStateManager.disableCull();

		this.bindTexture(TEXTURE);
		MODEL.render();

		GlStateManager.enableCull();
		GlStateManager.popMatrix();

		if(tile != null && tile.hasShimmerstone()) {
			GlStateManager.pushMatrix();
			GlStateManager.translate((float)(x + 0.5F + xOff), (float)(y + 1.15F), (float)(z + 0.5F - zOff));
			GlStateManager.scale(0.008F, 0.008F, 0.008F);
			float rot = ((float)Math.sin((tile.renderTicks + partialTicks) / 40.0F) + 1.0F) / 10.0F + 1.4F;
			this.renderShine(rot, 20, 
					1.0F, 0.8F, 0.0F, 0.0F,
					1.0F, 0.8F, 0.0F, 1.0F);
			GlStateManager.color(1, 1, 1, 1);
			GlStateManager.popMatrix();
		}

		/*if(tileRepeller.isRunning()) {
			WorldRenderHandler.INSTANCE.repellerShields.add(new SimpleEntry(new Vector3d(x + 0.5F + xOff, y + 1.15F, z + 0.5F - zOff), tileRepeller.getRadius(partialTicks)));
		}*/
	}

	protected void renderShine(float rotation, int iterations, float or, float og, float ob, float oa, float ir, float ig, float ib, float ia) {
		Random random = new Random(432L);
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();
		float f1 = rotation;
		float f2 = 0.0f;
		if( f1 > 0.8F ) {
			f2 = (f1 - 0.8F) / 0.2F;
		}
		/*Tessellator tessellator = Tessellator.instance;
		for( int i = 0; (float) i < iterations; ++i ) {
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			tessellator.startDrawing(6);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			tessellator.setColorRGBA_F(ir, ig, ib, ia * f2);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.setColorRGBA_F(or, og, ob, oa * f2);
			tessellator.addVertex(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.addVertex(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.addVertex(0.0D, (double) pos1, (double) (1.0F * pos2));
			tessellator.addVertex(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2));
			tessellator.draw();
		}*/
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		for (int i = 0; (float) i < iterations; ++i) {
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(random.nextFloat() * 360.0F + f1 * 90.0F, 0.0F, 0.0F, 1.0F);
			float pos1 = random.nextFloat() * 20.0F + 5.0F + f2 * 10.0F;
			float pos2 = random.nextFloat() * 2.0F + 1.0F + f2 * 2.0F;
			buffer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(0.0D, 0.0D, 0.0D).color(255, 255, 255, (int) (255.0F * (1.0F - f2))).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.0D, (double) pos1, (double) (1.0F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			tessellator.draw();
		}
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}
}
