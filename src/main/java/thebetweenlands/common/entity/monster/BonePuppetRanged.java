package thebetweenlands.common.entity.monster;

import java.util.EnumSet;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.projectile.ThrownBone;

public class BonePuppetRanged extends BonePuppetBase {

	public static final EntityDataAccessor<Boolean> RELOADING = SynchedEntityData.defineId(BonePuppetRanged.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> RELOAD_TIMER= SynchedEntityData.defineId(BonePuppetRanged.class, EntityDataSerializers.INT);
	public int prevReloadTimer;

    public BonePuppetRanged(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RELOADING, false);
        builder.define(RELOAD_TIMER, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new ThrowBoneGoal(this, 1D, 8F));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }

	@Override
	public void aiStep() {
		super.aiStep();

		if (level().isClientSide()) {
			prevReloadTimer = getReloadTimer();
			if (getReloadTimer() == 0)
				prevReloadTimer = 0;
		}

		if (!level().isClientSide()) {
			if (isAlive()) {
				if (isReloading()) {
					setReloadTimer(getReloadTimer() + 1);
					if (getReloadTimer() > 20) {
						setReloadTimer(0);
						setReloading(false);
					}
				} else
					setReloadTimer(0);
			}
			else {
				setReloadTimer(0);
				setReloading(false);
			}
		}
	}

    public void setReloading(boolean attacking) {
    	getEntityData().set(RELOADING, attacking);
    }

    public boolean isReloading() {
        return getEntityData().get(RELOADING);
    }

    public void setReloadTimer(int progress) {
    	getEntityData().set(RELOAD_TIMER, progress);
    }

    public int getReloadTimer() {
        return getEntityData().get(RELOAD_TIMER);
    }

    public static class ThrowBoneGoal extends Goal {
        public static final UniformInt PATHFINDING_DELAY_RANGE = TimeUtil.rangeOfSeconds(1, 2);
        private final BonePuppetRanged puppet;
        private final double speedModifier;
        private final float attackRadiusSqr;
        private int updatePathDelay;

        public ThrowBoneGoal(BonePuppetRanged puppet, double speedModifier, float range) {
            this.puppet = puppet;
            this.speedModifier = speedModifier;
            attackRadiusSqr = range * range;
            setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return isValidTarget();
        }

        @Override
        public boolean canContinueToUse() {
            return isValidTarget() || !puppet.getNavigation().isDone();
        }

        private boolean isValidTarget() {
            return puppet.getTarget() != null && puppet.getTarget().isAlive();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

		@Override
		public void tick() {
			LivingEntity livingentity = puppet.getTarget();
			if (livingentity != null) {
				boolean canSee = puppet.getSensing().hasLineOfSight(livingentity);
				if (canSee) {
					double distanceToTarget = puppet.distanceToSqr(livingentity);
					boolean outOfRange = distanceToTarget > (double) attackRadiusSqr;
					if (outOfRange) {
						updatePathDelay--;
						if (updatePathDelay <= 0) {
							puppet.getNavigation().moveTo(livingentity, speedModifier);
							updatePathDelay = PATHFINDING_DELAY_RANGE.sample(puppet.getRandom());
						}
					} else {
						updatePathDelay = 0;
						puppet.getNavigation().stop();
					}

					puppet.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
					throwBone(livingentity);
				}
			}
		}

		private void throwBone(LivingEntity target) {
			if (canPerformAttack(target)) {
				if(!puppet.level().isClientSide()) {
					puppet.setAttacking(true);
					if (puppet.getAttackTimer() == 17) {
						Level level = puppet.level();
						ThrownBone bone = new ThrownBone(level, puppet, (float) puppet.getAttributeValue(Attributes.ATTACK_DAMAGE));
						double targetX = target.getX() + target.getDeltaMovement().x() - puppet.getX();
						double targetY = target.getY() - puppet.getY() + 0.5D;
						double targetZ = target.getZ() + target.getDeltaMovement().z() - puppet.getZ();
						double direction = Math.toRadians(puppet.getYRot());
						bone.absMoveTo(puppet.getX() + -Math.sin(direction) * 0.5D, puppet.getY() + 1.5D, puppet.getZ() + Math.cos(direction) * 0.5D, puppet.getYRot(), 0F);
						level.addFreshEntity(bone);
						bone.shoot(targetX, targetY, targetZ, 0.5F, 0.0F);
						puppet.setReloading(true);
					}
				}
			}
		}

	    protected boolean canPerformAttack(LivingEntity entity) {
	        return !puppet.isReloading() && puppet.getSensing().hasLineOfSight(entity);
	    }
    }
}
