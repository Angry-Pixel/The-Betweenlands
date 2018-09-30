package thebetweenlands.common.config.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

import thebetweenlands.common.config.ConfigProperty;

public class StringSetProperty extends ConfigProperty implements Set<String> {
	private Set<String> set;

	private final Supplier<String[]> strings;

	public StringSetProperty(Supplier<String[]> ints) {
		this.strings = ints;
	}

	@Override
	protected void init() {
		this.set = new HashSet<>();

		String[] ints = this.strings.get();
		for(String s : ints) {
			this.set.add(s);
		}
	}

	/**
	 * Returns whether the specified string is listed
	 * @return
	 */
	public boolean isListed(String s) {
		return this.set.contains(s);
	}

	@Override
	public boolean add(String e) {
		return this.set.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends String> c) {
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
	public Iterator<String> iterator() {
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
