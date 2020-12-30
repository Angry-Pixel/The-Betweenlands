package thebetweenlands.api.runechain.base;

import java.util.List;

public interface IType {
	/**
	 * Returns the class of the type
	 * @return class of the type
	 */
	public Class<?> getJavaType();
	
	/**
	 * Returns the generic types of the type. These are optional and if not
	 * specified, i.e. returning an empty list, no generic type checks will be done by {@link IConfigurationInput#isType(IConfigurationOutput, IType)}.
	 * @return generic types of the type
	 */
	public List<IType> getTypeGenerics();
}