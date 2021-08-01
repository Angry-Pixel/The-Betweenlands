package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.Iterator;
import java.util.LinkedHashSet;

import com.google.common.base.MoreObjects;

public class Substitution {
	final Rule rule;
	final LinkedHashSet<Node> nodes = new LinkedHashSet<>();

	Substitution(Rule rule) {
		this.rule = rule;
	}

	public Rule getRule() {
		return this.rule;
	}

	public LinkedHashSet<Node> getNodes() {
		return this.nodes;
	}

	public Iterable<Node> getSourceNodes() {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {
				Iterator<Node> it = Substitution.this.nodes.iterator();
				return new Iterator<Node>() {
					private Node next;

					{
						this.next = this.findNext();
					}

					private Node findNext() {
						while(it.hasNext()) {
							Node next = it.next();
							if(Substitution.this.isSourceOf(next)) {
								return next;
							}
						}
						return null;
					}

					@Override
					public boolean hasNext() {
						return this.next != null;
					}

					@Override
					public Node next() {
						Node curr = this.next;
						this.next = this.findNext();
						return curr;
					}
				};
			}
		};
	}

	public boolean isSourceOf(Node node) {
		return node.getSourceSubstitution() == this;
	}

	@Override
	public String toString() {
		StringBuilder sourceStr = new StringBuilder();
		StringBuilder otherStr = new StringBuilder();
		for(Node node : this.nodes) {
			if(this.isSourceOf(node)) {
				sourceStr.append(node.toString());
				sourceStr.append(", ");
			} else {
				otherStr.append(node.toString());
				otherStr.append(", ");
			}
		}
		return MoreObjects.toStringHelper(this).add("rule", this.rule).add("src_nodes", "[" + sourceStr.substring(0, Math.max(0, sourceStr.length() - 2)) + "]").add("other_nodes", "[" + otherStr.substring(0, Math.max(0, otherStr.length() - 2)) + "]").toString();
	}
}
