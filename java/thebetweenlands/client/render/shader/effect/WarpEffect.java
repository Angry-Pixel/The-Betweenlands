package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBShaderObjects;

import net.minecraft.util.ResourceLocation;

public class WarpEffect extends DeferredEffect {
	private float scale = 1.0F;
	private float timeScale = 1.0F;
	private float multipier = 1.0F;
	private float xOffset = 0.0F;
	private float yOffset = 0.0F;
	
	private int timeUniformID = -1;
	private int scaleUniformID = -1;
	private int timeScaleUniformID = -1;
	private int multiplierUniformID = -1;
	private int xOffsetUniformID = -1;
	private int yOffsetUniformID = -1;
	
	public WarpEffect setScale(float scale) {
		this.scale = scale;
		return this;
	}
	
	public WarpEffect setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}
	
	public WarpEffect setMultiplier(float multiplier) {
		this.multipier = multiplier;
		return this;
	}
	
	public WarpEffect setOffset(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		return this;
	}
	
	@Override
	protected boolean initEffect() {
		this.timeUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_msTime");
		this.scaleUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_scale");
		this.timeScaleUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_timeScale");
		this.multiplierUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_multiplier");
		this.xOffsetUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_xOffset");
		this.yOffsetUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "u_yOffset");
		return /*this.timeUniformID >= 0 && this.scaleUniformID >= 0 && this.timeScaleUniformID >= 0 && this.multiplierUniformID >= 0 && this.xOffsetUniformID >= 0 && this.yOffsetUniformID >= 0*/true;
	}
	
	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/warp/warp.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/warp/warp.fsh")};
	}
	
	@Override
	protected void uploadUniforms() {
		ARBShaderObjects.glUniform1fARB(this.timeUniformID, System.nanoTime() / 1000000.0F);
		ARBShaderObjects.glUniform1fARB(this.scaleUniformID, this.scale);
		ARBShaderObjects.glUniform1fARB(this.timeScaleUniformID, this.timeScale);
		ARBShaderObjects.glUniform1fARB(this.multiplierUniformID, this.multipier);
		ARBShaderObjects.glUniform1fARB(this.xOffsetUniformID, this.xOffset);
		ARBShaderObjects.glUniform1fARB(this.yOffsetUniformID, this.yOffset);
	}
}