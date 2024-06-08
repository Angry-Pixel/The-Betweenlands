package net.minecraft.world.level.chunk.storage;

import java.util.concurrent.CompletableFuture;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.world.level.ChunkPos;

public interface ChunkScanAccess {
   CompletableFuture<Void> scanChunk(ChunkPos p_196888_, StreamTagVisitor p_196889_);
}