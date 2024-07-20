package thebetweenlands.common.block.entity.spawner;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class BetweenlandsBaseSpawner extends BaseSpawner {

	private double checkRange = 8.0D;
	private boolean spawnInAir = true;
	private boolean spawnParticles = true;

	public BetweenlandsBaseSpawner setNextEntityName(EntityType<?> type, @Nullable Level level, RandomSource random, BlockPos pos) {
		this.setEntityId(type, level, random, pos);
		this.setChanged(level, pos);
		return this;
	}

	public BetweenlandsBaseSpawner setNextEntity(@Nullable Level level, BlockPos pos, SpawnData data) {
		this.setNextSpawnData(level, pos, data);
		this.setChanged(level, pos);
		return this;
	}

	public BetweenlandsBaseSpawner setNextEntity(@Nullable Level level, RandomSource random, BlockPos pos, EntityType<?> type) {
		this.setNextSpawnData(level, pos, new SpawnData());
		this.setNextEntityName(type, level, random, pos);
		this.setChanged(level, pos);
		return this;
	}

	public BetweenlandsBaseSpawner setEntitySpawnList(@Nullable Level level, RandomSource random, BlockPos pos, SimpleWeightedRandomList<SpawnData> entitySpawnList) {
		this.spawnPotentials = entitySpawnList;
		this.setNextSpawnData(level, pos, this.spawnPotentials.getRandom(random).map(WeightedEntry.Wrapper::data).orElseGet(SpawnData::new));
		this.setChanged(level, pos);
		return this;
	}

	public BetweenlandsBaseSpawner setSpawnInAir(boolean spawnInAir) {
		this.spawnInAir = spawnInAir;
		return this;
	}

	public boolean canSpawnInAir() {
		return this.spawnInAir;
	}

	public BetweenlandsBaseSpawner setParticles(boolean hasParticles) {
		this.spawnParticles = hasParticles;
		return this;
	}

	public boolean hasParticles() {
		return this.spawnParticles;
	}

	public BetweenlandsBaseSpawner setMaxEntities(int maxEntities) {
		this.maxNearbyEntities = maxEntities;
		return this;
	}

	public BetweenlandsBaseSpawner setDelayRange(int minDelay, int maxDelay) {
		this.minSpawnDelay = minDelay;
		this.maxSpawnDelay = maxDelay;
		return this;
	}

	public BetweenlandsBaseSpawner setDelay(int delay) {
		this.spawnDelay = delay;
		return this;
	}

	public BetweenlandsBaseSpawner setSpawnRange(int range) {
		this.spawnRange = range;
		return this;
	}

	public BetweenlandsBaseSpawner setCheckRange(double range) {
		this.checkRange = range;
		return this;
	}

	public void setMaxSpawnCount(int count) {
		this.spawnCount = count;
	}

	protected void spawnParticles(Level level, BlockPos pos) {
		double rx = level.getRandom().nextFloat();
		double ry = level.getRandom().nextFloat();
		double rz = level.getRandom().nextFloat();
		double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
//				BLParticles.SPAWNER.spawn(len, (float) pos.getX() + rx, (float) pos.getY() + ry, (float) pos.getZ() + rz,
//					ParticleFactory.ParticleArgs.get().withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D));
	}

	//[VanillaCopy] of BaseSpawner.clientTick, edits noted
	@Override
	public void clientTick(Level level, BlockPos pos) {
		if (!this.isNearPlayer(level, pos)) {
			this.oSpin = this.spin;
		} else if (this.displayEntity != null) {
			//BL: make particles optional, use custom particles instead
			if (this.hasParticles()) {
				this.spawnParticles(level, pos);
			}
			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			}

			this.oSpin = this.spin;
			this.spin = (this.spin + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
		}
	}

	//[VanillaCopy] of BaseSpawner.serverTick, edits noted
	@Override
	public void serverTick(ServerLevel serverLevel, BlockPos pos) {
		if (this.isNearPlayer(serverLevel, pos)) {
			if (this.spawnDelay == -1) {
				this.delay(serverLevel, pos);
			}

			if (this.spawnDelay > 0) {
				this.spawnDelay--;
			} else {
				boolean flag = false;
				RandomSource randomsource = serverLevel.getRandom();
				SpawnData spawndata = this.getOrCreateNextSpawnData(serverLevel, randomsource, pos);

				for (int i = 0; i < this.spawnCount; i++) {
					CompoundTag compoundtag = spawndata.getEntityToSpawn();
					Optional<EntityType<?>> optional = EntityType.by(compoundtag);
					if (optional.isEmpty()) {
						this.delay(serverLevel, pos);
						return;
					}

					ListTag listtag = compoundtag.getList("Pos", 6);
					int j = listtag.size();
					double d0 = j >= 1
						? listtag.getDouble(0)
						: (double) pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5;
					double d1 = j >= 2 ? listtag.getDouble(1) : (double) (pos.getY() + randomsource.nextInt(3) - 1);
					double d2 = j >= 3
						? listtag.getDouble(2)
						: (double) pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.spawnRange + 0.5;
					if (serverLevel.noCollision(optional.get().getSpawnAABB(d0, d1, d2))) {
						BlockPos blockpos = BlockPos.containing(d0, d1, d2);
						if (spawndata.getCustomSpawnRules().isPresent()) {
							if (!optional.get().getCategory().isFriendly() && serverLevel.getDifficulty() == Difficulty.PEACEFUL) {
								continue;
							}

							SpawnData.CustomSpawnRules spawndata$customspawnrules = spawndata.getCustomSpawnRules().get();
							if (!spawndata$customspawnrules.isValidPosition(blockpos, serverLevel)) {
								continue;
							}
						} else if (!SpawnPlacements.checkSpawnRules(optional.get(), serverLevel, MobSpawnType.SPAWNER, blockpos, serverLevel.getRandom())) {
							continue;
						}

						Entity entity = EntityType.loadEntityRecursive(compoundtag, serverLevel, p_151310_ -> {
							p_151310_.moveTo(d0, d1, d2, p_151310_.getYRot(), p_151310_.getXRot());
							return p_151310_;
						});
						if (entity == null) {
							this.delay(serverLevel, pos);
							return;
						}

						//BL: use checkRange instead of spawnRange
						int k = serverLevel.getEntities(EntityTypeTest.forExactClass(entity.getClass()), new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(this.checkRange), EntitySelector.NO_SPECTATORS).size();
						if (k >= this.maxNearbyEntities) {
							this.delay(serverLevel, pos);
							return;
						}

						entity.moveTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
						//BL: add check for ground spawning and adjust position to find valid ground
						boolean canSpawn = this.canSpawnInAir() || entity.getBoundingBox().hasNaN();
						if (!canSpawn) {
							BlockPos down = entity.blockPosition().below();
							BlockState blockState = serverLevel.getBlockState(down);
							if (!blockState.isAir()) {
								AABB boundingBox = blockState.getCollisionShape(serverLevel, down).bounds();
								if (!boundingBox.hasNaN()) {
									boundingBox = boundingBox.move(down);
									AABB entityBoundingBox = entity.getBoundingBox();
									if (boundingBox.intersects(entityBoundingBox.minX, boundingBox.minY, entityBoundingBox.minZ, entityBoundingBox.maxX, boundingBox.maxY, entityBoundingBox.maxZ)) {
										Optional<Vec3> intercept = boundingBox.clip(entity.position(), entity.position().add(0, -2, 0));
										if (intercept.isPresent()) {
											canSpawn = true;
											entity.moveTo(entity.getX(), intercept.get().y + 0.1D, entity.getZ(), entity.getYRot(), entity.getXRot());
										}
									}
								}
							}
						}

						if (canSpawn) {
							if (entity instanceof Mob mob) {
								if (!EventHooks.checkSpawnPositionSpawner(mob, serverLevel, MobSpawnType.SPAWNER, spawndata, this)) {
									continue;
								}

								boolean flag1 = spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().contains("id", 8);
								EventHooks.finalizeMobSpawnSpawner(mob, serverLevel, serverLevel.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.SPAWNER, null, this, flag1);

								spawndata.getEquipment().ifPresent(mob::equip);
							}

							if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
								this.delay(serverLevel, pos);
								return;
							}

							serverLevel.levelEvent(2004, pos, 0);
							serverLevel.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
							if (entity instanceof Mob mob) {
								mob.spawnAnim();
							}

							flag = true;
						}
					}
				}

				if (flag) {
					this.delay(serverLevel, pos);
				}
			}
		}
	}

	public void setChanged(@Nullable Level level, BlockPos pos) {
		if (level != null && level.isClientSide()) {
			this.displayEntity = null;
		}
	}
}
