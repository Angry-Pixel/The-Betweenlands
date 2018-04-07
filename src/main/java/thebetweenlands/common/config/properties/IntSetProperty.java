package thebetweenlands.common.config.properties;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import thebetweenlands.common.config.ConfigProperty;

public class IntSetProperty extends ConfigProperty implements Set<Integer> {
	private IntOpenHashSet set;

	private final Supplier<int[]> ints;

	public IntSetProperty(Supplier<int[]> ints) {
		this.ints = ints;
	}

	@Override
	protected void init() {
		this.set = new IntOpenHashSet();

		int[] ints = this.ints.get();
		for(int i : ints) {
			this.set.add(i);
		}
	}

	/**
	 * Returns whether the specified int is listed
	 * @return
	 */
	public boolean isListed(int i) {
		return this.set.contains(i);
	}

	@Override
	public boolean add(Integer e) {
		return this.set.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Integer> c) {
		return this.set.addAll(c);
	}

	@Override
	public void clear() {
		this.set.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.set.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.set.isEmpty();
	}

	@Override
	public IntIterator iterator() {
		return this.set.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.set.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.set.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.set.retainAll(c);
	}

	@Override
	public int size() {
		return this.set.size();
	}

	@Override
	public Object[] toArray() {
		return this.set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.set.toArray(a);
	}
}
