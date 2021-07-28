package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nullable;

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
			this.graph2lhs = new HashMap<>();
			this.visit(lhsNode, graphNode);
		}

		private Match(Match parent, Node lhsNode, Node graphNode) {
			this.rule = parent.rule;
			this.graph = parent.graph;
			this.lhs2graph = new HashMap<>(parent.lhs2graph);
			this.graph2lhs = new HashMap<>(parent.graph2lhs);
			this.visit(lhsNode, graphNode);
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
		return this.match(graph, false, false, false, null);
	}

	public LinkedHashMap<Node, List<Match>> match(Graph graph, boolean firstMatchPerGraph, boolean firstMatchPerNode, boolean firstMatchPerRule, @Nullable Random rng) {
		LinkedHashMap<Node, List<Match>> matches = new LinkedHashMap<>();

		for(Node node : randomIterable(graph.getNodes(), rng)) {
			List<Match> nodeMatches = this.match(node, firstMatchPerGraph || firstMatchPerNode, firstMatchPerGraph || firstMatchPerRule, rng);
			if(!nodeMatches.isEmpty()) {
				matches.put(node, nodeMatches);

				if(firstMatchPerGraph) {
					return matches;
				}
			}
		}

		return matches;
	}

	public List<Match> match(Node node, boolean firstMatchPerNode, boolean firstMatchPerRule, @Nullable Random rng) {
		Graph graph = node.getGraph();
		Node graphNode = node;

		List<Match> matches = new ArrayList<>();

		for(Rule rule : randomIterable(this.rules, rng)) {
			for(Node lhsNode : randomIterable(rule.getLHS().getNodesByType(graphNode.getType()), rng)) {
				Match match = new Match(rule, graph, lhsNode, graphNode);

				if(match.isFinished()) {
					if(match.isFullMatch()) {
						matches.add(match);

						if(firstMatchPerNode) {
							return matches;
						}
					}
				} else if(this.traverse(matches, lhsNode, graphNode, match, firstMatchPerNode || firstMatchPerRule, rng)) {
					if(firstMatchPerNode) {
						return matches;
					}
				}
			}
		}

		return matches;
	}

	private boolean traverse(List<Match> matches, Node lhsNode, Node graphNode, Match match, boolean firstMatchPerRule, @Nullable Random rng) {
		for(Edge lhsEdge : randomIterable(lhsNode.getEdges(), rng)) {
			Node lhsNeighbor = lhsEdge.getOther(lhsNode);

			if(!match.hasVisited(lhsNeighbor)) {
				for(Edge graphEdge : randomIterable(graphNode.getEdges(), rng)) {
					if(lhsEdge.isSameType(graphEdge) && lhsEdge.isSameDirection(lhsNode, graphEdge, graphNode)) {
						Node graphNeighbor = graphEdge.getOther(graphNode);

						if(lhsNeighbor.isSameType(graphNeighbor)) {
							Match newMatch;
							if(firstMatchPerRule) {
								newMatch = match;
								newMatch.visit(lhsNeighbor, graphNeighbor);
							} else {
								newMatch = new Match(match, lhsNeighbor, graphNeighbor);
							}

							if(newMatch.isFinished()) {
								if(newMatch.isFullMatch()) {
									matches.add(newMatch);

									if(firstMatchPerRule) {
										return true;
									}
								}
							} else if(this.traverse(matches, lhsNeighbor, graphNeighbor, newMatch, firstMatchPerRule, rng)) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private static <T, F extends List<T>> Iterable<T> randomIterable(F list, @Nullable Random rng) {
		final int n = list.size();
		if(rng == null || n <= 1) {
			return list;
		} else {
			return new Iterable<T>() {
				@Override
				public Iterator<T> iterator() {
					return new Iterator<T>() {
						/*
						 * 1. m and c need to be relatively prime.
						 * 2. a - 1 is divisible by all prime factors of m.
						 * 3. a - 1 is divisible by 4, if m is also divisible by 4.
						 * Implements a full period LCG, i.e., it returns every number
						 * between 0 and m (exclusive) exactly once before looping.
						 */

						private static final int c = 1013904223;
						private static final int a = 1664525;
						private final int seed, m;
						private int next;
						private boolean hasNext;

						{
							if(n > 0) {
								this.hasNext = true;
								//Set m to smallest power of 2 larger or equal n so
								//that condition 2 holds
								int i = n - 1;
								i = i | i >> 1;
								i = i | i >> 2;
								i = i | i >> 4;
								i = i | i >> 8;
								i = i | i >> 16;
								this.m = i + 1;
								this.next = this.seed = rng.nextInt(n);
							} else {
								this.hasNext = false;
								this.m = 1;
								this.next = this.seed = 0;
							}
						}

						@Override
						public boolean hasNext() {
							return this.hasNext;
						}

						@Override
						public T next() {
							final int m = this.m;
							int next = this.next;
							do {
								next = (a * next + c) % m;
							} while(next >= n);
							if(next == this.seed) {
								this.hasNext = false;
							}
							this.next = next;
							return list.get(next);
						}
					};
				}
			};
		}
	}
}
