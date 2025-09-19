package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class MatriarchReturnToNestGoal extends Goal {
	private final ChiromawMatriarch matriarch;
	protected Vec3 flyTarget;
	private final double speed, speedHigh;
	private int timeToRecalcPath;

	public MatriarchReturnToNestGoal(ChiromawMatriarch matriarch, double speed, double speedHigh) {
		this.matriarch = matriarch;
		this.speed = speed;
		this.speedHigh = speedHigh;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		LivingEntity entity = this.matriarch.getTarget();
		if (entity != null)
			return false;
		if (this.matriarch.homeisOccupied())
			return false;
		if (this.matriarch.isReturningToNest() && !this.matriarch.isNesting()) {
			Vec3 nestLocation = this.getNestPosition();

			if (nestLocation == null) {
				return false;
			} else {
				this.flyTarget = nestLocation;
				return true;
			}
		}
		return false;
	}

	@Nullable
	protected Vec3 getNestPosition() {
		BlockPos home = this.matriarch.getRestrictCenter();
		home = this.matriarch.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, home);
		return new Vec3(home.getX() + 0.5D, home.getY(), home.getZ() + 0.5D);
	}

	@Override
	public void start() {
		this.moveTowardsNest();
	}

	@Override
	public boolean canContinueToUse() {
		return this.matriarch.isReturningToNest() && !this.matriarch.isNesting() && this.matriarch.getTarget() == null && !this.matriarch.homeisOccupied();
	}

	@Override
	public void tick() {
		if (--this.timeToRecalcPath <= 0) {
			if (!this.moveTowardsNest()) {
				this.timeToRecalcPath = 40;
			} else {
				this.timeToRecalcPath = 3;
			}
		}
	}

	private boolean moveTowardsNest() {
		if (this.matriarch.distanceToSqr(this.flyTarget) < 6) {
			this.matriarch.getMoveControl().setWantedPosition(this.flyTarget.x(), this.flyTarget.y(), this.flyTarget.z(), this.speed * 0.25F);
			this.matriarch.returnFast = false;
			return true;
		}

		PathNavigation navigator = this.matriarch.getNavigation();
		if (!navigator.moveTo(this.flyTarget.x(), this.flyTarget.y(), this.flyTarget.z(), this.matriarch.returnFast ? speedHigh : speed)) {
			Vec3 target = this.flyTarget;
			target = this.findNextPointTowards(target);
			if (!navigator.moveTo(target.x, target.y, target.z, this.matriarch.returnFast ? speedHigh : speed)) {
				this.matriarch.getMoveControl().setWantedPosition(target.x, target.y, target.z, this.matriarch.returnFast ? speedHigh : speed);
			}
			return false;
		}
		return true;
	}

	private @NotNull Vec3 findNextPointTowards(Vec3 target) {
		Vec3 offset = target.subtract(this.matriarch.position()).normalize().scale(8 + this.matriarch.getRandom().nextFloat() * 6);
		Vec3 side = offset.cross(new Vec3(0, 1, 0)).normalize();
		Vec3 up = side.cross(offset).normalize();
		return this.matriarch.position().add(offset).add(side.scale((this.matriarch.getRandom().nextFloat() - 0.5f) * 8)).add(up.scale((this.matriarch.getRandom().nextFloat() - 0.5f) * 8));
	}
}
