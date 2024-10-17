package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FollowTargetGoal<T extends LivingEntity> extends Goal {

	public static class FollowClosest<T extends LivingEntity> implements Supplier<T> {
		private final double range;
		private final Class<T> type;
		@Nullable
		private final Predicate<T> predicate;
		private final LivingEntity taskOwner;

		public FollowClosest(LivingEntity taskOwner, Class<T> type, double range) {
			this.taskOwner = taskOwner;
			this.type = type;
			this.range = range;
			this.predicate = null;
		}

		public FollowClosest(LivingEntity taskOwner, Class<T> type, Predicate<T> predicate, double range) {
			this.taskOwner = taskOwner;
			this.type = type;
			this.range = range;
			this.predicate = predicate;
		}

		@Override
		public T get() {
			List<T> entities = this.taskOwner.level().getEntitiesOfClass(this.type, this.taskOwner.getBoundingBox().inflate(this.range), this.predicate == null ? EntitySelector.NO_SPECTATORS : this.predicate);
			T closest = null;
			for(T entity : entities) {
				if(closest == null || entity.distanceToSqr(this.taskOwner) < closest.distanceToSqr(this.taskOwner)) {
					closest = entity;
				}
			}
			return closest;
		}
	}

	protected final Mob taskOwner;
	protected final Supplier<T> target;
	protected Level level;
	protected final double speed;
	protected final PathNavigation navigator;
	protected int timeToRecalcPath;
	protected float maxDist;
	protected float minDist;
	protected float oldWaterCost;
	protected boolean teleport;

	public FollowTargetGoal(Mob taskOwner, Supplier<T> target, double speed, float minDist, float maxDist, boolean teleport) {
		this.taskOwner = taskOwner;
		this.level = taskOwner.level();
		this.target = target;
		this.speed = speed;
		this.navigator = taskOwner.getNavigation();
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.teleport = teleport;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.target.get();

		if (target == null) {
			return false;
		} else if (target instanceof Player player && player.isSpectator()) {
			return false;
		} else return !(this.taskOwner.distanceToSqr(target) < (double) (this.minDist * this.minDist));
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.target.get();
		return target != null && !this.navigator.isDone() && this.taskOwner.distanceToSqr(target) > (double)(this.maxDist * this.maxDist);
	}

	@Override
	public void start() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.taskOwner.getPathfindingMalus(PathType.WATER);
		this.taskOwner.setPathfindingMalus(PathType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.navigator.stop();
		this.taskOwner.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
	}

	private boolean isEmptyBlock(BlockPos pos) {
		BlockState blockState = this.level.getBlockState(pos);
		return blockState.isAir() || !blockState.isRedstoneConductor(this.level, pos);
	}

	@Override
	public void tick() {
		LivingEntity target = this.target.get();

		if(target != null) {
			this.taskOwner.getLookControl().setLookAt(target, 10.0F, this.taskOwner.getMaxHeadXRot());

			if (--this.timeToRecalcPath <= 0) {
				this.timeToRecalcPath = 10;

				if (!this.navigator.moveTo(target, this.speed) && this.teleport) {
					if (!this.taskOwner.isLeashed()) {
						if (this.taskOwner.distanceToSqr(target) >= 144.0D) {
							int i = Mth.floor(target.getX()) - 2;
							int j = Mth.floor(target.getZ()) - 2;
							int k = Mth.floor(target.getBoundingBox().minY);

							for (int l = 0; l <= 4; ++l) {
								for (int i1 = 0; i1 <= 4; ++i1) {
									if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.level.getBlockState(new BlockPos(i + l, k - 1, j + i1)).isRedstoneConductor(this.level, new BlockPos(i + l, k - 1, j + i1)) && this.isEmptyBlock(new BlockPos(i + l, k, j + i1)) && this.isEmptyBlock(new BlockPos(i + l, k + 1, j + i1))) {
										this.taskOwner.moveTo((i + l) + 0.5F, k, (j + i1) + 0.5F, this.taskOwner.getYRot(), this.taskOwner.getXRot());
										this.navigator.stop();
										return;
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
