package net.minecraft.server.level;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLightUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.util.DebugBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ChunkHolder {
   public static final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> UNLOADED_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
   public static final CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> UNLOADED_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_CHUNK);
   public static final Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> UNLOADED_LEVEL_CHUNK = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
   private static final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> NOT_DONE_YET = Either.right(ChunkHolder.ChunkLoadingFailure.UNLOADED);
   private static final CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> UNLOADED_LEVEL_CHUNK_FUTURE = CompletableFuture.completedFuture(UNLOADED_LEVEL_CHUNK);
   private static final List<ChunkStatus> CHUNK_STATUSES = ChunkStatus.getStatusList();
   private static final ChunkHolder.FullChunkStatus[] FULL_CHUNK_STATUSES = ChunkHolder.FullChunkStatus.values();
   private static final int BLOCKS_BEFORE_RESEND_FUDGE = 64;
   private final AtomicReferenceArray<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> futures = new AtomicReferenceArray<>(CHUNK_STATUSES.size());
   private final LevelHeightAccessor levelHeightAccessor;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private volatile CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
   private CompletableFuture<ChunkAccess> chunkToSave = CompletableFuture.completedFuture((ChunkAccess)null);
   @Nullable
   private final DebugBuffer<ChunkHolder.ChunkSaveDebug> chunkToSaveHistory = null;
   private int oldTicketLevel;
   private int ticketLevel;
   private int queueLevel;
   final ChunkPos pos;
   private boolean hasChangedSections;
   private final ShortSet[] changedBlocksPerSection;
   private final BitSet blockChangedLightSectionFilter = new BitSet();
   private final BitSet skyChangedLightSectionFilter = new BitSet();
   private final LevelLightEngine lightEngine;
   private final ChunkHolder.LevelChangeListener onLevelChange;
   private final ChunkHolder.PlayerProvider playerProvider;
   private boolean wasAccessibleSinceLastSave;
   private boolean resendLight;
   LevelChunk currentlyLoading; // Forge: Used to bypass future chain when loading chunks.
   private CompletableFuture<Void> pendingFullStateConfirmation = CompletableFuture.completedFuture((Void)null);

   public ChunkHolder(ChunkPos p_142986_, int p_142987_, LevelHeightAccessor p_142988_, LevelLightEngine p_142989_, ChunkHolder.LevelChangeListener p_142990_, ChunkHolder.PlayerProvider p_142991_) {
      this.pos = p_142986_;
      this.levelHeightAccessor = p_142988_;
      this.lightEngine = p_142989_;
      this.onLevelChange = p_142990_;
      this.playerProvider = p_142991_;
      this.oldTicketLevel = ChunkMap.MAX_CHUNK_DISTANCE + 1;
      this.ticketLevel = this.oldTicketLevel;
      this.queueLevel = this.oldTicketLevel;
      this.setTicketLevel(p_142987_);
      this.changedBlocksPerSection = new ShortSet[p_142988_.getSectionsCount()];
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getFutureIfPresentUnchecked(ChunkStatus p_140048_) {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.futures.get(p_140048_.getIndex());
      return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getFutureIfPresent(ChunkStatus p_140081_) {
      return getStatus(this.ticketLevel).isOrAfter(p_140081_) ? this.getFutureIfPresentUnchecked(p_140081_) : UNLOADED_CHUNK_FUTURE;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getTickingChunkFuture() {
      return this.tickingChunkFuture;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getEntityTickingChunkFuture() {
      return this.entityTickingChunkFuture;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> getFullChunkFuture() {
      return this.fullChunkFuture;
   }

   @Nullable
   public LevelChunk getTickingChunk() {
      CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getTickingChunkFuture();
      Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either = completablefuture.getNow((Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>)null);
      return either == null ? null : either.left().orElse((LevelChunk)null);
   }

   @Nullable
   public LevelChunk getFullChunk() {
      CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getFullChunkFuture();
      Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either = completablefuture.getNow((Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>)null);
      return either == null ? null : either.left().orElse((LevelChunk)null);
   }

   @Nullable
   public ChunkStatus getLastAvailableStatus() {
      for(int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
         if (completablefuture.getNow(UNLOADED_CHUNK).left().isPresent()) {
            return chunkstatus;
         }
      }

      return null;
   }

   @Nullable
   public ChunkAccess getLastAvailable() {
      for(int i = CHUNK_STATUSES.size() - 1; i >= 0; --i) {
         ChunkStatus chunkstatus = CHUNK_STATUSES.get(i);
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getFutureIfPresentUnchecked(chunkstatus);
         if (!completablefuture.isCompletedExceptionally()) {
            Optional<ChunkAccess> optional = completablefuture.getNow(UNLOADED_CHUNK).left();
            if (optional.isPresent()) {
               return optional.get();
            }
         }
      }

      return null;
   }

   public CompletableFuture<ChunkAccess> getChunkToSave() {
      return this.chunkToSave;
   }

   public void blockChanged(BlockPos p_140057_) {
      LevelChunk levelchunk = this.getTickingChunk();
      if (levelchunk != null) {
         int i = this.levelHeightAccessor.getSectionIndex(p_140057_.getY());
         if (this.changedBlocksPerSection[i] == null) {
            this.hasChangedSections = true;
            this.changedBlocksPerSection[i] = new ShortOpenHashSet();
         }

         this.changedBlocksPerSection[i].add(SectionPos.sectionRelativePos(p_140057_));
      }
   }

   public void sectionLightChanged(LightLayer p_140037_, int p_140038_) {
      LevelChunk levelchunk = this.getFullChunk();
      if (levelchunk != null) {
         levelchunk.setUnsaved(true);
         LevelChunk levelchunk1 = this.getTickingChunk();
         if (levelchunk1 != null) {
            int i = this.lightEngine.getMinLightSection();
            int j = this.lightEngine.getMaxLightSection();
            if (p_140038_ >= i && p_140038_ <= j) {
               int k = p_140038_ - i;
               if (p_140037_ == LightLayer.SKY) {
                  this.skyChangedLightSectionFilter.set(k);
               } else {
                  this.blockChangedLightSectionFilter.set(k);
               }

            }
         }
      }
   }

   public void broadcastChanges(LevelChunk p_140055_) {
      if (this.hasChangedSections || !this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
         Level level = p_140055_.getLevel();
         int i = 0;

         for(int j = 0; j < this.changedBlocksPerSection.length; ++j) {
            i += this.changedBlocksPerSection[j] != null ? this.changedBlocksPerSection[j].size() : 0;
         }

         this.resendLight |= i >= 64;
         if (!this.skyChangedLightSectionFilter.isEmpty() || !this.blockChangedLightSectionFilter.isEmpty()) {
            this.broadcast(new ClientboundLightUpdatePacket(p_140055_.getPos(), this.lightEngine, this.skyChangedLightSectionFilter, this.blockChangedLightSectionFilter, true), !this.resendLight);
            this.skyChangedLightSectionFilter.clear();
            this.blockChangedLightSectionFilter.clear();
         }

         for(int l = 0; l < this.changedBlocksPerSection.length; ++l) {
            ShortSet shortset = this.changedBlocksPerSection[l];
            if (shortset != null) {
               int k = this.levelHeightAccessor.getSectionYFromSectionIndex(l);
               SectionPos sectionpos = SectionPos.of(p_140055_.getPos(), k);
               if (shortset.size() == 1) {
                  BlockPos blockpos = sectionpos.relativeToBlockPos(shortset.iterator().nextShort());
                  BlockState blockstate = level.getBlockState(blockpos);
                  this.broadcast(new ClientboundBlockUpdatePacket(blockpos, blockstate), false);
                  this.broadcastBlockEntityIfNeeded(level, blockpos, blockstate);
               } else {
                  LevelChunkSection levelchunksection = p_140055_.getSection(l);
                  ClientboundSectionBlocksUpdatePacket clientboundsectionblocksupdatepacket = new ClientboundSectionBlocksUpdatePacket(sectionpos, shortset, levelchunksection, this.resendLight);
                  this.broadcast(clientboundsectionblocksupdatepacket, false);
                  clientboundsectionblocksupdatepacket.runUpdates((p_140078_, p_140079_) -> {
                     this.broadcastBlockEntityIfNeeded(level, p_140078_, p_140079_);
                  });
               }

               this.changedBlocksPerSection[l] = null;
            }
         }

         this.hasChangedSections = false;
      }
   }

   private void broadcastBlockEntityIfNeeded(Level p_140033_, BlockPos p_140034_, BlockState p_140035_) {
      if (p_140035_.hasBlockEntity()) {
         this.broadcastBlockEntity(p_140033_, p_140034_);
      }

   }

   private void broadcastBlockEntity(Level p_140030_, BlockPos p_140031_) {
      BlockEntity blockentity = p_140030_.getBlockEntity(p_140031_);
      if (blockentity != null) {
         Packet<?> packet = blockentity.getUpdatePacket();
         if (packet != null) {
            this.broadcast(packet, false);
         }
      }

   }

   private void broadcast(Packet<?> p_140064_, boolean p_140065_) {
      this.playerProvider.getPlayers(this.pos, p_140065_).forEach((p_140062_) -> {
         p_140062_.connection.send(p_140064_);
      });
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> getOrScheduleFuture(ChunkStatus p_140050_, ChunkMap p_140051_) {
      int i = p_140050_.getIndex();
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.futures.get(i);
      if (completablefuture != null) {
         Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = completablefuture.getNow(NOT_DONE_YET);
         if (either == null) {
            String s = "value in future for status: " + p_140050_ + " was incorrectly set to null at chunk: " + this.pos;
            throw p_140051_.debugFuturesAndCreateReportedException(new IllegalStateException("null value previously set for chunk status"), s);
         }

         if (either == NOT_DONE_YET || either.right().isEmpty()) {
            return completablefuture;
         }
      }

      if (getStatus(this.ticketLevel).isOrAfter(p_140050_)) {
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture1 = p_140051_.schedule(this, p_140050_);
         this.updateChunkToSave(completablefuture1, "schedule " + p_140050_);
         this.futures.set(i, completablefuture1);
         return completablefuture1;
      } else {
         return completablefuture == null ? UNLOADED_CHUNK_FUTURE : completablefuture;
      }
   }

   protected void addSaveDependency(String p_200417_, CompletableFuture<?> p_200418_) {
      if (this.chunkToSaveHistory != null) {
         this.chunkToSaveHistory.push(new ChunkHolder.ChunkSaveDebug(Thread.currentThread(), p_200418_, p_200417_));
      }

      this.chunkToSave = this.chunkToSave.thenCombine(p_200418_, (p_200414_, p_200415_) -> {
         return p_200414_;
      });
   }

   private void updateChunkToSave(CompletableFuture<? extends Either<? extends ChunkAccess, ChunkHolder.ChunkLoadingFailure>> p_143018_, String p_143019_) {
      if (this.chunkToSaveHistory != null) {
         this.chunkToSaveHistory.push(new ChunkHolder.ChunkSaveDebug(Thread.currentThread(), p_143018_, p_143019_));
      }

      this.chunkToSave = this.chunkToSave.thenCombine(p_143018_, (p_200411_, p_200412_) -> {
         return p_200412_.map((p_200406_) -> {
            return p_200406_;
         }, (p_200409_) -> {
            return p_200411_;
         });
      });
   }

   public ChunkHolder.FullChunkStatus getFullStatus() {
      return getFullChunkStatus(this.ticketLevel);
   }

   public ChunkPos getPos() {
      return this.pos;
   }

   public int getTicketLevel() {
      return this.ticketLevel;
   }

   public int getQueueLevel() {
      return this.queueLevel;
   }

   private void setQueueLevel(int p_140087_) {
      this.queueLevel = p_140087_;
   }

   public void setTicketLevel(int p_140028_) {
      this.ticketLevel = p_140028_;
   }

   private void scheduleFullChunkPromotion(ChunkMap p_142999_, CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> p_143000_, Executor p_143001_, ChunkHolder.FullChunkStatus p_143002_) {
      this.pendingFullStateConfirmation.cancel(false);
      CompletableFuture<Void> completablefuture = new CompletableFuture<>();
      completablefuture.thenRunAsync(() -> {
         p_142999_.onFullChunkStatusChange(this.pos, p_143002_);
      }, p_143001_);
      this.pendingFullStateConfirmation = completablefuture;
      p_143000_.thenAccept((p_200421_) -> {
         p_200421_.ifLeft((p_200424_) -> {
            completablefuture.complete((Void)null);
         });
      });
   }

   private void demoteFullChunk(ChunkMap p_142993_, ChunkHolder.FullChunkStatus p_142994_) {
      this.pendingFullStateConfirmation.cancel(false);
      p_142993_.onFullChunkStatusChange(this.pos, p_142994_);
   }

   protected void updateFutures(ChunkMap p_143004_, Executor p_143005_) {
      ChunkStatus chunkstatus = getStatus(this.oldTicketLevel);
      ChunkStatus chunkstatus1 = getStatus(this.ticketLevel);
      boolean flag = this.oldTicketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      boolean flag1 = this.ticketLevel <= ChunkMap.MAX_CHUNK_DISTANCE;
      ChunkHolder.FullChunkStatus chunkholder$fullchunkstatus = getFullChunkStatus(this.oldTicketLevel);
      ChunkHolder.FullChunkStatus chunkholder$fullchunkstatus1 = getFullChunkStatus(this.ticketLevel);
      if (flag) {
         Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either = Either.right(new ChunkHolder.ChunkLoadingFailure() {
            public String toString() {
               return "Unloaded ticket level " + ChunkHolder.this.pos;
            }
         });

         for(int i = flag1 ? chunkstatus1.getIndex() + 1 : 0; i <= chunkstatus.getIndex(); ++i) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.futures.get(i);
            if (completablefuture == null) {
               this.futures.set(i, CompletableFuture.completedFuture(either));
            }
         }
      }

      boolean flag5 = chunkholder$fullchunkstatus.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      boolean flag6 = chunkholder$fullchunkstatus1.isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
      this.wasAccessibleSinceLastSave |= flag6;
      if (!flag5 && flag6) {
         this.fullChunkFuture = p_143004_.prepareAccessibleChunk(this);
         this.scheduleFullChunkPromotion(p_143004_, this.fullChunkFuture, p_143005_, ChunkHolder.FullChunkStatus.BORDER);
         this.updateChunkToSave(this.fullChunkFuture, "full");
      }

      if (flag5 && !flag6) {
         this.fullChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.fullChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean flag7 = chunkholder$fullchunkstatus.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      boolean flag2 = chunkholder$fullchunkstatus1.isOrAfter(ChunkHolder.FullChunkStatus.TICKING);
      if (!flag7 && flag2) {
         this.tickingChunkFuture = p_143004_.prepareTickingChunk(this);
         this.scheduleFullChunkPromotion(p_143004_, this.tickingChunkFuture, p_143005_, ChunkHolder.FullChunkStatus.TICKING);
         this.updateChunkToSave(this.tickingChunkFuture, "ticking");
      }

      if (flag7 && !flag2) {
         this.tickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.tickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      boolean flag3 = chunkholder$fullchunkstatus.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      boolean flag4 = chunkholder$fullchunkstatus1.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING);
      if (!flag3 && flag4) {
         if (this.entityTickingChunkFuture != UNLOADED_LEVEL_CHUNK_FUTURE) {
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
         }

         this.entityTickingChunkFuture = p_143004_.prepareEntityTickingChunk(this.pos);
         this.scheduleFullChunkPromotion(p_143004_, this.entityTickingChunkFuture, p_143005_, ChunkHolder.FullChunkStatus.ENTITY_TICKING);
         this.updateChunkToSave(this.entityTickingChunkFuture, "entity ticking");
      }

      if (flag3 && !flag4) {
         this.entityTickingChunkFuture.complete(UNLOADED_LEVEL_CHUNK);
         this.entityTickingChunkFuture = UNLOADED_LEVEL_CHUNK_FUTURE;
      }

      if (!chunkholder$fullchunkstatus1.isOrAfter(chunkholder$fullchunkstatus)) {
         this.demoteFullChunk(p_143004_, chunkholder$fullchunkstatus1);
      }

      this.onLevelChange.onLevelChange(this.pos, this::getQueueLevel, this.ticketLevel, this::setQueueLevel);
      this.oldTicketLevel = this.ticketLevel;
   }

   public static ChunkStatus getStatus(int p_140075_) {
      return p_140075_ < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(p_140075_ - 33);
   }

   public static ChunkHolder.FullChunkStatus getFullChunkStatus(int p_140084_) {
      return FULL_CHUNK_STATUSES[Mth.clamp(33 - p_140084_ + 1, 0, FULL_CHUNK_STATUSES.length - 1)];
   }

   public boolean wasAccessibleSinceLastSave() {
      return this.wasAccessibleSinceLastSave;
   }

   public void refreshAccessibility() {
      this.wasAccessibleSinceLastSave = getFullChunkStatus(this.ticketLevel).isOrAfter(ChunkHolder.FullChunkStatus.BORDER);
   }

   public void replaceProtoChunk(ImposterProtoChunk p_140053_) {
      for(int i = 0; i < this.futures.length(); ++i) {
         CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.futures.get(i);
         if (completablefuture != null) {
            Optional<ChunkAccess> optional = completablefuture.getNow(UNLOADED_CHUNK).left();
            if (!optional.isEmpty() && optional.get() instanceof ProtoChunk) {
               this.futures.set(i, CompletableFuture.completedFuture(Either.left(p_140053_)));
            }
         }
      }

      this.updateChunkToSave(CompletableFuture.completedFuture(Either.left(p_140053_.getWrapped())), "replaceProto");
   }

   public List<Pair<ChunkStatus, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>> getAllFutures() {
      List<Pair<ChunkStatus, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>> list = new ArrayList<>();

      for(int i = 0; i < CHUNK_STATUSES.size(); ++i) {
         list.add(Pair.of(CHUNK_STATUSES.get(i), this.futures.get(i)));
      }

      return list;
   }

   public interface ChunkLoadingFailure {
      ChunkHolder.ChunkLoadingFailure UNLOADED = new ChunkHolder.ChunkLoadingFailure() {
         public String toString() {
            return "UNLOADED";
         }
      };
   }

   static final class ChunkSaveDebug {
      private final Thread thread;
      private final CompletableFuture<?> future;
      private final String source;

      ChunkSaveDebug(Thread p_143027_, CompletableFuture<?> p_143028_, String p_143029_) {
         this.thread = p_143027_;
         this.future = p_143028_;
         this.source = p_143029_;
      }
   }

   public static enum FullChunkStatus {
      INACCESSIBLE,
      BORDER,
      TICKING,
      ENTITY_TICKING;

      public boolean isOrAfter(ChunkHolder.FullChunkStatus p_140115_) {
         return this.ordinal() >= p_140115_.ordinal();
      }
   }

   @FunctionalInterface
   public interface LevelChangeListener {
      void onLevelChange(ChunkPos p_140119_, IntSupplier p_140120_, int p_140121_, IntConsumer p_140122_);
   }

   public interface PlayerProvider {
      List<ServerPlayer> getPlayers(ChunkPos p_183717_, boolean p_183718_);
   }
}
