package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class SourceSubstitutionPattern {
	public static class Builder {
		private final Map<String, Integer> pattern = new HashMap<>();

		private Builder() { }

		public Builder with(String type, int count) {
			this.pattern.put(type, count);
			return this;
		}

		public Builder with(String type) {
			return this.with(type, 1);
		}

		public SourceSubstitutionPattern build() {
			return new SourceSubstitutionPattern(this.pattern);
		}
	}

	private final Map<String, Integer> pattern;

	private SourceSubstitutionPattern(Map<String, Integer> pattern) {
		this.pattern = pattern;
	}

	public static Builder builder() {
		return new Builder();
	}

	public List<Map<String, List<Node>>> find(Graph graph) {
		if(this.pattern.isEmpty()) {
			return Collections.emptyList();
		}

		String searchType = this.pattern.keySet().iterator().next();

		List<Node> nodes = graph.getNodesByType(searchType);

		List<Map<String, List<Node>>> matches = new ArrayList<>();

		Set<Node> checked = new HashSet<>();

		for(Node node : nodes) {
			if(checked.contains(node)) {
				continue;
			}

			Map<String, List<Node>> currentMatch = new HashMap<>();

			Substitution source = node.getSourceSubstitution();

			for(Node sourceNode : source.getSourceNodes()) {
				checked.add(sourceNode);

				String type = sourceNode.getType();

				if(this.pattern.containsKey(type)) {
					List<Node> matchingNodes = currentMatch.get(type);
					if(matchingNodes == null) {
						currentMatch.put(type, matchingNodes = new ArrayList<>());
					}
					matchingNodes.add(sourceNode);
				}
			}

			boolean fullMatch = true;

			for(Entry<String, Integer> requirement : this.pattern.entrySet()) {
				int requiredCount = requirement.getValue();
				int count = currentMatch.getOrDefault(requirement.getKey(), Collections.emptyList()).size();

				if(count != requiredCount) {
					fullMatch = false;	
					break;
				}
			}

			if(fullMatch) {
				matches.add(currentMatch);
			}
		}

		return matches;
	}
}
