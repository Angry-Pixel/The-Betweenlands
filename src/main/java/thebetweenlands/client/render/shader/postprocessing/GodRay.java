package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class GodRay extends PostProcessingEffect<GodRay> {
	private Framebuffer occlusionMap = null;

	private int occlusionMapUniformID = -1;
	private int godRayXUniformID = -1;
	private int godRayYUniformID = -1;
	private int exposureUniformID = -1;
	private int decayUniformID = -1;
	private int densityUniformID = -1;
	private int weightUniformID = -1;
	private int illuminationDecayUniformID = -1;

	private float godRayX = 0.5F;
	private float godRayY = 0.5F;

	private float exposure = 1.0F;
	private float decay = 1.0F;
	private float density = 1.0F;
	private float weight = 1.0F;
	private float illuminationDecay = 1.0F;

	public GodRay setOcclusionMap(Framebuffer occlusionMap) {
		this.occlusionMap = occlusionMap;
		return this;
	}

	public GodRay setRayPos(float x, float y) {
		this.godRayX = x;
		this.godRayY = y;
		return this;
	}

	public GodRay setParams(float exposure, float decay, float density, float weight, float illuminationDecay) {
		this.exposure = exposure;
		this.decay = decay;
		this.density = density;
		this.weight = weight;
		this.illuminationDecay = illuminationDecay;
		return this;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/godray/godray.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/godray/godray.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.occlusionMapUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "s_occlusion");
		this.godRayXUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_godRayX");
		this.godRayYUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_godRayY");
		this.exposureUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_exposure");
		this.decayUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_decay");
		this.densityUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_density");
		this.weightUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_weight");
		this.illuminationDecayUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "u_illuminationDecay");
		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		//Override diffuse sampler
		this.uploadSampler(this.occlusionMapUniformID, this.occlusionMap.framebufferTexture, 0);

		this.uploadFloat(this.godRayXUniformID, this.godRayX);
		this.uploadFloat(this.godRayYUniformID, this.godRayY);
		this.uploadFloat(this.exposureUniformID, this.exposure);
		this.uploadFloat(this.decayUniformID, this.decay);
		this.uploadFloat(this.densityUniformID, this.density);
		this.uploadFloat(this.weightUniformID, this.weight);
		this.uploadFloat(this.illuminationDecayUniformID, this.illuminationDecay);
	}
}
