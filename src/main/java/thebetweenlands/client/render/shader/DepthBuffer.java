package thebetweenlands.client.render.shader;

import java.io.IOException;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class DepthBuffer extends AbstractTexture {
	private int depthTextureId = -1;
	private int width, height;

	/**
	 * Creates a new depth buffer
	 * @param textureManager The texture manager
	 * @param name The texture's resource name
	 */
	public DepthBuffer(@Nullable TextureManager textureManager, @Nullable ResourceLocation name) {
		if(textureManager != null && name != null) {
			textureManager.loadTexture(name, this);
		}
	}

	/**
	 * Copies the depth buffer of the specified {@link Framebuffer} into a color buffer
	 * <p><b>Note:</b> Binds the input Framebuffer
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean blitDepthBuffer(Framebuffer input) {
		boolean changed = false;

		if(this.depthTextureId != -1 && (this.width != input.framebufferWidth || this.height != input.framebufferHeight)) {
			TextureUtil.deleteTexture(this.depthTextureId);
			this.depthTextureId = -1;
		}

		if(this.depthTextureId == -1) {
			this.width = input.framebufferWidth;
			this.height = input.framebufferHeight;

			TextureUtil.allocateTexture(this.depthTextureId = TextureUtil.glGenTextures(), this.width, this.height);

			changed = true;
		}

		input.bindFramebuffer(false);
		GlStateManager.bindTexture(this.depthTextureId);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, this.width, this.height, 0);

		return changed;
	}

	/**
	 * Deletes the buffer
	 */
	public void deleteBuffer() {
		if(this.depthTextureId != -1) {
			TextureUtil.deleteTexture(this.depthTextureId);
		}
	}

	@Override
	public void loadTexture(IResourceManager resourceManager) throws IOException { }

	@Override
	public int getGlTextureId() {
		return this.depthTextureId;
	}

	@Override
	public void deleteGlTexture() {
		this.deleteBuffer();
	}
}
