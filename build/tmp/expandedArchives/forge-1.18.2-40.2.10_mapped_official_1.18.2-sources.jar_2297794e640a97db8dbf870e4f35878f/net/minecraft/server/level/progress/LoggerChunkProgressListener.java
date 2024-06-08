package net.minecraft.server.level.progress;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.slf4j.Logger;

public class LoggerChunkProgressListener implements ChunkProgressListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final int maxCount;
   private int count;
   private long startTime;
   private long nextTickTime = Long.MAX_VALUE;

   public LoggerChunkProgressListener(int p_9629_) {
      int i = p_9629_ * 2 + 1;
      this.maxCount = i * i;
   }

   public void updateSpawnPos(ChunkPos p_9631_) {
      this.nextTickTime = Util.getMillis();
      this.startTime = this.nextTickTime;
   }

   public void onStatusChange(ChunkPos p_9633_, @Nullable ChunkStatus p_9634_) {
      if (p_9634_ == ChunkStatus.FULL) {
         ++this.count;
      }

      int i = this.getProgress();
      if (Util.getMillis() > this.nextTickTime) {
         this.nextTickTime += 500L;
         LOGGER.info((new TranslatableComponent("menu.preparingSpawn", Mth.clamp(i, 0, 100))).getString());
      }

   }

   public void start() {
   }

   public void stop() {
      LOGGER.info("Time elapsed: {} ms", (long)(Util.getMillis() - this.startTime));
      this.nextTickTime = Long.MAX_VALUE;
   }

   public int getProgress() {
      return Mth.floor((float)this.count * 100.0F / (float)this.maxCount);
   }
}