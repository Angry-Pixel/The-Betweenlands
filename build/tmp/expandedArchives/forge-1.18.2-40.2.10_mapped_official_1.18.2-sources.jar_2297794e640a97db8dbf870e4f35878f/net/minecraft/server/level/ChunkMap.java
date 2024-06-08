package net.minecraft.server.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSetChunkCacheCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class ChunkMap extends ChunkStorage implements ChunkHolder.PlayerProvider {
   private static final byte CHUNK_TYPE_REPLACEABLE = -1;
   private static final byte CHUNK_TYPE_UNKNOWN = 0;
   private static final byte CHUNK_TYPE_FULL = 1;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int CHUNK_SAVED_PER_TICK = 200;
   private static final int CHUNK_SAVED_EAGERLY_PER_TICK = 20;
   private static final int EAGER_CHUNK_SAVE_COOLDOWN_IN_MILLIS = 10000;
   private static final int MIN_VIEW_DISTANCE = 3;
   public static final int MAX_VIEW_DISTANCE = 33;
   public static final int MAX_CHUNK_DISTANCE = 33 + ChunkStatus.maxDistance();
   public static final int FORCED_TICKET_LEVEL = 31;
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap = new Long2ObjectLinkedOpenHashMap<>();
   private volatile Long2ObjectLinkedOpenHashMap<ChunkHolder> visibleChunkMap = this.updatingChunkMap.clone();
   private final Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads = new Long2ObjectLinkedOpenHashMap<>();
   private final LongSet entitiesInLevel = new LongOpenHashSet();
   final ServerLevel level;
   private final ThreadedLevelLightEngine lightEngine;
   private final BlockableEventLoop<Runnable> mainThreadExecutor;
   private ChunkGenerator generator;
   private final Supplier<DimensionDataStorage> overworldDataStorage;
   private final PoiManager poiManager;
   final LongSet toDrop = new LongOpenHashSet();
   private boolean modified;
   private final ChunkTaskPriorityQueueSorter queueSorter;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> worldgenMailbox;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> mainThreadMailbox;
   private final ChunkProgressListener progressListener;
   private final ChunkStatusUpdateListener chunkStatusListener;
   private final ChunkMap.DistanceManager distanceManager;
   private final AtomicInteger tickingGenerated = new AtomicInteger();
   private final StructureManager structureManager;
   private final String storageName;
   private final PlayerMap playerMap = new PlayerMap();
   private final Int2ObjectMap<ChunkMap.TrackedEntity> entityMap = new Int2ObjectOpenHashMap<>();
   private final Long2ByteMap chunkTypeCache = new Long2ByteOpenHashMap();
   private final Long2LongMap chunkSaveCooldowns = new Long2LongOpenHashMap();
   private final Queue<Runnable> unloadQueue = Queues.newConcurrentLinkedQueue();
   int viewDistance;

   public ChunkMap(ServerLevel p_143040_, LevelStorageSource.LevelStorageAccess p_143041_, DataFixer p_143042_, StructureManager p_143043_, Executor p_143044_, BlockableEventLoop<Runnable> p_143045_, LightChunkGetter p_143046_, ChunkGenerator p_143047_, ChunkProgressListener p_143048_, ChunkStatusUpdateListener p_143049_, Supplier<DimensionDataStorage> p_143050_, int p_143051_, boolean p_143052_) {
      super(p_143041_.getDimensionPath(p_143040_.dimension()).resolve("region"), p_143042_, p_143052_);
      this.structureManager = p_143043_;
      Path path = p_143041_.getDimensionPath(p_143040_.dimension());
      this.storageName = path.getFileName().toString();
      this.level = p_143040_;
      this.generator = p_143047_;
      this.mainThreadExecutor = p_143045_;
      ProcessorMailbox<Runnable> processormailbox = ProcessorMailbox.create(p_143044_, "worldgen");
      ProcessorHandle<Runnable> processorhandle = ProcessorHandle.of("main", p_143045_::tell);
      this.progressListener = p_143048_;
      this.chunkStatusListener = p_143049_;
      ProcessorMailbox<Runnable> processormailbox1 = ProcessorMailbox.create(p_143044_, "light");
      this.queueSorter = new ChunkTaskPriorityQueueSorter(ImmutableList.of(processormailbox, processorhandle, processormailbox1), p_143044_, Integer.MAX_VALUE);
      this.worldgenMailbox = this.queueSorter.getProcessor(processormailbox, false);
      this.mainThreadMailbox = this.queueSorter.getProcessor(processorhandle, false);
      this.lightEngine = new ThreadedLevelLightEngine(p_143046_, this, this.level.dimensionType().hasSkyLight(), processormailbox1, this.queueSorter.getProcessor(processormailbox1, false));
      this.distanceManager = new ChunkMap.DistanceManager(p_143044_, p_143045_);
      this.overworldDataStorage = p_143050_;
      this.poiManager = new PoiManager(path.resolve("poi"), p_143042_, p_143052_, p_143040_);
      this.setViewDistance(p_143051_);
   }

   protected ChunkGenerator generator() {
      return this.generator;
   }

   public void debugReloadGenerator() {
      DataResult<JsonElement> dataresult = ChunkGenerator.CODEC.encodeStart(JsonOps.INSTANCE, this.generator);
      DataResult<ChunkGenerator> dataresult1 = dataresult.flatMap((p_183804_) -> {
         return ChunkGenerator.CODEC.parse(JsonOps.INSTANCE, p_183804_);
      });
      dataresult1.result().ifPresent((p_183808_) -> {
         this.generator = p_183808_;
      });
   }

   private static double euclideanDistanceSquared(ChunkPos p_140227_, Entity p_140228_) {
      double d0 = (double)SectionPos.sectionToBlockCoord(p_140227_.x, 8);
      double d1 = (double)SectionPos.sectionToBlockCoord(p_140227_.z, 8);
      double d2 = d0 - p_140228_.getX();
      double d3 = d1 - p_140228_.getZ();
      return d2 * d2 + d3 * d3;
   }

   public static boolean isChunkInRange(int p_200879_, int p_200880_, int p_200881_, int p_200882_, int p_200883_) {
      int i = Math.max(0, Math.abs(p_200879_ - p_200881_) - 1);
      int j = Math.max(0, Math.abs(p_200880_ - p_200882_) - 1);
      long k = (long)Math.max(0, Math.max(i, j) - 1);
      long l = (long)Math.min(i, j);
      long i1 = l * l + k * k;
      int j1 = p_200883_ - 1;
      int k1 = j1 * j1;
      return i1 <= (long)k1;
   }

   private static boolean isChunkOnRangeBorder(int p_183829_, int p_183830_, int p_183831_, int p_183832_, int p_183833_) {
      if (!isChunkInRange(p_183829_, p_183830_, p_183831_, p_183832_, p_183833_)) {
         return false;
      } else if (!isChunkInRange(p_183829_ + 1, p_183830_, p_183831_, p_183832_, p_183833_)) {
         return true;
      } else if (!isChunkInRange(p_183829_, p_183830_ + 1, p_183831_, p_183832_, p_183833_)) {
         return true;
      } else if (!isChunkInRange(p_183829_ - 1, p_183830_, p_183831_, p_183832_, p_183833_)) {
         return true;
      } else {
         return !isChunkInRange(p_183829_, p_183830_ - 1, p_183831_, p_183832_, p_183833_);
      }
   }

   protected ThreadedLevelLightEngine getLightEngine() {
      return this.lightEngine;
   }

   @Nullable
   protected ChunkHolder getUpdatingChunkIfPresent(long p_140175_) {
      return this.updatingChunkMap.get(p_140175_);
   }

   @Nullable
   protected ChunkHolder getVisibleChunkIfPresent(long p_140328_) {
      return this.visibleChunkMap.get(p_140328_);
   }

   protected IntSupplier getChunkQueueLevel(long p_140372_) {
      return () -> {
         ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_140372_);
         return chunkholder == null ? ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1 : Math.min(chunkholder.getQueueLevel(), ChunkTaskPriorityQueue.PRIORITY_LEVEL_COUNT - 1);
      };
   }

   public String getChunkDebugData(ChunkPos p_140205_) {
      ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_140205_.toLong());
      if (chunkholder == null) {
         return "null";
      } else {
         String s = chunkholder.getTicketLevel() + "\n";
         ChunkStatus chunkstatus = chunkholder.getLastAvailableStatus();
         ChunkAccess chunkaccess = chunkholder.getLastAvailable();
         if (chunkstatus != null) {
            s = s + "St: \u00a7" + chunkstatus.getIndex() + chunkstatus + "\u00a7r\n";
         }

         if (chunkaccess != null) {
            s = s + "Ch: \u00a7" + chunkaccess.getStatus().getIndex() + chunkaccess.getStatus() + "\u00a7r\n";
         }

         ChunkHolder.FullChunkStatus chunkholder$fullchunkstatus = chunkholder.getFullStatus();
         s = s + "\u00a7" + chunkholder$fullchunkstatus.ordinal() + chunkholder$fullchunkstatus;
         return s + "\u00a7r";
      }
   }

   private CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> getChunkRangeFuture(ChunkPos p_140211_, int p_140212_, IntFunction<ChunkStatus> p_140213_) {
      List<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> list = new ArrayList<>();
      List<ChunkHolder> list1 = new ArrayList<>();
      int i = p_140211_.x;
      int j = p_140211_.z;

      for(int k = -p_140212_; k <= p_140212_; ++k) {
         for(int l = -p_140212_; l <= p_140212_; ++l) {
            int i1 = Math.max(Math.abs(l), Math.abs(k));
            final ChunkPos chunkpos = new ChunkPos(i + l, j + k);
            long j1 = chunkpos.toLong();
            ChunkHolder chunkholder = this.getUpdatingChunkIfPresent(j1);
            if (chunkholder == null) {
               return CompletableFuture.completedFuture(Either.right(new ChunkHolder.ChunkLoadingFailure() {
                  public String toString() {
                     return "Unloaded " + chunkpos;
                  }
               }));
            }

            ChunkStatus chunkstatus = p_140213_.apply(i1);
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = chunkholder.getOrScheduleFuture(chunkstatus, this);
            list1.add(chunkholder);
            list.add(completablefuture);
         }
      }

      CompletableFuture<List<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> completablefuture1 = Util.sequence(list);
      CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> completablefuture2 = completablefuture1.thenApply((p_183730_) -> {
         List<ChunkAccess> list2 = Lists.newArrayList();
         int k1 = 0;

         for(final Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure> either : p_183730_) {
            if (either == null) {
               throw this.debugFuturesAndCreateReportedException(new IllegalStateException("At least one of the chunk futures were null"), "n/a");
            }

            Optional<ChunkAccess> optional = either.left();
            if (!optional.isPresent()) {
               final int l1 = k1;
               return Either.right(new ChunkHolder.ChunkLoadingFailure() {
                  public String toString() {
                     return "Unloaded " + new ChunkPos(i + l1 % (p_140212_ * 2 + 1), j + l1 / (p_140212_ * 2 + 1)) + " " + either.right().get();
                  }
               });
            }

            list2.add(optional.get());
            ++k1;
         }

         return Either.left(list2);
      });

      for(ChunkHolder chunkholder1 : list1) {
         chunkholder1.addSaveDependency("getChunkRangeFuture " + p_140211_ + " " + p_140212_, completablefuture2);
      }

      return completablefuture2;
   }

   public ReportedException debugFuturesAndCreateReportedException(IllegalStateException p_203752_, String p_203753_) {
      StringBuilder stringbuilder = new StringBuilder();
      Consumer<ChunkHolder> consumer = (p_203756_) -> {
         p_203756_.getAllFutures().forEach((p_203760_) -> {
            ChunkStatus chunkstatus = p_203760_.getFirst();
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = p_203760_.getSecond();
            if (completablefuture != null && completablefuture.isDone() && completablefuture.join() == null) {
               stringbuilder.append((Object)p_203756_.getPos()).append(" - status: ").append((Object)chunkstatus).append(" future: ").append((Object)completablefuture).append(System.lineSeparator());
            }

         });
      };
      stringbuilder.append("Updating:").append(System.lineSeparator());
      this.updatingChunkMap.values().forEach(consumer);
      stringbuilder.append("Visible:").append(System.lineSeparator());
      this.visibleChunkMap.values().forEach(consumer);
      CrashReport crashreport = CrashReport.forThrowable(p_203752_, "Chunk loading");
      CrashReportCategory crashreportcategory = crashreport.addCategory("Chunk loading");
      crashreportcategory.setDetail("Details", p_203753_);
      crashreportcategory.setDetail("Futures", stringbuilder);
      return new ReportedException(crashreport);
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareEntityTickingChunk(ChunkPos p_143118_) {
      return this.getChunkRangeFuture(p_143118_, 2, (p_203078_) -> {
         return ChunkStatus.FULL;
      }).thenApplyAsync((p_203086_) -> {
         return p_203086_.mapLeft((p_203092_) -> {
            return (LevelChunk)p_203092_.get(p_203092_.size() / 2);
         });
      }, this.mainThreadExecutor);
   }

   @Nullable
   ChunkHolder updateChunkScheduling(long p_140177_, int p_140178_, @Nullable ChunkHolder p_140179_, int p_140180_) {
      if (p_140180_ > MAX_CHUNK_DISTANCE && p_140178_ > MAX_CHUNK_DISTANCE) {
         return p_140179_;
      } else {
         if (p_140179_ != null) {
            p_140179_.setTicketLevel(p_140178_);
         }

         if (p_140179_ != null) {
            if (p_140178_ > MAX_CHUNK_DISTANCE) {
               this.toDrop.add(p_140177_);
            } else {
               this.toDrop.remove(p_140177_);
            }
         }

         if (p_140178_ <= MAX_CHUNK_DISTANCE && p_140179_ == null) {
            p_140179_ = this.pendingUnloads.remove(p_140177_);
            if (p_140179_ != null) {
               p_140179_.setTicketLevel(p_140178_);
            } else {
               p_140179_ = new ChunkHolder(new ChunkPos(p_140177_), p_140178_, this.level, this.lightEngine, this.queueSorter, this);
            }

            this.updatingChunkMap.put(p_140177_, p_140179_);
            this.modified = true;
         }

         return p_140179_;
      }
   }

   public void close() throws IOException {
      try {
         this.queueSorter.close();
         this.poiManager.close();
      } finally {
         super.close();
      }

   }

   protected void saveAllChunks(boolean p_140319_) {
      if (p_140319_) {
         List<ChunkHolder> list = this.visibleChunkMap.values().stream().filter(ChunkHolder::wasAccessibleSinceLastSave).peek(ChunkHolder::refreshAccessibility).collect(Collectors.toList());
         MutableBoolean mutableboolean = new MutableBoolean();

         do {
            mutableboolean.setFalse();
            list.stream().map((p_203102_) -> {
               CompletableFuture<ChunkAccess> completablefuture;
               do {
                  completablefuture = p_203102_.getChunkToSave();
                  this.mainThreadExecutor.managedBlock(completablefuture::isDone);
               } while(completablefuture != p_203102_.getChunkToSave());

               return completablefuture.join();
            }).filter((p_203088_) -> {
               return p_203088_ instanceof ImposterProtoChunk || p_203088_ instanceof LevelChunk;
            }).filter(this::save).forEach((p_203051_) -> {
               mutableboolean.setTrue();
            });
         } while(mutableboolean.isTrue());

         this.processUnloads(() -> {
            return true;
         });
         this.flushWorker();
      } else {
         this.visibleChunkMap.values().forEach(this::saveChunkIfNeeded);
      }

   }

   protected void tick(BooleanSupplier p_140281_) {
      ProfilerFiller profilerfiller = this.level.getProfiler();
      profilerfiller.push("poi");
      this.poiManager.tick(p_140281_);
      profilerfiller.popPush("chunk_unload");
      if (!this.level.noSave()) {
         this.processUnloads(p_140281_);
      }

      profilerfiller.pop();
   }

   public boolean hasWork() {
      return this.lightEngine.hasLightWork() || !this.pendingUnloads.isEmpty() || !this.updatingChunkMap.isEmpty() || this.poiManager.hasWork() || !this.toDrop.isEmpty() || !this.unloadQueue.isEmpty() || this.queueSorter.hasWork() || this.distanceManager.hasTickets();
   }

   private void processUnloads(BooleanSupplier p_140354_) {
      LongIterator longiterator = this.toDrop.iterator();

      for(int i = 0; longiterator.hasNext() && (p_140354_.getAsBoolean() || i < 200 || this.toDrop.size() > 2000); longiterator.remove()) {
         long j = longiterator.nextLong();
         ChunkHolder chunkholder = this.updatingChunkMap.remove(j);
         if (chunkholder != null) {
            this.pendingUnloads.put(j, chunkholder);
            this.modified = true;
            ++i;
            this.scheduleUnload(j, chunkholder);
         }
      }

      int k = Math.max(0, this.unloadQueue.size() - 2000);

      Runnable runnable;
      while((p_140354_.getAsBoolean() || k > 0) && (runnable = this.unloadQueue.poll()) != null) {
         --k;
         runnable.run();
      }

      int l = 0;
      ObjectIterator<ChunkHolder> objectiterator = this.visibleChunkMap.values().iterator();

      while(l < 20 && p_140354_.getAsBoolean() && objectiterator.hasNext()) {
         if (this.saveChunkIfNeeded(objectiterator.next())) {
            ++l;
         }
      }

   }

   private void scheduleUnload(long p_140182_, ChunkHolder p_140183_) {
      CompletableFuture<ChunkAccess> completablefuture = p_140183_.getChunkToSave();
      completablefuture.thenAcceptAsync((p_203002_) -> {
         CompletableFuture<ChunkAccess> completablefuture1 = p_140183_.getChunkToSave();
         if (completablefuture1 != completablefuture) {
            this.scheduleUnload(p_140182_, p_140183_);
         } else {
            if (this.pendingUnloads.remove(p_140182_, p_140183_) && p_203002_ != null) {
               if (p_203002_ instanceof LevelChunk) {
                  ((LevelChunk)p_203002_).setLoaded(false);
                  net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Unload(p_203002_));
               }

               this.save(p_203002_);
               if (this.entitiesInLevel.remove(p_140182_) && p_203002_ instanceof LevelChunk) {
                  LevelChunk levelchunk = (LevelChunk)p_203002_;
                  this.level.unload(levelchunk);
               }

               this.lightEngine.updateChunkStatus(p_203002_.getPos());
               this.lightEngine.tryScheduleUpdate();
               this.progressListener.onStatusChange(p_203002_.getPos(), (ChunkStatus)null);
               this.chunkSaveCooldowns.remove(p_203002_.getPos().toLong());
            }

         }
      }, this.unloadQueue::add).whenComplete((p_202996_, p_202997_) -> {
         if (p_202997_ != null) {
            LOGGER.error("Failed to save chunk {}", p_140183_.getPos(), p_202997_);
         }

      });
   }

   protected boolean promoteChunkMap() {
      if (!this.modified) {
         return false;
      } else {
         this.visibleChunkMap = this.updatingChunkMap.clone();
         this.modified = false;
         return true;
      }
   }

   public CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> schedule(ChunkHolder p_140293_, ChunkStatus p_140294_) {
      ChunkPos chunkpos = p_140293_.getPos();
      if (p_140294_ == ChunkStatus.EMPTY) {
         return this.scheduleChunkLoad(chunkpos);
      } else {
         if (p_140294_ == ChunkStatus.LIGHT) {
            this.distanceManager.addTicket(TicketType.LIGHT, chunkpos, 33 + ChunkStatus.getDistance(ChunkStatus.LIGHT), chunkpos);
         }

         Optional<ChunkAccess> optional = p_140293_.getOrScheduleFuture(p_140294_.getParent(), this).getNow(ChunkHolder.UNLOADED_CHUNK).left();
         if (optional.isPresent() && optional.get().getStatus().isOrAfter(p_140294_)) {
            CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = p_140294_.load(this.level, this.structureManager, this.lightEngine, (p_203081_) -> {
               return this.protoChunkToFullChunk(p_140293_);
            }, optional.get());
            this.progressListener.onStatusChange(chunkpos, p_140294_);
            return completablefuture;
         } else {
            return this.scheduleChunkGeneration(p_140293_, p_140294_);
         }
      }
   }

   private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkLoad(ChunkPos p_140418_) {
      return CompletableFuture.supplyAsync(() -> {
         try {
            this.level.getProfiler().incrementCounter("chunkLoad");
            CompoundTag compoundtag = this.readChunk(p_140418_);
            if (compoundtag != null) {
               boolean flag = compoundtag.contains("Status", 8);
               if (flag) {
                  ChunkAccess chunkaccess = ChunkSerializer.read(this.level, this.poiManager, p_140418_, compoundtag);
                  this.markPosition(p_140418_, chunkaccess.getStatus().getChunkType());
                  return Either.left(chunkaccess);
               }

               LOGGER.error("Chunk file at {} is missing level data, skipping", (Object)p_140418_);
            }
         } catch (ReportedException reportedexception) {
            Throwable throwable = reportedexception.getCause();
            if (!(throwable instanceof IOException)) {
               this.markPositionReplaceable(p_140418_);
               throw reportedexception;
            }

            LOGGER.error("Couldn't load chunk {}", p_140418_, throwable);
         } catch (Exception exception) {
            LOGGER.error("Couldn't load chunk {}", p_140418_, exception);
         }

         this.markPositionReplaceable(p_140418_);
         return Either.left(new ProtoChunk(p_140418_, UpgradeData.EMPTY, this.level, this.level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY), (BlendingData)null));
      }, this.mainThreadExecutor);
   }

   private void markPositionReplaceable(ChunkPos p_140423_) {
      this.chunkTypeCache.put(p_140423_.toLong(), (byte)-1);
   }

   private byte markPosition(ChunkPos p_140230_, ChunkStatus.ChunkType p_140231_) {
      return this.chunkTypeCache.put(p_140230_.toLong(), (byte)(p_140231_ == ChunkStatus.ChunkType.PROTOCHUNK ? -1 : 1));
   }

   private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> scheduleChunkGeneration(ChunkHolder p_140361_, ChunkStatus p_140362_) {
      ChunkPos chunkpos = p_140361_.getPos();
      CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getChunkRangeFuture(chunkpos, p_140362_.getRange(), (p_203072_) -> {
         return this.getDependencyStatus(p_140362_, p_203072_);
      });
      this.level.getProfiler().incrementCounter(() -> {
         return "chunkGenerate " + p_140362_.getName();
      });
      Executor executor = (p_203100_) -> {
         this.worldgenMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_140361_, p_203100_));
      };
      return completablefuture.thenComposeAsync((p_203016_) -> {
         return p_203016_.map((p_203022_) -> {
            try {
               CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture1 = p_140362_.generate(executor, this.level, this.generator, this.structureManager, this.lightEngine, (p_203062_) -> {
                  return this.protoChunkToFullChunk(p_140361_);
               }, p_203022_, false);
               this.progressListener.onStatusChange(chunkpos, p_140362_);
               return completablefuture1;
            } catch (Exception exception) {
               exception.getStackTrace();
               CrashReport crashreport = CrashReport.forThrowable(exception, "Exception generating new chunk");
               CrashReportCategory crashreportcategory = crashreport.addCategory("Chunk to be generated");
               crashreportcategory.setDetail("Location", String.format("%d,%d", chunkpos.x, chunkpos.z));
               crashreportcategory.setDetail("Position hash", ChunkPos.asLong(chunkpos.x, chunkpos.z));
               crashreportcategory.setDetail("Generator", this.generator);
               this.mainThreadExecutor.execute(() -> {
                  throw new ReportedException(crashreport);
               });
               throw new ReportedException(crashreport);
            }
         }, (p_203010_) -> {
            this.releaseLightTicket(chunkpos);
            return CompletableFuture.completedFuture(Either.right(p_203010_));
         });
      }, executor);
   }

   protected void releaseLightTicket(ChunkPos p_140376_) {
      this.mainThreadExecutor.tell(Util.name(() -> {
         this.distanceManager.removeTicket(TicketType.LIGHT, p_140376_, 33 + ChunkStatus.getDistance(ChunkStatus.LIGHT), p_140376_);
      }, () -> {
         return "release light ticket " + p_140376_;
      }));
   }

   private ChunkStatus getDependencyStatus(ChunkStatus p_140263_, int p_140264_) {
      ChunkStatus chunkstatus;
      if (p_140264_ == 0) {
         chunkstatus = p_140263_.getParent();
      } else {
         chunkstatus = ChunkStatus.getStatusAroundFullChunk(ChunkStatus.getDistance(p_140263_) + p_140264_);
      }

      return chunkstatus;
   }

   private static void postLoadProtoChunk(ServerLevel p_143065_, List<CompoundTag> p_143066_) {
      if (!p_143066_.isEmpty()) {
         p_143065_.addWorldGenChunkEntities(EntityType.loadEntitiesRecursive(p_143066_, p_143065_));
      }

   }

   private CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> protoChunkToFullChunk(ChunkHolder p_140384_) {
      CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> completablefuture = p_140384_.getFutureIfPresentUnchecked(ChunkStatus.FULL.getParent());
      return completablefuture.thenApplyAsync((p_202987_) -> {
         ChunkStatus chunkstatus = ChunkHolder.getStatus(p_140384_.getTicketLevel());
         return !chunkstatus.isOrAfter(ChunkStatus.FULL) ? ChunkHolder.UNLOADED_CHUNK : p_202987_.mapLeft((p_202990_) -> {
            ChunkPos chunkpos = p_140384_.getPos();
            ProtoChunk protochunk = (ProtoChunk)p_202990_;
            LevelChunk levelchunk;
            if (protochunk instanceof ImposterProtoChunk) {
               levelchunk = ((ImposterProtoChunk)protochunk).getWrapped();
            } else {
               levelchunk = new LevelChunk(this.level, protochunk, (p_203037_) -> {
                  postLoadProtoChunk(this.level, protochunk.getEntities());
               });
               p_140384_.replaceProtoChunk(new ImposterProtoChunk(levelchunk, false));
            }

            levelchunk.setFullStatus(() -> {
               return ChunkHolder.getFullChunkStatus(p_140384_.getTicketLevel());
            });
            levelchunk.runPostLoad();
            if (this.entitiesInLevel.add(chunkpos.toLong())) {
               levelchunk.setLoaded(true);
               try {
               p_140384_.currentlyLoading = levelchunk; // Forge - bypass the future chain when getChunk is called, this prevents deadlocks.
               levelchunk.registerAllBlockEntitiesAfterLevelLoad();
               levelchunk.registerTickContainerInLevel(this.level);
               net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkEvent.Load(levelchunk));
               } finally {
                   p_140384_.currentlyLoading = null; // Forge - Stop bypassing the future chain.
               }
            }

            return levelchunk;
         });
      }, (p_203095_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_203095_, p_140384_.getPos().toLong(), p_140384_::getTicketLevel));
      });
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareTickingChunk(ChunkHolder p_143054_) {
      ChunkPos chunkpos = p_143054_.getPos();
      CompletableFuture<Either<List<ChunkAccess>, ChunkHolder.ChunkLoadingFailure>> completablefuture = this.getChunkRangeFuture(chunkpos, 1, (p_203059_) -> {
         return ChunkStatus.FULL;
      });
      CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> completablefuture1 = completablefuture.thenApplyAsync((p_203067_) -> {
         return p_203067_.mapLeft((p_212884_) -> {
            return (LevelChunk)p_212884_.get(p_212884_.size() / 2);
         });
      }, (p_203084_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_143054_, p_203084_));
      }).thenApplyAsync((p_212878_) -> {
         return p_212878_.ifLeft((p_212888_) -> {
            p_212888_.postProcessGeneration();
            this.level.startTickingChunk(p_212888_);
         });
      }, this.mainThreadExecutor);
      completablefuture1.thenAcceptAsync((p_212860_) -> {
         p_212860_.ifLeft((p_212863_) -> {
            this.tickingGenerated.getAndIncrement();
            MutableObject<ClientboundLevelChunkWithLightPacket> mutableobject = new MutableObject<>();
            this.getPlayers(chunkpos, false).forEach((p_212873_) -> {
               this.playerLoadedChunk(p_212873_, mutableobject, p_212863_);
            });
         });
      }, (p_212876_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_143054_, p_212876_));
      });
      return completablefuture1;
   }

   public CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> prepareAccessibleChunk(ChunkHolder p_143110_) {
      return this.getChunkRangeFuture(p_143110_.getPos(), 1, ChunkStatus::getStatusAroundFullChunk).thenApplyAsync((p_212865_) -> {
         return p_212865_.mapLeft((p_212869_) -> {
            return (LevelChunk)p_212869_.get(p_212869_.size() / 2);
         });
      }, (p_212852_) -> {
         this.mainThreadMailbox.tell(ChunkTaskPriorityQueueSorter.message(p_143110_, p_212852_));
      });
   }

   public int getTickingGenerated() {
      return this.tickingGenerated.get();
   }

   private boolean saveChunkIfNeeded(ChunkHolder p_198875_) {
      if (!p_198875_.wasAccessibleSinceLastSave()) {
         return false;
      } else {
         ChunkAccess chunkaccess = p_198875_.getChunkToSave().getNow((ChunkAccess)null);
         if (!(chunkaccess instanceof ImposterProtoChunk) && !(chunkaccess instanceof LevelChunk)) {
            return false;
         } else {
            long i = chunkaccess.getPos().toLong();
            long j = this.chunkSaveCooldowns.getOrDefault(i, -1L);
            long k = System.currentTimeMillis();
            if (k < j) {
               return false;
            } else {
               boolean flag = this.save(chunkaccess);
               p_198875_.refreshAccessibility();
               if (flag) {
                  this.chunkSaveCooldowns.put(i, k + 10000L);
               }

               return flag;
            }
         }
      }
   }

   private boolean save(ChunkAccess p_140259_) {
      this.poiManager.flush(p_140259_.getPos());
      if (!p_140259_.isUnsaved()) {
         return false;
      } else {
         p_140259_.setUnsaved(false);
         ChunkPos chunkpos = p_140259_.getPos();

         try {
            ChunkStatus chunkstatus = p_140259_.getStatus();
            if (chunkstatus.getChunkType() != ChunkStatus.ChunkType.LEVELCHUNK) {
               if (this.isExistingChunkFull(chunkpos)) {
                  return false;
               }

               if (chunkstatus == ChunkStatus.EMPTY && p_140259_.getAllStarts().values().stream().noneMatch(StructureStart::isValid)) {
                  return false;
               }
            }

            this.level.getProfiler().incrementCounter("chunkSave");
            CompoundTag compoundtag = ChunkSerializer.write(this.level, p_140259_);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.world.ChunkDataEvent.Save(p_140259_, p_140259_.getWorldForge() != null ? p_140259_.getWorldForge() : this.level, compoundtag));
            this.write(chunkpos, compoundtag);
            this.markPosition(chunkpos, chunkstatus.getChunkType());
            return true;
         } catch (Exception exception) {
            LOGGER.error("Failed to save chunk {},{}", chunkpos.x, chunkpos.z, exception);
            return false;
         }
      }
   }

   private boolean isExistingChunkFull(ChunkPos p_140426_) {
      byte b0 = this.chunkTypeCache.get(p_140426_.toLong());
      if (b0 != 0) {
         return b0 == 1;
      } else {
         CompoundTag compoundtag;
         try {
            compoundtag = this.readChunk(p_140426_);
            if (compoundtag == null) {
               this.markPositionReplaceable(p_140426_);
               return false;
            }
         } catch (Exception exception) {
            LOGGER.error("Failed to read chunk {}", p_140426_, exception);
            this.markPositionReplaceable(p_140426_);
            return false;
         }

         ChunkStatus.ChunkType chunkstatus$chunktype = ChunkSerializer.getChunkTypeFromTag(compoundtag);
         return this.markPosition(p_140426_, chunkstatus$chunktype) == 1;
      }
   }

   protected void setViewDistance(int p_140168_) {
      int i = Mth.clamp(p_140168_ + 1, 3, 33);
      if (i != this.viewDistance) {
         int j = this.viewDistance;
         this.viewDistance = i;
         this.distanceManager.updatePlayerTickets(this.viewDistance + 1);

         for(ChunkHolder chunkholder : this.updatingChunkMap.values()) {
            ChunkPos chunkpos = chunkholder.getPos();
            MutableObject<ClientboundLevelChunkWithLightPacket> mutableobject = new MutableObject<>();
            this.getPlayers(chunkpos, false).forEach((p_212857_) -> {
               SectionPos sectionpos = p_212857_.getLastSectionPos();
               boolean flag = isChunkInRange(chunkpos.x, chunkpos.z, sectionpos.x(), sectionpos.z(), j);
               boolean flag1 = isChunkInRange(chunkpos.x, chunkpos.z, sectionpos.x(), sectionpos.z(), this.viewDistance);
               this.updateChunkTracking(p_212857_, chunkpos, mutableobject, flag, flag1);
            });
         }
      }

   }

   protected void updateChunkTracking(ServerPlayer p_183755_, ChunkPos p_183756_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183757_, boolean p_183758_, boolean p_183759_) {
      if (p_183755_.level == this.level) {
         net.minecraftforge.event.ForgeEventFactory.fireChunkWatch(p_183758_, p_183759_, p_183755_, p_183756_, this.level);
         if (p_183759_ && !p_183758_) {
            ChunkHolder chunkholder = this.getVisibleChunkIfPresent(p_183756_.toLong());
            if (chunkholder != null) {
               LevelChunk levelchunk = chunkholder.getTickingChunk();
               if (levelchunk != null) {
                  this.playerLoadedChunk(p_183755_, p_183757_, levelchunk);
               }

               DebugPackets.sendPoiPacketsForChunk(this.level, p_183756_);
            }
         }

         if (!p_183759_ && p_183758_) {
            p_183755_.untrackChunk(p_183756_);
         }

      }
   }

   public int size() {
      return this.visibleChunkMap.size();
   }

   public net.minecraft.server.level.DistanceManager getDistanceManager() {
      return this.distanceManager;
   }

   protected Iterable<ChunkHolder> getChunks() {
      return Iterables.unmodifiableIterable(this.visibleChunkMap.values());
   }

   void dumpChunks(Writer p_140275_) throws IOException {
      CsvOutput csvoutput = CsvOutput.builder().addColumn("x").addColumn("z").addColumn("level").addColumn("in_memory").addColumn("status").addColumn("full_status").addColumn("accessible_ready").addColumn("ticking_ready").addColumn("entity_ticking_ready").addColumn("ticket").addColumn("spawning").addColumn("block_entity_count").addColumn("ticking_ticket").addColumn("ticking_level").addColumn("block_ticks").addColumn("fluid_ticks").build(p_140275_);
      TickingTracker tickingtracker = this.distanceManager.tickingTracker();

      for(Entry<ChunkHolder> entry : this.visibleChunkMap.long2ObjectEntrySet()) {
         long i = entry.getLongKey();
         ChunkPos chunkpos = new ChunkPos(i);
         ChunkHolder chunkholder = entry.getValue();
         Optional<ChunkAccess> optional = Optional.ofNullable(chunkholder.getLastAvailable());
         Optional<LevelChunk> optional1 = optional.flatMap((p_212880_) -> {
            return p_212880_ instanceof LevelChunk ? Optional.of((LevelChunk)p_212880_) : Optional.empty();
         });
         csvoutput.writeRow(chunkpos.x, chunkpos.z, chunkholder.getTicketLevel(), optional.isPresent(), optional.map(ChunkAccess::getStatus).orElse((ChunkStatus)null), optional1.map(LevelChunk::getFullStatus).orElse((ChunkHolder.FullChunkStatus)null), printFuture(chunkholder.getFullChunkFuture()), printFuture(chunkholder.getTickingChunkFuture()), printFuture(chunkholder.getEntityTickingChunkFuture()), this.distanceManager.getTicketDebugString(i), this.anyPlayerCloseEnoughForSpawning(chunkpos), optional1.map((p_203074_) -> {
            return p_203074_.getBlockEntities().size();
         }).orElse(0), tickingtracker.getTicketDebugString(i), tickingtracker.getLevel(i), optional1.map((p_212886_) -> {
            return p_212886_.getBlockTicks().count();
         }).orElse(0), optional1.map((p_212882_) -> {
            return p_212882_.getFluidTicks().count();
         }).orElse(0));
      }

   }

   private static String printFuture(CompletableFuture<Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>> p_140279_) {
      try {
         Either<LevelChunk, ChunkHolder.ChunkLoadingFailure> either = p_140279_.getNow((Either<LevelChunk, ChunkHolder.ChunkLoadingFailure>)null);
         return either != null ? either.map((p_212867_) -> {
            return "done";
         }, (p_212849_) -> {
            return "unloaded";
         }) : "not completed";
      } catch (CompletionException completionexception) {
         return "failed " + completionexception.getCause().getMessage();
      } catch (CancellationException cancellationexception) {
         return "cancelled";
      }
   }

   @Nullable
   private CompoundTag readChunk(ChunkPos p_140428_) throws IOException {
      CompoundTag compoundtag = this.read(p_140428_);
      return compoundtag == null ? null : this.upgradeChunkTag(this.level.dimension(), this.overworldDataStorage, compoundtag, this.generator.getTypeNameForDataFixer());
   }

   boolean anyPlayerCloseEnoughForSpawning(ChunkPos p_183880_) {
      long i = p_183880_.toLong();
      if (!this.distanceManager.hasPlayersNearby(i)) {
         return false;
      } else {
         for(ServerPlayer serverplayer : this.playerMap.getPlayers(i)) {
            if (this.playerIsCloseEnoughForSpawning(serverplayer, p_183880_)) {
               return true;
            }
         }

         return false;
      }
   }

   public List<ServerPlayer> getPlayersCloseForSpawning(ChunkPos p_183889_) {
      long i = p_183889_.toLong();
      if (!this.distanceManager.hasPlayersNearby(i)) {
         return List.of();
      } else {
         Builder<ServerPlayer> builder = ImmutableList.builder();

         for(ServerPlayer serverplayer : this.playerMap.getPlayers(i)) {
            if (this.playerIsCloseEnoughForSpawning(serverplayer, p_183889_)) {
               builder.add(serverplayer);
            }
         }

         return builder.build();
      }
   }

   private boolean playerIsCloseEnoughForSpawning(ServerPlayer p_183752_, ChunkPos p_183753_) {
      if (p_183752_.isSpectator()) {
         return false;
      } else {
         double d0 = euclideanDistanceSquared(p_183753_, p_183752_);
         return d0 < 16384.0D;
      }
   }

   private boolean skipPlayer(ServerPlayer p_140330_) {
      return p_140330_.isSpectator() && !this.level.getGameRules().getBoolean(GameRules.RULE_SPECTATORSGENERATECHUNKS);
   }

   void updatePlayerStatus(ServerPlayer p_140193_, boolean p_140194_) {
      boolean flag = this.skipPlayer(p_140193_);
      boolean flag1 = this.playerMap.ignoredOrUnknown(p_140193_);
      int i = SectionPos.blockToSectionCoord(p_140193_.getBlockX());
      int j = SectionPos.blockToSectionCoord(p_140193_.getBlockZ());
      if (p_140194_) {
         this.playerMap.addPlayer(ChunkPos.asLong(i, j), p_140193_, flag);
         this.updatePlayerPos(p_140193_);
         if (!flag) {
            this.distanceManager.addPlayer(SectionPos.of(p_140193_), p_140193_);
         }
      } else {
         SectionPos sectionpos = p_140193_.getLastSectionPos();
         this.playerMap.removePlayer(sectionpos.chunk().toLong(), p_140193_);
         if (!flag1) {
            this.distanceManager.removePlayer(sectionpos, p_140193_);
         }
      }

      for(int l = i - this.viewDistance - 1; l <= i + this.viewDistance + 1; ++l) {
         for(int k = j - this.viewDistance - 1; k <= j + this.viewDistance + 1; ++k) {
            if (isChunkInRange(l, k, i, j, this.viewDistance)) {
               ChunkPos chunkpos = new ChunkPos(l, k);
               this.updateChunkTracking(p_140193_, chunkpos, new MutableObject<>(), !p_140194_, p_140194_);
            }
         }
      }

   }

   private SectionPos updatePlayerPos(ServerPlayer p_140374_) {
      SectionPos sectionpos = SectionPos.of(p_140374_);
      p_140374_.setLastSectionPos(sectionpos);
      p_140374_.connection.send(new ClientboundSetChunkCacheCenterPacket(sectionpos.x(), sectionpos.z()));
      return sectionpos;
   }

   public void move(ServerPlayer p_140185_) {
      for(ChunkMap.TrackedEntity chunkmap$trackedentity : this.entityMap.values()) {
         if (chunkmap$trackedentity.entity == p_140185_) {
            chunkmap$trackedentity.updatePlayers(this.level.players());
         } else {
            chunkmap$trackedentity.updatePlayer(p_140185_);
         }
      }

      int i2 = SectionPos.blockToSectionCoord(p_140185_.getBlockX());
      int j2 = SectionPos.blockToSectionCoord(p_140185_.getBlockZ());
      SectionPos sectionpos = p_140185_.getLastSectionPos();
      SectionPos sectionpos1 = SectionPos.of(p_140185_);
      long i = sectionpos.chunk().toLong();
      long j = sectionpos1.chunk().toLong();
      boolean flag = this.playerMap.ignored(p_140185_);
      boolean flag1 = this.skipPlayer(p_140185_);
      boolean flag2 = sectionpos.asLong() != sectionpos1.asLong();
      if (flag2 || flag != flag1) {
         this.updatePlayerPos(p_140185_);
         if (!flag) {
            this.distanceManager.removePlayer(sectionpos, p_140185_);
         }

         if (!flag1) {
            this.distanceManager.addPlayer(sectionpos1, p_140185_);
         }

         if (!flag && flag1) {
            this.playerMap.ignorePlayer(p_140185_);
         }

         if (flag && !flag1) {
            this.playerMap.unIgnorePlayer(p_140185_);
         }

         if (i != j) {
            this.playerMap.updatePlayer(i, j, p_140185_);
         }
      }

      int k = sectionpos.x();
      int l = sectionpos.z();
      if (Math.abs(k - i2) <= this.viewDistance * 2 && Math.abs(l - j2) <= this.viewDistance * 2) {
         int l2 = Math.min(i2, k) - this.viewDistance - 1;
         int j3 = Math.min(j2, l) - this.viewDistance - 1;
         int k3 = Math.max(i2, k) + this.viewDistance + 1;
         int l3 = Math.max(j2, l) + this.viewDistance + 1;

         for(int k1 = l2; k1 <= k3; ++k1) {
            for(int l1 = j3; l1 <= l3; ++l1) {
               boolean flag5 = isChunkInRange(k1, l1, k, l, this.viewDistance);
               boolean flag6 = isChunkInRange(k1, l1, i2, j2, this.viewDistance);
               this.updateChunkTracking(p_140185_, new ChunkPos(k1, l1), new MutableObject<>(), flag5, flag6);
            }
         }
      } else {
         for(int i1 = k - this.viewDistance - 1; i1 <= k + this.viewDistance + 1; ++i1) {
            for(int j1 = l - this.viewDistance - 1; j1 <= l + this.viewDistance + 1; ++j1) {
               if (isChunkInRange(i1, j1, k, l, this.viewDistance)) {
                  boolean flag3 = true;
                  boolean flag4 = false;
                  this.updateChunkTracking(p_140185_, new ChunkPos(i1, j1), new MutableObject<>(), true, false);
               }
            }
         }

         for(int k2 = i2 - this.viewDistance - 1; k2 <= i2 + this.viewDistance + 1; ++k2) {
            for(int i3 = j2 - this.viewDistance - 1; i3 <= j2 + this.viewDistance + 1; ++i3) {
               if (isChunkInRange(k2, i3, i2, j2, this.viewDistance)) {
                  boolean flag7 = false;
                  boolean flag8 = true;
                  this.updateChunkTracking(p_140185_, new ChunkPos(k2, i3), new MutableObject<>(), false, true);
               }
            }
         }
      }

   }

   public List<ServerPlayer> getPlayers(ChunkPos p_183801_, boolean p_183802_) {
      Set<ServerPlayer> set = this.playerMap.getPlayers(p_183801_.toLong());
      Builder<ServerPlayer> builder = ImmutableList.builder();

      for(ServerPlayer serverplayer : set) {
         SectionPos sectionpos = serverplayer.getLastSectionPos();
         if (p_183802_ && isChunkOnRangeBorder(p_183801_.x, p_183801_.z, sectionpos.x(), sectionpos.z(), this.viewDistance) || !p_183802_ && isChunkInRange(p_183801_.x, p_183801_.z, sectionpos.x(), sectionpos.z(), this.viewDistance)) {
            builder.add(serverplayer);
         }
      }

      return builder.build();
   }

   protected void addEntity(Entity p_140200_) {
      if (!(p_140200_ instanceof net.minecraftforge.entity.PartEntity)) {
         EntityType<?> entitytype = p_140200_.getType();
         int i = entitytype.clientTrackingRange() * 16;
         if (i != 0) {
            int j = entitytype.updateInterval();
            if (this.entityMap.containsKey(p_140200_.getId())) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Entity is already tracked!"));
            } else {
               ChunkMap.TrackedEntity chunkmap$trackedentity = new ChunkMap.TrackedEntity(p_140200_, i, j, entitytype.trackDeltas());
               this.entityMap.put(p_140200_.getId(), chunkmap$trackedentity);
               chunkmap$trackedentity.updatePlayers(this.level.players());
               if (p_140200_ instanceof ServerPlayer) {
                  ServerPlayer serverplayer = (ServerPlayer)p_140200_;
                  this.updatePlayerStatus(serverplayer, true);

                  for(ChunkMap.TrackedEntity chunkmap$trackedentity1 : this.entityMap.values()) {
                     if (chunkmap$trackedentity1.entity != serverplayer) {
                        chunkmap$trackedentity1.updatePlayer(serverplayer);
                     }
                  }
               }

            }
         }
      }
   }

   protected void removeEntity(Entity p_140332_) {
      if (p_140332_ instanceof ServerPlayer) {
         ServerPlayer serverplayer = (ServerPlayer)p_140332_;
         this.updatePlayerStatus(serverplayer, false);

         for(ChunkMap.TrackedEntity chunkmap$trackedentity : this.entityMap.values()) {
            chunkmap$trackedentity.removePlayer(serverplayer);
         }
      }

      ChunkMap.TrackedEntity chunkmap$trackedentity1 = this.entityMap.remove(p_140332_.getId());
      if (chunkmap$trackedentity1 != null) {
         chunkmap$trackedentity1.broadcastRemoved();
      }

   }

   protected void tick() {
      List<ServerPlayer> list = Lists.newArrayList();
      List<ServerPlayer> list1 = this.level.players();

      for(ChunkMap.TrackedEntity chunkmap$trackedentity : this.entityMap.values()) {
         SectionPos sectionpos = chunkmap$trackedentity.lastSectionPos;
         SectionPos sectionpos1 = SectionPos.of(chunkmap$trackedentity.entity);
         boolean flag = !Objects.equals(sectionpos, sectionpos1);
         if (flag) {
            chunkmap$trackedentity.updatePlayers(list1);
            Entity entity = chunkmap$trackedentity.entity;
            if (entity instanceof ServerPlayer) {
               list.add((ServerPlayer)entity);
            }

            chunkmap$trackedentity.lastSectionPos = sectionpos1;
         }

         if (flag || this.distanceManager.inEntityTickingRange(sectionpos1.chunk().toLong())) {
            chunkmap$trackedentity.serverEntity.sendChanges();
         }
      }

      if (!list.isEmpty()) {
         for(ChunkMap.TrackedEntity chunkmap$trackedentity1 : this.entityMap.values()) {
            chunkmap$trackedentity1.updatePlayers(list);
         }
      }

   }

   public void broadcast(Entity p_140202_, Packet<?> p_140203_) {
      ChunkMap.TrackedEntity chunkmap$trackedentity = this.entityMap.get(p_140202_.getId());
      if (chunkmap$trackedentity != null) {
         chunkmap$trackedentity.broadcast(p_140203_);
      }

   }

   protected void broadcastAndSend(Entity p_140334_, Packet<?> p_140335_) {
      ChunkMap.TrackedEntity chunkmap$trackedentity = this.entityMap.get(p_140334_.getId());
      if (chunkmap$trackedentity != null) {
         chunkmap$trackedentity.broadcastAndSend(p_140335_);
      }

   }

   private void playerLoadedChunk(ServerPlayer p_183761_, MutableObject<ClientboundLevelChunkWithLightPacket> p_183762_, LevelChunk p_183763_) {
      if (p_183762_.getValue() == null) {
         p_183762_.setValue(new ClientboundLevelChunkWithLightPacket(p_183763_, this.lightEngine, (BitSet)null, (BitSet)null, true));
      }

      p_183761_.trackChunk(p_183763_.getPos(), p_183762_.getValue());
      DebugPackets.sendPoiPacketsForChunk(this.level, p_183763_.getPos());
      List<Entity> list = Lists.newArrayList();
      List<Entity> list1 = Lists.newArrayList();

      for(ChunkMap.TrackedEntity chunkmap$trackedentity : this.entityMap.values()) {
         Entity entity = chunkmap$trackedentity.entity;
         if (entity != p_183761_ && entity.chunkPosition().equals(p_183763_.getPos())) {
            chunkmap$trackedentity.updatePlayer(p_183761_);
            if (entity instanceof Mob && ((Mob)entity).getLeashHolder() != null) {
               list.add(entity);
            }

            if (!entity.getPassengers().isEmpty()) {
               list1.add(entity);
            }
         }
      }

      if (!list.isEmpty()) {
         for(Entity entity1 : list) {
            p_183761_.connection.send(new ClientboundSetEntityLinkPacket(entity1, ((Mob)entity1).getLeashHolder()));
         }
      }

      if (!list1.isEmpty()) {
         for(Entity entity2 : list1) {
            p_183761_.connection.send(new ClientboundSetPassengersPacket(entity2));
         }
      }

   }

   protected PoiManager getPoiManager() {
      return this.poiManager;
   }

   public String getStorageName() {
      return this.storageName;
   }

   void onFullChunkStatusChange(ChunkPos p_143076_, ChunkHolder.FullChunkStatus p_143077_) {
      this.chunkStatusListener.onChunkStatusChange(p_143076_, p_143077_);
   }

   class DistanceManager extends net.minecraft.server.level.DistanceManager {
      protected DistanceManager(Executor p_140459_, Executor p_140460_) {
         super(p_140459_, p_140460_);
      }

      protected boolean isChunkToRemove(long p_140462_) {
         return ChunkMap.this.toDrop.contains(p_140462_);
      }

      @Nullable
      protected ChunkHolder getChunk(long p_140469_) {
         return ChunkMap.this.getUpdatingChunkIfPresent(p_140469_);
      }

      @Nullable
      protected ChunkHolder updateChunkScheduling(long p_140464_, int p_140465_, @Nullable ChunkHolder p_140466_, int p_140467_) {
         return ChunkMap.this.updateChunkScheduling(p_140464_, p_140465_, p_140466_, p_140467_);
      }
   }

   class TrackedEntity {
      final ServerEntity serverEntity;
      final Entity entity;
      private final int range;
      SectionPos lastSectionPos;
      private final Set<ServerPlayerConnection> seenBy = Sets.newIdentityHashSet();

      public TrackedEntity(Entity p_140478_, int p_140479_, int p_140480_, boolean p_140481_) {
         this.serverEntity = new ServerEntity(ChunkMap.this.level, p_140478_, p_140480_, p_140481_, this::broadcast);
         this.entity = p_140478_;
         this.range = p_140479_;
         this.lastSectionPos = SectionPos.of(p_140478_);
      }

      public boolean equals(Object p_140506_) {
         if (p_140506_ instanceof ChunkMap.TrackedEntity) {
            return ((ChunkMap.TrackedEntity)p_140506_).entity.getId() == this.entity.getId();
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.entity.getId();
      }

      public void broadcast(Packet<?> p_140490_) {
         for(ServerPlayerConnection serverplayerconnection : this.seenBy) {
            serverplayerconnection.send(p_140490_);
         }

      }

      public void broadcastAndSend(Packet<?> p_140500_) {
         this.broadcast(p_140500_);
         if (this.entity instanceof ServerPlayer) {
            ((ServerPlayer)this.entity).connection.send(p_140500_);
         }

      }

      public void broadcastRemoved() {
         for(ServerPlayerConnection serverplayerconnection : this.seenBy) {
            this.serverEntity.removePairing(serverplayerconnection.getPlayer());
         }

      }

      public void removePlayer(ServerPlayer p_140486_) {
         if (this.seenBy.remove(p_140486_.connection)) {
            this.serverEntity.removePairing(p_140486_);
         }

      }

      public void updatePlayer(ServerPlayer p_140498_) {
         if (p_140498_ != this.entity) {
            Vec3 vec3 = p_140498_.position().subtract(this.serverEntity.sentPos());
            double d0 = (double)Math.min(this.getEffectiveRange(), (ChunkMap.this.viewDistance - 1) * 16);
            double d1 = vec3.x * vec3.x + vec3.z * vec3.z;
            double d2 = d0 * d0;
            boolean flag = d1 <= d2 && this.entity.broadcastToPlayer(p_140498_);
            if (flag) {
               if (this.seenBy.add(p_140498_.connection)) {
                  this.serverEntity.addPairing(p_140498_);
               }
            } else if (this.seenBy.remove(p_140498_.connection)) {
               this.serverEntity.removePairing(p_140498_);
            }

         }
      }

      private int scaledRange(int p_140484_) {
         return ChunkMap.this.level.getServer().getScaledTrackingDistance(p_140484_);
      }

      private int getEffectiveRange() {
         int i = this.range;

         for(Entity entity : this.entity.getIndirectPassengers()) {
            int j = entity.getType().clientTrackingRange() * 16;
            if (j > i) {
               i = j;
            }
         }

         return this.scaledRange(i);
      }

      public void updatePlayers(List<ServerPlayer> p_140488_) {
         for(ServerPlayer serverplayer : p_140488_) {
            this.updatePlayer(serverplayer);
         }

      }
   }
}
