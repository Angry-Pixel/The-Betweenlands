package thebetweenlands.client.render.shader;

import net.minecraft.client.shader.Framebuffer;

public class ResizableFramebuffer {
	private final boolean depth;
	private Framebuffer framebuffer;

	public ResizableFramebuffer(boolean depth) {
		this.depth = depth;
	}

	/**
	 * Resizes the framebuffer to the specified dimensions and returns the framebuffer
	 * @param width
	 * @param height
	 * @return
	 */
	public Framebuffer getFramebuffer(int width, int height) {
		if(this.framebuffer == null || width != this.framebuffer.framebufferWidth || height != this.framebuffer.framebufferHeight) {
			if(this.framebuffer != null) {
				this.framebuffer.deleteFramebuffer();
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
			this.framebuffer.deleteFramebuffer();
	}
}
