package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.registries.EntityRegistry;

public class Seat extends Entity {

	private static final EntityDataAccessor<Float> OFFSET = SynchedEntityData.defineId(Seat.class, EntityDataSerializers.FLOAT);
	public boolean tempSeat = false;

	public Seat(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	public Seat(Level level, boolean tempSeat) {
		super(EntityRegistry.SEAT.get(), level);
		this.tempSeat = tempSeat;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(OFFSET, 0.0F);
	}

	public float getSeatOffset() {
		return this.getEntityData().get(OFFSET);
	}

	public void setSeatOffset(float amount) {
		this.getEntityData().set(OFFSET, amount);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (OFFSET.equals(key)) {
			this.setSeatOffset(this.getSeatOffset());
		}
		super.onSyncedDataUpdated(key);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			if (!this.isVehicle() || this.level().isEmptyBlock(this.blockPosition()) || (this.tempSeat && this.tickCount >= 60)) {
				this.discard();
			}
		}
	}

	@Override
	protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
		return super.getPassengerAttachmentPoint(entity, dimensions, partialTick).add(0.0D, this.getSeatOffset(), 0.0D);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.setSeatOffset(tag.getFloat("seat_offset"));
		this.tempSeat = tag.getBoolean("temp_seat");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putFloat("seat_offset", this.getSeatOffset());
		tag.putBoolean("temp_seat", this.tempSeat);
	}
}
