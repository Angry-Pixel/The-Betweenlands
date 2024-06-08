package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CarvingMask {
   private final int minY;
   private final BitSet mask;
   private CarvingMask.Mask additionalMask = (p_196713_, p_196714_, p_196715_) -> {
      return false;
   };

   public CarvingMask(int p_187579_, int p_187580_) {
      this.minY = p_187580_;
      this.mask = new BitSet(256 * p_187579_);
   }

   public void setAdditionalMask(CarvingMask.Mask p_196711_) {
      this.additionalMask = p_196711_;
   }

   public CarvingMask(long[] p_187582_, int p_187583_) {
      this.minY = p_187583_;
      this.mask = BitSet.valueOf(p_187582_);
   }

   private int getIndex(int p_187599_, int p_187600_, int p_187601_) {
      return p_187599_ & 15 | (p_187601_ & 15) << 4 | p_187600_ - this.minY << 8;
   }

   public void set(int p_187586_, int p_187587_, int p_187588_) {
      this.mask.set(this.getIndex(p_187586_, p_187587_, p_187588_));
   }

   public boolean get(int p_187595_, int p_187596_, int p_187597_) {
      return this.additionalMask.test(p_187595_, p_187596_, p_187597_) || this.mask.get(this.getIndex(p_187595_, p_187596_, p_187597_));
   }

   public Stream<BlockPos> stream(ChunkPos p_187590_) {
      return this.mask.stream().mapToObj((p_196709_) -> {
         int i = p_196709_ & 15;
         int j = p_196709_ >> 4 & 15;
         int k = p_196709_ >> 8;
         return p_187590_.getBlockAt(i, k + this.minY, j);
      });
   }

   public long[] toArray() {
      return this.mask.toLongArray();
   }

   public interface Mask {
      boolean test(int p_196717_, int p_196718_, int p_196719_);
   }
}