package thebetweenlands.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import javax.annotation.Nullable;

public class CustomCullingObject2IntHashMap<K> implements Map<K, Integer>, Serializable {
	private static final long serialVersionUID = -8588233762092841277L;

	private final Object2IntOpenHashMap<K> internalMap;
	private final int cullValue;
	private final Integer boxedCullValue;

	public CustomCullingObject2IntHashMap(int expected, int value) {
		internalMap = new Object2IntOpenHashMap<>(expected);
		internalMap.defaultReturnValue(value);
		cullValue = value;
		boxedCullValue = value;
	}

	public CustomCullingObject2IntHashMap(int value) {
		this(Hash.DEFAULT_INITIAL_SIZE, value);
	}

	public CustomCullingObject2IntHashMap() {
		this(0);
	}


	@Override
	public int size() {
		return internalMap.size();
	}

	@Override
	public boolean isEmpty() {
		return internalMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return internalMap.containsKey(key);
	}

	@Override
	@Deprecated
	public boolean containsValue(Object value) {
		return internalMap.containsValue(value);
	}

	public boolean containsValue(int value) {
		return internalMap.containsValue(value);
	}

	@Override
	@Deprecated
	public Integer get(Object key) {
		if(!internalMap.containsKey(key)) return boxedCullValue;
		return internalMap.get(key);
	}

	public int getInt(Object key) {
		return internalMap.getInt(key);
	}

	@Nullable
	@Override
	@Deprecated
	public Integer put(K key, Integer value) {
		final boolean containsKey = containsKey(key);
		if(value.equals(boxedCullValue)) {
			return containsKey ? internalMap.remove(key) : boxedCullValue;
		}
		final Integer out = internalMap.put(key, value);
		return containsKey ? out : boxedCullValue;
	}

	public int put(K key, int value) {
		return value == cullValue ? internalMap.removeInt(key) : internalMap.put(key, value);
	}

	/**
	 * Adds an increment to value currently associated with a key and returns the old value.
	 *
	 * @see Object2IntOpenHashMap#addTo
	 * @param key the key.
	 * @param increment the increment.
	 * @return the old value.
	 */
	public int addTo(K key, int increment) {
		final int value = internalMap.getInt(key);
		final int newValue = value + increment;
		if(newValue == cullValue) {
			internalMap.removeInt(key);
		} else {
			internalMap.put(key, newValue);
		}
		return value;
	}

	/**
	 * Increments the value of the key by 1 and returns the new value
	 * @param key the key.
	 * @return the new value.
	 */
	public int increment(K key) {
		final int value = internalMap.getInt(key) + 1;
		if(value == cullValue) {
			internalMap.removeInt(key);
		} else {
			internalMap.put(key, value);
		}
		return value;
	}

	/**
	 * Decreases the value of the key by 1 and returns the new value
	 * @param key the key.
	 * @return the new value.
	 */
	public int decrement(K key) {
		final int value = internalMap.getInt(key) - 1;
		if(value == cullValue) {
			internalMap.removeInt(key);
		} else {
			internalMap.put(key, value);
		}
		return value;
	}

	@Override
	@Deprecated
	public Integer remove(Object key) {
		return containsKey(key) ? internalMap.remove(key) : boxedCullValue;
	}

	public Integer removeInt(Object key) {
		return internalMap.removeInt(key);
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public void putAll(Map<? extends K, ? extends Integer> m) {
		final Object2IntOpenHashMap<K> internalMap = this.internalMap;

		if (m instanceof Object2IntMap) {
			final int cullValue = this.cullValue;
			final ObjectIterator<Object2IntMap.Entry<K>> i = Object2IntMaps.fastIterator((Object2IntMap<K>)m);
			while (i.hasNext()) {
				final Object2IntMap.Entry<? extends K> e = i.next();
				final int value = e.getIntValue();
				final K key = e.getKey();
				if(value != cullValue)
					internalMap.put(key, value);
				else
					internalMap.removeInt(key);
			}
		} else {
			final int boxedCullValue = this.boxedCullValue;
			int n = m.size();
			final Iterator<? extends Map.Entry<? extends K, ? extends Integer>> i = m.entrySet().iterator();
			Map.Entry<? extends K, ? extends Integer> e;
			while (n-- != 0) {
				e = i.next();
				final Integer value = e.getValue();
				final K key = e.getKey();
				if(value != boxedCullValue)
					internalMap.put(key, value);
				else
					internalMap.removeInt(key);
			}
		}
	}

	@Override
	public void clear() {
		internalMap.clear();
	}

	@Override
	public ObjectSet<K> keySet() {
		return internalMap.keySet();
	}

	@Override
	public IntCollection values() {
		return internalMap.values();
	}

	@Override
	@Deprecated
	public Set<Entry<K, Integer>> entrySet() {
		return internalMap.entrySet();
	}

	public Object2IntMap.FastEntrySet<K> object2IntEntrySet() {
		return internalMap.object2IntEntrySet();
	}

}
