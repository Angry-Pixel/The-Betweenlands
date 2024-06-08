package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ThreadFactory THREAD_FACTORY = (new ThreadFactoryBuilder()).setDaemon(true).build();
   private final WorldGenSettings worldGenSettings;
   private final boolean eraseCache;
   private final LevelStorageSource.LevelStorageAccess levelStorage;
   private final Thread thread;
   private final DataFixer dataFixer;
   private volatile boolean running = true;
   private volatile boolean finished;
   private volatile float progress;
   private volatile int totalChunks;
   private volatile int converted;
   private volatile int skipped;
   private final Object2FloatMap<ResourceKey<Level>> progressMap = Object2FloatMaps.synchronize(new Object2FloatOpenCustomHashMap<>(Util.identityStrategy()));
   private volatile Component status = new TranslatableComponent("optimizeWorld.stage.counting");
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final DimensionDataStorage overworldDataStorage;

   public WorldUpgrader(LevelStorageSource.LevelStorageAccess p_185927_, DataFixer p_185928_, WorldGenSettings p_185929_, boolean p_185930_) {
      this.worldGenSettings = p_185929_;
      this.eraseCache = p_185930_;
      this.dataFixer = p_185928_;
      this.levelStorage = p_185927_;
      this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data").toFile(), p_185928_);
      this.thread = THREAD_FACTORY.newThread(this::work);
      this.thread.setUncaughtExceptionHandler((p_18825_, p_18826_) -> {
         LOGGER.error("Error upgrading world", p_18826_);
         this.status = new TranslatableComponent("optimizeWorld.stage.failed");
         this.finished = true;
      });
      this.thread.start();
   }

   public void cancel() {
      this.running = false;

      try {
         this.thread.join();
      } catch (InterruptedException interruptedexception) {
      }

   }

   private void work() {
      this.totalChunks = 0;
      Builder<ResourceKey<Level>, ListIterator<ChunkPos>> builder = ImmutableMap.builder();
      ImmutableSet<ResourceKey<Level>> immutableset = this.worldGenSettings.levels();

      for(ResourceKey<Level> resourcekey : immutableset) {
         List<ChunkPos> list = this.getAllChunkPos(resourcekey);
         builder.put(resourcekey, list.listIterator());
         this.totalChunks += list.size();
      }

      if (this.totalChunks == 0) {
         this.finished = true;
      } else {
         float f1 = (float)this.totalChunks;
         ImmutableMap<ResourceKey<Level>, ListIterator<ChunkPos>> immutablemap = builder.build();
         Builder<ResourceKey<Level>, ChunkStorage> builder1 = ImmutableMap.builder();

         for(ResourceKey<Level> resourcekey1 : immutableset) {
            Path path = this.levelStorage.getDimensionPath(resourcekey1);
            builder1.put(resourcekey1, new ChunkStorage(path.resolve("region"), this.dataFixer, true));
         }

         ImmutableMap<ResourceKey<Level>, ChunkStorage> immutablemap1 = builder1.build();
         long i = Util.getMillis();
         this.status = new TranslatableComponent("optimizeWorld.stage.upgrading");

         while(this.running) {
            boolean flag = false;
            float f = 0.0F;

            for(ResourceKey<Level> resourcekey2 : immutableset) {
               ListIterator<ChunkPos> listiterator = immutablemap.get(resourcekey2);
               ChunkStorage chunkstorage = immutablemap1.get(resourcekey2);
               if (listiterator.hasNext()) {
                  ChunkPos chunkpos = listiterator.next();
                  boolean flag1 = false;

                  try {
                     CompoundTag compoundtag = chunkstorage.read(chunkpos);
                     if (compoundtag != null) {
                        int j = ChunkStorage.getVersion(compoundtag);
                        ChunkGenerator chunkgenerator = this.worldGenSettings.dimensions().get(WorldGenSettings.levelToLevelStem(resourcekey2)).generator();
                        CompoundTag compoundtag1 = chunkstorage.upgradeChunkTag(resourcekey2, () -> {
                           return this.overworldDataStorage;
                        }, compoundtag, chunkgenerator.getTypeNameForDataFixer());
                        ChunkPos chunkpos1 = new ChunkPos(compoundtag1.getInt("xPos"), compoundtag1.getInt("zPos"));
                        if (!chunkpos1.equals(chunkpos)) {
                           LOGGER.warn("Chunk {} has invalid position {}", chunkpos, chunkpos1);
                        }

                        boolean flag2 = j < SharedConstants.getCurrentVersion().getWorldVersion();
                        if (this.eraseCache) {
                           flag2 = flag2 || compoundtag1.contains("Heightmaps");
                           compoundtag1.remove("Heightmaps");
                           flag2 = flag2 || compoundtag1.contains("isLightOn");
                           compoundtag1.remove("isLightOn");
                        }

                        if (flag2) {
                           chunkstorage.write(chunkpos, compoundtag1);
                           flag1 = true;
                        }
                     }
                  } catch (ReportedException reportedexception) {
                     Throwable throwable = reportedexception.getCause();
                     if (!(throwable instanceof IOException)) {
                        throw reportedexception;
                     }

                     LOGGER.error("Error upgrading chunk {}", chunkpos, throwable);
                  } catch (IOException ioexception1) {
                     LOGGER.error("Error upgrading chunk {}", chunkpos, ioexception1);
                  }

                  if (flag1) {
                     ++this.converted;
                  } else {
                     ++this.skipped;
                  }

                  flag = true;
               }

               float f2 = (float)listiterator.nextIndex() / f1;
               this.progressMap.put(resourcekey2, f2);
               f += f2;
            }

            this.progress = f;
            if (!flag) {
               this.running = false;
            }
         }

         this.status = new TranslatableComponent("optimizeWorld.stage.finished");

         for(ChunkStorage chunkstorage1 : immutablemap1.values()) {
            try {
               chunkstorage1.close();
            } catch (IOException ioexception) {
               LOGGER.error("Error upgrading chunk", (Throwable)ioexception);
            }
         }

         this.overworldDataStorage.save();
         i = Util.getMillis() - i;
         LOGGER.info("World optimizaton finished after {} ms", (long)i);
         this.finished = true;
      }
   }

   private List<ChunkPos> getAllChunkPos(ResourceKey<Level> p_18831_) {
      File file1 = this.levelStorage.getDimensionPath(p_18831_).toFile();
      File file2 = new File(file1, "region");
      File[] afile = file2.listFiles((p_18822_, p_18823_) -> {
         return p_18823_.endsWith(".mca");
      });
      if (afile == null) {
         return ImmutableList.of();
      } else {
         List<ChunkPos> list = Lists.newArrayList();

         for(File file3 : afile) {
            Matcher matcher = REGEX.matcher(file3.getName());
            if (matcher.matches()) {
               int i = Integer.parseInt(matcher.group(1)) << 5;
               int j = Integer.parseInt(matcher.group(2)) << 5;

               try {
                  RegionFile regionfile = new RegionFile(file3.toPath(), file2.toPath(), true);

                  try {
                     for(int k = 0; k < 32; ++k) {
                        for(int l = 0; l < 32; ++l) {
                           ChunkPos chunkpos = new ChunkPos(k + i, l + j);
                           if (regionfile.doesChunkExist(chunkpos)) {
                              list.add(chunkpos);
                           }
                        }
                     }
                  } catch (Throwable throwable1) {
                     try {
                        regionfile.close();
                     } catch (Throwable throwable) {
                        throwable1.addSuppressed(throwable);
                     }

                     throw throwable1;
                  }

                  regionfile.close();
               } catch (Throwable throwable2) {
               }
            }
         }

         return list;
      }
   }

   public boolean isFinished() {
      return this.finished;
   }

   public ImmutableSet<ResourceKey<Level>> levels() {
      return this.worldGenSettings.levels();
   }

   public float dimensionProgress(ResourceKey<Level> p_18828_) {
      return this.progressMap.getFloat(p_18828_);
   }

   public float getProgress() {
      return this.progress;
   }

   public int getTotalChunks() {
      return this.totalChunks;
   }

   public int getConverted() {
      return this.converted;
   }

   public int getSkipped() {
      return this.skipped;
   }

   public Component getStatus() {
      return this.status;
   }
}