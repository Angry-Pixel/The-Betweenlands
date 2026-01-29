package thebetweenlands.common.entity.monster;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

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
        goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
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
			if (isReloading()) {
				setReloadTimer(getReloadTimer() + 1);
				if (getReloadTimer() > 20) {
					setReloadTimer(0);
					setReloading(false);
				}
			} else
				setReloadTimer(0);
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
}
