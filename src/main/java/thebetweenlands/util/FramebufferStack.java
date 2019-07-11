package thebetweenlands.util;

import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.shader.Framebuffer;

public class FramebufferStack {
	public static final class State implements AutoCloseable {
		private final int prevFboId;
		private final Framebuffer minecraftFbo;

		private State(int prevFboId, Framebuffer minecraftFbo) {
			this.prevFboId = prevFboId;
			this.minecraftFbo = minecraftFbo;
		}

		@Override
		public void close() {
			this.pop();
		}

		public int getPreviousFboId() {
			return this.prevFboId;
		}

		public Framebuffer getMinecraftFbo() {
			return this.minecraftFbo;
		}

		public void pop() {
			if(this.prevFboId >= 0) {
				OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, this.prevFboId);
			} else {
				this.minecraftFbo.bindFramebuffer(false);
			}
			GL11.glPopAttrib();
		}
	}

	private static int getBoundFramebuffer() {
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

	public static State push() {
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		return new State(getBoundFramebuffer(), Minecraft.getMinecraft().getFramebuffer());
	}
}
