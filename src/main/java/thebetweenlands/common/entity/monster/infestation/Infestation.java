package thebetweenlands.common.entity.monster.infestation;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.component.entity.SwarmedData;
import thebetweenlands.common.entity.ClimbingMob;
import thebetweenlands.common.entity.ai.goals.InfestationMergeGoal;
import thebetweenlands.common.entity.ai.goals.NearestNonImmuneAttackableTargetGoal;
import thebetweenlands.common.registries.*;

import java.util.List;

public class Infestation extends ClimbingMob implements Enemy {

	public static final EntityDataAccessor<Float> SWARM_SIZE = SynchedEntityData.defineId(Infestation.class, EntityDataSerializers.FLOAT);

	private final ClientInfestation clientInfestation = new ClientInfestation();

	public Infestation(EntityType<? extends ClimbingMob> type, Level level) {
		this(type, level, 1);
	}

	public Infestation(EntityType<? extends ClimbingMob> type, Level level, float swarmSize) {
		super(type, level);
		this.setSwarmSize(swarmSize);
		this.xpReward = 5;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SWARM_SIZE, 1.0F);
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new InfestationMergeGoal(this, 50, 1.0D));
		this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
		this.targetSelector.addGoal(0, new NearestNonImmuneAttackableTargetGoal<>(this, Player.class, 1, false, false));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.2D)
			.add(Attributes.ATTACK_DAMAGE, 2.0D)
			.add(Attributes.FOLLOW_RANGE, 24.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	public float getSwarmSize() {
		return this.getEntityData().get(SWARM_SIZE);
	}

	public void setSwarmSize(float swarmSize) {
		this.getEntityData().set(SWARM_SIZE, swarmSize);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.setSwarmSize(tag.getFloat("swarm_size"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putFloat("swarm_size", this.getSwarmSize());
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		if (entity.hurt(this.damageSources().source(DamageTypeRegistry.SWARM, this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE))) {
			SwarmedData data = entity.getData(AttachmentRegistry.SWARMED);

			data.setSwarmedStrength(data.getSwarmedStrength() + 0.33f);
			data.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
			data.setSwarmSource(this);
			entity.syncData(AttachmentRegistry.SWARMED);

			return true;
		}
		return false;
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void push(Entity entity) {
		if (entity instanceof ServerPlayer sp)
			AdvancementCriteriaRegistry.INFESTED.get().trigger(sp);
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			if (this.isOnFire() || this.isInWater()) {
				if (this.getSwarmSize() > 0.1F) {
					this.setSwarmSize(Math.max(0.1F, this.getSwarmSize() - 0.005F));
				}

				if (this.wasOnFire && this.getRandom().nextInt(10) == 0) {
					List<Infestation> swarms = this.level().getEntitiesOfClass(Infestation.class, this.getBoundingBox().inflate(1), s -> !s.isOnFire());

					for (Infestation swarm : swarms) {
						swarm.igniteForSeconds(2);
					}
				}
			}

			float range = 3.25f;

			List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(range), EntitySelector.NO_CREATIVE_OR_SPECTATOR);

			for (Player player : players) {
				double dst = player.distanceTo(this);

				if (dst < range && this.hasLineOfSight(player)) {
					SwarmedData data = player.getData(AttachmentRegistry.SWARMED);

					data.setSwarmedStrength(data.getSwarmedStrength() + (1.0f - (float) dst / range) * 0.03f * Mth.clamp(this.getSwarmSize() * 1.75f, 0, 1));
					data.setDamage((float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
					data.setSwarmSource(this);
					player.syncData(AttachmentRegistry.SWARMED);
				}
			}
		} else {
			this.clientInfestation.tick(this);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.CRUNCH.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.SQUISH.get();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypeTags.IS_FIRE)) {
			amount *= 2;
		}

		boolean attacked = super.hurt(source, amount);

		if (this.isAlive() && attacked && amount > 2 && (this.getRandom().nextFloat() * 16 < amount || this.getHealth() < this.getMaxHealth() * 0.25F)) {
			this.split();
		}

		return attacked;
	}

	protected boolean split() {
		float swarmSize = this.getSwarmSize();

		if (swarmSize > 0.3f) {
			float initialSwarmSize = swarmSize;

			float fraction = initialSwarmSize * 0.25F + initialSwarmSize * (this.getRandom().nextFloat() - 0.5f) * 0.05f;
			this.setSwarmSize(fraction);
			swarmSize -= fraction;

			for (int i = 0; i < 3; i++) {
				fraction = i == 2 ? swarmSize : (initialSwarmSize * 0.25f + initialSwarmSize * (this.getRandom().nextFloat() - 0.5f) * 0.05f);
				Infestation swarm = new Infestation(EntityRegistry.INFESTATION.get(), this.level(), fraction);
				swarmSize -= fraction;

				swarm.setHealth(this.getHealth() * 0.66f);
				swarm.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());

				if (this.isOnFire()) {
					swarm.igniteForTicks(40);
				}

				float mx = this.getRandom().nextFloat() - 0.5f;
				float mz = this.getRandom().nextFloat() - 0.5f;

				float len = Mth.sqrt(mx * mx + mz * mz);

				mx /= len;
				mz /= len;

				swarm.setDeltaMovement(mx * 0.5F, 0.3F, mz * 0.5F);

				this.level().addFreshEntity(swarm);
			}

			this.setHealth(this.getHealth() * 0.5f);

			return true;
		}

		return false;
	}

	public void mergeInto(Infestation swarm) {
		swarm.setSwarmSize(swarm.getSwarmSize() + this.getSwarmSize());

		if (this.getHealth() < swarm.getHealth()) {
			swarm.setHealth((this.getHealth() / 0.66f + swarm.getHealth()) * 0.5f);
		}

		if (this.isOnFire()) {
			swarm.igniteForSeconds(2);
		}

		this.discard();
	}

	@Override
	public boolean displayFireAnimation() {
		return false;
	}
}
