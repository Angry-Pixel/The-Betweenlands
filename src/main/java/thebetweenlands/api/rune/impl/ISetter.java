package thebetweenlands.api.rune.impl;

import thebetweenlands.api.rune.INodeBlueprint.INodeIO;

@FunctionalInterface
public interface ISetter<T> {
	public int index();

	/**
	 * Sets the output value at this port
	 * @param io - node I/O
	 * @param obj - value to output
	 */
	public default void set(INodeIO io, T obj) {
		io.set(this.index(), obj);
	}
}
