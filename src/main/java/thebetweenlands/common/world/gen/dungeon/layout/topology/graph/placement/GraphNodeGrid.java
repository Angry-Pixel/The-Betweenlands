package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.grid.Direction;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.Node;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort.GroupedGraph.Group;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphPlacement.SpaceInstance;

public class GraphNodeGrid implements INodeGridHandle, INodeGrid {
	public static class Pos {
		public final int x, y, z;

		public Pos(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + this.x;
			result = prime * result + this.y;
			result = prime * result + this.z;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null || this.getClass() != obj.getClass()) {
				return false;
			}
			Pos other = (Pos) obj;
			if(this.x != other.x || this.y != other.y || this.z != other.z) {
				return false;
			}
			return true;
		}
	}

	public static interface IGridNodeHandle {
		@Nullable
		public Pos getSourcePos();

		@Nullable
		public Direction getSourceConnection();

		public Pos getPos();

		@Nullable
		public Node getGraphNode();

		@Nullable
		public Group getGraphGroup();

		@Nullable
		public Set<SpaceInstance> getSpaces();

		@Nullable
		public String getPrimarySpaceTag();

		public Set<String> getSpaceTags();

		public boolean isSpaceTagged(String tag);

		public int getIndex();

		public boolean isReservedSpace();

		public boolean isExclusiveSpace();
	}

	public static class GridNode implements IGridNodeHandle {
		private final Pos pos;

		private Node graphNode;
		private int index = Integer.MAX_VALUE;

		private Set<SpaceInstance> spaces = new LinkedHashSet<>();

		private String primarySpaceTag = null;
		private Set<String> definitiveSpaceTags = new HashSet<>();

		private Group graphGroup;
		private Pos sourcePos;
		private Direction sourceConnection;
		private boolean reserved;
		private boolean exclusive;

		private GridNode(Pos pos) {
			this.pos = pos;
		}

		@Override
		public Pos getSourcePos() {
			return this.sourcePos;
		}

		@Override
		public Direction getSourceConnection() {
			return this.sourceConnection;
		}

		@Override
		public Pos getPos() {
			return this.pos;
		}

		@Override
		@Nullable
		public Node getGraphNode() {
			return this.graphNode;
		}

		@Override
		public Group getGraphGroup() {
			return this.graphGroup;
		}

		@Override
		public Set<SpaceInstance> getSpaces() {
			return this.spaces;
		}

		@Override
		public String getPrimarySpaceTag() {
			return this.primarySpaceTag;
		}

		@Override
		public Set<String> getSpaceTags() {
			Set<String> tags = new HashSet<>(this.definitiveSpaceTags);
			for(SpaceInstance space : this.spaces) {
				String tag = space.getTag();
				if(tag != null) {
					tags.add(tag);
				}
			}
			return tags;
		}

		@Override
		public boolean isSpaceTagged(String tag) {
			if(this.definitiveSpaceTags.contains(tag)) {
				return true;
			}
			for(SpaceInstance space : this.spaces) {
				return tag.equals(space.getTag());
			}
			return false;
		}

		@Override
		public int getIndex() {
			return this.index;
		}

		@Override
		public boolean isReservedSpace() {
			return this.reserved;
		}

		@Override
		public boolean isExclusiveSpace() {
			return this.exclusive;
		}

		void confirmSpaceTags(@Nullable SpaceInstance primarySpace) {
			if(primarySpace != null) {
				String tag = primarySpace.getTag();
				if(tag != null) {
					this.definitiveSpaceTags.add(tag);
					this.primarySpaceTag = tag;
				}
			}
			for(SpaceInstance space : this.spaces) {
				String tag = space.getTag();
				if(tag != null) {
					this.definitiveSpaceTags.add(tag);
				}
			}
		}

		boolean removeSpace(SpaceInstance space) {
			return this.spaces.remove(space);
		}

		boolean hasSpaces() {
			return !this.spaces.isEmpty();
		}

		void addSpace(SpaceInstance space) {
			this.spaces.add(space);
		}

		void setNode(Node graphNode, int index) {
			this.graphNode = graphNode;
			this.index = index;
		}

		void setGroup(@Nullable Group group) {
			this.graphGroup = group;
		}

		void setReservedSpace(boolean reserved, boolean exclusive) {
			this.reserved = reserved;
			if(!reserved) {
				this.exclusive = false;
			} else {
				this.exclusive |= exclusive;
			}
		}

		void setSourcePos(@Nullable Pos pos, @Nullable Direction connection) {
			if((pos != null) != (connection != null)) {
				throw new IllegalStateException();
			}
			this.sourcePos = pos;
			this.sourceConnection = connection;
		}
	}

	private final Map<Pos, GridNode> pos2gn = new LinkedHashMap<>();
	private final Map<Node, GridNode> n2gn = new LinkedHashMap<>();

	void removeReservedNodes() {
		Iterator<GridNode> nodes = this.pos2gn.values().iterator();
		while(nodes.hasNext()) {
			GridNode node = nodes.next();
			if(node.isReservedSpace()) {
				nodes.remove();
				this.n2gn.remove(node.getGraphNode());
			}
		}
	}

	@Override
	public GridNode set(int x, int y, int z, Node node, int index) {
		return this.set(new Pos(x, y, z), node, index);
	}

	@Override
	public GridNode set(Pos pos, Node graphNode, int index) {
		GridNode gridNode = this.get(pos);
		if(gridNode == null) {
			gridNode = new GridNode(pos);
		} else if(!gridNode.isReservedSpace()) {
			throw new IllegalStateException();
		}

		gridNode.setNode(graphNode, index);

		this.pos2gn.put(pos, gridNode);
		this.n2gn.put(graphNode, gridNode);

		return gridNode;
	}

	GridNode reserveSpace(Pos pos, SpaceInstance space, boolean exclusive) {
		GridNode gridNode = this.get(pos);
		if(gridNode == null) {
			gridNode = new GridNode(pos);
		} else if(!gridNode.isReservedSpace()) {
			throw new IllegalStateException();
		}

		gridNode.addSpace(space);
		gridNode.setReservedSpace(true, exclusive);

		this.pos2gn.put(pos, gridNode);

		return gridNode;
	}

	void removeSpace(SpaceInstance space) {
		for(Pos pos : space.getPositions()) {
			this.removeSpace(pos, space);
		}
	}

	void removeSpace(Pos pos, SpaceInstance space) {
		GridNode gridNode = this.get(pos);
		if(gridNode != null && gridNode.removeSpace(space) && !gridNode.hasSpaces() && gridNode.getGraphNode() == null) {
			this.remove(gridNode);
		}
	}

	@Override
	public void remove(Pos pos) {
		GridNode node = this.get(pos);
		if(node != null) {
			this.remove(node);
		}
	}

	@Override
	public void remove(Node graphNode) {
		GridNode node = this.get(graphNode);
		if(node != null) {
			this.remove(node);
		}
	}

	@Override
	public void remove(IGridNodeHandle node) {
		this.pos2gn.remove(node.getPos());
		this.n2gn.remove(node.getGraphNode());
	}

	@Override
	@Nullable
	public GridNode get(Pos pos) {
		return this.pos2gn.get(pos);
	}

	@Override
	@Nullable
	public GridNode get(Node graphNode) {
		return this.n2gn.get(graphNode);
	}

	@Override
	public Collection<GridNode> get() {
		return this.pos2gn.values();
	}

	private static long hash(long x) {
		x += (x << 10);
		x ^= (x >> 6);
		x += (x << 3);
		x ^= (x >> 11);
		x += (x << 15);
		return x;
	}

	public static float randomFloat(int x, int y, int z, int seed) {
		long m = hash(seed ^ hash(x) ^ hash(y) ^ hash(z));
		m &= 0x007FFFFFL;
		m |= 0x3F800000L;
		return Float.intBitsToFloat((int)m) - 1.0f;
	}
}
