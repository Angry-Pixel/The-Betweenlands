package net.minecraft.world.level;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class ChunkPos {
   private static final int SAFETY_MARGIN = 1056;
   public static final long INVALID_CHUNK_POS = asLong(1875066, 1875066);
   public static final ChunkPos ZERO = new ChunkPos(0, 0);
   private static final long COORD_BITS = 32L;
   private static final long COORD_MASK = 4294967295L;
   private static final int REGION_BITS = 5;
   private static final int REGION_MASK = 31;
   public final int x;
   public final int z;
   private static final int HASH_A = 1664525;
   private static final int HASH_C = 1013904223;
   private static final int HASH_Z_XOR = -559038737;

   public ChunkPos(int p_45582_, int p_45583_) {
      this.x = p_45582_;
      this.z = p_45583_;
   }

   public ChunkPos(BlockPos p_45587_) {
      this.x = SectionPos.blockToSectionCoord(p_45587_.getX());
      this.z = SectionPos.blockToSectionCoord(p_45587_.getZ());
   }

   public ChunkPos(long p_45585_) {
      this.x = (int)p_45585_;
      this.z = (int)(p_45585_ >> 32);
   }

   public long toLong() {
      return asLong(this.x, this.z);
   }

   public static long asLong(int p_45590_, int p_45591_) {
      return (long)p_45590_ & 4294967295L | ((long)p_45591_ & 4294967295L) << 32;
   }

   public static long asLong(BlockPos p_151389_) {
      return asLong(SectionPos.blockToSectionCoord(p_151389_.getX()), SectionPos.blockToSectionCoord(p_151389_.getZ()));
   }

   public static int getX(long p_45593_) {
      return (int)(p_45593_ & 4294967295L);
   }

   public static int getZ(long p_45603_) {
      return (int)(p_45603_ >>> 32 & 4294967295L);
   }

   public int hashCode() {
      int i = 1664525 * this.x + 1013904223;
      int j = 1664525 * (this.z ^ -559038737) + 1013904223;
      return i ^ j;
   }

   public boolean equals(Object p_45607_) {
      if (this == p_45607_) {
         return true;
      } else if (!(p_45607_ instanceof ChunkPos)) {
         return false;
      } else {
         ChunkPos chunkpos = (ChunkPos)p_45607_;
         return this.x == chunkpos.x && this.z == chunkpos.z;
      }
   }

   public int getMiddleBlockX() {
      return this.getBlockX(8);
   }

   public int getMiddleBlockZ() {
      return this.getBlockZ(8);
   }

   public int getMinBlockX() {
      return SectionPos.sectionToBlockCoord(this.x);
   }

   public int getMinBlockZ() {
      return SectionPos.sectionToBlockCoord(this.z);
   }

   public int getMaxBlockX() {
      return this.getBlockX(15);
   }

   public int getMaxBlockZ() {
      return this.getBlockZ(15);
   }

   public int getRegionX() {
      return this.x >> 5;
   }

   public int getRegionZ() {
      return this.z >> 5;
   }

   public int getRegionLocalX() {
      return this.x & 31;
   }

   public int getRegionLocalZ() {
      return this.z & 31;
   }

   public BlockPos getBlockAt(int p_151385_, int p_151386_, int p_151387_) {
      return new BlockPos(this.getBlockX(p_151385_), p_151386_, this.getBlockZ(p_151387_));
   }

   public int getBlockX(int p_151383_) {
      return SectionPos.sectionToBlockCoord(this.x, p_151383_);
   }

   public int getBlockZ(int p_151392_) {
      return SectionPos.sectionToBlockCoord(this.z, p_151392_);
   }

   public BlockPos getMiddleBlockPosition(int p_151395_) {
      return new BlockPos(this.getMiddleBlockX(), p_151395_, this.getMiddleBlockZ());
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public BlockPos getWorldPosition() {
      return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
   }

   public int getChessboardDistance(ChunkPos p_45595_) {
      return Math.max(Math.abs(this.x - p_45595_.x), Math.abs(this.z - p_45595_.z));
   }

   public static Stream<ChunkPos> rangeClosed(ChunkPos p_45597_, int p_45598_) {
      return rangeClosed(new ChunkPos(p_45597_.x - p_45598_, p_45597_.z - p_45598_), new ChunkPos(p_45597_.x + p_45598_, p_45597_.z + p_45598_));
   }

   public static Stream<ChunkPos> rangeClosed(final ChunkPos p_45600_, final ChunkPos p_45601_) {
      int i = Math.abs(p_45600_.x - p_45601_.x) + 1;
      int j = Math.abs(p_45600_.z - p_45601_.z) + 1;
      final int k = p_45600_.x < p_45601_.x ? 1 : -1;
      final int l = p_45600_.z < p_45601_.z ? 1 : -1;
      return StreamSupport.stream(new AbstractSpliterator<ChunkPos>((long)(i * j), 64) {
         @Nullable
         private ChunkPos pos;

         public boolean tryAdvance(Consumer<? super ChunkPos> p_45630_) {
            if (this.pos == null) {
               this.pos = p_45600_;
            } else {
               int i1 = this.pos.x;
               int j1 = this.pos.z;
               if (i1 == p_45601_.x) {
                  if (j1 == p_45601_.z) {
                     return false;
                  }

                  this.pos = new ChunkPos(p_45600_.x, j1 + l);
               } else {
                  this.pos = new ChunkPos(i1 + k, j1);
               }
            }

            p_45630_.accept(this.pos);
            return true;
         }
      }, false);
   }
}