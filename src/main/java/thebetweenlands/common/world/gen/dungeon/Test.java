package thebetweenlands.common.world.gen.dungeon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import thebetweenlands.common.world.gen.dungeon.layout.LayoutGenerator;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.LoggingCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PathPercentageCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.PhaseLimitCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.criteria.RetryCriterion;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Grid;
import thebetweenlands.common.world.gen.dungeon.layout.grid.Link;
import thebetweenlands.common.world.gen.dungeon.layout.pathfinder.SimplePathfinder;
import thebetweenlands.common.world.gen.dungeon.layout.postprocessor.CompactionPostprocessor;
import thebetweenlands.common.world.gen.dungeon.layout.topology.Topology;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.GraphTopology;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.GraphTopologyMeta;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Grammar;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Graph;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.GraphPrinter;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Mutator;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.SourceSubstitutionPattern;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.SourceSubstitutionPattern.Pattern;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Substitution;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort.GroupedGraph;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.GridNode;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphPlacement;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.WeightedPlacementStrategy;

public class Test {
	public static Test TEST = new Test();

	public Grid grid = new Grid(new Random(), 16);

	public Topology<GraphTopologyMeta> topology;
	public CompactionPostprocessor postprocessor = new CompactionPostprocessor();
	public SimplePathfinder pathfinder = new SimplePathfinder();

	public List<Pattern> patterns = new ArrayList<>();
	
	private boolean isShrinking = false;
	private int counter = 0;

	public static Link invalidLink = null;

	public void init() {
		System.out.println("---------------------------- Generate ----------------------------");

		this.patterns.clear();
		
		this.topology = new GraphTopology(this.graph());
		
		this.isShrinking = false;
		this.counter = 0;

		this.grid = new Grid(new Random(), 8);

		this.isShrinking = false;
		boolean generated = LayoutGenerator.sequence()
		.watchdog(() -> new PhaseLimitCriterion(9))
		.watchdog(() -> new LoggingCriterion())
		.topology(() -> this.topology)
		.criterion(() -> new RetryCriterion(3, false, true))
		.postprocessor(() -> this.postprocessor)
		.criterion(() -> new RetryCriterion(3, false, true))
		.pathfinder(() -> this.pathfinder)
		.criterion(() -> new PathPercentageCriterion(0.2f, 3, true))
		.finish()
		.generate(this.grid, new Random());
		System.out.println("Generated: " + generated);
	}

	public void step() {
		this.grid.clearMeta(this.pathfinder);

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);*/

		/*this.isShrinking = false;
		this.grid.resolve(new Random());
		this.postprocessor.init(this.grid, new Random(), num -> 0);
		this.postprocessor.process();*/

		/*if(!this.isShrinking) {
			if(!this.grid.resolveIteration(new Random())) {
				this.isShrinking = true;
				this.counter = 0;
			}
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.shrinkStep(new Random(), 1));
		}*/

		if(!this.isShrinking) {
			this.grid.resolve();
			this.isShrinking = true;
			this.counter = 0;
		} else {
			if(this.counter == 0) this.postprocessor.init(this.grid, new Random(), num -> 0);
			System.out.println((++this.counter) + " " + this.postprocessor.compactIteration(new Random(), 1));
			if(this.counter > 20) {
				this.isShrinking = false;
			}
		}

		/*if(!this.isShrinking) {
			this.grid.resolve(new Random());
			this.isShrinking = true;
		} else {
			this.postprocessor.init(this.grid, new Random(), num -> 0);
			this.postprocessor.process();
			this.isShrinking = false;
		}*/
	}

	public GraphNodeGrid graph() {
		Graph graph = new Graph();
		graph.addNode("S"); //Axiom

		Grammar grammar = Grammar.builder()
				//Start rule
				.rule(1, lhs -> {
					lhs.addNode("S");
				}, rhs -> {
					Node e = rhs.addNode("e");
					Node C = rhs.addNode("C");
					Node G = rhs.addNode("G");
					Node bm = rhs.addNode("bm");
					Node iq = rhs.addNode("iq");
					Node ti = rhs.addNode("ti");
					Node CF = rhs.addNode("CF");
					Node g = rhs.addNode("g");


					Node n = rhs.addNode("n");
					Node r1s = rhs.addNode("r1s");
					Node r1e = rhs.addNode("r1e");
					Node C2 = rhs.addNode("C");
					Node G2 = rhs.addNode("G");
					Node ib1 = rhs.addNode("ib");
					Node r2s = rhs.addNode("r2s");
					Node r2e = rhs.addNode("r2e");
					Node C3 = rhs.addNode("C");
					Node G3 = rhs.addNode("G");
					Node ib2 = rhs.addNode("ib");

					e.chain(n).chain(C, "double").chain(G).chain(bm, "double")
					.chain(iq, "double").chain(ti, "double")
					.chain(CF, "double").chain(g);
					n.chain(r1s).chain(r1e, "double");
					n.chain(r2s).chain(r2e, "double");
					r1s.chain(C2, "double").chain(G2).chain(ib1, "double").chain(r1e);
					r2s.chain(C3, "double").chain(G3).chain(ib2, "double").chain(r2e);

					/*e.chain(C).chain(G).chain(bm, "double")
					.chain(iq, "double").chain(ti, "double")
					.chain(CF, "double").chain(g);*/
				})
				//Create Final Chain
				.rule(1, lhs -> {
					Node CF = lhs.addNode("CF", "start");
					Node g = lhs.addNode("g", "end");
					CF.connect(g);
				}, rhs -> {
					Node C = rhs.addNode("C", "start");
					Node H1 = rhs.addNode("H");
					Node G = rhs.addNode("G");
					Node lf = rhs.addNode("lf");
					Node bl = rhs.addNode("bl");
					Node g = rhs.addNode("g", "end");
					Node t = rhs.addNode("t");
					Node kf = rhs.addNode("kf");
					Node H2 = rhs.addNode("H");

					C.connect(H1);
					C.chain(G).chain(lf, "double")
					.chain(bl, "double").chain(g, "double");
					C.chain(t).chain(kf, "double").chain(lf);
					t.connect(H2);
				})
				//Create Linear Chain 1
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double");
				})
				//Create Linear Chain 2
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL");
					Node CL4 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double")
					.chain(CL4, "double");
				})
				//Create Linear Chain 3
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");
					C.connect(G);
				}, rhs -> {
					Node CL1 = rhs.addNode("CL", "start");
					Node CL2 = rhs.addNode("CL");
					Node CL3 = rhs.addNode("CL");
					Node CL4 = rhs.addNode("CL");
					Node CL5 = rhs.addNode("CL", "end");

					CL1.chain(CL2, "double").chain(CL3, "double")
					.chain(CL4, "double").chain(CL5, "double");
				})
				//Create Linear Chain 4
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					rhs.addNode("t", "start");
				})
				//Create Linear Chain 5
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					Node t1 = rhs.addNode("t", "start");
					Node t2 = rhs.addNode("t");
					Node ib = rhs.addNode("ib", "start");

					t1.chain(t2, "double").chain(ib, "double");
				})
				//Create Linear Chain 6
				.rule(1, lhs -> {
					lhs.addNode("CL", "start");
				}, rhs -> {
					rhs.addNode("ts", "start");
				})
				//Create Linear Chain 7
				.rule(1, lhs -> {
					Node CL1 = lhs.addNode("CL", "start");
					Node CL2 = lhs.addNode("CL", "end");

					CL1.connect(CL2, "double");
				}, rhs -> {
					Node k = rhs.addNode("k", "start");
					Node l = rhs.addNode("l", "end");

					k.connect(l, "double");
				})
				//Create Linear Chain 8
				.rule(1, lhs -> {
					Node CL1 = lhs.addNode("CL", "start");
					Node CL2 = lhs.addNode("CL", "end");

					CL1.connect(CL2, "double");
				}, rhs -> {
					Node k = rhs.addNode("k", "start");
					Node l = rhs.addNode("l");
					Node CL = rhs.addNode("CL", "end");

					k.chain(l, "double").chain(CL, "double");
				})
				//Resolve hook 1
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					rhs.addNode("n", "start");
				})
				//Resolve hook 2
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					Node t = rhs.addNode("t", "start");
					Node ib = rhs.addNode("ib", "start");

					t.connect(ib, "double");
				})
				//Resolve hook 3
				.rule(1, lhs -> {
					lhs.addNode("H", "start");
				}, rhs -> {
					Node ts = rhs.addNode("ts", "start");
					Node ib = rhs.addNode("ib", "start");

					ts.connect(ib, "double");
				})
				//Create Parallel Chain 1
				.rule(1, lhs -> {
					Node C = lhs.addNode("C", "start");
					Node G = lhs.addNode("G", "end");

					C.connect(G);
				}, rhs -> {
					Node CP = rhs.addNode("CP", "start");
					Node G = rhs.addNode("G", "end");

					CP.connect(G);
				})
				//Create Parallel Chain 2
				.rule(1, lhs -> {
					Node CP = lhs.addNode("CP", "start");
					Node G = lhs.addNode("G", "end");

					CP.connect(G);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node km1 = rhs.addNode("km");
					Node km2 = rhs.addNode("km");
					Node km3 = rhs.addNode("km");
					Node lm = rhs.addNode("lm", "end");

					F.chain(km1).chain(lm);
					F.chain(km2).chain(lm);
					F.chain(km3).chain(lm);
				})
				//Create Parallel Chain 3
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node k = rhs.addNode("k");
					Node l = rhs.addNode("l");
					Node km = rhs.addNode("km", "end");
					Node H = rhs.addNode("H");

					F.chain(k).chain(l).chain(km, "double");
					l.connect(H, "double");
				})
				//Create Parallel Chain 4
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node t = rhs.addNode("t");
					Node km = rhs.addNode("km", "end");

					F.chain(t).chain(km, "double");
				})
				//Create Parallel Chain 5
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node km = lhs.addNode("km", "end");

					F.connect(km);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node ts = rhs.addNode("ts");
					Node km = rhs.addNode("km", "end");

					F.chain(ts).chain(km, "double");
				})
				//Create Parallel Chain 6
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node k = lhs.addNode("k", "end");

					F.connect(k);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node t = rhs.addNode("t");
					Node k = rhs.addNode("k", "end");

					F.chain(t).chain(k, "double");
				})
				//Create Parallel Chain 7
				.rule(1, lhs -> {
					Node F = lhs.addNode("F", "start");
					Node k = lhs.addNode("k", "end");

					F.connect(k);
				}, rhs -> {
					Node F = rhs.addNode("F", "start");
					Node ts = rhs.addNode("ts");
					Node k = rhs.addNode("k", "end");

					F.chain(ts).chain(k, "double");
				})
				//Create Parallel Chain 8
				.rule(1, lhs -> {
					lhs.addNode("F", "start");
				}, rhs -> {
					Node n = rhs.addNode("n", "start");
					Node H1 = rhs.addNode("H", "start");
					Node H2 = rhs.addNode("H");

					n.connect(H1);
					n.connect(H2);
				})
				.build();

		Mutator mutator = Mutator.builder()
				.addMutation(grammar, true)
				.build();

		long seed = new Random().nextLong();

		//seed = -3882572050713031964L;
		//seed = -2220811479062544325L;
		//seed = -8981807003221640430L;
		//seed = 1824453356624892194L;
		//seed = 4864012680087479409L;
		//seed = -1374954073690921752L;
		//seed = 195479986408876174L;

		long start = System.nanoTime();
		int n = mutator.mutate(graph, new Random(seed), 50);

		/*graph = new Graph();
		Node a = graph.addNode("a");
		Node b = graph.addNode("b");
		Node c = graph.addNode("c");
		Node d = graph.addNode("d");
		Node e = graph.addNode("e");
		Node f = graph.addNode("f");
		Node g = graph.addNode("g");
		Node h = graph.addNode("h");
		Node i = graph.addNode("i");
		Node j = graph.addNode("j");
		Node k = graph.addNode("k");
		Node l = graph.addNode("l");

		a.chain(a, "double").chain(b, "double").chain(f, "double").chain(k, "double");
		a.chain(e, "double").chain(l, "double");
		b.connect(d);
		d.connect(j, "double");
		j.connect(l);
		d.connect(g);
		g.connect(h, "double");
		h.connect(j);
		c.connect(i, "double");
		c.connect(g);
		h.connect(i);*/

		/*graph = new Graph();
		Node a = graph.addNode("a");
		Node b = graph.addNode("b");
		Node c = graph.addNode("c");
		Node d = graph.addNode("d");
		Node e = graph.addNode("e");
		Node f = graph.addNode("f");
		Node g = graph.addNode("g");
		Node h = graph.addNode("h");

		a.chain(b).chain(e, "double").chain(h);
		a.chain(c, "double").chain(f, "double").chain(h, "double");
		a.chain(d).chain(g, "double").chain(h);*/

		System.out.println("Seed: " + seed);
		System.out.println("Run time: " + (((System.nanoTime() - start) % 10000000000L) / 1000000.0f) + "ms");
		System.out.println("Mutated in: " + n + " steps");
		System.out.println("Substitutions:");
		for(Node node : graph.getNodes()) {
			Substitution sub = node.getSourceSubstitution();
			if(sub != null) {
				System.out.println(node.toString() + ": " + sub.hashCode() + " " + sub);
			}
		}
		System.out.println("Graph nodes: " + graph.getNodes().size());
		System.out.println("Graph: \n" + GraphPrinter.toEdgeListString(graph));
		System.out.println("Tree: \n" + GraphPrinter.toSpanningTreeString(graph.getNodesByType("e").get(0)));

		try {
			start = System.nanoTime();

			TopologicalSort.Properties properties = new TopologicalSort.Properties();
			properties.groupingPredicate = edge -> edge.getType().equals("double");
			properties.nodeWeight.put(node -> node.getType().equals("g"), 100);
			properties.groupSizeWeight = -1;

			GroupedGraph sorted = TopologicalSort.sort(graph, properties);

			System.out.println("Sort time: " + (((System.nanoTime() - start) % 10000000000L) / 1000000.0f) + "ms");
			System.out.println("Sorted nodes: " + sorted.getNodes().size());
			System.out.println("Groups:");
			for(GroupedGraph.Group group : new HashSet<>(sorted.getNodeGroups().values())) {
				System.out.println(group);
			}
			System.out.println("Node order:");
			for(Node node : sorted.getNodes()) {
				System.out.println(node.getType() + " (" + node.getID() + ")");
			}

			start = System.nanoTime();
			GraphNodeGrid nodeGrid = GraphPlacement.generate(sorted, new WeightedPlacementStrategy(), new Random(seed));
			System.out.println("Placement time: " + (((System.nanoTime() - start) % 10000000000L) / 1000000.0f) + "ms");
			System.out.println("Placed nodes: " + nodeGrid.get().size());
			for(GridNode gridNode : nodeGrid.get()) {
				String str = gridNode.getGraphNode().toString();
				if(!gridNode.getSpaceTags().isEmpty()) {
					str += " (";
					str += gridNode.getPrimarySpaceTag() + "; ";
					for(String tag : gridNode.getSpaceTags()) {
						str += tag + ", ";
					}
					str = str.substring(0, str.length() - 2);
					str += ")";
				}
				System.out.println(str);
			}
			
			this.patterns.addAll(SourceSubstitutionPattern.builder().hub("lm").contains("lm").contains("km", 3).build().find(sorted));
			this.patterns.addAll(SourceSubstitutionPattern.builder().hub("l").contains("l").contains("k").build().find(sorted));
			this.patterns.addAll(SourceSubstitutionPattern.builder().hub("lf").contains("lf").contains("kf").build().find(sorted));
			System.out.println("Patterns: " + this.patterns.size());

			return nodeGrid;
		} catch(Throwable ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
