package thebetweenlands.client.renderer.util;

import java.util.Collection;
import java.util.Map;
import java.util.SequencedMap;
import java.util.Set;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectSortedMaps;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import thebetweenlands.client.renderer.BeamOriginMirrorWorldRenderer;

/**
 * Custom buffer source that can apply stencilling and flipping of cullfaces to rendered areas.
 * <p>Used by {@link BeamOriginMirrorWorldRenderer} to render mirror world entities</p>
 * <p>Additionally, can "steal" fixed buffers from another buffer source.</p>
 */
public class StealingMirrorWorldBufferSource extends MirrorWorldBufferSource {

	/**
	 * Create a new {@link StealingMirrorWorldBufferSource} on the specified buffer
	 * @param sharedBuffer
	 * @return
	 */
	public static StealingMirrorWorldBufferSource immediate(ByteBufferBuilder sharedBuffer) {
		return new StealingMirrorWorldBufferSource(sharedBuffer);
	}
	
	private final DelegateSequencedMap<RenderType, ByteBufferBuilder> stolenFixedBuffers;
	
	protected StealingMirrorWorldBufferSource(ByteBufferBuilder sharedBuffer) {
		this(sharedBuffer, new DelegateSequencedMap<>(), null);
	}

	private StealingMirrorWorldBufferSource(ByteBufferBuilder sharedBuffer, DelegateSequencedMap<RenderType, ByteBufferBuilder> stolenFixedBuffers, Void signature) {
		super(sharedBuffer, stolenFixedBuffers);
		this.stolenFixedBuffers = stolenFixedBuffers;
	}
	
	/**
	 * "Steals" the fixed buffers from a {@link MultiBufferSource.BufferSource}
	 * @param bufferSource
	 */
	public void stealFixedBuffers(MultiBufferSource.BufferSource bufferSource) {
		this.stolenFixedBuffers.delegate = bufferSource.fixedBuffers;
	}
	
	/**
	 * Clears the fixed buffers
	 */
	public void clearFixedBuffers() {
		this.stolenFixedBuffers.delegate = Object2ObjectSortedMaps.emptyMap();
	}
	
	private static class DelegateSequencedMap<K, V> implements SequencedMap<K, V> {

		private SequencedMap<K, V> delegate = Object2ObjectSortedMaps.emptyMap();
		
		@Override
		public int size() {
			return this.delegate.size();
		}

		@Override
		public boolean isEmpty() {
			return this.delegate.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return this.delegate.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return this.delegate.containsKey(value);
		}

		@Override
		public V get(Object key) {
			return this.delegate.get(key);
		}

		@Override
		public V put(K key, V value) {
			return this.delegate.put(key, value);
		}

		@Override
		public V remove(Object key) {
			return this.delegate.remove(key);
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			this.delegate.putAll(m);
		}

		@Override
		public void clear() {
			this.delegate.clear();
		}

		@Override
		public Set<K> keySet() {
			return this.delegate.keySet();
		}

		@Override
		public Collection<V> values() {
			return this.delegate.values();
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return this.delegate.entrySet();
		}

		@Override
		public SequencedMap<K, V> reversed() {
			return this.delegate.reversed();
		}
	}
}
