package net.minecraft.server.level.progress;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class StoringChunkProgressListener implements ChunkProgressListener {
   private final LoggerChunkProgressListener delegate;
   private final Long2ObjectOpenHashMap<ChunkStatus> statuses;
   private ChunkPos spawnPos = new ChunkPos(0, 0);
   private final int fullDiameter;
   private final int radius;
   private final int diameter;
   private boolean started;

   public StoringChunkProgressListener(int p_9661_) {
      this.delegate = new LoggerChunkProgressListener(p_9661_);
      this.fullDiameter = p_9661_ * 2 + 1;
      this.radius = p_9661_ + ChunkStatus.maxDistance();
      this.diameter = this.radius * 2 + 1;
      this.statuses = new Long2ObjectOpenHashMap<>();
   }

   public void updateSpawnPos(ChunkPos p_9667_) {
      if (this.started) {
         this.delegate.updateSpawnPos(p_9667_);
         this.spawnPos = p_9667_;
      }
   }

   public void onStatusChange(ChunkPos p_9669_, @Nullable ChunkStatus p_9670_) {
      if (this.started) {
         this.delegate.onStatusChange(p_9669_, p_9670_);
         if (p_9670_ == null) {
            this.statuses.remove(p_9669_.toLong());
         } else {
            this.statuses.put(p_9669_.toLong(), p_9670_);
         }

      }
   }

   public void start() {
      this.started = true;
      this.statuses.clear();
      this.delegate.start();
   }

   public void stop() {
      this.started = false;
      this.delegate.stop();
   }

   public int getFullDiameter() {
      return this.fullDiameter;
   }

   public int getDiameter() {
      return this.diameter;
   }

   public int getProgress() {
      return this.delegate.getProgress();
   }

   @Nullable
   public ChunkStatus getStatus(int p_9664_, int p_9665_) {
      return this.statuses.get(ChunkPos.asLong(p_9664_ + this.spawnPos.x - this.radius, p_9665_ + this.spawnPos.z - this.radius));
   }
}