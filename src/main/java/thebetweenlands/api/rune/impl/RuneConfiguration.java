package thebetweenlands.api.rune.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.rune.INodeBlueprint.INodeInput;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneChainUser;

/**
 * This port based node configuration allows for easy creation of configurations
 * by using input and output ports that accepts specific types.
 */
public class RuneConfiguration implements INodeConfiguration {
	public static interface InputSerializer<T> {
		public void write(T obj, PacketBuffer buffer);

		public T read(IRuneChainUser user, PacketBuffer buffer) throws IOException;
	}

	public static final class Builder {
		private int inIndices = 0;
		private int outIndices = 0;

		private int id = 0;

		private final List<InputKey<?, ?>> inputPorts = new ArrayList<>();
		private final List<OutputKey<?, ?>> outputPorts = new ArrayList<>();

		@Nullable
		private final String descriptorWildcard;

		private Builder(@Nullable ResourceLocation descriptorWildcard) {
			this.descriptorWildcard = descriptorWildcard != null ? this.getDescriptorString(descriptorWildcard) : null;
		}

		private String getDescriptorString(ResourceLocation descriptor) {
			return String.format("%s.%s", descriptor.getNamespace(), descriptor.getPath());
		}

		public InputKey.Builder<Object, Object> in(ResourceLocation descriptor) {
			return this.in(this.getDescriptorString(descriptor));
		}

		public InputKey.Builder<Object, Object> in(String descriptor) {
			return InputKey.create(() -> this.inIndices++, in -> this.inputPorts.add(in), d -> this.descriptorWildcard != null && this.descriptorWildcard.equals(d), descriptor);
		}

		public OutputKey.Builder<Object, Object> out(ResourceLocation descriptor) {
			return this.out(this.getDescriptorString(descriptor));
		}

		public OutputKey.Builder<Object, Object> out(String descriptor) {
			return OutputKey.create(() -> this.outIndices++, out -> this.outputPorts.add(out), descriptor);
		}

		/**
		 * Creates the new configuration
		 * @return the new configuration
		 */
		public RuneConfiguration build() {
			ImmutableList.Builder<InputKey<?, ?>> inputTypes = ImmutableList.builder();
			ImmutableList.Builder<IConfigurationOutput> outputTypes = ImmutableList.builder();

			inputTypes.addAll(this.inputPorts);
			outputTypes.addAll(this.outputPorts);

			this.inIndices = 0;
			this.outIndices = 0;
			this.inputPorts.clear();
			this.outputPorts.clear();

			return new RuneConfiguration(inputTypes.build(), outputTypes.build(), this.id++);
		}
	}

	private final List<InputKey<?, ?>> inputTypes;
	private final List<IConfigurationOutput> outputTypes;
	private final int id;

	private RuneConfiguration(List<InputKey<?, ?>> inputTypes, List<IConfigurationOutput> outputTypes, int id) {
		this.inputTypes = inputTypes;
		this.outputTypes = outputTypes;
		this.id = id;
	}

	public static Builder create() {
		return new Builder(null);
	}

	/**
	 * Creates a port node configuration builder with the specified descriptor as input type descriptor wildcard. 
	 * When an input of this builder specifies this wildcard as type descriptor any output
	 * will be accepted regardless of the output's type descriptor as long as the input's
	 * and output's Java types are compatible.
	 * @param descriptorWildcard Descriptor wildcard
	 * @return New port node configuration builder
	 */
	public static Builder create(ResourceLocation descriptorWildcard) {
		return new Builder(descriptorWildcard);
	}

	@Override
	public List<InputKey<?, ?>> getInputs() {
		return this.inputTypes;
	}

	@Override
	public List<IConfigurationOutput> getOutputs() {
		return this.outputTypes;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@SuppressWarnings("unchecked")
	public void serialize(IRuneChainUser user, INodeInput input, PacketBuffer buffer) {
		for(InputKey<?, ?> inputPort : this.inputTypes) {
			InputSerializer<?> serializer = inputPort.getSerializer();

			if(serializer != null) {
				Object value = inputPort.getter().get(input);

				buffer.writeBoolean(value != null);

				if(value != null) {
					if(inputPort.isCollection() && value instanceof Collection) {
						buffer.writeBoolean(true);

						Collection<?> collection = (Collection<?>) value;

						buffer.writeVarInt(collection.size());

						for(Object element : collection) {
							((InputSerializer<Object>) serializer).write(element, buffer);
						}
					} else {
						buffer.writeBoolean(false);

						((InputSerializer<Object>) serializer).write(value, buffer);
					}
				}
			}
		}
	}

	public List<Object> deserialize(IRuneChainUser user, PacketBuffer buffer) {
		List<Object> values = new ArrayList<>(this.inputTypes.size());

		for(InputKey<?, ?> inputPort : this.inputTypes) {
			InputSerializer<?> serializer = inputPort.getSerializer();

			if(serializer != null) {
				if(buffer.readBoolean()) {
					if(buffer.readBoolean()) {
						try {
							int size = buffer.readVarInt();

							Collection<Object> collection = new ArrayList<>(size);

							for(int i = 0; i < size; i++) {
								collection.add(serializer.read(user, buffer));
							}

							values.add(collection);
						} catch (IOException e) {
							e.printStackTrace();

							values.add(null);
						}
					} else {
						try {
							values.add(serializer.read(user, buffer));
						} catch (IOException e) {
							e.printStackTrace();

							values.add(null);
						}
					}
				} else {
					values.add(null);
				}
			}
		}

		return values;
	}
}
