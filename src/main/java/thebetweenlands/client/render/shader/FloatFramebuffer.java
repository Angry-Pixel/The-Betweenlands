package thebetweenlands.client.render.shader;

import java.nio.IntBuffer;

import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;

public class FloatFramebuffer extends Framebuffer {
	public final boolean useFloatBuffer;

	public FloatFramebuffer(int width, int height, boolean useDepth) {
		this(width, height, useDepth, ShaderHelper.INSTANCE.isShaderSupported() && ShaderHelper.INSTANCE.isHDRActive());
	}

	public FloatFramebuffer(int width, int height, boolean useDepth, boolean useFloatBuffer) {
		super(width, height, useDepth);
		this.useFloatBuffer = useFloatBuffer;
		if(this.useFloatBuffer) {
			int colorFormat = ShaderHelper.INSTANCE.isFloatBufferSupported() ? GL30.GL_RGBA16F : ShaderHelper.INSTANCE.isARBFloatBufferSupported() ? ARBTextureFloat.GL_RGB16F_ARB : -1;
			if(colorFormat != -1) {
				//Use float buffers for HDR
				GlStateManager.bindTexture(this.framebufferTexture);
				GlStateManager.glTexImage2D(GL11.GL_TEXTURE_2D, 0, colorFormat, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (IntBuffer)null);
			}
		}
	}
}
