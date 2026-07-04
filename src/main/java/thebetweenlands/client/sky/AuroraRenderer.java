package thebetweenlands.client.sky;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.util.TextureAtlasHelper;

import java.util.List;

public class AuroraRenderer {
	private static final ResourceLocation AURORA_TEXTURE = TheBetweenlands.prefix("textures/sky/aurora.png");
	private static final TextureAtlasHelper ATLAS = new TextureAtlasHelper(320, 128, 64, 128, 0, 0);

	private final double x;
	private final double y;
	private final double z;
	private final Vector2d direction;
	private Vector2d currDirection;
	private final int tiles;

	private RandomSource rand;

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
		upVec.cross(new Vector3d(direction.x, 0, direction.y), upVec);
		Vector2d res = new Vector2d(upVec.x, upVec.z);
		res.mul(offset);
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
		return Mth.lerp(partialTicks, this.lastFadeTicks, this.fadeTicks) / 500.0F;
	}

	public void render(float partialTicks, float alphaMultiplier, PoseStack stack, Matrix4f projectionMatrix) {
		//TODO: Only generate vertices once per tick and then interpolate

		alphaMultiplier *= this.getAlpha(partialTicks);

		Tesselator tesselator = Tesselator.getInstance();

		int segments = this.tiles;
		int subSegments = 5;
		double segmentWidth = 15.0D;
		double segmentHeight = 25.0D;
		int cGradients = this.colorGradients.size();

		RenderSystem.disableCull();
		RenderSystem.enableBlend();

		stack.pushPose();
		stack.translate(this.x, 0, this.z);

		this.rand = RandomSource.create((long)(((int)(this.x + this.y + this.z))^((int)(this.x * this.y * this.z))));

		int randNoiseOffset = rand.nextInt(100);

		this.currDirection = new Vector2d(this.direction.x, this.direction.y);
		Vector2d prevDirection = currDirection;

		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, AURORA_TEXTURE);
		RenderSystem.enableBlend();

		BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

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

					float vmax = ((gi+1) / (float)(cGradients-1));
					float vmin = ((gi) / (float)(cGradients-1));

					float[][] interpolatedUVs = ATLAS.getInterpolatedUVs(textureSegment, (float)relUMin, vmin, (float)relUMax, vmax);

					vmin = 1 - interpolatedUVs[0][1];
					vmax = 1 - interpolatedUVs[1][1];

					float umin = interpolatedUVs[0][0];
					float umax = interpolatedUVs[1][0];

					Vector4f bottomGradient = this.colorGradients.get(gi);
					Vector4f topGradient = this.colorGradients.get(gi+1);

					Entity renderView = Minecraft.getInstance().getCameraEntity();

					double camDist = renderView != null ? Math.sqrt(((renderView.xo - segStopX) * (renderView.xo - segStopX)) + ((renderView.zo - segStopZ) * (renderView.zo - segStopZ))) : 0.0D;
					double camDistNext = renderView != null ? Math.sqrt(((renderView.xo - segStartX) * (renderView.xo - segStartX)) + ((renderView.zo - segStartZ) * (renderView.zo - segStartZ))) : 0.0D;
					float alphaGradMultiplier = salphaGradMultiplier;
					float alphaGradMultiplierNext = salphaGradMultiplierNext;
					float viewDist = Minecraft.getInstance().options.getEffectiveRenderDistance() * 16.0F  - 10.0F;

					if(camDistNext > viewDist) {
						alphaGradMultiplier *= (float) (10.0F / (camDistNext - (viewDist - 10.0F)));
					}
					if(camDist > viewDist) {
						alphaGradMultiplierNext *= (float) (10.0F / (camDist - (viewDist - 10.0F)));
					}

					//Front face
					builder.addVertex((float) (segStartX - this.x), (float) segStopY, (float) (segStartZ - this.z)).setColor(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier).setUv(umax, vmax);
					builder.addVertex((float) (segStopX - this.x), (float) segStopY, (float) (segStopZ - this.z)).setColor(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier).setUv(umin, vmax);
					builder.addVertex((float) (segStopX - this.x), (float) segStartY, (float) (segStopZ - this.z)).setColor(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier).setUv(umin, vmin);
					builder.addVertex((float) (segStartX - this.x), (float) segStartY, (float) (segStartZ - this.z)).setColor(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier).setUv(umax, vmin);

					//Back face
					//builder.addVertex((float) (segStartX - this.x), (float) segStopY, (float) (segStartZ - this.z)).setUv(umax, vmax).setColor(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplier * alphaMultiplier);
					//builder.addVertex((float) (segStartX - this.x), (float) segStartY, (float) (segStartZ - this.z)).setUv(umax, vmin).setColor(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplier * alphaMultiplier);
					//builder.addVertex((float) (segStopX - this.x), (float) segStartY, (float) (segStopZ - this.z)).setUv(umin, vmin).setColor(bottomGradient.x, bottomGradient.y, bottomGradient.z, bottomGradient.w * alphaGradMultiplierNext * alphaMultiplier);
					//builder.addVertex((float) (segStopX - this.x), (float) segStopY, (float) (segStopZ - this.z)).setUv(umin, vmax).setColor(topGradient.x, topGradient.y, topGradient.z, topGradient.w * alphaGradMultiplierNext * alphaMultiplier);
				}
			}
		}
		ShaderHelper.INSTANCE.getAuroraShader().setDefaultUniforms(VertexFormat.Mode.QUADS, stack.last().pose(), projectionMatrix, Minecraft.getInstance().getWindow());
		ShaderHelper.INSTANCE.getAuroraShader().apply();
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		RenderSystem.texParameter(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.enableBlend();
		BufferUploader.draw(builder.buildOrThrow());
		ShaderHelper.INSTANCE.getAuroraShader().clear();
		VertexBuffer.unbind();

		RenderSystem.disableBlend();
		RenderSystem.enableCull();

		stack.popPose();
	}
}
