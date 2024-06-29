package thebetweenlands.client.shader;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.ARBTextureFloat;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FloatFramebuffer extends RenderTarget {
	public final boolean useFloatBuffer;

	public FloatFramebuffer(int width, int height, boolean useDepth) {
		this(width, height, useDepth, ShaderHelper.INSTANCE.isShaderSupported() && ShaderHelper.INSTANCE.isHDRActive());
	}

	public FloatFramebuffer(int width, int height, boolean useDepth, boolean useFloatBuffer) {
		super(useDepth);
		this.resize(width, height, Minecraft.ON_OSX);
		this.useFloatBuffer = useFloatBuffer;
		if(this.useFloatBuffer) {
			int colorFormat = ShaderHelper.INSTANCE.isFloatBufferSupported() ? GL30.GL_RGBA16F : ShaderHelper.INSTANCE.isARBFloatBufferSupported() ? ARBTextureFloat.GL_RGB16F_ARB : -1;
			if(colorFormat != -1) {
				//Use float buffers for HDR
				RenderSystem.bindTexture(this.colorTextureId);
				GlStateManager._texImage2D(GL11.GL_TEXTURE_2D, 0, colorFormat, this.width, this.height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, null);
			}
		}
	}
}
