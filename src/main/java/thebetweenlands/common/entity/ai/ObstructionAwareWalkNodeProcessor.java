package thebetweenlands.common.entity.ai;

import javax.annotation.Nullable;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ObstructionAwareWalkNodeProcessor<T extends EntityLiving & IPathObstructionAwareEntity> extends WalkNodeProcessor {
	private T callbackEntity;

	public void setCallback(T callback) {
		this.callbackEntity = callback;
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

		PathPoint[] pathsPZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, true);
		PathPoint[] pathsNX = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z, stepHeight, height, EnumFacing.WEST, true);
		PathPoint[] pathsPX = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z, stepHeight, height, EnumFacing.EAST, true);
		PathPoint[] pathsNZ = this.getSafePoints(currentPoint.x, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, true);

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

		//Also check for negative Y paths since those could be alternative obstructed paths
		PathPoint[] pathsNY = this.getSafePoints(currentPoint.x, currentPoint.y - 1, currentPoint.z, stepHeight, height, EnumFacing.DOWN, true);
		for(int k = 0; k < pathsNY.length; k++) {
			if(pathsNY[k] != null && !pathsNY[k].visited && pathsNY[k].distanceTo(targetPoint) < maxDistance) {
				pathOptions[openedNodeCount++] = pathsNY[k];
			}
		}

		boolean avoidPathNZ = pathsNZ.length == 0 || (pathsNZ[0] != null && (pathsNZ[0].nodeType == PathNodeType.OPEN || pathsNZ[0].costMalus != 0.0F));
		boolean avoidPathPZ = pathsPZ.length == 0 || (pathsPZ[0] != null && (pathsPZ[0].nodeType == PathNodeType.OPEN || pathsPZ[0].costMalus != 0.0F));
		boolean avoidPathPX = pathsPX.length == 0 || (pathsPX[0] != null && (pathsPX[0].nodeType == PathNodeType.OPEN || pathsPX[0].costMalus != 0.0F));
		boolean avoidPathNX = pathsNX.length == 0 || (pathsNX[0] != null && (pathsNX[0].nodeType == PathNodeType.OPEN || pathsNX[0].costMalus != 0.0F));

		if(avoidPathNZ && avoidPathNX) {
			PathPoint[] pathsNXNZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, true);

			for(int k = 0; k < pathsNXNZ.length; k++) {
				if(pathsNXNZ[k] != null && !pathsNXNZ[k].visited && pathsNXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXNZ[k];
				}
			}
		}

		if(avoidPathNZ && avoidPathPX) {
			PathPoint[] pathsPXNZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, stepHeight, height, EnumFacing.NORTH, true);

			for(int k = 0; k < pathsPXNZ.length; k++) {
				if(pathsPXNZ[k] != null && !pathsPXNZ[k].visited && pathsPXNZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXNZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathNX) {
			PathPoint[] pathsNXPZ = this.getSafePoints(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, true);

			for(int k = 0; k < pathsNXPZ.length; k++) {
				if(pathsNXPZ[k] != null && !pathsNXPZ[k].visited && pathsNXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsNXPZ[k];
				}
			}
		}

		if(avoidPathPZ && avoidPathPX) {
			PathPoint[] pathsPXPZ = this.getSafePoints(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, stepHeight, height, EnumFacing.SOUTH, true);

			for(int k = 0; k < pathsPXPZ.length; k++) {
				if(pathsPXPZ[k] != null && !pathsPXPZ[k].visited && pathsPXPZ[k].distanceTo(targetPoint) < maxDistance) {
					pathOptions[openedNodeCount++] = pathsPXPZ[k];
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

			float malus = this.callbackEntity.getPathingMalus(this.callbackEntity, nodeType, pos); //Replaces EntityLiving#getPathPriority

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
				if (directPathPoint == null && stepHeight > 0 && nodeType != PathNodeType.FENCE && nodeType != PathNodeType.TRAPDOOR) {
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
						PathNodeType nodeTypeBelow = this.getPathNodeType(this.entity, x, y - 1, z);

						if(nodeTypeBelow == PathNodeType.BLOCKED) {
							directPathPoint = this.openPoint(x, y, z);
							directPathPoint.nodeType = PathNodeType.WALKABLE;
							directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
							result[0] = directPathPoint;
							return result;
						}
					}

					int fallDistance = 0;

					while (y > 0 && nodeType == PathNodeType.OPEN) {
						--y;

						if (fallDistance++ >= this.entity.getMaxFallHeight()) {
							result[0] = null;
							return result;
						}

						nodeType = this.getPathNodeType(this.entity, x, y, z);
						malus = this.entity.getPathPriority(nodeType);

						if(nodeType != PathNodeType.OPEN && malus >= 0.0F) {
							directPathPoint = this.openPoint(x, y, z);
							directPathPoint.nodeType = nodeType;
							directPathPoint.costMalus = Math.max(directPathPoint.costMalus, malus);
							break;
						}

						if(malus < 0.0F) {
							result[0] = null;
							return result;
						}
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
}