package thebetweenlands.common.entity.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import thebetweenlands.common.entity.monster.Stalker;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.EnumSet;

public class StalkerScreechGoal extends Goal {

	private final Stalker entity;
	private final int minCooldown, maxCooldown;
	private final int minAllies, maxAllies;

	private int cooldown;

	private int allies;
	private boolean didSpawn;

	public StalkerScreechGoal(Stalker entity, int minCooldown, int maxCooldown, int minAllies, int maxAllies) {
		this.entity = entity;
		this.minCooldown = minCooldown;
		this.maxCooldown = maxCooldown;
		this.minAllies = minAllies;
		this.maxAllies = maxAllies;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean isInterruptable() {
		return false;
	}

	@Override
	public boolean canUse() {
		LivingEntity target = this.entity.getTarget();
		return this.cooldown-- <= 0 && !this.entity.isStalking && this.entity.getHealth() <= this.entity.getMaxHealth() * 0.5f && target != null && this.entity.distanceTo(target) > 8 && this.entity.canCallAllies();
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity target = this.entity.getTarget();
		return !this.entity.isStalking && target != null && this.entity.canCallAllies() && this.entity.isScreeching() && this.allies > 0 && this.entity.screechingTicks < 160;
	}

	@Override
	public void start() {
		this.cooldown = this.minCooldown + this.entity.getRandom().nextInt(this.maxCooldown - this.minCooldown + 1);
		this.entity.setScreeching(true);
		this.allies = this.minAllies + this.entity.getRandom().nextInt(this.maxAllies - this.minAllies + 1);
	}

	@Override
	public void stop() {
		this.entity.setScreeching(false);
		if (this.didSpawn) {
			this.entity.setCanCallAllies(false);
		}
	}

	@Override
	public void tick() {
		this.entity.setDropping(true);

		if (this.canContinueToUse() && this.entity.screechingTicks >= 80) {
			LivingEntity target = this.entity.getTarget();
			LookControl targetSenses = target instanceof Mob mob ? mob.getLookControl() : null;
			Vec3 targetLook = target.getLookAngle();

			Stalker stalker = null;

			for (int i = 0; i < 32 && this.allies > 0; i++) {
				float dx = this.entity.getRandom().nextFloat() - 0.5f;
				float dy = this.entity.getRandom().nextFloat() - 0.5f;
				float dz = this.entity.getRandom().nextFloat() - 0.5f;

				float dst = 8 + this.entity.getRandom().nextFloat() * 8;

				float scale = dst / Mth.sqrt(dx * dx + dy * dy + dz * dz);
				dx *= scale;
				dy *= scale;
				dz *= scale;

				BlockPos pos = BlockPos.containing(target.getX() + dx, target.getY() + dy, target.getZ() + dz);

				for (int j = 0; j < 8 && this.entity.level().isEmptyBlock(pos); j++) {
					pos = pos.below();
				}

				if (!this.entity.level().isEmptyBlock(pos) && this.entity.level().isEmptyBlock(pos.above())) {
					pos = pos.above();

					if (stalker == null) {
						stalker = new Stalker(EntityRegistry.STALKER.get(), this.entity.level());
					}

					boolean isBlockedFromSight = true;
					boolean isNotInView = true;

					AABB visibilityBox = new AABB(pos).inflate(2);
					checks:
					for (int xo = 0; xo <= 1; xo++) {
						for (int yo = 0; yo <= 1; yo++) {
							for (int zo = 0; zo <= 1; zo++) {
								double cx = xo == 0 ? visibilityBox.minX : visibilityBox.maxX;
								double cy = yo == 0 ? visibilityBox.minY : visibilityBox.maxY;
								double cz = zo == 0 ? visibilityBox.minZ : visibilityBox.maxZ;

								Vec3 diff = new Vec3(cx, cy, cz).subtract(target.getEyePosition());

								if (diff.normalize().dot(targetLook) > 0.1f) {
									isNotInView = false;
								}

								stalker.moveTo(cx, cy + 0.1f - stalker.getEyeHeight() /*offset for canEntityBeSeen*/, cz, 0, 0);

								if (target.hasLineOfSight(stalker)) {
									isBlockedFromSight = false;
								}

								if (!isNotInView && !isBlockedFromSight) {
									break checks;
								}
							}
						}
					}

					if (isBlockedFromSight || isNotInView) {
						stalker.moveTo(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f, this.entity.getRandom().nextFloat() * 360.0f, 0);

						if (EventHooks.checkSpawnPosition(stalker, ((ServerLevel) this.entity.level()), MobSpawnType.REINFORCEMENT)) {
							stalker.setCanCallAllies(false);
							stalker.setTarget(this.entity.getTarget());
							stalker.isStalking = false;

							this.entity.level().addFreshEntity(stalker);

							stalker = null;

							this.allies--;
							this.didSpawn = true;
						}
					}
				}
			}

			if (stalker != null) {
				stalker.discard();
			}
		}
	}
}
