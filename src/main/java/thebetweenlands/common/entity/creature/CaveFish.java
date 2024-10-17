package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.entity.ai.goals.FollowTargetGoal;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class CaveFish extends WaterAnimal implements BLEntity {

	protected static final EntityDataAccessor<Boolean> IS_LEADER = SynchedEntityData.defineId(CaveFish.class, EntityDataSerializers.BOOLEAN);
	private RandomSwimmingGoal wanderAbout;
	private FollowTargetGoal<CaveFish> followLeader;
	private AvoidEntityGoal<CaveFish> aiAvoidFollowers;
	private NearestAttackableTargetGoal<CaveFish> targetRivalLeader;
	private MeleeAttackGoal attackLeader;
	private boolean spawnedChildren = false;

	public CaveFish(EntityType<? extends WaterAnimal> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_LEADER, false);
	}

	@Override
	protected void registerGoals() {
		this.aiAvoidFollowers = new AvoidEntityGoal<>(this, CaveFish.class, 10.0F, 0.75D, 1.0D);
		this.wanderAbout = new RandomSwimmingGoal(this, 0.5D, 5);
		this.followLeader = new FollowTargetGoal<>(this, new FollowTargetGoal.FollowClosest<>(this, CaveFish.class, CaveFish::isLeader, 16), 14D, 1F, 16.0F, false);
		this.targetRivalLeader = new NearestAttackableTargetGoal<>(this, CaveFish.class, 0, false, true, entity -> ((CaveFish)entity).isLeader());
		this.attackLeader = new MeleeAttackGoal(this, 0.65D, false);

		this.goalSelector.addGoal(2, this.aiAvoidFollowers);
		this.goalSelector.addGoal(3, new MoveTowardsRestrictionGoal(this, 0.4D));
		this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
		this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 3.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.4D)
			.add(Attributes.ATTACK_DAMAGE, 1.0D);
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		return new WaterBoundPathNavigation(this, level);
	}

	private void checkSpawnChildren() {
		if (!this.level().isClientSide() && !this.spawnedChildren) {
			this.setIsLeader(true);

			for (int x = 0; x < 3 + this.getRandom().nextInt(4); x++) {
				CaveFish fish = new CaveFish(EntityRegistry.CAVE_FISH.get(), this.level());
				fish.moveTo(this.getX(), this.getY(), this.getZ(), this.level().getRandom().nextFloat() * 360, 0);
				fish.setIsLeader(false);
				fish.spawnedChildren = true;
				this.level().addFreshEntity(fish);
			}

			this.spawnedChildren = true;
		}
	}

	public void setIsLeader(boolean isLeader) {
		this.getEntityData().set(IS_LEADER, isLeader);
		if (!this.level().isClientSide()) {
			if (isLeader) {
				this.targetSelector.addGoal(0, this.targetRivalLeader);
				this.goalSelector.removeGoal(this.followLeader);
				this.goalSelector.addGoal(0, this.wanderAbout);
				this.goalSelector.addGoal(1, this.attackLeader);
			} else {
				this.targetSelector.removeGoal(this.targetRivalLeader);
				this.goalSelector.removeGoal(this.wanderAbout);
				this.goalSelector.addGoal(0, this.followLeader);
				this.goalSelector.removeGoal(this.attackLeader);
			}
		}
	}

	public boolean isLeader() {
		return this.getEntityData().get(IS_LEADER);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (IS_LEADER.equals(key)) {
			this.refreshDimensions();
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	protected EntityDimensions getDefaultDimensions(Pose pose) {
		return this.isLeader() ? EntityDimensions.scalable(0.6F, 0.4F) : EntityDimensions.scalable(0.3F, 0.2F);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("leader", this.isLeader());
		tag.putBoolean("spawned_children", this.spawnedChildren);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setIsLeader(tag.getBoolean("isLeader"));
		this.spawnedChildren = tag.getBoolean("spawnedChildren");
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.FISH_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.FISH_DEATH.get();
	}

	@Override
	protected SoundEvent getSwimSound() {
		return SoundEvents.FISH_SWIM;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public float getWalkTargetValue(BlockPos pos) {
		return this.level().getBlockState(pos).liquid() ? 10.0F + this.level().getMaxLocalRawBrightness(pos) - 0.5F : super.getWalkTargetValue(pos);
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isLeader()) {
			if (this.level().isClientSide() && this.level().getGameTime() % 5 + this.level().getRandom().nextInt(5) == 0) {
				if (this.isInWater()) {
					for (int i = 0; i < 2; ++i) {
						double a = Math.toRadians(this.getYRot());
						double offSetX = -Math.sin(a) * this.getBbWidth() * 0.5D;
						double offSetZ = Math.cos(a) * this.getBbWidth() * 0.5D;
						this.level().addParticle(ParticleTypes.BUBBLE, this.getX() + offSetX, this.getY() + this.getBbHeight() * 0.5D + this.getRandom().nextDouble() * 0.5D, this.getZ() + offSetZ, 0.0D, 0.4D, 0.0D);
					}
				}
			}
		} else {
			if (!this.level().isClientSide()) {
				if (this.level().getGameTime() % 200 == 0)
					this.checkIfCanBeLeader(); // just in case there is no leader
			}
		}

		if (!this.spawnedChildren && this.tickCount % 20 == 0 && this.isLeader() && this.level().getNearestPlayer(this, 16) != null) {
			this.checkSpawnChildren();
		}
	}

	private void checkIfCanBeLeader() {
		AABB aoe = new AABB(this.blockPosition()).inflate(10.0D);
		if (!this.level().isClientSide()) {
			List<CaveFish> list = this.level().getEntitiesOfClass(CaveFish.class, aoe, CaveFish::isLeader);
			if (list.isEmpty())
				this.promoteThisFish();
		}

	}

	private void promoteThisFish() {
		this.setIsLeader(true);
	}

	@Override
	public boolean isPickable() {
		return this.isLeader();
	}
}
