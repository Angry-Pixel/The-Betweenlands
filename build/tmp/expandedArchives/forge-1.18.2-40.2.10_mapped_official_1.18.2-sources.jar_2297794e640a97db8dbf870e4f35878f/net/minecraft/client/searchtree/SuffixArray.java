package net.minecraft.client.searchtree;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class SuffixArray<T> {
   private static final boolean DEBUG_COMPARISONS = Boolean.parseBoolean(System.getProperty("SuffixArray.printComparisons", "false"));
   private static final boolean DEBUG_ARRAY = Boolean.parseBoolean(System.getProperty("SuffixArray.printArray", "false"));
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int END_OF_TEXT_MARKER = -1;
   private static final int END_OF_DATA = -2;
   protected final List<T> list = Lists.newArrayList();
   private final IntList chars = new IntArrayList();
   private final IntList wordStarts = new IntArrayList();
   private IntList suffixToT = new IntArrayList();
   private IntList offsets = new IntArrayList();
   private int maxStringLength;

   public void add(T p_119971_, String p_119972_) {
      this.maxStringLength = Math.max(this.maxStringLength, p_119972_.length());
      int i = this.list.size();
      this.list.add(p_119971_);
      this.wordStarts.add(this.chars.size());

      for(int j = 0; j < p_119972_.length(); ++j) {
         this.suffixToT.add(i);
         this.offsets.add(j);
         this.chars.add(p_119972_.charAt(j));
      }

      this.suffixToT.add(i);
      this.offsets.add(p_119972_.length());
      this.chars.add(-1);
   }

   public void generate() {
      int i = this.chars.size();
      int[] aint = new int[i];
      int[] aint1 = new int[i];
      int[] aint2 = new int[i];
      int[] aint3 = new int[i];
      IntComparator intcomparator = (p_194458_, p_194459_) -> {
         return aint1[p_194458_] == aint1[p_194459_] ? Integer.compare(aint2[p_194458_], aint2[p_194459_]) : Integer.compare(aint1[p_194458_], aint1[p_194459_]);
      };
      Swapper swapper = (p_194464_, p_194465_) -> {
         if (p_194464_ != p_194465_) {
            int i2 = aint1[p_194464_];
            aint1[p_194464_] = aint1[p_194465_];
            aint1[p_194465_] = i2;
            i2 = aint2[p_194464_];
            aint2[p_194464_] = aint2[p_194465_];
            aint2[p_194465_] = i2;
            i2 = aint3[p_194464_];
            aint3[p_194464_] = aint3[p_194465_];
            aint3[p_194465_] = i2;
         }

      };

      for(int j = 0; j < i; ++j) {
         aint[j] = this.chars.getInt(j);
      }

      int k1 = 1;

      for(int k = Math.min(i, this.maxStringLength); k1 * 2 < k; k1 *= 2) {
         for(int l = 0; l < i; aint3[l] = l++) {
            aint1[l] = aint[l];
            aint2[l] = l + k1 < i ? aint[l + k1] : -2;
         }

         Arrays.quickSort(0, i, intcomparator, swapper);

         for(int l1 = 0; l1 < i; ++l1) {
            if (l1 > 0 && aint1[l1] == aint1[l1 - 1] && aint2[l1] == aint2[l1 - 1]) {
               aint[aint3[l1]] = aint[aint3[l1 - 1]];
            } else {
               aint[aint3[l1]] = l1;
            }
         }
      }

      IntList intlist1 = this.suffixToT;
      IntList intlist = this.offsets;
      this.suffixToT = new IntArrayList(intlist1.size());
      this.offsets = new IntArrayList(intlist.size());

      for(int i1 = 0; i1 < i; ++i1) {
         int j1 = aint3[i1];
         this.suffixToT.add(intlist1.getInt(j1));
         this.offsets.add(intlist.getInt(j1));
      }

      if (DEBUG_ARRAY) {
         this.print();
      }

   }

   private void print() {
      for(int i = 0; i < this.suffixToT.size(); ++i) {
         LOGGER.debug("{} {}", i, this.getString(i));
      }

      LOGGER.debug("");
   }

   private String getString(int p_119969_) {
      int i = this.offsets.getInt(p_119969_);
      int j = this.wordStarts.getInt(this.suffixToT.getInt(p_119969_));
      StringBuilder stringbuilder = new StringBuilder();

      for(int k = 0; j + k < this.chars.size(); ++k) {
         if (k == i) {
            stringbuilder.append('^');
         }

         int l = this.chars.getInt(j + k);
         if (l == -1) {
            break;
         }

         stringbuilder.append((char)l);
      }

      return stringbuilder.toString();
   }

   private int compare(String p_119976_, int p_119977_) {
      int i = this.wordStarts.getInt(this.suffixToT.getInt(p_119977_));
      int j = this.offsets.getInt(p_119977_);

      for(int k = 0; k < p_119976_.length(); ++k) {
         int l = this.chars.getInt(i + j + k);
         if (l == -1) {
            return 1;
         }

         char c0 = p_119976_.charAt(k);
         char c1 = (char)l;
         if (c0 < c1) {
            return -1;
         }

         if (c0 > c1) {
            return 1;
         }
      }

      return 0;
   }

   public List<T> search(String p_119974_) {
      int i = this.suffixToT.size();
      int j = 0;
      int k = i;

      while(j < k) {
         int l = j + (k - j) / 2;
         int i1 = this.compare(p_119974_, l);
         if (DEBUG_COMPARISONS) {
            LOGGER.debug("comparing lower \"{}\" with {} \"{}\": {}", p_119974_, l, this.getString(l), i1);
         }

         if (i1 > 0) {
            j = l + 1;
         } else {
            k = l;
         }
      }

      if (j >= 0 && j < i) {
         int i2 = j;
         k = i;

         while(j < k) {
            int j2 = j + (k - j) / 2;
            int j1 = this.compare(p_119974_, j2);
            if (DEBUG_COMPARISONS) {
               LOGGER.debug("comparing upper \"{}\" with {} \"{}\": {}", p_119974_, j2, this.getString(j2), j1);
            }

            if (j1 >= 0) {
               j = j2 + 1;
            } else {
               k = j2;
            }
         }

         int k2 = j;
         IntSet intset = new IntOpenHashSet();

         for(int k1 = i2; k1 < k2; ++k1) {
            intset.add(this.suffixToT.getInt(k1));
         }

         int[] aint = intset.toIntArray();
         java.util.Arrays.sort(aint);
         Set<T> set = Sets.newLinkedHashSet();

         for(int l1 : aint) {
            set.add(this.list.get(l1));
         }

         return Lists.newArrayList(set);
      } else {
         return Collections.emptyList();
      }
   }
}