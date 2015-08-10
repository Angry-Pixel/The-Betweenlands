package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBShaderObjects;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class OcclusionExtractor extends DeferredEffect {
	private Framebuffer worldDepth = null;
	private Framebuffer clipPlaneDepth = null;

	private int worldDepthFBOUniformID = -1;
	private int clipPlaneDepthFBOUniformID = -1;

	public void setFBOs(Framebuffer worldDepth, Framebuffer clipPlaneDepth) {
		this.worldDepth = worldDepth;
		this.clipPlaneDepth = clipPlaneDepth;
	}

	@Override
	protected ResourceLocation[] getShaders() {
		return new ResourceLocation[] {new ResourceLocation("thebetweenlands:shaders/deferred/occlusionextractor/occlusionextractor.vsh"), new ResourceLocation("thebetweenlands:shaders/deferred/occlusionextractor/occlusionextractor.fsh")};
	}

	@Override
	protected boolean initEffect() {
		this.worldDepthFBOUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "s_world_depth");
		this.clipPlaneDepthFBOUniformID = ARBShaderObjects.glGetUniformLocationARB(this.getShaderProgram(), "s_clipPlane_depth");
		return true;
	}

	@Override
	protected void uploadUniforms() {
		ARBShaderObjects.glUniform1iARB(this.worldDepthFBOUniformID, this.worldDepth.framebufferTexture);
		ARBShaderObjects.glUniform1iARB(this.clipPlaneDepthFBOUniformID, this.clipPlaneDepth.framebufferTexture);
	}
}
