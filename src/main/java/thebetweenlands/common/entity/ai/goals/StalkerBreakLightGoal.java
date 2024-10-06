package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.datagen.tags.BLBlockTagProvider;
import thebetweenlands.common.entity.monster.Stalker;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class StalkerBreakLightGoal extends Goal {

	private final Stalker entity;

	@Nullable
	private BlockPos lightSourcePos = null;

	private int failCount = 0;

	public StalkerBreakLightGoal(Stalker entity) {
		this.entity = entity;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Nullable
	private BlockHitResult rayTraceBlockLight(Vec3 start, Vec3 end) {
		if (!Double.isNaN(start.x) && !Double.isNaN(start.y) && !Double.isNaN(start.z)) {
			if (!Double.isNaN(end.x) && !Double.isNaN(end.y) && !Double.isNaN(end.z)) {
				int ex = Mth.floor(end.x);
				int ey = Mth.floor(end.y);
				int ez = Mth.floor(end.z);
				int sx = Mth.floor(start.x);
				int sy = Mth.floor(start.y);
				int sz = Mth.floor(start.z);
				BlockPos pos = new BlockPos(sx, sy, sz);
				BlockState state = this.entity.level().getBlockState(pos);

				if (this.entity.level().getBrightness(LightLayer.BLOCK, pos) > 0 && !state.isAir() && !state.getShape(this.entity.level(), pos).isEmpty()) {
					BlockHitResult result = this.entity.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.entity));

					if (result.getType() != HitResult.Type.MISS) {
						return result;
					} else {
						return new BlockHitResult(new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f), Direction.UP, pos, true);
					}
				}

				int steps = 200;

				while (steps-- >= 0) {
					if (Double.isNaN(start.x) || Double.isNaN(start.y) || Double.isNaN(start.z)) {
						return null;
					}

					if (sx == ex && sy == ey && sz == ez) {
						return null;
					}

					boolean xDiff = true;
					boolean yDiff = true;
					boolean zDiff = true;
					double newX = 999.0D;
					double newY = 999.0D;
					double newZ = 999.0D;

					if (ex > sx) {
						newX = (double) sx + 1.0D;
					} else if (ex < sx) {
						newX = (double) sx + 0.0D;
					} else {
						xDiff = false;
					}

					if (ey > sy) {
						newY = (double) sy + 1.0D;
					} else if (ey < sy) {
						newY = (double) sy + 0.0D;
					} else {
						yDiff = false;
					}

					if (ez > sz) {
						newZ = (double) sz + 1.0D;
					} else if (ez < sz) {
						newZ = (double) sz + 0.0D;
					} else {
						zDiff = false;
					}

					double offsetX = 999.0D;
					double offsetY = 999.0D;
					double offsetZ = 999.0D;
					double dx = end.x - start.x;
					double dy = end.y - start.y;
					double dz = end.z - start.z;

					if (xDiff) {
						offsetX = (newX - start.x) / dx;
					}

					if (yDiff) {
						offsetY = (newY - start.y) / dy;
					}

					if (zDiff) {
						offsetZ = (newZ - start.z) / dz;
					}

					if (offsetX == -0.0D) {
						offsetX = -1.0E-4D;
					}

					if (offsetY == -0.0D) {
						offsetY = -1.0E-4D;
					}

					if (offsetZ == -0.0D) {
						offsetZ = -1.0E-4D;
					}

					Direction hitFacing;

					if (offsetX < offsetY && offsetX < offsetZ) {
						hitFacing = ex > sx ? Direction.WEST : Direction.EAST;
						start = new Vec3(newX, start.y + dy * offsetX, start.z + dz * offsetX);
					} else if (offsetY < offsetZ) {
						hitFacing = ey > sy ? Direction.DOWN : Direction.UP;
						start = new Vec3(start.x + dx * offsetY, newY, start.z + dz * offsetY);
					} else {
						hitFacing = ez > sz ? Direction.NORTH : Direction.SOUTH;
						start = new Vec3(start.x + dx * offsetZ, start.y + dy * offsetZ, newZ);
					}

					sx = Mth.floor(start.x) - (hitFacing == Direction.EAST ? 1 : 0);
					sy = Mth.floor(start.y) - (hitFacing == Direction.UP ? 1 : 0);
					sz = Mth.floor(start.z) - (hitFacing == Direction.SOUTH ? 1 : 0);
					pos = new BlockPos(sx, sy, sz);
					BlockState offsetState = this.entity.level().getBlockState(pos);

					if (this.entity.level().getBrightness(LightLayer.BLOCK, pos) > 0 && !offsetState.isAir() && !offsetState.getShape(this.entity.level(), pos).isEmpty()) {
						BlockHitResult result = this.entity.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.entity));

						if (result.getType() != HitResult.Type.MISS) {
							return result;
						} else {
							return new BlockHitResult(new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f), Direction.UP, pos, true);
						}
					}
				}

				return null;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Nullable
	private BlockPos findLightSource(BlockPos pos, int maxChecks) {
		int lightValue = this.entity.level().getBrightness(LightLayer.BLOCK, pos);

		if (lightValue > 0) {
			Set<BlockPos> checked = new HashSet<>();
			PriorityQueue<Pair<BlockPos, Integer>> queue = new PriorityQueue<>(1, (p1, p2) -> -Integer.compare(p1.getRight(), p2.getRight()));

			queue.add(Pair.of(pos, lightValue));

			while (!queue.isEmpty() && maxChecks-- > 0) {
				Pair<BlockPos, Integer> queueEntry = queue.remove();
				BlockPos queuePos = queueEntry.getLeft();
				int queueLight = queueEntry.getRight();

				BlockState state = this.entity.level().getBlockState(queuePos);
				if (state.getLightEmission(this.entity.level(), queuePos) > 0) {
					return queuePos;
				}

				for (Direction offset : Direction.values()) {
					BlockPos offsetPos = queuePos.relative(offset);

					if (this.entity.level().isLoaded(offsetPos)) {
						lightValue = this.entity.level().getBrightness(LightLayer.BLOCK, offsetPos);

						if (lightValue > queueLight && checked.add(offsetPos)) {
							queue.add(Pair.of(offsetPos, lightValue));
						}
					}
				}
			}
		}

		return null;
	}

	private boolean isTargetLightSource(BlockPos pos) {
		LivingEntity target = this.entity.getTarget();

		if (target != null) {
			if (target.distanceToSqr(Vec3.atCenterOf(pos)) < this.entity.stalkingDistanceFar * this.entity.stalkingDistanceFar) {
				return false;
			}

			Vec3 look = target.getViewVector(1);
			Vec3 dir = new Vec3(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f).subtract(target.getEyePosition().subtract(look)).normalize();
			float dot = (float) look.dot(dir);
			float angle = (float) Math.toDegrees(Math.acos(dot));

			if (angle < this.entity.farAngle) {
				return false;
			}
		}

		BlockState state = this.entity.level().getBlockState(pos);

		if (!state.is(BLBlockTagProvider.STALKER_IGNORED_LIGHT_SOURCES)) {
			float hardness = state.getDestroySpeed(this.entity.level(), pos);
			return this.entity.level().getBlockState(pos).getLightEmission(this.entity.level(), pos) > 0 && hardness >= 0 && hardness <= 2.5F && state.canEntityDestroy(this.entity.level(), pos, this.entity);
		}

		return false;
	}

	@Override
	public boolean canUse() {
		if (this.entity.isStalking && this.entity.tickCount % 10 == 0 && EventHooks.canEntityGrief(this.entity.level(), this.entity)) {
			float checkRange = 32.0f;

			Vec3 start = this.entity.getEyePosition();
			Vec3 end = start.add(new Vec3(this.entity.getRandom().nextFloat() - 0.5f, this.entity.getRandom().nextFloat() - 0.5f, this.entity.getRandom().nextFloat() - 0.5f).normalize().scale(checkRange));

			BlockHitResult result = this.rayTraceBlockLight(start, end);

			if (result != null) {
				BlockPos lightPos = this.findLightSource(result.getBlockPos(), 32);

				if (lightPos != null && this.isTargetLightSource(lightPos)) {
					if (this.entity.getNavigation().createPath(lightPos.getX() + 0.5f, lightPos.getY() + 0.5f, lightPos.getZ() + 0.5f, 0) != null) {
						this.lightSourcePos = lightPos;
					}
				}
			}

			return this.lightSourcePos != null;
		}

		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return this.entity.isStalking && this.lightSourcePos != null && this.isTargetLightSource(this.lightSourcePos) && EventHooks.canEntityGrief(this.entity.level(), this.entity) && this.failCount <= 5;
	}

	@Override
	public void stop() {
		this.lightSourcePos = null;
		this.failCount = 0;
		this.entity.getNavigation().stop();
	}

	@Override
	public void tick() {
		if (this.lightSourcePos != null) {
			if (this.failCount == 0 || this.entity.tickCount % 20 == 0) {
				if (!this.entity.getNavigation().moveTo(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f, this.lightSourcePos.getZ() + 0.5f, 1)) {
					this.failCount++;
				} else {
					this.failCount = 1;
				}
			}

			if (this.entity.distanceToSqr(this.lightSourcePos.getX() + 0.5f, this.lightSourcePos.getY() + 0.5f - this.entity.getBbHeight() / 2.0f, this.lightSourcePos.getZ() + 0.5f) < 2.0f) {
				BlockState state = this.entity.level().getBlockState(this.lightSourcePos);

				if (EventHooks.canEntityGrief(this.entity.level(), this.entity) && this.isTargetLightSource(this.lightSourcePos) && EventHooks.onEntityDestroyBlock(this.entity, this.lightSourcePos, state)) {
					this.entity.level().destroyBlock(this.lightSourcePos, true, this.entity);
				} else {
					this.lightSourcePos = null;
				}
			}
		}
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
