package thebetweenlands.client.render.shader;

import java.io.IOException;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class GeometryBuffer extends AbstractTexture {
	private Framebuffer geometryBuffer;
	private DepthBuffer geometryDepthBuffer;
	private boolean depthBuffer = false;

	protected final TextureManager textureManager;
	protected final ResourceLocation nameDiffuse, nameDepth;

	/**
	 * Creates a new geometry buffer
	 * @param textureManager The texture manager
	 * @param name The diffuse texture's resource name
	 * @param name The depth texture's resource name
	 * @param depthBuffer Whether this geometry buffer should have a depth buffer
	 */
	public GeometryBuffer(TextureManager textureManager, @Nullable ResourceLocation nameDiffuse, @Nullable ResourceLocation nameDepth, boolean depthBuffer) {
		this.textureManager = textureManager;
		this.depthBuffer = depthBuffer;
		this.nameDiffuse = nameDiffuse;
		this.nameDepth = nameDepth;

		if(this.textureManager != null && this.nameDiffuse != null) {
			this.textureManager.loadTexture(this.nameDiffuse, this);
		}
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
			this.geometryBuffer = new FloatFramebuffer(width, height, this.depthBuffer);
			changed = true;
		}
		if(width != this.geometryBuffer.framebufferWidth
				|| height != this.geometryBuffer.framebufferHeight) {
			this.geometryBuffer.deleteFramebuffer();
			this.geometryBuffer = new FloatFramebuffer(width, height, this.depthBuffer);
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
		boolean changed = false;
		if(this.depthBuffer && this.geometryBuffer != null) {
			if(this.geometryDepthBuffer == null) {
				this.geometryDepthBuffer = new DepthBuffer(this.textureManager, this.nameDepth);
				changed = true;
			}
			if(this.geometryDepthBuffer.blitDepthBuffer(this.geometryBuffer)) {
				changed = true;
			}
		}
		return changed;
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
			return this.geometryDepthBuffer.getGlTextureId();
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
	 * Returns whether the buffer has been initialized already
	 * @return
	 */
	public boolean isInitialized() {
		return this.geometryBuffer != null;
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
			GlStateManager.clearColor(r, g, b, a);
			GlStateManager.clearDepth(depth);
			GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
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

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException { }

	@Override
	public int getGlTextureId() {
		if(this.geometryBuffer != null) {
			return this.geometryBuffer.framebufferTexture;
		}
		return -1;
	}

	@Override
	public void deleteGlTexture() {
		this.deleteBuffers();
	}
}
