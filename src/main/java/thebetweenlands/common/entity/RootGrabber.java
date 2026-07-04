package thebetweenlands.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import net.neoforged.neoforge.fluids.FluidType;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.client.particle.SpikeParticle;
import thebetweenlands.client.particle.options.SpikeParticleOptions;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.MobEffectRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class RootGrabber extends Entity implements IEntityWithComplexSpawn {
	public static final EntityDataAccessor<Float> DAMAGE = SynchedEntityData.defineId(RootGrabber.class, EntityDataSerializers.FLOAT);
	public static final EntityDataAccessor<Boolean> RETRACT = SynchedEntityData.defineId(RootGrabber.class, EntityDataSerializers.BOOLEAN);

	public static final byte EVENT_BROKEN = 40;
	public static final byte EVENT_HIT = 41;

	protected BlockPos origin = BlockPos.ZERO;
	protected int delay;

	protected int maxAge = 12 * 20;

	protected int prevAttackTicks = 0;
	protected int attackTicks = 0;

	protected int prevRetractTicks = 0;
	protected int retractTicks = 0;

	protected boolean emergeSound = true;
	protected boolean retractSound = true;

	@Nullable
	protected LivingEntity grabbedEntity = null;


	private boolean isChains;

	public RootGrabber(EntityType<? extends Entity> type, Level level) {
		this(type, level, false);
	}

	public RootGrabber(EntityType<? extends Entity> type, Level level, boolean isGears) {
		super(type, level);
		this.noPhysics = true;
		this.isChains = isGears;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(DAMAGE, 0.0F);
		builder.define(RETRACT, false);
	}

	public float getDamage() {
		return this.getEntityData().get(DAMAGE);
	}

	public void setDamage(float damage) {
		this.getEntityData().set(DAMAGE, damage);

		if (damage >= 1.0F && !this.level().isClientSide()) {
			this.discard();
			this.level().broadcastEntityEvent(this, EVENT_BROKEN);
		}
	}

	@Override
	public void setPos(double x, double y, double z) {
		this.origin = BlockPos.containing(x, y, z);
		super.setPos(x, y, z);
	}

	public void setPosAndDelay(BlockPos pos, int delay) {
		this.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		this.setOldPosAndRot();
		this.delay = delay;
	}

	public float getRootYOffset(float partialTicks) {
		float attackTicks = this.prevAttackTicks + (this.attackTicks - this.prevAttackTicks) * partialTicks;
		float retractTicks = this.prevRetractTicks + (this.retractTicks - this.prevRetractTicks) * partialTicks;

		float y;
		if (attackTicks < 5) {
			y = -2.5F + attackTicks / 5.0F;
		} else if (attackTicks >= this.delay) {
			y = -1.5F + Math.min(1.25F, (attackTicks - this.delay) / 0.5F);
		} else {
			y = -1.5F;
		}
		y = Math.max(-2.5F, y - retractTicks / 5.0F * 2.5F);
		return y;
	}

	@Override
	public void tick() {
		this.level().getProfiler().push("entityBaseTick");
		if (this.delay == Integer.MAX_VALUE) this.delay = 0;

		this.setOldPosAndRot();
		this.setDeltaMovement(Vec3.ZERO);

		this.prevAttackTicks = this.attackTicks;
		this.prevRetractTicks = this.retractTicks;

		if (this.attackTicks >= this.delay) {
			if (this.attackTicks == this.delay) {
				if (!this.level().isClientSide()) {
					List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox(), e -> EntitySelector.NO_CREATIVE_OR_SPECTATOR.and(entity -> !entity.isInvulnerable()).test(e));
					if (!targets.isEmpty()) {
						this.grabbedEntity = targets.get(this.getRandom().nextInt(targets.size()));
						this.grabbedEntity.moveTo(this.getX(), this.getY() + 1, this.getZ(), this.grabbedEntity.getYRot(), this.grabbedEntity.getXRot());
						this.grabbedEntity.setDeltaMovement(Vec3.ZERO);
						this.grabbedEntity.hurtMarked = true;
						if (this.grabbedEntity instanceof ServerPlayer sp) {
							sp.connection.teleport(this.grabbedEntity.getX(), this.grabbedEntity.getY(), this.grabbedEntity.getZ(), this.grabbedEntity.getYRot(), this.grabbedEntity.getXRot());
						}
					}
				} else {
					this.spawnExtendParticles();
					this.level().playLocalSound(this.blockPosition(), SoundRegistry.SPIRIT_TREE_SPIKES.get(), SoundSource.HOSTILE, 1, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
				}
			}

			if (!this.level().isClientSide() && this.grabbedEntity != null && !this.getEntityData().get(RETRACT)) {
				if (this.getBoundingBox().intersects(this.grabbedEntity.getBoundingBox())) {
					this.grabbedEntity.addEffect(new MobEffectInstance(MobEffectRegistry.ROOT_BOUND, 5, 0, true, false));
				} else {
					this.grabbedEntity = null;
				}
			}

			if (!this.level().isClientSide()) {
				if (this.grabbedEntity != null) {
					if (this.attackTicks >= this.delay + this.maxAge) {
						this.getEntityData().set(RETRACT, true);
					}
				} else {
					if (this.attackTicks >= this.delay + 20) {
						this.getEntityData().set(RETRACT, true);
					}
				}
			}

			if (this.getEntityData().get(RETRACT)) {
				this.retractTicks++;

				if (!this.level().isClientSide() && this.getRootYOffset(1) <= -2.4F) {
					this.discard();
				}
			}
		}

		boolean retracting = this.getEntityData().get(RETRACT);

		if (this.level().isClientSide() && (this.attackTicks <= 5 || retracting)) {
			this.spawnBlockDust();
			if (this.emergeSound && !retracting) {
				this.emergeSound = false;
				this.level().playLocalSound(this.blockPosition(), SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE.get(), SoundSource.HOSTILE, 1, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
			}
			if (this.retractSound && retracting) {
				this.retractSound = false;
				this.level().playLocalSound(this.blockPosition(), SoundRegistry.SPIRIT_TREE_SPIKE_TRAP_EMERGE.get(), SoundSource.HOSTILE, 1, 0.9F + this.getRandom().nextFloat() * 0.2F, false);
			}
		}

		this.attackTicks++;

		this.firstTick = false;
		this.level().getProfiler().pop();
	}

	protected void spawnExtendParticles() {
		if (!this.isChains) {
			for (int i = 0; i < 64; i++) {
				double dx = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
				double dy = this.getBbHeight() / 2.0D - 0.5D;
				double dz = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
				double mx = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				double my = (this.getRandom().nextDouble() - 0.5D) * 0.15D + 0.3D;
				double mz = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				BlockPos pos = BlockPos.containing(this.getX() + dx, Mth.floor(this.getY() + dy), this.getZ() + dz);
				BlockState state = this.level().getBlockState(pos);
				if (!state.isAir()) {
					this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX() + dx, pos.getY() + 1 + this.getRandom().nextDouble() * 0.5D, this.getZ() + dz, mx, my, mz);
				}
			}

			for (int i = 0; i < 8; i++) {
				double dx = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
				double dy = this.getBbHeight() / 2.0D - 0.5D;
				double dz = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
				double mx = (this.getRandom().nextDouble() - 0.5D) * 0.2D;
				double my = (this.getRandom().nextDouble() - 0.5D) * 0.2D + 0.4D;
				double mz = (this.getRandom().nextDouble() - 0.5D) * 0.2D;
				TheBetweenlands.createParticle(new SpikeParticleOptions(SpikeParticle.ROOT_TEXTURE, this.getRandom().nextInt(3) == 0), this.level(), this.getX() + dx, this.getY() + dy, this.getZ() + dz, ParticleFactory.ParticleArgs.get().withMotion(mx, my, mz).withScale(0.4F));
			}
		} else {
			this.spawnBlockDust();
		}
	}

	protected void spawnBlockDust() {
		for (int i = 0; i < 8; i++) {
			double dx = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
			double dy = this.getBbHeight() / 2.0D - 0.5D;
			double dz = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2;
			double mx = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
			double my = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
			double mz = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
			BlockPos pos = BlockPos.containing(this.getX() + dx, Mth.floor(this.getY() + dy), this.getZ() + dz);
			BlockState state = this.level().getBlockState(pos);
			if (!state.isAir()) {
				this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), this.getX() + dx, pos.getY() + 1 + this.getRandom().nextDouble() * 0.5D, this.getZ() + dz, mx, my, mz);
			}
		}
	}

	public boolean isChains() {
		return this.isChains;
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
	public boolean isPickable() {
		return true;
	}

	@Override
	public boolean skipAttackInteraction(Entity entity) {
		if (!this.level().isClientSide()) {
			if (entity instanceof Player player && player.isCreative()) {
				this.setDamage(1.0F);
			} else {
				this.setDamage(this.getDamage() + 0.05F);
			}
			this.level().broadcastEntityEvent(this, EVENT_HIT);
		}
		return true;
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);

		if (id == EVENT_BROKEN) {
			for (int i = 0; i < 128; i++) {
				double dx = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2.2F;
				double dy = (this.getRandom().nextDouble() * 2 - 1) * this.getBbHeight() / 1.2F + this.getBbHeight() / 2;
				double dz = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 2.2F;
				double mx = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				double my = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				double mz = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.SPIRIT_TREE_BARK.get().defaultBlockState()), this.getX() + dx, this.getY() + dy, this.getZ() + dz, mx, my, mz);
			}

			SoundType soundType = SoundType.WOOD;
			this.level().playLocalSound(this.blockPosition(), soundType.getBreakSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, false);
		} else if (id == EVENT_HIT) {
			for (int i = 0; i < 8; i++) {
				double dx = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 4;
				double dy = (this.getRandom().nextDouble() * 2 - 1) * this.getBbHeight() / 2 + this.getBbHeight() / 2;
				double dz = (this.getRandom().nextDouble() * 2 - 1) * this.getBbWidth() / 4;
				double mx = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				double my = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				double mz = (this.getRandom().nextDouble() - 0.5D) * 0.15D;
				this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.SPIRIT_TREE_BARK.get().defaultBlockState()), this.getX() + dx, this.getY() + dy, this.getZ() + dz, mx, my, mz);
			}

			SoundType soundType = SoundType.WOOD;
			this.level().playLocalSound(this.blockPosition(), soundType.getHitSound(), SoundSource.NEUTRAL, (soundType.getVolume() + 1.0F) / 8.0F, soundType.getPitch() * 0.5F, false);
		}
	}

	@Override
	public void writeSpawnData(RegistryFriendlyByteBuf data) {
		data.writeBlockPos(this.origin);
		data.writeInt(this.delay);
		data.writeInt(this.attackTicks);
		data.writeBoolean(this.isChains);
	}

	@Override
	public void readSpawnData(RegistryFriendlyByteBuf data) {
		this.origin = data.readBlockPos();
		this.delay = data.readInt();
		this.attackTicks = data.readInt();
		this.isChains = data.readBoolean();
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.delay = tag.getInt("delay");
		this.origin = BlockPos.of(tag.getLong("origin"));
		this.attackTicks = tag.getInt("attack_ticks");
		this.getEntityData().set(DAMAGE, tag.getFloat("damage"));
		this.isChains = tag.getBoolean("chains");
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("delay", this.delay);
		tag.putLong("origin", this.origin.asLong());
		tag.putInt("attack_ticks", this.attackTicks);
		tag.putFloat("damage", this.getEntityData().get(DAMAGE));
		tag.putBoolean("chains", this.isChains);
	}
}
