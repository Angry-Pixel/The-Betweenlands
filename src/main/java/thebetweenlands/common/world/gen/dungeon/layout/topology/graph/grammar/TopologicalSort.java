package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;

public class TopologicalSort {
	public static class LoopDetectedException extends Exception {
		private static final long serialVersionUID = -173239956750955924L;
	}

	private static class Hypernode {
		private final Node node;
		private final Set<Hypernode> children = new LinkedHashSet<>();

		private Hypernode parent;

		private LinkedHashSet<Edge> cachedEdges = null;
		private LinkedHashSet<Edge> cachedOutgoingEdges = null;
		private Properties cachedWeightProperties = null;
		private int cachedWeight = 0;

		private Hypernode() {
			this.node = null;
		}

		private Hypernode(Node node) {
			this.node = node;
		}

		private boolean add(Hypernode hypernode) {
			if(hypernode != this && hypernode.parent != this) {
				if(hypernode.parent != null) {
					hypernode.parent.children.remove(hypernode);
					hypernode.parent.cachedEdges = null;
					hypernode.parent.cachedOutgoingEdges = null;
					hypernode.parent.cachedWeightProperties = null;
				}
				hypernode.parent = this;
				if(this.children.add(hypernode)) {
					this.cachedEdges = null;
					this.cachedOutgoingEdges = null;
					this.cachedWeightProperties = null;
					return true;
				}
			}
			return false;
		}

		private boolean contains(Node node) {
			if(node == null) {
				return false;
			}
			if(this.node == node) {
				return true;
			}
			for(Hypernode child : this.children) {
				if(child.contains(node)) {
					return true;
				}
			}
			return false;
		}

		private boolean contains(Predicate<Node> predicate) {
			if(this.node == null) {
				return false;
			}
			if(predicate.test(this.node)) {
				return true;
			}
			for(Hypernode child : this.children) {
				if(child.contains(predicate)) {
					return true;
				}
			}
			return false;
		}

		private boolean contains(Hypernode hypernode) {
			if(hypernode == null) {
				return false;
			}
			if(this == hypernode || this.children.contains(hypernode)) {
				return true;
			}
			for(Hypernode child : this.children) {
				if(child == hypernode || child.contains(hypernode)) {
					return true;
				}
			}
			return false;
		}

		private Hypernode getParentOnSameDepth(Hypernode hypernode) {
			Hypernode current = this;
			do {
				if(current.parent == hypernode.parent) {
					return current;
				}
				current = current.parent;
			} while(current != null);
			return null;
		}

		private LinkedHashSet<Edge> getEdges(boolean outgoingOnly) {
			if(!outgoingOnly && this.cachedEdges != null) {
				return this.cachedEdges;
			} else if(outgoingOnly && this.cachedOutgoingEdges != null) {
				return this.cachedOutgoingEdges;
			}
			LinkedHashSet<Edge> edges = new LinkedHashSet<>();
			this.getEdges(edges, this, outgoingOnly);
			if(!outgoingOnly) {
				this.cachedEdges = edges;
			} else {
				this.cachedOutgoingEdges = edges;
			}
			return edges;
		}

		private void getEdges(LinkedHashSet<Edge> edges, Hypernode parent, boolean outgoingOnly) {
			if(this.node != null) {
				for(Edge edge : this.node.getEdges()) {
					Node other = edge.getOther(this.node);
					if((!outgoingOnly || parent.contains(edge.getLeft())) && !parent.contains(other)) {
						edges.add(edge);
					}
				}
			}
			for(Hypernode child : this.children) {
				child.getEdges(edges, parent, outgoingOnly);
			}
		}

		private boolean isSingleNode() {
			return this.node != null;
		}

		private int getWeight(Properties properties) {
			if(this.cachedWeightProperties == properties) {
				return this.cachedWeight;
			}
			int weight = properties.groupSizeWeight * this.children.size();
			for(Entry<Predicate<Node>, Integer> entry : properties.nodeWeight.entrySet()) {
				if(this.contains(entry.getKey())) {
					weight += entry.getValue();
				} else {
					Set<Node> visited = new HashSet<>();
					for(Edge edge : this.getEdges(true)) {
						if(this.hasNodeDownstream(edge.getRight(), entry.getKey(), visited)) {
							weight += entry.getValue();
							break;
						}
					}
				}
			}
			this.cachedWeightProperties = properties;
			this.cachedWeight = weight;
			return weight;
		}

		private boolean hasNodeDownstream(Node node, Predicate<Node> predicate, Set<Node> visited) {
			if(predicate.test(node)) {
				return true;
			}
			for(Edge edge : node.getEdges()) {
				if(edge.getLeft() == node) {
					if(visited.add(edge.getRight()) && this.hasNodeDownstream(edge.getRight(), predicate, visited)) {
						return true;
					}
				}
			}
			return false;
		}

		private List<Node> sort(Map<Node, Hypernode> hypergraph, Properties properties) {
			List<Node> list = new ArrayList<>();
			this.sort(hypergraph, list, properties);
			return list;
		}

		private void sort(Map<Node, Hypernode> hypergraph, List<Node> list, Properties properties) {
			LinkedHashSet<Hypernode> pendingSet;

			if(properties.groupSizeWeight != 0 || !properties.nodeWeight.isEmpty()) {
				//Sort child hypernodes according to their weight
				List<Hypernode> sortedChildren = new ArrayList<>(this.children);
				Collections.sort(sortedChildren, (h1, h2) -> -Integer.compare(h1.getWeight(properties), h2.getWeight(properties)));
				pendingSet = new LinkedHashSet<>(sortedChildren);
			} else {
				pendingSet = new LinkedHashSet<>(this.children);
			}

			//Topologically sort the children of this hypernode.
			//Single node children are added directly to the list, whereas
			//complex hypernodes' contents are added with a recursive sort
			boolean changed;
			do {
				changed = false;

				Iterator<Hypernode> it = pendingSet.iterator();
				while(it.hasNext()) {
					Hypernode hypernode = it.next();

					boolean hasIncomingEdges = false;

					//Check if hypernode has any incoming edges.
					for(Edge edge : hypernode.getEdges(false)) {
						if(hypernode.contains(edge.getRight())) {
							Hypernode neighbor = hypergraph.get(edge.getLeft());
							if(neighbor != null) {
								neighbor = neighbor.getParentOnSameDepth(hypernode);
							}
							if(neighbor != null && pendingSet.contains(neighbor)) {
								hasIncomingEdges = true;
								break;
							}
						}
					}

					//If no, the hypernode can be added to the list and
					//be removed from the pending set
					if(!hasIncomingEdges) {
						changed = true;
						it.remove();
						if(hypernode.isSingleNode()) {
							list.add(hypernode.node);
						} else {
							hypernode.sort(hypergraph, list, properties);
						}
					}
				}
			} while(changed);
		}

		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();
			if(this.isSingleNode()) {
				str.append("[1] ");
				str.append(this.node.getType());
				str.append(" (");
				str.append(this.node.getID());
				str.append(") ");
			} else {
				str.append("[");
				str.append(this.children.size());
				str.append("] ");
			}
			for(Hypernode child : this.children) {
				if(child.isSingleNode()) {
					str.append(child.node.getType());
					str.append(" (");
					str.append(child.node.getID());
					str.append(") ");
				} else {
					str.append("{");
					str.append(child.toString());
					str.append("} ");
				}
			}
			return str.substring(0, str.length() - 1);
		}
	}

	private static class TraversalNode {
		private final TraversalNode parent;
		private final Hypernode hypernode;

		private TraversalNode(TraversalNode parent, Hypernode hypernode) {
			this.parent = parent;
			this.hypernode = hypernode;
		}
	}

	public static class GroupedGraph extends Graph {
		public static class Group {
			private final List<Node> nodes = new ArrayList<>();

			private Group() { }

			public List<Node> getNodes() {
				return Collections.unmodifiableList(this.nodes);
			}

			@Override
			public String toString() {
				StringBuilder str = new StringBuilder();
				for(Node node : this.nodes) {
					str.append(node.toString());
					str.append(", ");
				}
				return MoreObjects.toStringHelper(this).add("nodes", "[" + str.substring(0, Math.max(0, str.length() - 2)) + "]").toString();
			}
		}

		private final Map<Node, Group> groups = new HashMap<>();

		private GroupedGraph(List<Node> nodes, Map<Node, Group> groups) {
			Map<Node, Node> merge = this.merge(nodes, true);

			if(!groups.isEmpty()) {
				for(Node node : nodes) {
					Group group = groups.get(node);

					if(group != null) {
						Node mergedNode = merge.get(node);
						group.nodes.add(mergedNode);
						this.groups.put(mergedNode, group);
					}
				}
			}
		}

		@Override
		public void removeNode(Node node) {
			super.removeNode(node);

			Group group = this.groups.remove(node);
			if(group != null) {
				group.nodes.remove(node);
			}
		}

		public Map<Node, Group> getGroups() {
			return Collections.unmodifiableMap(this.groups);
		}
	}

	public static class Properties {
		/**
		 * The grouping predicate specifies which edges are considered grouping edges, i.e.,
		 * whether the two nodes connected to the edge should be grouped together.
		 */
		@Nullable
		public Predicate<Edge> groupingPredicate;

		/**
		 * Determines by how much a group's size should be multiplied before being added to
		 * the group's priority weight.
		 * The default value of -1 prioritizes small groups (e.g. individual nodes). This
		 * causes a group's own nodes (one-sized groups) to be visited first before visiting
		 * loops starting and ending on that same group.
		 */
		public int groupSizeWeight = -1;

		/**
		 * If a group itself or any nodes downstream contain a node matched by the specified predicate
		 * then the corresponding weight is added to the group's priority weight.
		 */
		public Map<Predicate<Node>, Integer> nodeWeight = new HashMap<>();
	}

	/**
	 * Topologically sorts the specified graph and returns the resulting sorted graph.
	 * @param graph
	 * @return
	 * @throws LoopDetectedException
	 */
	public static GroupedGraph sort(Graph graph) throws LoopDetectedException {
		return sort(graph, null);
	}

	/**
	 * Topologically sorts the specified graph and returns the resulting sorted graph.
	 * Nodes may be grouped together (see {@link Properties#groupingPredicate}) to make them
	 * appear consecutively in the output wherever possible.
	 * @param graph
	 * @param properties
	 * @return
	 * @throws LoopDetectedException
	 */
	public static GroupedGraph sort(Graph graph, @Nullable Properties properties) throws LoopDetectedException {
		if(properties == null) {
			properties = new Properties();
		}

		final int n = graph.getNodes().size();

		Map<Node, GroupedGraph.Group> groups = new HashMap<>();
		Map<Node, Hypernode> hypergraph = new HashMap<>();
		List<Hypernode> hypernodeList = new ArrayList<>();

		if(properties.groupingPredicate != null) {
			for(Node node : graph.getNodes()) {
				createHypernodes(properties.groupingPredicate, node, hypergraph, hypernodeList, null, 0, n);
			}

			for(Hypernode hypernode : hypernodeList) {
				if(!hypernode.isSingleNode()) {
					GroupedGraph.Group group = new GroupedGraph.Group();

					for(Hypernode child : hypernode.children) {
						if(child.isSingleNode()) {
							groups.put(child.node, group);
						} else {
							throw new IllegalStateException();
						}
					}
				}
			}

			boolean changed;
			do {
				changed = false;
				for(Hypernode hypernode : hypernodeList) {
					if(!hypernode.isSingleNode()) {
						changed |= resolveLoops(hypernode, hypergraph);
					}
				}
			} while(changed);
		} else {
			for(Node node : graph.getNodes()) {
				hypergraph.put(node, new Hypernode(node));
			}
		}

		Hypernode graphHypernode = new Hypernode();
		for(Hypernode hypernode : hypernodeList) {
			if(hypernode.parent == null) {
				graphHypernode.add(hypernode);
			}
		}

		return new GroupedGraph(graphHypernode.sort(hypergraph, properties), groups);
	}

	/**
	 * Groups the specified node itself and all nodes connected through edges accepted by the predicate into a single group {@link Hypernode}
	 * @param groupingPredicate
	 * @param node
	 * @param hypergraph
	 * @param groupHypernode
	 * @param i
	 * @param n
	 */
	private static void createHypernodes(Predicate<Edge> groupingPredicate, Node node, Map<Node, Hypernode> hypergraph, List<Hypernode> hypernodeList, Hypernode groupHypernode, int i, int n) throws LoopDetectedException {
		if(i > n) {
			throw new LoopDetectedException();
		}

		if(!hypergraph.containsKey(node)) {
			boolean added = false;

			//Recursively traverse through all edges that are matched
			//by the grouping predicate, so that all those connected
			//nodes are grouped into the same hypernode
			for(Edge edge : node.getEdges()) {
				if(edge.isBidirectional()) {
					throw new LoopDetectedException();
				}

				if(groupingPredicate.test(edge)) {
					//If there is no grouping hypernode yet a new one needs
					//to be created
					if(groupHypernode == null) {
						groupHypernode = new Hypernode();
						hypernodeList.add(groupHypernode);
					}

					//If there is such an edge then the current node
					//needs to be added to the group hypernode
					if(!added) {
						Hypernode hypernode;
						hypergraph.put(node, hypernode = new Hypernode(node));
						hypernodeList.add(hypernode);
						groupHypernode.add(hypernode);
						added = true;
					}

					createHypernodes(groupingPredicate, edge.getOther(node), hypergraph, hypernodeList, groupHypernode, i + 1, n);
				}
			}

			//If the node wasn't added yet then it's a terminal
			//node of a group or a non-grouped node, so it needs
			//to be added now
			if(!hypergraph.containsKey(node)) {
				Hypernode hypernode;
				hypergraph.put(node, hypernode = new Hypernode(node));
				hypernodeList.add(hypernode);

				if(groupHypernode != null) {
					groupHypernode.add(hypernode);
				}
			}
		}
	}

	/**
	 * Returns the right neighbor {@link Hypernode} of the specified edge, i.e., neighbor is at the same depth as the source {@link Hypernode}
	 * @param source
	 * @param edge
	 * @param hypergraph
	 * @return
	 */
	@Nullable
	private static Hypernode getRightNeighbor(Hypernode source, Hypernode edgeNode, Edge edge, Map<Node, Hypernode> hypergraph) {
		if(edge.isBidirectional()) {
			throw new IllegalStateException();
		}
		Node rightNode = edge.getRight();
		//Right node is edgeNode --> there is no right neighbor
		if(edgeNode.contains(rightNode)) {
			return null;
		}
		Hypernode rightNeighbor = hypergraph.get(rightNode);
		return rightNeighbor != null ? rightNeighbor.getParentOnSameDepth(source) : null;
	}

	/**
	 * Resolves loops on the same depth as the source {@link Hypernode} in the hypergraph by finding loops that
	 * start and end on the specified source {@link Hypernode} and then moving all {@link Hypernode}s on those
	 * loops into the source {@link Hypernode} as children. Therefore, edges that create loops are moved inside
	 * the source {@link Hypernode}.
	 * @param source
	 * @param hypergraph
	 * @return
	 */
	private static boolean resolveLoops(Hypernode source, Map<Node, Hypernode> hypergraph) {
		LinkedHashSet<Hypernode> loopHypernodes = new LinkedHashSet<>();

		//Start a traversal from each outgoing neighbor of the
		//source hypernode. If the traversal ends up at the source
		//hypernode again then there is a loop and the hypernodes
		//on that loop must be incorporated into the source hypernode.
		for(Edge edge : source.getEdges(true)) {
			Hypernode neighbor = getRightNeighbor(source, source, edge, hypergraph);

			if(neighbor != null) {
				traverseHypergraphLoops(source, new TraversalNode(null, neighbor), new HashSet<>(), loopHypernodes, hypergraph);
			}
		}

		boolean changed = false;

		//Incorporate hypernodes on loops into source hypernode
		for(Hypernode loopHypernode : loopHypernodes) {
			if(!source.contains(loopHypernode)) {
				changed |= source.add(loopHypernode);
			}
		}

		return changed;
	}

	/**
	 * Traverses the hypergraph from the specified source {@link Hypernode} and adds all other {@link Hypernode}s on the same depth that lie on loops
	 * starting and ending at the source {@link Hypernode} to the specified set.
	 * @param source
	 * @param current
	 * @param visited
	 * @param loopHypernodes
	 * @param hypergraph
	 */
	private static void traverseHypergraphLoops(Hypernode source, TraversalNode current, Set<Hypernode> visited, Set<Hypernode> loopHypernodes, Map<Node, Hypernode> hypergraph) {
		for(Edge edge : current.hypernode.getEdges(true)) {
			Hypernode neighbor = getRightNeighbor(source, current.hypernode, edge, hypergraph);

			if(neighbor != null) {
				if(source.contains(neighbor)) {
					//Backtrack and add all hypernodes on the loop to the list
					TraversalNode traversal = current;
					do {
						loopHypernodes.add(traversal.hypernode);
						traversal = traversal.parent;
					} while(traversal != null);
				} else {
					//There can be loops on the same depth because not all loops
					//have been resolved yet, so a safety check is needed
					if(visited.add(neighbor)) {
						traverseHypergraphLoops(source, new TraversalNode(current, neighbor), visited, loopHypernodes, hypergraph);
					}
				}
			}
		}
	}
}
