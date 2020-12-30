package thebetweenlands.api.runechain.base;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

public interface INodeCompositionBlueprint<E> {
	/**
	 * Returns the number of node blueprints in this composition blueprint.
	 * @return the number of node blueprints in this composition blueprint
	 */
	public int getNodeBlueprints();

	/**
	 * Returns the node blueprint at the specified node index
	 * @param node the index of the node
	 * @return the node blueprint at the specified node index
	 * @throws IndexOutOfBoundsException if the node index is out of range (node < 0 || node >= {@link #getNodeBlueprints()})
	 */
	public INodeBlueprint<?, E> getNodeBlueprint(int node);
	
	/**
	 * Returns the node configuration at the specified node index. May return null
	 * if the configuration was not specified (yet).
	 * @param node the index of the node
	 * @return the node configuration at the specified node index
	 * @throws IndexOutOfBoundsException if the node index is out of range (node < 0 || node >= {@link #getNodeBlueprints()})
	 */
	@Nullable
	public INodeConfiguration getNodeConfiguration(int node);
	
	/**
	 * Returns the link at the specified node index and input index.
	 * @param node the index of the node
	 * @param input the index of the input
	 * @return the link at the specified node index and input index.
	 * If the specified input does not exist or no link is available <b><i>null</b></i> is returned
	 * @throws IndexOutOfBoundsException if the node index is out of range (node < 0 || node >= {@link #getNodeBlueprints()})
	 */
	public INodeLink getLink(int node, int input);
	
	/**
	 * Returns a collection of input indices that have links.
	 * @param node the index of the node
	 * @return a collection containing all input indices that have links. If no links are available at any slot an empty collection is returned
	 * @throws IndexOutOfBoundsException if the node index is out of range (node < 0 || node >= {@link #getNodeBlueprints()})
	 */
	public Collection<Integer> getLinkedSlots(int node);
	
	/**
	 * Creates a node composition instance from this blueprint.
	 * @return a node composition instance created from this blueprint
	 */
	public INodeComposition<E> create();
	
	public default IConfigurationLinkAccess createLinkAccess(int node) {
		return input -> {
			INodeLink link = this.getLink(node, input);
			if(link != null && link.getOutput() >= 0) {
				INodeConfiguration configuration = this.getNodeConfiguration(link.getNode());
				if(configuration != null) {
					List<? extends IConfigurationOutput> outputs = configuration.getOutputs();
					if(link.getOutput() < outputs.size()) {
						return outputs.get(link.getOutput());
					}
				}
			}
			return null;
		};
	}
}
