package thebetweenlands.api.rune;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface INodeConfiguration {
	public static interface IType {
		/**
		 * Returns the class of the type
		 * @return class of the type
		 */
		public Class<?> getTypeClass();
		
		/**
		 * Returns the generic types of the type. These are optional and if not
		 * specified no generic type checks will be done by {@link IConfigurationInput#test(IType)}.
		 * @return generic types of the type
		 */
		public List<IType> getTypeGenerics();
	}
	
	public static interface IConfigurationInput extends Predicate<IType> {
		/**
		 * Returns whether the specified type is applicable to this input.
		 * @param type - type to check. The generic types {@link IType#getTypeGenerics()} are optional and
		 * if not specified the predicate <b>must not</b> do any checks for the generic types.
		 * @return whether the specified type is applicable to this input
		 */
		@Override
		public boolean test(IType type);

		/**
		 * Returns whether this input accepts multiple values of the input's type at once
		 * in a collection object, i.e. the input value will be a {@link Collection}<code>&lt;input type&gt;</code>, where <code>input type</code>
		 * is a type accepted by {@link #test(IType)}.
		 * @return whether this input accepts multiple values of the input's type at once
		 */
		public boolean isCollection();
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
	}

	/**
	 * Returns a list of inputs that determine which types are accepted on each input slot.
	 * @return a list of inputs that determine which types are accepted on each input slot
	 */
	public List<IConfigurationInput> getInputs();

	/**
	 * Returns a list of outputs that determine the type produced on each output slot.
	 * @return a list of outputs that determine the type produced on each output slot
	 */
	public List<IConfigurationOutput> getOutputs();
}
