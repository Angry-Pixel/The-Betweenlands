package thebetweenlands.client.shader;

import javax.annotation.Nullable;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

public class GeometryBuffer extends AbstractTexture {
	@Nullable
	private RenderTarget geometryBuffer;
	@Nullable
	private DepthBuffer geometryDepthBuffer;
	private final boolean depthBuffer;

	protected final TextureManager textureManager;
	@Nullable
	protected final ResourceLocation nameDiffuse, nameDepth;

	/**
	 * Creates a new geometry buffer
	 * @param textureManager The texture manager
	 * @param nameDiffuse The diffuse texture's resource name
	 * @param nameDepth The depth texture's resource name
	 * @param depthBuffer Whether this geometry buffer should have a depth buffer
	 */
	public GeometryBuffer(TextureManager textureManager, @Nullable ResourceLocation nameDiffuse, @Nullable ResourceLocation nameDepth, boolean depthBuffer) {
		this.textureManager = textureManager;
		this.depthBuffer = depthBuffer;
		this.nameDiffuse = nameDiffuse;
		this.nameDepth = nameDepth;

		if(this.nameDiffuse != null) {
			this.textureManager.register(this.nameDiffuse, this);
		}
	}

	/**
	 * Initializes the geometry buffer if necessary and updates the dimensions
	 * @param width
	 * @param height
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean updateGeometryBuffer(int width, int height) {
		boolean changed = false;

		//Init geometry buffer
		if(this.geometryBuffer == null) {
			this.geometryBuffer = new FloatFramebuffer(width, height, this.depthBuffer);
			changed = true;
		}
		if(width != this.geometryBuffer.viewWidth
				|| height != this.geometryBuffer.viewHeight) {
			this.geometryBuffer.destroyBuffers();
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
			return this.geometryBuffer.getColorTextureId();
		return -1;
	}

	/**
	 * Returns the depth buffer
	 * @return
	 */
	public int getDepthTexture() {
		if(this.depthBuffer && this.geometryDepthBuffer != null) {
			return this.geometryDepthBuffer.getId();
		}
		return -1;
	}

	/**
	 * Binds the underlying FBO
	 */
	public void bind() {
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindWrite(false);
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
			this.geometryBuffer.bindWrite(false);
			RenderSystem.clearColor(r, g, b, a);
			RenderSystem.clearDepth(depth);
			RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT, Minecraft.ON_OSX);
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
		if(this.geometryBuffer != null) this.geometryBuffer.destroyBuffers();
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
	public void load(ResourceManager resourceManager) {

	}

	@Override
	public int getId() {
		if(this.geometryBuffer != null) {
			return this.geometryBuffer.getColorTextureId();
		}
		return -1;
	}

	@Override
	public void releaseId() {
		this.deleteBuffers();
	}
}
