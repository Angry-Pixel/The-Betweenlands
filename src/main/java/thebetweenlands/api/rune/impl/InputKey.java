package thebetweenlands.api.rune.impl;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;

import thebetweenlands.api.rune.INodeConfiguration.IConfigurationInput;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.INodeConfiguration.IType;
import thebetweenlands.api.rune.impl.RuneConfiguration.InputSerializer;

/**
 * An input port that allows retrieving values from the node input
 */
public class InputKey<T, R> implements IConfigurationInput {
	private final Class<T> type;
	private final Class<?>[] types;
	private final int index;
	private final boolean collection;
	private final boolean wildcard;
	private final String descriptor;
	private final boolean nullable;
	private final InputSerializer<?> serializer;

	@SuppressWarnings("unchecked")
	private InputKey(Class<?> type, boolean nullable, int index, boolean wildcard, String descriptor, boolean collection, InputSerializer<?> serializer) {
		this.type = (Class<T>) type;
		this.types = null;
		this.nullable = nullable;
		this.index = index;
		this.collection = collection;
		this.wildcard = wildcard;
		this.descriptor = descriptor;
		this.serializer = serializer;
	}

	private InputKey(Class<?>[] types, boolean nullable, int index, boolean wildcard, String descriptor, boolean collection, InputSerializer<?> serializer) {
		this.type = null;
		this.types = types;
		this.nullable = nullable;
		this.index = index;
		this.collection = collection;
		this.wildcard = wildcard;
		this.descriptor = descriptor;
		this.serializer = serializer;
	}

	private InputKey(Class<T> type, boolean nullable, int index, InputKey<? extends T, ?> in) {
		this.type = type;
		this.types = in.types;
		this.nullable = nullable;
		this.index = index;
		this.collection = in.collection;
		this.wildcard = in.wildcard;
		this.descriptor = in.descriptor;
		this.serializer = in.serializer;
	}

	private InputKey(Class<T> type, boolean nullable, int index, OutputKey<? extends T, ?> in) {
		this.type = type;
		this.types = null;
		this.nullable = nullable;
		this.index = index;
		this.collection = in.isCollection();
		this.wildcard = false;
		this.descriptor = in.getDescriptor();
		this.serializer = null;
	}

	@Nullable
	public Class<T> getJavaType() {
		return this.type;
	}

	@Nullable
	public Class<?>[] getJavaTypes() {
		return this.types;
	}

	public boolean isWildcard() {
		return this.wildcard;
	}

	public int getIndex() {
		return this.index;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public InputSerializer<?> getSerializer() {
		return this.serializer;
	}

	public IGetter<R> getter() {
		return () -> this.index;
	}

	@Override
	public boolean isType(IConfigurationOutput output, IType type) {
		if(this.type != null) {
			if(this.type.isAssignableFrom(type.getJavaType()) && (this.isWildcard() || output.getDescriptor().equals(this.getDescriptor()))) {
				//TODO Check generics?
				return true;
			}
		} else {
			if(this.isWildcard() || output.getDescriptor().equals(this.getDescriptor())) {
				for(Class<?> inputType : this.types) {
					if(inputType.isAssignableFrom(type.getJavaType())) {
						//TODO Check generics?
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isCollection() {
		return this.collection;
	}

	@Override
	public String getDescriptor() {
		return this.descriptor;
	}

	@Override
	public boolean validate(Object value) {
		return this.nullable || value != null;
	}

	public static Builder<Object, Object> create(Supplier<Integer> indexer, Consumer<InputKey<?, ?>> consumer, Predicate<String> wildcardPredicate, String descriptor) {
		return new Builder<Object, Object>(indexer, consumer, wildcardPredicate, descriptor);
	}
	
	public static class Builder<T, R> {
		private final Supplier<Integer> indexer;
		private final Consumer<InputKey<?, ?>> consumer;
		private final Predicate<String> wildcardPredicate;

		private final String descriptor;
		private final Class<?>[] types;
		private final boolean collection;

		private InputSerializer<? super T> serializer;
		private boolean nullable;

		private Builder(Supplier<Integer> indexer, Consumer<InputKey<?, ?>> consumer, Predicate<String> wildcardPredicate, String descriptor) {
			this.indexer = indexer;
			this.consumer = consumer;
			this.wildcardPredicate = wildcardPredicate;
			this.descriptor = descriptor;
			this.types = new Class[] { Object.class };
			this.collection = false;
		}

		private Builder(Builder<? super T,  ?> builder, boolean collection) {
			this.indexer = builder.indexer;
			this.consumer = builder.consumer;
			this.wildcardPredicate = builder.wildcardPredicate;
			this.descriptor = builder.descriptor;
			this.types = builder.types;
			this.collection = collection;
			this.serializer = builder.serializer;
			this.nullable = builder.nullable;
		}

		private Builder(Builder<? super T,  ?> builder, Class<?>[] types, String descriptor, Predicate<String> wildcardPredicate, boolean collection, boolean nullable) {
			this.indexer = builder.indexer;
			this.consumer = builder.consumer;
			this.wildcardPredicate = builder.wildcardPredicate != wildcardPredicate ? builder.wildcardPredicate.or(wildcardPredicate) : builder.wildcardPredicate;
			this.descriptor = descriptor;
			this.types = types;
			this.collection = collection;
			this.serializer = builder.serializer;
			this.nullable = nullable;
		}

		public <C extends T> Builder<C, C> type(Class<C> type) {
			return new Builder<>(this, new Class[] { type }, this.descriptor, this.wildcardPredicate, false, this.nullable);
		}

		@SuppressWarnings("unchecked")
		public <C extends T> Builder<?, ?> type(Class<C> type, Class<?>... types) {
			return new Builder<>(this, ArrayUtils.addAll(types, type), this.descriptor, this.wildcardPredicate, false, this.nullable);
		}

		public <C extends T> Builder<C, C> type(InputKey<C, ?> type) {
			return new Builder<>(this, type.types != null ? type.types : new Class[] { type.type }, type.descriptor, d -> type.wildcard, type.collection, type.nullable);
		}

		public <C extends T> Builder<C, C> type(OutputKey<C, ?> type) {
			return new Builder<>(this, new Class[] { type.getJavaType() }, type.getDescriptor(), d -> false, type.isCollection(), type.isNullable());
		}

		public Builder<T, R> serializer(InputSerializer<? super T> serializer) {
			this.serializer = serializer;
			return this;
		}

		public Builder<T, R> nullable() {
			this.nullable = true;
			return this;
		}

		public Builder<T, Collection<T>> collection() {
			return new Builder<T, Collection<T>>(this, true);
		}

		public void register() {
			this.key();
		}

		public InputKey<T, R> key() {
			InputKey<T, R> key;
			if(this.types.length > 1) {
				key = new InputKey<T, R>(this.types, this.nullable, this.indexer.get(), this.wildcardPredicate.test(this.descriptor), this.descriptor, this.collection, this.serializer);
			} else {
				key = new InputKey<T, R>(this.types[0], this.nullable, this.indexer.get(), this.wildcardPredicate.test(this.descriptor), this.descriptor, this.collection, this.serializer);
			}
			this.consumer.accept(key);
			return key;
		}

		public IGetter<R> getter() {
			return this.key().getter();
		}
	}
}