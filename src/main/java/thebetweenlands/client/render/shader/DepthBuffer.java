package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.shader.Framebuffer;

public class DepthBuffer {
	private Framebuffer depthBufferFBO;

	/**
	 * Copies the depth buffer of the specified {@link Framebuffer} into a color buffer.
	 * Resizes the underlying FBO if necessary.
	 * <p><b>Note:</b> Binds the input Framebuffer
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean blitDepthBuffer(Framebuffer input) {
		boolean changed = false;

		if(this.depthBufferFBO == null) {
			this.depthBufferFBO = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			changed = true;
		}
		if(input.framebufferWidth != this.depthBufferFBO.framebufferWidth
				|| input.framebufferHeight != this.depthBufferFBO.framebufferHeight) {
			this.depthBufferFBO.deleteFramebuffer();
			this.depthBufferFBO = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
			changed = true;
		}

		input.bindFramebuffer(false);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthBufferFBO.framebufferTexture);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
				this.depthBufferFBO.framebufferTextureWidth, 
				this.depthBufferFBO.framebufferTextureHeight, 
				0);

		return changed;
	}

	/**
	 * Returns the texture
	 * @return
	 */
	public int getTexture() {
		if(this.depthBufferFBO != null)
			return this.depthBufferFBO.framebufferTexture;
		return -1;
	}

	/**
	 * Clears the buffer
	 * <p><b>Note:</b> Binds the FBO
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 * @param depth Depth
	 */
	public void clear(float r, float g, float b, float a, double depth) {
		if(this.depthBufferFBO != null) {
			this.depthBufferFBO.bindFramebuffer(false);
			GL11.glClearColor(r, g, b, a);
			GL11.glClearDepth(depth);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		}
	}

	/**
	 * Clears the buffer with depth = 1.0
	 * <p><b>Note:</b> Binds the FBO
	 * @param r Red
	 * @param g Green
	 * @param b Blue
	 * @param a Alpha
	 */
	public void clear(float r, float g, float b, float a) {
		this.clear(r, g, b, a, 1.0D);
	}

	/**
	 * Deletes the buffer
	 */
	public void deleteBuffer() {
		if(this.depthBufferFBO != null) 
			this.depthBufferFBO.deleteFramebuffer();
	}
}
