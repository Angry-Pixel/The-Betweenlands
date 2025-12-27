package thebetweenlands.common.entity;


import java.util.List;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import thebetweenlands.common.entity.monster.LargeSludgeWorm;
import thebetweenlands.common.entity.monster.SludgeWorm;
import thebetweenlands.common.entity.monster.TinySludgeWorm;
import thebetweenlands.common.registries.EntityRegistry;

public class WormGroundSpawner extends CCGroundSpawner {

	public WormGroundSpawner(EntityType<? extends BasicProximitySpawnerExtended> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
	}

	// TODO Enable after testing
	@Override
	public boolean isSpawnEventActive(Level level) {
		//BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(this.level());
		//return worldStorage != null && EnvironmentEventRegistry.HEAVY_RAIN.get().isActive();
		return true;
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
				if(list.stream().filter(e -> e instanceof SludgeWorm).count() >= 4)
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

	@Override
    public boolean canBeRemovedNow() {
    	AABB dead_zone = getBoundingBox().inflate(0D, 1D, 0D).move(0D, -0.5D, 0D);
		List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, dead_zone);
		if(list.stream().filter(e -> e instanceof SludgeWorm).count() >= 1)
			return false;
        return true;
    }

	@SuppressWarnings("deprecation")
	@Override
	public Entity getEntitySpawned() {
		Monster worm = null;
		int rand = random.nextInt(5);

		switch (rand) {
		case 0:
			worm = new LargeSludgeWorm(EntityRegistry.LARGE_SLUDGE_WORM.get(), level());
			break;
		case 1:
		case 2:
			worm  = new SludgeWorm(EntityRegistry.SLUDGE_WORM.get(), level());
			break;
		case 3:
		case 4:
			worm  = new TinySludgeWorm(EntityRegistry.TINY_SLUDGE_WORM.get(), level());
			break;
		}

		if(worm != null)
			worm.finalizeSpawn((ServerLevelAccessor)level(), level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.SPAWNER, null);
		return worm;
	}
	
	@Override
	public void performPreSpawnaction(Entity targetEntity, Entity entitySpawned) {
		if(isWorldSpawned())
			setSpawnCount(getSpawnCount() + 1);
		level().playSound(null, blockPosition(), getDigSound(), SoundSource.HOSTILE, 0.5F, 1.0F);
		entitySpawned.setPos(blockPosition().getX() + 0.5F, blockPosition().getY(), blockPosition().getZ() + 0.5F);
	}

	@Override
	public int maxUseCount() {
		return 8;
	}
}