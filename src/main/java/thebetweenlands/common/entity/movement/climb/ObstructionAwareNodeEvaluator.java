package thebetweenlands.common.entity.movement.climb;

import java.util.EnumSet;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;

public class ObstructionAwareNodeEvaluator extends WalkNodeEvaluator {

	protected static final PathType[] PATH_NODE_TYPES = PathType.values();
	protected static final Direction[] DIRECTIONS = Direction.values();

	protected static final Vec3i PX = new Vec3i(1, 0, 0);
	protected static final Vec3i NX = new Vec3i(-1, 0, 0);
	protected static final Vec3i PY = new Vec3i(0, 1, 0);
	protected static final Vec3i NY = new Vec3i(0, -1, 0);
	protected static final Vec3i PZ = new Vec3i(0, 0, 1);
	protected static final Vec3i NZ = new Vec3i(0, 0, -1);

	protected static final Vec3i PXPY = new Vec3i(1, 1, 0);
	protected static final Vec3i NXPY = new Vec3i(-1, 1, 0);
	protected static final Vec3i PXNY = new Vec3i(1, -1, 0);
	protected static final Vec3i NXNY = new Vec3i(-1, -1, 0);

	protected static final Vec3i PXPZ = new Vec3i(1, 0, 1);
	protected static final Vec3i NXPZ = new Vec3i(-1, 0, 1);
	protected static final Vec3i PXNZ = new Vec3i(1, 0, -1);
	protected static final Vec3i NXNZ = new Vec3i(-1, 0, -1);

	protected static final Vec3i PYPZ = new Vec3i(0, 1, 1);
	protected static final Vec3i NYPZ = new Vec3i(0, -1, 1);
	protected static final Vec3i PYNZ = new Vec3i(0, 1, -1);
	protected static final Vec3i NYNZ = new Vec3i(0, -1, -1);

	protected PathObstructionAwareEntity entity;
	protected boolean startFromGround = true;
	protected boolean checkObstructions;
	protected int pathingSizeOffsetX, pathingSizeOffsetY, pathingSizeOffsetZ;
	protected EnumSet<Direction> pathableFacings = EnumSet.of(Direction.DOWN);
	protected Direction[] pathableFacingsArray;

	private final Long2LongMap pathNodeTypeCache = new Long2LongOpenHashMap();
	private final Long2ObjectMap<PathType> rawPathNodeTypeCache = new Long2ObjectOpenHashMap<>();
	private final Object2BooleanMap<AABB> aabbCollisionCache = new Object2BooleanOpenHashMap<>();

	protected boolean alwaysAllowDiagonals = true;

	public void setStartPathOnGround(boolean startFromGround) {
		this.startFromGround = startFromGround;
	}

	public void setCheckObstructions(boolean checkObstructions) {
		this.checkObstructions = checkObstructions;
	}

	public void setCanPathWalls(boolean canPathWalls) {
		if (canPathWalls) {
			this.pathableFacings.add(Direction.NORTH);
			this.pathableFacings.add(Direction.EAST);
			this.pathableFacings.add(Direction.SOUTH);
			this.pathableFacings.add(Direction.WEST);
		} else {
			this.pathableFacings.remove(Direction.NORTH);
			this.pathableFacings.remove(Direction.EAST);
			this.pathableFacings.remove(Direction.SOUTH);
			this.pathableFacings.remove(Direction.WEST);
		}
	}

	public void setCanPathCeiling(boolean canPathCeiling) {
		if (canPathCeiling) {
			this.pathableFacings.add(Direction.UP);
		} else {
			this.pathableFacings.remove(Direction.UP);
		}
	}

	@Override
	public void prepare(PathNavigationRegion region, Mob mob) {
		super.prepare(region, mob);

		if (mob instanceof PathObstructionAwareEntity entity) {
			this.entity = entity;
		} else {
			throw new IllegalArgumentException("Only mobs that extend " + PathObstructionAwareEntity.class.getSimpleName() + " are supported. Received: " + mob.getClass().getName());
		}

		this.pathingSizeOffsetX = Math.max(1, Mth.floor(this.mob.getBbWidth() / 2.0f + 1));
		this.pathingSizeOffsetY = Math.max(1, Mth.floor(this.mob.getBbHeight() + 1));
		this.pathingSizeOffsetZ = Math.max(1, Mth.floor(this.mob.getBbWidth() / 2.0f + 1));

		this.pathableFacingsArray = this.pathableFacings.toArray(new Direction[0]);
	}

	@Override
	public void done() {
		super.done();
		this.pathNodeTypeCache.clear();
		this.rawPathNodeTypeCache.clear();
		this.aabbCollisionCache.clear();
		this.entity.pathFinderCleanup();
	}

	private boolean checkAabbCollision(AABB aabb) {
		return this.aabbCollisionCache.computeIfAbsent(aabb, (p_237237_2_) -> !this.currentContext.level().noCollision(this.mob, aabb));
	}

	@Override
	public Node getStart() {
		double x = this.mob.getX();
		double y = this.mob.getY();
		double z = this.mob.getZ();

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		int by = Mth.floor(y);

		BlockState state = this.currentContext.getBlockState(checkPos.set(x, by, z));

		if (!this.mob.canStandOnFluid(state.getFluidState())) {
			if (this.canFloat() && this.mob.isInWater()) {
				while (true) {
					if (state.getBlock() != Blocks.WATER && state.getFluidState() != Fluids.WATER.getSource(false)) {
						--by;
						break;
					}

					++by;
					state = this.currentContext.getBlockState(checkPos.set(x, by, z));
				}
			} else if (this.mob.onGround() || !this.startFromGround) {
				by = Mth.floor(y + Math.min(0.5D, Math.max(this.mob.getBbHeight() - 0.1f, 0.0D)));
			} else {
				BlockPos blockpos;
				for (blockpos = this.mob.blockPosition(); (this.currentContext.getBlockState(blockpos).isAir() || this.currentContext.getBlockState(blockpos).isPathfindable(PathComputationType.LAND)) && blockpos.getY() > 0; blockpos = blockpos.below()) {
				}

				by = blockpos.above().getY();
			}
		} else {
			while (this.mob.canStandOnFluid(state.getFluidState())) {
				++by;
				state = this.currentContext.getBlockState(checkPos.set(x, by, z));
			}

			--by;
		}

		final BlockPos initialStartPos = BlockPos.containing(x, by, z);
		BlockPos startPos = initialStartPos;

		long packed = this.removeNonStartingSides(this.getDirectionalPathNodeTypeCached(this.mob, startPos.getX(), startPos.getY(), startPos.getZ()));
		DirectionalNode startPathPoint = this.openPoint(startPos.getX(), startPos.getY(), startPos.getZ(), packed, false);
		startPathPoint.type = unpackNodeType(packed);
		startPathPoint.costMalus = this.mob.getPathfindingMalus(startPathPoint.type);

		startPos = this.findSuitableStartingPosition(startPos, startPathPoint);

		if (!initialStartPos.equals(startPos)) {
			packed = this.removeNonStartingSides(this.getDirectionalPathNodeTypeCached(this.mob, startPos.getX(), startPos.getY(), startPos.getZ()));
			startPathPoint = this.openPoint(startPos.getX(), startPos.getY(), startPos.getZ(), packed, false);
			startPathPoint.type = unpackNodeType(packed);
			startPathPoint.costMalus = this.mob.getPathfindingMalus(startPathPoint.type);
		}

		if (this.mob.getPathfindingMalus(startPathPoint.type) < 0.0F) {
			AABB aabb = this.mob.getBoundingBox();

			if (this.isSafeStartingPosition(checkPos.set(aabb.minX, by, aabb.minZ)) || this.isSafeStartingPosition(checkPos.set(aabb.minX, by, aabb.maxZ)) || this.isSafeStartingPosition(checkPos.set(aabb.maxX, by, aabb.minZ)) || this.isSafeStartingPosition(checkPos.set(aabb.maxX, by, aabb.maxZ))) {
				packed = this.removeNonStartingSides(this.getDirectionalPathNodeTypeCached(this.mob, checkPos.getX(), checkPos.getY(), checkPos.getZ()));
				startPathPoint = this.openPoint(checkPos.getX(), checkPos.getY(), checkPos.getZ(), packed, false);
				startPathPoint.type = unpackNodeType(packed);
				startPathPoint.costMalus = this.mob.getPathfindingMalus(startPathPoint.type);
			}
		}

		return startPathPoint;
	}

	private long removeNonStartingSides(long packed) {
		long newPacked = packed & ~0xFFFFFFFFL;

		for (Direction side : DIRECTIONS) {
			if (unpackDirection(side, packed) && this.isValidStartingSide(side)) {
				newPacked = packDirection(side, newPacked);
			}
		}

		return newPacked;
	}

	protected boolean isValidStartingSide(Direction side) {
		Direction groundSide = this.entity.getGroundSide();
		return side == groundSide || side.getAxis() != groundSide.getAxis();
	}

	protected BlockPos findSuitableStartingPosition(BlockPos pos, DirectionalNode startPathPoint) {
		if (startPathPoint.getPathableSides().length == 0) {
			Direction avoidedOffset = this.entity.getGroundSide().getOpposite();

			for (int xo = -1; xo <= 1; xo++) {
				for (int yo = -1; yo <= 1; yo++) {
					for (int zo = -1; zo <= 1; zo++) {
						if (xo != avoidedOffset.getStepX() && yo != avoidedOffset.getStepY() && zo != avoidedOffset.getStepZ()) {
							BlockPos offsetPos = pos.offset(xo, yo, zo);

							long packed = this.getDirectionalPathNodeTypeCached(this.mob, offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
							PathType nodeType = unpackNodeType(packed);

							if (nodeType == PathType.WALKABLE && unpackDirection(packed)) {
								return offsetPos;
							}
						}
					}
				}
			}
		}

		return pos;
	}

	private boolean isSafeStartingPosition(BlockPos pos) {
		PathType pathnodetype = unpackNodeType(this.getDirectionalPathNodeTypeCached(this.mob, pos.getX(), pos.getY(), pos.getZ()));
		return this.mob.getPathfindingMalus(pathnodetype) >= 0.0F;
	}

	private boolean allowDiagonalPathOptions(Node[] options) {
		return this.alwaysAllowDiagonals || options.length == 0 || (options[0] == null || options[0].type == PathType.OPEN || options[0].costMalus != 0.0F) && (options.length <= 1 || options[1] == null || options[1].type == PathType.OPEN || options[1].costMalus != 0.0F);
	}

	@Override
	public int getNeighbors(Node[] pathOptions, Node currentPointIn) {
		DirectionalNode currentPoint;
		if (currentPointIn instanceof DirectionalNode) {
			currentPoint = (DirectionalNode) currentPointIn;
		} else {
			currentPoint = new DirectionalNode(currentPointIn);
		}

		int openedNodeCount = 0;
		int stepHeight = 0;

		PathType nodeTypeAbove = unpackNodeType(this.getDirectionalPathNodeTypeCached(this.mob, currentPoint.x, currentPoint.y + 1, currentPoint.z));

		if (this.mob.getPathfindingMalus(nodeTypeAbove) >= 0.0F) {
			stepHeight = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
		}

		double height = currentPoint.y - getFloorLevel(this.currentContext.level(), new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z));

		DirectionalNode[] pathsPZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z + 1, stepHeight, height, PZ, this.checkObstructions);
		DirectionalNode[] pathsNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z, stepHeight, height, NX, this.checkObstructions);
		DirectionalNode[] pathsPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z, stepHeight, height, PX, this.checkObstructions);
		DirectionalNode[] pathsNZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z - 1, stepHeight, height, NZ, this.checkObstructions);

		for (DirectionalNode directionalNode : pathsPZ) {
			if (this.isSuitablePoint(directionalNode, currentPoint, this.checkObstructions)) {
				pathOptions[openedNodeCount++] = directionalNode;
			}
		}

		for (DirectionalNode nx : pathsNX) {
			if (this.isSuitablePoint(nx, currentPoint, this.checkObstructions)) {
				pathOptions[openedNodeCount++] = nx;
			}
		}

		for (DirectionalNode px : pathsPX) {
			if (this.isSuitablePoint(px, currentPoint, this.checkObstructions)) {
				pathOptions[openedNodeCount++] = px;
			}
		}

		for (DirectionalNode node : pathsNZ) {
			if (this.isSuitablePoint(node, currentPoint, this.checkObstructions)) {
				pathOptions[openedNodeCount++] = node;
			}
		}

		DirectionalNode[] pathsNY = null;
		if (this.checkObstructions || this.pathableFacings.size() > 1) {
			pathsNY = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z, stepHeight, height, NY, this.checkObstructions);

			for (DirectionalNode node : pathsNY) {
				if (this.isSuitablePoint(node, currentPoint, this.checkObstructions)) {
					pathOptions[openedNodeCount++] = node;
				}
			}
		}

		DirectionalNode[] pathsPY = null;
		if (this.pathableFacings.size() > 1) {
			pathsPY = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z, stepHeight, height, PY, this.checkObstructions);

			for (DirectionalNode node : pathsPY) {
				if (this.isSuitablePoint(node, currentPoint, this.checkObstructions)) {
					pathOptions[openedNodeCount++] = node;
				}
			}
		}

		boolean allowDiagonalNZ = this.allowDiagonalPathOptions(pathsNZ);
		boolean allowDiagonalPZ = this.allowDiagonalPathOptions(pathsPZ);
		boolean allowDiagonalPX = this.allowDiagonalPathOptions(pathsPX);
		boolean allowDiagonalNX = this.allowDiagonalPathOptions(pathsNX);

		boolean fitsThroughPoles = this.mob.getBbWidth() < 0.5f;

		boolean is3DPathing = this.pathableFacings.size() >= 3;

		if (allowDiagonalNZ && allowDiagonalNX) {
			DirectionalNode[] pathsNXNZ = this.getSafePoints(currentPoint.x - this.entityWidth, currentPoint.y, currentPoint.z - 1, stepHeight, height, NXNZ, this.checkObstructions);

			boolean foundDiagonal = false;

			for (DirectionalNode node : pathsNXNZ) {
				if (this.isSuitablePoint(pathsNX, currentPoint.x - 1, currentPoint.y, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
					pathOptions[openedNodeCount++] = node;
					foundDiagonal = true;
				}
			}

			if (!foundDiagonal && (this.entityWidth != 1 || this.entityDepth != 1)) {
				pathsNXNZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z - this.entityDepth, stepHeight, height, NXNZ, this.checkObstructions);

				for (DirectionalNode node : pathsNXNZ) {
					if (this.isSuitablePoint(pathsNX, currentPoint.x - 1, currentPoint.y, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
					}
				}
			}
		}

		if (allowDiagonalNZ && allowDiagonalPX) {
			DirectionalNode[] pathsPXNZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, PXNZ, this.checkObstructions);

			for (DirectionalNode node : pathsPXNZ) {
				if (this.isSuitablePoint(pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
					pathOptions[openedNodeCount++] = node;
				}
			}
		}

		if (allowDiagonalPZ && allowDiagonalNX) {
			DirectionalNode[] pathsNXPZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, NXPZ, this.checkObstructions);

			for (DirectionalNode node : pathsNXPZ) {
				if (this.isSuitablePoint(pathsNX, currentPoint.x - 1, currentPoint.y, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
					pathOptions[openedNodeCount++] = node;
				}
			}
		}

		if (allowDiagonalPZ && allowDiagonalPX) {
			DirectionalNode[] pathsPXPZ = this.getSafePoints(currentPoint.x + this.entityWidth, currentPoint.y, currentPoint.z + 1, stepHeight, height, PXPZ, this.checkObstructions);

			boolean foundDiagonal = false;

			for (DirectionalNode node : pathsPXPZ) {
				if (this.isSuitablePoint(pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
					pathOptions[openedNodeCount++] = node;
					foundDiagonal = true;
				}
			}

			if (!foundDiagonal && (this.entityWidth != 1 || this.entityDepth != 1)) {
				pathsPXPZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z + this.entityDepth, stepHeight, height, PXPZ, this.checkObstructions);

				for (DirectionalNode node : pathsPXPZ) {
					if (this.isSuitablePoint(pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
					}
				}
			}
		}

		if (this.pathableFacings.size() > 1) {
			boolean allowDiagonalPY = this.allowDiagonalPathOptions(pathsPY);
			boolean allowDiagonalNY = this.allowDiagonalPathOptions(pathsNY);

			if (allowDiagonalNY && allowDiagonalNX) {
				DirectionalNode[] pathsNYNX = this.getSafePoints(currentPoint.x - this.entityWidth, currentPoint.y - 1, currentPoint.z, stepHeight, height, NXNY, this.checkObstructions);

				boolean foundDiagonal = false;

				for (DirectionalNode nynx : pathsNYNX) {
					if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsNX, currentPoint.x - 1, currentPoint.y, currentPoint.z, nynx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = nynx;
						foundDiagonal = true;
					}
				}

				if (!foundDiagonal && (this.entityWidth != 1 || this.entityHeight != 1)) {
					pathsNYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y - this.entityHeight, currentPoint.z, stepHeight, height, NXNY, this.checkObstructions);

					for (DirectionalNode nynx : pathsNYNX) {
						if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsNX, currentPoint.x - 1, currentPoint.y, currentPoint.z, nynx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
							pathOptions[openedNodeCount++] = nynx;
						}
					}
				}
			}

			if (allowDiagonalNY && allowDiagonalPX) {
				DirectionalNode[] pathsNYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y - 1, currentPoint.z, stepHeight, height, PXNY, this.checkObstructions);

				for (DirectionalNode nypx : pathsNYPX) {
					if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, nypx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = nypx;
					}
				}
			}

			if (allowDiagonalNY && allowDiagonalNZ) {
				DirectionalNode[] pathsNYNZ = this.getSafePoints(currentPoint.x, currentPoint.y - this.entityHeight, currentPoint.z - 1, stepHeight, height, NYNZ, this.checkObstructions);

				boolean foundDiagonal = false;

				for (DirectionalNode node : pathsNYNZ) {
					if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
						foundDiagonal = true;
					}
				}

				if (!foundDiagonal && (this.entityHeight != 1 || this.entityDepth != 1)) {
					pathsNYNZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z - this.entityDepth, stepHeight, height, NYNZ, this.checkObstructions);

					for (DirectionalNode node : pathsNYNZ) {
						if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
							pathOptions[openedNodeCount++] = node;
						}
					}
				}
			}

			if (allowDiagonalNY && allowDiagonalPZ) {
				DirectionalNode[] pathsNYPZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z + 1, stepHeight, height, NYPZ, this.checkObstructions);

				for (DirectionalNode node : pathsNYPZ) {
					if (this.isSuitablePoint(pathsNY, currentPoint.x, currentPoint.y - 1, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
					}
				}
			}

			if (allowDiagonalPY && allowDiagonalNX) {
				DirectionalNode[] pathsPYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y + 1, currentPoint.z, stepHeight, height, NXPY, this.checkObstructions);

				for (DirectionalNode pynx : pathsPYNX) {
					if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsNZ, currentPoint.x - 1, currentPoint.y, currentPoint.z, pynx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = pynx;
					}
				}
			}

			if (allowDiagonalPY && allowDiagonalPX) {
				DirectionalNode[] pathsPYPX = this.getSafePoints(currentPoint.x + this.entityWidth, currentPoint.y + 1, currentPoint.z, stepHeight, height, PXPY, this.checkObstructions);

				boolean foundDiagonal = false;

				for (DirectionalNode pypx : pathsPYPX) {
					if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, pypx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = pypx;
						foundDiagonal = true;
					}
				}

				if (!foundDiagonal && (this.entityWidth != 1 || this.entityHeight != 1)) {
					pathsPYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y + this.entityHeight, currentPoint.z, stepHeight, height, PXPY, this.checkObstructions);

					for (DirectionalNode pypx : pathsPYPX) {
						if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsPX, currentPoint.x + 1, currentPoint.y, currentPoint.z, pypx, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
							pathOptions[openedNodeCount++] = pypx;
						}
					}
				}
			}

			if (allowDiagonalPY && allowDiagonalNZ) {
				DirectionalNode[] pathsPYNZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z - 1, stepHeight, height, PYNZ, this.checkObstructions);

				for (DirectionalNode node : pathsPYNZ) {
					if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsNZ, currentPoint.x, currentPoint.y, currentPoint.z - 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
					}
				}
			}

			if (allowDiagonalPY && allowDiagonalPZ) {
				DirectionalNode[] pathsPYPZ = this.getSafePoints(currentPoint.x, currentPoint.y + this.entityHeight, currentPoint.z + 1, stepHeight, height, PYPZ, this.checkObstructions);

				boolean foundDiagonal = false;

				for (DirectionalNode node : pathsPYPZ) {
					if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
						pathOptions[openedNodeCount++] = node;
						foundDiagonal = true;
					}
				}

				if (!foundDiagonal && (this.entityHeight != 1 || this.entityDepth != 1)) {
					pathsPYPZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z + this.entityDepth, stepHeight, height, PYPZ, this.checkObstructions);

					for (DirectionalNode node : pathsPYPZ) {
						if (this.isSuitablePoint(pathsPY, currentPoint.x, currentPoint.y + 1, currentPoint.z, pathsPZ, currentPoint.x, currentPoint.y, currentPoint.z + 1, node, currentPoint, this.checkObstructions, fitsThroughPoles, is3DPathing)) {
							pathOptions[openedNodeCount++] = node;
						}
					}
				}
			}
		}

		return openedNodeCount;
	}

	protected boolean isTraversible(DirectionalNode from, DirectionalNode to) {
		if (this.canFloat() && (from.type == PathType.WATER || from.type == PathType.WATER_BORDER || from.type == PathType.LAVA || to.type == PathType.WATER || to.type == PathType.WATER_BORDER || to.type == PathType.LAVA)) {
			//When swimming it can always reach any side
			return true;
		}

		boolean dx = (to.x - from.x) != 0;
		boolean dy = (to.y - from.y) != 0;
		boolean dz = (to.z - from.z) != 0;

		boolean isDiagonal = (dx ? 1 : 0) + (dy ? 1 : 0) + (dz ? 1 : 0) > 1;

		Direction[] fromDirections = from.getPathableSides();
		Direction[] toDirections = to.getPathableSides();

		for (Direction d1 : fromDirections) {
			for (Direction d2 : toDirections) {
				if (d1 == d2) {
					return true;
				} else if (isDiagonal) {
					Direction.Axis a1 = d1.getAxis();
					Direction.Axis a2 = d2.getAxis();

					if ((a1 == Direction.Axis.X && a2 == Direction.Axis.Y) || (a1 == Direction.Axis.Y && a2 == Direction.Axis.X)) {
						return !dz;
					} else if ((a1 == Direction.Axis.X && a2 == Direction.Axis.Z) || (a1 == Direction.Axis.Z && a2 == Direction.Axis.X)) {
						return !dy;
					} else if ((a1 == Direction.Axis.Z && a2 == Direction.Axis.Y) || (a1 == Direction.Axis.Y && a2 == Direction.Axis.Z)) {
						return !dx;
					}
				}
			}
		}

		return false;
	}

	protected static boolean isSharingDirection(DirectionalNode from, DirectionalNode to) {
		Direction[] fromDirections = from.getPathableSides();
		Direction[] toDirections = to.getPathableSides();

		for (Direction d1 : fromDirections) {
			for (Direction d2 : toDirections) {
				if (d1 == d2) {
					return true;
				}
			}
		}

		return false;
	}

	protected boolean isSuitablePoint(@Nullable DirectionalNode newPoint, DirectionalNode currentPoint, boolean allowObstructions) {
		return newPoint != null && !newPoint.closed && (allowObstructions || newPoint.costMalus >= 0.0F || currentPoint.costMalus < 0.0F) && this.isTraversible(currentPoint, newPoint);
	}

	protected boolean isSuitablePoint(@Nullable DirectionalNode[] newPoints1, int np1x, int np1y, int np1z, @Nullable DirectionalNode[] newPoints2, int np2x, int np2y, int np2z, @Nullable DirectionalNode newPointDiagonal, DirectionalNode currentPoint, boolean allowObstructions, boolean fitsThroughPoles, boolean is3DPathing) {
		if (!is3DPathing) {
			if (newPointDiagonal != null && !newPointDiagonal.closed && newPoints2 != null && newPoints2.length > 0 && (newPoints2[0] != null || (newPoints2.length > 1 && newPoints2[1] != null)) && newPoints1 != null && newPoints1.length > 0 && (newPoints1[0] != null || (newPoints1.length > 1 && newPoints1[1] != null))) {
				if ((newPoints1[0] == null || newPoints1[0].type != PathType.WALKABLE_DOOR) && (newPoints2[0] == null || newPoints2[0].type != PathType.WALKABLE_DOOR) && newPointDiagonal.type != PathType.WALKABLE_DOOR) {
					boolean canPassPoleDiagonally = newPoints2[0] != null && newPoints2[0].type == PathType.FENCE && newPoints1[0] != null && newPoints1[0].type == PathType.FENCE && fitsThroughPoles;
					return (allowObstructions || newPointDiagonal.costMalus >= 0.0F) &&
						(canPassPoleDiagonally || (
							((newPoints2[0] != null && (allowObstructions || newPoints2[0].costMalus >= 0.0F)) || (newPoints2.length > 1 && newPoints2[1] != null && (allowObstructions || newPoints2[1].costMalus >= 0.0F))) &&
								((newPoints1[0] != null && (allowObstructions || newPoints1[0].costMalus >= 0.0F)) || (newPoints1.length > 1 && newPoints1[1] != null && (allowObstructions || newPoints1[1].costMalus >= 0.0F)))
						));
				}
			}
		} else {
			if (newPointDiagonal != null && !newPointDiagonal.closed && this.isTraversible(currentPoint, newPointDiagonal)) {
				long packed2 = this.getDirectionalPathNodeTypeCached(this.mob, np2x, np2y, np2z);
				PathType pathNodeType2 = unpackNodeType(packed2);
				boolean open2 = (pathNodeType2 == PathType.OPEN || pathNodeType2 == PathType.WALKABLE);

				long packed1 = this.getDirectionalPathNodeTypeCached(this.mob, np1x, np1y, np1z);
				PathType pathNodeType1 = unpackNodeType(packed1);
				boolean open1 = (pathNodeType1 == PathType.OPEN || pathNodeType1 == PathType.WALKABLE);

				return open1 != open2 || open1 && isSharingDirection(newPointDiagonal, currentPoint);
			}
		}

		return false;
	}

	protected DirectionalNode openPoint(int x, int y, int z, long packed, boolean isDrop) {
		int hash = Node.createHash(x, y, z);

		Node point = this.nodes.computeIfAbsent(hash, (key) -> new DirectionalNode(x, y, z, packed, isDrop));

		if (!(point instanceof DirectionalNode)) {
			point = new DirectionalNode(point);
			this.nodes.put(hash, point);
		}

		return (DirectionalNode) point;
	}

	private DirectionalNode[] getSafePoints(int x, int y, int z, int stepHeight, double height, Vec3i direction, boolean allowBlocked) {
		DirectionalNode directPathPoint = null;

		BlockPos pos = new BlockPos(x, y, z);

		double blockHeight = y - getFloorLevel(this.currentContext.level(), new BlockPos(x, y, z));

		if (blockHeight - height > 1.125D) {
			return new DirectionalNode[0];
		} else {
			final long initialPacked = this.getDirectionalPathNodeTypeCached(this.mob, x, y, z);
			long packed = initialPacked;
			PathType nodeType = unpackNodeType(packed);

			float malus = this.entity.getPathingMalus(this.currentContext.level(), this.mob, nodeType, pos, direction, dir -> unpackDirection(dir, initialPacked)); //Replaces EntityLiving#getPathPriority

			double halfWidth = (double) this.mob.getBbWidth() / 2.0D;

			DirectionalNode[] result = new DirectionalNode[1];

			if (malus >= 0.0F && (allowBlocked || nodeType != PathType.BLOCKED)) {
				directPathPoint = this.openPoint(x, y, z, packed, false);
				directPathPoint.type = nodeType;
				directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);

				//Allow other nodes than this obstructed node to also be considered, otherwise jumping/pathing up steps does no longer work
				if (directPathPoint.type == PathType.BLOCKED) {
					result = new DirectionalNode[2];
					result[1] = directPathPoint;
					directPathPoint = null;
				}
			}

			if (nodeType == PathType.WALKABLE) {
				result[0] = directPathPoint;
				return result;
			} else {
				if (directPathPoint == null && stepHeight > 0 && nodeType != PathType.FENCE && nodeType != PathType.UNPASSABLE_RAIL && nodeType != PathType.TRAPDOOR && direction.getY() == 0 && Math.abs(direction.getX()) + Math.abs(direction.getY()) + Math.abs(direction.getZ()) == 1) {
					DirectionalNode[] pointsAbove = this.getSafePoints(x, y + 1, z, stepHeight - 1, height, direction, false);
					directPathPoint = pointsAbove.length > 0 ? pointsAbove[0] : null;

					if (directPathPoint != null && (directPathPoint.type == PathType.OPEN || directPathPoint.type == PathType.WALKABLE) && this.mob.getBbWidth() < 1.0F) {
						double offsetX = (x - direction.getX()) + 0.5D;
						double offsetZ = (z - direction.getY()) + 0.5D;

						AABB enclosingAabb = new AABB(
							offsetX - halfWidth,
							getFloorLevel(this.currentContext.level(), BlockPos.containing(offsetX, (double) (y + 1), offsetZ)) + 0.001D,
							offsetZ - halfWidth,
							offsetX + halfWidth,
							(double) this.mob.getBbHeight() + getFloorLevel(this.currentContext.level(), new BlockPos(directPathPoint.x, directPathPoint.y, directPathPoint.z)) - 0.002D,
							offsetZ + halfWidth);
						if (this.checkAabbCollision(enclosingAabb)) {
							directPathPoint = null;
						}
					}
				}

				if (nodeType == PathType.OPEN) {
					directPathPoint = null;

					AABB checkAabb = new AABB((double) x - halfWidth + 0.5D, (double) y + 0.001D, (double) z - halfWidth + 0.5D, (double) x + halfWidth + 0.5D, (double) ((float) y + this.mob.getBbHeight()), (double) z + halfWidth + 0.5D);

					if (this.checkAabbCollision(checkAabb)) {
						result[0] = null;
						return result;
					}

					if (this.mob.getBbWidth() >= 1.0F) {
						for (Direction pathableFacing : this.pathableFacingsArray) {
							long packedAtFacing = this.getDirectionalPathNodeTypeCached(this.mob, x + pathableFacing.getStepX() * this.pathingSizeOffsetX, y + (pathableFacing == Direction.DOWN ? -1 : pathableFacing == Direction.UP ? this.pathingSizeOffsetY : 0), z + pathableFacing.getStepZ() * this.pathingSizeOffsetZ);
							PathType nodeTypeAtFacing = unpackNodeType(packedAtFacing);

							if (nodeTypeAtFacing == PathType.BLOCKED) {
								directPathPoint = this.openPoint(x, y, z, packedAtFacing, false);
								directPathPoint.type = PathType.WALKABLE;
								directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
								result[0] = directPathPoint;
								return result;
							}
						}
					}


					boolean cancelFallDown = false;
					DirectionalNode fallPathPoint = null;

					int fallDistance = 0;
					int preFallY = y;

					while (y > this.currentContext.level().getMinBuildHeight() && nodeType == PathType.OPEN) {
						--y;

						if (fallDistance++ >= Math.max(1, this.mob.getMaxFallDistance()) /*at least one chance is required for swimming*/ || y == 0) {
							cancelFallDown = true;
							break;
						}

						packed = this.getDirectionalPathNodeTypeCached(this.mob, x, y, z);
						nodeType = unpackNodeType(packed);

						malus = this.mob.getPathfindingMalus(nodeType);

						if (((this.mob.getMaxFallDistance() > 0 && nodeType != PathType.OPEN) || nodeType == PathType.WATER || nodeType == PathType.LAVA) && malus >= 0.0F) {
							fallPathPoint = this.openPoint(x, y, z, packed, true);
							fallPathPoint.type = nodeType;
							fallPathPoint.costMalus = Math.max(fallPathPoint.costMalus, malus);
							break;
						}

						if (malus < 0.0F) {
							cancelFallDown = true;
						}
					}

					boolean hasPathUp = false;

					if (this.pathableFacings.size() > 1) {
						packed = this.getDirectionalPathNodeTypeCached(this.mob, x, preFallY, z);
						nodeType = unpackNodeType(packed);

						malus = this.mob.getPathfindingMalus(nodeType);

						if (nodeType != PathType.OPEN && malus >= 0.0F) {
							if (fallPathPoint != null) {
								result = new DirectionalNode[2];
								result[1] = fallPathPoint;
							}

							result[0] = directPathPoint = this.openPoint(x, preFallY, z, packed, false);
							directPathPoint.type = nodeType;
							directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
							hasPathUp = true;
						}
					}

					if (fallPathPoint != null) {
						if (!hasPathUp) {
							result[0] = directPathPoint = fallPathPoint;
						} else {
							result = new DirectionalNode[2];
							result[0] = directPathPoint;
							result[1] = fallPathPoint;
						}
					}

					if (fallPathPoint != null) {
						float bridingMalus = this.entity.getBridgePathingMalus(this.mob, new BlockPos(x, preFallY, z), fallPathPoint);

						if (bridingMalus >= 0.0f) {
							result = new DirectionalNode[2];
							result[0] = directPathPoint;

							DirectionalNode bridgePathPoint = this.openPoint(x, preFallY, z, packed, false);
							bridgePathPoint.type = PathType.WALKABLE;
							bridgePathPoint.costMalus = Math.max(bridgePathPoint.costMalus, bridingMalus);
							result[1] = bridgePathPoint;
						}
					}

					if (cancelFallDown && !hasPathUp) {
						result[0] = null;
						if (result.length == 2) {
							result[1] = null;
						}
						return result;
					}
				}

				if (nodeType == PathType.FENCE) {
					directPathPoint = this.openPoint(x, y, z, packed, false);
					directPathPoint.closed = true;
					directPathPoint.type = nodeType;
					directPathPoint.costMalus = nodeType.getMalus();
				}

				result[0] = directPathPoint;
				return result;
			}
		}
	}

	protected long getDirectionalPathNodeTypeCached(Mob entitylivingIn, int x, int y, int z) {
		return this.pathNodeTypeCache.computeIfAbsent(BlockPos.asLong(x, y, z), (key) -> this.getDirectionalPathNodeType(this.currentContext, x, y, z, entitylivingIn, this.entityWidth, this.entityHeight, this.entityDepth));
	}

	static long packDirection(Direction facing, long packed) {
		return packed | (1L << facing.ordinal());
	}

	static long packDirection(long packed1, long packed2) {
		return (packed1 & ~0xFFFFFFFFL) | (packed1 & 0xFFFFFFFFL) | (packed2 & 0xFFFFFFFFL);
	}

	static boolean unpackDirection(Direction facing, long packed) {
		return (packed & (1L << facing.ordinal())) != 0;
	}

	static boolean unpackDirection(long packed) {
		return (packed & 0xFFFFFFFFL) != 0;
	}

	static long packNodeType(PathType type, long packed) {
		return ((long) type.ordinal() << 32) | (packed & 0xFFFFFFFFL);
	}

	static PathType unpackNodeType(long packed) {
		return PATH_NODE_TYPES[(int) (packed >> 32)];
	}


	@Override
	public PathType getPathTypeOfMob(PathfindingContext context, int x, int y, int z, Mob entity) {
		return unpackNodeType(this.getDirectionalPathNodeType(context, x, y, z, entity, 0, 0, 0));
	}

	protected long getDirectionalPathNodeType(PathfindingContext context, int x, int y, int z, Mob entity, int xSize, int ySize, int zSize) {
		BlockPos pos = entity.blockPosition();

		EnumSet<PathType> applicablePathNodeTypes = EnumSet.noneOf(PathType.class);

		long centerPacked = this.getDirectionalPathNodeType(context, x, y, z, xSize, ySize, zSize, applicablePathNodeTypes, pos);
		PathType centerPathNodeType = unpackNodeType(centerPacked);

		if (applicablePathNodeTypes.contains(PathType.FENCE)) {
			return packNodeType(PathType.FENCE, centerPacked);
		} else if (applicablePathNodeTypes.contains(PathType.UNPASSABLE_RAIL)) {
			return packNodeType(PathType.UNPASSABLE_RAIL, centerPacked);
		} else {
			PathType selectedPathNodeType = PathType.BLOCKED;

			for (PathType applicablePathNodeType : applicablePathNodeTypes) {
				if (entity.getPathfindingMalus(applicablePathNodeType) < 0.0F) {
					return packNodeType(applicablePathNodeType, centerPacked);
				}

				float p1 = entity.getPathfindingMalus(applicablePathNodeType);
				float p2 = entity.getPathfindingMalus(selectedPathNodeType);
				if (p1 > p2 || (p1 == p2 && !(selectedPathNodeType == PathType.WALKABLE && applicablePathNodeType == PathType.OPEN)) || (p1 == p2 && selectedPathNodeType == PathType.OPEN && applicablePathNodeType == PathType.WALKABLE)) {
					selectedPathNodeType = applicablePathNodeType;
				}
			}

			if (centerPathNodeType == PathType.OPEN && entity.getPathfindingMalus(selectedPathNodeType) == 0.0F) {
				return packNodeType(PathType.OPEN, 0L);
			} else {
				return packNodeType(selectedPathNodeType, centerPacked);
			}
		}
	}

	protected long getDirectionalPathNodeType(PathfindingContext context, int x, int y, int z, int xSize, int ySize, int zSize, EnumSet<PathType> nodeTypeEnum, BlockPos pos) {
		long packed = 0L;

		for (int ox = 0; ox < xSize; ++ox) {
			for (int oy = 0; oy < ySize; ++oy) {
				for (int oz = 0; oz < zSize; ++oz) {
					int bx = ox + x;
					int by = oy + y;
					int bz = oz + z;

					long packedAdjusted = this.getDirectionalPathNodeType(context, bx, by, bz);
					PathType adjustedNodeType = unpackNodeType(packedAdjusted);

					if (adjustedNodeType == PathType.DOOR_WOOD_CLOSED && this.canOpenDoors() && canPassDoors()) {
						adjustedNodeType = PathType.WALKABLE_DOOR;
					}

					if (adjustedNodeType == PathType.DOOR_OPEN && !canPassDoors()) {
						adjustedNodeType = PathType.BLOCKED;
					}

					if (adjustedNodeType == PathType.RAIL && this.getPathType(context, pos.getX(), pos.getY(), pos.getZ()) != PathType.RAIL && this.getPathType(context, pos.getX(), pos.getY() - 1, pos.getZ()) != PathType.RAIL) {
						adjustedNodeType = PathType.UNPASSABLE_RAIL;
					}
					if (ox == 0 && oy == 0 && oz == 0) {
						packed = packNodeType(adjustedNodeType, packedAdjusted);
					}

					nodeTypeEnum.add(adjustedNodeType);
				}
			}
		}

		return packed;
	}

	@Override
	public PathType getPathType(PathfindingContext context, int x, int y, int z) {
		return unpackNodeType(this.getDirectionalPathNodeType(context, x, y, z));
	}

	protected long getDirectionalPathNodeType(PathfindingContext context, int x, int y, int z) {
		return getDirectionalPathNodeType(this.rawPathNodeTypeCache, context, x, y, z, this.pathingSizeOffsetX, this.pathingSizeOffsetY, this.pathingSizeOffsetZ, this.pathableFacingsArray);
	}

	protected static PathType getRawPathNodeTypeCached(Long2ObjectMap<PathType> cache, PathfindingContext context, BlockPos.MutableBlockPos pos) {
		return cache.computeIfAbsent(BlockPos.asLong(pos.getX(), pos.getY(), pos.getZ()), (key) -> {
			return getPathTypeFromState(context.level(), pos); //getPathNodeTypeRaw
		});
	}

	protected static long getDirectionalPathNodeType(Long2ObjectMap<PathType> rawPathNodeTypeCache, PathfindingContext context, int x, int y, int z, int pathingSizeOffsetX, int pathingSizeOffsetY, int pathingSizeOffsetZ, Direction[] pathableFacings) {
		long packed = 0L;

		BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

		PathType nodeType = getRawPathNodeTypeCached(rawPathNodeTypeCache, context, pos.set(x, y, z));
		boolean isWalkable = false;

		if (nodeType == PathType.OPEN && y >= context.level().getMinBuildHeight() + 1) {
			for (int i = 0; i < pathableFacings.length; i++) {
				Direction pathableFacing = pathableFacings[i];

				int checkHeight = pathableFacing.getAxis() != Direction.Axis.Y ? Math.min(4, pathingSizeOffsetY - 1) : 0;

				int cx = x + pathableFacing.getStepX() * pathingSizeOffsetX;
				int cy = y + (pathableFacing == Direction.DOWN ? -1 : pathableFacing == Direction.UP ? pathingSizeOffsetY : 0);
				int cz = z + pathableFacing.getStepZ() * pathingSizeOffsetZ;

				for (int yo = 0; yo <= checkHeight; yo++) {
					pos.set(cx, cy + yo, cz);

					PathType offsetNodeType = getRawPathNodeTypeCached(rawPathNodeTypeCache, context, pos);
					nodeType = offsetNodeType != PathType.WALKABLE && offsetNodeType != PathType.OPEN && offsetNodeType != PathType.WATER && offsetNodeType != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;

					if (offsetNodeType == PathType.DAMAGE_FIRE) {
						nodeType = PathType.DAMAGE_FIRE;
					}

					if (offsetNodeType == PathType.DANGER_OTHER) {
						nodeType = PathType.DANGER_OTHER;
					}

					if (offsetNodeType == PathType.DAMAGE_OTHER) {
						nodeType = PathType.DAMAGE_OTHER;
					}

					if (offsetNodeType == PathType.STICKY_HONEY) {
						nodeType = PathType.STICKY_HONEY;
					}

					if (nodeType == PathType.WALKABLE) {
						if (isColliderNodeType(offsetNodeType)) {
							packed = packDirection(pathableFacing, packed);
						}
						isWalkable = true;
					}
				}
			}
		}

		if (isWalkable) {
			nodeType = checkNeighbourBlocks(context, x, y, z, PathType.WALKABLE); //checkNeighborBlocks
		}

		return packNodeType(nodeType, packed);
	}

	protected static boolean isColliderNodeType(PathType type) {
		return type == PathType.BLOCKED || type == PathType.TRAPDOOR || type == PathType.FENCE ||
			type == PathType.DOOR_WOOD_CLOSED || type == PathType.DOOR_IRON_CLOSED || type == PathType.LEAVES ||
			type == PathType.STICKY_HONEY || type == PathType.COCOA;
	}
}