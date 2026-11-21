package thebetweenlands.client.renderer.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

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
			return delegate.size();
		}

		@Override
		public boolean isEmpty() {
			return delegate.isEmpty();
		}

		@Override
		public boolean containsKey(Object key) {
			return delegate.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return delegate.containsKey(value);
		}

		@Override
		public V get(Object key) {
			return delegate.get(key);
		}

		@Override
		public V put(K key, V value) {
			return delegate.put(key, value);
		}

		@Override
		public V remove(Object key) {
			return delegate.remove(key);
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			delegate.putAll(m);
		}

		@Override
		public void clear() {
			delegate.clear();
		}

		@Override
		public Set<K> keySet() {
			return delegate.keySet();
		}

		@Override
		public Collection<V> values() {
			return delegate.values();
		}

		@Override
		public Set<Entry<K, V>> entrySet() {
			return delegate.entrySet();
		}

		@Override
		public SequencedMap<K, V> reversed() {
			return delegate.reversed();
		}

		@Override
		public Entry<K, V> firstEntry() {
			return delegate.firstEntry();
		}

		@Override
		public Entry<K, V> lastEntry() {
			return delegate.lastEntry();
		}

		@Override
		public Entry<K, V> pollFirstEntry() {
			return delegate.pollFirstEntry();
		}

		@Override
		public Entry<K, V> pollLastEntry() {
			return delegate.pollLastEntry();
		}

		@Override
		public V putFirst(K k, V v) {
			return delegate.putFirst(k, v);
		}

		@Override
		public V putLast(K k, V v) {
			return delegate.putLast(k, v);
		}

		@Override
		public SequencedSet<K> sequencedKeySet() {
			return delegate.sequencedKeySet();
		}

		@Override
		public SequencedCollection<V> sequencedValues() {
			return delegate.sequencedValues();
		}

		@Override
		public SequencedSet<Entry<K, V>> sequencedEntrySet() {
			return delegate.sequencedEntrySet();
		}

		@Override
		public V getOrDefault(Object key, V defaultValue) {
			return delegate.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(BiConsumer<? super K, ? super V> action) {
			delegate.forEach(action);
		}

		@Override
		public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
			delegate.replaceAll(function);
		}

		@Override
		public V putIfAbsent(K key, V value) {
			return delegate.putIfAbsent(key, value);
		}

		@Override
		public boolean remove(Object key, Object value) {
			return delegate.remove(key, value);
		}

		@Override
		public boolean replace(K key, V oldValue, V newValue) {
			return delegate.replace(key, oldValue, newValue);
		}

		@Override
		public V replace(K key, V value) {
			return delegate.replace(key, value);
		}

		@Override
		public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
			return delegate.computeIfAbsent(key, mappingFunction);
		}

		@Override
		public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			return delegate.computeIfPresent(key, remappingFunction);
		}

		@Override
		public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
			return delegate.compute(key, remappingFunction);
		}

		@Override
		public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			return delegate.merge(key, value, remappingFunction);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(this.delegate);
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof DelegateSequencedMap map) {
				return Objects.equals(this.delegate, map.delegate);
			}
			return false;
		}
	}
}
