package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

public class SourceSubstitutionPattern {
	public static class Builder {
		private String hub = null;
		private final Map<String, Integer> pattern = new HashMap<>();
		
		private Builder() { }

		public Builder hub(String type) {
			this.hub = type;
			return this;
		}
		
		public Builder contains(String type, int count) {
			this.pattern.put(type, count);
			return this;
		}

		public Builder contains(String type) {
			return this.contains(type, 1);
		}

		public SourceSubstitutionPattern build() {
			return new SourceSubstitutionPattern(this.hub, this.pattern);
		}
	}

	private final String hub;
	private final Map<String, Integer> pattern;

	private SourceSubstitutionPattern(String hub, Map<String, Integer> pattern) {
		this.hub = hub;
		this.pattern = pattern;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Pattern {
		private final String hub;
		private final Map<String, List<Node>> pattern;
		
		private Pattern(String hub, Map<String, List<Node>> pattern) {
			this.hub = hub;
			this.pattern = pattern;
		}
		
		public Set<String> getTypes() {
			return this.pattern.keySet();
		}
		
		@Nullable
		public String getHubType() {
			return this.hub;
		}
		
		public List<Node> getNodes(String type) {
			List<Node> nodes = this.pattern.get(type);
			if(nodes == null) {
				return Collections.emptyList();
			}
			return nodes;
		}

		public List<Node> getNodes() {
			List<Node> nodes = new ArrayList<>();
			for(String type : this.getTypes()) {
				nodes.addAll(this.getNodes(type));
			}
			return nodes;
		}
	}

	public List<Pattern> find(Graph graph) {
		if(this.pattern.isEmpty()) {
			return Collections.emptyList();
		}

		String searchType = this.pattern.keySet().iterator().next();

		List<Node> nodes = graph.getNodesByType(searchType);

		List<Pattern> matches = new ArrayList<>();

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
				matches.add(new Pattern(this.hub, currentMatch));
			}
		}

		return matches;
	}
}
