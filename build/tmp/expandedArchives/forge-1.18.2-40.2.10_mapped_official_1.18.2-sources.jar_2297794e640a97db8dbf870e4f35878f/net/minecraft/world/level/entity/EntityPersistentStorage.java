package net.minecraft.world.level.entity;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.world.level.ChunkPos;

public interface EntityPersistentStorage<T> extends AutoCloseable {
   CompletableFuture<ChunkEntities<T>> loadEntities(ChunkPos p_156824_);

   void storeEntities(ChunkEntities<T> p_156825_);

   void flush(boolean p_182503_);

   default void close() throws IOException {
   }
}