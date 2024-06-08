package net.minecraft.world.entity.npc;

import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.horse.TraderLlama;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;

public class WanderingTraderSpawner implements CustomSpawner {
   private static final int DEFAULT_TICK_DELAY = 1200;
   public static final int DEFAULT_SPAWN_DELAY = 24000;
   private static final int MIN_SPAWN_CHANCE = 25;
   private static final int MAX_SPAWN_CHANCE = 75;
   private static final int SPAWN_CHANCE_INCREASE = 25;
   private static final int SPAWN_ONE_IN_X_CHANCE = 10;
   private static final int NUMBER_OF_SPAWN_ATTEMPTS = 10;
   private final Random random = new Random();
   private final ServerLevelData serverLevelData;
   private int tickDelay;
   private int spawnDelay;
   private int spawnChance;

   public WanderingTraderSpawner(ServerLevelData p_35914_) {
      this.serverLevelData = p_35914_;
      this.tickDelay = 1200;
      this.spawnDelay = p_35914_.getWanderingTraderSpawnDelay();
      this.spawnChance = p_35914_.getWanderingTraderSpawnChance();
      if (this.spawnDelay == 0 && this.spawnChance == 0) {
         this.spawnDelay = 24000;
         p_35914_.setWanderingTraderSpawnDelay(this.spawnDelay);
         this.spawnChance = 25;
         p_35914_.setWanderingTraderSpawnChance(this.spawnChance);
      }

   }

   public int tick(ServerLevel p_35922_, boolean p_35923_, boolean p_35924_) {
      if (!p_35922_.getGameRules().getBoolean(GameRules.RULE_DO_TRADER_SPAWNING)) {
         return 0;
      } else if (--this.tickDelay > 0) {
         return 0;
      } else {
         this.tickDelay = 1200;
         this.spawnDelay -= 1200;
         this.serverLevelData.setWanderingTraderSpawnDelay(this.spawnDelay);
         if (this.spawnDelay > 0) {
            return 0;
         } else {
            this.spawnDelay = 24000;
            if (!p_35922_.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
               return 0;
            } else {
               int i = this.spawnChance;
               this.spawnChance = Mth.clamp(this.spawnChance + 25, 25, 75);
               this.serverLevelData.setWanderingTraderSpawnChance(this.spawnChance);
               if (this.random.nextInt(100) > i) {
                  return 0;
               } else if (this.spawn(p_35922_)) {
                  this.spawnChance = 25;
                  return 1;
               } else {
                  return 0;
               }
            }
         }
      }
   }

   private boolean spawn(ServerLevel p_35916_) {
      Player player = p_35916_.getRandomPlayer();
      if (player == null) {
         return true;
      } else if (this.random.nextInt(10) != 0) {
         return false;
      } else {
         BlockPos blockpos = player.blockPosition();
         int i = 48;
         PoiManager poimanager = p_35916_.getPoiManager();
         Optional<BlockPos> optional = poimanager.find(PoiType.MEETING.getPredicate(), (p_35933_) -> {
            return true;
         }, blockpos, 48, PoiManager.Occupancy.ANY);
         BlockPos blockpos1 = optional.orElse(blockpos);
         BlockPos blockpos2 = this.findSpawnPositionNear(p_35916_, blockpos1, 48);
         if (blockpos2 != null && this.hasEnoughSpace(p_35916_, blockpos2)) {
            if (p_35916_.getBiome(blockpos2).is(Biomes.THE_VOID)) {
               return false;
            }

            WanderingTrader wanderingtrader = EntityType.WANDERING_TRADER.spawn(p_35916_, (CompoundTag)null, (Component)null, (Player)null, blockpos2, MobSpawnType.EVENT, false, false);
            if (wanderingtrader != null) {
               for(int j = 0; j < 2; ++j) {
                  this.tryToSpawnLlamaFor(p_35916_, wanderingtrader, 4);
               }

               this.serverLevelData.setWanderingTraderId(wanderingtrader.getUUID());
               wanderingtrader.setDespawnDelay(48000);
               wanderingtrader.setWanderTarget(blockpos1);
               wanderingtrader.restrictTo(blockpos1, 16);
               return true;
            }
         }

         return false;
      }
   }

   private void tryToSpawnLlamaFor(ServerLevel p_35918_, WanderingTrader p_35919_, int p_35920_) {
      BlockPos blockpos = this.findSpawnPositionNear(p_35918_, p_35919_.blockPosition(), p_35920_);
      if (blockpos != null) {
         TraderLlama traderllama = EntityType.TRADER_LLAMA.spawn(p_35918_, (CompoundTag)null, (Component)null, (Player)null, blockpos, MobSpawnType.EVENT, false, false);
         if (traderllama != null) {
            traderllama.setLeashedTo(p_35919_, true);
         }
      }
   }

   @Nullable
   private BlockPos findSpawnPositionNear(LevelReader p_35929_, BlockPos p_35930_, int p_35931_) {
      BlockPos blockpos = null;

      for(int i = 0; i < 10; ++i) {
         int j = p_35930_.getX() + this.random.nextInt(p_35931_ * 2) - p_35931_;
         int k = p_35930_.getZ() + this.random.nextInt(p_35931_ * 2) - p_35931_;
         int l = p_35929_.getHeight(Heightmap.Types.WORLD_SURFACE, j, k);
         BlockPos blockpos1 = new BlockPos(j, l, k);
         if (NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, p_35929_, blockpos1, EntityType.WANDERING_TRADER)) {
            blockpos = blockpos1;
            break;
         }
      }

      return blockpos;
   }

   private boolean hasEnoughSpace(BlockGetter p_35926_, BlockPos p_35927_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(p_35927_, p_35927_.offset(1, 2, 1))) {
         if (!p_35926_.getBlockState(blockpos).getCollisionShape(p_35926_, blockpos).isEmpty()) {
            return false;
         }
      }

      return true;
   }
}