package thebetweenlands.common.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawGreeblingRider;
import thebetweenlands.common.registries.ItemRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ChiromawRiderSlingshotGoal extends Goal {

	private final ChiromawGreeblingRider chiromawRider;
	@Nullable
	private LivingEntity target;

	public ChiromawRiderSlingshotGoal(ChiromawGreeblingRider chiromawRider) {
		this.chiromawRider = chiromawRider;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
	}

	@Override
	public boolean canUse() {
		this.target = this.chiromawRider.getTarget();

		if (this.target == null) {
			return false;
		} else if (!this.chiromawRider.getOffhandItem().is(ItemRegistry.SLINGSHOT)) {
			return false;
		} else {
			double distance = this.chiromawRider.distanceToSqr(this.target);
			return distance >= 36.0D && distance <= 576.0D && this.chiromawRider.getReloadTimer() >= 90;
		}
	}

	@Override
	public boolean canContinueToUse() {
		return this.target != null && this.chiromawRider.getLastHurtByPlayerTimestamp() <= 40 && this.chiromawRider.getReloadTimer() >= 90;
	}

	@Override
	public void start() {
		//this.chiromawRider.playSound(SoundRegistry.GREEBLING_HEY.get());
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		if (!this.chiromawRider.isShooting())
			this.chiromawRider.setShooting(true);
		if (this.target != null) {
			this.chiromawRider.lookAt(this.target, 30F, 30F);
			this.chiromawRider.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
			if (this.chiromawRider.getReloadTimer() == 90) {
				this.chiromawRider.performRangedAttack(this.target, 1.6F);
			}
		}
		if (this.chiromawRider.getReloadTimer() == 100) {
			if (this.chiromawRider.isShooting()) {
				this.chiromawRider.setShooting(false);
				this.chiromawRider.setReloadTimer(0);
				this.chiromawRider.playPullSound = true;
			}
			this.stop();
		}
	}

	@Override
	public void stop() {
		this.target = null;
	}
}
