package thebetweenlands.entities.particles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityLeafSwirlFX extends EntityFX {
	private ResourceLocation particleTexture;
	private float scale;
	private int color;
	private int textures;
	private double relativeTextureHeight;
	private int currentTexture = 0;
	private int textureCounter = 0;
	private float progress = 0;
	private final Entity target;
	private final float startRotation;
	private final float endRadius;

	private static final float VELOCITY_OFFSET_MULTIPLIER = 4.0F;

	public EntityLeafSwirlFX(World world, double x, double y, double z, int maxAge, float scale, int color, ResourceLocation texture, int textures, Entity target) {
		super(world, x, y, z, 0, 0, 0);
		this.target = target;
		this.posX = this.prevPosX = x - this.target.motionX * VELOCITY_OFFSET_MULTIPLIER;
		this.posY = this.prevPosY = y + 0.8D - 1.6D - (this.target.isCollidedVertically ? 0.0D : this.target.motionY * VELOCITY_OFFSET_MULTIPLIER);
		this.posZ = this.prevPosZ = z - this.target.motionZ * VELOCITY_OFFSET_MULTIPLIER;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleMaxAge = maxAge;
		this.noClip = false;
		this.color = color;
		this.scale = scale;
		this.textures = textures;
		this.relativeTextureHeight = 1.0D / this.textures;
		this.particleTexture = texture;
		this.startRotation = (float) (this.rand.nextFloat() * Math.PI * 2.0F);
		this.endRadius = 0.35F + this.rand.nextFloat() * 0.35F;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
		float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
		float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);

		int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.particleTexture);

		float a = (float)(this.color >> 24 & 0xff) / 255F;
		float r = (float)(this.color >> 16 & 0xff) / 255F;
		float g = (float)(this.color >> 8 & 0xff) / 255F;
		float b = (float)(this.color & 0xff) / 255F;

		par1Tessellator.startDrawingQuads();
		par1Tessellator.setBrightness(this.getBrightnessForRender(partialTicks));
		par1Tessellator.setColorRGBA_F(r, g, b, a);
		par1Tessellator.addVertexWithUV(ipx - rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz - rz * this.scale - rxy * this.scale, 1.0D, (this.currentTexture + 1) * this.relativeTextureHeight);
		par1Tessellator.addVertexWithUV(ipx - rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz - rz * this.scale + rxy * this.scale, 1.0D, this.currentTexture * this.relativeTextureHeight);
		par1Tessellator.addVertexWithUV(ipx + rx * this.scale + ryz * this.scale, ipy + rxz * this.scale, ipz + rz * this.scale + rxy * this.scale, 0.0D, this.currentTexture * this.relativeTextureHeight);
		par1Tessellator.addVertexWithUV(ipx + rx * this.scale - ryz * this.scale, ipy - rxz * this.scale, ipz + rz * this.scale - rxy * this.scale, 0.0D, (this.currentTexture + 1) * this.relativeTextureHeight);
		par1Tessellator.draw();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
	}

	@Override
	public int getFXLayer() {
		return 3;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if(this.target == null || !this.target.isEntityAlive() || this.progress > 1.0F) {
			this.setDead();
			return;
		}

		this.progress += 0.01F;

		double sx = this.target.posX - this.target.motionX * VELOCITY_OFFSET_MULTIPLIER;
		double sy = this.target.posY - 1.6D - (this.target.isCollidedVertically ? 0.0D : this.target.motionY * VELOCITY_OFFSET_MULTIPLIER);
		double sz = this.target.posZ - this.target.motionZ * VELOCITY_OFFSET_MULTIPLIER;

		Vec3 connection = Vec3.createVectorHelper(this.target.posX - sx, this.target.posY - sy, this.target.posZ - sz);

		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		this.posX = sx + connection.xCoord * (1-(1-this.progress)*(1-this.progress)*(1-this.progress)) + Math.sin(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
		this.posY = sy + connection.yCoord * this.progress + 0.8D;
		this.posZ = sz + connection.zCoord * (1-(1-this.progress)*(1-this.progress)*(1-this.progress)) + Math.cos(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;

		this.textureCounter++;
		if(this.textureCounter >= 5) {
			this.textureCounter = 0;
			this.currentTexture++;
			if(this.currentTexture >= this.textures) {
				this.currentTexture = 0;
			}
		}
	}
}
