package thebetweenlands.common.entity.ai;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class FlyingNodeProcessorBL extends NodeProcessor {
	public int preferredMinHeight = -1;

	public FlyingNodeProcessorBL() {
		this.preferredMinHeight = -1;
	}

	public FlyingNodeProcessorBL(int preferredMinHeight) {
		this.preferredMinHeight = preferredMinHeight;
	}

	@Override
	public PathPoint getStart() {
		return this.openPoint(MathHelper.floor(this.entity.getEntityBoundingBox().minX), MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.getEntityBoundingBox().minZ));
	}

	@Override
	public PathPoint getPathPointToCoords(double x, double y, double z) {
		return this.openPoint(MathHelper.floor(x - (double) (this.entity.width / 2.0F)), MathHelper.floor(y + 0.5D), MathHelper.floor(z - (double) (this.entity.width / 2.0F)));
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		int i = 0;
		for (EnumFacing enumfacing : EnumFacing.values()) {
			PathPoint pathpoint = this.getAirNode(currentPoint.x + enumfacing.getFrontOffsetX(), currentPoint.y + enumfacing.getFrontOffsetY(), currentPoint.z + enumfacing.getFrontOffsetZ(), targetPoint);
			if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance)
				pathOptions[i++] = pathpoint;
		}
		return i;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		return PathNodeType.OPEN;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
		return PathNodeType.OPEN;
	}

	@Nullable
	private PathPoint getAirNode(int x, int y, int z, PathPoint targetPoint) {
		PathNodeType pathnodetype = this.isFree(x, y, z);
		if(pathnodetype == PathNodeType.OPEN) {
			PathPoint pathPoint = this.openPoint(x, y, z);
			if(this.preferredMinHeight > 0) {
				int height = 0;
				for(int yOff = 0; yOff <= this.preferredMinHeight; yOff++) {
					PathPoint cached = this.pointMap.lookup(PathPoint.makeHash(x, y - yOff, z));
					if(cached != null && cached.visited && cached.nodeType == PathNodeType.BLOCKED) {
						break;
					} else if(this.isFree(x, y - yOff, z) == PathNodeType.BLOCKED) {
						break;
					}
					height++;
				}
				if(height <= this.preferredMinHeight && height > 0) {
					double heightCostPenalty = (height / (double)this.preferredMinHeight * 4 + 1) * 2.0D * Math.sqrt(MathHelper.clamp((targetPoint.distanceTo(pathPoint) - height) / (double)height, 0.0D, 1.0D));
					pathPoint.costMalus += heightCostPenalty;
				}
			}
			return pathPoint;
		}
		return null;
	}

	private PathNodeType isFree(int x, int y, int z) {
		BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
		for (int i = x; i < x + this.entitySizeX; ++i) {
			for (int j = y; j < y + this.entitySizeY; ++j) {
				for (int k = z; k < z + this.entitySizeZ; ++k) {
					IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

					/*if (iblockstate.getMaterial() != Material.AIR && iblockstate.getMaterial() != Material.PLANTS)
						return PathNodeType.BLOCKED;*/

					List<AxisAlignedBB> collidingAABBs = new ArrayList<>();
					iblockstate.addCollisionBoxToList(this.entity.world, blockpos$mutableblockpos.setPos(i, j, k), new AxisAlignedBB(x, y, z, x + this.entitySizeX, y + this.entitySizeY, z + this.entitySizeZ), collidingAABBs, this.entity, true);
					if(!collidingAABBs.isEmpty()) {
						return PathNodeType.BLOCKED;
					}
				}
			}
		}
		return PathNodeType.OPEN;
	}
}