package net.minecraft.util.profiling.jfr;

import com.google.common.math.Quantiles;
import com.google.common.math.Quantiles.ScaleAndIndexes;
import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;

public class Percentiles {
   public static final ScaleAndIndexes DEFAULT_INDEXES = Quantiles.scale(100).indexes(50, 75, 90, 99);

   private Percentiles() {
   }

   public static Map<Integer, Double> evaluate(long[] p_185393_) {
      return p_185393_.length == 0 ? Map.of() : sorted(DEFAULT_INDEXES.compute(p_185393_));
   }

   public static Map<Integer, Double> evaluate(double[] p_185391_) {
      return p_185391_.length == 0 ? Map.of() : sorted(DEFAULT_INDEXES.compute(p_185391_));
   }

   private static Map<Integer, Double> sorted(Map<Integer, Double> p_185386_) {
      Int2DoubleSortedMap int2doublesortedmap = Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), (p_185389_) -> {
         p_185389_.putAll(p_185386_);
      });
      return Int2DoubleSortedMaps.unmodifiable(int2doublesortedmap);
   }
}