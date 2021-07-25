package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import javax.annotation.Nullable;

public class Edge {
	public static final String DEFAULT_TYPE = "generic";
	
	private final int id;
	private final Node left;
	private final Node right;
	private final boolean bidirectional;
	private final String type;
	
	Edge(Node left, Node right, String type, boolean bidirectional) {
		if(left.getGraph() != right.getGraph()) {
			throw new IllegalStateException("Cannot create edge between nodes from different graphs");
		}
		this.id = left.getGraph().edgeIdCounter++;
		this.left = left;
		this.right = right;
		this.bidirectional = bidirectional;
		this.type = type;
	}
	
	public boolean isBidirectional() {
		return this.bidirectional;
	}
	
	public boolean isFrom(Node node) {
		return this.bidirectional || this.left == node;
	}
	
	public boolean isTo(Node node) {
		return this.bidirectional || this.right == node;
	}
	
	public Node getLeft() {
		return this.left;
	}
	
	public Node getRight() {
		return this.right;
	}
	
	@Nullable
	public Node getOther(Node node) {
		if(this.left == node) {
			return this.right;
		} else if(this.right == node) {
			return this.left;
		}
		return null;
	}
	
	public Graph getGraph() {
		return this.left.getGraph();
	}
	
	public int getID() {
		return this.id;
	}
	
	public String getType() {
		return this.type;
	}
	
	public boolean isSameDirection(Node node, Edge edge, Node edgeNode) {
		return this.isBidirectional() == edge.isBidirectional() && (this.isBidirectional() || this.isFrom(node) == edge.isFrom(edgeNode));
	}
	
	public boolean isSameType(Edge edge) {
		return (this.type == null && edge.type == null) || (this.type != null && this.type.equals(edge.type));
	}
}
