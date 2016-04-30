package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

public class OcclusionExtractor extends DeferredEffect {
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
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/occlusionextractor/occlusionextractor.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/occlusionextractor/occlusionextractor.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.worldDepthFBOUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "s_world_depth");
		this.clipPlaneDepthFBOUniformID = OpenGlHelper.func_153194_a(this.getShaderProgram(), "s_clipPlane_depth");
		return true;
	}

	@Override
	protected void uploadUniforms() {
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 0);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.worldDepth);
		OpenGlHelper.func_153163_f(this.worldDepthFBOUniformID, 0);

		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 1);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.clipPlaneDepth);
		OpenGlHelper.func_153163_f(this.clipPlaneDepthFBOUniformID, 1);

		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB );
	}
}
