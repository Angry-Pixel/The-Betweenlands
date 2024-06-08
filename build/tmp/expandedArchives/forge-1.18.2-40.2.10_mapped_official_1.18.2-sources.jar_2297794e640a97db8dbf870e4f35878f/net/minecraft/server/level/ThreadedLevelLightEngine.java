package net.minecraft.server.level;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.thread.ProcessorHandle;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.slf4j.Logger;

public class ThreadedLevelLightEngine extends LevelLightEngine implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ProcessorMailbox<Runnable> taskMailbox;
   private final ObjectList<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> lightTasks = new ObjectArrayList<>();
   private final ChunkMap chunkMap;
   private final ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> sorterMailbox;
   private volatile int taskPerBatch = 5;
   private final AtomicBoolean scheduled = new AtomicBoolean();

   public ThreadedLevelLightEngine(LightChunkGetter p_9305_, ChunkMap p_9306_, boolean p_9307_, ProcessorMailbox<Runnable> p_9308_, ProcessorHandle<ChunkTaskPriorityQueueSorter.Message<Runnable>> p_9309_) {
      super(p_9305_, true, p_9307_);
      this.chunkMap = p_9306_;
      this.sorterMailbox = p_9309_;
      this.taskMailbox = p_9308_;
   }

   public void close() {
   }

   public int runUpdates(int p_9324_, boolean p_9325_, boolean p_9326_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
   }

   public void onBlockEmissionIncrease(BlockPos p_9359_, int p_9360_) {
      throw (UnsupportedOperationException)Util.pauseInIde(new UnsupportedOperationException("Ran automatically on a different thread!"));
   }

   public void checkBlock(BlockPos p_9357_) {
      BlockPos blockpos = p_9357_.immutable();
      this.addTask(SectionPos.blockToSectionCoord(p_9357_.getX()), SectionPos.blockToSectionCoord(p_9357_.getZ()), ThreadedLevelLightEngine.TaskType.POST_UPDATE, Util.name(() -> {
         super.checkBlock(blockpos);
      }, () -> {
         return "checkBlock " + blockpos;
      }));
   }

   protected void updateChunkStatus(ChunkPos p_9331_) {
      this.addTask(p_9331_.x, p_9331_.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(p_9331_, false);
         super.enableLightSources(p_9331_, false);

         for(int i = this.getMinLightSection(); i < this.getMaxLightSection(); ++i) {
            super.queueSectionData(LightLayer.BLOCK, SectionPos.of(p_9331_, i), (DataLayer)null, true);
            super.queueSectionData(LightLayer.SKY, SectionPos.of(p_9331_, i), (DataLayer)null, true);
         }

         for(int j = this.levelHeightAccessor.getMinSection(); j < this.levelHeightAccessor.getMaxSection(); ++j) {
            super.updateSectionStatus(SectionPos.of(p_9331_, j), true);
         }

      }, () -> {
         return "updateChunkStatus " + p_9331_ + " true";
      }));
   }

   public void updateSectionStatus(SectionPos p_9364_, boolean p_9365_) {
      this.addTask(p_9364_.x(), p_9364_.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.updateSectionStatus(p_9364_, p_9365_);
      }, () -> {
         return "updateSectionStatus " + p_9364_ + " " + p_9365_;
      }));
   }

   public void enableLightSources(ChunkPos p_9336_, boolean p_9337_) {
      this.addTask(p_9336_.x, p_9336_.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.enableLightSources(p_9336_, p_9337_);
      }, () -> {
         return "enableLight " + p_9336_ + " " + p_9337_;
      }));
   }

   public void queueSectionData(LightLayer p_9339_, SectionPos p_9340_, @Nullable DataLayer p_9341_, boolean p_9342_) {
      this.addTask(p_9340_.x(), p_9340_.z(), () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.queueSectionData(p_9339_, p_9340_, p_9341_, p_9342_);
      }, () -> {
         return "queueData " + p_9340_;
      }));
   }

   private void addTask(int p_9313_, int p_9314_, ThreadedLevelLightEngine.TaskType p_9315_, Runnable p_9316_) {
      this.addTask(p_9313_, p_9314_, this.chunkMap.getChunkQueueLevel(ChunkPos.asLong(p_9313_, p_9314_)), p_9315_, p_9316_);
   }

   private void addTask(int p_9318_, int p_9319_, IntSupplier p_9320_, ThreadedLevelLightEngine.TaskType p_9321_, Runnable p_9322_) {
      this.sorterMailbox.tell(ChunkTaskPriorityQueueSorter.message(() -> {
         this.lightTasks.add(Pair.of(p_9321_, p_9322_));
         if (this.lightTasks.size() >= this.taskPerBatch) {
            this.runUpdate();
         }

      }, ChunkPos.asLong(p_9318_, p_9319_), p_9320_));
   }

   public void retainData(ChunkPos p_9370_, boolean p_9371_) {
      this.addTask(p_9370_.x, p_9370_.z, () -> {
         return 0;
      }, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         super.retainData(p_9370_, p_9371_);
      }, () -> {
         return "retainData " + p_9370_;
      }));
   }

   public CompletableFuture<ChunkAccess> lightChunk(ChunkAccess p_9354_, boolean p_9355_) {
      ChunkPos chunkpos = p_9354_.getPos();
      p_9354_.setLightCorrect(false);
      this.addTask(chunkpos.x, chunkpos.z, ThreadedLevelLightEngine.TaskType.PRE_UPDATE, Util.name(() -> {
         LevelChunkSection[] alevelchunksection = p_9354_.getSections();

         for(int i = 0; i < p_9354_.getSectionsCount(); ++i) {
            LevelChunkSection levelchunksection = alevelchunksection[i];
            if (!levelchunksection.hasOnlyAir()) {
               int j = this.levelHeightAccessor.getSectionYFromSectionIndex(i);
               super.updateSectionStatus(SectionPos.of(chunkpos, j), false);
            }
         }

         super.enableLightSources(chunkpos, true);
         if (!p_9355_) {
            p_9354_.getLights().forEach((p_143477_) -> {
               super.onBlockEmissionIncrease(p_143477_, p_9354_.getLightEmission(p_143477_));
            });
         }

      }, () -> {
         return "lightChunk " + chunkpos + " " + p_9355_;
      }));
      return CompletableFuture.supplyAsync(() -> {
         p_9354_.setLightCorrect(true);
         super.retainData(chunkpos, false);
         this.chunkMap.releaseLightTicket(chunkpos);
         return p_9354_;
      }, (p_9334_) -> {
         this.addTask(chunkpos.x, chunkpos.z, ThreadedLevelLightEngine.TaskType.POST_UPDATE, p_9334_);
      });
   }

   public void tryScheduleUpdate() {
      if ((!this.lightTasks.isEmpty() || super.hasLightWork()) && this.scheduled.compareAndSet(false, true)) {
         this.taskMailbox.tell(() -> {
            this.runUpdate();
            this.scheduled.set(false);
         });
      }

   }

   private void runUpdate() {
      int i = Math.min(this.lightTasks.size(), this.taskPerBatch);
      ObjectListIterator<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> objectlistiterator = this.lightTasks.iterator();

      int j;
      for(j = 0; objectlistiterator.hasNext() && j < i; ++j) {
         Pair<ThreadedLevelLightEngine.TaskType, Runnable> pair = objectlistiterator.next();
         if (pair.getFirst() == ThreadedLevelLightEngine.TaskType.PRE_UPDATE) {
            pair.getSecond().run();
         }
      }

      objectlistiterator.back(j);
      super.runUpdates(Integer.MAX_VALUE, true, true);

      for(int k = 0; objectlistiterator.hasNext() && k < i; ++k) {
         Pair<ThreadedLevelLightEngine.TaskType, Runnable> pair1 = objectlistiterator.next();
         if (pair1.getFirst() == ThreadedLevelLightEngine.TaskType.POST_UPDATE) {
            pair1.getSecond().run();
         }

         objectlistiterator.remove();
      }

   }

   public void setTaskPerBatch(int p_9311_) {
      this.taskPerBatch = p_9311_;
   }

   static enum TaskType {
      PRE_UPDATE,
      POST_UPDATE;
   }
}