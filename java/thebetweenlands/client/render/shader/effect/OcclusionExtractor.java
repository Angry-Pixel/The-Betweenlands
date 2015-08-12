package thebetweenlands.client.render.shader.effect;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

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
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 0);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.worldDepth.framebufferTexture);
		ARBShaderObjects.glUniform1iARB(this.worldDepthFBOUniformID, 0);
		
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB + 1);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.clipPlaneDepth.framebufferTexture);
		ARBShaderObjects.glUniform1iARB(this.clipPlaneDepthFBOUniformID, 1);
		
		GL13.glActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB );
	}
}
