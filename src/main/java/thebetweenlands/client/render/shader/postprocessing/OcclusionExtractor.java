package thebetweenlands.client.render.shader.postprocessing;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class OcclusionExtractor extends PostProcessingEffect<OcclusionExtractor> {
	private int worldDepth = -1;
	private int clipPlaneDepth = -1;

	private int worldDepthFBOUniformID = -1;
	private int clipPlaneDepthFBOUniformID = -1;

	public void setDepthTextures(int worldDepth, int clipPlaneDepth) {
		this.worldDepth = worldDepth;
		this.clipPlaneDepth = clipPlaneDepth;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/postprocessing/occlusionextractor/occlusionextractor.vsh"), new ResourceLocation("thebetweenlands:shaders/postprocessing/occlusionextractor/occlusionextractor.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.worldDepthFBOUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "s_world_depth");
		this.clipPlaneDepthFBOUniformID = OpenGlHelper.glGetUniformLocation(this.getShaderProgram(), "s_clipPlane_depth");
		return true;
	}

	@Override
	protected void uploadUniforms(float partialTicks) {
		//Override diffuse sampler
		this.uploadSampler(this.worldDepthFBOUniformID, this.worldDepth, 0);
		this.uploadSampler(this.clipPlaneDepthFBOUniformID, this.clipPlaneDepth, 1);
	}
}
