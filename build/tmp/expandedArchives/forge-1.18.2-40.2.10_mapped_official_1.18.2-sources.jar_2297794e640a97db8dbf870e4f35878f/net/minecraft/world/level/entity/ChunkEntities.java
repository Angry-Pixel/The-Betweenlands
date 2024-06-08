package net.minecraft.world.level.entity;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.world.level.ChunkPos;

public class ChunkEntities<T> {
   private final ChunkPos pos;
   private final List<T> entities;

   public ChunkEntities(ChunkPos p_156789_, List<T> p_156790_) {
      this.pos = p_156789_;
      this.entities = p_156790_;
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public Stream<T> getEntities() {
      return this.entities.stream();
   }

   public boolean isEmpty() {
      return this.entities.isEmpty();
   }
}