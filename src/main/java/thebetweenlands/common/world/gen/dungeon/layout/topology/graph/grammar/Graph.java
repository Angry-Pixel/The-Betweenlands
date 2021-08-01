package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Graph {
	private final List<Node> nodes = new ArrayList<>();
	private final Map<String, List<Node>> typeMap = new HashMap<>();
	private final Map<String, List<Node>> tagMap = new HashMap<>();

	private boolean mutated = false;

	int nodeIdCounter = 0;
	int edgeIdCounter = 0;

	public Graph copy() {
		Graph graph = new Graph();
		graph.merge(this, true);
		return graph;
	}

	public Pair<Map<Node, Node>, Map<Edge, Edge>> merge(Graph graph) {
		return this.merge(graph, false);
	}

	public Pair<Map<Node, Node>, Map<Edge, Edge>> merge(Graph graph, boolean mergeTags) {
		return this.merge(graph.nodes, false);
	}

	protected Pair<Map<Node, Node>, Map<Edge, Edge>> merge(List<Node> nodes, boolean mergeTags) {
		Map<Node, Node> nodeMerge = new HashMap<>();
		Map<Edge, Edge> edgeMerge = new HashMap<>();

		for(Node node : nodes) {
			nodeMerge.put(node, this.addNode(node.getType(), mergeTags ? node.getTag() : null));
		}

		Set<Edge> edges = new HashSet<>();

		for(Node node : nodes) {
			for(Edge edge : node.getEdges()) {
				if(edges.add(edge)) {
					Node left = nodeMerge.get(edge.getLeft());
					Node right = nodeMerge.get(edge.getRight());
					edgeMerge.put(edge, left.connect(right, edge.getType(), edge.isBidirectional()));
				}
			}
		}

		return Pair.of(nodeMerge, edgeMerge);
	}

	public Node addNode(String type) {
		return this.addNode(type, null);
	}

	public Node addNode(String type, @Nullable String tag) {
		Node node = new Node(this, type, tag);

		this.nodes.add(node);

		List<Node> typeNodes = this.typeMap.get(type);
		if(typeNodes == null) {
			this.typeMap.put(type, typeNodes = new ArrayList<>());
		}
		typeNodes.add(node);

		if(tag != null) {
			List<Node> tagNodes = this.tagMap.get(tag);
			if(tagNodes == null) {
				this.tagMap.put(tag, tagNodes = new ArrayList<>());
			}
			tagNodes.add(node);
		}

		return node;
	}

	public void removeNode(Node node) {
		this.nodes.remove(node);

		List<Node> typeNodes = this.typeMap.get(node.getType());
		if(typeNodes != null) {
			typeNodes.remove(node);
			if(typeNodes.isEmpty()) {
				this.typeMap.remove(node.getType());
			}
		}

		String tag = node.getTag();
		if(tag != null) {
			List<Node> tagNodes = this.tagMap.get(tag);
			if(tagNodes != null) {
				tagNodes.remove(node);
				if(tagNodes.isEmpty()) {
					this.tagMap.remove(tag);
				}
			}
		}

		for(Edge edge : node.getEdges()) {
			edge.getOther(node).removeEdge(edge);
		}
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public List<Node> getNodesByType(@Nullable String type) {
		List<Node> nodes = this.typeMap.get(type);
		if(nodes == null) {
			nodes = Collections.emptyList();
		}
		return nodes;
	}

	public List<Node> getNodesByTag(@Nullable String tag) {
		List<Node> nodes = this.tagMap.get(tag);
		if(nodes == null) {
			nodes = Collections.emptyList();
		}
		return nodes;
	}

	public Set<String> getTypes() {
		return this.typeMap.keySet();
	}

	public Set<String> getTags() {
		return this.tagMap.keySet();
	}

	void setMutated(boolean mutated) {
		this.mutated = mutated;
	}

	public boolean isMutated() {
		return this.mutated;
	}

	@Override
	public String toString() {
		ToStringHelper str = MoreObjects.toStringHelper(this);
		str.add("nodes", this.getNodes().size());
		for(Node node : this.getNodes()) {
			for(Edge edge : node.getEdges()) {
				if(edge.isBidirectional() || edge.getLeft() == node) {
					str.addValue(node.getType() + " (" + node.getID() + ") --(" + edge.getType() + ")--> " + edge.getOther(node).getType() + " (" + edge.getOther(node).getID() + ")");
				}
			}
		}
		return str.toString();
	}
}
