package thebetweenlands.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.util.AnimationMathHelper;

public class SwordEnergy extends Entity implements IEntityWithComplexSpawn {

	private static final AABB RENDER_BOUNDING_BOX = new AABB(-9, -2, -9, 10, 3, 10);
	public static final float DISTANCE = 3.0F;

	private static final EntityDataAccessor<Float> PART_POS_1 = SynchedEntityData.defineId(SwordEnergy.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> PART_POS_2 = SynchedEntityData.defineId(SwordEnergy.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> PART_POS_3 = SynchedEntityData.defineId(SwordEnergy.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> PART_POS_4 = SynchedEntityData.defineId(SwordEnergy.class, EntityDataSerializers.FLOAT);

	public float pulseFloat;
	public float pos1, pos2, pos3, pos4, lastPos1, lastPos2, lastPos3, lastPos4;
	AnimationMathHelper pulse = new AnimationMathHelper();

	public SwordEnergy(EntityType<? extends Entity> type, Level level) {
		super(type, level);
	}

	@Override
	public AABB getBoundingBoxForCulling() {
		return RENDER_BOUNDING_BOX.move(this.position());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(PART_POS_1, DISTANCE);
		builder.define(PART_POS_2, DISTANCE);
		builder.define(PART_POS_3, DISTANCE);
		builder.define(PART_POS_4, DISTANCE);
	}

	@Override
	public void push(Entity entity) {

	}

	@Override
	public boolean isAttackable() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();
		this.pulseFloat = this.pulse.swing(0.3F, 0.75F, false);
		this.setDeltaMovement(Vec3.ZERO);
		if (!this.level().isClientSide()) {
			if (this.tickCount % 140 == 0)
				this.level().playSound(null, this.blockPosition(), SoundRegistry.ENERGY_SWORD_ORB.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

			if (this.getSwordPart1Pos() > 0 && this.getSwordPart1Pos() < DISTANCE)
				this.setSwordPart1Pos(this.getSwordPart1Pos() - 0.05F);

			if (this.getSwordPart2Pos() > 0 && this.getSwordPart2Pos() < DISTANCE)
				this.setSwordPart2Pos(this.getSwordPart2Pos() - 0.05F);

			if (this.getSwordPart3Pos() > 0 && this.getSwordPart3Pos() < DISTANCE)
				this.setSwordPart3Pos(this.getSwordPart3Pos() - 0.05F);

			if (this.getSwordPart4Pos() > 0 && this.getSwordPart4Pos() < DISTANCE)
				this.setSwordPart4Pos(this.getSwordPart4Pos() - 0.05F);

			if (this.getSwordPart1Pos() <= 0 && this.getSwordPart2Pos() <= 0 && this.getSwordPart3Pos() <= 0 && this.getSwordPart4Pos() <= 0) {
				this.level().playSound(null, this.blockPosition(), SoundRegistry.ENERGY_SWORD.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
				ItemEntity entityItem = new ShockwaveSwordItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), ItemRegistry.SHOCKWAVE_SWORD.toStack());
				entityItem.setDeltaMovement(Vec3.ZERO);
				this.level().addFreshEntity(entityItem);
				this.discard();
			}
		} else {
			this.lastPos1 = this.pos1;
			this.lastPos2 = this.pos2;
			this.lastPos3 = this.pos3;
			this.lastPos4 = this.pos4;
			this.pos1 = this.getSwordPart1Pos();
			this.pos2 = this.getSwordPart2Pos();
			this.pos3 = this.getSwordPart3Pos();
			this.pos4 = this.getSwordPart4Pos();
		}
	}

	public float getSwordPart1Pos() {
		return this.getEntityData().get(PART_POS_1);
	}

	public void setSwordPart1Pos(float pos) {
		this.getEntityData().set(PART_POS_1, pos);
	}

	public float getSwordPart2Pos() {
		return this.getEntityData().get(PART_POS_2);
	}

	public void setSwordPart2Pos(float pos) {
		this.getEntityData().set(PART_POS_2, pos);
	}

	public float getSwordPart3Pos() {
		return this.getEntityData().get(PART_POS_3);
	}

	public void setSwordPart3Pos(float pos) {
		this.getEntityData().set(PART_POS_3, pos);
	}

	public float getSwordPart4Pos() {
		return this.getEntityData().get(PART_POS_4);
	}

	public void setSwordPart4Pos(float pos) {
		this.getEntityData().set(PART_POS_4, pos);
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putFloat("pos_1", this.getSwordPart1Pos());
		tag.putFloat("pos_2", this.getSwordPart2Pos());
		tag.putFloat("pos_3", this.getSwordPart3Pos());
		tag.putFloat("pos_4", this.getSwordPart4Pos());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.setSwordPart1Pos(tag.getFloat("pos_1"));
		this.setSwordPart2Pos(tag.getFloat("pos_2"));
		this.setSwordPart3Pos(tag.getFloat("pos_3"));
		this.setSwordPart4Pos(tag.getFloat("pos_4"));
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		buffer.writeFloat(this.getEntityData().get(PART_POS_1));
		buffer.writeFloat(this.getEntityData().get(PART_POS_2));
		buffer.writeFloat(this.getEntityData().get(PART_POS_3));
		buffer.writeFloat(this.getEntityData().get(PART_POS_4));
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
		this.getEntityData().set(PART_POS_1, additionalData.readFloat());
		this.getEntityData().set(PART_POS_2, additionalData.readFloat());
		this.getEntityData().set(PART_POS_3, additionalData.readFloat());
		this.getEntityData().set(PART_POS_4, additionalData.readFloat());
	}
}
