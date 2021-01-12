package thebetweenlands.api.runechain.rune;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.IRuneChainUser;
import thebetweenlands.api.runechain.base.INodeConfiguration;
import thebetweenlands.api.runechain.base.INodeInput;
import thebetweenlands.api.runechain.io.IInputSerializer;
import thebetweenlands.api.runechain.io.InputKey;
import thebetweenlands.api.runechain.io.OutputKey;

/**
 * This key based rune configuration allows for easy creation of configurations
 * by using input and output keys that accept specific types.
 */
public class RuneConfiguration implements INodeConfiguration {
	public static final class Builder {
		private int configurationId = 0;

		private int inputId = 0;
		private int outputId = 0;

		private final List<InputKey<?, ?>> inputs = new ArrayList<>();
		private final List<OutputKey<?, ?>> outputs = new ArrayList<>();

		@Nullable
		private final String descriptorWildcard;

		private Builder(@Nullable ResourceLocation descriptorWildcard) {
			this.descriptorWildcard = descriptorWildcard != null ? this.getDescriptorString(descriptorWildcard) : null;
		}

		private String getDescriptorString(ResourceLocation descriptor) {
			return String.format("%s.%s", descriptor.getNamespace(), descriptor.getPath());
		}

		/**
		 * See {@link #in(String)}
		 * @param descriptor input type descriptor. Inputs and outputs can only be connected if their descriptors match
		 * @return the builder of the new input key
		 */
		public InputKey.Builder<Object, Object> in(ResourceLocation descriptor) {
			return this.in(this.getDescriptorString(descriptor));
		}

		/**
		 * Adds a new input key to the configuration
		 * @param descriptor input type descriptor. Inputs and outputs can only be connected if their descriptors match
		 * @return the builder of the new input key
		 */
		public InputKey.Builder<Object, Object> in(String descriptor) {
			return InputKey.create(() -> this.inputId++, in -> this.inputs.add(in), d -> this.descriptorWildcard != null && this.descriptorWildcard.equals(d), descriptor);
		}

		/**
		 * See {@link #out(String)}
		 * @param descriptor output type descriptor. Inputs and outputs can only be connected if their descriptors match
		 * @return the builder of the new output key
		 */
		public OutputKey.Builder<Object, Object> out(ResourceLocation descriptor) {
			return this.out(this.getDescriptorString(descriptor));
		}

		/**
		 * Adds a new output key to the configuration
		 * @param descriptor output type descriptor. Inputs and outputs can only be connected if their descriptors match
		 * @return the builder of the new output key
		 */
		public OutputKey.Builder<Object, Object> out(String descriptor) {
			return OutputKey.create(() -> this.outputId++, out -> this.outputs.add(out), descriptor);
		}

		/**
		 * Creates the new configuration
		 * @return the new configuration
		 */
		public RuneConfiguration build() {
			ImmutableList.Builder<InputKey<?, ?>> configurationInputs = ImmutableList.builder();
			ImmutableList.Builder<OutputKey<?, ?>> configurationOutputs = ImmutableList.builder();

			configurationInputs.addAll(this.inputs);
			configurationOutputs.addAll(this.outputs);

			this.inputId = 0;
			this.outputId = 0;
			this.inputs.clear();
			this.outputs.clear();

			return new RuneConfiguration(configurationInputs.build(), configurationOutputs.build(), this.configurationId++);
		}
	}

	private final List<InputKey<?, ?>> inputs;
	private final List<OutputKey<?, ?>> outputs;
	private final int id;

	private RuneConfiguration(List<InputKey<?, ?>> inputTypes, List<OutputKey<?, ?>> outputTypes, int id) {
		this.inputs = inputTypes;
		this.outputs = outputTypes;
		this.id = id;
	}

	/**
	 * Creates a rune configuration builder without an input type descriptor wildcard. See {@link #create(ResourceLocation)}.
	 * @return
	 */
	public static Builder create() {
		return new Builder(null);
	}

	/**
	 * Creates a rune configuration builder with the specified descriptor as input type descriptor wildcard. 
	 * When an input of this builder specifies this wildcard as type descriptor any output
	 * will be accepted regardless of the output's type descriptor as long as the input's
	 * and output's Java types are compatible.
	 * @param descriptorWildcard descriptor wildcard, can be null if no wildcard is to be used
	 * @return new rune configuration builder
	 */
	public static Builder create(@Nullable ResourceLocation descriptorWildcard) {
		return new Builder(descriptorWildcard);
	}

	@Override
	public List<InputKey<?, ?>> getInputs() {
		return this.inputs;
	}

	@Override
	public List<OutputKey<?, ?>> getOutputs() {
		return this.outputs;
	}

	@Override
	public int getId() {
		return this.id;
	}

	/**
	 * Serializes the specified {@link INodeInput}'s input values according to this configuration's inputs into a {@link PacketBuffer}
	 * @param user the user of the rune chain
	 * @param input the input that provides the rune's input values
	 * @param buffer the packet buffer to serialize into
	 */
	@SuppressWarnings("unchecked")
	public void serialize(IRuneChainUser user, INodeInput input, PacketBuffer buffer) {
		for(InputKey<?, ?> inputKey : this.inputs) {
			IInputSerializer<?> serializer = inputKey.getSerializer();

			if(serializer != null) {
				Object value = inputKey.getter().get(input);

				buffer.writeBoolean(value != null);

				if(value != null) {
					if(inputKey.isCollection() && value instanceof Collection) {
						buffer.writeBoolean(true);

						Collection<?> collection = (Collection<?>) value;

						buffer.writeVarInt(collection.size());

						for(Object element : collection) {
							((IInputSerializer<Object>) serializer).write(element, buffer);
						}
					} else {
						buffer.writeBoolean(false);

						((IInputSerializer<Object>) serializer).write(value, buffer);
					}
				}
			}
		}
	}

	/**
	 * Deserializes the specified {@link PacketBuffer} according to this configuration's inputs into a list of input values
	 * @param user the user of the rune chain
	 * @param buffer the packet buffer to deserialize
	 * @return the list of input values
	 */
	public List<Object> deserialize(IRuneChainUser user, PacketBuffer buffer) {
		List<Object> values = new ArrayList<>(this.inputs.size());

		for(InputKey<?, ?> inputKey : this.inputs) {
			IInputSerializer<?> serializer = inputKey.getSerializer();

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
