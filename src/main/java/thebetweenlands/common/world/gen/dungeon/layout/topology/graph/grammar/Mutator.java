package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Grammar.Match;

public class Mutator {
	public static class Builder {
		private final List<Function<Graph, Grammar>> mutations = new ArrayList<>();

		private Builder() { }

		public Builder addMutation(Grammar grammar, boolean infinite) {
			if(infinite) {
				this.mutations.add(new Function<Graph, Grammar>() {
					private boolean first = true;
					@Override
					public Grammar apply(Graph graph) {
						if(this.first || graph.isMutated()) {
							this.first = false;
							return grammar;
						} else {
							return null;
						}
					}
				});
			} else {
				this.mutations.add(graph -> grammar);
			}
			return this;
		}

		public Builder addMutation(Grammar grammar, int n) {
			this.mutations.add(new Function<Graph, Grammar>() {
				private int count = n;
				@Override
				public Grammar apply(Graph graph) {
					if((this.count == n || graph.isMutated()) && this.count-- > 0) {
						return grammar;
					} else {
						return null;
					}
				}
			});
			return this;
		}

		public Builder addMutation(Function<Graph, Grammar> mutation) {
			this.mutations.add(mutation);
			return this;
		}

		public Mutator build() {
			Mutator mutator = new Mutator();
			for(Function<Graph, Grammar> mutation : this.mutations) {
				mutator.addMutation(mutation);
			}
			return mutator;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private final List<Function<Graph, Grammar>> mutations = new ArrayList<>();

	public Mutator addMutation(Function<Graph, Grammar> mutation) {
		this.mutations.add(mutation);
		return this;
	}

	public int mutate(Graph graph, Random rng, int max) {
		int n = 0;

		graph.setMutated(false);

		for(Function<Graph, Grammar> mutation : this.mutations) {
			Grammar grammar;
			while((grammar = mutation.apply(graph)) != null) {
				n++;

				if(n >= max) {
					return max;
				}

				graph.setMutated(false);

				LinkedHashMap<Node, List<Match>> graphMatches = grammar.match(graph);

				List<Node> matchedNodes = new ArrayList<>(graphMatches.keySet());
				Collections.shuffle(matchedNodes, rng);

				for(Node node : matchedNodes) {
					List<Match> matches = graphMatches.get(node);

					//TODO Instead of random shuffle it should use the rule weights
					//to select matches randomly
					Collections.shuffle(matches, rng);

					for(Match match : matches) {
						if(match.apply()) {
							graph.setMutated(true);
						}
					}
				}
			}
		}

		graph.setMutated(false);

		return n;
	}
}
