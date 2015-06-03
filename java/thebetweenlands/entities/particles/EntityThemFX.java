package thebetweenlands.entities.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thebetweenlands.event.render.FogHandler;

public class EntityThemFX extends EntityFX {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/them.png");
	public static final double TEXTURE_HEIGHT = 0.1279D;
	public static final int TEXTURE_COUNT = 5;
	
	private double startY;
	private float scale;
	private int color;
	private double texture;

	public EntityThemFX(World world, double x, double y, double z) {
		super(world, x, y, z, 0, 0, 0);
		this.posX = this.prevPosX = x;
		this.posY = this.prevPosY = y;
		this.posZ = this.prevPosZ = z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		this.particleMaxAge = (int)500;
		this.noClip = true;
		this.color = 0xFFFFFFFF;
		this.scale = 0.8f;
		this.startY = this.posY;
		
		this.texture = (double) this.worldObj.rand.nextInt(EntityThemFX.TEXTURE_COUNT) /
				(double) EntityThemFX.TEXTURE_COUNT;
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		par1Tessellator.draw();
		par1Tessellator.startDrawingQuads();
		
		float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
		float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
		float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);

		int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		
		par1Tessellator.setBrightness(255);

		float fogDist = FogHandler.INSTANCE.getCurrentFogEnd() / 4.0f;
		float a = (float)(color >> 24 & 0xff) / 255F - (float) ((fogDist - Minecraft.getMinecraft().renderViewEntity.getDistance(posX, posY, posZ) + fogDist) / 1.0f);
		int particleAgeLeft = this.particleMaxAge - this.particleAge;
		if(particleAgeLeft < 100) {
			int revLeft = 100 - particleAgeLeft;
			a -= revLeft / 100.0f;
		}
		if(this.particleAge < 100) {
			a -= 1.0f - this.particleAge / 100.0f;
		}
		a += 0.2f;
		float r = (float)(color >> 16 & 0xff) / 255F;
		float g = (float)(color >> 8 & 0xff) / 255F;
		float b = (float)(color & 0xff) / 255F;

		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
		
		par1Tessellator.setColorRGBA_F(r, g, b, a);
		
		par1Tessellator.addVertexWithUV(ipx - rx * scale - ryz * scale, ipy - rxz * scale*2, ipz - rz * scale - rxy * scale, 1.0D, this.texture + EntityThemFX.TEXTURE_HEIGHT);
		par1Tessellator.addVertexWithUV(ipx - rx * scale + ryz * scale, ipy + rxz * scale*2, ipz - rz * scale + rxy * scale, 1.0D, this.texture);
		par1Tessellator.addVertexWithUV(ipx + rx * scale + ryz * scale, ipy + rxz * scale*2, ipz + rz * scale + rxy * scale, 0.0D, this.texture);
		par1Tessellator.addVertexWithUV(ipx + rx * scale - ryz * scale, ipy - rxz * scale*2, ipz + rz * scale - rxy * scale, 0.0D, this.texture + EntityThemFX.TEXTURE_HEIGHT);
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		par1Tessellator.draw();
		par1Tessellator.startDrawingQuads();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
	}

	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		this.setPosition(this.posX, this.startY + Math.sin(this.particleAge / 120.0f), this.posZ);
		
		this.moveEntity(this.motionX, this.motionY, this.motionZ);

		this.motionX *= 0.95999997854232788D;
		this.motionZ *= 0.95999997854232788D;

		if (this.particleAge++ >= this.particleMaxAge || this.scale <= 0) {
			setDead();
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}
}
