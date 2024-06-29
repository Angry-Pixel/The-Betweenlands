package thebetweenlands.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import net.minecraft.client.Minecraft;

public class FramebufferStack {
	public static final class State implements AutoCloseable {
		private final int prevFboId;
		private final RenderTarget target;

		private State(int prevFboId, RenderTarget target) {
			this.prevFboId = prevFboId;
			this.target = target;
		}

		@Override
		public void close() {
			this.pop();
		}

		public int getPreviousFboId() {
			return this.prevFboId;
		}

		public RenderTarget getTarget() {
			return this.target;
		}

		public void pop() {
			if(this.prevFboId >= 0) {
				RenderSystem.glBindBuffer(36160, this.prevFboId);
			} else {
				this.target.bindWrite(false);
			}
			GL11.glPopAttrib();
		}
	}

	private static int getBoundFramebuffer() {
		if (RenderUtils.framebufferSupported) {
			return switch (RenderUtils.framebufferType) {
				case BASE -> GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
				case ARB -> GL11.glGetInteger(ARBFramebufferObject.GL_FRAMEBUFFER_BINDING);
				case EXT -> GL11.glGetInteger(EXTFramebufferObject.GL_FRAMEBUFFER_BINDING_EXT);
			};
		}
		return -1;
	}

	public static State push() {
		GL11.glPushAttrib(GL11.GL_VIEWPORT_BIT);
		return new State(getBoundFramebuffer(), Minecraft.getInstance().getMainRenderTarget());
	}
}
