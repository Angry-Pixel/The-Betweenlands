package thebetweenlands.entities.particles;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;

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
	private double dragX, dragY, dragZ;

	private static final float VELOCITY_OFFSET_MULTIPLIER = 4.0F;

	public EntityLeafSwirlFX(World world, double x, double y, double z, int maxAge, float scale, int color, ResourceLocation texture, int textures, Entity target, float progress) {
		super(world, x, y, z, 0, 0, 0);
		this.target = target;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.progress = progress;

		double tmx = this.target.posX - this.target.lastTickPosX;
		double tmy = this.target.posY - this.target.lastTickPosY;
		double tmz = this.target.posZ - this.target.lastTickPosZ;

		double my = this.target.isCollidedVertically ? 0.0D : (tmy * VELOCITY_OFFSET_MULTIPLIER);
		if(my < -0.3D) {
			my = -0.3D;
		}

		this.dragX = MathHelper.clamp_double(tmx * VELOCITY_OFFSET_MULTIPLIER, -1, 1);
		this.dragY = MathHelper.clamp_double(my, -0.3D, 1);
		this.dragZ = MathHelper.clamp_double(tmz * VELOCITY_OFFSET_MULTIPLIER, -1, 1);

		double sx = this.target.posX - this.dragX;
		double sy = this.target.posY - 1.6D - this.dragY;
		double sz = this.target.posZ - this.dragZ;

		Vec3 connection = Vec3.createVectorHelper(this.target.posX - sx, this.target.posY - sy, this.target.posZ - sz);
		this.startRotation = (float) (this.rand.nextFloat() * Math.PI * 2.0F);
		this.endRadius = 0.35F + this.rand.nextFloat() * 0.35F;

		this.posX = sx + connection.xCoord * (1-(1-this.progress)*(1-this.progress)*(1-this.progress)) + Math.sin(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
		this.posY = sy + connection.yCoord * this.progress + (this.target == TheBetweenlands.proxy.getClientPlayer() ? -1.25D : 0.6D);
		this.posZ = sz + connection.zCoord * (1-(1-this.progress)*(1-this.progress)*(1-this.progress)) + Math.cos(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
		this.lastTickPosX = this.prevPosX = this.posX;
		this.lastTickPosY = this.prevPosY = this.posY;
		this.lastTickPosZ = this.prevPosZ = this.posZ;

		this.particleMaxAge = maxAge;
		this.noClip = false;
		this.color = color;
		this.scale = scale;
		this.textures = textures;
		this.relativeTextureHeight = 1.0D / this.textures;
		this.particleTexture = texture;
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

		float dragIncrement = 0.1F;

		double tmx = this.target.posX - this.target.lastTickPosX;
		double tmy = this.target.posY - this.target.lastTickPosY;
		double tmz = this.target.posZ - this.target.lastTickPosZ;

		if(this.dragX > tmx * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragX -= dragIncrement;
		} else if(this.dragX < tmx * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragX += dragIncrement;
		}
		if(Math.abs(this.dragX - tmx * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			this.dragX = tmx * VELOCITY_OFFSET_MULTIPLIER;
		}
		double my = this.target.isCollidedVertically ? 0.0D : (tmy * VELOCITY_OFFSET_MULTIPLIER);
		if(this.dragY > my) {
			this.dragY -= dragIncrement;
		} else if(this.dragY < my) {
			this.dragY += dragIncrement;
		}
		if(Math.abs(this.dragY - my) <= dragIncrement) {
			this.dragY = my;
		}
		if(this.dragZ > tmz * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragZ -= dragIncrement;
		} else if(this.dragZ < tmz * VELOCITY_OFFSET_MULTIPLIER) {
			this.dragZ += dragIncrement;
		}
		if(Math.abs(this.dragZ - tmz * VELOCITY_OFFSET_MULTIPLIER) <= dragIncrement) {
			this.dragZ = tmz * VELOCITY_OFFSET_MULTIPLIER;
		}

		this.dragX = MathHelper.clamp_double(this.dragX, -1, 1);
		this.dragY = MathHelper.clamp_double(this.dragY, -0.3D, 1);
		this.dragZ = MathHelper.clamp_double(this.dragZ, -1, 1);

		double sx = this.target.posX - this.dragX;
		double sy = this.target.posY - 1.6D - this.dragY;
		double sz = this.target.posZ - this.dragZ;

		Vec3 connection = Vec3.createVectorHelper(this.target.posX - sx, this.target.posY - sy, this.target.posZ - sz);

		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;

		this.posX = sx + connection.xCoord * (1-(1-this.progress)*(1-this.progress)*(1-this.progress)) + Math.sin(this.startRotation + this.progress * 4.0F * Math.PI * 2.0F) * this.progress * this.endRadius;
		this.posY = sy + connection.yCoord * this.progress + (this.target == TheBetweenlands.proxy.getClientPlayer() ? -1.25D : 1.0D);
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
