package thebetweenlands.common.entity;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.block.misc.SlopeBlock;
import thebetweenlands.common.entity.monster.BipedCryptCrawler;
import thebetweenlands.common.entity.monster.ChiefCryptCrawler;
import thebetweenlands.common.entity.monster.CryptCrawler;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.gen.SurfaceType;

public class CCGroundSpawner extends BasicProximitySpawnerExtended {
	private static final byte EVENT_DIG_PARTICLES = 100;
	
	private static final EntityDataAccessor<Boolean> IS_WORLD_SPANWED = SynchedEntityData.defineId(CCGroundSpawner.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> SPAWN_COUNT = SynchedEntityData.defineId(CCGroundSpawner.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> CAN_BE_REMOVED_SAFELY = SynchedEntityData.defineId(CCGroundSpawner.class, EntityDataSerializers.BOOLEAN);
	//private SludgeWormMazeBlockHelper blockHelper = new SludgeWormMazeBlockHelper(null);

	// TODO Check Methods marked with TODOs to Enable after testing
	
	public CCGroundSpawner(EntityType<? extends BasicProximitySpawnerExtended> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(IS_WORLD_SPANWED, true);
		builder.define(SPAWN_COUNT, 0);
		builder.define(CAN_BE_REMOVED_SAFELY, false);
	}

	@SuppressWarnings("deprecation")
	public static boolean canSpawnHere(EntityType<CCGroundSpawner> entity, LevelAccessor level, MobSpawnType spawn, BlockPos pos, RandomSource random) {
		int solidCount = 0;

		// TODO Enable after testing
		//if(pos.getY() < TheBetweenlands.CAVE_START) {
		//return false;
		//}

		for(int xo = -1; xo <= 1; xo++) {
			for(int zo = -1; zo <= 1; zo++) {
				BlockPos offsetPos = pos.offset(xo, 0, zo);
				BlockState state = level.getBlockState(offsetPos);
				if(state.liquid())
					return false;
				if(SurfaceType.MIXED_GROUND.apply(state))
					solidCount++;
				else if(xo == 0 && zo == 0)
					return false;
				if(!level.isEmptyBlock(offsetPos.above()))
					return false;
			}
		}
		return solidCount >= 6;
	}

	@Override
	protected boolean isImmobile() {
		return true;
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public void tick() {
		super.tick();

		if (!level().isClientSide()) {
			if(isWorldSpawned() && !isSpawnEventActive(level()))
				kill();
			if (level().getGameTime() % 60 == 0)
				checkArea(this, LivingEntity.class);
			List<FallingBlockEntity> listPlug = level().getEntitiesOfClass(FallingBlockEntity.class, getBoundingBox().move(0D,0.5D,0D));
			if (!listPlug.isEmpty()) {
				System.out.println("should die here");
				level().setBlock(blockPosition(), Blocks.AIR.defaultBlockState(), 3);
				kill();
			}
		}
		setPos(Math.floor(blockPosition().getX()) + 0.5D, Math.floor(blockPosition().getY()), Math.floor(blockPosition().getZ()) + 0.5D);
		xo = xOld;
		yo = yOld;
		zo = zOld;
	}

	// TODO Enable after testing
	public boolean isSpawnEventActive(Level level) {
		//BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(this.level());
		//return worldStorage != null && EnvironmentEventRegistry.BLOOD_SKY.get().isActive();
		return true;
	}

	@Override
	public <T extends LivingEntity> void performDetectionLogic(T detected) {
	}

	@Override
	public <T extends LivingEntity> void checkArea(LivingEntity spawner, Class<T> toDetect) {
		if (!level().isClientSide()) {
			if(getCanBeRemovedSafely() && canBeRemovedNow())
				kill();
			if (level().getDifficulty() != Difficulty.PEACEFUL) {
				if(isWorldSpawned() && !isSpawnEventActive(level()))
					return;
				List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, proximityBox(this));
				if(list.stream().filter(e -> e instanceof CryptCrawler).count() >= 4)
					return;
				for (int entityCount = 0; entityCount < list.size(); entityCount++) {
					Entity entity = list.get(entityCount);
					if (entity != null)
						if (entity instanceof Player && !((Player) entity).isSpectator() && !((Player) entity).isCreative()) {
							if (canSneakPast() && entity.isCrouching())
								return;
							else if (checkSight() && !hasLineOfSight(entity) || getCanBeRemovedSafely())
								return;
							else {
								for (int count = 0; count < getEntitySpawnCount(); count++) {
									Entity spawn = getEntitySpawned();
									if (spawn != null) {
										performPreSpawnaction(entity, spawn);
										if (spawn.isAlive()) // just in case of pre-emptive removal
											level().addFreshEntity(spawn);
										performPostSpawnaction(entity, spawn);
									}
								}
							}
						}
				}
			}
		}
	}

    public boolean canBeRemovedNow() {
    	AABB dead_zone = getBoundingBox().inflate(0D, 1D, 0D).move(0D, -0.5D, 0D);
		List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, dead_zone);
		if(list.stream().filter(e -> e instanceof CryptCrawler).count() >= 1)
			return false;

        return true;
    }

    @Override
    protected void doPush(Entity entity) {
		if (entity instanceof FallingBlockEntity)
			if (!level().isClientSide())
				setCanBeRemovedSafely(true);
	}

	@Override
	public boolean isInvulnerable() {
		return true;
	}

	@Override
	public boolean hurt(DamageSource source, float damage) {
		if (source.isCreativePlayer())
			setCanBeRemovedSafely(true);
		return false;
	}

	@Override
	public void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		level().playSound(null, blockPosition(), getDigSound(), SoundSource.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPos(blockPosition().getX() + 0.5F, blockPosition().getY() - 1.5F, blockPosition().getZ() + 0.5F);
	}

	protected SoundEvent getDigSound() {
		return SoundRegistry.CRYPT_CRAWLER_DIG.get();
	}

	@Override
	public void performPostSpawnaction(Entity targetEntity, @Nullable Entity entitySpawned) {
		if(!level().isClientSide()) {
			this.level().broadcastEntityEvent(this, EVENT_DIG_PARTICLES);
			entitySpawned.setDeltaMovement(entitySpawned.getDeltaMovement().add(0D, 0.5D, 0D));
			if(isWorldSpawned() && getSpawnCount() >= maxUseCount())
				setCanBeRemovedSafely(true);
		}
	}

	@Override
	public void handleEntityEvent(byte id) {
		super.handleEntityEvent(id);
		if (id == EVENT_DIG_PARTICLES)
			for (int count = 0; count <= 200; ++count)
				level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, BlockRegistry.COMPACTED_MUD.get().defaultBlockState()), false, this.getX() + (this.getRandom().nextDouble() - 0.5D), this.getY() + 1D + this.getRandom().nextDouble(), this.getZ() + (this.getRandom().nextDouble() - 0.5D), 0.0D, 0.0D, 0.0D);
	}

	@Override
	public float getProximityHorizontal() {
		return 8F;
	}

	@Override
	public float getProximityVertical() {
		return 2F;
	}

	@Override
	public boolean canSneakPast() {
		return false;
	}

	@Override
	public boolean checkSight() {
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Entity getEntitySpawned() {
		boolean isBiped = false;
		boolean isChief = false;
		CryptCrawler crawler = new CryptCrawler(EntityRegistry.CRYPT_CRAWLER.get(), level());
		BipedCryptCrawler biped_crawler = new BipedCryptCrawler(EntityRegistry.BIPED_CRYPT_CRAWLER.get(), level());
		ChiefCryptCrawler chief_crawler = new ChiefCryptCrawler(EntityRegistry.CHIEF_CRYPT_CRAWLER.get(), level());
		crawler.finalizeSpawn((ServerLevelAccessor)level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.SPAWNER, null);
		biped_crawler.finalizeSpawn((ServerLevelAccessor)level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.SPAWNER, null);
		chief_crawler.finalizeSpawn((ServerLevelAccessor)level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.SPAWNER, null);
		if (random.nextInt(3) == 0) {
			isBiped = true;
			if (random.nextInt(3) == 0)
				isChief = true;
		}
		if (isBiped)
			if (random.nextFloat() < 0.05F)
				setLeftHanded(true);
			else
				setLeftHanded(false);
		return isBiped && isChief ? chief_crawler : isBiped ? biped_crawler : crawler;
	}

	@Override
	protected int getEntitySpawnCount() {
		return 1;
	}

	@Override
	public boolean isSingleUse() {
		return false;
	}

	@Override
	protected int maxUseCount() {
		return 5;
	}

	public void setIsWorldSpawned(boolean world_spawned) {
		getEntityData().set(IS_WORLD_SPANWED, world_spawned);
	}

	public boolean isWorldSpawned() {
		return getEntityData().get(IS_WORLD_SPANWED);
	}

	public void setSpawnCount(int spawn_count) {
		getEntityData().set(SPAWN_COUNT, spawn_count);
	}

	public int getSpawnCount() {
		return getEntityData().get(SPAWN_COUNT);
	}

	public void setCanBeRemovedSafely(boolean safe) {
		getEntityData().set(CAN_BE_REMOVED_SAFELY, safe);
	}

	public boolean getCanBeRemovedSafely() {
		return getEntityData().get(CAN_BE_REMOVED_SAFELY);
	}

	@Override
    public void kill() {
		if(!level().isClientSide()) {
			if(isWorldSpawned())
				if(getPersistentData().contains("tempBlockTypes"))
					loadOriginBlocks(level(), getPersistentData());
        super.kill();
        }
    }

	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData) {
		spawnGroupData = super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
		if (!level().isClientSide()) {
			getOriginBlocks(level(), blockPosition());
			level().setBlockAndUpdate(blockPosition(), Blocks.AIR.defaultBlockState());
			level().setBlockAndUpdate(blockPosition().offset(0, -1, 0), BlockRegistry.COMPACTED_MUD.get().defaultBlockState());
			level().setBlockAndUpdate(blockPosition().offset(-1, 0, -1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.NORTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(0, 0, -1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.NORTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(1, 0, -1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.NORTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(-1, 0, 1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.SOUTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(0, 0, 1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.SOUTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(1, 0, 1), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.SOUTH).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(-1, 0, 0), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.WEST).setValue(SlopeBlock.HALF, Half.BOTTOM));
			level().setBlockAndUpdate(blockPosition().offset(1, 0, 0), BlockRegistry.COMPACTED_MUD_SLOPE.get().defaultBlockState().setValue(SlopeBlock.FACING, Direction.EAST).setValue(SlopeBlock.HALF, Half.BOTTOM));
		}
		return spawnGroupData;
	}

	private void getOriginBlocks(Level level, BlockPos pos) {
		ListTag tagList = new ListTag();
		CompoundTag entityNbt = getPersistentData();
		for (int x = -1; x <= 1; x ++)
			for (int z = -1; z <= 1; z++) 
				for(int y = 0; y <= 1; y++) {
				BlockState state = level.getBlockState(pos.offset(x, -y, z));
				tagList.add(NbtUtils.writeBlockState(state));
			}
		if (!tagList.isEmpty()) {
			entityNbt.put("tempBlockTypes", tagList);
			entityNbt.put("originPos",  NbtUtils.writeBlockPos(pos));
		}
		addAdditionalSaveData(entityNbt);
	}

	public void loadOriginBlocks(Level level, CompoundTag tag) {
		BlockPos origin = NbtUtils.readBlockPos(tag, "originPos").orElse(null);
		List<BlockState> list = new ArrayList<BlockState>();
		ListTag tagList = tag.getList("tempBlockTypes", Tag.TAG_COMPOUND);
		for (int indexCount = 0; indexCount < tagList.size(); ++indexCount) {
			BlockState state = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK), tagList.getCompound(indexCount));
			list.add(indexCount, state);
		}
		int a = 0;
		for (int x = -1; x <= 1; x++)
			for (int z = -1; z <= 1; z++)
				for(int y = 0; y <= 1; y++) {
				level.setBlock(origin.offset(x, -y, z), list.get(a++), 3);
			}
		level().playSound(null, origin, SoundRegistry.ROOF_COLLAPSE.get(), SoundSource.BLOCKS, 1F, 1.0F);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if(tag.contains("world_spawned", Tag.TAG_BYTE))
			setIsWorldSpawned(tag.getBoolean("world_spawned"));
		if(tag.contains("remove_safely", Tag.TAG_BYTE))
			setCanBeRemovedSafely(tag.getBoolean("remove_safely"));
		setSpawnCount(tag.getInt("spawn_count"));
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putBoolean("world_spawned", isWorldSpawned());
		tag.putBoolean("remove_safely", getCanBeRemovedSafely());
		tag.putInt("spawn_count", getSpawnCount());
	}

}