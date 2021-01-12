package thebetweenlands.api.runechain.chain;

import thebetweenlands.api.runechain.base.INodeBlueprint;
import thebetweenlands.api.runechain.base.INodeCompositionBlueprint;
import thebetweenlands.api.runechain.base.INodeConfiguration;

public interface IRuneChainBlueprint extends INodeCompositionBlueprint<IRuneExecutionContext> {
	@Override
	public IRuneChain create();
	
	/**
	 * Adds a new node blueprint to the rune chain
	 * @param blueprint the blueprint to add to the rune chain
	 */
	public void addNodeBlueprint(INodeBlueprint<?, IRuneExecutionContext> blueprint);

	/**
	 * Adds a new node blueprint at the specified index to the rune chain
	 * @param index the index where the blueprint should be inserted
	 * @param blueprint the blueprint to add to the rune chain
	 */
	public void addNodeBlueprint(int index, INodeBlueprint<?, IRuneExecutionContext> blueprint);

	/**
	 * Adds a new node blueprint with the specified configuration to the rune chain
	 * @param blueprint the blueprint to add to the rune chain
	 * @param configuration the node configuration to be used
	 */
	public void addNodeBlueprint(INodeBlueprint<?, IRuneExecutionContext> blueprint, INodeConfiguration configuration);

	/**
	 * Adds a new node blueprint with the specified configuration at the specified index to the rune chain
	 * @param index the index where the blueprint should be inserted
	 * @param blueprint the blueprint to add to the rune chain
	 * @param configuration the node configuration to be used
	 */
	public void addNodeBlueprint(int index, INodeBlueprint<?, IRuneExecutionContext> blueprint, INodeConfiguration configuration);

	/**
	 * Removes the node blueprint at the specified index
	 * @param index the index of the node blueprint to remove
	 * @return the removed node blueprint
	 */
	public INodeBlueprint<?, IRuneExecutionContext> removeNodeBlueprint(int index);

	/**
	 * Tries to unlink the specified input
	 * @param inNodeIndex the index of the input node
	 * @param inputIndex the index of the input node's input
	 * @return <i>true</i> if the input was successfully unlinked, <i>false</i> otherwise
	 */
	public boolean unlink(int inNodeIndex, int inputIndex);

	/**
	 * Link the specified input to the specified output.
	 * No validation is done, if necessary see {@link #canLink(int, int, int, int)}.
	 * @param inNodeIndex the index of the input node
	 * @param inputIndex the index of the input node's input
	 * @param outNodeIndex the index of the output node
	 * @param outputIndex the index of the output node's output
	 * @return <i>true</i> if the input was successfully linked, <i>false</i> otherwise
	 */
	public boolean link(int inNodeIndex, int inputIndex, int outNodeIndex, int outputIndex);

	/**
	 * Returns whether the specified input can be linked to the specified output
	 * @param inNodeIndex the index of the input node
	 * @param inputIndex the index of the input node's input
	 * @param outNodeIndex the index of the output node
	 * @param outputIndex the index of the output node's output
	 * @return whether the specifeid input can be linked to the specified output
	 */
	public boolean canLink(int inNodeIndex, int inputIndex, int outNodeIndex, int outputIndex);
}
