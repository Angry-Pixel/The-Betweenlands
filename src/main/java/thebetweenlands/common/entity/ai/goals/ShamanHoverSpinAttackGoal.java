package thebetweenlands.common.entity.ai.goals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.monster.EmberlingShaman;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ShamanHoverSpinAttackGoal extends Goal {
	private final EmberlingShaman emberling;
	@Nullable
	private LivingEntity target;
	private final float motionY;
	private float rotation;

	public ShamanHoverSpinAttackGoal(EmberlingShaman emberling, float motionY) {
		this.emberling = emberling;
		this.motionY = motionY;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		this.target = this.emberling.getTarget();
		this.rotation = 0F;

		if (this.target == null)
			return false;
		else {
			double distance = this.emberling.distanceToSqr(this.target);
			if (distance >= 4.0D && distance <= 36.0D) {
				if (!this.emberling.onGround())
					return false;
				else
					return this.emberling.getRandom().nextInt(5) == 0;
			} else
				return false;
		}
	}

	@Override
	public boolean canContinueToUse() {
		return !this.emberling.onGround();
	}

	@Override
	public void start() {
		if (this.emberling.isCastingSpell())
			this.emberling.setCastingSpell(false);
		this.emberling.playSound(SoundRegistry.EMBERLING_JUMP.get());
		double d0 = this.target.getX() - this.emberling.getX();
		double d1 = this.target.getZ() - this.emberling.getZ();
		double f = Mth.sqrt((float) (d0 * d0 + d1 * d1));
		if (f >= 1.0E-4D) {
			this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().add(
				d0 / f * 0.5D * 0.8D + this.emberling.getDeltaMovement().x() * 0.2D,
				0.0D,
				d1 / f * 0.5D * 0.8D + this.emberling.getDeltaMovement().z() * 0.2D
			));
		}
		this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().x(), this.motionY, this.emberling.getDeltaMovement().z());
	}

	@Override
	public void tick() {
		this.rotation += 30;
		this.emberling.absRotateTo(this.rotation, 0F);
		if (this.emberling.getDeltaMovement().y() < 0)
			this.emberling.setDeltaMovement(this.emberling.getDeltaMovement().multiply(1.0D, 0.5D, 1.0D));
	}
}
