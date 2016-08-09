package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.shader.Framebuffer;

public class GeometryBuffer {
	private Framebuffer geometryBuffer;
	private DepthBuffer geometryDepthBuffer;
	private boolean depthBuffer = false;

	/**
	 * Creates a new geometry buffer
	 * @param depthBuffer Whether this geometry buffer should have a depth buffer
	 */
	public GeometryBuffer(boolean depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	/**
	 * Initializes the geometry buffer if necessary and updates the dimensions
	 * @param input
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean updateGeometryBuffer(int width, int height) {
		boolean changed = false;

		//Init geometry buffer
		if(this.geometryBuffer == null) {
			this.geometryBuffer = new Framebuffer(width, height, this.depthBuffer);
			changed = true;
		}
		if(width != this.geometryBuffer.framebufferWidth
				|| height != this.geometryBuffer.framebufferHeight) {
			this.geometryBuffer.deleteFramebuffer();
			this.geometryBuffer = new Framebuffer(width, height, this.depthBuffer);
			changed = true;
		}

		return changed;
	}

	/**
	 * Initializes the geometry buffer if necessary and updates the dimensions.
	 * Copies the depth buffer into a seperate texture
	 * <p><b>Note:</b> Binds the FBO
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean updateDepthBuffer() {
		if(this.depthBuffer) {
			return this.geometryDepthBuffer.blitDepthBuffer(this.geometryBuffer);
		}
		return false;
	}

	/**
	 * Returns the diffuse buffer
	 * @return
	 */
	public int getDiffuseTexture() {
		if(this.geometryBuffer != null)
			return this.geometryBuffer.framebufferTexture;
		return -1;
	}

	/**
	 * Returns the depth buffer
	 * @return
	 */
	public int getDepthTexture() {
		if(this.depthBuffer && this.geometryDepthBuffer != null) {
			return this.geometryDepthBuffer.getTexture();
		}
		return -1;
	}

	/**
	 * Binds the underlying FBO
	 */
	public void bind() {
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindFramebuffer(false);
		} else
			throw new NullPointerException("The buffers aren't initialized yet");
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
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindFramebuffer(false);
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
	 * Deletes both buffers
	 */
	public void deleteBuffers() {
		if(this.geometryBuffer != null) this.geometryBuffer.deleteFramebuffer();
		if(this.geometryDepthBuffer != null) this.geometryDepthBuffer.deleteBuffer();
	}

	/**
	 * Returns whether this geometry buffer has a depth buffer
	 * @return
	 */
	public boolean hasDepthBuffer() {
		return this.depthBuffer;
	}
}
