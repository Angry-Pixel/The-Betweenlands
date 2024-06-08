package net.minecraft.world.level.chunk.storage;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Codec;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.LegacyStructureDataHandler;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class ChunkStorage implements AutoCloseable {
   public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
   private final IOWorker worker;
   protected final DataFixer fixerUpper;
   @Nullable
   private LegacyStructureDataHandler legacyStructureHandler;

   public ChunkStorage(Path p_196912_, DataFixer p_196913_, boolean p_196914_) {
      this.fixerUpper = p_196913_;
      this.worker = new IOWorker(p_196912_, p_196914_, "chunk");
   }

   public CompoundTag upgradeChunkTag(ResourceKey<Level> p_188289_, Supplier<DimensionDataStorage> p_188290_, CompoundTag p_188291_, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> p_188292_) {
      int i = getVersion(p_188291_);
      if (i < 1493) {
         p_188291_ = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, p_188291_, i, 1493);
         if (p_188291_.getCompound("Level").getBoolean("hasLegacyStructureData")) {
            if (this.legacyStructureHandler == null) {
               this.legacyStructureHandler = LegacyStructureDataHandler.getLegacyStructureHandler(p_188289_, p_188290_.get());
            }

            p_188291_ = this.legacyStructureHandler.updateFromLegacy(p_188291_);
         }
      }

      injectDatafixingContext(p_188291_, p_188289_, p_188292_);
      p_188291_ = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, p_188291_, Math.max(1493, i));
      if (i < SharedConstants.getCurrentVersion().getWorldVersion()) {
         p_188291_.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());
      }

      p_188291_.remove("__context");
      return p_188291_;
   }

   public static void injectDatafixingContext(CompoundTag p_196919_, ResourceKey<Level> p_196920_, Optional<ResourceKey<Codec<? extends ChunkGenerator>>> p_196921_) {
      CompoundTag compoundtag = new CompoundTag();
      compoundtag.putString("dimension", p_196920_.location().toString());
      p_196921_.ifPresent((p_196917_) -> {
         compoundtag.putString("generator", p_196917_.location().toString());
      });
      p_196919_.put("__context", compoundtag);
   }

   public static int getVersion(CompoundTag p_63506_) {
      return p_63506_.contains("DataVersion", 99) ? p_63506_.getInt("DataVersion") : -1;
   }

   @Nullable
   public CompoundTag read(ChunkPos p_63513_) throws IOException {
      return this.worker.load(p_63513_);
   }

   public void write(ChunkPos p_63503_, CompoundTag p_63504_) {
      this.worker.store(p_63503_, p_63504_);
      if (this.legacyStructureHandler != null) {
         this.legacyStructureHandler.removeIndex(p_63503_.toLong());
      }

   }

   public void flushWorker() {
      this.worker.synchronize(true).join();
   }

   public void close() throws IOException {
      this.worker.close();
   }

   public ChunkScanAccess chunkScanner() {
      return this.worker;
   }
}