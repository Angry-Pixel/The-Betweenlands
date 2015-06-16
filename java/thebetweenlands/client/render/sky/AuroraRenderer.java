package thebetweenlands.client.render.sky;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import scala.util.Random;

public class AuroraRenderer {
	private double x, y, z;
	private Vector2d direction;
	private Vector2d currDirection;
	private int tiles = 20;

	public AuroraRenderer(double x, double y, double z, Vector2d direction, int tiles) {
		this.x = x;
		this.y = y;
		this.z = z;
		direction.normalize();
		this.direction = direction;
		this.tiles = tiles;
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
	private float perlinNoise1D(float noisePos, float persistence, int octaves) {
        float octaveNoise = 0;
        for (int i = 0; i < octaves; i++) {
            float frequency = (float) Math.pow(2, i);
            double amplitude = Math.pow(persistence, i);
            octaveNoise += interpolatedNoise(noisePos * frequency) * amplitude;
        }
        return (int) octaveNoise;
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
    
	public void render(float alphaMultiplier, List<Vector4f> colorGradients) {
		Tessellator tessellator = Tessellator.instance;

		int segments = this.tiles;
		int subSegments = 10;
		double segmentWidth = 8.0D;
		double segmentHeight = 40.0D;
		int cGradients = colorGradients.size();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_LIGHTING);

		Random rand = new Random();
		rand.setSeed((long)(((int)(this.x + this.y + this.z))^((int)(this.x * this.y * this.z))));
		
		int randNoiseOffset = rand.nextInt(100);
		
		this.currDirection = new Vector2d(this.direction.x, this.direction.y);
		Vector2d prevDirection = this.currDirection;
		
		for(int i = 0; i < segments; i++) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thebetweenlands:textures/sky/aurora" + (rand.nextInt(5) + 1) + ".png"));
			
			tessellator.startDrawingQuads();
			
			tessellator.setBrightness(240);
			
			for(int si = 0; si < subSegments; si++) {
				prevDirection = new Vector2d(this.currDirection.x, this.currDirection.y);
				
				float dirXNoise = this.interpolatedNoise(randNoiseOffset + 0.01F * ((i + (si) / (float)subSegments))) * 0.1F - 0.05F;
				float dirYNoise = this.interpolatedNoise(randNoiseOffset + 0.01F * ((i + (si) / (float)subSegments) * 5)) * 0.1F - 0.05F;
				
				this.currDirection = new Vector2d(this.currDirection.x + dirXNoise, this.currDirection.y + dirYNoise);
				this.currDirection.normalize();
				
				float offset1 = this.interpolatedNoise(randNoiseOffset + (i + (si) / (float)subSegments) * 2 + (float)(System.nanoTime() / 2000000000.0D)) * 10;
				float offset2 = this.interpolatedNoise(randNoiseOffset + (i + (si+1) / (float)subSegments) * 2 + (float)(System.nanoTime() / 2000000000.0D)) * 10;
				
				double segStartX = this.x + prevDirection.x * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1, prevDirection).x;
				double segStartZ = this.z + prevDirection.y * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1, prevDirection).y;

				double segStopX = this.x + this.currDirection.x * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2, this.currDirection).x;
				double segStopZ = this.z + this.currDirection.y * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2, this.currDirection).y;

				double umin = ((si+1) / (float)subSegments);
				double umax = ((si) / (float)subSegments);

				float alphaGradMultiplier = 1.0F;
				float alphaGradMultiplierNext = 1.0F;
				if(i == 0) {
					alphaGradMultiplier = 1.0F / subSegments * si;
					alphaGradMultiplierNext = 1.0F / subSegments * (si + 1);
				} else if(i == segments - 1) {
					alphaGradMultiplier = 1.0F / subSegments * (subSegments - si);
					alphaGradMultiplierNext = 1.0F / subSegments * (subSegments - (si + 1));
				}
				
				for(int gi = 0; gi < cGradients - 1; gi++) {
					double segStartY = this.y + segmentHeight / (float)(cGradients-1) * gi;
					double segStopY = this.y + segmentHeight / (float)(cGradients-1) * (gi + 1);

					double vmax = ((gi+1) / (float)(cGradients-1));
					double vmin = ((gi) / (float)(cGradients-1));
					
					Vector4f bottomGradient = colorGradients.get(gi);
					Vector4f topGradient = colorGradients.get(gi+1);
					
					//Front face
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier);
					tessellator.addVertexWithUV(segStartX, segStopY, segStartZ, umax, vmax);
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier);
					tessellator.addVertexWithUV(segStopX, segStopY, segStopZ, umin, vmax);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier);
					tessellator.addVertexWithUV(segStopX, segStartY, segStopZ, umin, vmin);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier);
					tessellator.addVertexWithUV(segStartX, segStartY, segStartZ, umax, vmin);
					
					//Back face
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier);
					tessellator.addVertexWithUV(segStartX, segStopY, segStartZ, umax, vmax);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier);
					tessellator.addVertexWithUV(segStartX, segStartY, segStartZ, umax, vmin);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier);
					tessellator.addVertexWithUV(segStopX, segStartY, segStopZ, umin, vmin);
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier);
					tessellator.addVertexWithUV(segStopX, segStopY, segStopZ, umin, vmax);
				}
			}
			tessellator.draw();
		}
		
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
