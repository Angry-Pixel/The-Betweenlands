package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public class Node {
	private final Graph graph;
	private final int id;
	private final String type;
	private final String tag;

	private final List<Edge> edges = new ArrayList<>();
	private final Map<Node, Edge> edgeMap = new HashMap<>();

	Node(Graph graph, String type, @Nullable String tag) {
		this.graph = graph;
		this.id = graph.nodeIdCounter++;
		this.type = type;
		this.tag = tag;
	}

	void removeEdge(Edge edge) {
		this.edges.remove(edge);
		this.edgeMap.remove(edge.getOther(this));
	}

	public void disconnect(Node node) {
		Edge edge = this.edgeMap.get(node);
		if(edge != null) {
			this.removeEdge(edge);
			node.removeEdge(edge);
		}
	}

	public Node chain(Node node) {
		this.connect(node);
		return node;
	}

	public Edge connect(Node node) {
		return this.connect(node, Edge.DEFAULT_TYPE, false);
	}

	public Node chain(Node node, boolean bidirectional) {
		this.connect(node, bidirectional);
		return node;
	}

	public Edge connect(Node node, boolean bidirectional) {
		return this.connect(node, Edge.DEFAULT_TYPE, bidirectional);
	}

	public Node chain(Node node, String type) {
		this.connect(node, type);
		return node;
	}

	public Edge connect(Node node, String type) {
		return this.connect(node, type, false);
	}

	public Node chain(Node node, String type, boolean bidirectional) {
		this.connect(node, type, bidirectional);
		return node;
	}

	public Edge connect(Node node, String type, boolean bidirectional) {
		Edge edge = new Edge(this, node, type, bidirectional);
		this.edges.add(edge);
		this.edgeMap.put(node, edge);
		node.edges.add(edge);
		node.edgeMap.put(this, edge);
		return edge;
	}

	@Nullable
	public Edge getEdge(Node node) {
		return this.edgeMap.get(node);
	}

	public List<Edge> getEdges() {
		return this.edges;
	}

	public Graph getGraph() {
		return this.graph;
	}

	public int getID() {
		return this.id;
	}

	public String getType() {
		return this.type;
	}

	@Nullable
	public String getTag() {
		return this.tag;
	}

	public boolean isSameType(Node node) {
		return (this.type == null && node.type == null) || (this.type != null && this.type.equals(node.type));
	}

	public boolean isSameTag(Node node) {
		return (this.tag == null && node.tag == null) || (this.tag != null && this.tag.equals(node.tag));
	}

	@Override
	public String toString() {
		ToStringHelper str = MoreObjects.toStringHelper(this);
		str.add("id", this.getID());
		str.add("type", this.getType());
		str.add("tag", this.getTag());
		return str.toString();
	}
}
