package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.creature.Emberling;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class EmberlingFireBreathGoal extends Goal {
	private final Emberling emberling;
	@Nullable
	private LivingEntity target;
	private int missileCount;
	private int shootCount;

	public EmberlingFireBreathGoal(Emberling emberling) {
		this.emberling = emberling;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		this.target = this.emberling.getTarget();

		if (this.target == null || this.emberling.isInWater() || this.emberling.isInSittingPose())
			return false;
		else {
			double distance = this.emberling.distanceToSqr(this.target);
			if (distance >= 4.0D && distance <= 25.0D) {
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
	public void stop() {
		this.shootCount = -1;
		this.missileCount = -1;
		if (this.emberling.isShootingFlames())
			this.emberling.setShootingFlames(false);
	}

	@Override
	public void tick() {
		if (!this.emberling.isShootingFlames())
			this.emberling.setShootingFlames(true);
		this.emberling.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
		float f = (float) Mth.atan2(this.target.getZ() - this.emberling.getZ(), this.target.getX() - this.emberling.getX());
		int distance = Mth.floor(this.emberling.distanceTo(this.target));
		this.missileCount++;
		if (this.missileCount % 5 == 0) {
			this.shootCount++;
			double d2 = this.shootCount;
			AABB flameBox = new AABB(BlockPos.containing(this.emberling.getX() + Mth.cos(f) * d2, this.emberling.getY(), this.emberling.getZ() + Mth.sin(f) * d2));
			List<LivingEntity> list = this.emberling.level().getEntitiesOfClass(LivingEntity.class, flameBox);
			for (LivingEntity entity : list) {
				if (entity != null && entity == this.target)
					if (!entity.isOnFire() && !entity.fireImmune())
						entity.igniteForSeconds(5); // seems ok for time
			}
		}
		if (this.shootCount >= distance || this.shootCount >= 4)
			this.stop();
	}
}
