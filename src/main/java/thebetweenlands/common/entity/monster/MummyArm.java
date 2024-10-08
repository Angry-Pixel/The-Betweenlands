package thebetweenlands.common.entity.monster;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.EntityRegistry;

public class MummyArm extends Mob implements BLEntity, OwnableEntity {

	private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(MummyArm.class, EntityDataSerializers.OPTIONAL_UUID);

	public int attackSwing = 0;

	private int spawnTicks = 0;

	private int despawnTicks = 0;

	private int deathTicks = 0;

	private double yOffset = 0.0D;

	public MummyArm(EntityType<? extends Mob> type, Level level) {
		super(type, level);
	}

	public MummyArm(Level level, Player player) {
		super(EntityRegistry.MUMMY_ARM.get(), level);
		this.getEntityData().set(OWNER_ID, Optional.of(player.getUUID()));
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(OWNER_ID, Optional.empty());
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		this.setYRot(this.level().getRandom().nextFloat() * 360.0F);
		this.yRotO = this.getYRot();
		return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
			.add(Attributes.ATTACK_DAMAGE, 3.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
	}

	public double getYOffset() {
		return this.yOffset;
	}

	@Override
	public void tick() {
		super.tick();

		BlockPos pos = this.blockPosition().below(1);
		BlockState blockState = this.level().getBlockState(pos);

		if(!this.level().isClientSide()) {
			if(blockState.isAir() || (this.getOwner() instanceof Player && !Block.isFaceFull(blockState.getBlockSupportShape(this.level(), pos), Direction.UP))) {
				this.discard();
			}

			Entity owner = this.getOwner();

			if(this.getOwner() instanceof Player && (owner == null || owner.distanceTo(this) > 32.0D)) {
				this.setHealth(0);
			} else if(owner instanceof Player player) {
//				if(!ItemRingOfSummoning.isRingActive(player)) {
//					this.setHealth(0);
//				}
			}

			if(this.despawnTicks >= 150) {
				this.setHealth(0);
			} else {
				if(this.spawnTicks >= 40) {
					this.despawnTicks++;
				}
			}
		}

		if(this.deathTicks > 0) {
			this.yOffset = -this.deathTicks / 40.0F;
		} else if(this.spawnTicks >= 40) {
			this.yOffset = 0.0F;
		}

		if(this.isAlive()) {
			if(this.spawnTicks >= 4) {
				List<LivingEntity> targets = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
				for(LivingEntity target : targets) {

					boolean isValidTarget;
					if(this.getOwner() instanceof Player player) {
						isValidTarget = target != this && target != player && (target instanceof PathfinderMob || target instanceof Enemy);
					} else {
						isValidTarget = target instanceof Player;
					}
					if(isValidTarget) {
						target.makeStuckInBlock(Blocks.AIR.defaultBlockState(), new Vec3(0.25F, 0.5F, 0.25F));

						if(target.invulnerableTime < 10) {
							DamageSource damageSource;
							Entity owner = this.getOwner();
							if(owner != null) {
								damageSource = this.damageSources().source(DamageTypes.MOB_ATTACK_NO_AGGRO, this, owner);
							} else {
								damageSource = this.damageSources().noAggroMobAttack(this);
							}

							target.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

							if(this.attackSwing <= 0) {
								this.attackSwing = 20;
							}
						}
					}
				}
			}

			if(this.spawnTicks < 40) {
				this.spawnTicks++;
				this.yOffset = -1 + this.spawnTicks / 40.0F;
			} else {
				this.yOffset = 0.0F;
			}

			if(this.attackSwing > 0) {
				this.attackSwing--;
			}
		}

		if(this.level().isClientSide() && this.getRandom().nextInt(this.yOffset < 0.0F ? 2 : 8) == 0) {
			if(!blockState.isAir()) {
				double px = this.getX();
				double py = this.getY();
				double pz = this.getZ();
				for (int i = 0, amount = 2 + this.getRandom().nextInt(this.yOffset < 0.0F ? 8 : 3); i < amount; i++) {
					double ox = this.getRandom().nextDouble() * 0.1F - 0.05F;
					double oz = this.getRandom().nextDouble() * 0.1F - 0.05F;
					double motionX = this.getRandom().nextDouble() * 0.2 - 0.1;
					double motionY = this.getRandom().nextDouble() * 0.1 + 0.1;
					double motionZ = this.getRandom().nextDouble() * 0.2 - 0.1;
					this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), px + ox, py, pz + oz, motionX, motionY, motionZ);
				}
			}
		}
	}

	@Override
	public void travel(Vec3 travelVector) {

	}

	@Override
	public boolean isPushedByFluid() {
		return false;
	}

	@Override
	protected void tickDeath() {
		this.deathTicks++;

		if(!this.level().isClientSide() && this.deathTicks >= 40) {
			this.discard();
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if(this.getOwnerUUID() != null) {
			tag.putUUID("ownerUUID", this.getOwnerUUID());
		}
		tag.putInt("spawnTicks", this.spawnTicks);
		tag.putInt("despawnTicks", this.despawnTicks);
		tag.putInt("deathTicks", this.deathTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if(tag.contains("ownerUUID")) {
			this.getEntityData().set(OWNER_ID, Optional.of(tag.getUUID("ownerUUID")));
		}
		this.spawnTicks = tag.getInt("spawn_ticks");
		this.despawnTicks = tag.getInt("despawn_ticks");
		this.deathTicks = tag.getInt("death_ticks");
	}

	@Nullable
	@Override
	public UUID getOwnerUUID() {
		return this.getEntityData().get(OWNER_ID).orElse(null);
	}
}
