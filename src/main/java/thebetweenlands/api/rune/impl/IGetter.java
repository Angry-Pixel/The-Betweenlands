package thebetweenlands.api.rune.impl;

import java.util.function.Consumer;
import java.util.function.Function;

import thebetweenlands.api.rune.INodeBlueprint.INodeInput;

@FunctionalInterface
public interface IGetter<T> {
	public int index();

	/**
	 * Returns the input value at this port, without doing any type checks
	 * @param input - node input
	 * @return the input value at this port
	 */
	@SuppressWarnings("unchecked")
	public default T get(INodeInput input) {
		return (T) input.get(this.index());
	}

	/**
	 * Returns the input value at this port after doing a type check.
	 * If the object cannot be cast to the specified type <code>null</code> is returned.
	 * @param input node input
	 * @param cls type to cast to
	 * @return the input value at this port, or null if the input value cannot be cast to the specified type
	 */
	@SuppressWarnings("unchecked")
	public default <F> F get(INodeInput input, Class<F> cls) {
		Object obj = this.get(input);
		if(obj == null || cls.isInstance(obj)) {
			return (F) obj;
		}
		return null;
	}

	/**
	 * Returns the input value at this port after doing a type check.
	 * If the object cannot be cast to the specified type <i>defaultValue</i> is returned.
	 * @param input node input
	 * @param cls type to cast to
	 * @param defaultValue default value returned if input value cannot be cast to the specified type
	 * @return the input value at this port, or <i>defaultValue</i> if the input value cannot be cast to the specified type
	 */
	@SuppressWarnings("unchecked")
	public default <F> F get(INodeInput input, Class<F> cls, F defaultValue) {
		Object obj = this.get(input);
		if(obj == null || cls.isInstance(obj)) {
			return (F) obj;
		}
		return defaultValue;
	}

	/**
	 * Calls the consumer with the input value at this port after doing a type check.
	 * If the object cannot be cast to the specified type the consumer will not be called.
	 * @param input node input
	 * @param cls type to cast to
	 * @param runnable consumer to call with the cast input value
	 */
	@SuppressWarnings("unchecked")
	public default <F> void run(INodeInput input, Class<F> cls, Consumer<F> runnable) {
		Object obj = this.get(input);
		if(obj == null || cls.isInstance(obj)) {
			runnable.accept((F) obj);
		}
	}

	/**
	 * Calls the consumer with the input value at this port after doing a type check.
	 * If the object cannot be cast to the specified type the consumer will not be called.
	 * @param input node input
	 * @param cls type to cast to
	 * @param runnable consumer to call with the cast input value
	 */
	@SuppressWarnings("unchecked")
	public default <F, G> G run(INodeInput input, Class<F> cls, Function<F, G> runnable, G defaultValue) {
		Object obj = this.get(input);
		if(obj == null || cls.isInstance(obj)) {
			return runnable.apply((F) obj);
		}
		return defaultValue;
	}
}
