package thebetweenlands.api.runechain.base;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

public interface IConfigurationOutput {
	/**
	 * Returns the type of the output.
	 * @param inputs the linked input types. This list contains all currently linked
	 * input types. <code>inputs.size() == {@link INodeConfiguration#getInputs()}</code>.
	 * If an input is not linked the type is <i><b>null</b></i>.
	 * @return the type of the output
	 */
	public IType getType(List<IType> inputs);

	/**
	 * Returns whether the output is enabled for the given linked inputs. Can be used
	 * to set a requirement for a specific input to be linked before the output
	 * becomes available, e.g. to let through the input type.
	 * @param inputs the linked input types. This list contains all currently linked
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
	
	/**
	 * Returns if the given output value is valid for this output, false otherwise.
	 * @param value output value to check
	 * @return whether the given output value is valid
	 */
	public boolean validate(Object value);
}