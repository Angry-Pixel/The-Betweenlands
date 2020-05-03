package thebetweenlands.common.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGroundObstructionCheck<T extends EntityLiving & IPathObstructionCallback> extends PathNavigateGround {
	public static interface PathObstructionCallback {
		public void onPathObstructed();
	}

	protected final T callbackEntity;

	protected int stuckCheckTicks = 0;

	public PathNavigateGroundObstructionCheck(T entity, World worldIn) {
		super(entity, worldIn);
		this.callbackEntity = entity;

		if(this.nodeProcessor instanceof WalkNodeProcessorObstructionCheck) {
			((WalkNodeProcessorObstructionCheck) this.nodeProcessor).setCallback(entity);
		}
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new WalkNodeProcessorObstructionCheck();
		this.nodeProcessor.setCanEnterDoors(true);
		return new PathFinder(this.nodeProcessor);
	}

	@Override
	protected void checkForStuck(Vec3d entityPos) {
		super.checkForStuck(entityPos);

		if(this.currentPath != null && !this.currentPath.isFinished()) {
			Vec3d target = this.currentPath.getVectorFromIndex(this.callbackEntity, Math.min(this.currentPath.getCurrentPathLength() - 1, this.currentPath.getCurrentPathIndex() + 0));
			Vec3d diff = target.subtract(entityPos);

			boolean isFacingX = Math.abs(diff.x) > Math.abs(diff.z);

			int height = MathHelper.floor(this.callbackEntity.height + 1.0F);

			int ceilHalfWidth = MathHelper.ceil(this.callbackEntity.width / 2.0f + 0.05F);

			Vec3d checkPos;
			if(isFacingX) {
				checkPos = new Vec3d(entityPos.x + Math.signum(diff.x) * ceilHalfWidth, entityPos.y, target.z);
			} else {
				checkPos = new Vec3d(target.x, entityPos.y, entityPos.z + Math.signum(diff.z) * ceilHalfWidth);
			}

			boolean blocked = false;

			loop: for(int yo = 0; yo < height; yo++) {
				for(int xzo = -ceilHalfWidth; xzo <= ceilHalfWidth; xzo++) {
					BlockPos pos = new BlockPos(checkPos.x + (!isFacingX ? xzo : 0), checkPos.y + yo, checkPos.z + (isFacingX ? xzo : 0));

					IBlockState state = this.callbackEntity.world.getBlockState(pos);

					PathNodeType nodeType = state.getBlock().isPassable(this.callbackEntity.world, pos) ? PathNodeType.OPEN : PathNodeType.BLOCKED;

					if(nodeType == PathNodeType.BLOCKED) {
						AxisAlignedBB collisionBox = state.getCollisionBoundingBox(this.callbackEntity.world, pos);

						if(collisionBox != null && collisionBox.offset(pos).intersects(this.callbackEntity.getEntityBoundingBox().expand(Math.signum(diff.x) * 0.2D, 0, Math.signum(diff.z) * 0.2D))) {
							blocked = true;
							break loop;
						}
					}
				}
			}

			if(blocked) {
				this.stuckCheckTicks++;

				if(this.stuckCheckTicks > 40) {
					this.callbackEntity.onPathingObstructed();
					this.stuckCheckTicks = 0;
				}
			} else {
				this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 4, 0);
			}
		} else {
			this.stuckCheckTicks = Math.max(this.stuckCheckTicks - 6, 0);
		}
	}
}