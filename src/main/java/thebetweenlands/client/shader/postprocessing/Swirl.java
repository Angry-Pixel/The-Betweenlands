package thebetweenlands.client.shader.postprocessing;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class Swirl extends PostProcessingEffect<Swirl> {
	private float angle = 0.0F;
	private int angleUniformID = -1;

	public Swirl setAngle(float angle) {
		this.angle = angle;
		return this;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("thebetweenlands:shaders/postprocessing/swirl/swirl.vsh"), TheBetweenlands.prefix("thebetweenlands:shaders/postprocessing/swirl/swirl.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.angleUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_angle");
		return /*this.timeUniformID >= 0 && this.scaleUniformID >= 0 && this.timeScaleUniformID >= 0 && this.multiplierUniformID >= 0 && this.xOffsetUniformID >= 0 && this.yOffsetUniformID >= 0*/true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadFloat(this.angleUniformID, this.angle);
	}
}
