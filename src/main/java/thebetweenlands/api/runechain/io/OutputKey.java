package thebetweenlands.api.runechain.io;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;

import thebetweenlands.api.runechain.base.IConfigurationOutput;
import thebetweenlands.api.runechain.base.IType;

/**
 * An output key that allows setting values of the node output
 */
public class OutputKey<T, R> implements IConfigurationOutput {
	private final Class<T> type;
	private final InputKey<?, ?> passthrough;
	private final boolean nullable;
	private final int index;
	private final boolean collection;
	private final String descriptor;

	private final IType outputType;

	@SuppressWarnings("unchecked")
	private OutputKey(Class<?> type, InputKey<?, ?> passthrough, boolean nullable, int index, String descriptor, boolean collection) {
		this.type = (Class<T>) type;
		this.passthrough = passthrough;
		this.nullable = nullable;
		this.index = index;
		this.collection = collection;
		this.descriptor = descriptor;
		this.outputType = new IType() {
			@Override
			public Class<?> getJavaType() {
				return type;
			}

			@Override
			public List<IType> getTypeGenerics() {
				return Collections.emptyList(); //TODO Add generics to outputs?
			}
		};
	}

	public Class<T> getJavaType() {
		return this.type;
	}

	public int getIndex() {
		return this.index;
	}

	public boolean isNullable() {
		return this.nullable;
	}

	public ISetter<R> setter() {
		return () -> this.index;
	}

	@Override
	public IType getType(List<IType> inputs) {
		if(this.passthrough != null) {
			return inputs.get(this.passthrough.getIndex());
		}
		return this.outputType;
	}

	@Override
	public boolean isEnabled(List<IType> inputs) {
		if(this.passthrough != null) {
			return inputs.get(this.passthrough.getIndex()) != null;
		}
		return true;
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

	public static Builder<Object, Object> create(Supplier<Integer> indexer, Consumer<OutputKey<?, ?>> consumer, String descriptor) {
		return new Builder<Object, Object>(indexer, consumer, descriptor);
	}

	public static class Builder<T, R> {
		private final Supplier<Integer> indexer;
		private final Consumer<OutputKey<?, ?>> consumer;

		private final String descriptor;
		private final Class<?> type;
		private final InputKey<?, ?> passthrough;
		private final boolean collection;

		private boolean nullable;

		private Builder(Supplier<Integer> indexer, Consumer<OutputKey<?, ?>> consumer, String descriptor) {
			this.indexer = indexer;
			this.consumer = consumer;
			this.descriptor = descriptor;
			this.type = Object.class;
			this.passthrough = null;
			this.collection = false;
		}

		private Builder(Builder<? super T,  ?> builder, boolean collection) {
			this.indexer = builder.indexer;
			this.consumer = builder.consumer;
			this.descriptor = builder.descriptor;
			this.type = builder.type;
			this.passthrough = builder.passthrough;
			this.collection = collection;
		}

		private Builder(Builder<? super T, ?> builder, Class<?> type, String descriptor, boolean collection) {
			this.indexer = builder.indexer;
			this.consumer = builder.consumer;
			this.descriptor = descriptor;
			this.type = type;
			this.passthrough = null;
			this.collection = collection;
		}

		private Builder(Builder<? super T, ?> builder, Class<?> type, InputKey<?, ?> passthrough, String descriptor, boolean collection) {
			this.indexer = builder.indexer;
			this.consumer = builder.consumer;
			this.descriptor = descriptor;
			this.type = type;
			this.passthrough = passthrough;
			this.collection = collection;
		}

		public <C extends T> Builder<C, C> type(Class<C> type) {
			return new Builder<C, C>(this, type, this.descriptor, false);
		}

		public <C extends T> Builder<C, C> type(InputKey<C, ?> type) {
			Preconditions.checkArgument(type.getJavaType() != null, "Input key must have exactly one java type");
			return new Builder<>(this, type.getJavaType(), type, type.getDescriptor(), type.isCollection());
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

		public OutputKey<T, R> key() {
			OutputKey<T, R> key = new OutputKey<T, R>(this.type, this.passthrough, this.nullable, this.indexer.get(), this.descriptor, this.collection);
			this.consumer.accept(key);
			return key;
		}

		public ISetter<R> setter() {
			return this.key().setter();
		}
	}
}