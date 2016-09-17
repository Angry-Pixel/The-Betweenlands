package thebetweenlands.client.render.shader.postprocessing;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class OcclusionExtractor extends PostProcessingEffect {
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
	protected void uploadUniforms() {
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.worldDepth);
		OpenGlHelper.glUniform1i(this.worldDepthFBOUniformID, 0);

		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.clipPlaneDepth);
		OpenGlHelper.glUniform1i(this.clipPlaneDepthFBOUniformID, 1);

		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB );
	}
}
