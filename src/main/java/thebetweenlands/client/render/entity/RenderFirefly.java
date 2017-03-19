package thebetweenlands.client.render.entity;

import java.util.AbstractMap.SimpleEntry;
import java.util.Random;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.handler.WorldRenderHandler;
import thebetweenlands.client.render.entity.layer.LayerGlow;
import thebetweenlands.client.render.model.entity.ModelFirefly;
import thebetweenlands.client.render.shader.LightSource;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.common.entity.mobs.EntityFirefly;

@SideOnly(Side.CLIENT)
public class RenderFirefly extends RenderLiving<EntityFirefly> {
	public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/firefly_glow.png");
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/entity/firefly.png");
	public static final ResourceLocation TEXTURE_OVERLAY = new ResourceLocation("thebetweenlands:textures/entity/firefly_glow_overlay.png");

	protected static final Random RANDOM = new Random();

	protected final LayerGlow<EntityFirefly> glow;

	public RenderFirefly(RenderManager renderManager) {
		super(renderManager, new ModelFirefly(), 0.0F);
		this.addLayer(this.glow = new LayerGlow<EntityFirefly>(this, TEXTURE_OVERLAY));
	}

	@Override
	public void doRender(EntityFirefly entity, double x, double y, double z, float yaw, float partialTicks) {
		double glowStrength = (float)entity.getEntityAttribute(EntityFirefly.GLOW_STRENGTH_ATTRIB).getAttributeValue();

		this.glow.setAlpha(entity.getGlowTicks(partialTicks) / 20.0F * (float)Math.min(glowStrength, 1.0D));

		GlStateManager.pushMatrix();
		GlStateManager.translate(0, Math.sin((entity.ticksExisted + partialTicks) / 10.0F) * 0.15F, 0);
		super.doRender(entity, x, y, z, yaw, partialTicks);
		GlStateManager.popMatrix();

		WorldRenderHandler.fireflies.add(new SimpleEntry<>(new SimpleEntry<>(this, entity), new Vector3d(x, y, z)));

		if (ShaderHelper.INSTANCE.isWorldShaderActive()) {
			float radius = entity.getGlowTicks(partialTicks) / 20.0F * 7.0F * (float)glowStrength;

			if(radius > 0.1F) {
				double interpX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
				double interpY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - 0.5D;
				double interpZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

				ShaderHelper.INSTANCE.getWorldShader().addLight(new LightSource(interpX, interpY, interpZ,
						entity.worldObj.rand.nextFloat() * 0.1f + radius,
						16.0f / 255.0f * 60.0F + entity.worldObj.rand.nextFloat() * 0.4f,
						12.0f / 255.0f * 60.0F + entity.worldObj.rand.nextFloat() * 0.1f,
						8.0f / 255.0f * 60.0F));
			}
		}
	}

	/**
	 * Renders the firefly glow
	 * @param entity
	 * @param x
	 * @param y
	 * @param z
	 * @param partialTicks
	 */
	public void renderFireflyGlow(EntityFirefly entity, double x, double y, double z, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		GlStateManager.pushMatrix();
		GlStateManager.depthMask(false);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.003921569F);

		this.bindTexture(GLOW_TEXTURE);

		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);

		float rx = ActiveRenderInfo.getRotationX();
		float rxz = ActiveRenderInfo.getRotationXZ();
		float rz = ActiveRenderInfo.getRotationZ();
		float ryz = ActiveRenderInfo.getRotationYZ();
		float rxy = ActiveRenderInfo.getRotationXY();

		double ipx = x;
		double ipy = y + 0.25D + Math.sin((entity.ticksExisted + partialTicks) / 10.0F) * 0.15F;
		double ipz = z;

		double glowStrength = (float)entity.getEntityAttribute(EntityFirefly.GLOW_STRENGTH_ATTRIB).getAttributeValue();

		double scale = entity.getGlowTicks(partialTicks) / 20.0F * glowStrength;

		float red = 0.4F;
		float green = 0.2F;
		float blue = 0.0F;
		float alpha = 0.2F;

		if (RANDOM.nextInt(10) <= 2) {
			red = RANDOM.nextInt(10) / 20.0F + 0.25F;
		}

		double currentScale = scale;

		for (int i = 0; i < (RANDOM.nextInt(10) <= 2 ? RANDOM.nextInt(10) : 10); i++) {
			currentScale -= scale * 0.15D;
			buffer.pos(ipx - rx * currentScale - ryz * currentScale, ipy - rxz * currentScale, ipz - rz * currentScale - rxy * currentScale).tex(0.0, 1.0).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx - rx * currentScale + ryz * currentScale, ipy + rxz * currentScale, ipz - rz * currentScale + rxy * currentScale).tex(1.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * currentScale + ryz * currentScale, ipy + rxz * currentScale, ipz + rz * currentScale + rxy * currentScale).tex(1.0D, 0.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * currentScale - ryz * currentScale, ipy - rxz * currentScale, ipz + rz * currentScale - rxy * currentScale).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		red = 0.6F;
		green = 0.6F;
		blue = 0.6F;

		currentScale = scale / 4.0D;
		for (int i = 0; i < 10; i++) {
			currentScale -= scale * 0.15D / 4.0D;
			buffer.pos(ipx - rx * currentScale - ryz * currentScale, ipy - rxz * currentScale, ipz - rz * currentScale - rxy * currentScale).tex(0.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx - rx * currentScale + ryz * currentScale, ipy + rxz * currentScale, ipz - rz * currentScale + rxy * currentScale).tex(1.0D, 1.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * currentScale + ryz * currentScale, ipy + rxz * currentScale, ipz + rz * currentScale + rxy * currentScale).tex(1.0D, 0.0D).color(red, green, blue, alpha).endVertex();
			buffer.pos(ipx + rx * currentScale - ryz * currentScale, ipy - rxz * currentScale, ipz + rz * currentScale - rxy * currentScale).tex(0.0D, 0.0D).color(red, green, blue, alpha).endVertex();
		}

		tessellator.draw();

		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();

		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFirefly entity) {
		return TEXTURE;
	}
}
