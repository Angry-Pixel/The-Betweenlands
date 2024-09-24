package thebetweenlands.common.entity.creature;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MireSnail extends Animal implements BLEntity {

	private static final EntityDataAccessor<Boolean> MATED = SynchedEntityData.defineId(MireSnail.class, EntityDataSerializers.BOOLEAN);

	public MireSnail(EntityType<? extends Animal> type, Level level) {
		super(type, level);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new PanicGoal(this, 1.2D));
		this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, stack -> stack.is(ItemRegistry.SAP_SPIT) || stack.is(ItemRegistry.SLUDGE_BALL), false));
		this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.85D));
		this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 5.0D).add(Attributes.MOVEMENT_SPEED, 0.18F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(MATED, false);
	}

	@Override
	public int getMaxSpawnClusterSize() {
		return 3;
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return stack.is(ItemRegistry.SAP_SPIT) || stack.is(ItemRegistry.SLUDGE_BALL);
	}

	@Override
	public boolean canFallInLove() {
		return !this.getEntityData().get(MATED) && super.canFallInLove();
	}

	@Override
	public void spawnChildFromBreeding(ServerLevel level, Animal mate) {
		this.getEntityData().set(MATED, true);
		super.spawnChildFromBreeding(level, mate);
	}

	@Nullable
	@Override
	public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}

	@Nullable
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundRegistry.SNAIL_LIVING.get();
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.SNAIL_HURT.get();
	}

	@Nullable
	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SNAIL_DEATH.get();
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putBoolean("mated", this.getEntityData().get(MATED));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.getEntityData().set(MATED, compound.getBoolean("mated"));
	}
}
