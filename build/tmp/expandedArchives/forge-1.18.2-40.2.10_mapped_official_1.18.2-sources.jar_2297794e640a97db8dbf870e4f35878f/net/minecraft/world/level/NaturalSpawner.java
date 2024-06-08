package net.minecraft.world.level;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.NetherFortressFeature;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

// TODO: ForgeHooks.canEntitySpawn
public final class NaturalSpawner {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MIN_SPAWN_DISTANCE = 24;
   public static final int SPAWN_DISTANCE_CHUNK = 8;
   public static final int SPAWN_DISTANCE_BLOCK = 128;
   static final int MAGIC_NUMBER = (int)Math.pow(17.0D, 2.0D);
   private static final MobCategory[] SPAWNING_CATEGORIES = Stream.of(MobCategory.values()).filter((p_47037_) -> {
      return p_47037_ != MobCategory.MISC;
   }).toArray((p_46983_) -> {
      return new MobCategory[p_46983_];
   });

   private NaturalSpawner() {
   }

   public static NaturalSpawner.SpawnState createState(int p_186525_, Iterable<Entity> p_186526_, NaturalSpawner.ChunkGetter p_186527_, LocalMobCapCalculator p_186528_) {
      PotentialCalculator potentialcalculator = new PotentialCalculator();
      Object2IntOpenHashMap<MobCategory> object2intopenhashmap = new Object2IntOpenHashMap<>();
      Iterator iterator = p_186526_.iterator();

      while(true) {
         Entity entity;
         Mob mob;
         do {
            if (!iterator.hasNext()) {
               return new NaturalSpawner.SpawnState(p_186525_, object2intopenhashmap, potentialcalculator, p_186528_);
            }

            entity = (Entity)iterator.next();
            if (!(entity instanceof Mob)) {
               break;
            }

            mob = (Mob)entity;
         } while(mob.isPersistenceRequired() || mob.requiresCustomPersistence());

         MobCategory mobcategory = entity.getClassification(true);
         if (mobcategory != MobCategory.MISC) {
            Entity entity_f = entity;
            BlockPos blockpos = entity.blockPosition();
            p_186527_.query(ChunkPos.asLong(blockpos), (p_186541_) -> {
               MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = getRoughBiome(blockpos, p_186541_).getMobSettings().getMobSpawnCost(entity_f.getType());
               if (mobspawnsettings$mobspawncost != null) {
                  potentialcalculator.addCharge(entity_f.blockPosition(), mobspawnsettings$mobspawncost.getCharge());
               }

               if (entity_f instanceof Mob) {
                  p_186528_.addMob(p_186541_.getPos(), mobcategory);
               }

               object2intopenhashmap.addTo(mobcategory, 1);
            });
         }
      }
   }

   static Biome getRoughBiome(BlockPos p_47096_, ChunkAccess p_47097_) {
      return p_47097_.getNoiseBiome(QuartPos.fromBlock(p_47096_.getX()), QuartPos.fromBlock(p_47096_.getY()), QuartPos.fromBlock(p_47096_.getZ())).value();
   }

   public static void spawnForChunk(ServerLevel p_47030_, LevelChunk p_47031_, NaturalSpawner.SpawnState p_47032_, boolean p_47033_, boolean p_47034_, boolean p_47035_) {
      p_47030_.getProfiler().push("spawner");

      for(MobCategory mobcategory : SPAWNING_CATEGORIES) {
         if ((p_47033_ || !mobcategory.isFriendly()) && (p_47034_ || mobcategory.isFriendly()) && (p_47035_ || !mobcategory.isPersistent()) && p_47032_.canSpawnForCategory(mobcategory, p_47031_.getPos())) {
            spawnCategoryForChunk(mobcategory, p_47030_, p_47031_, p_47032_::canSpawn, p_47032_::afterSpawn);
         }
      }

      p_47030_.getProfiler().pop();
   }

   public static void spawnCategoryForChunk(MobCategory p_47046_, ServerLevel p_47047_, LevelChunk p_47048_, NaturalSpawner.SpawnPredicate p_47049_, NaturalSpawner.AfterSpawnCallback p_47050_) {
      BlockPos blockpos = getRandomPosWithin(p_47047_, p_47048_);
      if (blockpos.getY() >= p_47047_.getMinBuildHeight() + 1) {
         spawnCategoryForPosition(p_47046_, p_47047_, p_47048_, blockpos, p_47049_, p_47050_);
      }
   }

   @VisibleForDebug
   public static void spawnCategoryForPosition(MobCategory p_151613_, ServerLevel p_151614_, BlockPos p_151615_) {
      spawnCategoryForPosition(p_151613_, p_151614_, p_151614_.getChunk(p_151615_), p_151615_, (p_151606_, p_151607_, p_151608_) -> {
         return true;
      }, (p_151610_, p_151611_) -> {
      });
   }

   public static void spawnCategoryForPosition(MobCategory p_47039_, ServerLevel p_47040_, ChunkAccess p_47041_, BlockPos p_47042_, NaturalSpawner.SpawnPredicate p_47043_, NaturalSpawner.AfterSpawnCallback p_47044_) {
      StructureFeatureManager structurefeaturemanager = p_47040_.structureFeatureManager();
      ChunkGenerator chunkgenerator = p_47040_.getChunkSource().getGenerator();
      int i = p_47042_.getY();
      BlockState blockstate = p_47041_.getBlockState(p_47042_);
      if (!blockstate.isRedstoneConductor(p_47041_, p_47042_)) {
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
         int j = 0;

         for(int k = 0; k < 3; ++k) {
            int l = p_47042_.getX();
            int i1 = p_47042_.getZ();
            int j1 = 6;
            MobSpawnSettings.SpawnerData mobspawnsettings$spawnerdata = null;
            SpawnGroupData spawngroupdata = null;
            int k1 = Mth.ceil(p_47040_.random.nextFloat() * 4.0F);
            int l1 = 0;

            for(int i2 = 0; i2 < k1; ++i2) {
               l += p_47040_.random.nextInt(6) - p_47040_.random.nextInt(6);
               i1 += p_47040_.random.nextInt(6) - p_47040_.random.nextInt(6);
               blockpos$mutableblockpos.set(l, i, i1);
               double d0 = (double)l + 0.5D;
               double d1 = (double)i1 + 0.5D;
               Player player = p_47040_.getNearestPlayer(d0, (double)i, d1, -1.0D, false);
               if (player != null) {
                  double d2 = player.distanceToSqr(d0, (double)i, d1);
                  if (isRightDistanceToPlayerAndSpawnPoint(p_47040_, p_47041_, blockpos$mutableblockpos, d2)) {
                     if (mobspawnsettings$spawnerdata == null) {
                        Optional<MobSpawnSettings.SpawnerData> optional = getRandomSpawnMobAt(p_47040_, structurefeaturemanager, chunkgenerator, p_47039_, p_47040_.random, blockpos$mutableblockpos);
                        if (optional.isEmpty()) {
                           break;
                        }

                        mobspawnsettings$spawnerdata = optional.get();
                        k1 = mobspawnsettings$spawnerdata.minCount + p_47040_.random.nextInt(1 + mobspawnsettings$spawnerdata.maxCount - mobspawnsettings$spawnerdata.minCount);
                     }

                     if (isValidSpawnPostitionForType(p_47040_, p_47039_, structurefeaturemanager, chunkgenerator, mobspawnsettings$spawnerdata, blockpos$mutableblockpos, d2) && p_47043_.test(mobspawnsettings$spawnerdata.type, blockpos$mutableblockpos, p_47041_)) {
                        Mob mob = getMobForSpawn(p_47040_, mobspawnsettings$spawnerdata.type);
                        if (mob == null) {
                           return;
                        }

                        mob.moveTo(d0, (double)i, d1, p_47040_.random.nextFloat() * 360.0F, 0.0F);
                        int canSpawn = net.minecraftforge.common.ForgeHooks.canEntitySpawn(mob, p_47040_, d0, i, d1, null, MobSpawnType.NATURAL);
                        if (canSpawn != -1 && (canSpawn == 1 || isValidPositionForMob(p_47040_, mob, d2))) {
                           if (!net.minecraftforge.event.ForgeEventFactory.doSpecialSpawn(mob, (LevelAccessor) p_47040_, (float)d0, (float)i, (float)d1, null, MobSpawnType.NATURAL))
                           spawngroupdata = mob.finalizeSpawn(p_47040_, p_47040_.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.NATURAL, spawngroupdata, (CompoundTag)null);
                           ++j;
                           ++l1;
                           p_47040_.addFreshEntityWithPassengers(mob);
                           p_47044_.run(mob, p_47041_);
                           if (j >= net.minecraftforge.event.ForgeEventFactory.getMaxSpawnPackSize(mob)) {
                              return;
                           }

                           if (mob.isMaxGroupSizeReached(l1)) {
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }
   }

   private static boolean isRightDistanceToPlayerAndSpawnPoint(ServerLevel p_47025_, ChunkAccess p_47026_, BlockPos.MutableBlockPos p_47027_, double p_47028_) {
      if (p_47028_ <= 576.0D) {
         return false;
      } else if (p_47025_.getSharedSpawnPos().closerToCenterThan(new Vec3((double)p_47027_.getX() + 0.5D, (double)p_47027_.getY(), (double)p_47027_.getZ() + 0.5D), 24.0D)) {
         return false;
      } else {
         return Objects.equals(new ChunkPos(p_47027_), p_47026_.getPos()) || p_47025_.isNaturalSpawningAllowed(p_47027_);
      }
   }

   private static boolean isValidSpawnPostitionForType(ServerLevel p_46996_, MobCategory p_46997_, StructureFeatureManager p_46998_, ChunkGenerator p_46999_, MobSpawnSettings.SpawnerData p_47000_, BlockPos.MutableBlockPos p_47001_, double p_47002_) {
      EntityType<?> entitytype = p_47000_.type;
      if (entitytype.getCategory() == MobCategory.MISC) {
         return false;
      } else if (!entitytype.canSpawnFarFromPlayer() && p_47002_ > (double)(entitytype.getCategory().getDespawnDistance() * entitytype.getCategory().getDespawnDistance())) {
         return false;
      } else if (entitytype.canSummon() && canSpawnMobAt(p_46996_, p_46998_, p_46999_, p_46997_, p_47000_, p_47001_)) {
         SpawnPlacements.Type spawnplacements$type = SpawnPlacements.getPlacementType(entitytype);
         if (!isSpawnPositionOk(spawnplacements$type, p_46996_, p_47001_, entitytype)) {
            return false;
         } else if (!SpawnPlacements.checkSpawnRules(entitytype, p_46996_, MobSpawnType.NATURAL, p_47001_, p_46996_.random)) {
            return false;
         } else {
            return p_46996_.noCollision(entitytype.getAABB((double)p_47001_.getX() + 0.5D, (double)p_47001_.getY(), (double)p_47001_.getZ() + 0.5D));
         }
      } else {
         return false;
      }
   }

   @Nullable
   private static Mob getMobForSpawn(ServerLevel p_46989_, EntityType<?> p_46990_) {
      try {
         Entity entity = p_46990_.create(p_46989_);
         if (!(entity instanceof Mob)) {
            throw new IllegalStateException("Trying to spawn a non-mob: " + Registry.ENTITY_TYPE.getKey(p_46990_));
         } else {
            return (Mob)entity;
         }
      } catch (Exception exception) {
         LOGGER.warn("Failed to create mob", (Throwable)exception);
         return null;
      }
   }

   private static boolean isValidPositionForMob(ServerLevel p_46992_, Mob p_46993_, double p_46994_) {
      if (p_46994_ > (double)(p_46993_.getType().getCategory().getDespawnDistance() * p_46993_.getType().getCategory().getDespawnDistance()) && p_46993_.removeWhenFarAway(p_46994_)) {
         return false;
      } else {
         return p_46993_.checkSpawnRules(p_46992_, MobSpawnType.NATURAL) && p_46993_.checkSpawnObstruction(p_46992_);
      }
   }

   private static Optional<MobSpawnSettings.SpawnerData> getRandomSpawnMobAt(ServerLevel p_151599_, StructureFeatureManager p_151600_, ChunkGenerator p_151601_, MobCategory p_151602_, Random p_151603_, BlockPos p_151604_) {
      Holder<Biome> holder = p_151599_.getBiome(p_151604_);
      return p_151602_ == MobCategory.WATER_AMBIENT && Biome.getBiomeCategory(holder) == Biome.BiomeCategory.RIVER && p_151603_.nextFloat() < 0.98F ? Optional.empty() : mobsAt(p_151599_, p_151600_, p_151601_, p_151602_, p_151604_, holder).getRandom(p_151603_);
   }

   private static boolean canSpawnMobAt(ServerLevel p_47004_, StructureFeatureManager p_47005_, ChunkGenerator p_47006_, MobCategory p_47007_, MobSpawnSettings.SpawnerData p_47008_, BlockPos p_47009_) {
      return mobsAt(p_47004_, p_47005_, p_47006_, p_47007_, p_47009_, (Holder<Biome>)null).unwrap().contains(p_47008_);
   }

   private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(ServerLevel p_204169_, StructureFeatureManager p_204170_, ChunkGenerator p_204171_, MobCategory p_204172_, BlockPos p_204173_, @Nullable Holder<Biome> p_204174_) {
      return net.minecraftforge.event.ForgeEventFactory.getPotentialSpawns(p_204169_, p_204172_, p_204173_, isInNetherFortressBounds(p_204173_, p_204169_, p_204172_, p_204170_) ? NetherFortressFeature.FORTRESS_ENEMIES : p_204171_.getMobsAt(p_204174_ != null ? p_204174_ : p_204169_.getBiome(p_204173_), p_204170_, p_204172_, p_204173_));
   }

   public static boolean isInNetherFortressBounds(BlockPos p_186530_, ServerLevel p_186531_, MobCategory p_186532_, StructureFeatureManager p_186533_) {
      if (p_186532_ == MobCategory.MONSTER && p_186531_.getBlockState(p_186530_.below()).is(Blocks.NETHER_BRICKS)) {
         ConfiguredStructureFeature<?, ?> configuredstructurefeature = p_186533_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY).get(BuiltinStructures.FORTRESS);
         return configuredstructurefeature == null ? false : p_186533_.getStructureAt(p_186530_, configuredstructurefeature).isValid();
      } else {
         return false;
      }
   }

   private static BlockPos getRandomPosWithin(Level p_47063_, LevelChunk p_47064_) {
      ChunkPos chunkpos = p_47064_.getPos();
      int i = chunkpos.getMinBlockX() + p_47063_.random.nextInt(16);
      int j = chunkpos.getMinBlockZ() + p_47063_.random.nextInt(16);
      int k = p_47064_.getHeight(Heightmap.Types.WORLD_SURFACE, i, j) + 1;
      int l = Mth.randomBetweenInclusive(p_47063_.random, p_47063_.getMinBuildHeight(), k);
      return new BlockPos(i, l, j);
   }

   public static boolean isValidEmptySpawnBlock(BlockGetter p_47057_, BlockPos p_47058_, BlockState p_47059_, FluidState p_47060_, EntityType<?> p_47061_) {
      if (p_47059_.isCollisionShapeFullBlock(p_47057_, p_47058_)) {
         return false;
      } else if (p_47059_.isSignalSource()) {
         return false;
      } else if (!p_47060_.isEmpty()) {
         return false;
      } else if (p_47059_.is(BlockTags.PREVENT_MOB_SPAWNING_INSIDE)) {
         return false;
      } else {
         return !p_47061_.isBlockDangerous(p_47059_);
      }
   }

   public static boolean isSpawnPositionOk(SpawnPlacements.Type p_47052_, LevelReader p_47053_, BlockPos p_47054_, @Nullable EntityType<?> p_47055_) {
      if (p_47052_ == SpawnPlacements.Type.NO_RESTRICTIONS) {
         return true;
      } else if (p_47055_ != null && p_47053_.getWorldBorder().isWithinBounds(p_47054_)) {
         return p_47052_.canSpawnAt(p_47053_, p_47054_, p_47055_);
      }
      return false;
   }

   public static boolean canSpawnAtBody(SpawnPlacements.Type p_47052_, LevelReader p_47053_, BlockPos p_47054_, @Nullable EntityType<?> p_47055_) {
      {
         BlockState blockstate = p_47053_.getBlockState(p_47054_);
         FluidState fluidstate = p_47053_.getFluidState(p_47054_);
         BlockPos blockpos = p_47054_.above();
         BlockPos blockpos1 = p_47054_.below();
         switch(p_47052_) {
         case IN_WATER:
            return fluidstate.is(FluidTags.WATER) && !p_47053_.getBlockState(blockpos).isRedstoneConductor(p_47053_, blockpos);
         case IN_LAVA:
            return fluidstate.is(FluidTags.LAVA);
         case ON_GROUND:
         default:
            BlockState blockstate1 = p_47053_.getBlockState(blockpos1);
            if (!blockstate1.isValidSpawn(p_47053_, blockpos1, p_47052_, p_47055_)) {
               return false;
            } else {
               return isValidEmptySpawnBlock(p_47053_, p_47054_, blockstate, fluidstate, p_47055_) && isValidEmptySpawnBlock(p_47053_, blockpos, p_47053_.getBlockState(blockpos), p_47053_.getFluidState(blockpos), p_47055_);
            }
         }
      }
   }

   public static void spawnMobsForChunkGeneration(ServerLevelAccessor p_204176_, Holder<Biome> p_204177_, ChunkPos p_204178_, Random p_204179_) {
      MobSpawnSettings mobspawnsettings = p_204177_.value().getMobSettings();
      WeightedRandomList<MobSpawnSettings.SpawnerData> weightedrandomlist = mobspawnsettings.getMobs(MobCategory.CREATURE);
      if (!weightedrandomlist.isEmpty()) {
         int i = p_204178_.getMinBlockX();
         int j = p_204178_.getMinBlockZ();

         while(p_204179_.nextFloat() < mobspawnsettings.getCreatureProbability()) {
            Optional<MobSpawnSettings.SpawnerData> optional = weightedrandomlist.getRandom(p_204179_);
            if (optional.isPresent()) {
               MobSpawnSettings.SpawnerData mobspawnsettings$spawnerdata = optional.get();
               int k = mobspawnsettings$spawnerdata.minCount + p_204179_.nextInt(1 + mobspawnsettings$spawnerdata.maxCount - mobspawnsettings$spawnerdata.minCount);
               SpawnGroupData spawngroupdata = null;
               int l = i + p_204179_.nextInt(16);
               int i1 = j + p_204179_.nextInt(16);
               int j1 = l;
               int k1 = i1;

               for(int l1 = 0; l1 < k; ++l1) {
                  boolean flag = false;

                  for(int i2 = 0; !flag && i2 < 4; ++i2) {
                     BlockPos blockpos = getTopNonCollidingPos(p_204176_, mobspawnsettings$spawnerdata.type, l, i1);
                     if (mobspawnsettings$spawnerdata.type.canSummon() && isSpawnPositionOk(SpawnPlacements.getPlacementType(mobspawnsettings$spawnerdata.type), p_204176_, blockpos, mobspawnsettings$spawnerdata.type)) {
                        float f = mobspawnsettings$spawnerdata.type.getWidth();
                        double d0 = Mth.clamp((double)l, (double)i + (double)f, (double)i + 16.0D - (double)f);
                        double d1 = Mth.clamp((double)i1, (double)j + (double)f, (double)j + 16.0D - (double)f);
                        if (!p_204176_.noCollision(mobspawnsettings$spawnerdata.type.getAABB(d0, (double)blockpos.getY(), d1)) || !SpawnPlacements.checkSpawnRules(mobspawnsettings$spawnerdata.type, p_204176_, MobSpawnType.CHUNK_GENERATION, new BlockPos(d0, (double)blockpos.getY(), d1), p_204176_.getRandom())) {
                           continue;
                        }

                        Entity entity;
                        try {
                           entity = mobspawnsettings$spawnerdata.type.create(p_204176_.getLevel());
                        } catch (Exception exception) {
                           LOGGER.warn("Failed to create mob", (Throwable)exception);
                           continue;
                        }

                        entity.moveTo(d0, (double)blockpos.getY(), d1, p_204179_.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof Mob) {
                           Mob mob = (Mob)entity;
                           if (net.minecraftforge.common.ForgeHooks.canEntitySpawn(mob, p_204176_, d0, blockpos.getY(), d1, null, MobSpawnType.CHUNK_GENERATION) == -1) continue;
                           if (mob.checkSpawnRules(p_204176_, MobSpawnType.CHUNK_GENERATION) && mob.checkSpawnObstruction(p_204176_)) {
                              spawngroupdata = mob.finalizeSpawn(p_204176_, p_204176_.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.CHUNK_GENERATION, spawngroupdata, (CompoundTag)null);
                              p_204176_.addFreshEntityWithPassengers(mob);
                              flag = true;
                           }
                        }
                     }

                     l += p_204179_.nextInt(5) - p_204179_.nextInt(5);

                     for(i1 += p_204179_.nextInt(5) - p_204179_.nextInt(5); l < i || l >= i + 16 || i1 < j || i1 >= j + 16; i1 = k1 + p_204179_.nextInt(5) - p_204179_.nextInt(5)) {
                        l = j1 + p_204179_.nextInt(5) - p_204179_.nextInt(5);
                     }
                  }
               }
            }
         }

      }
   }

   private static BlockPos getTopNonCollidingPos(LevelReader p_47066_, EntityType<?> p_47067_, int p_47068_, int p_47069_) {
      int i = p_47066_.getHeight(SpawnPlacements.getHeightmapType(p_47067_), p_47068_, p_47069_);
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_47068_, i, p_47069_);
      if (p_47066_.dimensionType().hasCeiling()) {
         do {
            blockpos$mutableblockpos.move(Direction.DOWN);
         } while(!p_47066_.getBlockState(blockpos$mutableblockpos).isAir());

         do {
            blockpos$mutableblockpos.move(Direction.DOWN);
         } while(p_47066_.getBlockState(blockpos$mutableblockpos).isAir() && blockpos$mutableblockpos.getY() > p_47066_.getMinBuildHeight());
      }

      if (SpawnPlacements.getPlacementType(p_47067_) == SpawnPlacements.Type.ON_GROUND) {
         BlockPos blockpos = blockpos$mutableblockpos.below();
         if (p_47066_.getBlockState(blockpos).isPathfindable(p_47066_, blockpos, PathComputationType.LAND)) {
            return blockpos;
         }
      }

      return blockpos$mutableblockpos.immutable();
   }

   @FunctionalInterface
   public interface AfterSpawnCallback {
      void run(Mob p_47101_, ChunkAccess p_47102_);
   }

   @FunctionalInterface
   public interface ChunkGetter {
      void query(long p_47104_, Consumer<LevelChunk> p_47105_);
   }

   @FunctionalInterface
   public interface SpawnPredicate {
      boolean test(EntityType<?> p_47107_, BlockPos p_47108_, ChunkAccess p_47109_);
   }

   public static class SpawnState {
      private final int spawnableChunkCount;
      private final Object2IntOpenHashMap<MobCategory> mobCategoryCounts;
      private final PotentialCalculator spawnPotential;
      private final Object2IntMap<MobCategory> unmodifiableMobCategoryCounts;
      private final LocalMobCapCalculator localMobCapCalculator;
      @Nullable
      private BlockPos lastCheckedPos;
      @Nullable
      private EntityType<?> lastCheckedType;
      private double lastCharge;

      SpawnState(int p_186544_, Object2IntOpenHashMap<MobCategory> p_186545_, PotentialCalculator p_186546_, LocalMobCapCalculator p_186547_) {
         this.spawnableChunkCount = p_186544_;
         this.mobCategoryCounts = p_186545_;
         this.spawnPotential = p_186546_;
         this.localMobCapCalculator = p_186547_;
         this.unmodifiableMobCategoryCounts = Object2IntMaps.unmodifiable(p_186545_);
      }

      private boolean canSpawn(EntityType<?> p_47128_, BlockPos p_47129_, ChunkAccess p_47130_) {
         this.lastCheckedPos = p_47129_;
         this.lastCheckedType = p_47128_;
         MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = NaturalSpawner.getRoughBiome(p_47129_, p_47130_).getMobSettings().getMobSpawnCost(p_47128_);
         if (mobspawnsettings$mobspawncost == null) {
            this.lastCharge = 0.0D;
            return true;
         } else {
            double d0 = mobspawnsettings$mobspawncost.getCharge();
            this.lastCharge = d0;
            double d1 = this.spawnPotential.getPotentialEnergyChange(p_47129_, d0);
            return d1 <= mobspawnsettings$mobspawncost.getEnergyBudget();
         }
      }

      private void afterSpawn(Mob p_47132_, ChunkAccess p_47133_) {
         EntityType<?> entitytype = p_47132_.getType();
         BlockPos blockpos = p_47132_.blockPosition();
         double d0;
         if (blockpos.equals(this.lastCheckedPos) && entitytype == this.lastCheckedType) {
            d0 = this.lastCharge;
         } else {
            MobSpawnSettings.MobSpawnCost mobspawnsettings$mobspawncost = NaturalSpawner.getRoughBiome(blockpos, p_47133_).getMobSettings().getMobSpawnCost(entitytype);
            if (mobspawnsettings$mobspawncost != null) {
               d0 = mobspawnsettings$mobspawncost.getCharge();
            } else {
               d0 = 0.0D;
            }
         }

         this.spawnPotential.addCharge(blockpos, d0);
         MobCategory mobcategory = entitytype.getCategory();
         this.mobCategoryCounts.addTo(mobcategory, 1);
         this.localMobCapCalculator.addMob(new ChunkPos(blockpos), mobcategory);
      }

      public int getSpawnableChunkCount() {
         return this.spawnableChunkCount;
      }

      public Object2IntMap<MobCategory> getMobCategoryCounts() {
         return this.unmodifiableMobCategoryCounts;
      }

      boolean canSpawnForCategory(MobCategory p_186549_, ChunkPos p_186550_) {
         int i = p_186549_.getMaxInstancesPerChunk() * this.spawnableChunkCount / NaturalSpawner.MAGIC_NUMBER;
         if (this.mobCategoryCounts.getInt(p_186549_) >= i) {
            return false;
         } else {
            return this.localMobCapCalculator.canSpawn(p_186549_, p_186550_);
         }
      }
   }
}
