package net.minecraft.util.profiling;

public final class ResultField implements Comparable<ResultField> {
   public final double percentage;
   public final double globalPercentage;
   public final long count;
   public final String name;

   public ResultField(String p_18612_, double p_18613_, double p_18614_, long p_18615_) {
      this.name = p_18612_;
      this.percentage = p_18613_;
      this.globalPercentage = p_18614_;
      this.count = p_18615_;
   }

   public int compareTo(ResultField p_18618_) {
      if (p_18618_.percentage < this.percentage) {
         return -1;
      } else {
         return p_18618_.percentage > this.percentage ? 1 : p_18618_.name.compareTo(this.name);
      }
   }

   public int getColor() {
      return (this.name.hashCode() & 11184810) + 4473924;
   }
}