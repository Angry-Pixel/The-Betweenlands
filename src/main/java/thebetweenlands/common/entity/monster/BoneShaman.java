package thebetweenlands.common.entity.monster;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thebetweenlands.common.entity.BLEntity;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public class BoneShaman extends Monster implements BLEntity {

	public static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(BoneShaman.class, EntityDataSerializers.INT);

	public int lastSpawningAnimationTicks = 0;
	public int spawnDuration = 30;

	public BoneShaman(EntityType<? extends Monster> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SPAWN_TIMER, 0);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new MeleeAttackGoal(this, 1D, true));
		goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.7D));
		targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Mob.createMobAttributes()
				.add(Attributes.MAX_HEALTH, 2.0D)
				.add(Attributes.MOVEMENT_SPEED, 0.33D)
				.add(Attributes.ATTACK_DAMAGE, 0.5D)
				.add(Attributes.ATTACK_KNOCKBACK, 2.0D)
				.add(Attributes.FOLLOW_RANGE, 64.0D);
	}
	
	@Override
	public void aiStep() {
		if (isAlive()) {
			lastSpawningAnimationTicks = getSpawnTimer();
			if (!level().isClientSide()) {
				if (getSpawnTimer() < spawnDuration)
					setSpawnTimer(getSpawnTimer() + 1);
				if (getSpawnTimer() == spawnDuration -1) //Temp 
					spawnPuppets();
			}
			if (level().isClientSide())
				if (getSpawnTimer() < 10)
					spawnEmergingParticles();
		}

		super.aiStep();

		if (level().isClientSide()) {
			if (isDeadOrDying())
				setDeltaMovement(Vec3.ZERO);
		}

		if (!level().isClientSide()) {
			if (isDeadOrDying())
				getNavigation().stop();
		}
	}

    private void spawnPuppets() {
    	//TODO clean up later
    	List<BlockPos> list = new ArrayList<>();
		AABB searchBox = new AABB(blockPosition()).inflate(8D, 8D, 8D);
		BlockPos minPos = BlockPos.containing(searchBox.minX, searchBox.minY, searchBox.minZ);
		BlockPos maxPos = BlockPos.containing(searchBox.maxX, searchBox.maxY, searchBox.maxZ);
		for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos)) {
			if (level().getBlockState(pos).is(BlockRegistry.SLIMY_BONE_ORE) && level().isEmptyBlock(pos.above())) {
				list.add(new BlockPos(pos.getX(), pos.getY(), pos.getZ()));
			}
		}
		if (!list.isEmpty()) {// && list.size() >= 4) { TODO a nice way to set amounts
			for (int spawn = 0; spawn < list.size(); spawn++) {
				BonePuppetRanged puppet1 = new BonePuppetRanged(EntityRegistry.BONE_PUPPET_RANGED.get(), level());
				BonePuppetMelee puppet2 = new BonePuppetMelee(EntityRegistry.BONE_PUPPET_MELEE.get(), level());
				level().destroyBlock(list.get(spawn), true);
				if (level().getRandom().nextBoolean()) {
					if (puppet1 != null) {
						puppet1.setPos(list.get(spawn).getBottomCenter());
						puppet1.setYRot(this.getYRot());
						puppet1.setParentEntityID(this.getId());
						level().addFreshEntity(puppet1);
					}
				} else {
					if (puppet2 != null) {
						puppet2.setPos(list.get(spawn).getBottomCenter());
						puppet2.setYRot(this.getYRot());
						puppet2.setParentEntityID(this.getId());
						level().addFreshEntity(puppet2);
					}
				}
			}
		}
	}

	public void spawnEmergingParticles() {
		double px = getX();
		double py = getY();
		double pz = getZ();
		for (int i = 0, amount = 5 + level().getRandom().nextInt(2); i < amount; i++) {
			double ox = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double oz = level().getRandom().nextDouble() * 0.1F - 0.05F;
			double motionX = level().getRandom().nextDouble() * 0.2F - 0.1F;
			double motionY = level().getRandom().nextDouble() * 0.1F + 0.075F;
			double motionZ = level().getRandom().nextDouble() * 0.2F - 0.1F;
			level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, getBlockStateOn()), false, px + ox, py, pz + oz, motionX, motionY, motionZ);
		}
	}

	@Override
    protected boolean isImmobile() {
        return isAlive() && (super.isImmobile() || getSpawnTimer() < spawnDuration);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        boolean isCreative = source.getEntity() instanceof Player player && player.isCreative();
        return (isEmerging() && !isCreative) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPushable() {
        return !isEmerging() && super.isPushable();
    }

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (isEmerging() && source.is(DamageTypes.IN_WALL))
			return false;
		return super.hurt(source, amount);
	}

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("spawn_timer", getSpawnTimer());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        setSpawnTimer(compound.getInt("spawn_timer"));
    }

    @Override
    protected SoundEvent getAmbientSound() {
		return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
		return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
		return null;
    }

	public boolean isEmerging() {
		return getEntityData().get(SPAWN_TIMER) < spawnDuration;
	}

	public int getSpawnTimer() {
		return getEntityData().get(SPAWN_TIMER);
	}

	public void setSpawnTimer(int timer) {
		getEntityData().set(SPAWN_TIMER, timer);
	}

    public float getSpawningAnimation(float partialTicks) {
        return Mth.lerp(partialTicks, lastSpawningAnimationTicks, getSpawnTimer()) / (float) spawnDuration;
    }

	// May need this for things and stuffs
	public boolean isWearingSkullMask(LivingEntity entity) {
		ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
		return !helmet.isEmpty() && helmet.is(ItemRegistry.SKULL_MASK);
	}

}
