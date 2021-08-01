package thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import javax.annotation.Nullable;

import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.grammar.TopologicalSort.GroupedGraph.Group;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.IGridNodeHandle;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphNodeGrid.Pos;
import thebetweenlands.common.world.gen.dungeon.layout.topology.graph.placement.GraphPlacement.SpaceInstance;

public class PlacementContext {
	private final GraphNodeGrid grid;
	
	private final boolean structured;
	private final Group group;
	private final SpaceInstance spaceInstance;
	private final BiPredicate<IGridNodeHandle, Pos> predicate;

	PlacementContext(GraphNodeGrid grid, boolean structured, @Nullable Group group, @Nullable SpaceInstance space, BiPredicate<IGridNodeHandle, Pos> predicate) {
		this.grid = grid;
		this.structured = structured;
		this.group = group;
		this.spaceInstance = space;
		this.predicate = predicate;
	}

	public boolean isStructured() {
		return this.structured;
	}

	@Nullable
	public Group getGraphGroup() {
		return this.group;
	}

	public SpaceInstance getSpaceInstance() {
		return this.spaceInstance;
	}

	public boolean isInGenerationSpace(IGridNodeHandle source, Pos pos) {
		IGridNodeHandle existing = this.grid.get(pos);
		return (existing == null || (existing.isReservedSpace() && existing.getGraphNode() == null)) && (this.spaceInstance == null || this.spaceInstance.getPositions().contains(pos)) && this.predicate.test(source, pos);
	}

	public static interface Traverser {
		public boolean traverse(IGridNodeHandle parent, Pos pos);
	}

	public void traverse(Pos start, Traverser traverser) {
		IGridNodeHandle node = this.grid.get(start);

		if(node != null) {
			
		}
	}
}
