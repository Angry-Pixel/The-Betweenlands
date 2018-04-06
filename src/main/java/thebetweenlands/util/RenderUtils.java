package thebetweenlands.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.shader.Framebuffer;

public class RenderUtils {

	@SuppressWarnings("unchecked")
	public static <T extends LayerRenderer<?>> T getRenderLayer(RenderLivingBase<?> renderer, Class<T> cls, boolean subclasses) {
		try {
			List<? extends LayerRenderer<?>> layers = renderer.layerRenderers;
			for(LayerRenderer<?> layer : layers) {
				if(subclasses ? cls.isInstance(layer) : cls == layer.getClass()) {
					return (T) layer;
				}
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		return null;
	}

	public static boolean doesRendererHaveLayer(RenderLivingBase<?> renderer, Class<? extends LayerRenderer<?>> cls, boolean subclasses) {
		return getRenderLayer(renderer, cls, subclasses) != null;
	}
	
	/**
	 * Returns the currently bound framebuffer, or -1 if not supported
	 * @return
	 */
	public static int getBoundFramebuffer() {
		if (OpenGlHelper.framebufferSupported) {
            switch (OpenGlHelper.framebufferType) {
                case BASE:
                	return GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
                case ARB:
                	return GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);
                case EXT:
                	return GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
            }
        }
		return -1;
	}
	
	/**
	 * Saves the texture of an FBO to the specified PNG file.
	 * 
	 * @param file
	 * @param fbo
	 */
	public static void saveFboToFile(File file, Framebuffer fbo) {
		int prevFBO = getBoundFramebuffer();

		fbo.bindFramebuffer(false);

		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = fbo.framebufferWidth;
		int height= fbo.framebufferHeight;
		int bpp = 4;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

		String format = "PNG";
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = buffer.get(i + 3) & 0xFF;
				image.setRGB(x, height - (y + 1), (a << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) { 
			e.printStackTrace(); 
		}

		//Bind previous fbo
		OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_RENDERBUFFER, prevFBO);
	}
}
