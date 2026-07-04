package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.particle.options.SpikeParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.DamageTypeRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SpikeWave extends Entity implements IEntityWithComplexSpawn {

	public List<BlockPos> positions = new ArrayList<>();

	@Nullable
	private AABB blockEnclosingBounds;
	@Nullable
	private AABB renderingBounds;

	public BlockPos origin = BlockPos.ZERO;
	public int delay;

	protected float attackDamage = 10.0F;

	public SpikeWave(EntityType<? extends Entity> type, Level level) {
		super(type, level);
		this.noPhysics = true;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {

	}

	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}

	public void addPosition(BlockPos pos) {
		if (this.origin == BlockPos.ZERO) {
			this.origin = pos;
			this.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
			this.setOldPosAndRot();
		}

		this.positions.add(pos);

		AABB aabb = new AABB(pos).inflate(0, 1, 0);
		if (this.blockEnclosingBounds == null) {
			this.blockEnclosingBounds = aabb;
		} else {
			this.blockEnclosingBounds = this.blockEnclosingBounds.minmax(aabb);
		}
		this.renderingBounds = this.blockEnclosingBounds.move(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D));
	}

	@Override
	public void tick() {
		this.level().getProfiler().push("entityBaseTick");

		this.setOldPosAndRot();
		this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));

		if (this.tickCount >= this.delay) {
			if (this.level().isClientSide() && this.tickCount == this.delay) {
				this.spawnEmergeParticles();
				this.level().playLocalSound(this.blockPosition(), SoundRegistry.SPIRIT_TREE_SPIKES.get(), SoundSource.HOSTILE, 0.7F, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
			}
			if (this.tickCount == this.delay && this.getDeltaMovement().y() <= 0.0D) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.25D, 0.0D));
			} else {
				this.setDeltaMovement(this.getDeltaMovement().subtract(0.0D, 0.05D, 0.0D));

				if (!this.level().isClientSide() && (this.getY() <= this.origin.getY() || this.onGround())) {
					this.discard();
				}
			}
		} else {
			this.setDeltaMovement(Vec3.ZERO);
		}

		if (this.getY() < this.level().getMinBuildHeight()) {
			this.discard();
		}

		if (this.getY() + this.getDeltaMovement().y() <= this.origin.getY()) {
			this.setDeltaMovement(Vec3.ZERO);
			this.moveTo(this.getX(), this.origin.getY(), this.getZ(), 0, 0);
		} else {
			this.move(MoverType.SELF, new Vec3(0.0D, this.getDeltaMovement().y(), 0.0D));
		}

		if (this.getDeltaMovement().y() > 0.1D && !this.level().isClientSide()) {
			DamageSource damageSource = this.damageSources().source(DamageTypeRegistry.SPIKE_WAVE);
			for (BlockPos pos : this.positions) {
				AABB aabb = new AABB(pos).move(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D)).deflate(0.1D).move(0, 0.2D, 0);
				List<LivingEntity> entities = this.level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.LIVING_ENTITY_STILL_ALIVE.and(EntitySelector.NO_CREATIVE_OR_SPECTATOR));
				for (LivingEntity entity : entities) {
					entity.hurt(damageSource, this.attackDamage);
				}
			}
		}

		this.renderingBounds = this.blockEnclosingBounds.move(this.getX() - (this.origin.getX() + 0.5D), this.getY() - this.origin.getY(), this.getZ() - (this.origin.getZ() + 0.5D));

		this.firstTick = false;
		this.level().getProfiler().pop();
	}

	private void spawnEmergeParticles() {
		if (!this.positions.isEmpty()) {
			int particles = 8 + this.getRandom().nextInt(8);
			for (int i = 0; i < particles; i++) {
				BlockPos pos = this.positions.get(this.getRandom().nextInt(this.positions.size()));

				double x = pos.getX() + this.getRandom().nextDouble();
				double y = pos.getY() + 1;
				double z = pos.getZ() + this.getRandom().nextDouble();
				double mx = (this.getRandom().nextDouble() - 0.5D) * 0.8F;
				double my = 0.1D + this.getRandom().nextDouble() * 0.4F;
				double mz = (this.getRandom().nextDouble() - 0.5D) * 0.8F;

				TheBetweenlands.createParticle(new SpikeParticleOptions(SpikeParticle.ROOT_TEXTURE, this.getRandom().nextInt(40) == 0), this.level(), x, y, z, ParticleFactory.ParticleArgs.get().withMotion(mx, my, mz));
			}

			for (BlockPos pos : this.positions) {
				BlockState state = this.level().getBlockState(pos);

				if (!state.isAir()) {
					int dustParticles = 1 + this.getRandom().nextInt(3);

					for (int i = 0; i < dustParticles; i++) {
						double x = pos.getX() + this.getRandom().nextDouble();
						double y = pos.getY() + 1;
						double z = pos.getZ() + this.getRandom().nextDouble();
						double mx = (this.getRandom().nextDouble() - 0.5D) * 0.3F;
						double my = 0.1D + this.getRandom().nextDouble() * 0.2F;
						double mz = (this.getRandom().nextDouble() - 0.5D) * 0.3F;

						this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, mx, my, mz);
					}
				}
			}
		}
	}

	@Override
	public boolean isPushedByFluid(FluidType type) {
		return false;
	}

	@Override
	protected MovementEmission getMovementEmission() {
		return MovementEmission.NONE;
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf data) {
		data.writeBlockPos(this.origin);

		data.writeInt(this.positions.size());
		for (BlockPos pos : this.positions) {
			data.writeBlockPos(pos);
		}

		data.writeInt(this.delay);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf data) {
		this.origin = data.readBlockPos();

		this.blockEnclosingBounds = null;
		this.positions.clear();
		int size = data.readInt();
		for (int i = 0; i < size; i++) {
			this.addPosition(data.readBlockPos());
		}

		this.delay = data.readInt();
	}

	@Nullable
	public AABB getRenderBoundingBox() {
		return this.renderingBounds;
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.delay = tag.getInt("delay");
		this.origin = BlockPos.of(tag.getLong("origin"));

		this.positions.clear();
		ListTag blocks = tag.getList("positions", Tag.TAG_LONG);
		for (Tag block : blocks) {
			this.addPosition(BlockPos.of(((LongTag) block).getAsLong()));
		}
		if (this.positions.isEmpty()) {
			this.addPosition(this.origin);
		}

		this.attackDamage = tag.getFloat("attack_damage");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("delay", this.delay);
		tag.putLong("origin", this.origin.asLong());

		ListTag blocks = new ListTag();
		for (BlockPos pos : this.positions) {
			blocks.add(LongTag.valueOf(pos.asLong()));
		}
		tag.put("positions", blocks);

		tag.putFloat("attack_damage", this.attackDamage);
	}
}
