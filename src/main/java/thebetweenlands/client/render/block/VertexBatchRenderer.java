package thebetweenlands.client.render.block;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;

public class VertexBatchRenderer {
	private final boolean useVbo;
	private final VertexFormat format;

	//Display lists
	private final WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
	private int displayList;

	//VBOs
	private final VertexBufferUploader vertexBufferUploader = new VertexBufferUploader();
	private VertexBuffer vertexBuffer;

	public VertexBatchRenderer(VertexFormat format, boolean useVbo) {
		this(format, useVbo, null, -1);
	}

	public VertexBatchRenderer(VertexFormat format, boolean useVbo, @Nullable VertexBuffer vertexBuffer, int displayList) {
		this.format = format;
		this.useVbo = useVbo;
		this.vertexBuffer = vertexBuffer;
		this.displayList = displayList;
	}

	public void compile(BufferBuilder bufferBuilder) {
		bufferBuilder.finishDrawing();

		if(this.useVbo) {
			if(this.vertexBuffer == null) {
				this.vertexBuffer = new VertexBuffer(this.format);
			}
			this.vertexBufferUploader.setVertexBuffer(this.vertexBuffer);
			this.vertexBufferUploader.draw(bufferBuilder);
		} else {
			if(this.displayList < 0) {
				this.displayList = GL11.glGenLists(1);
			}
			GL11.glNewList(this.displayList, GL11.GL_COMPILE);
			this.worldVertexUploader.draw(bufferBuilder);
			GL11.glEndList();
		}
	}

	public void render() {
		if(this.useVbo) {
			if(this.vertexBuffer != null) {
				this.vertexBuffer.bindBuffer();

				int stride = this.format.getSize();

				int elementIndex = 0;
				for(VertexFormatElement element : this.format.getElements()) {
					int count = element.getElementCount();
					int constant = element.getType().getGlConstant();
					int offset = this.format.getOffset(elementIndex);

					switch(element.getUsage()) {
					case POSITION:
						GlStateManager.glVertexPointer(count, constant, stride, offset);
						GlStateManager.glEnableClientState(GL11.GL_VERTEX_ARRAY);
						break;
					case NORMAL:
						if(count != 3) {
							throw new IllegalArgumentException("Normal attribute should have the size 3: " + element);
						}
						//GlStateManager.glNormalPointer(constant, stride, offset);
						GL11.glNormalPointer(constant, stride, offset);
						GlStateManager.glEnableClientState(GL11.GL_NORMAL_ARRAY);
						break;
					case COLOR:
						GlStateManager.glColorPointer(count, constant, stride, offset);
						GlStateManager.glEnableClientState(GL11.GL_COLOR_ARRAY);
						break;
					case UV:
						OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + element.getIndex());
						GlStateManager.glTexCoordPointer(count, constant, stride, offset);
						GlStateManager.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
						OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
						break;
					case PADDING:
						break;
					case GENERIC:
						GL20.glEnableVertexAttribArray(element.getIndex());
						GL20.glVertexAttribPointer(element.getIndex(), count, constant, false, stride, offset);
						break;
					}

					elementIndex++;
				}

				this.vertexBuffer.drawArrays(GL11.GL_QUADS);

				this.vertexBuffer.unbindBuffer();

				for(VertexFormatElement element : this.format.getElements()) {
					switch(element.getUsage()) {
					case POSITION:
						GlStateManager.glDisableClientState(GL11.GL_VERTEX_ARRAY);
						break;
					case NORMAL:
						GlStateManager.glDisableClientState(GL11.GL_NORMAL_ARRAY);
						break;
					case COLOR:
						GlStateManager.glDisableClientState(GL11.GL_COLOR_ARRAY);
						GlStateManager.resetColor();
						break;
					case UV:
						OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + element.getIndex());
						GlStateManager.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
						OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
						break;
					case PADDING:
						break;
					case GENERIC:
						GL20.glDisableVertexAttribArray(element.getIndex());
						break;
					}
				}
			}
		} else {
			if(this.displayList >= 0) {
				GL11.glCallList(this.displayList);
			}
		}
	}

	public void deleteBuffers() {
		if(this.vertexBuffer != null) {
			this.vertexBuffer.deleteGlBuffers();
			this.vertexBuffer = null;
		}

		if(this.displayList >= 0) {
			GL11.glDeleteLists(this.displayList, 1);
			this.displayList = -1;
		}
	}
}
