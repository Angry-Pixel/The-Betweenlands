package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

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
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/tonemapper/tonemapper.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/tonemapper/tonemapper.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.gammaUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_gamma");
		this.exposureUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_exposure");
		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		this.uploadFloat(this.gammaUniformID, this.gamma);
		this.uploadFloat(this.exposureUniformID, this.exposure);
	}
}
