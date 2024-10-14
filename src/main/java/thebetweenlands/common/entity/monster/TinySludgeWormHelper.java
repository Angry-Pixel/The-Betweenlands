package thebetweenlands.common.entity.monster;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import thebetweenlands.common.registries.EntityRegistry;

public class TinySludgeWormHelper extends TinySludgeWorm implements OwnableEntity {

	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(TinySludgeWormHelper.class, EntityDataSerializers.OPTIONAL_UUID);

	public TinySludgeWormHelper(EntityType<? extends Monster> type, Level level) {
		super(type, level);
		this.xpReward = 0;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
		this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D, 1));
		this.targetSelector.addGoal(0, new HurtByTargetGoal(this, Player.class));
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 2, true, true, entity -> entity instanceof Enemy));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(DATA_OWNERUUID_ID, Optional.empty());
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Animal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 30.0D)
			.add(Attributes.FOLLOW_RANGE, 20.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.25D)
			.add(Attributes.ATTACK_DAMAGE, 3.0D);
	}

	@Override
	public boolean canAttackType(EntityType<?> typeIn) {
		return typeIn != EntityRegistry.TINY_SLUDGE_WORM_HELPER.get();
	}

	@Override
	protected boolean shouldDespawnInPeaceful() {
		return false;
	}

	@Override
	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(DATA_OWNERUUID_ID).orElse(null);
	}

	public void setOwnerUUID(@Nullable UUID uuid) {
		this.getEntityData().set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (this.getOwnerUUID() != null)
			compound.putUUID("Owner", this.getOwnerUUID());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		UUID uuid;
		if (compound.hasUUID("Owner")) {
			uuid = compound.getUUID("Owner");
		} else {
			String s = compound.getString("Owner");
			uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
		}

		if (uuid != null) {
			try {
				this.setOwnerUUID(uuid);
			} catch (Throwable throwable) {
			}
		}
	}

	@Override
	public boolean canAttack(LivingEntity target) {
		return !this.isOwnedBy(target) && super.canAttack(target);
	}

	public boolean isOwnedBy(LivingEntity entity) {
		return entity == this.getOwner();
	}

	@Override
	public PlayerTeam getTeam() {
		if (this.getOwnerUUID() != null) {
			LivingEntity livingentity = this.getOwner();
			if (livingentity != null) {
				return livingentity.getTeam();
			}
		}

		return super.getTeam();
	}

	@Override
	public boolean isAlliedTo(Entity entity) {
		if (this.getOwnerUUID() != null) {
			LivingEntity livingentity = this.getOwner();
			if (entity == livingentity) {
				return true;
			}

			if (livingentity != null) {
				return livingentity.isAlliedTo(entity);
			}
		}

		return super.isAlliedTo(entity);
	}
}
