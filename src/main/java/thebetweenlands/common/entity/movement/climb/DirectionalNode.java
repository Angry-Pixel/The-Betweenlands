package thebetweenlands.common.entity.movement.climb;

import net.minecraft.core.Direction;
import net.minecraft.world.level.pathfinder.Node;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DirectionalNode extends Node {
	protected static final long ALL_DIRECTIONS = ObstructionAwareNodeEvaluator.packDirection(Direction.UP, ObstructionAwareNodeEvaluator.packDirection(Direction.DOWN, ObstructionAwareNodeEvaluator.packDirection(Direction.NORTH, ObstructionAwareNodeEvaluator.packDirection(Direction.EAST, ObstructionAwareNodeEvaluator.packDirection(Direction.SOUTH, ObstructionAwareNodeEvaluator.packDirection(Direction.WEST, 0L))))));

	protected static final Direction[] DIRECTIONS = Direction.values();

	private final Direction[] pathableSides;
	@Nullable
	private final Direction pathSide;

	private final boolean isDrop;

	public DirectionalNode(int x, int y, int z, long packed, boolean isDrop) {
		super(x, y, z);

		EnumSet<Direction> directionsSet = EnumSet.noneOf(Direction.class);
		for (Direction dir : DIRECTIONS) {
			if (ObstructionAwareNodeEvaluator.unpackDirection(dir, packed)) {
				directionsSet.add(dir);
			}
		}

		this.pathableSides = directionsSet.toArray(new Direction[0]);
		this.pathSide = null;

		this.isDrop = isDrop;
	}

	public DirectionalNode(Node point, long packed, boolean isDrop) {
		this(point.x, point.y, point.z, packed, isDrop);

		this.heapIdx = point.heapIdx;
		this.g = point.g;
		this.h = point.h;
		this.f = point.f;
		this.cameFrom = point.cameFrom;
		this.closed = point.closed;
		this.walkedDistance = point.walkedDistance;
		this.costMalus = point.costMalus;
		this.type = point.type;
	}

	public DirectionalNode(Node point) {
		this(point, ALL_DIRECTIONS, false);
	}

	private DirectionalNode(int x, int y, int z, Direction[] pathableSides, Direction pathSide, boolean isDrop) {
		super(x, y, z);

		this.pathableSides = new Direction[pathableSides.length];
		System.arraycopy(pathableSides, 0, this.pathableSides, 0, pathableSides.length);

		this.pathSide = pathSide;

		this.isDrop = isDrop;
	}

	public DirectionalNode(Node point, Direction pathSide) {
		super(point.x, point.y, point.z);

		this.heapIdx = point.heapIdx;
		this.g = point.g;
		this.h = point.h;
		this.f = point.f;
		this.cameFrom = point.cameFrom;
		this.closed = point.closed;
		this.walkedDistance = point.walkedDistance;
		this.costMalus = point.costMalus;
		this.type = point.type;

		if(point instanceof DirectionalNode node) {

			this.pathableSides = new Direction[node.pathableSides.length];
			System.arraycopy(node.pathableSides, 0, this.pathableSides, 0, node.pathableSides.length);

			this.isDrop = node.isDrop;
		} else {
			this.pathableSides = Direction.values();

			this.isDrop = false;
		}

		this.pathSide = pathSide;
	}

	public DirectionalNode assignPathSide(Direction pathDirection) {
		return new DirectionalNode(this, pathDirection);
	}

	@Override
	public Node cloneAndMove(int x, int y, int z) {
		Node pathPoint = new DirectionalNode(x, y, z, this.pathableSides, this.pathSide, this.isDrop);
		pathPoint.heapIdx = this.heapIdx;
		pathPoint.g = this.g;
		pathPoint.h = this.h;
		pathPoint.f = this.f;
		pathPoint.cameFrom = this.cameFrom;
		pathPoint.closed = this.closed;
		pathPoint.walkedDistance = this.walkedDistance;
		pathPoint.costMalus = this.costMalus;
		pathPoint.type = this.type;
		return pathPoint;
	}

	public Direction[] getPathableSides() {
		return this.pathableSides;
	}

	@Nullable
	public Direction getPathSide() {
		return this.pathSide;
	}

	public boolean isDrop() {
		return this.isDrop;
	}
}
