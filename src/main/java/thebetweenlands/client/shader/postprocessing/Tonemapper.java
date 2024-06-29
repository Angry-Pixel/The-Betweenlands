package thebetweenlands.client.shader.postprocessing;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;

public class Tonemapper extends PostProcessingEffect<Tonemapper> {
	private float gamma = 1.0F;
	private float exposure = 1.0F;

	private int gammaUniformID = -1;
	private int exposureUniformID = -1;

	public Tonemapper setGamma(float gamma) {
		this.gamma = gamma;
		return this;
	}

	public Tonemapper setExposure(float exposure) {
		this.exposure = exposure;
		return this;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {TheBetweenlands.prefix("shaders/postprocessing/tonemapper/tonemapper.vsh"), TheBetweenlands.prefix("shaders/postprocessing/tonemapper/tonemapper.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.gammaUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_gamma");
		this.exposureUniformID = GlStateManager._glGetUniformLocation(this.getShaderProgram(), "u_exposure");
		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadFloat(this.gammaUniformID, this.gamma);
		this.uploadFloat(this.exposureUniformID, this.exposure);
	}
}
