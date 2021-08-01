package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

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

	@Override
	public String toString() {
		ToStringHelper str = MoreObjects.toStringHelper(this);
		StringBuilder lhs = new StringBuilder();
		for(Node node : this.lhs.getNodes()) {
			String tag = node.getTag();
			lhs.append(node.getType());
			if(tag != null) {
				lhs.append(" (");
				lhs.append(tag);
				lhs.append(")");
			}
			lhs.append(", ");
		}
		StringBuilder rhs = new StringBuilder();
		for(Node node : this.rhs.getNodes()) {
			String tag = node.getTag();
			rhs.append(node.getType());
			if(tag != null) {
				rhs.append(" (");
				rhs.append(tag);
				rhs.append(")");
			}
			rhs.append(", ");
		}
		return str.add("lhs", lhs.substring(0, Math.max(0, lhs.length() - 2))).add("rhs", rhs.substring(0, Math.max(0, rhs.length() - 2))).add("weight", this.weight).toString();
	}
}
