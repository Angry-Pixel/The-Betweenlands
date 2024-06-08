package net.minecraft.world.level;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobCategory;

public class LocalMobCapCalculator {
   private final Long2ObjectMap<List<ServerPlayer>> playersNearChunk = new Long2ObjectOpenHashMap<>();
   private final Map<ServerPlayer, LocalMobCapCalculator.MobCounts> playerMobCounts = Maps.newHashMap();
   private final ChunkMap chunkMap;

   public LocalMobCapCalculator(ChunkMap p_186501_) {
      this.chunkMap = p_186501_;
   }

   private List<ServerPlayer> getPlayersNear(ChunkPos p_186508_) {
      return this.playersNearChunk.computeIfAbsent(p_186508_.toLong(), (p_186511_) -> {
         return this.chunkMap.getPlayersCloseForSpawning(p_186508_);
      });
   }

   public void addMob(ChunkPos p_186513_, MobCategory p_186514_) {
      for(ServerPlayer serverplayer : this.getPlayersNear(p_186513_)) {
         this.playerMobCounts.computeIfAbsent(serverplayer, (p_186503_) -> {
            return new LocalMobCapCalculator.MobCounts();
         }).add(p_186514_);
      }

   }

   public boolean canSpawn(MobCategory p_186505_, ChunkPos p_186506_) {
      for(ServerPlayer serverplayer : this.getPlayersNear(p_186506_)) {
         LocalMobCapCalculator.MobCounts localmobcapcalculator$mobcounts = this.playerMobCounts.get(serverplayer);
         if (localmobcapcalculator$mobcounts == null || localmobcapcalculator$mobcounts.canSpawn(p_186505_)) {
            return true;
         }
      }

      return false;
   }

   static class MobCounts {
      private final Object2IntMap<MobCategory> counts = new Object2IntOpenHashMap<>(MobCategory.values().length);

      public void add(MobCategory p_186518_) {
         this.counts.computeInt(p_186518_, (p_186520_, p_186521_) -> {
            return p_186521_ == null ? 1 : p_186521_ + 1;
         });
      }

      public boolean canSpawn(MobCategory p_186523_) {
         return this.counts.getOrDefault(p_186523_, 0) < p_186523_.getMaxInstancesPerChunk();
      }
   }
}