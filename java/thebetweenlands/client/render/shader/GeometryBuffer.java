package thebetweenlands.client.render.shader;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;

public class GeometryBuffer {
	private Framebuffer geometryBuffer;
	private Framebuffer geometryDepthBuffer;
	private boolean depthBuffer = false;

	/**
	 * Creates a new geometry buffer
	 * @param depthBuffer Whether this geometry buffer should have a depth buffer
	 */
	public GeometryBuffer(boolean depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	/**
	 * Initializes the buffers if necessary and updates the dimensions
	 * @param input
	 * @return
	 */
	public boolean updateBuffers(Framebuffer input) {
		boolean changed = false;

		//Init geometry buffer
		if(this.geometryBuffer == null) {
			this.geometryBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, this.depthBuffer);
			changed = true;
		}
		if(input.framebufferWidth != this.geometryBuffer.framebufferWidth
				|| input.framebufferHeight != this.geometryBuffer.framebufferHeight) {
			this.geometryBuffer.deleteFramebuffer();
			this.geometryBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, this.depthBuffer);
			changed = true;
		}

		if(this.depthBuffer) {
			//Init geometry depth buffer
			if(this.geometryDepthBuffer == null) {
				this.geometryDepthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
				changed = true;
			}
			if(input.framebufferWidth != this.geometryDepthBuffer.framebufferWidth
					|| input.framebufferHeight != this.geometryDepthBuffer.framebufferHeight) {
				this.geometryDepthBuffer.deleteFramebuffer();
				this.geometryDepthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Copies the depth buffer into a seperate texture
	 */
	public void updateDepth() {
		if(this.depthBuffer) {
			//Update depth buffer
			this.geometryBuffer.bindFramebuffer(false);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.geometryDepthBuffer.framebufferTexture);
			GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
					this.geometryDepthBuffer.framebufferTextureWidth, 
					this.geometryDepthBuffer.framebufferTextureHeight, 
					0);
		}
	}

	/**
	 * Returns the diffuse buffer
	 * @return
	 */
	public int getDiffuseTexture() {
		if(this.geometryBuffer == null) {
			this.geometryBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, this.depthBuffer);
		}
		return this.geometryBuffer.framebufferTexture;
	}

	/**
	 * Returns the depth buffer
	 * @return
	 */
	public int getDepthTexture() {
		if(this.depthBuffer) {
			if(this.geometryDepthBuffer == null) {
				this.geometryDepthBuffer = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
			}
			return this.geometryDepthBuffer.framebufferTexture;
		}
		return -1;
	}

	public void bind() {
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindFramebuffer(false);
		}
	}

	/**
	 * Clears the buffer
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
		if(this.geometryDepthBuffer != null) this.geometryDepthBuffer.deleteFramebuffer();
	}

	/**
	 * Returns whether this geometry buffer has a depth buffer
	 * @return
	 */
	public boolean hasDepthBuffer() {
		return this.depthBuffer;
	}
}
