package net.minecraft.world.entity.npc;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.phys.AABB;

public class CatSpawner implements CustomSpawner {
   private static final int TICK_DELAY = 1200;
   private int nextTick;

   public int tick(ServerLevel p_35330_, boolean p_35331_, boolean p_35332_) {
      if (p_35332_ && p_35330_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         --this.nextTick;
         if (this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick = 1200;
            Player player = p_35330_.getRandomPlayer();
            if (player == null) {
               return 0;
            } else {
               Random random = p_35330_.random;
               int i = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               int j = (8 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
               BlockPos blockpos = player.blockPosition().offset(i, 0, j);
               int k = 10;
               if (!p_35330_.hasChunksAt(blockpos.getX() - 10, blockpos.getZ() - 10, blockpos.getX() + 10, blockpos.getZ() + 10)) {
                  return 0;
               } else {
                  if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, p_35330_, blockpos, EntityType.CAT)) {
                     if (p_35330_.isCloseToVillage(blockpos, 2)) {
                        return this.spawnInVillage(p_35330_, blockpos);
                     }

                     Registry<ConfiguredStructureFeature<?, ?>> registry = p_35330_.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
                     if (ChunkGenerator.allConfigurations(registry, StructureFeature.SWAMP_HUT).anyMatch((p_207764_) -> {
                        return p_35330_.structureFeatureManager().getStructureWithPieceAt(blockpos, p_207764_).isValid();
                     })) {
                        return this.spawnInHut(p_35330_, blockpos);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int spawnInVillage(ServerLevel p_35327_, BlockPos p_35328_) {
      int i = 48;
      if (p_35327_.getPoiManager().getCountInRange(PoiType.HOME.getPredicate(), p_35328_, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L) {
         List<Cat> list = p_35327_.getEntitiesOfClass(Cat.class, (new AABB(p_35328_)).inflate(48.0D, 8.0D, 48.0D));
         if (list.size() < 5) {
            return this.spawnCat(p_35328_, p_35327_);
         }
      }

      return 0;
   }

   private int spawnInHut(ServerLevel p_35337_, BlockPos p_35338_) {
      int i = 16;
      List<Cat> list = p_35337_.getEntitiesOfClass(Cat.class, (new AABB(p_35338_)).inflate(16.0D, 8.0D, 16.0D));
      return list.size() < 1 ? this.spawnCat(p_35338_, p_35337_) : 0;
   }

   private int spawnCat(BlockPos p_35334_, ServerLevel p_35335_) {
      Cat cat = EntityType.CAT.create(p_35335_);
      if (cat == null) {
         return 0;
      } else {
         cat.moveTo(p_35334_, 0.0F, 0.0F); // Fix MC-147659: Some witch huts spawn the incorrect cat
         if(net.minecraftforge.common.ForgeHooks.canEntitySpawn(cat, p_35335_, p_35334_.getX(), p_35334_.getY(), p_35334_.getZ(), null, MobSpawnType.NATURAL) == -1) return 0;
         cat.finalizeSpawn(p_35335_, p_35335_.getCurrentDifficultyAt(p_35334_), MobSpawnType.NATURAL, (SpawnGroupData)null, (CompoundTag)null);
         p_35335_.addFreshEntityWithPassengers(cat);
         return 1;
      }
   }
}
