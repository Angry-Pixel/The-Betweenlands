package thebetweenlands.client.renderer.util;

import java.util.Objects;
import java.util.SequencedMap;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;

import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.client.renderer.BeamOriginMirrorWorldRenderer;

/**
 * Custom buffer source that can apply stencilling and flipping of cullfaces to rendered areas.
 * <p>Used by {@link BeamOriginMirrorWorldRenderer} to render mirror world entities</p>
 */
public class MirrorWorldBufferSource extends MultiBufferSource.BufferSource {

	/**
	 * Create a new {@link MirrorWorldBufferSource} on the specified buffer
	 * @param sharedBuffer
	 * @return
	 */
	public static MirrorWorldBufferSource immediate(ByteBufferBuilder sharedBuffer) {
		return immediateWithBuffers(Object2ObjectSortedMaps.emptyMap(), sharedBuffer);
	}

	/**
	 * Create a new {@link MirrorWorldBufferSource} on the specified buffer, with the specified fixed buffers
	 * @param sharedBuffer
	 * @return
	 */
	public static MirrorWorldBufferSource immediateWithBuffers(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer) {
		return new MirrorWorldBufferSource(sharedBuffer, fixedBuffers);
	}
	
	private boolean isMirror = false;
	private Stencil stencil = null;
	
	protected MirrorWorldBufferSource(ByteBufferBuilder sharedBuffer, SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers) {
		super(sharedBuffer, fixedBuffers);
	}
	
	/**
	 * Sets the stencil instance associated with this buffer source
	 * @param stencil
	 */
	public void setStencil(Stencil stencil) {
		this.stencil = Objects.requireNonNull(stencil);
	}
	
	/**
	 * Clears the stencil instance associated with this buffer source
	 */
	public void clearStencil() {
		this.stencil = null;
		this.isMirror = false;
	}

	/**
	 * Sets whether this buffer source acts as a mirror.
	 * When mirrored, the cullface is flipped and the stencil is applied (if it exists)
	 * @param isMirror
	 */
	public void setIsMirror(boolean isMirror) {
		this.isMirror = isMirror;
	}

	/**
	 * {@return whether this buffer source currently acts as a mirror}
	 */
	public boolean isMirror() {
		return this.isMirror;
	}
	
	@Override
	public void endBatch(RenderType renderType) {
		BufferBuilder bufferbuilder = this.startedBuilders.remove(renderType);
		if (bufferbuilder != null) {
			this.endBatch(renderType, bufferbuilder);
		}
	}

	/**
	 * Renders a rendertype batch
	 * @param renderType
	 * @param builder
	 */
	private void endBatch(RenderType renderType, BufferBuilder builder) {
		MeshData meshdata = builder.build();
		if (meshdata != null) {
			if (renderType.sortOnUpload()) {
				ByteBufferBuilder bytebufferbuilder = this.fixedBuffers.getOrDefault(renderType, this.sharedBuffer);
				meshdata.sortQuads(bytebufferbuilder, RenderSystem.getVertexSorting());
			}

			renderType.setupRenderState();

			// Get our custom fields
			final boolean isMirror = this.isMirror;
			final Stencil stencil  = this.stencil;
			
			// If we're a mirror and have a stencil
			if (isMirror && stencil != null) {
				// Only draw pixels where our bit is set in the stencil
				stencil.func(GL11.GL_EQUAL, true);
				
				// Never change the stencil
				stencil.op(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
			}
			
			// If we're a mirror, flip the cullface
			if (isMirror) {
				// Flip cullface
				GL11.glCullFace(GL11.GL_FRONT);
			}
			
			BufferUploader.drawWithShader(meshdata);

			// If we were a mirror, reset the cullface
			if (isMirror) {
				// Flip cullface
				GL11.glCullFace(GL11.GL_BACK);
			}
			
			renderType.clearRenderState();
		}

		if (renderType.equals(this.lastSharedType)) {
			this.lastSharedType = null;
		}
	}
}
