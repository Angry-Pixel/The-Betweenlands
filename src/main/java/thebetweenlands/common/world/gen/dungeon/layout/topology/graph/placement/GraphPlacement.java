package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Edge;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort.GroupedGraph;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort.GroupedGraph.Group;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.GridNode;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.PlacementStrategy.Placement;

public class GraphPlacement {
	private static class Context {
		private final GroupedGraph graph;
		private final PlacementStrategy placementStrategy;
		private final Random rng;
		private final GraphNodeGrid grid;
		private final Map<Node, Group> nodeGroups;
		private final Map<Edge, Group> edgeGroups;

		private final Map<Node, SpaceInstance> spaceCache = new HashMap<>();

		private Context(GroupedGraph graph, PlacementStrategy placementStrategy, Random rng,
				GraphNodeGrid grid, Map<Node, Group> nodeGroups, Map<Edge, Group> edgeGroups) {
			this.graph = graph;
			this.placementStrategy = placementStrategy;
			this.rng = rng;
			this.grid = grid;
			this.nodeGroups = nodeGroups;
			this.edgeGroups = edgeGroups;
		}
	}

	/*
	 * When placing a node, all other nodes in the same group must also be placed precautionarily ("reserved").
	 * Only then should regular placement resume/proceed to the next node. If next node was already placed/reserved,
	 * it can be skipped.
	 * 
	 * Space push node: reserves a certain space for the following nodes until the corresponding space pop node.
	 * Placement of following nodes must take place in the reserved space unless not possible (in which case
	 * regular placement takes over). There can be different "space" implementations, e.g., loop space which
	 * creates a loop back to some previous nodes (with a one-way connection at the end?).
	 * 
	 * Placement strategies: decide where to place next node (can pick any feasible place, but is given reserved
	 * spaces which take priority). E.g.: pick closest possible place to the predecessor (i.e. previous node in
	 * sorted graph, not previously placed node), i.e., a BFS type search starting at predecessor (this prioritizes
	 * expanding linearly or at already explored places, such that the player is likely to have already seen that
	 * path before backtracking), or better yet, an A* search such that certain nodes/edges can have different
	 * costs (e.g. to give floor changes a large penalty).
	 */
	public static GraphNodeGrid generate(GroupedGraph graph, PlacementStrategy placementStrategy, Random rng) {
		GraphNodeGrid grid = new GraphNodeGrid();

		Map<Node, Group> nodeGroups = graph.getNodeGroups();
		Map<Edge, Group> edgeGroups = graph.getEdgeGroups();

		Stack<SpaceInstance> spaces = new Stack<>();

		Context ctx = new Context(graph, placementStrategy, rng, grid, nodeGroups, edgeGroups);

		for(Node graphNode : graph.getNodes()) {
			GridNode node = grid.get(graphNode);

			if(node == null) {
				Group group = nodeGroups.get(graphNode);

				if(group != null) {
					generateGroupNodes(ctx, graphNode.getID(), group, spaces);

					node = grid.get(graphNode);
					if(node == null) {
						throw new IllegalStateException();
					}

					node.setReservedSpace(false, false);
					if(!spaces.isEmpty()) {
						node.confirmSpaceTags(spaces.peek());
					}
				} else {
					Placement placement = findPlacement(ctx, graphNode, null, getPredecessor(ctx, graphNode, null), false, spaces);

					handleSpace(ctx, graphNode, placement, spaces, true);

					node = placeNode(ctx, placement, graphNode, null, spaces);

					node.setReservedSpace(false, false);
					if(!spaces.isEmpty()) {
						node.confirmSpaceTags(spaces.peek());
					}

					updateSpaces(ctx, node, spaces);
				}
			} else if(node.isReservedSpace()) {
				Pos source = node.getSourcePos();
				if(source == null) {
					throw new IllegalStateException();
				}

				IGridNodeHandle sourceNode = grid.get(source);
				if(sourceNode == null) {
					throw new IllegalStateException();
				}

				Pos pos = node.getPos();
				Direction offset = Direction.of(pos.x - source.x, pos.y - source.y, pos.z - source.z);
				if(offset == null) {
					throw new IllegalStateException();
				}

				Direction connection = node.getSourceConnection();

				handleSpace(ctx, graphNode, new Placement(sourceNode, offset, connection), spaces, true);

				node.setReservedSpace(false, false);
				if(!spaces.isEmpty()) {
					node.confirmSpaceTags(spaces.peek());
				}
			} else {
				throw new IllegalStateException();
			}
		}

		grid.removeReservedNodes();

		return grid;
	}

	@Nullable
	private static GridNode getPredecessor(Context ctx, Node graphNode, @Nullable GroupedGraph.Group group) {
		//If grouped then try to find the direct predecessor in
		//the graph and return that if it already exists in order
		//to enforce the same structure as in the graph
		if(group != null) {

			//Find earliest (w.r.t. the topological sort) neighbor
			//in the graph
			Node parentGroupNode = null;
			for(Node groupNode : group.getNodes()) {
				Edge edge = groupNode.getEdge(graphNode);
				if(edge != null && edge.isFrom(groupNode) && ctx.edgeGroups.get(edge) == group) {
					if(parentGroupNode == null || groupNode.getID() < parentGroupNode.getID()) {
						parentGroupNode = groupNode;
					}
				}
			}

			//And return the corresponding GridNode if it exists
			//(should always be the case)
			if(parentGroupNode != null) {
				GridNode node = ctx.grid.get(parentGroupNode);
				if(node != null) {
					return node;
				} else {
					throw new IllegalStateException();
				}
			}
		}

		//Otherwise, just return the predecessor in the topological
		//sort, since the structure needn't be enforced
		List<Node> nodes = graphNode.getGraph().getNodes();
		int id = graphNode.getID();
		if(id > 0 && id - 1 < nodes.size()) {
			Node previous = nodes.get(id - 1);
			return ctx.grid.get(previous);
		}

		return null;
	}

	private static Placement generateGroupNodes(Context ctx, int index, GroupedGraph.Group group, Stack<SpaceInstance> spaces) {
		/*Stack<SpaceInstance> groupSpaces = new Stack<>();
		groupSpaces.addAll(spaces);

		int generated = 0;
		for(Node graphNode : ctx.graph.getNodes()) {
			//Must handle spaces for all nodes, even those not in the group,
			//because some of the group's nodes could be after these nodes
			//in the topological sort
			if(graphNode.getID() >= index) {
				handleSpace(ctx, graphNode, groupSpaces);
			}

			Group graphNodeGroup = ctx.nodeGroups.get(graphNode);
			if(graphNodeGroup == group) {
				//The first node may be placed freely because it is only connected
				//via non-grouped edges. The nodes afterwards must be structured
				//like in the graph.
				boolean structured = generated > 0;
				GridNode predecessor = getPredecessor(ctx, graphNode, group);
				GridNode node = placeNode(ctx, graphNode, group, predecessor, structured, groupSpaces);
				node.setReservedSpace(true);

				generated++;
			}

			if(generated >= group.getNodes().size()) {
				break;
			}
		}

		if(groupSpaces.size() > spaces.size()) {
			throw new IllegalStateException();
		}*/

		Stack<SpaceInstance> groupSpaces = new Stack<>();
		groupSpaces.addAll(spaces);

		Placement firstPlacement = null;

		for(Node graphNode : group.getNodes()) {
			boolean isFirstNode = firstPlacement == null;

			boolean structured = graphNode.getID() == index;

			GridNode predecessor = getPredecessor(ctx, graphNode, group);

			Placement placement = findPlacement(ctx, graphNode, group, predecessor, structured, groupSpaces);
			if(firstPlacement == null) {
				firstPlacement = placement;
			}

			if(handleSpace(ctx, graphNode, placement, groupSpaces, isFirstNode) && isFirstNode) {
				spaces.push(groupSpaces.peek());
			}

			GridNode node = placeNode(ctx, placement, graphNode, group, groupSpaces);

			node.setReservedSpace(true, false);
			if(!groupSpaces.isEmpty()) {
				node.confirmSpaceTags(groupSpaces.peek());
			}

			updateSpaces(ctx, node, groupSpaces);
		}

		return firstPlacement;
	}

	public static class SpaceInstance {
		private final SpaceType.Instance type;
		private final String tag;

		private Set<Pos> positions;

		private SpaceInstance(SpaceType.Instance type) {
			this.type = type;
			this.tag = type.getTag();
		}

		public SpaceType.Instance getType() {
			return this.type;
		}

		public Set<Pos> getPositions() {
			return Collections.unmodifiableSet(this.positions);
		}

		@Nullable
		public String getTag() {
			return this.tag;
		}
	}

	private static boolean isSpacePopNode(Node graphNode) {
		return graphNode.getType().equals("r1e") || graphNode.getType().equals("r2e"); //TODO Debug
	}

	private static SpaceType.Instance getNodeSpaceType(Node graphNode) {
		//TODO Debug
		if(graphNode.getType().equals("r1s")) {
			return new RegionSpaceType().init("region_1");
		} else if(graphNode.getType().equals("r2s")) {
			return new RegionSpaceType().init("region_2");
		}
		return null;
	}

	@Nullable
	private static boolean handleSpace(Context ctx, Node graphNode, @Nullable Placement placement, Stack<SpaceInstance> spaces, boolean finishSpaces) {
		if(isSpacePopNode(graphNode)) {
			if(spaces.isEmpty()) {
				throw new IllegalStateException();
			} else {
				SpaceInstance space = spaces.pop();

				if(finishSpaces) {
					//TODO Does this actually work? The finish method is supposed to finalize the region. E.g., the loop space might
					//fill in the remaining cells of the loop with tiles.
					space.type.finish(new INodeGrid() {
						@Override
						public GridNode set(Pos pos, Node graphNode, int index) {
							if(space.positions.contains(pos)) {
								GridNode node = ctx.grid.get(pos);
								if(node == null || node.getGraphNode() == null) {
									return ctx.grid.set(pos, graphNode, index);
								}
							}
							throw new IllegalStateException();
						}

						@Override
						public void remove(Pos pos) {
							GridNode existing = ctx.grid.get(pos);
							if(existing != null) {
								if(existing.getGraphNode() == null && space.positions.contains(pos)) {
									ctx.grid.remove(existing);
								} else {
									throw new IllegalStateException();
								}
							}
						}

						@Override
						public void remove(Node graphNode) {
							GridNode existing = ctx.grid.get(graphNode);
							if(existing != null) {
								if(existing.getGraphNode() == null && space.positions.contains(existing.getPos())) {
									ctx.grid.remove(existing);
								} else {
									throw new IllegalStateException();
								}
							}
						}

						@Override
						public void remove(IGridNodeHandle node) {
							if(node != null) {
								if(node.getGraphNode() == null && space.positions.contains(node.getPos())) {
									ctx.grid.remove(node);
								} else {
									throw new IllegalStateException();
								}
							}
						}

						@Override
						public Collection<? extends IGridNodeHandle> get() {
							return ctx.grid.get();
						}

						@Override
						public IGridNodeHandle get(Node graphNode) {
							return ctx.grid.get(graphNode);
						}

						@Override
						public IGridNodeHandle get(Pos pos) {
							return ctx.grid.get(pos);
						}
					});
				}

				ctx.grid.removeSpace(space);

				updateSpaces(ctx, null, spaces);
			}
		} else {
			SpaceInstance cachedInstance = ctx.spaceCache.get(graphNode);

			if(cachedInstance != null) {
				spaces.push(cachedInstance);
			} else {
				if(placement == null) {
					throw new IllegalStateException();
				}

				SpaceType.Instance type = getNodeSpaceType(graphNode);

				if(type != null) {
					SpaceInstance space = new SpaceInstance(type);

					if(generateSpace(ctx, space, graphNode, placement)) {
						ctx.spaceCache.put(graphNode, space);
						spaces.push(space);
						return true;
					}
				}
			}
		}

		return false;
	}

	private static Placement findPlacement(Context ctx, Node graphNode, Group group, @Nullable GridNode start, boolean structured, Stack<SpaceInstance> spaces) {
		if(start == null) {
			Pos pos = new Pos(0, 0, 0);

			GridNode node = ctx.grid.get(pos);
			if(node == null || node.isReservedSpace()) {
				return Placement.ORIGIN;
			} else {
				throw new IllegalStateException();
			}
		}

		SpaceInstance spaceInstance = spaces.isEmpty() ? null : spaces.peek();

		//Validate placement, i.e., either no node must exist yet or the
		//existing node must be a reserved node and it must have been
		//generated by the current space
		BiPredicate<IGridNodeHandle, Pos> predicate = (parent, newPos) -> {
			//TODO Validate group placement?

			if(parent == null) {
				return false;
			}

			if(parent.getIndex() > graphNode.getID()) {
				return false;
			}

			GridNode existing = ctx.grid.get(newPos);
			if(existing != null && (existing.getGraphNode() != null || !existing.isReservedSpace() || spaces.isEmpty() || !existing.getSpaces().contains(spaces.peek()))) {
				return false;
			}

			return true;
		};

		Placement placement = ctx.placementStrategy.findPlacement(start, graphNode, graphNode.getID(), ctx.grid, new PlacementContext(ctx.grid, structured, group, spaceInstance, predicate), ctx.rng);

		if(placement == Placement.ORIGIN || !predicate.test(placement.source, placement.getPos())) {
			throw new IllegalStateException();
		}

		return placement;
	}

	private static GridNode placeNode(Context ctx, Placement placement, Node graphNode, Group group, Stack<SpaceInstance> spaces) {
		GridNode node;

		if(placement == Placement.ORIGIN) {
			node = ctx.grid.set(new Pos(0, 0, 0), graphNode, graphNode.getID());

			node.setGroup(group);
		} else {
			Pos sourcePos = placement.source.getPos();
			Pos newPos = new Pos(sourcePos.x + placement.offset.x, sourcePos.y + placement.offset.y, sourcePos.z + placement.offset.z);

			node = ctx.grid.set(newPos, graphNode, graphNode.getID());

			node.setSourcePos(sourcePos, placement.connection);
			node.setGroup(group);
		}

		return node;
	}

	private static boolean generateSpace(Context ctx, SpaceInstance space, Node graphNode, @Nullable Placement placement) {
		Set<Pair<Pos, Boolean>> reservations = new HashSet<>();

		boolean generated = space.getType().generate(ctx.grid, placement, new ISpaceReservations() {
			@Override
			public boolean reserve(Pos pos, boolean exclusive) {
				if(this.canReserve(pos)) {
					reservations.add(Pair.of(pos, exclusive));
					return true;
				}
				return false;
			}

			@Override
			public boolean canReserve(Pos pos) {
				GridNode node = ctx.grid.get(pos);
				if(node == null || node.getSpaces().contains(space) || ((node.getGraphNode() == null || node.getGraphNode() == graphNode) && node.isReservedSpace() && !node.isExclusiveSpace())) {
					return true;
				}
				return false;
			}
		}, ctx.rng); 

		if(generated) {
			space.positions = new HashSet<>();
			for(Pair<Pos, Boolean> reservation : reservations) {
				space.positions.add(reservation.getLeft());
				ctx.grid.reserveSpace(reservation.getLeft(), space, reservation.getRight());
			}
		} else {
			space.positions = Collections.emptySet();
		}

		return generated;
	}

	private static void updateSpaces(Context ctx, @Nullable IGridNodeHandle placedNode, Stack<SpaceInstance> spaces) {
		if(!spaces.isEmpty()) {
			SpaceInstance space = spaces.peek();

			if(space.getType().requiresRefresh(ctx.grid, placedNode)) {
				ctx.grid.removeSpace(space);

				generateSpace(ctx, space, null, null);
			}
		}
	}
}
