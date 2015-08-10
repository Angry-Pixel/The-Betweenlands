package thebetweenlands.client.render.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.Project;

public class GeometryBuffer {
	private Framebuffer geometryBuffer;
	private Framebuffer geometryDepthBuffer;
	private boolean depthBuffer = false;

	public GeometryBuffer(boolean depthBuffer) {
		this.depthBuffer = depthBuffer;
	}

	public boolean update(Framebuffer input) {
		boolean changed = false;

		//Init geometry buffer
		if(this.geometryBuffer == null) {
			this.geometryBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, this.depthBuffer);
			changed = true;
		}
		if(input.framebufferWidth != this.geometryBuffer.framebufferWidth
				|| input.framebufferHeight != this.geometryBuffer.framebufferHeight) {
			this.geometryBuffer.deleteFramebuffer();
			this.geometryBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, this.depthBuffer);
			changed = true;
		}

		if(this.depthBuffer) {
			//Init geometry depth buffer
			if(this.geometryDepthBuffer == null) {
				this.geometryDepthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
				changed = true;
			}
			if(input.framebufferWidth != this.geometryDepthBuffer.framebufferWidth
					|| input.framebufferHeight != this.geometryDepthBuffer.framebufferHeight) {
				this.geometryDepthBuffer.deleteFramebuffer();
				this.geometryDepthBuffer = new Framebuffer(input.framebufferWidth, input.framebufferHeight, false);
				changed = true;
			}

			//Update depth buffer
			this.geometryBuffer.bindFramebuffer(false);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.geometryDepthBuffer.framebufferTexture);
			GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, 0, 0, 
					this.geometryDepthBuffer.framebufferTextureWidth, 
					this.geometryDepthBuffer.framebufferTextureHeight, 
					0);
		}

		return changed;
	}

	public Framebuffer getGeometryBuffer() {
		return this.geometryBuffer;
	}

	public Framebuffer getGeometryDepthBuffer() {
		return this.geometryDepthBuffer;
	}

	public void bind() {
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindFramebuffer(false);
		}
	}

	public void clear(float r, float g, float b, float a) {
		if(this.geometryBuffer != null) {
			this.geometryBuffer.bindFramebuffer(false);
			GL11.glClearColor(r, g, b, a);
			GL11.glClearDepth(1.0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT);
		}
	}
	
	public void deleteBuffers() {
		if(this.geometryBuffer != null) this.geometryBuffer.deleteFramebuffer();
		if(this.geometryDepthBuffer != null) this.geometryDepthBuffer.deleteFramebuffer();
	}
}
