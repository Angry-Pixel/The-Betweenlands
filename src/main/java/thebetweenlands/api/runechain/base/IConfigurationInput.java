package thebetweenlands.api.runechain.base;

import java.util.Collection;

import javax.annotation.Nullable;

public interface IConfigurationInput {
	/**
	 * Returns whether the specified configuration output and its output type are applicable to this input.
	 * @param output configuration output to check
	 * @param type type of the configuration output to check. The generic types {@link IType#getTypeGenerics()} are optional and
	 * if not specified this method <b>must not</b> do any checks for the generic types.
	 * @return whether the specified type is applicable to this input
	 */
	public boolean isType(IConfigurationOutput output, IType type);

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
	
	/**
	 * Returns if the given input value is valid for this input, false otherwise.
	 * @param value input value to check
	 * @return whether the given input value is valid
	 */
	public boolean validate(Object value);
}