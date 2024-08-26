package thebetweenlands.client.shader;

import javax.annotation.Nullable;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;

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
			textureManager.register(name, this);
		}
	}

	/**
	 * Copies the depth buffer of the specified {@link RenderTarget} into a color buffer
	 * <p><b>Note:</b> Binds the input Framebuffer
	 * @return Returns true if the FBO was initialized or resized
	 */
	public boolean blitDepthBuffer(RenderTarget input) {
		boolean changed = false;

		if(this.depthTextureId != -1 && (this.width != input.viewWidth || this.height != input.viewHeight)) {
			TextureUtil.releaseTextureId(this.depthTextureId);
			this.depthTextureId = -1;
		}

		if(this.depthTextureId == -1) {
			this.width = input.viewWidth;
			this.height = input.viewHeight;

			TextureUtil.prepareImage(this.depthTextureId = TextureUtil.generateTextureId(), this.width, this.height);

			changed = true;
		}

		input.bindWrite(false);
		RenderSystem.bindTexture(this.depthTextureId);
		GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, this.width, this.height, 0);

		return changed;
	}

	/**
	 * Deletes the buffer
	 */
	public void deleteBuffer() {
		if(this.depthTextureId != -1) {
			TextureUtil.releaseTextureId(this.depthTextureId);
		}
	}

	@Override
	public void load(ResourceManager resourceManager) {

	}

	@Override
	public int getId() {
		return this.depthTextureId;
	}

	@Override
	public void releaseId() {
		this.deleteBuffer();
	}
}
