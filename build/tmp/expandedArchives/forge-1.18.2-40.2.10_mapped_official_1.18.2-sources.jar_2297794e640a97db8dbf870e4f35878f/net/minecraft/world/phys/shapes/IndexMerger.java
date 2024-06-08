package net.minecraft.world.phys.shapes;

import it.unimi.dsi.fastutil.doubles.DoubleList;

interface IndexMerger {
   DoubleList getList();

   boolean forMergedIndexes(IndexMerger.IndexConsumer p_82907_);

   int size();

   public interface IndexConsumer {
      boolean merge(int p_82909_, int p_82910_, int p_82911_);
   }
}