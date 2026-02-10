package thebetweenlands.common.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.registries.SoundRegistry;

public class TriggeredFallingBlock extends BasicProximitySpawnerExtended {

	private static final EntityDataAccessor<Boolean> IS_WALK_WAY = SynchedEntityData.defineId(TriggeredFallingBlock.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_HANGING = SynchedEntityData.defineId(TriggeredFallingBlock.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> IS_TEMP = SynchedEntityData.defineId(TriggeredFallingBlock.class, EntityDataSerializers.BOOLEAN);

	public TriggeredFallingBlock(EntityType<? extends BasicProximitySpawner> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_WALK_WAY, false);
		builder.define(IS_HANGING, false);
		builder.define(IS_TEMP, false);
	}

	@Override
	public void tick() {
		if (!level().isClientSide()) {
			if (isTemporary() && tickCount > 20)
				discard();
			if (level().getGameTime() % 5 == 0) {
				if (level().isEmptyBlock(blockPosition()))
					discard();
				checkArea(this, LivingEntity.class);
			}
		}
		if (level().isClientSide())
			dustParticles();
	}

	@SuppressWarnings("static-access")
	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
		for (int count = 0; count < getEntitySpawnCount(); count++) {
			Entity spawn = getEntitySpawned();
			if (spawn != null) {
				performPreSpawnaction(this, spawn);
				if (spawn.isAlive())// just in case of pre-emptive removal
					((FallingBlockEntity) spawn).fall(level(), blockPosition(), getBlockType(level(), blockPosition()));
				performPostSpawnaction(this, spawn);
			}
		}
		if (isAlive() && isSingleUse())
			discard();

	}

	public void dustParticles() {
		if (random.nextInt(16) == 0) {
			BlockPos blockpos = blockPosition().below();
			if (canFallThrough(level().getBlockState(blockpos)) && blockPosition().getY() >= level().getMinBuildHeight()) {
				double d0 = (double) ((float) blockPosition().getX() + random.nextFloat());
				double d1 = !isWalkway() ? (double) blockPosition().getY() - 0.05D : (double) blockPosition().getY() + 1D;
				double d2 = (double) ((float) blockPosition().getZ() + random.nextFloat());
				if(!isWalkway())
					level().addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, getBlockType(level(), blockpos)), d0, d1, d2, 0.0D, 0.0D, 0.0D);
				else {
					double motionX = random.nextDouble() * 0.1F - 0.05F;
					double motionY = random.nextDouble() * 0.025F + 0.025F;
					double motionZ = random.nextDouble() * 0.1F - 0.05F;
					level().addParticle(new BlockParticleOption(ParticleTypes.FALLING_DUST, getBlockType(level(), blockpos)), d0, d1, d2, motionX, motionY, motionZ);
				}
			}
		}
	}

    @SuppressWarnings("deprecation")
	public static boolean canFallThrough(BlockState state) {
    	return state.isAir() || state.is(BlockTags.FIRE) || state.liquid() || state.canBeReplaced();
    }

	@Override
	public void performPreSpawnaction(Entity spawner, Entity entitySpawned) {
		((FallingBlockEntity)entitySpawned).dropItem  = false;
		 if (!level().isClientSide()) {
			 spawner.level().playSound(null, spawner.blockPosition(), SoundRegistry.ROOF_COLLAPSE.get(), SoundSource.BLOCKS, 0.5F, 1.0F);
		 }
	}

	@Override
	public float getProximityHorizontal() {
		return isWalkway() ? 0.0625F : 0.25F;
	}

	@Override
	public float getProximityVertical() {
		return 1F;
	}

	@Override
	public AABB proximityBox(LivingEntity spawner) {
		return getBoundingBox().inflate(getProximityHorizontal(), getProximityVertical(), getProximityHorizontal()).move(0D, isWalkway() ? 1D : - getProximityVertical () * 2 , 0D);
	}

	@Override
	public boolean checkSight() {
		return false;
	}

	@Override
	public Entity getEntitySpawned() {
		if(getBlockType(level(), blockPosition()).getBlock() != null) {
			FallingBlockEntity entity = EntityType.FALLING_BLOCK.create(this.level());
			return entity;
		}
		return null;
	}

	private BlockState getBlockType(Level level, BlockPos pos) {
		return level.getBlockState(pos);
	}

	@Override
	public int getEntitySpawnCount() {
		return 1;
	}

	@Override
	public int maxUseCount() {
		return 0;
	}

	public void setWalkway(boolean walkway) {
		getEntityData().set(IS_WALK_WAY, walkway);
	}

	public boolean isWalkway() {
		return getEntityData().get(IS_WALK_WAY);
	}

	public boolean isHanging() {
		return getEntityData().get(IS_HANGING);
	}

	public void setHanging(boolean walkway) {
		getEntityData().set(IS_HANGING, walkway);
	}

	public boolean isTemporary() {
		return getEntityData().get(IS_TEMP);
	}

	public void setTemporary(boolean temp) {
		getEntityData().set(IS_TEMP, temp);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag nbt) {
		super.readAdditionalSaveData(nbt);
		setWalkway(nbt.getBoolean("walk_way"));
		setHanging(nbt.getBoolean("hanging"));
		setTemporary(nbt.getBoolean("temp"));
	}

	@Override
	  public void addAdditionalSaveData(CompoundTag nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean("walk_way", isWalkway());
		nbt.putBoolean("hanging", isHanging());
		nbt.putBoolean("temp", isTemporary());
	}
}