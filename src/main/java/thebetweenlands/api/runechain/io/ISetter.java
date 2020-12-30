package thebetweenlands.api.runechain.io;

import thebetweenlands.api.runechain.base.INodeIO;

@FunctionalInterface
public interface ISetter<T> {
	public int index();

	/**
	 * Sets the output value
	 * @param io the node input/output
	 * @param obj the output value
	 */
	public default void set(INodeIO io, T obj) {
		io.set(this.index(), obj);
	}
}
