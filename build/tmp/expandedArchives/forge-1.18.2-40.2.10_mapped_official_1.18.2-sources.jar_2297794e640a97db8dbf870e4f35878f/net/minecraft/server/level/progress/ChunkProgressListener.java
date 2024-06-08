package net.minecraft.server.level.progress;

import javax.annotation.Nullable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public interface ChunkProgressListener {
   void updateSpawnPos(ChunkPos p_9617_);

   void onStatusChange(ChunkPos p_9618_, @Nullable ChunkStatus p_9619_);

   void start();

   void stop();
}