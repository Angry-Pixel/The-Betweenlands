package thebetweenlands.api.rune;

import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

public interface INodeConfiguration {
	public static interface IType {
		/**
		 * Returns the class of the type
		 * @return class of the type
		 */
		public Class<?> getTypeClass();

		/**
		 * Returns the generic types of the type. These are optional and if not
		 * specified, i.e. returning an empty list, no generic type checks will be done by {@link IConfigurationInput#test(IConfigurationOutput, IType)}.
		 * @return generic types of the type
		 */
		public List<IType> getTypeGenerics();
	}

	public static interface IConfigurationInput extends BiPredicate<IConfigurationOutput, IType> {
		/**
		 * Returns whether the specified configuration output and its output type are applicable to this input.
		 * @param output configuration output to check
		 * @param type type of the configuration output to check. The generic types {@link IType#getTypeGenerics()} are optional and
		 * if not specified the predicate <b>must not</b> do any checks for the generic types.
		 * @return whether the specified type is applicable to this input
		 */
		@Override
		public boolean test(IConfigurationOutput output, IType type);

		/**
		 * Returns whether this input accepts multiple values of the input's type at once
		 * in a collection object, i.e. the input value will be a {@link Collection}<code>&lt;input type&gt;</code>, where <code>input type</code>
		 * is a type accepted by {@link #test(IType)}.
		 * @return whether this input accepts multiple values of the input's type at once
		 */
		public boolean isCollection();

		/**
		 * Returns a descriptor for this input that identifies the kind
		 * of types this input accepts. May be null if such a descriptor is not feasible
		 * @return a descriptor for this input
		 */
		@Nullable
		public String getDescriptor();
	}

	public static interface IConfigurationOutput {
		/**
		 * Returns the type of the output.
		 * @param inputs - linked input types. This list contains all currently linked
		 * input types. <code>inputs.size() == {@link INodeConfiguration#getInputs()}</code>.
		 * If an input is not linked the type is <i><b>null</b></i>.
		 * @return the type of the output
		 */
		public IType getType(List<IType> inputs);

		/**
		 * Returns whether the output is enabled for the given linked inputs. Can be used
		 * to set a requirement for a specific input to be linked before the output
		 * becomes available, e.g. to let through the input type.
		 * @param inputs - linked input types. This list contains all currently linked
		 * input types. <code>inputs.size() == {@link INodeConfiguration#getInputs()}</code>.
		 * If an input is not linked the type is <i><b>null</b></i>.
		 * @return whether the output is enabled for the given linked inputs
		 */
		public boolean isEnabled(List<IType> inputs);

		/**
		 * Returns whether this output produces multiple values of the output's type at once
		 * in a collection object, i.e. the output value will be a {@link Collection}<code>&lt;output type&gt;</code>, where <code>output type</code>
		 * is {@link #getType(List)}.
		 * @return whether this output produces multiple values of the output's type at once
		 */
		public boolean isCollection();


		/**
		 * Returns a descriptor for this output that identifies the kind
		 * of types this output produces. May be null if such a descriptor is not feasible
		 * @return a descriptor for this output
		 */
		@Nullable
		public String getDescriptor();
	}

	/**
	 * Returns a list of inputs that determine which types are accepted on each input slot.
	 * @return a list of inputs that determine which types are accepted on each input slot
	 */
	public List<? extends IConfigurationInput> getInputs();

	/**
	 * Returns a list of outputs that determine the type produced on each output slot.
	 * @return a list of outputs that determine the type produced on each output slot
	 */
	public List<? extends IConfigurationOutput> getOutputs();

	/**
	 * Returns the ID of this node configuration. Must be unique per {@link INodeBlueprint} and
	 * must be persistent.
	 * @return the ID of this node configuration
	 */
	public int getId();
}
