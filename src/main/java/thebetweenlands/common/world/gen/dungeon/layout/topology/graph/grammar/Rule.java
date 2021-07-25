package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

public class Rule {
	private final Graph lhs;
	private final Graph rhs;
	private final int weight;	

	public Rule(Graph lhs, Graph rhs, int weight) {
		this.lhs = lhs;
		this.rhs = rhs;
		this.weight = weight;

		for(String tag : lhs.getTags()) {
			if(lhs.getNodesByTag(tag).size() > 1) {
				throw new IllegalStateException("LHS can have at most 1 node with the same tag '" + tag + "'");
			}
		}

		for(Node node : rhs.getNodes()) {
			String tag = node.getTag();
			if(tag != null && this.lhs.getNodesByTag(tag).isEmpty()) {
				throw new IllegalStateException("RHS contains tag '" + tag + "' that does not exist in LHS");
			}
		}

		for(String tag : rhs.getTags()) {
			if(rhs.getNodesByTag(tag).size() > 2) {
				throw new IllegalStateException("RHS can have at most 2 nodes with the same tag '" + tag + "'");
			}
		}
	}

	public Graph getLHS() {
		return this.lhs;
	}

	public Graph getRHS() {
		return this.rhs;
	}

	public int getWeight() {
		return this.weight;
	}
}
