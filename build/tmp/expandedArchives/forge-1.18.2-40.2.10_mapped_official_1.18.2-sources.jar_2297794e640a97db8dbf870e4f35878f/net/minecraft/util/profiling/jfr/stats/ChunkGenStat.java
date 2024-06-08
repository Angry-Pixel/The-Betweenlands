package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public record ChunkGenStat(Duration duration, ChunkPos chunkPos, ColumnPos worldPos, ChunkStatus status, String level) implements TimedStat {
   public static ChunkGenStat from(RecordedEvent p_185605_) {
      return new ChunkGenStat(p_185605_.getDuration(), new ChunkPos(p_185605_.getInt("chunkPosX"), p_185605_.getInt("chunkPosX")), new ColumnPos(p_185605_.getInt("worldPosX"), p_185605_.getInt("worldPosZ")), ChunkStatus.byName(p_185605_.getString("status")), p_185605_.getString("level"));
   }

   public Duration duration() {
      return this.duration;
   }
}