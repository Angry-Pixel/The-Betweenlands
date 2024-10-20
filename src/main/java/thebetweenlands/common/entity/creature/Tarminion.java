package thebetweenlands.common.entity.creature;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.client.particle.ParticleFactory;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.FluidTypeRegistry;
import thebetweenlands.common.registries.ParticleRegistry;
import thebetweenlands.common.registries.SoundRegistry;

import java.util.List;

public class Tarminion extends TamableAnimal implements BLEntity {

	private int despawnTicks = 0;

	protected boolean dropContentsWhenDead = true;

	public Tarminion(EntityType<? extends TamableAnimal> type, Level level) {
		super(type, level);
		this.xpReward = 0;
	}

	@Override
	protected void registerGoals() {
		this.goalSelector.addGoal(0, new FloatGoal(this));
		this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
		this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 3.0F, 40.0F));
		this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5D));

		this.targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
		this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
		this.targetSelector.addGoal(2, new HurtByTargetGoal(this, Tarminion.class));
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false, entity ->
			entity instanceof Enemy && (!(entity instanceof OwnableEntity ownable) || ownable.getOwner() != Tarminion.this.getOwner())));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return TamableAnimal.createMobAttributes()
			.add(Attributes.MAX_HEALTH, 60.0D)
			.add(Attributes.MOVEMENT_SPEED, 0.5D)
			.add(Attributes.FOLLOW_RANGE, 32.0D)
			.add(Attributes.ATTACK_DAMAGE, 4.0D)
			.add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
	}


	@Override
	public boolean isTame() {
		return true;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		if (this.getRandom().nextInt(10) == 0) {
			this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 0.8F, 1.5F);
		}
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundRegistry.TARMINION_HURT.get();
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundRegistry.TARMINION_DEATH.get();
	}

	@Override
	public void tick() {
		super.tick();

		if (!this.level().isClientSide()) {
			this.despawnTicks++;
			if (this.despawnTicks > 7200) {
				this.hurt(this.damageSources().generic(), this.getMaxHealth());
			}
		}

		if (this.level().isClientSide() && this.tickCount % 20 == 0) {
			this.spawnParticles(this.level(), this.getX(), this.getY(), this.getZ(), this.getRandom());
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("despawn_ticks", this.despawnTicks);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.despawnTicks = tag.getInt("despawn_ticks");
	}

	@Override
	public void remove(RemovalReason reason) {
		if (reason.shouldDestroy() && !this.dead && this.dropContentsWhenDead) {
			if (this.getTarget() != null) {
				if (this.level().isClientSide()) {
					for (int i = 0; i < 200; i++) {
						RandomSource rnd = this.level().getRandom();
						float rx = rnd.nextFloat() - 0.5F;
						float ry = rnd.nextFloat() - 0.5F;
						float rz = rnd.nextFloat() - 0.5F;
						Vec3 vec = new Vec3(rx, ry, rz);
						vec = vec.normalize();
						TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)), this.level(), this.getX() + rx + 0.1F, this.getY() + ry, this.getZ() + rz + 0.1F, ParticleFactory.ParticleArgs.get().withColor(0.0F, 0.0F, 0.0F, 1.0F).withMotion(vec.x * 0.4F, vec.y * 0.4F, vec.z * 0.4F));
					}
				} else {
					for (int i = 0; i < 8; i++) {
						this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 1F, (this.getRandom().nextFloat() * 0.4F + 0.8F) * 0.8F);
					}
					List<Mob> affectedEntities = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(5.25F));
					for (Mob e : affectedEntities) {
						if (e == this || e.distanceTo(this) > 5.25F || !e.hasLineOfSight(this) || e instanceof Tarminion) continue;
						double dst = e.distanceTo(this);
						e.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 4);
						e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (int) (20 + (1.0F - dst / 5.25F) * 150), 1));
					}
				}
			}

			if (!this.level().isClientSide()) {
				this.dropAllDeathLoot((ServerLevel) this.level(), this.damageSources().generic());
			}

			this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 2.5F, 0.5F);

			if (this.level().isClientSide()) {
				for (int i = 0; i < 100; i++) {
					RandomSource rnd = this.level().getRandom();
					float rx = rnd.nextFloat() - 0.5F;
					float ry = rnd.nextFloat() - 0.5F;
					float rz = rnd.nextFloat() - 0.5F;
					Vec3 vec = new Vec3(rx, ry, rz);
					vec = vec.normalize();
					TheBetweenlands.createParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.SLIME_BALL)), this.level(), this.getX() + rx + 0.1F, this.getY() + ry, this.getZ() + rz + 0.1F, ParticleFactory.ParticleArgs.get().withColor(0.0F, 0.0F, 0.0F, 1.0F).withMotion(vec.x * 0.2F, vec.y * 0.2F, vec.z * 0.2F));
				}
			}
		}

		super.remove(reason);
	}

	@Override
	public boolean doHurtTarget(Entity entity) {
		super.doHurtTarget(entity);
		return this.attack(entity);
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.DROWN) && this.isEyeInFluidType(FluidTypeRegistry.TAR.get())) {
			return false;
		}
		if (source.getEntity() instanceof Mob) {
			this.attack(source.getEntity());
		}
		return super.hurt(source, amount);
	}

	protected boolean attack(Entity entity) {
		if (!this.level().isClientSide()) {
			if (this.onGround()) {
				double dx = entity.getX() - this.getX();
				double dz = entity.getZ() - this.getZ();
				double dist = Mth.sqrt((float) (dx * dx + dz * dz));
				this.setDeltaMovement(dx / dist * 0.2D + this.getDeltaMovement().x() * 0.2D, 0.3D, dz / dist * 0.2D + this.getDeltaMovement().z() * 0.2D);
			}

			DamageSource damageSource;

			LivingEntity owner = this.getOwner();
			if (owner != null) {
				damageSource = this.damageSources().source(DamageTypes.MOB_ATTACK, this, owner);
			} else {
				damageSource = this.damageSources().mobAttack(this);
			}

			entity.hurt(damageSource, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));

			if (entity instanceof LivingEntity living && this.level().getRandom().nextInt(4) == 0) {
				//Set revenge target to tarminion so it can be attacked by the mob
				living.setLastHurtByMob(this);
				living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, this.level().getDifficulty().getId() * 50, 0));
			}

			this.playSound(SoundRegistry.TAR_BEAST_STEP.get(), 1.0F, 2.0F);



			return true;
		}
		return true;
	}

	public void spawnParticles(Level level, double x, double y, double z, RandomSource rand) {
		for (int count = 0; count < 3; ++count) {
			double a = Math.toRadians(this.getYRot());
			double offSetX = -Math.sin(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			double offSetZ = Math.cos(a) * 0D + rand.nextDouble() * 0.1D - rand.nextDouble() * 0.1D;
			TheBetweenlands.createParticle(ParticleRegistry.DRIPPING_TAR.get(), level, x + offSetX, y + 0.1D, z + offSetZ, ParticleFactory.ParticleArgs.get().withColor(0.0F, 0.0F, 0.0F, 1.0F));
		}
	}

//	@Override
//	public void setDropItemsWhenDead(boolean dropWhenDead) {
//		this.dropContentsWhenDead = dropWhenDead;
//	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionTransition transition) {
		this.dropContentsWhenDead = false;
		return super.changeDimension(transition);
	}

	@Override
	public boolean isFood(ItemStack stack) {
		return false;
	}

	@Override
	public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
		return null;
	}
}
