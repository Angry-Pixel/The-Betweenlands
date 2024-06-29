package thebetweenlands.client.shader.postprocessing;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class Warp extends PostProcessingEffect<Warp> {
	private float scale = 1.0F;
	private float timeScale = 1.0F;
	private float multipier = 1.0F;
	private float xOffset = 0.0F;
	private float yOffset = 0.0F;
	private float warpX = 1.0F;
	private float warpY = 1.0F;

	private int timeUniformID = -1;
	private int scaleUniformID = -1;
	private int timeScaleUniformID = -1;
	private int multiplierUniformID = -1;
	private int xOffsetUniformID = -1;
	private int yOffsetUniformID = -1;
	private int warpXUniformID = -1;
	private int warpYUniformID = -1;

	public Warp setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public Warp setTimeScale(float timeScale) {
		this.timeScale = timeScale;
		return this;
	}

	public Warp setMultiplier(float multiplier) {
		this.multipier = multiplier;
		return this;
	}

	public Warp setOffset(float xOffset, float yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		return this;
	}

	public Warp setWarpDir(float warpX, float warpY) {
		this.warpX = warpX;
		this.warpY = warpY;
		return this;
	}

	@Override
	protected boolean initEffect() {
		this.timeUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_msTime");
		this.scaleUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_scale");
		this.timeScaleUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_timeScale");
		this.multiplierUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_multiplier");
		this.xOffsetUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_xOffset");
		this.yOffsetUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_yOffset");
		this.warpXUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_warpX");
		this.warpYUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_warpY");
		return /*this.timeUniformID >= 0 && this.scaleUniformID >= 0 && this.timeScaleUniformID >= 0 && this.multiplierUniformID >= 0 && this.xOffsetUniformID >= 0 && this.yOffsetUniformID >= 0*/true;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("shaders/postprocessing/warp/warp.vsh"), TheBetweenlands.prefix("shaders/postprocessing/warp/warp.fsh")};
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadFloat(this.timeUniformID, System.nanoTime() / 1000000.0F);
		this.uploadFloat(this.scaleUniformID, this.scale);
		this.uploadFloat(this.timeScaleUniformID, this.timeScale);
		this.uploadFloat(this.multiplierUniformID, this.multipier);
		this.uploadFloat(this.xOffsetUniformID, this.xOffset);
		this.uploadFloat(this.yOffsetUniformID, this.yOffset);
		this.uploadFloat(this.warpXUniformID, this.warpX);
		this.uploadFloat(this.warpYUniformID, this.warpY);
	}
}