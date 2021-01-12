package thebetweenlands.api.runechain.base;

import javax.annotation.Nullable;

@FunctionalInterface
public interface IConfigurationLinkAccess {
	/**
	 * Returns the configuration output of the node that is linked to the specified input.
	 * Calling this method is safe for any input index.
	 * @param input the index of the input
	 * @return
	 */
	@Nullable
	public IConfigurationOutput getLinkedOutput(int input);
}