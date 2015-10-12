package thebetweenlands.entities.particles;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import thebetweenlands.entities.WeedWoodBushUncollidableEntity;

public class EntityWeedWoodRustleFX extends EntityFX implements WeedWoodBushUncollidableEntity {
	private static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/leaf.png");

	private float scale;

	private int color;

	private int textureCount;

	private double relativeTextureHeight;

	private int currentTexture = 0;

	private int textureCounter = 0;

	public EntityWeedWoodRustleFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		Random rand = world.rand;
		float dx = rand.nextFloat() * 2 - 1;
		float dy = rand.nextFloat() * 2 - 0.5F;
		float dz = rand.nextFloat() * 2 - 1;
		float len = MathHelper.sqrt_float(dx * dx + dy * dy + dz * dz);
		dx /= len;
		dy /= len;
		dz /= len;
		float radius = 0.7F;
		posX += dx * radius;
		posY += dy * radius;
		posZ += dz * radius;
		setPosition(posX, posY, posZ);
		float mag = 0.01F + rand.nextFloat() * 0.07F;
		motionX = dx * mag;
		motionY = dy * mag;
		motionZ = dz * mag;
		particleMaxAge = 50 + rand.nextInt(60);
		noClip = false;
		color = 0xFFFFFFFF;
		scale = 0.12F * rand.nextFloat() + 0.03F;
		textureCount = 5;
		textureCounter = rand.nextInt(5);
		relativeTextureHeight = 1.0 / textureCount;
		particleGravity = 0.09F;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (!onGround) {
			textureCounter++;
			if (textureCounter >= 5) {
				textureCounter = 0;
				currentTexture++;
				if (currentTexture >= textureCount) {
					currentTexture = 0;
				}
			}
		}
	}

	@Override
	public void renderParticle(Tessellator tessellator, float delta, float rx, float rxz, float rz, float ryz, float rxy) {
		float ipx = (float) (prevPosX + (posX - prevPosX) * delta - interpPosX);
		float ipy = (float) (prevPosY + (posY - prevPosY) * delta - interpPosY);
		float ipz = (float) (prevPosZ + (posZ - prevPosZ) * delta - interpPosZ);

		int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

		float a = (color >> 24 & 0xff) / 255F;
		float r = (color >> 16 & 0xff) / 255F;
		float g = (color >> 8 & 0xff) / 255F;
		float b = (color & 0xff) / 255F;

		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F(r, g, b, a);
		tessellator.addVertexWithUV(ipx - rx * scale - ryz * scale, ipy - rxz * scale, ipz - rz * scale - rxy * scale, 1.0D, (currentTexture + 1) * relativeTextureHeight);
		tessellator.addVertexWithUV(ipx - rx * scale + ryz * scale, ipy + rxz * scale, ipz - rz * scale + rxy * scale, 1.0D, currentTexture * relativeTextureHeight);
		tessellator.addVertexWithUV(ipx + rx * scale + ryz * scale, ipy + rxz * scale, ipz + rz * scale + rxy * scale, 0.0D, currentTexture * relativeTextureHeight);
		tessellator.addVertexWithUV(ipx + rx * scale - ryz * scale, ipy - rxz * scale, ipz + rz * scale - rxy * scale, 0.0D, (currentTexture + 1) * relativeTextureHeight);
		tessellator.draw();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
	}

	@Override
	public int getFXLayer() {
		return 3;
	}
}
