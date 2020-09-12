package thebetweenlands.common.entity.ai;

import java.util.EnumSet;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class ObstructionAwareWalkNodeProcessor<T extends EntityLiving & IPathObstructionAwareEntity> extends WalkNodeProcessor {
	protected T obstructionAwareEntity;
	protected boolean checkObstructions;
	protected int pathingSizeOffsetX, pathingSizeOffsetY, pathingSizeOffsetZ;
	protected EnumSet<EnumFacing> pathableFacings = EnumSet.of(EnumFacing.DOWN);

	public void setObstructionAwareEntity(T obstructionAwareEntity) {
		this.obstructionAwareEntity = obstructionAwareEntity;
	}

	public void setCheckObstructions(boolean checkObstructions) {
		this.checkObstructions = checkObstructions;
	}

	public void setCanPathWalls(boolean canPathWalls) {
		if(canPathWalls) {
			this.pathableFacings.add(EnumFacing.NORTH);
			this.pathableFacings.add(EnumFacing.EAST);
			this.pathableFacings.add(EnumFacing.SOUTH);
			this.pathableFacings.add(EnumFacing.WEST);
		} else {
			this.pathableFacings.remove(EnumFacing.NORTH);
			this.pathableFacings.remove(EnumFacing.EAST);
			this.pathableFacings.remove(EnumFacing.SOUTH);
			this.pathableFacings.remove(EnumFacing.WEST);
		}
	}

	public void setCanPathCeiling(boolean canPathCeiling) {
		if(canPathCeiling) {
			this.pathableFacings.add(EnumFacing.UP);
		} else {
			this.pathableFacings.remove(EnumFacing.UP);
		}
	}

	@Override
	public void init(IBlockAccess sourceIn, EntityLiving mob) {
		super.init(sourceIn, mob);
		this.pathingSizeOffsetX = Math.max(1, MathHelper.floor(this.entity.width / 2.0f + 1));
		this.pathingSizeOffsetY = Math.max(1, MathHelper.floor(this.entity.height + 1));
		this.pathingSizeOffsetZ = Math.max(1, MathHelper.floor(this.entity.width / 2.0f + 1));
	}

	private boolean shouldAvoidPathOptions(PathPoint[] options) {
		return options == null || options.length == 0 || ((options[0] == null || options[0].nodeType == PathNodeType.OPEN || options[0].costMalus != 0.0F) && (options.length <= 1 || (options[1] == null || options[1].nodeType == PathNodeType.OPEN || options[1].costMalus != 0.0F)));
	}

	private boolean isPassableWithExemptions(IBlockAccess blockAccess, int x, int y, int z, EnumSet<EnumFacing> exemptions) {
		PathNodeType nodeType = this.getPathNodeType(blockAccess, x, y, z, exemptions);
		return nodeType != PathNodeType.OPEN && this.entity.getPathPriority(nodeType) >= 0.0f;
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance){
		int openedNodeCount = 0;
		int stepHeight = 0;

		PathNodeType nodeTypeAbove = this.getPathNodeType(this.entity, currentPoint.x, currentPoint.y + 1, currentPoint.z);

		if(this.entity.getPathPriority(nodeTypeAbove) >= 0.0F) {
			stepHeight = MathHelper.floor(Math.max(1.0F, this.entity.stepHeight));
		}

		BlockPos posDown = (new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z)).down();

		double height = (double)currentPoint.y - (1.0D - this.blockaccess.getBlockState(posDown).getBoundingBox(this.blockaccess, posDown).maxY);

		PathPoint[] pathsPZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, this.checkObstructions);
		PathPoint[] pathsNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z, stepHeight, height, EnumFacing.WEST, this.checkObstructions);
		PathPoint[] pathsPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z, stepHeight, height, EnumFacing.EAST, this.checkObstructions);
		PathPoint[] pathsNZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, this.checkObstructions);

		for(int k = 0; k < pathsPZ.length; k++) {
			if(pathsPZ[k] != null && !pathsPZ[k].visited && pathsPZ[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsPZ[k];
			}
		}

		for(int k = 0; k < pathsNX.length; k++) {
			if(pathsNX[k] != null && !pathsNX[k].visited && pathsNX[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsNX[k];
			}
		}

		for(int k = 0; k < pathsPX.length; k++) {
			if(pathsPX[k] != null && !pathsPX[k].visited && pathsPX[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsPX[k];
			}
		}

		for(int k = 0; k < pathsNZ.length; k++) {
			if(pathsNZ[k] != null && !pathsNZ[k].visited && pathsNZ[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsNZ[k];
			}
		}

		PathPoint[] pathsNY = null;
		if(this.checkObstructions || this.pathableFacings.size() > 1) {
			pathsNY = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z, stepHeight, height, EnumFacing.DOWN, this.checkObstructions);
			for(int k = 0; k < pathsNY.length; k++) {
				if(pathsNY[k] != null && !pathsNY[k].visited && pathsNY[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNY[k];
				}
			}
		}

		PathPoint[] pathsPY = null;
		if(this.pathableFacings.size() > 1) {
			pathsPY = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z, stepHeight, height, EnumFacing.UP, this.checkObstructions);
			for(int k = 0; k < pathsPY.length; k++) {
				if(pathsPY[k] != null && !pathsPY[k].visited && pathsPY[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPY[k];
				}
			}
		}

		boolean avoidPathNZ = this.shouldAvoidPathOptions(pathsNZ);
		boolean avoidPathPZ = this.shouldAvoidPathOptions(pathsPZ);
		boolean avoidPathPX = this.shouldAvoidPathOptions(pathsPX);
		boolean avoidPathNX = this.shouldAvoidPathOptions(pathsNX);

		if(avoidPathNZ && avoidPathNX) {
			PathPoint[] pathsNXNZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, this.checkObstructions);

			for(int k = 0; k < pathsNXNZ.length; k++) {
				if(pathsNXNZ[k] != null && !pathsNXNZ[k].visited && pathsNXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXNZ[k];
				}
			}
		}

		if(avoidPathNZ && avoidPathPX) {
			PathPoint[] pathsPXNZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, this.checkObstructions);

			for(int k = 0; k < pathsPXNZ.length; k++) {
				if(pathsPXNZ[k] != null && !pathsPXNZ[k].visited && pathsPXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXNZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathNX) {
			PathPoint[] pathsNXPZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, this.checkObstructions);

			for(int k = 0; k < pathsNXPZ.length; k++) {
				if(pathsNXPZ[k] != null && !pathsNXPZ[k].visited && pathsNXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXPZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathPX) {
			PathPoint[] pathsPXPZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, this.checkObstructions);

			for(int k = 0; k < pathsPXPZ.length; k++) {
				if(pathsPXPZ[k] != null && !pathsPXPZ[k].visited && pathsPXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXPZ[k];
				}
			}
		}

		if(this.pathableFacings.size() > 1) {
			boolean avoidPathPY = this.shouldAvoidPathOptions(pathsPY);
			boolean avoidPathNY = this.shouldAvoidPathOptions(pathsNY);

			if(avoidPathNY && avoidPathNX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.UP, EnumFacing.EAST, EnumFacing.WEST))) {
				PathPoint[] pathsNYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y - 1, currentPoint.z, stepHeight, height, EnumFacing.WEST, this.checkObstructions);

				for(int k = 0; k < pathsNYNX.length; k++) {
					if(pathsNYNX[k] != null && !pathsNYNX[k].visited && pathsNYNX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYNX[k];
					}
				}
			}

			if(avoidPathNY && avoidPathPX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.UP, EnumFacing.EAST, EnumFacing.WEST))) {
				PathPoint[] pathsNYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y - 1, currentPoint.z, stepHeight, height, EnumFacing.EAST, this.checkObstructions);

				for(int k = 0; k < pathsNYPX.length; k++) {
					if(pathsNYPX[k] != null && !pathsNYPX[k].visited && pathsNYPX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYPX[k];
					}
				}
			}

			if(avoidPathNY && avoidPathNZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH))) {
				PathPoint[] pathsNYNZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, this.checkObstructions);

				for(int k = 0; k < pathsNYNZ.length; k++) {
					if(pathsNYNZ[k] != null && !pathsNYNZ[k].visited && pathsNYNZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYNZ[k];
					}
				}
			}

			if(avoidPathNY && avoidPathPZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH))) {
				PathPoint[] pathsNYPZ = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, this.checkObstructions);

				for(int k = 0; k < pathsNYPZ.length; k++) {
					if(pathsNYPZ[k] != null && !pathsNYPZ[k].visited && pathsNYPZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsNYPZ[k];
					}
				}
			}

			if(avoidPathPY && avoidPathNX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST))) {
				PathPoint[] pathsPYNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y + 1, currentPoint.z, stepHeight, height, EnumFacing.WEST, this.checkObstructions);

				for(int k = 0; k < pathsPYNX.length; k++) {
					if(pathsPYNX[k] != null && !pathsPYNX[k].visited && pathsPYNX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYNX[k];
					}
				}
			}

			if(avoidPathPY && avoidPathPX && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST))) {
				PathPoint[] pathsPYPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y + 1, currentPoint.z, stepHeight, height, EnumFacing.EAST, this.checkObstructions);

				for(int k = 0; k < pathsPYPX.length; k++) {
					if(pathsPYPX[k] != null && !pathsPYPX[k].visited && pathsPYPX[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYPX[k];
					}
				}
			}

			if(avoidPathPY && avoidPathNZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH))) {
				PathPoint[] pathsPYNZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, this.checkObstructions);

				for(int k = 0; k < pathsPYNZ.length; k++) {
					if(pathsPYNZ[k] != null && !pathsPYNZ[k].visited && pathsPYNZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYNZ[k];
					}
				}
			}

			if(avoidPathPY && avoidPathPZ && this.isPassableWithExemptions(this.blockaccess, currentPoint.x, currentPoint.y, currentPoint.z, EnumSet.of(EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH))) {
				PathPoint[] pathsPYPZ = this.getSafePoints(currentPoint.x, currentPoint.y + 1, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, this.checkObstructions);

				for(int k = 0; k < pathsPYPZ.length; k++) {
					if(pathsPYPZ[k] != null && !pathsPYPZ[k].visited && pathsPYPZ[k].distanceTo(targetPoint) < maxDistance) {
						pathOptions[openedNodeCount++] = pathsPYPZ[k];
					}
				}
			}
		}

		return openedNodeCount;
	}

	@Nullable
	private PathPoint[] getSafePoints(int x, int y, int z, int stepHeight, double height, EnumFacing facing, boolean allowBlocked) {
		PathPoint directPathPoint = null;

		BlockPos pos = new BlockPos(x, y, z);
		BlockPos posDown = pos.down();

		double blockHeight = (double)y - (1.0D - this.blockaccess.getBlockState(posDown).getBoundingBox(this.blockaccess, posDown).maxY);

		if (blockHeight - height > 1.125D) {
			return new PathPoint[0];
		} else {
			PathNodeType nodeType = this.getPathNodeType(this.entity, x, y, z);

			float malus = this.obstructionAwareEntity.getPathingMalus(this.obstructionAwareEntity, nodeType, pos); //Replaces EntityLiving#getPathPriority

			double halfWidth = (double)this.entity.width / 2.0D;

			PathPoint[] result = new PathPoint[1];

			if(malus >= 0.0F && (allowBlocked || nodeType != PathNodeType.BLOCKED)) {
				directPathPoint = this.openPoint(x, y, z);
				directPathPoint.nodeType = nodeType;
				directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);

				//Allow other nodes than this obstructed node to also be considered, otherwise jumping/pathing up steps does no longer work
				if(directPathPoint.nodeType == PathNodeType.BLOCKED) {
					result = new PathPoint[2];
					result[1] = directPathPoint;
					directPathPoint = null;
				}
			}

			if(nodeType == PathNodeType.WALKABLE) {
				result[0] = directPathPoint;
				return result;
			} else {
				if (directPathPoint == null && stepHeight > 0 && nodeType != PathNodeType.FENCE && nodeType != PathNodeType.TRAPDOOR && facing.getAxis() != EnumFacing.Axis.Y) {
					PathPoint[] pointsAbove = this.getSafePoints(x, y + 1, z, stepHeight - 1, height, facing, false);
					directPathPoint = pointsAbove.length > 0 ? pointsAbove[0] : null;

					if(directPathPoint != null && (directPathPoint.nodeType == PathNodeType.OPEN || directPathPoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
						double offsetX = (double)(x - facing.getXOffset()) + 0.5D;
						double offsetZ = (double)(z - facing.getZOffset()) + 0.5D;

						AxisAlignedBB checkAabb = new AxisAlignedBB(offsetX - halfWidth, (double)y + 0.001D, offsetZ - halfWidth, offsetX + halfWidth, (double)((float)y + this.entity.height), offsetZ + halfWidth);
						AxisAlignedBB blockAabb = this.blockaccess.getBlockState(pos).getBoundingBox(this.blockaccess, pos);
						AxisAlignedBB enclosingAabb = checkAabb.expand(0.0D, blockAabb.maxY - 0.002D, 0.0D);

						if(this.entity.world.collidesWithAnyBlock(enclosingAabb)) {
							directPathPoint = null;
						}
					}
				}

				if(nodeType == PathNodeType.OPEN) {
					AxisAlignedBB checkAabb = new AxisAlignedBB((double)x - halfWidth + 0.5D, (double)y + 0.001D, (double)z - halfWidth + 0.5D, (double)x + halfWidth + 0.5D, (double)((float)y + this.entity.height), (double)z + halfWidth + 0.5D);

					if(this.entity.world.collidesWithAnyBlock(checkAabb)) {
						result[0] = null;
						return result;
					}

					if(this.entity.width >= 1.0F) {
						for(EnumFacing pathableFacing : this.pathableFacings) {
							PathNodeType nodeTypeAtFacing = this.getPathNodeType(this.entity, x + pathableFacing.getXOffset() * this.pathingSizeOffsetX, y + (pathableFacing == EnumFacing.DOWN ? -1 : pathableFacing == EnumFacing.UP ? this.pathingSizeOffsetY : 0), z + pathableFacing.getZOffset() * this.pathingSizeOffsetZ);

							if(nodeTypeAtFacing == PathNodeType.BLOCKED) {
								directPathPoint = this.openPoint(x, y, z);
								directPathPoint.nodeType = PathNodeType.WALKABLE;
								directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
								result[0] = directPathPoint;
								return result;
							}
						}
					}


					boolean cancelFallDown = false;
					PathPoint fallPathPoint = null;

					int fallDistance = 0;
					int preFallY = y;

					while(y > 0 && nodeType == PathNodeType.OPEN) {
						--y;

						if(fallDistance++ >= this.entity.getMaxFallHeight() || y == 0) {
							cancelFallDown = true;
							break;
						}

						nodeType = this.getPathNodeType(this.entity, x, y, z);
						malus = this.entity.getPathPriority(nodeType);

						if(nodeType != PathNodeType.OPEN && malus >= 0.0F) {
							fallPathPoint = this.openPoint(x, y, z);
							fallPathPoint.nodeType = nodeType;
							fallPathPoint.costMalus = Math.max(fallPathPoint.costMalus, malus);
							break;
						}

						if(malus < 0.0F) {
							cancelFallDown = true;
						}
					}

					boolean hasPathUp = false;

					if(this.pathableFacings.contains(EnumFacing.UP)) {
						nodeType = this.getPathNodeType(this.entity, x, preFallY, z);
						malus = this.entity.getPathPriority(nodeType);

						if(nodeType != PathNodeType.OPEN && malus >= 0.0F) {
							if(fallPathPoint != null) {
								result = new PathPoint[2];
								result[1] = fallPathPoint;
							}

							result[0] = directPathPoint = this.openPoint(x, preFallY, z);
							directPathPoint.nodeType = nodeType;
							directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
							hasPathUp = true;
						}
					}

					if(fallPathPoint != null) {
						if(!hasPathUp) {
							result[0] = directPathPoint = fallPathPoint;
						} else {
							result = new PathPoint[2];
							result[0] = directPathPoint;
							result[1] = fallPathPoint;
						}
					}

					if(fallPathPoint != null) {
						float bridingMalus = this.obstructionAwareEntity.getBridgePathingMalus(this.obstructionAwareEntity, new BlockPos(x, preFallY, z), fallPathPoint);

						if(bridingMalus >= 0.0f) {
							result = new PathPoint[2];
							result[0] = directPathPoint;

							PathPoint bridgePathPoint = this.openPoint(x, preFallY, z);
							bridgePathPoint.nodeType = PathNodeType.WALKABLE;
							bridgePathPoint.costMalus = Math.max(bridgePathPoint.costMalus, bridingMalus);
							result[1] = bridgePathPoint;
						}
					}

					if(cancelFallDown && !hasPathUp) {
						result[0] = null;
						if(result.length == 2) {
							result[1] = null;
						}
						return result;
					}
				}

				result[0] = directPathPoint;
				return result;
			}
		}
	}

	private PathNodeType getPathNodeType(EntityLiving entitylivingIn, int x, int y, int z) {
		return this.getPathNodeType(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanOpenDoors(), this.getCanEnterDoors());
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
		return this.getPathNodeType(blockaccessIn, x, y, z, EnumSet.noneOf(EnumFacing.class));
	}

	protected PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EnumSet<EnumFacing> exemptions) {
		PathNodeType nodeType = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);

		if(nodeType == PathNodeType.OPEN && y >= 1) {
			PooledMutableBlockPos pos = PooledMutableBlockPos.retain();

			facings: for(EnumFacing pathableFacing : this.pathableFacings) {
				if(!exemptions.contains(pathableFacing)) {
					int checkHeight = pathableFacing.getAxis() != Axis.Y ? Math.min(4, this.pathingSizeOffsetY - 1) : 0;

					int cx = x + pathableFacing.getXOffset() * this.pathingSizeOffsetX;
					int cy = y + (pathableFacing == EnumFacing.DOWN ? -1 : pathableFacing == EnumFacing.UP ? this.pathingSizeOffsetY : 0);
					int cz = z + pathableFacing.getZOffset() * this.pathingSizeOffsetZ;

					for(int yo = 0; yo <= checkHeight; yo++) {
						pos.setPos(cx, cy + yo, cz);

						Block block = blockaccessIn.getBlockState(pos).getBlock();
						PathNodeType offsetNodeType = this.getPathNodeTypeRaw(blockaccessIn, pos.getX(), pos.getY(), pos.getZ());
						nodeType = offsetNodeType != PathNodeType.WALKABLE && offsetNodeType != PathNodeType.OPEN && offsetNodeType != PathNodeType.WATER && offsetNodeType != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

						if(offsetNodeType == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
							nodeType = PathNodeType.DAMAGE_FIRE;
						}

						if(offsetNodeType == PathNodeType.DAMAGE_CACTUS) {
							nodeType = PathNodeType.DAMAGE_CACTUS;
						}

						if(nodeType == PathNodeType.WALKABLE) {
							break facings;
						}
					}
				}
			}

			pos.release();
		}

		nodeType = this.checkNeighborBlocks(blockaccessIn, x, y, z, nodeType);
		return nodeType;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entity, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		BlockPos pos = new BlockPos(entity);

		EnumSet<PathNodeType> applicablePathNodeTypes = EnumSet.noneOf(PathNodeType.class);
		PathNodeType centerPathNodeType = this.getPathNodeType(blockaccessIn, x, y, z, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn, applicablePathNodeTypes, PathNodeType.BLOCKED, pos);

		if(applicablePathNodeTypes.contains(PathNodeType.FENCE)) {
			return PathNodeType.FENCE;
		} else {
			PathNodeType selectedPathNodeType = PathNodeType.BLOCKED;

			for(PathNodeType applicablePathNodeType : applicablePathNodeTypes) {
				if(entity.getPathPriority(applicablePathNodeType) < 0.0F) {
					return applicablePathNodeType;
				}

				float p1 = entity.getPathPriority(applicablePathNodeType);
				float p2 = entity.getPathPriority(selectedPathNodeType);
				if(p1 > p2 || (p1 == p2 && !(selectedPathNodeType == PathNodeType.WALKABLE && applicablePathNodeType == PathNodeType.OPEN)) || (p1 == p2 && selectedPathNodeType == PathNodeType.OPEN && applicablePathNodeType == PathNodeType.WALKABLE)) {
					selectedPathNodeType = applicablePathNodeType;
				}
			}

			if(centerPathNodeType == PathNodeType.OPEN && entity.getPathPriority(selectedPathNodeType) == 0.0F) {
				return PathNodeType.OPEN;
			} else {
				return selectedPathNodeType;
			}
		}
	}
}