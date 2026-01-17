package thebetweenlands.common.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.ItemRegistry;

public class BonePuppetRanged extends Monster implements BLEntity {

    private static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(BonePuppetRanged.class, EntityDataSerializers.INT);

    private int lastSpawningAnimationTicks = 0;

    public BonePuppetRanged(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SPAWN_TIMER, 0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, player -> !player.isShiftKeyDown()));
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
        lastSpawningAnimationTicks = getSpawnTimer();
            if (getSpawnTimer() < 20)
                setSpawnTimer(getSpawnTimer() +1);
        super.aiStep();
    }

    
    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || getSpawnTimer() < 20;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean isCreative = source.getEntity() instanceof Player player && player.isCreative();
        return (isEmerging() && !isCreative) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return !isEmerging() && super.isPushable();
    }

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isEmerging() && source.is(DamageTypes.IN_WALL))
			return false;
		return super.hurt(source, amount);
	}

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("spawn_timer", getSpawnTimer());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSpawnTimer(compound.getInt("spawn_timer"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
		return null;
        //return SoundRegistry.WIGHT_MOAN.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
        //return SoundRegistry.WIGHT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
		return null;
       // return SoundRegistry.WIGHT_DEATH.get();
    }

    public boolean isEmerging() {
        return getEntityData().get(SPAWN_TIMER) < 20;
    }

    public int getSpawnTimer() {
        return getEntityData().get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int timer) {
        getEntityData().set(SPAWN_TIMER, timer);
    }

    public float getSpawningAnimation(float partialTicks) {
        return Mth.lerp(partialTicks, lastSpawningAnimationTicks, getSpawnTimer()) / 20.0F;
    }

    // May need this for when wights do the thing and stuffs
    public boolean isWearingSkullMask(LivingEntity entity) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        return !helmet.isEmpty() && helmet.is(ItemRegistry.SKULL_MASK);
    }

}
