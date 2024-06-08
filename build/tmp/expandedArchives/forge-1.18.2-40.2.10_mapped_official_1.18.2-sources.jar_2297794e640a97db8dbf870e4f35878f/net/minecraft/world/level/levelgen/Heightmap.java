package net.minecraft.world.level.levelgen;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;

public class Heightmap {
   private static final Logger LOGGER = LogUtils.getLogger();
   static final Predicate<BlockState> NOT_AIR = (p_64263_) -> {
      return !p_64263_.isAir();
   };
   static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = (p_64255_) -> {
      return p_64255_.getMaterial().blocksMotion();
   };
   private final BitStorage data;
   private final Predicate<BlockState> isOpaque;
   private final ChunkAccess chunk;

   public Heightmap(ChunkAccess p_64237_, Heightmap.Types p_64238_) {
      this.isOpaque = p_64238_.isOpaque();
      this.chunk = p_64237_;
      int i = Mth.ceillog2(p_64237_.getHeight() + 1);
      this.data = new SimpleBitStorage(i, 256);
   }

   public static void primeHeightmaps(ChunkAccess p_64257_, Set<Heightmap.Types> p_64258_) {
      int i = p_64258_.size();
      ObjectList<Heightmap> objectlist = new ObjectArrayList<>(i);
      ObjectListIterator<Heightmap> objectlistiterator = objectlist.iterator();
      int j = p_64257_.getHighestSectionPosition() + 16;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

      for(int k = 0; k < 16; ++k) {
         for(int l = 0; l < 16; ++l) {
            for(Heightmap.Types heightmap$types : p_64258_) {
               objectlist.add(p_64257_.getOrCreateHeightmapUnprimed(heightmap$types));
            }

            for(int i1 = j - 1; i1 >= p_64257_.getMinBuildHeight(); --i1) {
               blockpos$mutableblockpos.set(k, i1, l);
               BlockState blockstate = p_64257_.getBlockState(blockpos$mutableblockpos);
               if (!blockstate.is(Blocks.AIR)) {
                  while(objectlistiterator.hasNext()) {
                     Heightmap heightmap = objectlistiterator.next();
                     if (heightmap.isOpaque.test(blockstate)) {
                        heightmap.setHeight(k, l, i1 + 1);
                        objectlistiterator.remove();
                     }
                  }

                  if (objectlist.isEmpty()) {
                     break;
                  }

                  objectlistiterator.back(i);
               }
            }
         }
      }

   }

   public boolean update(int p_64250_, int p_64251_, int p_64252_, BlockState p_64253_) {
      int i = this.getFirstAvailable(p_64250_, p_64252_);
      if (p_64251_ <= i - 2) {
         return false;
      } else {
         if (this.isOpaque.test(p_64253_)) {
            if (p_64251_ >= i) {
               this.setHeight(p_64250_, p_64252_, p_64251_ + 1);
               return true;
            }
         } else if (i - 1 == p_64251_) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for(int j = p_64251_ - 1; j >= this.chunk.getMinBuildHeight(); --j) {
               blockpos$mutableblockpos.set(p_64250_, j, p_64252_);
               if (this.isOpaque.test(this.chunk.getBlockState(blockpos$mutableblockpos))) {
                  this.setHeight(p_64250_, p_64252_, j + 1);
                  return true;
               }
            }

            this.setHeight(p_64250_, p_64252_, this.chunk.getMinBuildHeight());
            return true;
         }

         return false;
      }
   }

   public int getFirstAvailable(int p_64243_, int p_64244_) {
      return this.getFirstAvailable(getIndex(p_64243_, p_64244_));
   }

   public int getHighestTaken(int p_158369_, int p_158370_) {
      return this.getFirstAvailable(getIndex(p_158369_, p_158370_)) - 1;
   }

   private int getFirstAvailable(int p_64241_) {
      return this.data.get(p_64241_) + this.chunk.getMinBuildHeight();
   }

   private void setHeight(int p_64246_, int p_64247_, int p_64248_) {
      this.data.set(getIndex(p_64246_, p_64247_), p_64248_ - this.chunk.getMinBuildHeight());
   }

   public void setRawData(ChunkAccess p_158365_, Heightmap.Types p_158366_, long[] p_158367_) {
      long[] along = this.data.getRaw();
      if (along.length == p_158367_.length) {
         System.arraycopy(p_158367_, 0, along, 0, p_158367_.length);
      } else {
         LOGGER.warn("Ignoring heightmap data for chunk " + p_158365_.getPos() + ", size does not match; expected: " + along.length + ", got: " + p_158367_.length);
         primeHeightmaps(p_158365_, EnumSet.of(p_158366_));
      }
   }

   public long[] getRawData() {
      return this.data.getRaw();
   }

   private static int getIndex(int p_64266_, int p_64267_) {
      return p_64266_ + p_64267_ * 16;
   }

   public static enum Types implements StringRepresentable {
      WORLD_SURFACE_WG("WORLD_SURFACE_WG", Heightmap.Usage.WORLDGEN, Heightmap.NOT_AIR),
      WORLD_SURFACE("WORLD_SURFACE", Heightmap.Usage.CLIENT, Heightmap.NOT_AIR),
      OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Heightmap.Usage.WORLDGEN, Heightmap.MATERIAL_MOTION_BLOCKING),
      OCEAN_FLOOR("OCEAN_FLOOR", Heightmap.Usage.LIVE_WORLD, Heightmap.MATERIAL_MOTION_BLOCKING),
      MOTION_BLOCKING("MOTION_BLOCKING", Heightmap.Usage.CLIENT, (p_64296_) -> {
         return p_64296_.getMaterial().blocksMotion() || !p_64296_.getFluidState().isEmpty();
      }),
      MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Heightmap.Usage.LIVE_WORLD, (p_64289_) -> {
         return (p_64289_.getMaterial().blocksMotion() || !p_64289_.getFluidState().isEmpty()) && !(p_64289_.getBlock() instanceof LeavesBlock);
      });

      public static final Codec<Heightmap.Types> CODEC = StringRepresentable.fromEnum(Heightmap.Types::values, Heightmap.Types::getFromKey);
      private final String serializationKey;
      private final Heightmap.Usage usage;
      private final Predicate<BlockState> isOpaque;
      private static final Map<String, Heightmap.Types> REVERSE_LOOKUP = Util.make(Maps.newHashMap(), (p_64293_) -> {
         for(Heightmap.Types heightmap$types : values()) {
            p_64293_.put(heightmap$types.serializationKey, heightmap$types);
         }

      });

      private Types(String p_64284_, Heightmap.Usage p_64285_, Predicate<BlockState> p_64286_) {
         this.serializationKey = p_64284_;
         this.usage = p_64285_;
         this.isOpaque = p_64286_;
      }

      public String getSerializationKey() {
         return this.serializationKey;
      }

      public boolean sendToClient() {
         return this.usage == Heightmap.Usage.CLIENT;
      }

      public boolean keepAfterWorldgen() {
         return this.usage != Heightmap.Usage.WORLDGEN;
      }

      @Nullable
      public static Heightmap.Types getFromKey(String p_64291_) {
         return REVERSE_LOOKUP.get(p_64291_);
      }

      public Predicate<BlockState> isOpaque() {
         return this.isOpaque;
      }

      public String getSerializedName() {
         return this.serializationKey;
      }
   }

   public static enum Usage {
      WORLDGEN,
      LIVE_WORLD,
      CLIENT;
   }
}