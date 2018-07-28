package thebetweenlands.api.rune.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeBlueprint.INodeIO;

/**
 * This port based node configuration allows for easy creation of configurations
 * by using input and output ports that accepts specific types.
 */
public class PortNodeConfiguration implements INodeConfiguration {
	public static final class Builder {
		private int inIndices = 0;
		private int outIndices = 0;

		private final List<InputPort<?>> inputPorts = new ArrayList<>();
		private final List<OutputPort<?>> outputPorts = new ArrayList<>();

		public Builder() {}

		/**
		 * Creates a new input that accepts the specified type
		 * @param type - type to accept
		 * @return a new input that accepts the specified type
		 */
		public <T> InputPort<T> in(Class<T> type) {
			InputPort<T> input = new InputPort<T>(type, this.inIndices++, false);
			this.inputPorts.add(input);
			return input;
		}

		/**
		 * Creates a new input that accepts a multiple objects of the specified type at once
		 * @param type - type to accept
		 * @return a new input that accepts a multiple objects of the specified type at once
		 */
		public <T> InputPort<Collection<T>> multiIn(Class<T> type) {
			InputPort<Collection<T>> input = new InputPort<Collection<T>>(type, this.inIndices++, true);
			this.inputPorts.add(input);
			return input;
		}

		/**
		 * Creates a new output that produces the specified type
		 * @param type - type to produce
		 * @return a new output that produces the specified type
		 */
		public <T> OutputPort<T> out(Class<T> type) {
			OutputPort<T> output = new OutputPort<>(type, this.outIndices++);
			this.outputPorts.add(output);
			return output;
		}

		/**
		 * Creates a new output that produces the same type as the specified input
		 * @param type - upper bound of the type to produce
		 * @param in - the input whose type should be used as output type
		 * @return a new output that produces the same type as the specifeid input
		 */
		public <T> OutputPort<T> out(Class<T> type, InputPort<? extends T> in) {
			PassthroughOutputPort<T> output = new PassthroughOutputPort<>(type, this.outIndices++, in);
			this.outputPorts.add(output);
			return output;
		}

		/**
		 * Creates a new output that produces multiple objects of the specified type at once
		 * @param type - type to produce
		 * @return a new output that produces multiple objects of the specified type at once
		 */
		public <T> OutputPort<Collection<T>> multiOut(Class<T> type) {
			OutputPort<Collection<T>> output = new OutputPort<>(type, this.outIndices++, true);
			this.outputPorts.add(output);
			return output;
		}

		/**
		 * Creates a new output that produces multiple objects of the same type as the specified input at once
		 * @param type - upper bound of the type to produce
		 * @param in - the input whose type should be used as output type
		 * @return a new output that produces multiple objects of the same type as the specified input at once
		 */
		public <T, A extends T> OutputPort<Collection<T>> multiOut(Class<T> type, InputPort<Collection<A>> in) {
			PassthroughOutputPort<Collection<T>> output = new PassthroughOutputPort<>(type, this.outIndices++, in, true);
			this.outputPorts.add(output);
			return output;
		}

		/**
		 * Creates the new configuration
		 * @return the new configuration
		 */
		public PortNodeConfiguration build() {
			ImmutableList.Builder<IConfigurationInput> inputTypes = ImmutableList.builder();
			ImmutableList.Builder<IConfigurationOutput> outputTypes = ImmutableList.builder();

			for(InputPort<?> input : this.inputPorts) {
				inputTypes.add(new IConfigurationInput() {
					@Override
					public boolean test(IType type) {
						if(input.type.isAssignableFrom(type.getTypeClass())) {
							//TODO Check generics?
							return true;
						}
						return false;
					}

					@Override
					public boolean isCollection() {
						return input.isMulti;
					}
				});
			}

			for(OutputPort<?> output : this.outputPorts) {
				final IType type = new IType() {
					@Override
					public Class<?> getTypeClass() {
						return output.type;
					}

					@Override
					public List<IType> getTypeGenerics() {
						return Collections.emptyList(); //TODO Add generics to output ports?
					}
				};
				outputTypes.add(new IConfigurationOutput() {
					@Override
					public IType getType(List<IType> inputs) {
						if(output instanceof PassthroughOutputPort) {
							PassthroughOutputPort<?> passthrough = (PassthroughOutputPort<?>) output;
							return inputs.get(passthrough.input.index);
						}
						return type;
					}

					@Override
					public boolean isEnabled(List<IType> inputs) {
						if(output instanceof PassthroughOutputPort) {
							PassthroughOutputPort<?> passthrough = (PassthroughOutputPort<?>) output;
							return inputs.get(passthrough.input.index) != null;
						}
						return true;
					}

					@Override
					public boolean isCollection() {
						return output.isMulti;
					}
				});
			}

			return new PortNodeConfiguration(inputTypes.build(), outputTypes.build());
		}
	}

	/**
	 * An input port that allows retrieving values from the node input
	 */
	public static class InputPort<T> {
		private final Class<T> type;
		private final int index;
		private final boolean isMulti;

		private InputPort(Class<T> type, int index) {
			this.type = type;
			this.index = index;
			this.isMulti = false;
		}

		@SuppressWarnings("unchecked")
		private InputPort(Class<?> type, int index, boolean isMulti) {
			this.type = (Class<T>) type;
			this.index = index;
			this.isMulti = isMulti;
		}

		/**
		 * Returns the input value at this port
		 * @param input - node I/O
		 * @return the input value at this port
		 */
		@SuppressWarnings("unchecked")
		public T get(INodeIO io) {
			// TODO Type check
			return (T) io.get(this.index);
		}
	}

	/**
	 * An output port that allows setting values of the node output
	 */
	public static class OutputPort<T> {
		private final Class<T> type;
		private final int index;
		private final boolean isMulti;

		private OutputPort(Class<T> type, int index) {
			this.type = type;
			this.index = index;
			this.isMulti = false;
		}

		@SuppressWarnings("unchecked")
		private OutputPort(Class<?> type, int index, boolean isMulti) {
			this.type = (Class<T>) type;
			this.index = index;
			this.isMulti = isMulti;
		}

		/**
		 * Sets the output value at this port
		 * @param io - node I/O
		 * @param obj - value to output
		 */
		public void set(INodeIO io, T obj) {
			io.set(this.index, obj);
		}
	}

	private static class PassthroughOutputPort<T> extends OutputPort<T> {
		private final InputPort<?> input;

		private PassthroughOutputPort(Class<T> type, int index, InputPort<?> input) {
			super(type, index);
			this.input = input;
		}

		private PassthroughOutputPort(Class<?> type, int index, InputPort<?> input, boolean isMulti) {
			super(type, index, isMulti);
			this.input = input;
		}
	}

	private final List<IConfigurationInput> inputTypes;
	private final List<IConfigurationOutput> outputTypes;

	private PortNodeConfiguration(List<IConfigurationInput> inputTypes, List<IConfigurationOutput> outputTypes) {
		this.inputTypes = inputTypes;
		this.outputTypes = outputTypes;
	}

	public static Builder builder() {
		return new Builder();
	}

	@Override
	public List<IConfigurationInput> getInputs() {
		return this.inputTypes;
	}

	@Override
	public List<IConfigurationOutput> getOutputs() {
		return this.outputTypes;
	}
}
