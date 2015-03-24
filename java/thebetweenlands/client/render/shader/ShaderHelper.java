package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import thebetweenlands.event.debugging.DebugHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class ShaderHelper {
	public static final ShaderHelper INSTANCE = new ShaderHelper();
	private Framebuffer depthBuffer;

	public Framebuffer getDepthBuffer() {
		return this.depthBuffer;
	}

	public void updateDepthBuffer(Framebuffer input) {
		if(this.depthBuffer == null) {
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
		}
		if(input.framebufferWidth != this.depthBuffer.framebufferWidth
				|| input.framebufferHeight != this.depthBuffer.framebufferHeight) {
			this.depthBuffer.deleteFramebuffer();
			this.depthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
		}
		input.bindFramebuffer(false);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthBuffer.framebufferTexture);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
				this.depthBuffer.framebufferTextureWidth, 
				this.depthBuffer.framebufferTextureHeight, 
				0);
	}
}
