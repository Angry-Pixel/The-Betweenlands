package thebetweenlands.entities.particles;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import thebetweenlands.event.render.FogHandler;

public class EntityLeafFX extends EntityFX {
	private ResourceLocation particleTexture;
	private float scale;
	private int color;
	private int textures;
	private double relativeTextureHeight;
	private int currentTexture = 0;
	private int textureCounter = 0;
	private double tx, ty, tz;

	public EntityLeafFX(World world, double x, double y, double z, int maxAge, float scale, int color, ResourceLocation texture, int textures) {
		super(world, x, y, z, 0, 0, 0);
		this.posX = this.prevPosX = this.tx = x;
		this.posY = this.prevPosY = this.ty = y;
		this.posZ = this.prevPosZ = this.tz = z;
		this.motionX = this.motionY = this.motionZ = 0.0D;
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

		par1Tessellator.setBrightness(0);

		float a = (float)(this.color >> 24 & 0xff) / 255F;
		float r = (float)(this.color >> 16 & 0xff) / 255F;
		float g = (float)(this.color >> 8 & 0xff) / 255F;
		float b = (float)(this.color & 0xff) / 255F;

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		par1Tessellator.startDrawingQuads();
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
		this.moveEntity(0, -0.04F, 0);
		if(!this.onGround) {
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
}
