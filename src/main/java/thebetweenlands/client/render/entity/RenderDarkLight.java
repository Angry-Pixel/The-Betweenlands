package thebetweenlands.client.render.entity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.render.model.entity.ModelDarkLightSkull;
import thebetweenlands.common.entity.mobs.EntityDarkLight;

@SideOnly(Side.CLIENT)
public class RenderDarkLight extends RenderLiving<EntityDarkLight> {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/sludge.png");
	protected static final ModelDarkLightSkull MODEL = new ModelDarkLightSkull();

	public RenderDarkLight(RenderManager renderManager) {
		super(renderManager, new ModelDarkLightSkull(), 0.0F);
	}

	@Override
	public void doRender(EntityDarkLight entity, double x, double y, double z, float entityYaw, float partialTicks) {
		float renderRotation = entity.rotation + (entity.rotation - entity.prevRotation) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y + 0.875F, z);
		GlStateManager.rotate(renderRotation * 2.0F, 0, 1, 0);
		double shineScale = 0.08F;
		GlStateManager.scale(shineScale, shineScale, shineScale);
		this.renderShine((float) Math.sin(Math.toRadians(renderRotation)) / 2.0F - 0.2F, 80);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.depthMask(false);
		GlStateManager.translate(x, y + 1.75F, z);
		GlStateManager.scale(1, -1, 1);
		GlStateManager.rotate(180F + entity.renderYawOffset, 0, -1, 0);
		GlStateManager.color(0.1F, 0.4F, 1F, 0.3F);
		bindTexture(TEXTURE);
		MODEL.render();
		MODEL.setLivingAnimations(entity, 0F, 0F, partialTicks);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GL11.glShadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private void renderShine(float rotation, int iterations) {
		Random random = new Random(432L);
		GlStateManager.disableTexture2D();
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GlStateManager.disableAlpha();
		GlStateManager.enableCull();
		GlStateManager.depthMask(false);
		GlStateManager.pushMatrix();
		float f1 = rotation;
		float f2 = 0.0f;
		if (f1 > 0.8F) {
			f2 = (f1 - 0.8F) / 0.2F;
		}
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
			buffer.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, (int) (255.0F * (1.0F - f2))).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(0.0D, (double) pos1, (double) (1.0F * pos2)).color(0, 0, 255, 0).endVertex();
			buffer.pos(-0.866D * (double) pos2, (double) pos1, (double) (-0.5F * pos2)).color(0, 0, 255, 0).endVertex();
			tessellator.draw();
		}
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GL11.glShadeModel(GL11.GL_FLAT);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		RenderHelper.enableStandardItemLighting();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDarkLight entity) {
		return TEXTURE;
	}
}
