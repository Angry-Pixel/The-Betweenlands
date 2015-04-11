package thebetweenlands.entities.particles;

import java.util.ArrayList;

import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import thebetweenlands.event.render.FogHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityAnimatorFX extends EntityPathParticle {
	public static final ResourceLocation TEXTURE = new ResourceLocation("thebetweenlands:textures/particle/them.png");

	private int ticks = 0;
	
	public EntityAnimatorFX(World world, double x, double y, double z,
			double motionX, double motionY, double motionZ,
			ArrayList<Vector3d> targetPoints) {
		super(world, x, y, z, motionX, motionY, motionZ, targetPoints);
		
		this.color = 0xFFFFFFFF;
		this.scale = 0.05f;
		this.startY = this.posY;
		this.particleMaxAge = 10000000;
		this.particleAge = 0;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		this.ticks++;
		if(this.ticks >= 200) {
			this.setDead();
		}
		
		double t = 1.0D / 200.0D * this.ticks;
		
		Vector3d pos = this.getPosition(t);
		
		this.setPosition(pos.x, pos.y, pos.z);
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
	}

	
	
	private double startY;
	private float scale;
	private int color;
	
	@Override
	public void renderParticle(Tessellator par1Tessellator, float partialTicks, float rx, float rxz, float rz, float ryz, float rxy) {
		par1Tessellator.draw();
		par1Tessellator.startDrawingQuads();
		
		//float ipx = (float)((this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks) - this.interpPosX);
		//float ipy = (float)((this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks) - this.interpPosY);
		//float ipz = (float)((this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks) - this.interpPosZ);

		float ipx = (float)(this.posX - this.interpPosX);
		float ipy = (float)(this.posY - this.interpPosY);
		float ipz = (float)(this.posZ - this.interpPosZ);
		
		int prevTex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
		
		par1Tessellator.setBrightness(255);

		//System.out.println(this.posX + " " + this.posY + " " + this.posZ);
		
		float a = 255.0F;
		float r = (float)(color >> 16 & 0xff) / 255F;
		float g = (float)(color >> 8 & 0xff) / 255F;
		float b = (float)(color & 0xff) / 255F;

		GL11.glDisable(GL11.GL_BLEND);
		
		par1Tessellator.setColorRGBA_F(r, g, b, a);
		par1Tessellator.addVertexWithUV(ipx - rx * scale - ryz * scale, ipy - rxz * scale*2, ipz - rz * scale - rxy * scale, 0.0D, 1.0D);
		par1Tessellator.addVertexWithUV(ipx - rx * scale + ryz * scale, ipy + rxz * scale*2, ipz - rz * scale + rxy * scale, 0.0D, 0.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale + ryz * scale, ipy + rxz * scale*2, ipz + rz * scale + rxy * scale, 1.0D, 0.0D);
		par1Tessellator.addVertexWithUV(ipx + rx * scale - ryz * scale, ipy - rxz * scale*2, ipz + rz * scale - rxy * scale, 1.0D, 1.0D);
		
		/*par1Tessellator.addVertex(ipx - rx * scale - ryz * scale, ipy - rxz * scale*2, ipz - rz * scale - rxy * scale);
		par1Tessellator.addVertex(ipx - rx * scale + ryz * scale, ipy + rxz * scale*2, ipz - rz * scale + rxy * scale);
		par1Tessellator.addVertex(ipx + rx * scale + ryz * scale, ipy + rxz * scale*2, ipz + rz * scale + rxy * scale);
		par1Tessellator.addVertex(ipx + rx * scale - ryz * scale, ipy - rxz * scale*2, ipz + rz * scale - rxy * scale);
		
		par1Tessellator.addVertex(ipx - rx * scale - ryz * scale, ipy - rxz * scale*2, ipz - rz * scale - rxy * scale);
		par1Tessellator.addVertex(ipx + rx * scale - ryz * scale, ipy - rxz * scale*2, ipz + rz * scale - rxy * scale);
		par1Tessellator.addVertex(ipx + rx * scale + ryz * scale, ipy + rxz * scale*2, ipz + rz * scale + rxy * scale);
		par1Tessellator.addVertex(ipx - rx * scale + ryz * scale, ipy + rxz * scale*2, ipz - rz * scale + rxy * scale);*/
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		par1Tessellator.draw();
		par1Tessellator.startDrawingQuads();
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTex);
	}
}
