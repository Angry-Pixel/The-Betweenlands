package thebetweenlands.client.renderer.util;

import java.lang.ref.WeakReference;
import java.util.Objects;

import com.mojang.blaze3d.vertex.VertexBuffer;

/**
 * "Fence" class for a {@linkplain VertexBuffer}, to make sure its reference isn't lost and ensure it is properly closed
 */
public class VertexBufferFence {

	/**
	 * Creates a new fence pair to keep track of a vertex buffer.
	 * @param data the data to keep track of
	 * @return a pair containing the strong and weak sides of a fence
	 */
	public static VertexBufferFencePair createVertexFence(VertexBufferData data) {
		Objects.requireNonNull(data);
		Object fence = new Object();
		VertexBufferStrongFence strongFence = new VertexBufferStrongFence(fence, data);
		VertexBufferWeakFence weakFence = new VertexBufferWeakFence(fence, data);
		return new VertexBufferFencePair(strongFence, weakFence);
	}

	/**
	 * <p>Wrapper class to return both the strong and weak side of a fence.</p>
	 * <p>Instances of this class should <strong>never</strong> be kept around longer than necessary, as they will prevent the fence from expiring.</p>
	 */
	public static final class VertexBufferFencePair {
		private final VertexBufferStrongFence strongFence;
		private final VertexBufferWeakFence weakFence;
		
		VertexBufferFencePair(VertexBufferStrongFence strongFence, VertexBufferWeakFence weakFence) {
			this.strongFence = strongFence;
			this.weakFence = weakFence;
		}
		
		public VertexBufferStrongFence strongFence() {
			return this.strongFence;
		}
		
		public VertexBufferWeakFence weakFence() {
			return this.weakFence;
		}
	};
	
	/**
	 * <p>The "strong" side of the fence.</p>
	 * <p>As long as this fence isn't garbage collected, the weak side of the fence won't expire.</p>
	 * <p>Typically, this is the side you'll want to pass off to your RenderType or other ephemeral class.</p>
	 */
	public static final class VertexBufferStrongFence {
		private final Object fence;
		private final VertexBufferData data;
		
		VertexBufferStrongFence(Object fence, VertexBufferData data) {
			this.fence = Objects.requireNonNull(fence);
			this.data = data;
		}
		
		public VertexBufferData getVertexBufferData() {
			WeakReference.reachabilityFence(fence);
			return this.data;
		}
	}
	
	/**
	 * <p>The "weak" side of the fence.</p>
	 * <p>Once the strong side of the fence gets garbage collected, this side becomes "expired".</p>
	 * <p>Typically, you'll want to keep track of this side of the fence in a map or list and close the vertex buffer once it becomes expired.</p>
	 */
	public static final class VertexBufferWeakFence {
		private final WeakReference<Object> fence;
		private final VertexBufferData data;
		
		VertexBufferWeakFence(Object fence, VertexBufferData data) {
			this.fence = new WeakReference<>(Objects.requireNonNull(fence));
			this.data = data;
		}
		
		public VertexBufferData getVertexBufferData() {
			return this.data;
		}
		
		public boolean hasExpired() {
			return this.fence.get() == null;
		}
	}
}
