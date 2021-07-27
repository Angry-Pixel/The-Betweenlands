package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Grammar {
	public static class Builder {
		private final List<Rule> rules = new ArrayList<>();

		private Builder() { }

		public Builder rule(int weight, Consumer<Graph> lhs, Consumer<Graph> rhs) {
			Graph glhs = new Graph();
			lhs.accept(glhs);
			Graph grhs = new Graph();
			rhs.accept(grhs);
			this.rules.add(new Rule(glhs, grhs, weight));
			return this;
		}

		public Grammar build() {
			Grammar grammar = new Grammar();
			for(Rule rule : this.rules) {
				grammar.addRule(rule);
			}
			return grammar;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Grammar single(Rule rule) {
		Grammar grammar = new Grammar();
		grammar.addRule(rule);
		return grammar;
	}

	private final List<Rule> rules = new ArrayList<>();

	public Grammar addRule(Rule rule) {
		this.rules.add(rule);
		return this;
	}

	public List<Rule> getRules() {
		return this.rules;
	}

	public static class Match {
		private final Rule rule;
		private final Graph graph;
		private final Map<Node, Node> lhs2graph;
		private final Map<Node, Node> graph2lhs;

		private Match(Rule rule, Graph graph, Node lhsNode, Node graphNode) {
			this.rule = rule;
			this.graph = graph;
			this.lhs2graph = new HashMap<>();
			this.lhs2graph.put(lhsNode, graphNode);
			this.graph2lhs = new HashMap<>();
			this.graph2lhs.put(graphNode, lhsNode);
		}

		private Match(Match parent, Node lhsNode, Node graphNode) {
			this.rule = parent.rule;
			this.graph = parent.graph;
			this.lhs2graph = new HashMap<>(parent.lhs2graph);
			this.lhs2graph.put(lhsNode, graphNode);
			this.graph2lhs = new HashMap<>(parent.graph2lhs);
			this.graph2lhs.put(graphNode, lhsNode);
		}

		private void visit(Node lhsNode, Node graphNode) {
			this.lhs2graph.put(lhsNode, graphNode);
			this.graph2lhs.put(graphNode, lhsNode);
		}

		private boolean hasVisited(Node lhsNode) {
			return this.lhs2graph.containsKey(lhsNode);
		}

		private boolean isFinished() {
			return this.lhs2graph.size() == this.rule.getLHS().getNodes().size();
		}

		private boolean isFullMatch() {
			if(this.isFinished()) {
				//Check whether all LHS edges have a matching edge in the graph
				for(Node lhsNode : this.rule.getLHS().getNodes()) {
					for(Edge lhsEdge : lhsNode.getEdges()) {
						Node graphLeftNode = this.getGraphNode(lhsEdge.getLeft());
						Node graphRightNode = this.getGraphNode(lhsEdge.getRight());

						Edge graphEdge = graphLeftNode.getEdge(graphRightNode);

						if(graphEdge == null) {
							return false;
						}

						if(graphEdge.isBidirectional() != lhsEdge.isBidirectional()) {
							return false;
						}

						if(!graphEdge.isBidirectional() && graphEdge.getLeft() != graphLeftNode) {
							return false;
						}

						if(!graphEdge.isSameType(lhsEdge)) {
							return false;
						}
					}
				}

				return true;
			}

			return false;
		}

		public Rule getRule() {
			return this.rule;
		}

		public Node getGraphNode(Node lhsNode) {
			return this.lhs2graph.get(lhsNode);
		}

		public Node getLHSNode(Node graphNode) {
			return this.graph2lhs.get(graphNode);
		}

		public boolean apply() {
			Graph lhs = this.rule.getLHS();
			Graph rhs = this.rule.getRHS();

			//Graph must contain all nodes that matched the rule's LHS
			for(Node ruleLhsNode : lhs.getNodes()) {
				Node graphLhsNode = this.getGraphNode(ruleLhsNode);

				if(graphLhsNode == null || !this.graph.getNodes().contains(graphLhsNode)) {
					return false;
				}
			}

			Map<Node, Node> merge = this.graph.merge(rhs);

			//Reconnect edges and remove old nodes
			for(Node ruleLhsNode : lhs.getNodes()) {
				Node graphLhsNode = this.getGraphNode(ruleLhsNode);

				//Get tagged RHS node(s). May be up to 2 nodes to allow splitting a single tagged LHS node into two tagged RHS nodes.
				String tag = ruleLhsNode.getTag();
				List<Node> ruleRhsNodes = tag != null ? rhs.getNodesByTag(tag) : Collections.emptyList();
				Node graphRhsNode1 = null;
				Node graphRhsNode2 = null;
				int i = 0;
				for(Node rhsNode : ruleRhsNodes) {
					if(i == 0) {
						graphRhsNode1 = merge.get(rhsNode);
					} else if(i == 1) {
						graphRhsNode2 = merge.get(rhsNode);
					} else {
						throw new IllegalStateException("RHS can have at most 2 nodes with the same tag '" + tag + "'");
					}
					i++;
				}

				if(graphRhsNode1 != null) {
					//Copy edges of the tagged LHS node to the tagged RHS node(s)

					for(Edge edge : graphLhsNode.getEdges()) {
						Node connectedNode = edge.getOther(graphLhsNode);

						//Ignore connections to nodes matched by the rule's LHS
						//because the LHS will be completely replaced by the RHS
						if(this.getLHSNode(connectedNode) == null) {
							if(connectedNode == edge.getLeft()) {
								connectedNode.connect(graphRhsNode1, edge.getType(), edge.isBidirectional());
							} else {
								if(graphRhsNode2 != null) {
									//Connect outgoing edges to second node with same tag, if available
									//(allows splitting one node into multiple nodes)
									graphRhsNode2.connect(connectedNode, edge.getType(), edge.isBidirectional());
								} else {
									graphRhsNode1.connect(connectedNode, edge.getType(), edge.isBidirectional());
								}
							}
						}
					}
				}

				//Remove all old nodes that matched the rule's LHS since
				//they were replaced by the rule's RHS
				this.graph.removeNode(graphLhsNode);
			}

			return true;
		}
	}

	public LinkedHashMap<Node, List<Match>> match(Graph graph) {
		LinkedHashMap<Node, List<Match>> matches = new LinkedHashMap<>();

		for(Node node : graph.getNodes()) {
			List<Match> nodeMatches = this.match(node);
			if(!nodeMatches.isEmpty()) {
				matches.put(node, nodeMatches);
			}
		}

		return matches;
	}

	public List<Match> match(Node node) {
		Graph graph = node.getGraph();
		Node graphNode = node;

		List<Match> matches = new ArrayList<>();

		for(Rule rule : this.rules) {
			for(Node lhsNode : rule.getLHS().getNodesByType(graphNode.getType())) {
				Match match = new Match(rule, graph, lhsNode, graphNode);

				if(match.isFinished()) {
					if(match.isFullMatch()) {
						matches.add(match);
					}
				} else if(this.traverse(lhsNode, graphNode, match) == 1) {
					matches.add(match);
				}
			}
		}

		return matches;
	}

	private int traverse(Node lhsNode, Node graphNode, Match match) {
		for(Edge lhsEdge : lhsNode.getEdges()) {
			Node lhsNeighbor = lhsEdge.getOther(lhsNode);

			if(!match.hasVisited(lhsNeighbor)) {
				for(Edge graphEdge : graphNode.getEdges()) {
					if(lhsEdge.isSameType(graphEdge) && lhsEdge.isSameDirection(lhsNode, graphEdge, graphNode)) {
						Node graphNeighbor = graphEdge.getOther(graphNode);

						if(lhsNeighbor.isSameType(graphNeighbor)) {
							match.visit(lhsNeighbor, graphNeighbor);

							if(match.isFinished()) {
								if(match.isFullMatch()) {
									return 1;
								} else {
									return 0;
								}
							}

							int result = this.traverse(lhsNeighbor, graphNeighbor, match);
							if(result >= 0) {
								return result;
							}
						}
					}
				}
			}
		}

		return -1;
	}
}
