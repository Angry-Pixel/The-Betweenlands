package thebetweenlands.client.render.sky;

import java.util.List;
import java.util.Random;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.util.TextureAtlasHelper;

public class AuroraRenderer {
	private static final ResourceLocation AURORA_TEXTURE = new ResourceLocation("thebetweenlands:textures/sky/aurora.png");
	private static final TextureAtlasHelper ATLAS = new TextureAtlasHelper(320, 128, 64, 128, 0, 0);

	private double x, y, z;
	private Vector2d direction;
	private Vector2d currDirection;
	private int tiles = 14;

	private final List<Vector4f> colorGradients;
	
	private int fadeTicks;
	private int lastFadeTicks;
	private boolean active = true;
	private boolean removed;
	
	public AuroraRenderer(double x, double y, double z, Vector2d direction, int tiles, List<Vector4f> colorGradients) {
		this.x = x;
		this.y = y;
		this.z = z;
		direction.normalize();
		this.direction = direction;
		this.tiles = tiles;
		this.colorGradients = colorGradients;
	}

	private Vector2d getRotatedVec(double offset, Vector2d direction) {
		Vector3d upVec = new Vector3d(0, 1, 0);
		upVec.cross(upVec, new Vector3d(direction.x, 0, direction.y));
		Vector2d res = new Vector2d(upVec.x, upVec.z);
		res.scale(offset);
		return res;
	}

	private float interpolatedNoise(float noisePos) {
		int posRounded = (int) noisePos;
		float posFraction = noisePos - posRounded;
		float noiseGrad1 = getNoiseGradient(posRounded);
		float noiseGrad2 = getNoiseGradient(posRounded + 1);
		return cosineInterpolate(noiseGrad1, noiseGrad2, posFraction);
	}

	private float getNoiseGradient(int noisePosRounded) {
		return getNoise(noisePosRounded) / 2.0F  +  getNoise(noisePosRounded - 1) / 4.0F  +  getNoise(noisePosRounded + 1) / 4.0F;
	}

	private float getNoise(int noisePosRounded) {
		noisePosRounded = (noisePosRounded << 13) ^ noisePosRounded;
		return (float)(1.0 - ((noisePosRounded * (noisePosRounded * noisePosRounded * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824.0);   
	}

	private float cosineInterpolate(float noiseGrad1, float noiseGrad2, float posFraction) {
		float cosVal = (float) ((1 - Math.cos((float) (posFraction * Math.PI))) * 0.5);
		return noiseGrad1 * (1 - cosVal) + noiseGrad2 * cosVal;
	}

	public double getDistance(double x, double y, double z) {
		return Math.sqrt((this.x-x)*(this.x-x)+(this.y-y)*(this.y-y)+(this.z-z)*(this.z-z));
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}

	public void update() {
		this.lastFadeTicks = this.fadeTicks;
		if(this.active && this.fadeTicks < 500) {
			this.fadeTicks++;
		} else if(!this.active && this.fadeTicks > 0) {
			this.fadeTicks--;
		}
		
		if(!this.active && this.fadeTicks <= 0) {
			this.remove();
		}
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void remove() {
		this.removed = true;
	}
	
	public boolean isRemoved() {
		return this.removed;
	}
	
	public float getAlpha(float partialTicks) {
		return (this.lastFadeTicks + (this.fadeTicks - this.lastFadeTicks) * partialTicks) / 500.0F;
	}
	
	public void render(float partialTicks, float alphaMultiplier) {
		//TODO: Only generate vertices once per tick and then interpolate

		alphaMultiplier *= this.getAlpha(partialTicks);
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();

		int segments = this.tiles;
		int subSegments = 5;
		double segmentWidth = 15.0D;
		double segmentHeight = 25.0D;
		int cGradients = this.colorGradients.size();

		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		GlStateManager.disableCull();
		GlStateManager.enableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.disableLighting();

		GlStateManager.pushMatrix();
		GlStateManager.translate(this.x, 0, this.z);

		Random rand = new Random();
		rand.setSeed((long)(((int)(this.x + this.y + this.z))^((int)(this.x * this.y * this.z))));

		int randNoiseOffset = rand.nextInt(100);

		this.currDirection = new Vector2d(this.direction.x, this.direction.y);
		Vector2d prevDirection = this.currDirection;

		Minecraft.getMinecraft().renderEngine.bindTexture(AURORA_TEXTURE);

		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

		for(int i = 0; i < segments; i++) {
			int textureSegment = rand.nextInt(5) + 1;

			for(int si = 0; si < subSegments; si++) {
				prevDirection = new Vector2d(this.currDirection.x, this.currDirection.y);

				float dirXNoise = this.interpolatedNoise(randNoiseOffset * 10 + 0.01F * ((i + (si) / (float)subSegments + (float)(System.nanoTime() / 7000000000.0D)))) * 0.1F - 0.05F;
				float dirYNoise = this.interpolatedNoise(randNoiseOffset * 20 + 0.01F * ((i + (si) / (float)subSegments + (float)(System.nanoTime() / 7000000000.0D)) * 5)) * 0.1F - 0.05F;

				this.currDirection = new Vector2d(this.currDirection.x + dirXNoise, this.currDirection.y + dirYNoise);
				this.currDirection.normalize();

				float offset1 = this.interpolatedNoise(randNoiseOffset + (i + (si) / (float)subSegments) * 2 + (float)(System.nanoTime() / 4000000000.0D)) * 10;
				float offset2 = this.interpolatedNoise(randNoiseOffset + (i + (si+1) / (float)subSegments) * 2 + (float)(System.nanoTime() / 4000000000.0D)) * 10;

				double segStartX = this.x + prevDirection.x * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1, prevDirection).x;
				double segStartZ = this.z + prevDirection.y * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1, prevDirection).y;

				double segStopX = this.x + this.currDirection.x * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2, this.currDirection).x;
				double segStopZ = this.z + this.currDirection.y * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2, this.currDirection).y;

				double relUMin = ((si+1) / (float)subSegments);
				double relUMax = ((si) / (float)subSegments);

				float salphaGradMultiplier = 1.0F;
				float salphaGradMultiplierNext = 1.0F;
				if(i == 0) {
					salphaGradMultiplier = 1.0F / subSegments * si;
					salphaGradMultiplierNext = 1.0F / subSegments * (si + 1);
				} else if(i == segments - 1) {
					salphaGradMultiplier = 1.0F / subSegments * (subSegments - si);
					salphaGradMultiplierNext = 1.0F / subSegments * (subSegments - (si + 1));
				}

				for(int gi = 0; gi < cGradients - 1; gi++) {
					double segStartY = this.y + segmentHeight / (float)(cGradients-1) * gi;
					double segStopY = this.y + segmentHeight / (float)(cGradients-1) * (gi + 1);

					double vmax = ((gi+1) / (float)(cGradients-1));
					double vmin = ((gi) / (float)(cGradients-1));

					float[][] interpolatedUVs = ATLAS.getInterpolatedUVs(textureSegment, (float)relUMin, (float)vmin, (float)relUMax, (float)vmax);

					vmin = 1 - interpolatedUVs[0][1];
					vmax = 1 - interpolatedUVs[1][1];

					float umin = interpolatedUVs[0][0];
					float umax = interpolatedUVs[1][0];

					Vector4f bottomGradient = this.colorGradients.get(gi);
					Vector4f topGradient = this.colorGradients.get(gi+1);

					Entity renderView = Minecraft.getMinecraft().getRenderViewEntity();
					
					double camDist = renderView != null ? renderView.getDistance(segStopX, renderView.posY, segStopZ) : 0.0D;
					double camDistNext = renderView != null ? renderView.getDistance(segStartX, renderView.posY, segStartZ) : 0.0D;
					float alphaGradMultiplier = (float) (salphaGradMultiplier);
					float alphaGradMultiplierNext = (float) (salphaGradMultiplierNext);
					float viewDist = Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 16.0F - 10.0F;

					if(camDistNext > viewDist) {
						alphaGradMultiplier *= 10.0F / (camDistNext - (viewDist - 10.0F));
					}
					if(camDist > viewDist) {
						alphaGradMultiplierNext *= 10.0F / (camDist - (viewDist - 10.0F));
					}

					//Front face
					vertexBuffer.pos(segStartX - this.x, segStopY, segStartZ - this.z).tex(umax, vmax).lightmap(238, 238).color(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStopX - this.x, segStopY, segStopZ - this.z).tex(umin, vmax).lightmap(238, 238).color(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStopX - this.x, segStartY, segStopZ - this.z).tex(umin, vmin).lightmap(238, 238).color(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStartX - this.x, segStartY, segStartZ - this.z).tex(umax, vmin).lightmap(238, 238).color(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier).endVertex();

					//Back face
					vertexBuffer.pos(segStartX - this.x, segStopY, segStartZ - this.z).tex(umax, vmax).lightmap(238, 238).color(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStartX - this.x, segStartY, segStartZ - this.z).tex(umax, vmin).lightmap(238, 238).color(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStopX - this.x, segStartY, segStopZ - this.z).tex(umin, vmin).lightmap(238, 238).color(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier).endVertex();
					vertexBuffer.pos(segStopX - this.x, segStopY, segStopZ - this.z).tex(umin, vmax).lightmap(238, 238).color(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier).endVertex();
				}
			}
		}

		tessellator.draw();

		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableBlend();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.disableLighting();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		GlStateManager.enableCull();

		GlStateManager.popMatrix();
	}
}
