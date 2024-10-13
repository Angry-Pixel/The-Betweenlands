package thebetweenlands.common.entity.monster;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import thebetweenlands.common.registries.EntityRegistry;

public class SludgeWormTinyHelper extends SludgeWormTiny implements OwnableEntity {
	protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(SludgeWormTinyHelper.class, EntityDataSerializers.OPTIONAL_UUID);

	public SludgeWormTinyHelper(EntityType<? extends PathfinderMob> type, Level level) {
		super(type, level);
		xpReward = 0;
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
		goalSelector.addGoal(1, new RandomStrollGoal(this, 0.8D, 1));
		targetSelector.addGoal(0, new HurtByTargetGoal(this, Player.class) {
			@Override
			public boolean canUse() {
				return getLastAttacker() == SludgeWormTinyHelper.this.getOwner() ? false : super.canUse();
			}
		});
		targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 2, true, true, entity -> entity instanceof Monster == true));
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
		return typeIn != EntityRegistry.SLUDGE_WORM_TINY_HELPER.get();
	}

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
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
}
