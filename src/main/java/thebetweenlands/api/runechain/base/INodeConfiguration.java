package thebetweenlands.api.runechain.base;

import java.util.List;
import java.util.function.BiPredicate;

public interface INodeConfiguration {
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
