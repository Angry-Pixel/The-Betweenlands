package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SectionStorage;

public class PoiManager extends SectionStorage<PoiSection> {
   public static final int MAX_VILLAGE_DISTANCE = 6;
   public static final int VILLAGE_SECTION_SIZE = 1;
   private final PoiManager.DistanceTracker distanceTracker;
   private final LongSet loadedChunks = new LongOpenHashSet();

   public PoiManager(Path p_196651_, DataFixer p_196652_, boolean p_196653_, LevelHeightAccessor p_196654_) {
      super(p_196651_, PoiSection::codec, PoiSection::new, p_196652_, DataFixTypes.POI_CHUNK, p_196653_, p_196654_);
      this.distanceTracker = new PoiManager.DistanceTracker();
   }

   public void add(BlockPos p_27086_, PoiType p_27087_) {
      this.getOrCreate(SectionPos.asLong(p_27086_)).add(p_27086_, p_27087_);
   }

   public void remove(BlockPos p_27080_) {
      this.getOrLoad(SectionPos.asLong(p_27080_)).ifPresent((p_148657_) -> {
         p_148657_.remove(p_27080_);
      });
   }

   public long getCountInRange(Predicate<PoiType> p_27122_, BlockPos p_27123_, int p_27124_, PoiManager.Occupancy p_27125_) {
      return this.getInRange(p_27122_, p_27123_, p_27124_, p_27125_).count();
   }

   public boolean existsAtPosition(PoiType p_27045_, BlockPos p_27046_) {
      return this.exists(p_27046_, p_27045_::equals);
   }

   public Stream<PoiRecord> getInSquare(Predicate<PoiType> p_27167_, BlockPos p_27168_, int p_27169_, PoiManager.Occupancy p_27170_) {
      int i = Math.floorDiv(p_27169_, 16) + 1;
      return ChunkPos.rangeClosed(new ChunkPos(p_27168_), i).flatMap((p_148616_) -> {
         return this.getInChunk(p_27167_, p_148616_, p_27170_);
      }).filter((p_148635_) -> {
         BlockPos blockpos = p_148635_.getPos();
         return Math.abs(blockpos.getX() - p_27168_.getX()) <= p_27169_ && Math.abs(blockpos.getZ() - p_27168_.getZ()) <= p_27169_;
      });
   }

   public Stream<PoiRecord> getInRange(Predicate<PoiType> p_27182_, BlockPos p_27183_, int p_27184_, PoiManager.Occupancy p_27185_) {
      int i = p_27184_ * p_27184_;
      return this.getInSquare(p_27182_, p_27183_, p_27184_, p_27185_).filter((p_148598_) -> {
         return p_148598_.getPos().distSqr(p_27183_) <= (double)i;
      });
   }

   @VisibleForDebug
   public Stream<PoiRecord> getInChunk(Predicate<PoiType> p_27118_, ChunkPos p_27119_, PoiManager.Occupancy p_27120_) {
      return IntStream.range(this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).boxed().map((p_148578_) -> {
         return this.getOrLoad(SectionPos.of(p_27119_, p_148578_).asLong());
      }).filter(Optional::isPresent).flatMap((p_148620_) -> {
         return p_148620_.get().getRecords(p_27118_, p_27120_);
      });
   }

   public Stream<BlockPos> findAll(Predicate<PoiType> p_27139_, Predicate<BlockPos> p_27140_, BlockPos p_27141_, int p_27142_, PoiManager.Occupancy p_27143_) {
      return this.getInRange(p_27139_, p_27141_, p_27142_, p_27143_).map(PoiRecord::getPos).filter(p_27140_);
   }

   public Stream<BlockPos> findAllClosestFirst(Predicate<PoiType> p_27172_, Predicate<BlockPos> p_27173_, BlockPos p_27174_, int p_27175_, PoiManager.Occupancy p_27176_) {
      return this.findAll(p_27172_, p_27173_, p_27174_, p_27175_, p_27176_).sorted(Comparator.comparingDouble((p_148652_) -> {
         return p_148652_.distSqr(p_27174_);
      }));
   }

   public Optional<BlockPos> find(Predicate<PoiType> p_27187_, Predicate<BlockPos> p_27188_, BlockPos p_27189_, int p_27190_, PoiManager.Occupancy p_27191_) {
      return this.findAll(p_27187_, p_27188_, p_27189_, p_27190_, p_27191_).findFirst();
   }

   public Optional<BlockPos> findClosest(Predicate<PoiType> p_27193_, BlockPos p_27194_, int p_27195_, PoiManager.Occupancy p_27196_) {
      return this.getInRange(p_27193_, p_27194_, p_27195_, p_27196_).map(PoiRecord::getPos).min(Comparator.comparingDouble((p_148641_) -> {
         return p_148641_.distSqr(p_27194_);
      }));
   }

   public Optional<BlockPos> findClosest(Predicate<PoiType> p_148659_, Predicate<BlockPos> p_148660_, BlockPos p_148661_, int p_148662_, PoiManager.Occupancy p_148663_) {
      return this.getInRange(p_148659_, p_148661_, p_148662_, p_148663_).map(PoiRecord::getPos).filter(p_148660_).min(Comparator.comparingDouble((p_148604_) -> {
         return p_148604_.distSqr(p_148661_);
      }));
   }

   public Optional<BlockPos> take(Predicate<PoiType> p_27134_, Predicate<BlockPos> p_27135_, BlockPos p_27136_, int p_27137_) {
      return this.getInRange(p_27134_, p_27136_, p_27137_, PoiManager.Occupancy.HAS_SPACE).filter((p_148646_) -> {
         return p_27135_.test(p_148646_.getPos());
      }).findFirst().map((p_148573_) -> {
         p_148573_.acquireTicket();
         return p_148573_.getPos();
      });
   }

   public Optional<BlockPos> getRandom(Predicate<PoiType> p_27127_, Predicate<BlockPos> p_27128_, PoiManager.Occupancy p_27129_, BlockPos p_27130_, int p_27131_, Random p_27132_) {
      List<PoiRecord> list = this.getInRange(p_27127_, p_27130_, p_27131_, p_27129_).collect(Collectors.toList());
      Collections.shuffle(list, p_27132_);
      return list.stream().filter((p_148623_) -> {
         return p_27128_.test(p_148623_.getPos());
      }).findFirst().map(PoiRecord::getPos);
   }

   public boolean release(BlockPos p_27155_) {
      return this.getOrLoad(SectionPos.asLong(p_27155_)).map((p_148649_) -> {
         return p_148649_.release(p_27155_);
      }).orElseThrow(() -> {
         return Util.pauseInIde(new IllegalStateException("POI never registered at " + p_27155_));
      });
   }

   public boolean exists(BlockPos p_27092_, Predicate<PoiType> p_27093_) {
      return this.getOrLoad(SectionPos.asLong(p_27092_)).map((p_148608_) -> {
         return p_148608_.exists(p_27092_, p_27093_);
      }).orElse(false);
   }

   public Optional<PoiType> getType(BlockPos p_27178_) {
      return this.getOrLoad(SectionPos.asLong(p_27178_)).flatMap((p_148638_) -> {
         return p_148638_.getType(p_27178_);
      });
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public int getFreeTickets(BlockPos p_148654_) {
      return this.getOrLoad(SectionPos.asLong(p_148654_)).map((p_148601_) -> {
         return p_148601_.getFreeTickets(p_148654_);
      }).orElse(0);
   }

   public int sectionsToVillage(SectionPos p_27099_) {
      this.distanceTracker.runAllUpdates();
      return this.distanceTracker.getLevel(p_27099_.asLong());
   }

   boolean isVillageCenter(long p_27198_) {
      Optional<PoiSection> optional = this.get(p_27198_);
      return optional == null ? false : optional.map((p_148575_) -> {
         return p_148575_.getRecords(PoiType.ALL, PoiManager.Occupancy.IS_OCCUPIED).count() > 0L;
      }).orElse(false);
   }

   public void tick(BooleanSupplier p_27105_) {
      super.tick(p_27105_);
      this.distanceTracker.runAllUpdates();
   }

   protected void setDirty(long p_27036_) {
      super.setDirty(p_27036_);
      this.distanceTracker.update(p_27036_, this.distanceTracker.getLevelFromSource(p_27036_), false);
   }

   protected void onSectionLoad(long p_27145_) {
      this.distanceTracker.update(p_27145_, this.distanceTracker.getLevelFromSource(p_27145_), false);
   }

   public void checkConsistencyWithBlocks(ChunkPos p_27048_, LevelChunkSection p_27049_) {
      SectionPos sectionpos = SectionPos.of(p_27048_, SectionPos.blockToSectionCoord(p_27049_.bottomBlockY()));
      Util.ifElse(this.getOrLoad(sectionpos.asLong()), (p_148588_) -> {
         p_148588_.refresh((p_148629_) -> {
            if (mayHavePoi(p_27049_)) {
               this.updateFromSection(p_27049_, sectionpos, p_148629_);
            }

         });
      }, () -> {
         if (mayHavePoi(p_27049_)) {
            PoiSection poisection = this.getOrCreate(sectionpos.asLong());
            this.updateFromSection(p_27049_, sectionpos, poisection::add);
         }

      });
   }

   private static boolean mayHavePoi(LevelChunkSection p_27061_) {
      return p_27061_.maybeHas(PoiType.ALL_STATES::contains);
   }

   private void updateFromSection(LevelChunkSection p_27070_, SectionPos p_27071_, BiConsumer<BlockPos, PoiType> p_27072_) {
      p_27071_.blocksInside().forEach((p_148592_) -> {
         BlockState blockstate = p_27070_.getBlockState(SectionPos.sectionRelative(p_148592_.getX()), SectionPos.sectionRelative(p_148592_.getY()), SectionPos.sectionRelative(p_148592_.getZ()));
         PoiType.forState(blockstate).ifPresent((p_148612_) -> {
            p_27072_.accept(p_148592_, p_148612_);
         });
      });
   }

   public void ensureLoadedAndValid(LevelReader p_27057_, BlockPos p_27058_, int p_27059_) {
      SectionPos.aroundChunk(new ChunkPos(p_27058_), Math.floorDiv(p_27059_, 16), this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection()).map((p_148643_) -> {
         return Pair.of(p_148643_, this.getOrLoad(p_148643_.asLong()));
      }).filter((p_148631_) -> {
         return !p_148631_.getSecond().map(PoiSection::isValid).orElse(false);
      }).map((p_148594_) -> {
         return p_148594_.getFirst().chunk();
      }).filter((p_148625_) -> {
         return this.loadedChunks.add(p_148625_.toLong());
      }).forEach((p_148581_) -> {
         p_27057_.getChunk(p_148581_.x, p_148581_.z, ChunkStatus.EMPTY);
      });
   }

   final class DistanceTracker extends SectionTracker {
      private final Long2ByteMap levels = new Long2ByteOpenHashMap();

      protected DistanceTracker() {
         super(7, 16, 256);
         this.levels.defaultReturnValue((byte)7);
      }

      protected int getLevelFromSource(long p_27208_) {
         return PoiManager.this.isVillageCenter(p_27208_) ? 0 : 7;
      }

      protected int getLevel(long p_27210_) {
         return this.levels.get(p_27210_);
      }

      protected void setLevel(long p_27205_, int p_27206_) {
         if (p_27206_ > 6) {
            this.levels.remove(p_27205_);
         } else {
            this.levels.put(p_27205_, (byte)p_27206_);
         }

      }

      public void runAllUpdates() {
         super.runUpdates(Integer.MAX_VALUE);
      }
   }

   public static enum Occupancy {
      HAS_SPACE(PoiRecord::hasSpace),
      IS_OCCUPIED(PoiRecord::isOccupied),
      ANY((p_27223_) -> {
         return true;
      });

      private final Predicate<? super PoiRecord> test;

      private Occupancy(Predicate<? super PoiRecord> p_27220_) {
         this.test = p_27220_;
      }

      public Predicate<? super PoiRecord> getTest() {
         return this.test;
      }
   }
}