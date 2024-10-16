package thebetweenlands.common.entity.ai.goals;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import thebetweenlands.common.entity.FlameJet;
import thebetweenlands.common.entity.monster.EmberlingShaman;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ShamanFireColumnGoal extends Goal {

	private final EmberlingShaman emberling;
	@Nullable
	private LivingEntity target;
	private int missileCount;
	private int shootCount;

	public ShamanFireColumnGoal(EmberlingShaman emberling) {
		this.emberling = emberling;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
	}

	@Override
	public boolean canUse() {
		this.target = this.emberling.getTarget();

		if (this.target == null) {
			return false;
		} else {
			double distance = this.emberling.distanceToSqr(this.target);
			if (distance >= 36.0D && distance <= 144.0D) {
				if (!this.emberling.onGround())
					return false;
				else
					return this.emberling.getRandom().nextInt(8) == 0;
			} else
				return false;
		}
	}

	@Override
	public boolean canContinueToUse() {
		return this.shootCount != -1 && this.missileCount != -1 && this.emberling.getLastHurtByMobTimestamp() <= 40;
	}

	@Override
	public void start() {
		this.missileCount = 0;
		this.shootCount = 0;
		this.emberling.playSound(SoundRegistry.EMBERLING_FLAMES.get());
	}

	@Override
	public void tick() {
		if (!this.emberling.isCastingSpell())
			this.emberling.setCastingSpell(true);
		this.emberling.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
		float f = (float) Mth.atan2(this.target.getZ() - this.emberling.getZ(), this.target.getX() - this.emberling.getX());
		int distance = Mth.floor(this.emberling.distanceTo(this.target));
		this.missileCount++;
		if (this.missileCount % 5 == 0) {
			this.shootCount++;
			double d2 = this.shootCount;
			FlameJet jet = new FlameJet(this.emberling.level(), this.emberling);
			jet.moveTo(this.emberling.getX() + Mth.cos(f) * d2, this.emberling.getY(), this.emberling.getZ() + Mth.sin(f) * d2);
			this.emberling.level().addFreshEntity(jet);
			this.emberling.playSound(SoundEvents.FIRECHARGE_USE, 0.5F, 1F + (this.emberling.level().getRandom().nextFloat() - this.emberling.level().getRandom().nextFloat()) * 0.8F);
		}
		if (this.shootCount >= distance || this.shootCount >= 12) {
			this.shootCount = -1;
			this.missileCount = -1;
			if (this.emberling.isCastingSpell())
				this.emberling.setCastingSpell(false);
		}
	}
}
