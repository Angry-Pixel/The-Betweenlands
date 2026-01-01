package thebetweenlands.util;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL30;

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
				RenderSystem.glBindBuffer(GL30.GL_FRAMEBUFFER, this.prevFboId);
			} else {
				this.target.bindWrite(false);
			}
		}
	}

	private static int getBoundFramebuffer() {
		return GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
	}

	public static State push() {
		return new State(getBoundFramebuffer(), Minecraft.getInstance().getMainRenderTarget());
	}
}
