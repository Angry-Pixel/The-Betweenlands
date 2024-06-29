package thebetweenlands.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;

public class ResizableFramebuffer {
	private final boolean depth;
	private RenderTarget framebuffer;

	public ResizableFramebuffer(boolean depth) {
		this.depth = depth;
	}

	/**
	 * Resizes the framebuffer to the specified dimensions and returns the framebuffer
	 * @param width
	 * @param height
	 * @return
	 */
	public RenderTarget getFramebuffer(int width, int height) {
		if(this.framebuffer == null || width != this.framebuffer.viewWidth || height != this.framebuffer.viewHeight) {
			if(this.framebuffer != null) {
				this.framebuffer.destroyBuffers();
			}
			this.framebuffer = new FloatFramebuffer(width, height, this.depth);
		}
		return this.framebuffer;
	}

	/**
	 * Returns whether the underlying frambuffer has a depth buffer
	 * @return
	 */
	public boolean hasDepth() {
		return this.depth;
	}

	/**
	 * Deletes the framebuffer
	 */
	public void delete() {
		if(this.framebuffer != null)
			this.framebuffer.destroyBuffers();
	}
}
