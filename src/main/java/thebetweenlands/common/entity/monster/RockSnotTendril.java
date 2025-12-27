package thebetweenlands.common.entity.monster;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.creature.Lurker;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class RockSnotTendril extends Entity {


	private static final EntityDataAccessor<Boolean> IS_EXTENDING = SynchedEntityData.defineId(RockSnotTendril.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(RockSnotTendril.class, EntityDataSerializers.INT);

	public RockSnotTendril(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		noCulling = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(IS_EXTENDING, false);
		builder.define(PARENT_ID, -1);
	}

	public boolean getExtending() {
		return getEntityData().get(IS_EXTENDING);
	}

	public void setExtending(boolean extending) {
		getEntityData().set(IS_EXTENDING, extending);
	}
	
	public Integer getParentEntityID() {
		return getEntityData().get(PARENT_ID);
	}
	
	public void setParentEntityID(Integer parentID) {
		getEntityData().set(PARENT_ID, parentID);
	}

	@Nullable
	public RockSnot getParentEntity() {
		RockSnot parentEntity = (RockSnot) level().getEntity(getEntityData().get(PARENT_ID));
		return getEntityData().get(PARENT_ID) != -1 ? parentEntity : null;
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (IS_EXTENDING.equals(key))
			setExtending(getExtending());
		if (PARENT_ID.equals(key))
			setParentEntityID(getParentEntityID());
		super.onSyncedDataUpdated(key);
	}

	@Override
	public void tick() {
		if (!this.level().isClientSide())
			if (getParentEntity() == null || !getParentEntity().isAlive())
				kill();

		checkCollision();

		if (getParentEntity() != null && !getExtending()) {
				returnToParent();
			if (getBoundingBox().intersects(getParentEntity().getBoundingBox())) {
				if (getX() != getParentEntity().getX() || getZ() != getParentEntity().getZ())
					setPos(getParentEntity().getX(), getParentEntity().getY() + getParentEntity().getBbHeight() * 0.5D, getParentEntity().getZ());

				if (!level().isClientSide()) {
					if (isVehicle() && !getParentEntity().isVehicle()) {
						Entity entity = getPassengers().get(0);
						entity.startRiding(getParentEntity(), true);
					}
					kill();
					getParentEntity().setTendrilCount(getParentEntity().getTendrilCount() - 1);
				}
			}
		}

		if (getParentEntity() != null && (!isVehicle() && tickCount > 20) || getParentEntity() != null && horizontalCollision) {
			if (!level().isClientSide()) {
				if (getExtending())
					setExtending(false);
			}
		}

		if(level().isClientSide() && level().getGameTime()%5 == 0)
			spawnDrips();
		
		move(MoverType.SELF, getDeltaMovement());
		super.tick();
	}

	public void moveToTarget(double targetX, double targetY, double targetZ, float velocity) {
		float distSq = Mth.sqrt((float) (targetX * targetX + targetY * targetY + targetZ * targetZ));
		targetX = targetX / (double) distSq * (double) velocity;
		targetY = targetY / (double) distSq * (double) velocity;
		targetZ = targetZ / (double) distSq * (double) velocity;

		setDeltaMovement(getDeltaMovement().add(targetX, targetY, targetZ));
		float angle = Mth.sqrt((float) (targetX * targetX + targetZ * targetZ));
		setYRot((float) (Mth.atan2(targetX, targetZ) * (180D / Math.PI)));
		setXRot((float) (Mth.atan2(targetY, (double) angle) * (180D / Math.PI)));
	}

	public void checkCollision() {
		if (getParentEntity() != null) {
			List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, getBoundingBox());
			for (int i = 0; i < list.size(); i++) {
				Entity entity = list.get(i);
				if (entity != null) {
					if (entity instanceof LivingEntity && !(entity instanceof RockSnot) && !(entity instanceof RockSnotTendril) && !(entity instanceof Lurker) && !entity.getType().is(Tags.EntityTypes.BOSSES)) {
						if (entity instanceof Player && getParentEntity().getPlacedByPlayer() || getParentEntity().isVehicle())
							return;
						if (!isVehicle()) {
							if (!level().isClientSide()) {
								entity.startRiding(this, true);
								level().playSound(null, blockPosition(), SoundRegistry.ROCK_SNOT_ATTACH.get(), SoundSource.HOSTILE, 1F, 1F);
								if (getExtending())
									setExtending(false);
							}
							returnToParent();
						}
					}
				}
			}
		}
	}

	public void returnToParent() {
		double targetX = getParentEntity().getX() - getX();
		double targetY = getParentEntity().getY() + getParentEntity().getBbHeight() * 0.5D - getY() + getBbHeight() * 0.5D;
		double targetZ = getParentEntity().getZ() - getZ();
		moveToTarget(targetX, targetY, targetZ, 0.25F);
	}

	@Override
	public void positionRider(Entity entity, Entity.MoveFunction moveFunction) {
		if (entity instanceof LivingEntity)
			entity.setPos(getX(), getY() + getBbHeight(), getZ());
	}

	@Override
	public boolean shouldRiderSit() {
		return false;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		if (getParentEntity() != null)
			tag.putInt("parent", getParentEntity().getId());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		if(tag.contains("parent", Tag.TAG_INT))
			setParentEntityID(tag.getInt("parent"));
	}

	@Override
	public boolean canCollideWith(Entity entity) {
		return !(entity instanceof RockSnot) && super.canCollideWith(entity);
	}

	@Override	
    public boolean canBeCollidedWith() {
        return true;
    }

	public void spawnDrips() {
		double x = getX()+ (double) random.nextFloat() * 0.25F;
		double y = getY();
		double z = getZ()+ (double) random.nextFloat() * 0.25F;
		TheBetweenlands.createParticle(ParticleRegistry.RAIN.get(), level(), x,y, z, ParticleFactory.ParticleArgs.get().withColor(FastColor.ARGB32.colorFromFloat(1F, 0.4118F, 0.2745F, 0.1568F)));
	}

}
