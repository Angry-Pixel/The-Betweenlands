package thebetweenlands.common.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.DamageTypeRegistry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ShockwaveBlock extends Entity implements OwnableEntity, IEntityWithComplexSpawn {

	private static final EntityDataAccessor<Optional<UUID>> OWNER = SynchedEntityData.defineId(ShockwaveBlock.class, EntityDataSerializers.OPTIONAL_UUID);
	private static final EntityDataAccessor<BlockState> STATE = SynchedEntityData.defineId(ShockwaveBlock.class, EntityDataSerializers.BLOCK_STATE);

	public int jumpDelay;
	@Nullable
	public BlockPos origin;
	private double waveStartX, waveStartZ;

	public ShockwaveBlock(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.setBlock(Blocks.STONE.defaultBlockState());
		this.noPhysics = true;
	}

	public BlockState getBlock() {
		return this.getEntityData().get(STATE);
	}

	public void setBlock(BlockState state) {
		this.getEntityData().set(STATE, state);
	}

	public void setOwner(@Nullable UUID ownerUUID) {
		this.getEntityData().set(OWNER, Optional.ofNullable(ownerUUID));
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER).orElse(null);
	}

	@Override
	public void tick() {
		this.setOldPosAndRot();
		this.setDeltaMovement(new Vec3(0.0D, this.getDeltaMovement().y(), 0.0D));

		if (this.origin == null) {
			this.discard();
			return;
		}

		if (this.tickCount >= this.jumpDelay) {
			if (this.tickCount == this.jumpDelay && this.getDeltaMovement().y() <= 0.0D) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
			} else {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.05D, 0.0D));

				if (!this.level().isClientSide() && (this.getY() <= this.origin.getY() || this.onGround() || this.tickCount >= this.jumpDelay + 20)) {
					this.discard();
				}
			}
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}

		if (this.getY() < this.level().getMinBuildHeight() - 20) {
			this.discard();
		}

		if (this.getY() + this.getDeltaMovement().y() <= this.origin.getY()) {
			this.setDeltaMovement(Vec3.ZERO);
			this.moveTo(this.getX(), this.origin.getY(), this.getZ(), 0, 0);
		} else {
			this.move(MoverType.SELF, new Vec3(0.0D, this.getDeltaMovement().y(), 0.0D));
		}

		if (this.getDeltaMovement().y() > 0.1D && !this.level().isClientSide()) {
			DamageSource damageSource;
			Entity owner = getOwner();
			if (owner instanceof LivingEntity) {
				if (owner instanceof Player player) {
					damageSource = this.damageSources().playerAttack(player);
				} else {
					damageSource = this.damageSources().source(DamageTypes.MOB_ATTACK, owner, this);
				}
			} else {
				damageSource = this.damageSources().source(DamageTypeRegistry.SHOCKWAVE);
			}
			List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.1D));
			for (LivingEntity entity : entities) {
				if (entity != null) {
					if (entity instanceof LivingEntity && entity != this.getOwner()) { // needs null check on owner?
						if (entity.hurt(damageSource, entity instanceof Player ? 5.0F : 10.0F)) {
							float knockback = 1.5F;
							Vec3 dir = new Vec3(this.getX() - this.waveStartX, 0, this.getZ() - this.waveStartZ);
							dir = dir.normalize();
							entity.setDeltaMovement(dir.x * knockback, 0.5D, dir.z * knockback);
							if (entity.getHealth() <= 0 && owner instanceof ServerPlayer sp) {
								AdvancementCriteriaRegistry.SHOCKWAVE_KILL.get().trigger(sp, entity);
							}
						}
					}
				}
			}
		}

		this.firstTick = false;
	}

	@Override
	public void updateInWaterStateAndDoWaterCurrentPushing() {
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public void push(Entity entity) {

	}

	@Override
	public void push(double x, double y, double z) {

	}

	@Override
	public boolean canBeCollidedWith() {
		return !this.isRemoved();
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
		buffer.writeBlockPos(this.origin);
		buffer.writeInt(this.jumpDelay);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf buffer) {
		this.origin = buffer.readBlockPos();
		this.jumpDelay = buffer.readInt();
	}

	public void setOrigin(BlockPos pos, int delay, double waveStartX, double waveStartZ, Entity source) {
		this.origin = pos;
		this.jumpDelay = delay;
		this.waveStartX = waveStartX;
		this.waveStartZ = waveStartZ;
		this.setOwner(source.getUUID());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(OWNER, Optional.empty());
		builder.define(STATE, Blocks.STONE.defaultBlockState());
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		BlockState blockstate = Blocks.STONE.defaultBlockState();
		if (tag.contains("block", Tag.TAG_COMPOUND)) {
			blockstate = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tag.getCompound("block"));
			if (blockstate.isAir()) {
				blockstate = Blocks.STONE.defaultBlockState();
			}
		}
		this.setBlock(blockstate);

		this.origin = NbtUtils.readBlockPos(tag, "origin").orElse(null);
		this.waveStartX = tag.getDouble("start_x");
		this.waveStartZ = tag.getDouble("start_z");
		this.jumpDelay = tag.getInt("jump_delay");
		if (tag.hasUUID("owner")) {
			this.setOwner(tag.getUUID("owner"));
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.put("block", NbtUtils.writeBlockState(this.getBlock()));
		if (this.origin != null) {
			tag.put("origin", NbtUtils.writeBlockPos(this.origin));
		}
		tag.putDouble("start_x", this.waveStartX);
		tag.putDouble("start_z", this.waveStartZ);
		tag.putInt("jump_delay", this.jumpDelay);
		if (this.getOwnerUUID() != null) {
			tag.putUUID("owner", this.getOwnerUUID());
		}
	}
}
