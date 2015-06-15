package thebetweenlands.client.render.sky;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import scala.util.Random;

public class AuroraRenderer {
	private double x, y, z;
	private Vector2d direction;

	public AuroraRenderer(double x, double y, double z, Vector2d direction) {
		this.x = x;
		this.y = y;
		this.z = z;
		direction.normalize();
		this.direction = direction;
	}

	private Vector2d getRotatedVec(double offset) {
		Vector3d upVec = new Vector3d(0, 1, 0);
		upVec.cross(upVec, new Vector3d(this.direction.x, 0, this.direction.y));
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

	public void render(float partialTicks) {
		Tessellator tessellator = Tessellator.instance;

		
		
		
		List<Vector3f> gradients = new ArrayList<Vector3f>();

		gradients.add(new Vector3f(0, 1, 0));
		gradients.add(new Vector3f(0, 1, 1));
		gradients.add(new Vector3f(0, 0.4F, 1));

		int segments = 20;
		int subSegments = 10;
		double segmentWidth = 4.0D;
		double segmentHeight = 4.0D;
		int cGradients = gradients.size();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glDisable(GL11.GL_LIGHTING);

		Random rand = new Random();
		rand.setSeed(0);
		
		for(int i = 0; i < segments; i++) {
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thebetweenlands:textures/sky/aurora" + (rand.nextInt(5) + 1) + ".png"));
			
			tessellator.startDrawingQuads();
			
			tessellator.setBrightness(240);
			
			for(int si = 0; si < subSegments; si++) {
				float offset1 = this.interpolatedNoise((i + (si) / (float)subSegments) * 2 + (float)(System.nanoTime() / 5000000000.0D)) * 4;
				float offset2 = this.interpolatedNoise((i + (si+1) / (float)subSegments) * 2 + (float)(System.nanoTime() / 5000000000.0D)) * 4;
				
				double segStartX = this.x + this.direction.x * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1).x;
				double segStartZ = this.z + this.direction.y * (i + (si) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset1).y;

				double segStopX = this.x + this.direction.x * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2).x;
				double segStopZ = this.z + this.direction.y * (i + (si+1) / (float)subSegments) * segmentWidth + this.getRotatedVec(offset2).y;

				double umin = ((si+1) / (float)subSegments);
				double umax = ((si) / (float)subSegments);

				for(int gi = 0; gi < cGradients - 1; gi++) {
					double segStartY = this.y + segmentHeight / (float)(cGradients-1) * gi;
					double segStopY = this.y + segmentHeight / (float)(cGradients-1) * (gi + 1);

					double vmax = ((gi+1) / (float)(cGradients-1));
					double vmin = ((gi) / (float)(cGradients-1));
					
					Vector3f bottomGradient = gradients.get(gi);
					Vector3f topGradient = gradients.get(gi+1);
					
					//Front face
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, 0.5F);
					tessellator.addVertexWithUV(segStartX, segStopY, segStartZ, umax, vmax);
					tessellator.addVertexWithUV(segStopX, segStopY, segStopZ, umin, vmax);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, 0.5F);
					tessellator.addVertexWithUV(segStopX, segStartY, segStopZ, umin, vmin);
					tessellator.addVertexWithUV(segStartX, segStartY, segStartZ, umax, vmin);
					
					//Back face
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, 0.5F);
					tessellator.addVertexWithUV(segStartX, segStopY, segStartZ, umax, vmax);
					tessellator.setColorRGBA_F(bottomGradient.x, bottomGradient.y, bottomGradient.z, 0.5F);
					tessellator.addVertexWithUV(segStartX, segStartY, segStartZ, umax, vmin);
					tessellator.addVertexWithUV(segStopX, segStartY, segStopZ, umin, vmin);
					tessellator.setColorRGBA_F(topGradient.x, topGradient.y, topGradient.z, 0.5F);
					tessellator.addVertexWithUV(segStopX, segStopY, segStopZ, umin, vmax);
				}
			}
			tessellator.draw();
		}
	}
}
