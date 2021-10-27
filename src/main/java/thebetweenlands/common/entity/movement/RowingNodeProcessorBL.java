package thebetweenlands.common.entity.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

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

public class RowingNodeProcessorBL extends NodeProcessor {

	public RowingNodeProcessorBL() {
	}

	@Override
	public PathPoint getStart() {
		int startY = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D);
		BlockPos blockpos1 = new BlockPos(this.entity);
		PathNodeType startNodeType = this.isFree(blockpos1.getX(), startY, blockpos1.getZ());

		if (this.entity.getPathPriority(startNodeType) < 0.0F) {
			Set<BlockPos> set = Sets.<BlockPos>newHashSet();
			set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, startY, this.entity.getEntityBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, startY, this.entity.getEntityBoundingBox().maxZ));
			set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, startY, this.entity.getEntityBoundingBox().minZ));
			set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, startY, this.entity.getEntityBoundingBox().maxZ));

			for (BlockPos blockpos : set) {
				PathNodeType pathnodetype = this.isFree(blockpos.getX(), blockpos.getY(), blockpos.getZ());//this.getPathNodeType(this.entity, blockpos);

				if (pathnodetype != PathNodeType.BLOCKED) {
					return this.openPoint(blockpos.getX(), blockpos.getY(), blockpos.getZ());
				}
			}
		}

		return this.openPoint(blockpos1.getX(), startY, blockpos1.getZ());
	}

	@Override
	public PathPoint getPathPointToCoords(double x, double y, double z) {
		return this.openPoint(MathHelper.floor(x - (double) (this.entity.width / 2.0F)), MathHelper.floor(y + 0.5D), MathHelper.floor(z - (double) (this.entity.width / 2.0F)));
	}

	@Override
	public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
		int i = 0;
		for (EnumFacing enumfacing : EnumFacing.VALUES) {
			PathPoint pathpoint = this.getAirNode(currentPoint.x + enumfacing.getXOffset(), currentPoint.y + enumfacing.getYOffset(), currentPoint.z + enumfacing.getZOffset(), targetPoint);
			if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
				pathOptions[i++] = pathpoint;
			}
		}
		return i;
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
		return this.isFree(x, y, z);
	}

	@Override
	public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
		return this.isFree(x, y, z);
	}

	@Nullable
	private PathPoint getAirNode(int x, int y, int z, PathPoint targetPoint) {
		PathNodeType pathnodetype = this.isFree(x, y, z);
		if (pathnodetype == PathNodeType.OPEN) {
			PathPoint pathPoint = this.openPoint(x, y, z);
			PathPoint cached = this.pointMap.lookup(PathPoint.makeHash(x, y - 1, z));
			if (cached != null && cached.visited && cached.nodeType == PathNodeType.BLOCKED) {
				return null;
			} else if (this.isFree(x, y - 1, z) == PathNodeType.BLOCKED) {
				return null;
			}
			return pathPoint;
		}
		return null;
	}

	private PathNodeType isFree(int x, int y, int z) {
		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();
		BlockPos.MutableBlockPos checkPosBelow = new BlockPos.MutableBlockPos();
		for (int i = x; i < x + this.entitySizeX; ++i) {
			for (int j = y; j < y + this.entitySizeY; ++j) {
				for (int k = z; k < z + this.entitySizeZ; ++k) {
					checkPos.setPos(i, j, k);
					checkPosBelow.setPos(i, j-1, k);

					IBlockState state = this.blockaccess.getBlockState(checkPos);
					IBlockState stateBelow = this.blockaccess.getBlockState(checkPosBelow);
					state = state.getActualState(this.blockaccess, checkPos);
					stateBelow = state.getActualState(this.blockaccess, checkPosBelow);

					List<AxisAlignedBB> collidingAABBs = new ArrayList<>();
					if(stateBelow.getMaterial().blocksMovement())
						state.addCollisionBoxToList(this.entity.world, checkPos, new AxisAlignedBB(x, y, z, x + this.entitySizeX, y + this.entitySizeY, z + this.entitySizeZ), collidingAABBs, this.entity, true);
					state.addCollisionBoxToList(this.entity.world, checkPos, new AxisAlignedBB(x, y, z, x + this.entitySizeX, y + this.entitySizeY, z + this.entitySizeZ), collidingAABBs, this.entity, true);
					if(!collidingAABBs.isEmpty()) {
						return PathNodeType.BLOCKED;
					}
				}
			}
		}
		return PathNodeType.OPEN;
	}
}