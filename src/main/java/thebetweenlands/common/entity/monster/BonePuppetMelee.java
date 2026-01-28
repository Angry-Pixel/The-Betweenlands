package thebetweenlands.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BonePuppetMelee extends BonePuppetBase {

	private static final EntityDataAccessor<Boolean> MELEE_ATTACK = SynchedEntityData.defineId(BonePuppetMelee.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> ATTACK_TIMER = SynchedEntityData.defineId(BonePuppetMelee.class, EntityDataSerializers.INT);
	public int prevAttackTimer;
	public BonePuppetMelee(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MELEE_ATTACK, false);
        builder.define(ATTACK_TIMER, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PuppetMeleeAttackGoal(this, 1D, true));
        goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    public static AttributeSupplier.Builder registerAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.33D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ATTACK_KNOCKBACK, 2.0D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }
    
	@Override
	public void aiStep() {
		super.aiStep();
		if (level().isClientSide()) {
			prevAttackTimer = getAttackTimer();
			if (getAttackTimer() == 0)
				prevAttackTimer = 0;
		}

		if (!level().isClientSide()) {
			if (isAttacking()) {
				setAttackTimer(getAttackTimer() + 1);
				if (getAttackTimer() > 20) {
					setAttackTimer(0);
					setAttacking(false);
				}
			} else
				setAttackTimer(0);
		}
	}

    public void setAttacking(boolean attacking) {
        entityData.set(MELEE_ATTACK, attacking);
    }

    public boolean isAttacking() {
        return entityData.get(MELEE_ATTACK);
    }
    
    public void setAttackTimer(int progress) {
        entityData.set(ATTACK_TIMER, progress);
    }

    public int getAttackTimer() {
        return entityData.get(ATTACK_TIMER);
    }

	public static class PuppetMeleeAttackGoal extends MeleeAttackGoal {

		private final BonePuppetMelee puppet;

		public PuppetMeleeAttackGoal(BonePuppetMelee puppet, double speedModifier, boolean mustSee) {
			super(puppet, speedModifier, mustSee);
			this.puppet = puppet;
		}

		@Override
		protected void checkAndPerformAttack(LivingEntity target) {
			if (canPerformAttack(target)) {
				puppet.setAttacking(true);
				if (puppet.getAttackTimer() >= 10) {
					puppet.doHurtTarget(target);
					resetAttackCooldown();
				}
			}
		}
	}

}
