package thebetweenlands.api.runechain.io;

import java.util.function.Consumer;
import java.util.function.Function;

import thebetweenlands.api.runechain.base.INodeInput;

@FunctionalInterface
public interface IGetter<T> {
	public int index();

	/**
	 * Gets the input value, without doing any type checks
	 * @param input the node input
	 * @return the input value
	 */
	@SuppressWarnings("unchecked")
	public default T get(INodeInput input) {
		return (T) input.get(this.index());
	}

	/**
	 * Gets the input value after doing a type check.
	 * If the object cannot be cast to the specified type <code>null</code> is returned.
	 * @param input the node input
	 * @param cls the type to cast to
	 * @return the input value, or null if the input value cannot be cast to the specified type
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
	 * Gets the input value after doing a type check.
	 * If the object cannot be cast to the specified type <i>defaultValue</i> is returned.
	 * @param input the node input
	 * @param cls the type to cast to
	 * @param defaultValue the default value returned if input value cannot be cast to the specified type
	 * @return the input value, or <i>defaultValue</i> if the input value cannot be cast to the specified type
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
	 * Calls the consumer with the input value after doing a type check.
	 * If the object cannot be cast to the specified type the consumer will not be called.
	 * @param input the node input
	 * @param cls the type to cast to
	 * @param the consumer to call with the cast input value
	 */
	@SuppressWarnings("unchecked")
	public default <F> void run(INodeInput input, Class<F> cls, Consumer<F> consumer) {
		Object obj = this.get(input);
		if(obj == null || cls.isInstance(obj)) {
			consumer.accept((F) obj);
		}
	}

	/**
	 * Calls the function with the input value after doing a type check.
	 * If the object cannot be cast to the specified type the consumer will not be called.
	 * @param input the node input
	 * @param cls the type to cast to
	 * @param the function to call with the cast input value
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
